/*
 * Name: Sai
 * Date: Today
 * Des: Basically to render everything that is there
 *
 */


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Renderer {

    private static final int SCR_WIDTH = 1920;
    private static final int SCR_HEIGHT = 1080;

    private long window;
    private InputHandler input;
    private ShaderProgram shader;

    private List<ShapeContainer> renderables = new ArrayList<>();

    // we need this in order to add shapes without breaking the opengl 
    private final ConcurrentLinkedQueue<Runnable> pendingShapeAdds = new ConcurrentLinkedQueue<>();


    // === Shape container class ===
    private static class ShapeContainer {
        public Object shape;
        public int vao;
        public float size;
        public float x, y, z;
        public String type;

        public ShapeContainer(Object shape, int vao, float size, float x, float y, float z, String type) {
            this.shape = shape;
            this.vao = vao;
            this.size = size;
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
        }
    }

    // Public method to safely request a shape addition
    public void queueAddShape(float size, float x, float y, float z, String shapeType) {
        pendingShapeAdds.add(() -> addShape(size, x, y, z, shapeType));
    }

    // Actual shape creation – must be called on the render thread!
    private void addShape(float size, float x, float y, float z, String shapeType) {
        if (shapeType.equalsIgnoreCase("Cube")) {
            Cube cube = new Cube(size); // Pass the size into the Cube constructor
            int vao = GLUtils.createVAO(cube);
            renderables.add(new ShapeContainer(cube, vao, size, x, y, z, "Cube"));
        } else if (shapeType.equalsIgnoreCase("Sphere")) {
            Sphere sphere = new Sphere(20, 20, (float)(size/10.0)); // Pass the size into the Sphere constructor
            int vao = GLUtils.createVAO(sphere);
            renderables.add(new ShapeContainer(sphere, vao, size, x, y, z, "Sphere"));
        } else if (shapeType.equalsIgnoreCase("Triangle")) {
            Triangle triangle = new Triangle(size); // Pass the size into the Triangle constructor
            int vao = GLUtils.createVAO(triangle);
            renderables.add(new ShapeContainer(triangle, vao, size, x, y, z, "Triangle"));
        }
    }

    public void run(Runnable shapeSetup) {
        init();
        shapeSetup.run(); // Add initial shapes
        loop();
        cleanup();
    }

    private void init() {
        GLFWUtils.initGLFW(SCR_WIDTH, SCR_HEIGHT);
        window = GLFWUtils.createWindow("MonkeyRenders");
        input = new InputHandler(window);
        GLFWUtils.initGLCapabilities(window);

        shader = new ShaderProgram();
        Cube tempCube = new Cube(1); // For shader source
        shader.create(tempCube.returnVS(), tempCube.returnFS());

        glEnable(GL_DEPTH_TEST);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {

            // SAFELY run any pending shape additions
            while (!pendingShapeAdds.isEmpty()) {
                Runnable task = pendingShapeAdds.poll();
                if (task != null) task.run();
            }

            input.processInput();

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Black background
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            shader.use();

            for (ShapeContainer sc : renderables) {
                GLUtils.setMatrixUniforms(
                        shader,
                        input.getCamera(),
                        SCR_WIDTH,
                        SCR_HEIGHT,
                        GLUtils.translate(sc.x, sc.y, sc.z)
                );

                glBindVertexArray(sc.vao);

                int count = switch (sc.type.toLowerCase()) {
                    case "cube" -> ((Cube) sc.shape).getIndexCount();
                    case "sphere" -> ((Sphere) sc.shape).getIndexCount();
                    case "triangle" -> ((Triangle) sc.shape).getIndexCount();
                    default -> 0;
                };

                glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        shader.cleanup();
        for (ShapeContainer sc : renderables) {
            glDeleteVertexArrays(sc.vao);
        }
        glfwTerminate();
    }
}

/*
* Name: Sai
* Date: Today
* Des: Basically to render everything that is there
*
*/


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.joml.Vector3f;
import org.joml.*;

public class Renderer {

    private static final int SCR_WIDTH = 1920;
    private static final int SCR_HEIGHT = 1080;

    private long window;
    private InputHandler input;
    private ShaderProgram shader;

    private List<ShapeContainer> renderables = new ArrayList<>();
    private final ConcurrentLinkedQueue<Runnable> pendingShapeAdds = new ConcurrentLinkedQueue<>();

    private boolean isMousePressed = false;
    private boolean isShapeSelected = false;
    private Vector3f lastMouseWorldPos = new Vector3f();


    private ShapeContainer selectedShape = null;
    private ShapeContainer lastMovedShape = null;

    // queus a new shape to be added to hte next frame
    public void queueAddShape(float size, float x, float y, float z, String shapeType) {
        pendingShapeAdds.add(() -> addShape(size, x, y, z, shapeType));
    }



    // add new renderables object into the thing
    private void addShape(float size, float x, float y, float z, String shapeType) {
        if (shapeType.equalsIgnoreCase("Cube")) {
            Cube cube = new Cube(size);
            int vao = GLUtils.createVAO(cube);
            renderables.add(new ShapeContainer(cube, vao, size, x, y, z, "Cube"));
        } else if (shapeType.equalsIgnoreCase("Sphere")) {
            Sphere sphere = new Sphere(20, 20, (float)(size/10.0));
            int vao = GLUtils.createVAO(sphere);
            renderables.add(new ShapeContainer(sphere, vao, size, x, y, z, "Sphere"));
        } else if (shapeType.equalsIgnoreCase("Triangle")) {
            Triangle triangle = new Triangle(size);
            int vao = GLUtils.createVAO(triangle);
            renderables.add(new ShapeContainer(triangle, vao, size, x, y, z, "Triangle"));
        }
    }



    // straing the render process
    public void run(Runnable shapeSetup) {
        init(); // initalizeing opengl
        shapeSetup.run(); // adding the shapes
        loop(); // starting the rendering loop
        cleanup(); // cleaing up everythign
    }



    // init GLFW, WINDOW, shaders and opengl states
    private void init() {
        GLFWUtils.initGLFW(SCR_WIDTH, SCR_HEIGHT);
        window = GLFWUtils.createWindow("MonkeyRenders");
        input = new InputHandler(window);
        GLFWUtils.initGLCapabilities(window);

        shader = new ShaderProgram();
        Cube tempCube = new Cube(1);
        shader.create(tempCube.returnVS(), tempCube.returnFS());

        glEnable(GL_DEPTH_TEST); 
    }
    

    // maing rendering loop
    private void loop() {
        while (!glfwWindowShouldClose(window)) {

            // adding any queued shapes before rendering
            while (!pendingShapeAdds.isEmpty()) {
                Runnable task = pendingShapeAdds.poll();
                if (task != null) task.run();
            }

            input.processInput();
            handleMouseInput();
            
            // clear screen
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            shader.use(); // active shader program
            

            // for all of the rendering shapes
            for (ShapeContainer sc : renderables) {
                GLUtils.setMatrixUniforms(
                        shader,
                        input.getCamera(),
                        SCR_WIDTH,
                        SCR_HEIGHT,
                        GLUtils.translate(sc.x, sc.y, sc.z)
                );

                glBindVertexArray(sc.vao);
                
                // for what is goign to happening depeing on which one of them is selected
                int count = switch (sc.type.toLowerCase()) {
                    case "cube" -> ((Cube) sc.shape).getIndexCount();
                    case "sphere" -> ((Sphere) sc.shape).getIndexCount();
                    case "triangle" -> ((Triangle) sc.shape).getIndexCount();
                    default -> 0;
                };
                
                // drawing the one whihc is selected
                glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    

    // handing the mouse input
    private void handleMouseInput() {
        // Reset wasMoved flags at the start of each frame
        for (ShapeContainer sc : renderables) {
            sc.wasMoved = false;
        }
        

        // gets the currnet pos
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        glfwGetCursorPos(window, xpos, ypos);

        float mouseX = (float) ((2.0 * xpos[0]) / SCR_WIDTH - 1.0);
        float mouseY = (float) (1.0 - (2.0 * ypos[0]) / SCR_HEIGHT);

        float zPlane = selectedShape != null ? selectedShape.z : 0.0f;
        Vector3f currentMouseWorld = screenToWorld(mouseX, mouseY, zPlane);



        // hadle left mouse press 
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
            if (!isMousePressed) {
                isMousePressed = true;

                // Select the closest shape under the cursor
                float closestDistance = Float.MAX_VALUE;
                ShapeContainer closest = null;

                for (ShapeContainer sc : renderables) {
                    Vector3f shapePos = new Vector3f(sc.x, sc.y, sc.z);
                    float dist = currentMouseWorld.distance(shapePos);
                    if (dist < sc.size && dist < closestDistance) {
                        closestDistance = dist;
                        closest = sc;
                    }
                }

                if (closest != null) {
                    selectedShape = closest;
                    isShapeSelected = true;
                    lastMouseWorldPos.set(currentMouseWorld);
                }
            } else if (isShapeSelected && selectedShape != null) {
                Vector3f delta = new Vector3f(currentMouseWorld).sub(lastMouseWorldPos);
                selectedShape.x += delta.x;
                selectedShape.y += delta.y;
                selectedShape.z += delta.z;
                lastMouseWorldPos.set(currentMouseWorld);

                // Mark this shape as moved this frame
                selectedShape.wasMoved = true;
                lastMovedShape = selectedShape;
            }
        } else {
            isMousePressed = false;
            isShapeSelected = false;
            selectedShape = null;
        }

        // Use the last added shape for animation
        ShapeContainer lastAddedShape = null;
        if (!renderables.isEmpty()) {
            lastAddedShape = renderables.get(renderables.size() - 1);
        }
        if (glfwGetKey(window, GLFW_KEY_J) == GLFW_PRESS) {
            System.out.println("J pressed");
            if (lastMovedShape != null) {
                System.out.println("Returning to original position: " + lastMovedShape.type);
                lastMovedShape.animation.active = true;
                lastMovedShape.animation.time = 0.0f;
                lastMovedShape.animation.duration = 1.0f;
                lastMovedShape.animation.start.set(lastMovedShape.x, lastMovedShape.y, lastMovedShape.z);
                lastMovedShape.animation.end.set(lastMovedShape.originalPosition);
            } else {
                System.out.println("No shape has been moved yet.");
            }
        }


        // Animate all shapes with active animations
        for (ShapeContainer sc : renderables) {
            if (sc.animation.active) {
                sc.animation.time += 0.016f;
                float t = Math.min(sc.animation.time / sc.animation.duration, 1.0f);

                Vector3f interpolated = new Vector3f();
                sc.animation.start.lerp(sc.animation.end, t, interpolated);

                sc.x = interpolated.x;
                sc.y = interpolated.y;
                sc.z = interpolated.z;

                if (t >= 1.0f) {
                    sc.animation.active = false;
                }
            }
        }
    }



    // convert the screen into cordinated at a give zplane
    private Vector3f screenToWorld(float mouseX, float mouseY, float zPlane) {
        Matrix4f projection = new Matrix4f().setPerspective(
                (float) Math.toRadians(45.0f),
                (float) SCR_WIDTH / SCR_HEIGHT,
                0.1f,
                100.0f
        );

        Matrix4f view = input.getCamera().getViewMatrixJOML();  // FIXED here

        Matrix4f inverseVP = new Matrix4f();
        projection.mul(view, inverseVP).invert();

        Vector4f nearPoint = new Vector4f(mouseX, mouseY, -1.0f, 1.0f);
        Vector4f farPoint = new Vector4f(mouseX, mouseY, 1.0f, 1.0f);

        inverseVP.transform(nearPoint);
        inverseVP.transform(farPoint);

        nearPoint.div(nearPoint.w);
        farPoint.div(farPoint.w);

        Vector3f rayOrigin = new Vector3f(nearPoint.x, nearPoint.y, nearPoint.z);
        Vector3f rayDirection = new Vector3f(farPoint.x, farPoint.y, farPoint.z)
                .sub(rayOrigin)
                .normalize();

        float t = (zPlane - rayOrigin.z) / rayDirection.z;
        return new Vector3f(rayDirection).mul(t).add(rayOrigin);
    }



    // cleans up opengl  and everythign else so that there is no memory leak 
    private void cleanup() {
        shader.cleanup();
        for (ShapeContainer sc : renderables) {
            glDeleteVertexArrays(sc.vao);
        }

        glfwTerminate();
        // closing the program
        System.exit(0);
    }
}



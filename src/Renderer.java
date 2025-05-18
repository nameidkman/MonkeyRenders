/*
 * Name: Sai
 * Date: Today
 * Des: Basically to render everything that is there
 *
 */

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    private static final int SCR_WIDTH = 1920;
    private static final int SCR_HEIGHT = 1080;

    private long window;
    private InputHandler input;
    private Cube cube;
    private ShaderProgram shader;
    private int vao;
    private Triangle triangle;
    private int vaoT;
    private Sphere sphere;
    private int vaoS;


    public void run() {
        init();
        loop();
        cleanup();
    }

    // Initializing all of the things required for rendering
    private void init() {
        // Basic GLFW and OpenGL initialization
        GLFWUtils.initGLFW(SCR_WIDTH, SCR_HEIGHT);
        window = GLFWUtils.createWindow("MonkeyRenders");
        input = new InputHandler(window);
        GLFWUtils.initGLCapabilities(window);

        // --- Cube ---
        cube = new Cube();
        shader = new ShaderProgram();
        shader.create(cube.returnVS(), cube.returnFS());
        vao = GLUtils.createVAO(cube);

        // --- Triangle ---
        triangle = new Triangle();
        vaoT = GLUtils.createVAO(triangle);  // Triangle VAO

        sphere = new Sphere(20, 20);
        vaoS = GLUtils.createVAO(sphere);

        glEnable(GL_DEPTH_TEST);  // Enable depth testing for 3D rendering



    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            input.processInput();  // Process user input (e.g., keyboard/mouse)

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);  // Clear color (dark greenish)
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);  // Clear the screen and depth buffer

            // --- Draw Cube ---
            shader.use();
            GLUtils.setMatrixUniforms(shader, input.getCamera(), SCR_WIDTH, SCR_HEIGHT, GLUtils.translate(0.0f, 0.0f, 0.0f));  // Cube at origin

            // Draw the cube at its position
            glBindVertexArray(vao);
            glDrawElements(GL_TRIANGLES, cube.getIndexCount(), GL_UNSIGNED_INT, 0);

            // --- Draw Triangle ---
            shader.use();  // Reuse the same shader for the triangle (you can use a different shader if needed)
            GLUtils.setMatrixUniforms(shader, input.getCamera(), SCR_WIDTH, SCR_HEIGHT, GLUtils.translate(-2.0f, 0.0f, 0.0f));  // Triangle shifted to left

            // Draw the triangle at its position
            glBindVertexArray(vaoT);
            glDrawElements(GL_TRIANGLES, triangle.getIndexCount(), GL_UNSIGNED_INT, 0);

            GLUtils.setMatrixUniforms(shader, input.getCamera(), SCR_WIDTH, SCR_HEIGHT, GLUtils.translate(2.0f, 0.0f, 0.0f));  // Second Cube shifted to right

            // Draw another sphere at a different position (to the right)
            glBindVertexArray(vaoS);
            glDrawElements(GL_TRIANGLES, sphere.getIndexCount(), GL_UNSIGNED_INT, 0);



            // Swap buffers and poll for events
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        // Clean up shaders and OpenGL resources
        shader.cleanup();
        glDeleteVertexArrays(vao);
        glDeleteVertexArrays(vaoT);
        glfwTerminate();  // Terminate GLFW
    }
}
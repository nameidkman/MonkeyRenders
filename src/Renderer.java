/*
 * Name: Sai 
 * Date: Today
 * Des: Basically to render the everything which is there 
 *
 *
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

    public void run() {
        init();
        loop();
        cleanup();
    }




    private void init() {
        GLFWUtils.initGLFW(SCR_WIDTH, SCR_HEIGHT);
        window = GLFWUtils.createWindow("MonkeyRenders");
        input = new InputHandler(window);
        GLFWUtils.initGLCapabilities(window);
        cube = new Cube();

        shader = new ShaderProgram();
        shader.create();

        vao = GLUtils.createVAO(cube);

        glEnable(GL_DEPTH_TEST);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            input.processInput();

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            shader.use();
            GLUtils.setMatrixUniforms(shader, input.getCamera(), SCR_WIDTH, SCR_HEIGHT);

            glBindVertexArray(vao);
            glDrawElements(GL_TRIANGLES, cube.getIndexCount(), GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        shader.cleanup();
        glDeleteVertexArrays(vao);
        glfwTerminate();
    }
}

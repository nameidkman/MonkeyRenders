

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

    private long window;
    private Camera camera;

    public InputHandler(long window) {
        this.window = window;
        this.camera = new Camera();
    }

    public void processInput() {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);

        // ✅ Add this line to enable keyboard and mouse input handling
        camera.processKeyboard(window);
    }

    public Camera getCamera() {
        return camera;
    }
}


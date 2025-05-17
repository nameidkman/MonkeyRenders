import org.lwjgl.ovr.OVRVector3f;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

    private long window;
    private Camera camera;

    public InputHandler(long window) {
        this.window = window;
        this.camera = new Camera();
//        glfwSetCursorPosCallback(window, camera::mouseCallback);
//        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void processInput() {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);

        camera.processKeyboard(window);
    }

    public Camera getCamera() {
        return camera;
    }

}

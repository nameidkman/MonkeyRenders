import org.lwjgl.glfw.GLFW;
import org.lwjgl.ovr.OVRVector3f;

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

    private long window;
    private Camera camera;
    private Frame frame;

    // Flag to track if a frame has already been opened
    private boolean frameOpen = false;

    public InputHandler(long window) {
        this.window = window;
        this.camera = new Camera();
//        glfwSetCursorPosCallback(window, camera::mouseCallback);
//        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void processInput() {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
            glfwSetWindowShouldClose(window, true);
        // Use glfwGetMouseButton instead of glfwGetKey
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS && !frameOpen) {
            // If the right-click is pressed and no frame is open, create a new frame
            SwingUtilities.invokeLater(() -> {
                frame = new Frame();  // Make sure Frame is created safely on the EDT
            });
            frameOpen = true; // Set the flag to true to indicate a frame is open
        }
        // You can release the frame when the right mouse button is released
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_RELEASE) {
            frameOpen = false;
        }
    }


    public Camera getCamera() {
        return camera;
    }

}

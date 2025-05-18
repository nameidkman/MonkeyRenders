import javax.swing.*;

import static java.lang.Math.*;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private float[] position = {0f, 0f, 3f};
    private float[] front = {0f, 0f, -1f};
    private float[] up = {0f, 1f, 0f};

    private float yaw = -90f;
    private float pitch = 0f;
    private boolean firstMouse = true;
    private double lastX = 400, lastY = 300;
    private final float speed = 0.05f;
    private final float sensitivity = 0.1f;
    private boolean shiftPressed = false;
    private Frame frame;
    private boolean frameOpen = false;


    public void processKeyboard(long window) {
        float[] right = GLUtils.cross(front, up);
        GLUtils.normalize(right);
        boolean currentlyPressed = glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS;

        // SHIFT just pressed
        if (currentlyPressed && !shiftPressed) {
            glfwSetCursorPosCallback(window, this::mouseCallback);
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            shiftPressed = true;
        } else if (!currentlyPressed && shiftPressed) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            glfwSetCursorPosCallback(window, null);
            shiftPressed = false;
        }


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

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            position[0] += front[0] * speed;
            position[1] += front[1] * speed;
            position[2] += front[2] * speed;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            position[0] -= front[0] * speed;
            position[1] -= front[1] * speed;
            position[2] -= front[2] * speed;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            position[0] -= right[0] * speed;
            position[1] -= right[1] * speed;
            position[2] -= right[2] * speed;
        }

        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            position[0] += right[0] * speed;
            position[1] += right[1] * speed;
            position[2] += right[2] * speed;
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            position[1] += speed;
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            position[1] -= speed;
        }
    }

    public void mouseCallback(long window, double xpos, double ypos) {
        if (firstMouse) {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }

        double xoffset = xpos - lastX;
        double yoffset = lastY - ypos; // Reversed

        lastX = xpos;
        lastY = ypos;

        xoffset *= sensitivity;
        yoffset *= sensitivity;

        yaw += xoffset;
        pitch += yoffset;

        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;

        float radYaw = (float) toRadians(yaw);
        float radPitch = (float) toRadians(pitch);

        front[0] = (float) (cos(radYaw) * cos(radPitch));
        front[1] = (float) sin(radPitch);
        front[2] = (float) (sin(radYaw) * cos(radPitch));
        GLUtils.normalize(front);
    }

    public float[] getViewMatrix() {
        float[] center = {
                position[0] + front[0],
                position[1] + front[1],
                position[2] + front[2]
        };
        return GLUtils.lookAt(position, center, up);
    }

    public float[] getPosition() {
        return position;
    }
}

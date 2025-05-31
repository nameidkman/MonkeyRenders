/*
 *  Name: Sai 
 *  Date: Today
 *  Des: this is for setting up opengl 
 *
 *
 */




import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GL;

public class GLFWUtils {
    

    // for the height and width
    private static int screenWidth, screenHeight;


    // init the glfw window
    public static void initGLFW(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        

        // for having the major and the minor verison so that we can use both
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    }



    // creating the actually window
    public static long createWindow(String title) {
        long window = glfwCreateWindow(screenWidth, screenHeight, title, NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable V-Sync
        glfwShowWindow(window);

        return window;
    }
    

    // init some other hting
    public static void initGLCapabilities(long window) {
        // Create OpenGL context capabilities after window is made current
        GL.createCapabilities();

        // Set the viewport to window size initially
        glfwSetFramebufferSizeCallback(window, (w, width, height) -> {
            glViewport(0, 0, width, height);
        });
    }
}

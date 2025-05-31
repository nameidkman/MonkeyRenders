/*
 *  Name: Sai 
 *  Date: Today
 *  Des: this is for handing some basic input  
 *
 *
 */



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

            camera.processKeyboard(window);
        }

        public Camera getCamera() {
            return camera;
        }
    }


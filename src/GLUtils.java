import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GLUtils {

    public static float[] cross(float[] a, float[] b) {
        return new float[]{
                a[1]*b[2] - a[2]*b[1],
                a[2]*b[0] - a[0]*b[2],
                a[0]*b[1] - a[1]*b[0]
        };
    }

    public static void normalize(float[] vec) {
        float len = (float) Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
        if (len != 0.0f) {
            vec[0] /= len;
            vec[1] /= len;
            vec[2] /= len;
        }
    }

    public static float dot(float[] a, float[] b) {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
    }

    public static float[] lookAt(float[] eye, float[] center, float[] up) {
        float[] f = {center[0] - eye[0], center[1] - eye[1], center[2] - eye[2]};
        normalize(f);
        float[] s = cross(f, up);
        normalize(s);
        float[] u = cross(s, f);

        return new float[]{
                s[0], u[0], -f[0], 0,
                s[1], u[1], -f[1], 0,
                s[2], u[2], -f[2], 0,
                -dot(s, eye), -dot(u, eye), dot(f, eye), 1
        };
    }

    public static void setMatrixUniforms(ShaderProgram shader, Camera camera, int width, int height) {
        float[] model = {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };

        float aspect = (float) width / height;
        float fov = (float) Math.toRadians(45);
        float zNear = 0.1f, zFar = 100.0f;
        float yScale = (float)(1 / Math.tan(fov / 2));
        float xScale = yScale / aspect;
        float length = zFar - zNear;

        float[] projection = {
                xScale, 0, 0, 0,
                0, yScale, 0, 0,
                0, 0, -(zFar + zNear) / length, -1,
                0, 0, -(2 * zNear * zFar) / length, 0
        };

        int programID = shader.getID();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(glGetUniformLocation(programID, "model"), false, stack.floats(model));
            glUniformMatrix4fv(glGetUniformLocation(programID, "view"), false, stack.floats(camera.getViewMatrix()));
            glUniformMatrix4fv(glGetUniformLocation(programID, "projection"), false, stack.floats(projection));
        }
    }

    public static int createVAO(Cube cube) {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, cube.getVertices(), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, cube.getIndices(), GL_STATIC_DRAW);

        // Assume vbo and vao have been generated and bound
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);               // Position
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES); // Color
        glEnableVertexAttribArray(1);


        glBindVertexArray(0);
        return vao;
    }

    public static void initGLCapabilities(long window) {
        org.lwjgl.opengl.GL.createCapabilities();
        glfwSetFramebufferSizeCallback(window, (w, width, height) -> glViewport(0, 0, width, height));
    }

    public static long createWindow(String title) {
        long window = glfwCreateWindow(800, 600, title, NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create GLFW window");
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        return window;
    }

    public static void initGLFW(int width, int height) {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    }
}

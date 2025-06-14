
/*
 *  Name: Sai 
 *  Date: Today
 *  Des: for the utils 
 *
 *
 */



import org.lwjgl.system.MemoryStack;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GLUtils {

    // compute the cross product of the two 3d vectors
    public static float[] cross(float[] a, float[] b) {
        return new float[]{
                a[1]*b[2] - a[2]*b[1],
                a[2]*b[0] - a[0]*b[2],
                a[0]*b[1] - a[1]*b[0]
        };
    }
    

    // normalize a 3d vector inplace
    public static void normalize(float[] vec) {
        float len = (float) Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
        if (len != 0.0f) {
            vec[0] /= len;
            vec[1] /= len;
            vec[2] /= len;
        }
    }
    

    // getting the dot product for the thign
    public static float dot(float[] a, float[] b) {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
    }


    // creats a  lootat view matrix given camera parrameter 
    public static float[] lookAt(float[] eye, float[] center, float[] up) {


        // compute forward vector from eye to center and normalizign it
        float[] f = {center[0] - eye[0], center[1] - eye[1], center[2] - eye[2]};
        normalize(f);


        // compute the right vector as a cross proudct of forward and up vectors
        float[] s = cross(f, up);
        normalize(s);

        // compute corrected up vector as cross product of right adn forward vector
        float[] u = cross(s, f);

        return new float[]{
                s[0], u[0], -f[0], 0,
                s[1], u[1], -f[1], 0,
                s[2], u[2], -f[2], 0,
                -dot(s, eye), -dot(u, eye), dot(f, eye), 1
        };
    }

    //  Custom: Accepts external model matrix
    public static void setMatrixUniforms(ShaderProgram shader, Camera camera, int width, int height, float[] model) {
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
    //  Translation matrix
    public static float[] translate(float x, float y, float z) {
        return new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                x, y, z, 1
        };
    }



    // creating the vao for the 3 different shapes, cube, sphere, trigangel




    public static int createVAO(Cube cube) {
        // basic setup 
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();


        // binding the vao
        glBindVertexArray(vao);


        // binding the vbo and ebo 
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, cube.getVertices(), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, cube.getIndices(), GL_STATIC_DRAW);
        



        // tellign what is the draw thing
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        
        // telling what are the color parrameters
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
        return vao;
    }

    public static int createVAO(Triangle triangle) {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, triangle.getVertices(), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, triangle.getIndices(), GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
        return vao;
    }
    public static int createVAO(Sphere sphere) {
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, sphere.getVertices(), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, sphere.getIndices(), GL_STATIC_DRAW);

        // Set the vertex position attribute (location 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0); // 3 for position (x, y, z)
        glEnableVertexAttribArray(0);

        // Set the vertex color attribute (location 1)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES); // 3 for color (r, g, b)
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
        return vao;
    }
}

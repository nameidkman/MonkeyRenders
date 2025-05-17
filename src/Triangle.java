import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Triangle {
    private final FloatBuffer vertices;
    private final IntBuffer indices;
    private final int indexCount;
    private static final String vertexShaderSource = """
            #version 330 core
            
            layout(location = 0) in vec3 aPos;   // Vertex position
            layout(location = 1) in vec3 aColor; // Vertex color
            
            out vec3 vertexColor; // Pass to fragment shader
            
            void main()
            {
                gl_Position = vec4(aPos, 1.0);
                vertexColor = aColor;
            }
            
            
            """;

    private static final String fragmentShaderSource = """
            #version 330 core
            
            in vec3 vertexColor;  // Interpolated color from vertex shader
            
            out vec4 FragColor;
            
            void main()
            {
                FragColor = vec4(vertexColor, 1.0); // Alpha set to 1 (opaque)
            }
            
            """;

    public static String returnVS(){
        return vertexShaderSource;
    }
    public static String returnFS(){
        return fragmentShaderSource;
    }

    public Triangle() {



        // Each vertex: x, y, z, r, g, b
        float[] verts = {
                // Base vertices (square)
                -0.5f, 0.0f, -0.5f,   1f, 0f, 0f, // 0: back-left (red)
                0.5f, 0.0f, -0.5f,   0f, 1f, 0f, // 1: back-right (green)
                0.5f, 0.0f,  0.5f,   0f, 0f, 1f, // 2: front-right (blue)
                -0.5f, 0.0f,  0.5f,   1f, 1f, 0f, // 3: front-left (yellow)

                // Apex vertex (top of the pyramid)
                0.0f, 1.0f,  0.0f,   1f, 0f, 1f  // 4: top (magenta)
        };


        int[] inds = {
                // Sides
                0, 1, 4, // Back face
                1, 2, 4, // Right face
                2, 3, 4, // Front face
                3, 0, 4, // Left face

                // Base (2 triangles)
                0, 1, 2,
                2, 3, 0
        };

        vertices = BufferUtils.createFloatBuffer(verts.length);
        vertices.put(verts).flip();

        indices = BufferUtils.createIntBuffer(inds.length);
        indices.put(inds).flip();

        indexCount = inds.length;
    }

    public FloatBuffer getVertices() {
        return vertices;
    }

    public IntBuffer getIndices() {
        return indices;
    }

    public int getIndexCount() {
        return indexCount;
    }
}

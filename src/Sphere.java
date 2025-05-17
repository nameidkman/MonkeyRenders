import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Sphere {
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

    public Sphere(int stacks, int slices) {
        float radius = 1.0f;
        float[] verts = new float[(stacks + 1) * (slices + 1) * 6];
        int[] inds = new int[stacks * slices * 6];

        int vertIndex = 0;
        int index = 0;

        // Generate vertices and indices for the sphere
        for (int i = 0; i <= stacks; i++) {
            float theta = (float) (Math.PI * i / stacks); // Latitude angle
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);

            for (int j = 0; j <= slices; j++) {
                float phi = (float) (2 * Math.PI * j / slices); // Longitude angle
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);

                float x = radius * sinTheta * cosPhi;
                float y = radius * cosTheta;
                float z = radius * sinTheta * sinPhi;

                // Position
                verts[vertIndex++] = x;
                verts[vertIndex++] = y;
                verts[vertIndex++] = z;

                // Color (for simplicity, just use rainbow color based on index)
                float red = (float) Math.abs(Math.sin(phi));
                float green = (float) Math.abs(Math.cos(phi));
                float blue = (float) Math.abs(Math.sin(theta));

                // Color
                verts[vertIndex++] = red;
                verts[vertIndex++] = green;
                verts[vertIndex++] = blue;
            }
        }

        // Create indices
        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int first = (i * (slices + 1)) + j;
                int second = first + slices + 1;

                // First triangle (top)
                inds[index++] = first;
                inds[index++] = second;
                inds[index++] = first + 1;

                // Second triangle (bottom)
                inds[index++] = second;
                inds[index++] = second + 1;
                inds[index++] = first + 1;
            }
        }

        // Convert arrays to buffers
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

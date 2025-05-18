import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public  class Cube {
    private final FloatBuffer vertices;
    private final IntBuffer indices;
    private final int indexCount;
    private static final String vertexShaderSource = """
			#version 330 core
                 layout (location = 0) in vec3 aPos;
                 layout (location = 1) in vec3 aColor;
                
                 out vec3 vertexColor;
                
                 uniform mat4 model;
                 uniform mat4 view;
                 uniform mat4 projection;
                
                 void main() {
                     gl_Position = projection * view * model * vec4(aPos, 1.0);
                     vertexColor = aColor;
                 }
		""";

    private static final String fragmentShaderSource = """
			#version 330 core
                 in vec3 vertexColor;
                 out vec4 FragColor;
                
                 void main() {
                     FragColor = vec4(vertexColor, 1.0);
                 }
		""";

    public static String returnVS(){
        return vertexShaderSource;
    }
    public static String returnFS(){
        return fragmentShaderSource;
    }
    public Cube(float size) {
        // Adjust the size based on the input size
        float halfSize = size / 2.0f;

        float[] verts = {
                // x, y, z,      r, g, b
                -halfSize, -halfSize, -halfSize, 1f, 0f, 0f, // red
                halfSize, -halfSize, -halfSize, 0f, 1f, 0f, // green
                halfSize, halfSize, -halfSize, 0f, 0f, 1f, // blue
                -halfSize, halfSize, -halfSize, 1f, 1f, 0f, // yellow
                -halfSize, -halfSize, halfSize, 1f, 0f, 1f, // magenta
                halfSize, -halfSize, halfSize, 0f, 1f, 1f, // cyan
                halfSize, halfSize, halfSize, 1f, 0.5f, 0f, // orange
                -halfSize, halfSize, halfSize, 0.5f, 0f, 1f  // violet
        };

        int[] inds = {
                0, 1, 2, 2, 3, 0,
                4, 5, 6, 6, 7, 4,
                0, 1, 5, 5, 4, 0,
                2, 3, 7, 7, 6, 2,
                0, 3, 7, 7, 4, 0,
                1, 2, 6, 6, 5, 1
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
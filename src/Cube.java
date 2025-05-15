import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public  class Cube {
    private final FloatBuffer vertices;
    private final IntBuffer indices;
    private final int indexCount;

    public Cube() {
        float[] verts = {
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f
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
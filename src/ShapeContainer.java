
// === Shape container class ===
public class ShapeContainer {
    public Object shape;
    public int vao;
    public float size;
    public float x, y, z;
    public String type;

    public ShapeContainer(Object shape, int vao, float size, float x, float y, float z, String type) {
        this.shape = shape;
        this.vao = vao;
        this.size = size;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }
}
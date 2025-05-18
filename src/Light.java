public class Light {
    // Position of the light in 3D space (x, y, z)
    private float[] position;
    // Color of the light (RGB)
    private float[] color;
    // Intensity of the light (brightness level)
    private float intensity;

    // Constructor to initialize the light with position, color, and intensity
    public Light(float[] position, float[] color, float intensity) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
    }

    // Getter for light position
    public float[] getPosition() {
        return position;
    }

    // Setter for light position
    public void setPosition(float[] position) {
        this.position = position;
    }

    // Getter for light color
    public float[] getColor() {
        return color;
    }

    // Setter for light color
    public void setColor(float[] color) {
        this.color = color;
    }

    // Getter for light intensity
    public float getIntensity() {
        return intensity;
    }

    // Setter for light intensity
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}

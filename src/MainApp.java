/*
 *  Name: Sai 
 *  Date: Today
 *  Des: this is for the run class at the moment what this is doign is just making a new render object and then going to 
 *        run the rendered which i have made for the class
 *
 *
 *
 *
 */


public class MainApp {
    public static Renderer renderer = new Renderer();

    public static void main(String[] args) {

        renderer.run(() -> {
            renderer.queueAddShape(1.0f, 0f, 0f, 0f, "Cube");
        });

    }
}

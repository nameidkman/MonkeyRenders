import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel implements Runnable {

    final int originalPixelSize = 16;
    final int increaseTileSize = 3;
    final int tileSize = originalPixelSize * increaseTileSize;
    final int maxScreenCol = 24;
    final int maxScreenRow = 12;
    final int screenX = tileSize * maxScreenCol;
    final int screenY = tileSize * maxScreenRow;

    Thread thread;
    int FPS = 60;

    // interface buttons
    Rectangle cube = new Rectangle(screenX / 2 - 75, 200, 150, 50);
    Rectangle pyramid = new Rectangle(screenX / 2 - 75, 270, 150, 50);
    Rectangle sphere = new Rectangle(screenX / 2 - 75, 340, 150, 50);
    Rectangle make = new Rectangle(screenX / 2 , screenY - 100, 150, 50);

    boolean makeCube = false;
    boolean makePyramid = false;
    boolean makeSphere = false;
    boolean mainMenu = true;
    boolean makeThing = false;
    // Sliders (for size and displacement)
    Rectangle sizeSliderBar = new Rectangle(20, 60, 200, 20);
    Rectangle xSliderBar = new Rectangle(20, 100, 200, 20);
    Rectangle ySliderBar = new Rectangle(20, 140, 200, 20);
    Rectangle zSliderBar = new Rectangle(20, 180, 200, 20);

    int sizeValue = 50; // 0 to 100
    int xOffset = 0;
    int yOffset = 0;
    int zOffset = 0;

    Rectangle selectedSlider = null;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenX, screenY));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.startGameThread();
        // Mouse interaction
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();

                if (mainMenu) {
                    if (cube.contains(p)) {
                        makeCube = true;
                        makePyramid = false;
                        makeSphere = false;
                        mainMenu = false;
                        System.out.println("Cube selected");
                    } else if (sphere.contains(p)) {
                        makeSphere = true;
                        makeCube = false;
                        makePyramid = false;
                        mainMenu = false;
                        System.out.println("Sphere selected");
                    } else if (pyramid.contains(p)) {
                        makePyramid = true;
                        makeCube = false;
                        makeSphere = false;
                        mainMenu = false;
                        System.out.println("Pyramid selected");
                    }
                }

                if (makeCube) {
                    if (sizeSliderBar.contains(p)) selectedSlider = sizeSliderBar;
                    else if (xSliderBar.contains(p)) selectedSlider = xSliderBar;
                    else if (ySliderBar.contains(p)) selectedSlider = ySliderBar;
                    else if (zSliderBar.contains(p)) selectedSlider = zSliderBar;
                    else if (make.contains(p)){
                        makeThing = true;
                        mainMenu = true;
                        makeCube = false;

                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                selectedSlider = null;
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectedSlider != null) {
                    int mouseX = e.getX();
                    int sliderX = selectedSlider.x;
                    int maxWidth = selectedSlider.width;

                    int relativeX = Math.max(0, Math.min(mouseX - sliderX, maxWidth));
                    int value = (int) ((relativeX / (double) maxWidth) * 100);

                    if (selectedSlider == sizeSliderBar) sizeValue = value;
                    else if (selectedSlider == xSliderBar) xOffset = value - 50; // center around 0
                    else if (selectedSlider == ySliderBar) yOffset = value - 50;
                    else if (selectedSlider == zSliderBar) zOffset = value - 50;

                    repaint();
                }
            }
        });
    }

    public void startGameThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = drawInterval + System.nanoTime();

        while (thread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        // Game logic here
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawBackgroundGrid(g2);
        drawPlayer(g2);
    }

    private void drawBackgroundGrid(Graphics2D g2) {
        g2.setColor(new Color(50, 50, 50));
        for (int x = 0; x < screenX; x += tileSize) {
            g2.drawLine(x, 0, x, screenY);
        }
        for (int y = 0; y < screenY; y += tileSize) {
            g2.drawLine(0, y, screenX, y);
        }
    }

    private void drawPlayer(Graphics2D g2) {
        if (mainMenu) {
            g2.setColor(Color.WHITE);
            g2.fill(cube);
            g2.fill(pyramid);
            g2.fill(sphere);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("Cube", cube.x + 20, cube.y + 30);
            g2.drawString("Pyramid", pyramid.x + 30, pyramid.y + 30);
            g2.drawString("Sphere", sphere.x + 30, sphere.y + 30);
        }

        // Cube Mode Rendering
        if (makeCube) {

            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenX, screenY); // Clear screen first
            drawBackgroundGrid(g2);              // Then draw the grid

            // Draw Cube as a Placeholder
            g2.setColor(Color.WHITE);
            g2.fillRect(screenX / 2 - sizeValue / 2, screenY / 2 - sizeValue / 2, sizeValue, sizeValue); // Cube

            // Draw Sliders
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.WHITE);
            g2.drawString("Size " + sizeValue, sizeSliderBar.x + sizeSliderBar.width + 10, sizeSliderBar.y + 15);
            g2.drawString("X " + xOffset, xSliderBar.x + xSliderBar.width + 10, xSliderBar.y + 15);
            g2.drawString("Y "  + yOffset, ySliderBar.x + ySliderBar.width + 10, ySliderBar.y + 15);
            g2.drawString("Z " + zOffset, zSliderBar.x + zSliderBar.width + 10, zSliderBar.y + 15);

            // Draw Sliders themselves
            drawSlider(g2, sizeSliderBar, sizeValue);
            drawSlider(g2, xSliderBar, xOffset + 50);
            drawSlider(g2, ySliderBar, yOffset + 50);
            drawSlider(g2, zSliderBar, zOffset + 50);

            g2.setColor(Color.WHITE);
            g2.fill(make);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.BLACK);
            g2.drawString("Make" , make.x + 20  , make.y + 30 );


        }

        // Pyramid Mode Rendering
        if (makePyramid) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenX, screenY);
            g2.setColor(Color.ORANGE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.drawString("Pyramid mode - work in progress", 100, 100);
        }

        // Sphere Mode Rendering
        if (makeSphere) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenX, screenY);
            g2.setColor(Color.CYAN);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.drawString("Sphere mode - work in progress", 100, 100);
        }
    }

    private void drawSlider(Graphics2D g2, Rectangle sliderBar, int value) {
        int knobWidth = 10;
        int barX = sliderBar.x;
        int barY = sliderBar.y;
        int barWidth = sliderBar.width;
        int barHeight = sliderBar.height;

        g2.setColor(Color.GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);

        int fillWidth = (int) (barWidth * (value / 100.0));
        g2.setColor(Color.GREEN);
        g2.fillRect(barX, barY, fillWidth, barHeight);

        g2.setColor(Color.WHITE);
        int knobX = barX + fillWidth - knobWidth / 2;
        g2.fillRect(knobX, barY - 2, knobWidth, barHeight + 4);

        g2.setColor(Color.BLACK);
        g2.drawRect(barX, barY, barWidth, barHeight);
    }
}

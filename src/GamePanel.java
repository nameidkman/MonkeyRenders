import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    final int originalPixelSize = 16;
    final int increaseTileSize = 3;
    final int tileSize = originalPixelSize * increaseTileSize;
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenX = tileSize * maxScreenCol;
    final int screenY = tileSize * maxScreenRow;

    Thread thread;
    public int postionX = (screenX - tileSize) / 2;
    public int postionY = (screenY - tileSize) / 2;

    int speed = 4;
    int FPS = 60;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenX, screenY));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
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
        // You can add movement logic here
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
        g2.setColor(new Color(0, 200, 255));
        g2.fillRoundRect(postionX, postionY, tileSize, tileSize, 10, 10);

        // Shadow effect
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(postionX + 5, postionY + 5, tileSize, tileSize, 10, 10);
    }
}

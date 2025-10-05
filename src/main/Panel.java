import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

public class Panel extends JPanel implements Runnable{

    final int miniTile = 16;
    final int scale = 3;
    final int bigTile = miniTile * scale;
    final int screenLength = 16 * bigTile;
    final int screenWidth = 16 * bigTile;

    final Color darkSquare = new Color(162, 209, 73);
    final Color lightSquare = new Color(170, 215, 81);

    Thread gameThread; // clk

    public Panel() {
        this.setPreferredSize(new Dimension(screenWidth, screenLength));
        this.setBackground(lightSquare);
        this.setDoubleBuffered(true); // better rendering
    }

    public void startThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (gameThread != null) { // fps = number of iterations per second
            update();
            repaint();
        }
    }

    public void update() {
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2 = (Graphics2D)graphics;
        int rowsOffset;
        for (int j = 0; j < screenWidth; j = j + bigTile) {
            if (j % 2 == 0) {
                rowsOffset = 1;
            }
            else {
                rowsOffset = 0;
            }
            for (int i = 0; i < screenLength; i = i + (2*bigTile)) {
                graphics2.setColor(darkSquare);
                graphics2.fillRect(i + rowsOffset, j, bigTile, bigTile);
            }
        }
        graphics2.dispose(); // save memory
    }
}
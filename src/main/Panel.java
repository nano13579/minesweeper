import javax.swing.JPanel;
import java.awt.Dimension;
// import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

public class Panel extends JPanel implements Runnable{

    final int miniTile = 16;
    final int scale = 3;
    final int bigTile = miniTile * scale;
    final int screenLength = 16 * bigTile;
    final int screenWidth = 16 * bigTile;

    Thread gameThread; // clk

    public Panel() {
        this.setPreferredSize(new Dimension(screenWidth, screenLength));
        this.setBackground(Color.GREEN);
        this.setDoubleBuffered(true); // better rendering
    }

    public void startThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        update();
        repaint();
    }

    public void update() {
    }

    public void paintComponent(Graphics2D graphics) {
        super.paintComponent(graphics);
    }
}
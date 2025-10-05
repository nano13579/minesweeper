import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.Timer;

public class Panel extends JPanel implements Runnable{

    final int miniTile = 16;
    final int scale = 3;
    final int bigTile = miniTile * scale;
    final int screenLength = 16 * bigTile;
    final int screenWidth = 16 * bigTile;
    final int medNumMines = 40;
    final int fps = 60;


    final Color darkSquare = new Color(162, 209, 73);
    final Color lightSquare = new Color(170, 215, 81);

    Thread gameThread; // clk
    MouseHandler mouseHandler = new MouseHandler();

    public Panel() {
        this.setPreferredSize(new Dimension(screenWidth, screenLength));
        this.setBackground(lightSquare);
        this.setDoubleBuffered(true); // better rendering
        this.addMouseListener(mouseHandler);
        this.setFocusable(true);
    }

    public void startThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double waitTime = (1 * 10^9) / fps;
        double nextPaintTime = System.nanoTime() + waitTime;
        while (gameThread != null) { // fps = number of iterations per second
            
            update();
            repaint();

            try {
                double timeLeft = nextPaintTime - System.nanoTime();
                timeLeft = timeLeft/1000000;
                if(timeLeft < 0) {
                    timeLeft = 0;
                }
                Thread.sleep((long) timeLeft);
                nextPaintTime += waitTime;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (mouseHandler.xPosition != 0);
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2 = (Graphics2D)graphics;
        int rowsOffset = 0;
        for (int j = 0; j < screenWidth; j = j + bigTile) {
            if ((j / bigTile) % 2 == 0) {
                rowsOffset = bigTile;
            }
            if ((j / bigTile) % 2 == 1) {
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
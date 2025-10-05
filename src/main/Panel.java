import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;

public class Panel extends JPanel implements Runnable {

    final int miniTile = 16;
    final int scale = 3;
    final int bigTile = miniTile * scale;
    final int screenLength = 16 * bigTile;
    final int screenWidth = 16 * bigTile;
    final int medNumMines = 40;
    final int fps = 60;


    final Color darkGreenSquare = new Color(162, 209, 73);
    final Color lightGreenSquare = new Color(170, 215, 81);
    final Color lightBrownSquare = new Color(222, 207, 180);
    final Color darkBrownSquare = new Color(193, 173, 144);

    ArrayList<Integer> xRandom = new ArrayList<>();
    ArrayList<Integer> yRandom = new ArrayList<>();

    public boolean minesGenerated = false;

    Thread gameThread; // clk
    MouseHandler mouseHandler = new MouseHandler();

    public Panel() {
        this.setPreferredSize(new Dimension(screenWidth, screenLength));
        this.setBackground(lightGreenSquare);
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
        double waitTime = (1000000000) / fps;
        System.out.println("testing run");
        double nextPaintTime = System.nanoTime() + waitTime;
        if (!minesGenerated) {
            randomMines();
            System.out.println(xRandom);
            System.out.println(yRandom);
            System.out.println("testing random mines inside run");
            minesGenerated = true;
        }
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
                graphics2.setColor(darkGreenSquare);
                graphics2.fillRect(i + rowsOffset, j, bigTile, bigTile);
            }
        }
        if (mouseHandler.xPosition != 0) {
            int xRemainder = mouseHandler.xPosition % bigTile;
            int yRemainder = mouseHandler.yPosition % bigTile;

            int xSquareSelect = mouseHandler.xPosition - xRemainder;
            int ySquareSelect = mouseHandler.yPosition - yRemainder;

            graphics2.setColor(lightBrownSquare);
            graphics2.fillRect(xSquareSelect, ySquareSelect, bigTile, bigTile);

        }
        graphics2.dispose(); // save memory
    }

    public void randomMines() {
        xRandom.clear();
        yRandom.clear();
        ArrayList<Integer> xIndexMatching = new ArrayList<>();

        for(int i = 0; i < medNumMines; i ++) {
            xRandom.add((int)(Math.random() * (screenWidth / bigTile)));
            yRandom.add((int)(Math.random() * (screenLength / bigTile)));
        }

        for(int outer = 0; outer < medNumMines; outer ++) { // checking for mine repeats in X dir
            for (int inner = 0; inner < medNumMines; inner ++) {
                if (outer != inner && xRandom.get(outer).equals(xRandom.get(inner))) {
                    xIndexMatching.add(outer);
                    xIndexMatching.add(inner);
                }
                else {
                    
                }
            }
        }
        
        for(int j = 0; j < xIndexMatching.size(); j = j + 2) { // removing repeats
            if(yRandom.get(xIndexMatching.get(j)) == yRandom.get(xIndexMatching.get(j+1))) {
                for (int i = 0; i < 1; ) {
                    yRandom.set(xIndexMatching.get(j), (int)(Math.random() * (screenLength / bigTile)));
                    if (yRandom.get(xIndexMatching.get(j)) != yRandom.get(xIndexMatching.get(j+1))) {
                        i++;
                    }
                    else {
                        
                    }
                }
            }
        }
    }
}
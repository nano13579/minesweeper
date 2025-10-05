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
    private int xRemainder;
    private int yRemainder;
    private int xSquareSelect;
    private int ySquareSelect;

    Thread gameThread; // clk
    MouseHandler mouseHandler = new MouseHandler();

    public boolean gameOn = true;

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
        double nextPaintTime = System.nanoTime() + waitTime;
        if (!minesGenerated) {
            randomMines();
            System.out.println(xRandom);
            System.out.println(yRandom);
            minesGenerated = true;
        }
        while (gameThread != null && gameOn) { // fps = number of iterations per second
            
            update();
            repaint();
            floodFill();


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
            xRemainder = mouseHandler.xPosition % bigTile;
            yRemainder = mouseHandler.yPosition % bigTile;

            xSquareSelect = mouseHandler.xPosition - xRemainder;
            ySquareSelect = mouseHandler.yPosition - yRemainder;

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

    public void floodFill() {
        ArrayList<Integer> xEightCheck = new ArrayList<>(); // checking eight surrounding squares for mines
        ArrayList<Integer> yEightCheck = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            xEightCheck.add(xSquareSelect - i);
            for (int j = -1; j < 2; j++) {
                yEightCheck.add(ySquareSelect - j);
            }
        }


        for (int j = 0; j < xRandom.size() - 1; j++) {
            if ((xSquareSelect / bigTile) == xRandom.get(j) && (ySquareSelect / bigTile) == yRandom.get(j)) {                    
                System.out.println("GAME OVER");
                gameOn = false;
                return;
            }
            else {
            }
        }   
    }
}
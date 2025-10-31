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
    ArrayList<Integer> numSurroundingMines = new ArrayList<>();
    ArrayList<Integer> xRevealed = new ArrayList<>();
    ArrayList<Integer> yRevealed = new ArrayList<>();

    public boolean minesGenerated = false;
    public boolean zeroSurroundingMinesCheck = false;
    private int xRemainder;
    private int yRemainder;
    private int xSquareSelect;
    private int ySquareSelect;
    private int surroundingIndividual;
    private int xCurrentFloodIndividual;
    private int yCurrentFloodIndividual;

    ArrayList<Boolean> mineAdjacentBoolean = new ArrayList<>();
    ArrayList<Integer> xMineAdjacentSquares = new ArrayList<>();
    ArrayList<Integer> yMineAdjacentSquares = new ArrayList<>();
    ArrayList<Integer> xFloodFill = new ArrayList<>();
    ArrayList<Integer> yFloodFill = new ArrayList<>();

    ArrayList<Integer> xAlreadyVisited = new ArrayList<>();
    ArrayList<Integer> yAlreadyVisited = new ArrayList<>();

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
            gameStatus();

            if (mouseHandler.mouseUp) {
                revealedCheck((xSquareSelect / bigTile), (ySquareSelect / bigTile));
                boundaryCheck((xSquareSelect / bigTile), (ySquareSelect / bigTile));
                xFloodFill.add(xSquareSelect / bigTile);
                yFloodFill.add(ySquareSelect / bigTile);
                floodFill();
                repaint();
            }

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
        //revealCells();
        for (int j = 0; j < screenWidth; j = j + bigTile) { // basic grid background
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

        for (int i = 0; i < xRandom.size(); i++) { // REMOVE CHEAT LATER!!
            graphics2.setColor(Color.blue);
            graphics2.fillRect(xRandom.get(i) * bigTile, yRandom.get(i) * bigTile, bigTile, bigTile);
        }

        if (zeroSurroundingMinesCheck) { // if no adjacent mines use revealCells for flood fill
            graphics2.setColor(Color.yellow); // yellow = no mines in adjacent 8 squares 
            graphics.fillRect(xCurrentFloodIndividual * bigTile, yCurrentFloodIndividual * bigTile, bigTile, bigTile);
        }

        mineAdjacentBoolean.clear();
        ArrayList<Integer> xDraw = new ArrayList<>(xFloodFill);
        ArrayList<Integer> yDraw = new ArrayList<>(yFloodFill);
        if (mouseHandler.mouseUp) {
            for(int i = 0; i < 16; i++) {
                for(int j = 0; j < 16; j++) {
                    boolean mineLocated = false;
                    boolean mineAdjacent = false;
                    for (int k = 0; k < xRandom.size(); k++) {
                        int x = Math.abs(xRandom.get(k) - i);
                        int y = Math.abs(yRandom.get(k) - j);
                        if (x == 0 && y == 0) {
                            mineLocated = true;
                            break;
                        }
                        else if (x <= 1 && y <= 1) {
                            mineAdjacent = true;
                            mineAdjacentBoolean.add(mineAdjacent);
                            xMineAdjacentSquares.add(xRandom.get(k) - i);
                            yMineAdjacentSquares.add(yRandom.get(k) - j);
                        }
                    }
                    if (mineLocated) {
                    } 
                    else if (mineAdjacent) {
                        graphics.setColor(Color.red);
                        graphics.fillRect(i * bigTile, j*bigTile, bigTile, bigTile);
                    } else {
                        // graphics.setColor(Color.orange);
                        // graphics.fillRect(i * bigTile, j*bigTile, bigTile, bigTile);
                    }
                }
            }

            if (mouseHandler.xPosition != 0) {
            xRemainder = mouseHandler.xPosition % bigTile;
            yRemainder = mouseHandler.yPosition % bigTile;

            xSquareSelect = mouseHandler.xPosition - xRemainder;
            ySquareSelect = mouseHandler.yPosition - yRemainder;

            graphics2.setColor(Color.green);
            graphics2.fillRect(xSquareSelect, ySquareSelect, bigTile, bigTile);
            }

            graphics.setColor(Color.orange);
            for (int i = 0; i < xDraw.size(); i++) {
                if (xDraw.get(i) != null && yDraw.get(i) != null){
                    graphics.fillRect(xDraw.get(i) * bigTile, yDraw.get(i) * bigTile, bigTile, bigTile);
                }
            }
        }
        graphics2.dispose(); // save memory
    }

    public void randomMines() {
        xRandom.clear();
        yRandom.clear();
        ArrayList<Integer> xIndexMatching = new ArrayList<>();

        for(int i = 0; i < medNumMines; i++) {
            xRandom.add((int)(Math.random() * (screenWidth / bigTile)));
            yRandom.add((int)(Math.random() * (screenLength / bigTile)));
            if (xRandom.get(i) == 0 && yRandom.get(i) == 0) {
                xRandom.set(i, ((int)(Math.random() * (screenWidth / bigTile)) + 1));
            }
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
    
    public void boundaryCheck(int x, int y) {
        
        ArrayList<Integer> xEightCheck = new ArrayList<>(); // checking eight surrounding squares for mines
        ArrayList<Integer> yEightCheck = new ArrayList<>();
        xCurrentFloodIndividual = x;
        yCurrentFloodIndividual = y;
        ArrayList<Integer> numSurroundingMines = new ArrayList<>();
        boolean startCounter = true;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) { // not including center square
                    continue;
                }
                else {
                yEightCheck.add(y - j);
                xEightCheck.add(x - i);
                }
            }
        }
        
        if (startCounter) { // initial check to identify surrounding mines on first click
            surroundingIndividual = 0;
            for (int i = 0; i < xEightCheck.size(); i++) {
                for (int k = 0; k < xRandom.size(); k++) {
                    if (xEightCheck.get(i) == xRandom.get(k) && yEightCheck.get(i) == yRandom.get(k)) {
                        surroundingIndividual++;
                        //System.out.println("surroundingIndividual" + surroundingIndividual);
                    }
                    else {
                        continue;
                    }
                }
            }
            numSurroundingMines.add(surroundingIndividual);
                for (int i = 0; i < numSurroundingMines.size(); i++) {
                    if (numSurroundingMines.get(i) != 0) {
                        zeroSurroundingMinesCheck = false;
                        break;
                    }
                    zeroSurroundingMinesCheck = true; // this is pretty much a duplicate of surroundingIndividual and
            }
                
        }
    }

    public void revealCells(int x, int y) {
        boolean isRevealed = false;
        boolean isMined = false;
        for (int i = 0; i < xRevealed.size(); i++) {
            if (x == xRevealed.get(i)) {
                for (int j = 0; j < yRevealed.size(); j++) {
                    if (y == yRevealed.get(j)) {
                        isRevealed = true;
                    }
                }
            }
        }
        for (int i = 0; i < xRandom.size(); i++) {
            if (x == xRandom.get(i)) {
                for (int j = 0; j < yRandom.size(); j++) {
                    if (y == yRandom.get(j)) {
                        isMined = true;
                    }
                }
            }
        }
        if (x < 0 || x > (screenWidth / 16) || y < 0 || y > (screenLength / 16)) {
            return;
        }
        if (isRevealed || isMined) {
            return;
        }
    }

    public void revealedCheck(int xSelected, int ySelected) {
        xRevealed.add(xSelected);
        yRevealed.add(ySelected);
    }

    private void floodFill() { // TODO actually start at (x, y) :heavy-sob:
        // System.out.println("flood fill invoked");
        // System.out.println("start sizes x=" + xFloodFill.size() + " y=" + yFloodFill.size());
        ArrayList<Integer> xVisited = new ArrayList<>();
        ArrayList<Integer> yVisited = new ArrayList<>();
        
        try {
            int index = 0;
            while (index < xFloodFill.size()) { // problem: xFloodFill 
                int xNew = xFloodFill.get(index);
                int yNew = yFloodFill.get(index);
                index++;

                boolean matchFound = false;
                
                for (int n = 0; n < xVisited.size(); n++) {
                    if (xVisited.get(n) == xNew && yVisited.get(n) == yNew) {
                        matchFound = true;
                        break;
                    }
                }

                if (matchFound) {
                    continue;
                }

                xVisited.add(xNew);
                yVisited.add(yNew);

                boolean mineHere = false;
                for (int i = 0; i < xRandom.size(); i++) {
                    if (xNew == xRandom.get(i) && yNew == yRandom.get(i)) {
                        mineHere = true;
                        break;
                    }
                }

                if (mineHere) {
                    continue;
                }

                boundaryCheck(xNew, yNew);
                if (surroundingIndividual != 0) {
                    continue;
                }       

                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        if (j == 0 && k == 0) continue; // xSelectSquare already covered in paintComponent

                        int xDoubleNew = xNew + j;
                        int yDoubleNew = yNew + k;

                        if (xDoubleNew < 0 || yDoubleNew < 0 || xDoubleNew >= (screenLength / bigTile) || yDoubleNew >= (screenWidth / bigTile)) continue;

                        boolean neighborMined = false;
                        for (int m = 0; m < xRandom.size(); m++) {
                            if (xDoubleNew == xRandom.get(m) && yDoubleNew == yRandom.get(m)) {
                                neighborMined = true;
                                break;
                            }
                        }

                        if (neighborMined) {
                            continue;
                        }

                        boolean neighborCheck = false;
                        for (int o = 0; o < xVisited.size(); o++) {
                            if (xVisited.get(o) == xDoubleNew && yVisited.get(o) == yDoubleNew) {
                                neighborCheck = true;
                                break;
                            }
                        }
                        if (!neighborCheck) {
                            xVisited.add(xDoubleNew);
                            yVisited.add(yDoubleNew);
                            xFloodFill.add(xDoubleNew);
                            yFloodFill.add(yDoubleNew);
                        }
                    }
                }
            }
    } catch (Exception e) {
    System.out.println("Exception in flood fill: " + e);
    e.printStackTrace();
    }
    }
    
    public void gameStatus() {
        for (int j = 0; j < xRandom.size(); j++) { // stop game on mine trigger
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
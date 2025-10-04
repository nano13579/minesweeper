import javax.swing.JFrame;

public class Main {

    public static void main (String[] args) {

        JFrame frame = new JFrame();
        frame.setTitle("minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Panel Panel = new Panel();
        frame.add(Panel);
        frame.pack();

        frame.setVisible(true);

    }

}
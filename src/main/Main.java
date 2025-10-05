import javax.swing.JFrame;

public class Main {

    public static void main (String[] args) {

        JFrame frame = new JFrame();
        frame.setTitle("minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Panel panel = new Panel();
        frame.add(panel);
        frame.pack();

        frame.setVisible(true);

        panel.startThread();
    }

}
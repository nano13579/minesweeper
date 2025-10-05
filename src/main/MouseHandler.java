import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class MouseHandler implements MouseListener{

    public int xPosition = 0, yPosition = 0;
    public boolean mouseUp = false;

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        xPosition = e.getX();
        yPosition = e.getY();
        mouseUp = true;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

}

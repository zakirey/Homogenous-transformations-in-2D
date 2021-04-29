import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Gallery2D extends JPanel {
    int ident;

    Rectangle2D.Double rectangle2D;
    Ellipse2D.Double ellipse2D;

    Gallery2D(int id) {
        super();
        setBorder(new LineBorder(Color.BLACK));
        ident = id;
        rectangle2D = new Rectangle2D.Double(20,40,100,100);
        ellipse2D = new Ellipse2D.Double(20,150,100,100);
    }

    public void paintComponent (Graphics g ) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        if(ident == 4 ) {
            g2d.fill(rectangle2D);
            g2d.fill(ellipse2D);
        }
    }
    public boolean mouseOnPanel(int mouseX, int mouseY) {
        return this.getX()<mouseX && mouseX<this.getX()+this.getWidth() && this.getY()<mouseY && mouseY<this.getY()+this.getHeight();
    }

    public boolean mouseOnRect(){
        int posX = 0;
        int posY = 0;
        try {
            posX = this.getMousePosition().x;
            posY = this.getMousePosition().y;

        } catch (Exception e) {
            System.out.println("Please enlarge frame before you can press on shape correctly");
        }
        return this.rectangle2D.x < posX && posX < this.rectangle2D.x + this.rectangle2D.width &&
                this.rectangle2D.y < posY && posY < this.rectangle2D.y + this.rectangle2D.height;
    }

    public boolean mouseOnCircle() {
        int posX = 0;
        int posY = 0;
        try {
            posX = this.getMousePosition().x;
            posY = this.getMousePosition().y;
        } catch (Exception e) {
            System.out.println("Please enlarge frame before you can press on shape correctly");
        }
        return this.ellipse2D.x<posX && posX<this.ellipse2D.x+this.ellipse2D.width &&
                this.ellipse2D.y<posY && posY<this.ellipse2D.y+this.ellipse2D.height;
    }
}

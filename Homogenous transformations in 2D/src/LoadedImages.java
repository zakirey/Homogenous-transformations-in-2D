import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LoadedImages extends JPanel {

    Image img1;
    Image img2;
    Image img3;

    int PressedImage;

    public ArrayList<Image> buffImages = new ArrayList<>();

    public static ArrayList<Shape> shapes = new ArrayList<>();
    public ArrayList<String> paths = new ArrayList<>();
    int ident;
    LoadedImages(int id) {
        super();
        setBorder(new LineBorder(Color.BLACK));
        try {
            img1 = ImageIO.read(new File("Images/image1.png"));
            img2 = ImageIO.read(new File("Images/image2.jpg"));
            img3 = ImageIO.read(new File("Images/image3.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        paths.add("Images/image1.png");
        paths.add("Images/image2.jpg");
        paths.add("Images/image3.jpg");
        buffImages.add(img1);
        buffImages.add(img2);
        buffImages.add(img3);

        ident = id;
    }

    public void paintComponent (Graphics g ) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        int changeNext = 20;
        for(Image i: buffImages) {
            Rectangle2D.Double tempImgRect = new Rectangle2D.Double(20,changeNext,100,100);
            shapes.add(tempImgRect);
            g2d.drawImage(i, 20, changeNext, 100, 100, null);
            changeNext += 110;
        }
    }

    public boolean mouseOnPanel(int mouseX, int mouseY) {
        return this.getX()<mouseX && mouseX<this.getX()+this.getWidth() && this.getY()<mouseY && mouseY<this.getY()+this.getHeight();
    }

    public boolean mouseOnImage(){
        int posX = this.getMousePosition().x;
        int posY = this.getMousePosition().y;
        for (int i = 0; i<shapes.size(); i++) {
            Rectangle imgRect = shapes.get(i).getBounds();
            if(imgRect.x < posX && posX < imgRect.x + imgRect.width &&
                    imgRect.y < posY && posY < imgRect.y + imgRect.height) {
                PressedImage = i;
                return true;
            }
        }
        return false;
    }
}

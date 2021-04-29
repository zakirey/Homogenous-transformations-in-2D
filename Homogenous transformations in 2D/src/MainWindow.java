import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

class TextWindow extends JDialog {

    JTextField text1;
    JTextField text2;
    JButton startButton;

    int width;
    int height;
    boolean submit = false;

    JFrame framez = new JFrame();

    TextWindow() {
        framez.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        framez.setSize(300, 300);
        JPanel inputWindow = new JPanel();

        inputWindow.setSize(200, 200);
        inputWindow.setLayout(new GridLayout(3, 1));

        inputWindow.add(new JLabel("Width"));
        text1 = new JTextField();
        text1.setSize(100, 20);
        inputWindow.add(text1);

        inputWindow.add(new JLabel("Height"));
        text2 = new JTextField();
        text2.setSize(100, 20);
        inputWindow.add(text2);

        startButton  = new JButton("Submit");
        startButton.setSize(50, 50);
        startButton.addActionListener(e -> {
            width = Integer.parseInt(text1.getText());
            height = Integer.parseInt(text2.getText());
            submit = true;
            framez.dispose();
        });
        inputWindow.add(startButton);

        framez.add(inputWindow, BorderLayout.CENTER);
        framez.setVisible(true);
    }

}
class MainWindow extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

    Buttons buttons;
    Poster poster;
    LoadedImages images;
    Gallery2D gallery2D;

    enum DND {Rectangle, Circle, ImageRectangle, None}
    DND dnd = DND.None;

    AffineTransform transform;
    TextWindow tw;
    public static ArrayList<String> savePaths = new ArrayList<>();
    public MainWindow(){
        super("Poster Composition" );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,800);
        setLayout(new BorderLayout());

        createPoster();
        createButtonBox();
        createFigureBox();
        pack();
        setVisible(true);

        transform = new AffineTransform( 0, 0, /* 0 */
                0, 0, /* 0 */
                0, 0 /* 1 */);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void createFigureBox(){
        JPanel figures = new JPanel(new GridLayout(2,1));
        images = new LoadedImages(3);
        images.setPreferredSize(new Dimension(150,150));
        gallery2D = new Gallery2D(4);
        gallery2D.setPreferredSize(new Dimension(150,150));
        figures.add(images, new FlowLayout(FlowLayout.LEADING));
        figures.add(gallery2D, new FlowLayout(FlowLayout.TRAILING));
        add(figures, BorderLayout.WEST);
    }

    private void createPoster(){
        poster = new Poster(1);
        add(poster, BorderLayout.CENTER);

    }
    private void createButtonBox() {
        buttons = new Buttons(2);
        add(buttons, BorderLayout.SOUTH);
        buttons.tr.addActionListener(this);
        buttons.tl.addActionListener(this);
        buttons.td.addActionListener(this);
        buttons.tu.addActionListener(this);
        buttons.rr.addActionListener(this);
        buttons.rl.addActionListener(this);
        buttons.store.addActionListener(this);
        buttons.load.addActionListener(this);
        buttons.saveRaster.addActionListener(this);
        buttons.giveRes.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(poster.selectedImage != null)
            if (source == buttons.tr)
                poster.selectedImage.translate(1, 0);
            else if (source == buttons.td)
                poster.selectedImage.translate(0, 1);
            else if (source == buttons.tl)
                poster.selectedImage.translate(-1, 0);
            else if (source == buttons.tu)
                poster.selectedImage.translate(0, -1);
            else if (source == buttons.rl)
                poster.selectedImage.rotateByDegs(-1);
            else if (source == buttons.rr)
                poster.selectedImage.rotateByDegs(1);
        if(poster.selectedRectangle != null)
            if (source == buttons.tr)
                poster.selectedRectangle.translate(1, 0);
            else if (source == buttons.td)
                poster.selectedRectangle.translate(0, 1);
            else if (source == buttons.tl)
                poster.selectedRectangle.translate(-1, 0);
            else if (source == buttons.tu)
                poster.selectedRectangle.translate(0, -1);
            else if (source == buttons.rl)
                poster.selectedRectangle.rotateByDegs(-1);
            else if (source == buttons.rr)
                poster.selectedRectangle.rotateByDegs(1);
        if(poster.selectedCircle != null)
            if (source == buttons.tr)
                poster.selectedCircle.translate(1, 0);
            else if (source == buttons.td)
                poster.selectedCircle.translate(0, 1);
            else if (source == buttons.tl)
                poster.selectedCircle.translate(-1, 0);
            else if (source == buttons.tu)
                poster.selectedCircle.translate(0, -1);
            else if (source == buttons.rl)
                poster.selectedCircle.rotateByDegs(-1);
            else if (source == buttons.rr)
                poster.selectedCircle.rotateByDegs(1);

        if(source == buttons.store)
            saveAffine();
        else if(source == buttons.load)
            loadAffine();
        else if(source == buttons.giveRes) {
            tw = new TextWindow();
        }
        else if(source == buttons.saveRaster) {
            saveImage();
        }
        poster.repaint();
    }

    private void saveImage(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Only .png files", "png"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            BufferedImage imagebuf = null;
            if (getFileExtension(file).equals("png")) {
                try {
                    if(tw != null && tw.submit) {
                        imagebuf = new BufferedImage(tw.width, tw.height, BufferedImage.TYPE_INT_RGB);
                    } else {
                        try {
                            imagebuf = new Robot().createScreenCapture(poster.getBounds());
                    }   catch (AWTException e1) {
                            e1.printStackTrace();
                    }
                    }
                    assert imagebuf != null;
                    Graphics2D graphics2D = imagebuf.createGraphics();
                    poster.paint(graphics2D);
                    try {
                        ImageIO.write(imagebuf, "png", new File(file.toString()));
                    } catch (Exception e) {
                        System.out.println("error");
                    }
                    JOptionPane.showMessageDialog(this, "File saved successfuly");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Unsupported filetype, use png file (.png) instead", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try{
            return name.substring(name.lastIndexOf(".") + 1);
        }catch (Exception e) {
            return "";
        }
    }

    private void loadAffine() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Only .txt files", "txt"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            BufferedReader br;
            if(getFileExtension(file).equals("txt")){
                try{
                    String line;

                    br = new BufferedReader(new FileReader(file.toString()));
                    poster.imgRects = new ArrayList<>();
                    poster.rectFigures = new ArrayList<>();
                    poster.circleFigures = new ArrayList<>();
                    savePaths = new ArrayList<>();
                    poster.selectedImage = null;
                    poster.selectedCircle = null;
                    poster.selectedRectangle = null;

                    if((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        int width = (int) Double.parseDouble(parts[0]);
                        int height = (int) Double.parseDouble(parts[1]);
                        setSize(width, height);
                        if ((line = br.readLine()) != null) {
                            parts = line.split(",");
                            int posterWidth = (int) Double.parseDouble(parts[0]);
                            int posterHeight = (int) Double.parseDouble(parts[1]);
                            poster.setSize(posterWidth, posterHeight);
                            while ((line = br.readLine()) != null) {
                                int n = (int) Double.parseDouble(line);
                                for (int i = 0; i < n; i++) {
                                    String shapeName = br.readLine();
                                    if (shapeName.equals("MyRectangle")) {
                                        parts = br.readLine().split(",");
                                        double m00 = Double.parseDouble(parts[0]);
                                        double m10 = Double.parseDouble(parts[1]);
                                        double m01 = Double.parseDouble(parts[2]);
                                        double m11 = Double.parseDouble(parts[3]);
                                        double m02 = Double.parseDouble(parts[4]);
                                        double m12 = Double.parseDouble(parts[5]);

                                        AffineTransform t = new AffineTransform(m00, m10, m01, m11, m02, m12);
                                        MyRectangle loadedRectangle = new MyRectangle(t);
                                        poster.rectFigures.add(loadedRectangle);
                                    }
                                    if (shapeName.equals("MyCircle")) {
                                        parts = br.readLine().split(",");
                                        double m00 = Double.parseDouble(parts[0]);
                                        double m10 = Double.parseDouble(parts[1]);
                                        double m01 = Double.parseDouble(parts[2]);
                                        double m11 = Double.parseDouble(parts[3]);
                                        double m02 = Double.parseDouble(parts[4]);
                                        double m12 = Double.parseDouble(parts[5]);

                                        AffineTransform t = new AffineTransform(m00, m10, m01, m11, m02, m12);
                                        MyCircle loadedCircle = new MyCircle(t);
                                        poster.circleFigures.add(loadedCircle);
                                    }
                                    if (shapeName.equals("ImageRectangle")) {
                                        String imagePath = br.readLine();
                                        parts = br.readLine().split(",");

                                        double m00 = Double.parseDouble(parts[0]);
                                        double m10 = Double.parseDouble(parts[1]);
                                        double m01 = Double.parseDouble(parts[2]);
                                        double m11 = Double.parseDouble(parts[3]);
                                        double m02 = Double.parseDouble(parts[4]);
                                        double m12 = Double.parseDouble(parts[5]);

                                        BufferedImage loadedImage = ImageIO.read(new File(imagePath));
                                        AffineTransform t = new AffineTransform(m00, m10, m01, m11, m02, m12);
                                        ImageRectangle loadedImageRect = new ImageRectangle(loadedImage, t);
                                        poster.imgRects.add(loadedImageRect);
                                        savePaths.add(imagePath);
                                    }
                                }
                            }
                            repaint();
                        }
                    }
                    br.close();
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this,"Unsupported filetype, use Text's file (.txt) instead","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveAffine() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Only .txt files", "txt"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            BufferedWriter bw;
            if(getFileExtension(file).equals("txt")){
                try{
                    bw = new BufferedWriter(new FileWriter(file.toString()));
                    bw.write(getWidth() + "," + getHeight());
                    bw.newLine();
                    bw.write(poster.getWidth() + "," + poster.getHeight());
                    bw.newLine();
                    if(poster.rectFigures.size() > 0) {
                        bw.write(poster.rectFigures.size() + "");
                        for (MyRectangle s : poster.rectFigures) {
                            bw.newLine();
                            bw.write(s.getClass().getSimpleName());
                            bw.newLine();
                            double[] mtx = new double[9];
                            s.transform.getMatrix( mtx );
                            bw.write(mtx[0] + "," + mtx[1]  + "," + mtx[2]  + "," + mtx[3]  + "," + mtx[4]  + "," + mtx[5]);
                        }
                    }
                    if(poster.circleFigures.size() > 0) {
                        bw.newLine();
                        bw.write(poster.circleFigures.size() + "");
                        for (MyCircle s : poster.circleFigures) {
                            bw.newLine();
                            bw.write(s.getClass().getSimpleName());
                            bw.newLine();
                            double[] mtx = new double[9];
                            s.transform.getMatrix( mtx );
                            bw.write(mtx[0] + "," + mtx[1]  + "," + mtx[2]  + "," + mtx[3]  + "," + mtx[4]  + "," + mtx[5]);
                        }
                    }
                    if(poster.imgRects.size() > 0) {
                        bw.newLine();
                        bw.write(poster.imgRects.size() + "");
                        for (int i = 0; i < poster.imgRects.size(); i++) {
                            bw.newLine();
                            bw.write(poster.imgRects.get(i).getClass().getSimpleName());
                            bw.newLine();
                            bw.write(savePaths.get(i));
                            bw.newLine();
                            double[] mtx = new double[9];
                            poster.imgRects.get(i).transform.getMatrix( mtx );
                            bw.write(mtx[0] + "," + mtx[1]  + "," + mtx[2]  + "," + mtx[3]  + "," + mtx[4]  + "," + mtx[5]);
                        }
                    }
                    bw.close();
                    JOptionPane.showMessageDialog(this, "File saved successfuly");
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(this,"Unsupported filetype, use Text's file (.txt) instead","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gallery2D.mouseOnPanel(e.getX(), e.getY())){
            if (gallery2D.mouseOnRect()){
                dnd = DND.Rectangle;
            }else if(gallery2D.mouseOnCircle()) {
                dnd = DND.Circle;
            }
        }else if(images.mouseOnPanel(e.getX(), e.getY())) {
            if(images.mouseOnImage()) {
                dnd = DND.ImageRectangle;

            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (poster.mouseOnPanel(e.getX(), e.getY())){
            int PosterX = poster.getMousePosition().x;
            int PosterY = poster.getMousePosition().y;
            if (dnd == DND.Rectangle){
                double width = (gallery2D.rectangle2D.getWidth())*2;
                double height = (gallery2D.rectangle2D.getHeight())*2;
                MyRectangle rect = new MyRectangle();
                rect.translate((int) (PosterX - (width/2)), (int) (PosterY - (height/2)));
                poster.rectFigures.add(rect);
                dnd = DND.None;
                poster.repaint();
            } else if(dnd == DND.Circle) {
                double width = (gallery2D.ellipse2D.getWidth())*2;
                double height = (gallery2D.ellipse2D.getHeight())*2;
                MyCircle circle = new MyCircle();
                circle.translate((int) (PosterX - (width/2)), (int) (PosterY - (height/2)));
                poster.circleFigures.add(circle);
                dnd = DND.None;
                poster.repaint();
            } else if(dnd == DND.ImageRectangle) {
                double width = images.buffImages.get(images.PressedImage).getWidth(null);
                double height = images.buffImages.get(images.PressedImage).getHeight(null);
                ImageRectangle imgRect = new ImageRectangle((BufferedImage) images.buffImages.get(images.PressedImage));
                imgRect.translate((int) (PosterX - (width/2)), (int) (PosterY - (height/2)));
                poster.imgRects.add(imgRect);
                savePaths.add(images.paths.get(images.PressedImage));
                dnd = DND.None;
                poster.repaint();
            }
        }
        dnd = DND.None;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Poster extends JPanel implements MouseMotionListener, MouseListener {

    int    m_press_x, m_press_y;

    static public final int  SENS_DIST = 10;

    enum OpModes  { OP_TRANSLATION, OP_ROTATION, OP_SCALING, OP_NONE }
    enum ShapeMode{ RECTANGLE, CIRCLE, NONE}

    OpModes op_mode = OpModes.OP_NONE;
    ShapeMode sh_mode = ShapeMode.NONE;

    MyRectangle selectedRectangle;
    ImageRectangle selectedImage;
    MyCircle selectedCircle;

    int ident;

    public  ArrayList<MyRectangle> rectFigures = new ArrayList<>();
    public  ArrayList<ImageRectangle> imgRects = new ArrayList<>();
    public  ArrayList<MyCircle> circleFigures = new ArrayList<>();
    AffineTransform transform;
    Poster(int id) {

        super();
        transform = new AffineTransform( 0, 0, /* 0 */
                0, 0, /* 0 */
                0, 0 /* 1 */);
        ident = id;
        addMouseListener(this);
        addMouseMotionListener(this);
    }


    public boolean mouseOnPanel(int mouseX, int mouseY) {
        return this.getX()<mouseX && mouseX<this.getX()+this.getWidth() && this.getY()<mouseY && mouseY<this.getY()+this.getHeight();
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        for(MyRectangle item: rectFigures) {
            item.drawWhole(g2d);
        }
        for(ImageRectangle item: imgRects) {
            item.drawWhole(g2d);
        }
        for(MyCircle item: circleFigures) {
            item.drawWhole(g2d);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3) {
            for(int i = 0; i< imgRects.size(); i++){
                if(imgRects.get(i).catchRectangleCenter(e.getX(), e.getY())) {
                    MainWindow.savePaths.remove(i);
                    imgRects.remove(i);
                    selectedImage.rect_caught = false;
                    selectedImage = null;
                    break;
                }
            }
            for (int i = 0; i< rectFigures.size(); i++) {
                if(rectFigures.get(i).catchRectangleCenter(e.getX(), e.getY())) {
                    rectFigures.remove(i);
                    selectedRectangle.rect_caught = false;
                    selectedRectangle = null;
                    break;
                }
            }
            for(int i = 0; i< circleFigures.size(); i++) {
                if(circleFigures.get(i).catchRectangleCenter(e.getX(), e.getY())) {
                    circleFigures.remove(i);
                    selectedCircle.circle_caught = false;
                    selectedCircle = null;
                    break;
                }
            }
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        m_press_x =  e.getX();
        m_press_y =  e.getY();

//        if(selectedImage == null)
            for(ImageRectangle i: imgRects) {
                if(i.catchRectangleCenter(m_press_x, m_press_y) || i.catchRectangle(m_press_x, m_press_y)) {
                    selectedImage = i;
                    sh_mode = ShapeMode.RECTANGLE;
                }
            }

//        if(selectedRectangle == null)
            for(MyRectangle i: rectFigures) {
                if(i.catchRectangleCenter(m_press_x, m_press_y) || i.catchRectangle(m_press_x, m_press_y)) {
                    selectedRectangle = i;
                    sh_mode = ShapeMode.RECTANGLE;
                }
            }

//        if(selectedCircle == null)
            for(MyCircle i: circleFigures) {
                if(i.catchRectangleCenter(m_press_x, m_press_y) || i.catchRectangle(m_press_x, m_press_y)) {
                    selectedCircle = i;
                    sh_mode = ShapeMode.CIRCLE;
                }
            }

        if (sh_mode == ShapeMode.RECTANGLE) {
            if (selectedImage != null && selectedImage.catchRectangle(m_press_x, m_press_y)) {
                selectedRectangle = null;
                selectedCircle = null;
                if (e.getButton() == MouseEvent.BUTTON1)
                    op_mode = OpModes.OP_ROTATION;
                else
                    op_mode = OpModes.OP_SCALING;
            } else if (selectedImage != null && selectedImage.catchRectangleCenter(m_press_x, m_press_y)) {
                selectedRectangle = null;
                selectedCircle = null;
                if (e.getButton() == MouseEvent.BUTTON1)
                    op_mode = OpModes.OP_TRANSLATION;
            }
            if (selectedRectangle != null && selectedRectangle.catchRectangle(m_press_x, m_press_y)) {
                selectedImage = null;
                selectedCircle = null;
                if (e.getButton() == MouseEvent.BUTTON1)
                    op_mode = OpModes.OP_ROTATION;
                else
                    op_mode = OpModes.OP_SCALING;
            } else if (selectedRectangle != null && selectedRectangle.catchRectangleCenter(m_press_x, m_press_y)) {
                selectedImage = null;
                selectedCircle = null;
                if (e.getButton() == MouseEvent.BUTTON1)
                    op_mode = OpModes.OP_TRANSLATION;
            }
        }
        else if(sh_mode == ShapeMode.CIRCLE) {
            if (selectedCircle != null && selectedCircle.catchRectangle(m_press_x, m_press_y)) {
                selectedRectangle = null;
                selectedImage = null;
                if (e.getButton() == MouseEvent.BUTTON1)
                    op_mode = OpModes.OP_ROTATION;
                else
                    op_mode = OpModes.OP_SCALING;
            } else if (selectedCircle != null && selectedCircle.catchRectangleCenter(m_press_x, m_press_y)) {
                selectedRectangle = null;
                selectedImage = null;
                if (e.getButton() == MouseEvent.BUTTON1)
                    op_mode = OpModes.OP_TRANSLATION;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (sh_mode == ShapeMode.RECTANGLE) {
            if (selectedImage != null && selectedImage.rect_caught) {
                Point2D.Double p_p_panel = new java.awt.geom.Point2D.Double(m_press_x, m_press_y);
                Point2D.Double r_p_panel = new java.awt.geom.Point2D.Double(e.getX(), e.getY());

                if (op_mode == OpModes.OP_ROTATION) {
                    selectedImage.rotateShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_SCALING) {
                    selectedImage.scaleShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_TRANSLATION) {
                    selectedImage.translateShapeByPoints(p_p_panel, r_p_panel);
                }

                selectedImage.rect_caught = false;
                repaint();
//                selectedImage = null;
            }
            if (selectedRectangle != null && selectedRectangle.rect_caught) {
                Point2D.Double p_p_panel = new java.awt.geom.Point2D.Double(m_press_x, m_press_y);
                Point2D.Double r_p_panel = new java.awt.geom.Point2D.Double(e.getX(), e.getY());

                if (op_mode == OpModes.OP_ROTATION) {
                    selectedRectangle.rotateShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_SCALING) {
                    selectedRectangle.scaleShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_TRANSLATION) {
                    selectedRectangle.translateShapeByPoints(p_p_panel, r_p_panel);
                }

                selectedRectangle.rect_caught = false;
                repaint();
//            selectedRectangle = null;
            }
        } else if (sh_mode == ShapeMode.CIRCLE){
            if (selectedCircle != null && selectedCircle.circle_caught) {
                Point2D.Double p_p_panel = new java.awt.geom.Point2D.Double(m_press_x, m_press_y);
                Point2D.Double r_p_panel = new java.awt.geom.Point2D.Double(e.getX(), e.getY());

                if (op_mode == OpModes.OP_ROTATION) {
                    selectedCircle.rotateShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_SCALING) {
                    selectedCircle.scaleShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_TRANSLATION) {
                    selectedCircle.translateShapeByPoints(p_p_panel, r_p_panel);
                }

                selectedCircle.circle_caught = false;
                repaint();
//            selectedCircle = null;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (sh_mode == ShapeMode.RECTANGLE) {
            if(selectedImage != null) {
                selectedImage.rect_caught = false;
            }
            if(selectedRectangle != null) {
                selectedRectangle.rect_caught = false;
            }
        } else if(sh_mode ==ShapeMode.CIRCLE) {
            if(selectedCircle != null) {
                selectedCircle.circle_caught = false;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(sh_mode == ShapeMode.RECTANGLE) {
            if (selectedImage != null && selectedImage.rect_caught) {
                Point2D.Double p_p_panel = new java.awt.geom.Point2D.Double(m_press_x, m_press_y);
                Point2D.Double r_p_panel = new java.awt.geom.Point2D.Double(e.getX(), e.getY());

                if (op_mode == OpModes.OP_ROTATION) {
                    selectedImage.rotateShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_SCALING) {
                    selectedImage.scaleShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_TRANSLATION) {
                    selectedImage.translateShapeByPoints(p_p_panel, r_p_panel);
                }

                m_press_x = e.getX();
                m_press_y = e.getY();

                repaint();
            }
            if (selectedRectangle != null && selectedRectangle.rect_caught) {
                Point2D.Double p_p_panel = new java.awt.geom.Point2D.Double(m_press_x, m_press_y);
                Point2D.Double r_p_panel = new java.awt.geom.Point2D.Double(e.getX(), e.getY());

                if (op_mode == OpModes.OP_ROTATION) {
                    selectedRectangle.rotateShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_SCALING) {
                    selectedRectangle.scaleShapeByPoints(p_p_panel, r_p_panel);
                } else if (op_mode == OpModes.OP_TRANSLATION) {
                    selectedRectangle.translateShapeByPoints(p_p_panel, r_p_panel);
                }

                m_press_x = e.getX();
                m_press_y = e.getY();

                repaint();
            }
        } else if(sh_mode == ShapeMode.CIRCLE) {
            if(selectedCircle != null && selectedCircle.circle_caught) {
                Point2D.Double p_p_panel = new java.awt.geom.Point2D.Double(m_press_x, m_press_y);
                Point2D.Double r_p_panel = new java.awt.geom.Point2D.Double(e.getX(), e.getY());

                if (op_mode == OpModes.OP_ROTATION) {
                    selectedCircle.rotateShapeByPoints(p_p_panel, r_p_panel);
                }
                else if(op_mode == OpModes.OP_SCALING) {
                    selectedCircle.scaleShapeByPoints(p_p_panel, r_p_panel);
                } else if(op_mode == OpModes.OP_TRANSLATION) {
                    selectedCircle.translateShapeByPoints(p_p_panel, r_p_panel);
                }

                m_press_x = e.getX();
                m_press_y = e.getY();

                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

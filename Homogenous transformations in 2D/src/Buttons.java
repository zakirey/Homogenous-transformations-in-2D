import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Buttons extends JPanel {

    JButton tr;
    JButton tl;
    JButton tu;
    JButton td;
    JButton rl;
    JButton rr;
    JButton store;
    JButton load;
    JButton saveRaster;
    JButton giveRes;

    int ident;

    Buttons(int id) {
        super();

        setBorder(new LineBorder(Color.red));
        setLayout( new GridLayout(2,5));
        tr = new JButton("R Translate by 1");
        tl = new JButton("L Translate by 1");
        tu = new JButton("U Translate by 1");
        td = new JButton("D Translate by 1");
        rl = new JButton("L Rotate by 1");
        rr = new JButton("R Rotate by 1");
        store = new JButton("Save all transformations");
        load = new JButton("Load transformations");
        saveRaster = new JButton("Save Image");
        giveRes = new JButton("Give dimensions for Save Image");
        add(tr); add(tl); add(tu); add(td); add(rl); add(rr); add(store); add(load); add(saveRaster); add(giveRes);
        ident  = id;
    }
}

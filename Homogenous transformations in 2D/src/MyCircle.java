import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class MyCircle extends Ellipse2D.Double
{
    static final int DEF_CIRCLE_SIZE_X = 200;
    static final int DEF_CIRCLE_SIZE_Y = 200;

    enum CircleVertices  {CENTER, TOP, LEFT, RIGHT, BOTTOM, NONE_VERT }

    boolean      circle_caught;
    CircleVertices vert_caught = CircleVertices.NONE_VERT;

    int circle_size_x, circle_size_y;

    public AffineTransform transform, inverse;

    MyCircle(AffineTransform t) {
        super();
        
        circle_size_x = DEF_CIRCLE_SIZE_X;
        circle_size_y = DEF_CIRCLE_SIZE_Y;
        
        transform = new AffineTransform(t);

        setFrame(0,0, circle_size_x, circle_size_y);

        try {
            inverse = transform.createInverse();
        }
        catch (Exception x){ inverse = null; }
    }
    
    MyCircle()
    {
        super();

        circle_size_x = DEF_CIRCLE_SIZE_X;
        circle_size_y = DEF_CIRCLE_SIZE_Y;

        transform = new AffineTransform( 1, 0, /* 0 */
                0, 1, /* 0 */
                0, 0 /* 1 */);


        setFrame(0,0, circle_size_x, circle_size_y);

        try {
            inverse = transform.createInverse();
        }
        catch (Exception x){ inverse = null; }
    }


    void drawWhole( Graphics2D g2d )
    {
        AffineTransform original = g2d.getTransform();
        AffineTransform transform = new AffineTransform(original);
        transform.concatenate(this.transform);
        g2d.setTransform(transform);

        g2d.fill( this );

        g2d.setTransform(original);
    }

    boolean catchRectangleCenter(int x, int y) {
        Point2D.Double p1 = new java.awt.geom.Point2D.Double( x, y );
        Point2D.Double p = new java.awt.geom.Point2D.Double(0, 0);
        inverse.transform(p1, p);

        double[] mtx = new double[9];
        transform.getMatrix( mtx );

        double sc_x = mtx[0]*mtx[0] + mtx[1]*mtx[1];
        double tolerance = (Poster.SENS_DIST / sc_x) * 3;

        if((Math.abs(p.x - (double)circle_size_x/2.0) < tolerance && (Math.abs(p.y - (double)circle_size_y/2.0)< tolerance))) {
            circle_caught = true;
            vert_caught = MyCircle.CircleVertices.CENTER;
            return true;
        }

        circle_caught = false;
        vert_caught = MyCircle.CircleVertices.NONE_VERT;

        return false;
    }

    boolean  catchRectangle( int x, int y )
    {

        Point2D.Double p1 = new java.awt.geom.Point2D.Double( x, y );
        Point2D.Double p = new java.awt.geom.Point2D.Double(0, 0);
        inverse.transform(p1, p);
        System.out.println( p.x + " " + p.y);


        double[] mtx = new double[9];
        transform.getMatrix( mtx );

        double sc_x = mtx[0]*mtx[0] + mtx[1]*mtx[1];
        double tolerance = Poster.SENS_DIST / sc_x;

        if (( Math.abs( p.x - (double) circle_size_x/2 ) < tolerance ) && ( Math.abs( p.y ) < tolerance ))
        {
            circle_caught = true;
            vert_caught = CircleVertices.TOP;
            return true;
        }
        
        if (( Math.abs( p.x - (double) circle_size_x/2) < tolerance ) && ( Math.abs( p.y -(double) circle_size_y ) < tolerance ))
        {
            circle_caught = true;
            vert_caught = CircleVertices.BOTTOM;
            return true;
        }
        if (( Math.abs( p.x - circle_size_x) < tolerance ) && ( Math.abs( p.y - (double) circle_size_y/2) < tolerance ))
        {
            circle_caught = true;
            vert_caught = CircleVertices.RIGHT;
            return true;
        }
        if (( Math.abs( p.x ) < tolerance ) && ( Math.abs( p.y -  (double) circle_size_y/2) < tolerance ))
        {
            circle_caught = true;
            vert_caught = CircleVertices.LEFT;
            return true;
        }

        circle_caught = false;
        vert_caught = CircleVertices.NONE_VERT;

        return false;
    }

    void rotateShapeByPoints(
            Point2D.Double p_p_panel, // mouse press point - start position for rotation
            Point2D.Double r_p_panel  // mouse release point - end position for rotation
    )
    {
        AffineTransform inverse;
        try {
            inverse = transform.createInverse();
        }
        catch (Exception x) { return; }


        Point2D.Double p_p = new java.awt.geom.Point2D.Double(0, 0);
        inverse.transform( p_p_panel, p_p);


        Point2D.Double p_r = new java.awt.geom.Point2D.Double(0, 0);
        inverse.transform( r_p_panel, p_r);


        Rectangle2D bb = getBounds2D();
        double shape_center_x = bb.getCenterX();
        double shape_center_y = bb.getCenterY();


        Point2D.Double v_p = new java.awt.geom.Point2D.Double(
                p_p.x - shape_center_x,
                p_p.y - shape_center_y );
        Point2D.Double v_r = new java.awt.geom.Point2D.Double(
                p_r.x - shape_center_x,
                p_r.y - shape_center_y );


        double a_p = Math.atan2( v_p.y, v_p.x);
        if ( a_p < 0 )
            a_p = 2*Math.PI + a_p;


        double a_r = Math.atan2( v_r.y, v_r.x);
        if ( a_p < 0 )
            a_p = 2*Math.PI + a_p;


        double  a_rotation = a_r - a_p;


        Point2D.Double pc = new java.awt.geom.Point2D.Double(shape_center_x, shape_center_y);
        Point2D.Double p2 = new java.awt.geom.Point2D.Double(0, 0);
        transform.transform(pc, p2);
        AffineTransform new_transform = new AffineTransform(  1, 0, /* 0 */
                0, 1, /* 0 */
                0, 0 /* 1 */);
        new_transform.rotate( a_rotation, p2.x, p2.y);
        new_transform.concatenate(transform);

        transform.setTransform( new_transform );

        try {
            this.inverse = transform.createInverse();
        }
        catch (Exception ignored) {
        }

    }

    void scaleShapeByPoints(
            Point2D.Double p_p_panel, // mouse press point - start position for rotation
            Point2D.Double r_p_panel  // mouse release point - end position for rotation
    )
    {
        AffineTransform inverse;
        try {
            inverse = transform.createInverse();
        }
        catch (Exception x) { return; }


        Point2D.Double p_p = new java.awt.geom.Point2D.Double(0, 0);
        inverse.transform( p_p_panel, p_p);


        Point2D.Double p_r = new java.awt.geom.Point2D.Double(0, 0);
        inverse.transform( r_p_panel, p_r);


        Rectangle2D bb = getBounds2D();
        double shape_center_x = bb.getCenterX();
        double shape_center_y = bb.getCenterY();


        Point2D.Double v_p = new java.awt.geom.Point2D.Double(
                p_p.x - shape_center_x,
                p_p.y - shape_center_y );
        Point2D.Double v_r = new java.awt.geom.Point2D.Double(
                p_r.x - shape_center_x,
                p_r.y - shape_center_y );

        double v_p_len = Math.sqrt( v_p.x*v_p.x + v_p.y*v_p.y );
        double v_r_len = Math.sqrt( v_r.x*v_r.x + v_r.y*v_r.y );

        double scale = v_r_len / v_p_len;


        Point2D.Double pc = new java.awt.geom.Point2D.Double(shape_center_x, shape_center_y);
        Point2D.Double p2 = new java.awt.geom.Point2D.Double(0, 0);
        transform.transform(pc, p2);
        AffineTransform new_transform = new AffineTransform(  1, 0, /* 0 */
                0, 1, /* 0 */
                0, 0 /* 1 */);
        new_transform.translate( p2.x, p2.y);
        new_transform.scale( scale, scale);
        new_transform.translate( -p2.x, -p2.y);
        new_transform.concatenate(transform);

        transform.setTransform( new_transform );

        try {
            this.inverse = transform.createInverse();
        }
        catch (Exception ignored) {
        }

    }

    void translateShapeByPoints(
            Point2D.Double p_p_panel, // mouse press point - start position for rotation
            Point2D.Double r_p_panel  // mouse release point - end position for rotation
    )
    {
        AffineTransform inverse;
        try {
            inverse = transform.createInverse();
        }
        catch (Exception x) { return; }

        Point2D.Double p_p = new java.awt.geom.Point2D.Double(0, 0);
        inverse.transform( p_p_panel, p_p);


        Point2D.Double p_r = new java.awt.geom.Point2D.Double(0, 0);
        inverse.transform( r_p_panel, p_r);


        Rectangle2D bb = getBounds2D();

        double shape_center_x = bb.getCenterX();
        double shape_center_y = bb.getCenterY();

        double releaseX = p_p_panel.x - r_p_panel.x;
        double releaseY = p_p_panel.y - r_p_panel.y;


        Point2D.Double pc = new java.awt.geom.Point2D.Double(shape_center_x, shape_center_y);
        Point2D.Double p2 = new java.awt.geom.Point2D.Double(0, 0);


        transform.transform(pc, p2);
        AffineTransform new_transform = new AffineTransform(  1, 0, /* 0 */
                0, 1, /* 0 */
                0, 0 /* 1 */);
        new_transform.translate( -releaseX, -releaseY);
        new_transform.concatenate(transform);

        transform.setTransform( new_transform );

        try {
            this.inverse = transform.createInverse();
        }
        catch (Exception x) {
            this.inverse = null;
        }
    }

    void translate( int x_t, int y_t )
    {
        AffineTransform new_transform = new AffineTransform(1, 0, /* 0 */
                0, 1, /* 0 */
                0, 0 /* 1 */);
        new_transform.translate( x_t, y_t);
        new_transform.concatenate(transform);
        transform = new_transform;
        // transform.concatenate( new_transform);
        try {
            inverse = transform.createInverse();
        }
        catch (Exception ignored) {}
    }


    void rotateByDegs( double angle )
    {
        Point2D.Double p1 = new java.awt.geom.Point2D.Double( (double) circle_size_x/2, (double)circle_size_y/2 );
        Point2D.Double p2 = new java.awt.geom.Point2D.Double(0, 0);
        transform.transform(p1, p2);
        AffineTransform new_transform = new AffineTransform(  1, 0, /* 0 */
                0, 1, /* 0 */
                0, 0 /* 1 */);
        new_transform.rotate( angle * 2 * Math.PI / 360, p2.x, p2.y);
        new_transform.concatenate(transform);

        transform = new_transform;
        try
        {
            inverse = transform.createInverse();
        }
        catch (Exception ignored)
        {
        }
    }

    void scale( double s_xy )
    {
        Point2D.Double p1 = new java.awt.geom.Point2D.Double( (double)circle_size_x/2, (double)circle_size_y/2 );
        Point2D.Double p2 = new java.awt.geom.Point2D.Double(0, 0);
        transform.transform(p1, p2);

        AffineTransform new_transform = new AffineTransform(  1, 0, /* 0 */
                0, 1, /* 0 */
                0, 0 /* 1 */);
        new_transform.translate( p2.x, p2.y);
        new_transform.scale( s_xy, s_xy );
        new_transform.translate( -p2.x, -p2.y);
        new_transform.concatenate(transform);

        transform = new_transform;

        // reduce size
        // transform.scale(0.5, 0.5);
        try
        {
            inverse = transform.createInverse();
        }
        catch (Exception ignored)
        {
        }
    }

    void reset()
    {
        double  init_scale = DEF_CIRCLE_SIZE_Y / (double)circle_size_y;
        transform.setToScale( init_scale, init_scale );

        try {
            inverse = transform.createInverse();
        }
        catch (Exception x){ inverse = null; }
    }
}

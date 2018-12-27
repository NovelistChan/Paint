import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.*;
public class MyRectangle extends MyItem implements Construct, Contain {
    MyRectangle(){
        itemIndex = 4;
    }

    @Override
    public void constructShape(){
        a0 = x0; b0 = y0; a1 = x1; b1 = y1;
        shape = new GeneralPath(Path2D.WIND_NON_ZERO);
        Point point1 = new Point(x0, y0);
        Point point2 = new Point(x0, y1);
        Point point3 = new Point(x1, y1);
        Point point4 = new Point(x1, y0);
        shape.moveTo(x0, y0);
        shape.lineTo(x0, y1);
        shape.moveTo(x0, y1);
        shape.lineTo(x1, y1);
        shape.moveTo(x1, y1);
        shape.lineTo(x1, y0);
        shape.moveTo(x1, y0);
        shape.lineTo(x0, y0);
        shape.closePath();
    }

    @Override
    public boolean contains(int x, int y) {
        boolean value = false;
        //      return shape.contains(new Point2D.Float(x, y));
        //      if(((x - xc) * (x - xc) * ry * ry + (y - yc) * (y - yc) * rx * rx) <= rx * rx * ry * ry) value = true;
        if(a0 < x && a1 > x && b0 < y && b1 > y) value = true;
        return value;
    }
}

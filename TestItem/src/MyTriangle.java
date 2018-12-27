import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
public class MyTriangle extends MyItem implements Construct, Contain{
    MyTriangle(){
        itemIndex = 5;
    }

    @Override
    public void constructShape(){
        a0 = x0; b0 = y0; a1 = x1; b1 = y1;
        shape = new GeneralPath(Path2D.WIND_NON_ZERO);
        shape.moveTo(x0, y1);
        shape.lineTo(x1, y1);
        shape.moveTo(x1, y1);
        shape.lineTo((x0 + x1) / 2, y0);
        shape.moveTo((x0 + x1) / 2, y0);
        shape.lineTo(x0, y1);
        shape.closePath();
    }

    @Override
    public boolean contains(int x, int y){
        boolean value = false;
        if(a0 < x && a1 > x && b0 < y && b1 > y) value = true;
        return value;
    }
}

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

public class MyItem implements Construct, Contain{
    int x0, y0, x1, y1;
    int a0, b0, a1, b1;//存放修正后的端点坐标
    boolean isSelected;
    boolean isSelectP0;
    boolean isSelectP1;
    int itemIndex;
    public static float lineWidth = 2f; // 线宽
    GeneralPath shape;

    @Override public void constructShape(){
        itemIndex = 0;
    }

    @Override public boolean contains(int x, int y){
        return true;
    }
}

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;


class PointNode{
    int nodex;
    int nodey;
    PointNode(){
        nodex = -1;
        nodey = -1;
    }
}

public class BoundaryFill implements Construct {
    int paintSeedx;
    int paintSeedy;
    Color fillColor = Color.RED;
    Color boundColor = Color.BLACK;
    GeneralPath shape;
    int nodeNum;
    PointNode[] pointStack;
    BoundaryFill(int x, int y){
        paintSeedx = x; paintSeedy = y;
        nodeNum = 0;
        pointStack = new PointNode[1000000];
        for(int i = 0; i < 1000000; i++){
            pointStack[i] = new PointNode();
            pointStack[i].nodex = -1;
            pointStack[i].nodey = -1;
        }
    }
    @Override
    public void constructShape(){
        shape = new GeneralPath(Path2D.WIND_NON_ZERO);
        boundaryFill4(paintSeedx, paintSeedy, paintSeedx, paintSeedy);
        shape.closePath();
    }

    boolean inPointStack(int x, int y){
        for(int i = 0; i < nodeNum; i++){
            if(x == pointStack[i].nodex && y == pointStack[i].nodey) return true;
        }
        return false;
    }

    void setPixel(int formerx,int formery, int nextx, int nexty){//在当前坐标画一个红点
        //   g.setStroke(new BasicStroke(MyItem.lineWidth));
        //   g.setPaint(fillColor);
        //   System.out.print(1);
        //   g.drawLine(formerx, formery, nextx, nexty);
        shape.moveTo(formerx, formery);
        shape.lineTo(nextx, nexty);
        //    g.dispose();
    }

    public Color getPixel(int x, int y){//获得当前坐标颜色信息
        Robot rb = null;
        try {
            rb = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(Paint.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedImage bufImage = rb.createScreenCapture(new Rectangle(0, 0, 1000, 850));
       // Color bufcolor = new Color(bufImage.getRGB(x - 100, y - 100));
        try {
            ImageIO.write(bufImage, "png", new File("E:/","1.png"));
            System.out.println("Catch!");
        } catch (IOException e) {
            e.printStackTrace();
        }

       // System.out.print(bufcolor);
        return rb.getPixelColor(x, y);
    }

    public void  boundaryFill4(int formerx, int formery, int nextx, int nexty) {
        Color interiorColor = getPixel(nextx, nexty);
        System.out.print(inPointStack(nextx, nexty));
        if (interiorColor != fillColor && interiorColor != boundColor && nextx < 1000 && nexty < 850 && nextx > 0 && nexty > 0 && !inPointStack(nextx, nexty)){
            pointStack[nodeNum].nodex = nextx; pointStack[nodeNum].nodey = nexty;
            nodeNum++;
            setPixel(formerx, formery, nextx, nexty); // set color of pixel to fillcolor.
        //    System.out.print(getPixel(nextx, nexty));
            boundaryFill4 (nextx, nexty, nextx + 1, nexty);
            boundaryFill4 (nextx, nexty, nextx - 1, nexty);
            boundaryFill4 (nextx, nexty, nextx, nexty + 1);
            boundaryFill4 (nextx, nexty, nextx, nexty - 1);
        }
    }
}

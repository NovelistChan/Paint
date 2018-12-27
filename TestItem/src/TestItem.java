import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestItem extends JPanel {
    private List<MyItem> Items = new ArrayList<MyItem>();
    private List<BoundaryFill> Fillpencils = new ArrayList<BoundaryFill>();
    private MyItem inItem;
    private BoundaryFill fillpencil;
    private float range = 4f;
    JPopupMenu popUp;
    private MyItem selectedItem; // 选中的Item
    MyItem tmpSelectedItem; // 移动时临时图元
    Graphics2D g;
    Color fillColor = Color.RED;
    Color boundColor = Color.BLACK;
    //4种状态：0：无任何操作 1：选中图元 2：选中图元端点 3：开始画图 4: 填充区域
    int pressIndex = 0;
    int itemIndex = 0;
    BufferedImage panelImage;

    boolean fillState = false;

    class MyMouseListener implements MouseInputListener {
        Point oldPoint;
        boolean isDragge;

        private void constructXY0(MyItem Item, int x, int y) {
            if (x > getWidth())
                Item.x0 = getWidth();
            else if (x < 0)
                Item.x0 = 0;
            else
                Item.x0 = x;
            if (y > getHeight())
                Item.y0 = getHeight();
            else if (y < 0)
                Item.y0 = 0;
            else
                Item.y0 = y;
        }

        private void constructXY1(MyItem Item, int x, int y) {
            if (x > getWidth())
                Item.x1 = getWidth();
            else if (x < 0)
                Item.x1 = 0;
            else
                Item.x1 = x;
            if (y > getHeight())
                Item.y1 = getHeight();
            else if (y < 0)
                Item.y1 = 0;
            else
                Item.y1 = y;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
          //  saveImage();
          //  System.out.println("Catch!");
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if (pressIndex != 3 && pressIndex != 4)
                pressIndex = 0;
            // 先将状态重置
            for (MyItem Item : Items) {
                Item.isSelectP0 = false;
                Item.isSelectP1 = false;
                Item.isSelected = false;
            }

            // 判断是否是画线状态，若是则将光标改变
            if (pressIndex == 3) {
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                return;
            }

            //判断是否是填充状态
            if (pressIndex == 4) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }

            // 判断是否选中直线，若是则将光标改变
            for (MyItem Item : Items) {
                if (Item.contains(e.getX(), e.getY())) {
                    Item.isSelected = true;
                    pressIndex = 1;
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    break;
                }
            }
            // 如果选中了某条直线，再判断该直线的端点是否被选中
            if (selectedItem != null) {
                MyItem Item = selectedItem;
                Point p1 = new Point(Item.a0, Item.b0); // 左端点
                Point p2 = new Point(Item.a1, Item.b1); // 右端点
                Point p = new Point(e.getX(), e.getY());
                if (isRange(p1, p)) {
                    Item.isSelectP0 = true;
                    pressIndex = 2;
                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                }
                if (isRange(p2, p)) {
                    Item.isSelectP1 = true;
                    pressIndex = 2;
                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                }

            }

        }

        @Override
        public void mousePressed(MouseEvent e) {
         //   System.out.print(getPixel(e.getX(),e.getY()));
            switch (pressIndex) {
                case 0:
                    selectedItem = null;
                    for (MyItem Item : Items) {
                        Item.isSelectP0 = false;
                        Item.isSelectP1 = false;
                        Item.isSelected = false;
                    }
                    break;
                case 1:
                    for (MyItem Item : Items) {
                        if (Item.isSelected) {
                            selectedItem = Item;
                            break;
                        }
                    }
                    switch (selectedItem.itemIndex){
                        case 1: tmpSelectedItem = new MyLine(); break;
                        case 2: tmpSelectedItem = new MyCircle(); break;
                        case 3: tmpSelectedItem = new MyOval(); break;
                        case 4: tmpSelectedItem = new MyRectangle(); break;
                        case 5: tmpSelectedItem = new MyTriangle(); break;
                        default:break;
                    }
                    tmpSelectedItem.x0 = selectedItem.x0;
                    tmpSelectedItem.x1 = selectedItem.x1;
                    tmpSelectedItem.y0 = selectedItem.y0;
                    tmpSelectedItem.y1 = selectedItem.y1;
                    tmpSelectedItem.constructShape();
                    oldPoint = new Point(e.getX(), e.getY());
                    break;
                case 2:

                    break;
                case 3:
                    switch (itemIndex){
                        case 1: inItem = new MyLine(); break;
                        case 2: inItem = new MyCircle(); break;
                        case 3: inItem = new MyOval(); break;
                        case 4: inItem = new MyRectangle(); break;
                        case 5: inItem = new MyTriangle(); break;
                        default:break;
                    }
                    inItem.x0 = e.getX();
                    inItem.y0 = e.getY();
                    break;
                case 4:
                    //fillpencil = new BoundaryFill(e.getX(), e.getY());
                    //fillState = true;
                    //Fillpencils.add(fillpencil);
                    //fillpencil.constructShape();
                    //saveImage();
                    break;
                default:
                    break;

            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            switch (pressIndex) {
                case 0:
                    break;
                case 1:
                    Point point = new Point(e.getX(), e.getY());
                    int lenx = point.x - oldPoint.x;
                    int leny = point.y - oldPoint.y;
                    tmpSelectedItem.x0 = selectedItem.x0 + lenx;
                    tmpSelectedItem.x1 = selectedItem.x1 + lenx;
                    tmpSelectedItem.y0 = selectedItem.y0 + leny;
                    tmpSelectedItem.y1 = selectedItem.y1 + leny;
                    tmpSelectedItem.constructShape();
                    isDragge = true;
                    break;
                case 2:
                    MyItem Item = selectedItem;// Items.get(selectPointInWhichItem);
                    if (Item.isSelectP0) {
                        constructXY0(Item, e.getX(), e.getY());
                    }
                    if (Item.isSelectP1) {
                        constructXY1(Item, e.getX(), e.getY());
                    }
                    Item.constructShape();
                    break;
                case 3:
                    constructXY1(inItem, e.getX(), e.getY());
                    inItem.constructShape();
                    break;
                default:
                    break;

            }
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            switch (pressIndex) {
                case 0:
                    break;
                case 1:
                    Point point = new Point(e.getX(), e.getY());
                    int lenx = point.x - oldPoint.x;
                    int leny = point.y - oldPoint.y;
                    selectedItem.x0 += lenx;
                    selectedItem.x1 += lenx;
                    selectedItem.y0 += leny;
                    selectedItem.y1 += leny;
                    selectedItem.constructShape();
                    tmpSelectedItem = null;
                    if (!isDragge && e.isPopupTrigger()) {
                        popUp.show(e.getComponent(), e.getX(), e.getY());
                    }
                    isDragge = false;
                    break;
                case 2:
                    MyItem Item = selectedItem;
                    if (Item.isSelectP0) {
                        constructXY0(Item, e.getX(), e.getY());
                    }
                    if (Item.isSelectP1) {
                        constructXY1(Item, e.getX(), e.getY());
                    }
                    Item.constructShape();
                    break;
                case 3:
                    constructXY1(inItem, e.getX(), e.getY());
                    inItem.constructShape();
                    Items.add(inItem);
                    selectedItem = inItem;
                    selectedItem.isSelected = true;
                    inItem = null;
                    pressIndex = 0;
                    break;
                case 4:
                    saveImage();
                    break;
                default:
                    break;

            }
            // 统一在鼠标释放的时候重绘
            repaint();

        }

    }

    class PopupAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        public PopupAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Items.remove(selectedItem)) {
                selectedItem = null;
                pressIndex = 0;
                JOptionPane.showMessageDialog(null, "已删除", "操作成功", JOptionPane.INFORMATION_MESSAGE);
            }
            repaint();
        }

    }

    private static final long serialVersionUID = 1L;

    TestItem ti;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JFrame frame = new JFrame("画图");
                frame.setSize(1000, 1000);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
                JButton addLine = new JButton("添加直线");
                addLine.setHorizontalTextPosition(SwingConstants.CENTER);
                addLine.setBounds(0, 900, 1000/3, 50);
                JButton addCircle = new JButton("添加圆");
                addCircle.setHorizontalTextPosition(SwingConstants.CENTER);
                addCircle.setBounds(1000/3,900,1000/3,50);
                JButton addOval = new JButton("添加椭圆");
                addOval.setHorizontalTextPosition(SwingConstants.CENTER);
                addOval.setBounds(2000 / 3, 900, 1000/3, 50);
                JButton addRectangle = new JButton("添加长方形");
                addRectangle.setHorizontalTextPosition(SwingConstants.CENTER);
                addRectangle.setBounds(0, 850, 1000/3, 50);
                JButton addTriangle = new JButton("添加三角形");
                addTriangle.setHorizontalTextPosition(SwingConstants.CENTER);
                addTriangle.setBounds(1000/3,850,1000/3,50);
                JButton fillColor = new JButton("填充");
                fillColor.setHorizontalTextPosition(SwingConstants.CENTER);
                fillColor.setBounds(2000/3,850, 1000/3, 50);
                TestItem ti = new TestItem();
                ti.setBounds(0, 0, 1000, 800);
                addLine.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ti.pressIndex = 3;
                        ti.itemIndex = 1;
                    }
                });
                addCircle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ti.pressIndex = 3;
                        ti.itemIndex = 2;
                    }
                });
                addOval.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ti.pressIndex = 3;
                        ti.itemIndex = 3;
                    }
                });
                addRectangle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ti.pressIndex = 3;
                        ti.itemIndex = 4;
                    }
                });
                addTriangle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ti.pressIndex = 3;
                        ti.itemIndex = 5;
                    }
                });
                fillColor.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ti.pressIndex = 4;
                    }
                });
                frame.add(addLine);
                frame.add(addCircle);
                frame.add(addOval);
                frame.add(addRectangle);
                frame.add(addTriangle);
                frame.add(fillColor);
                frame.getContentPane().add(ti);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }

    // 初始化鼠标事件监听
    public TestItem() {
        MyMouseListener ml = new MyMouseListener();
        addMouseListener(ml);
        addMouseMotionListener(ml);
        constructPopUp();
        panelImage = new BufferedImage(1000, 850, BufferedImage.TYPE_INT_RGB);
    }

    public void saveImage(){
        //BufferedImage bufImage = deepCopy(ti.panelImage);
        JComponent tmpImage = (JComponent)ti;
        tmpImage.printAll(ti.panelImage.getGraphics());
        BufferedImage bufImage = deepCopy(ti.panelImage);
        // Color bufcolor = new Color(bufImage.getRGB(x - 100, y - 100));
        try {
            ImageIO.write(ti.panelImage, "png", new File("E:/","1.png"));
            System.out.println("Catch!");
        } catch (IOException ey) {
            ey.printStackTrace();
        }
    }

    private void constructPopUp() {
        popUp = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAction(new PopupAction("删除"));
        popUp.add(menuItem);
    }

    //判断两点间距是否足够小，足够小则可以视为重合，用来确认是否指示端点
    private boolean isRange(Point a, Point b) {
        if (Math.hypot(a.x - b.x, a.y - b.y) < range)
            return true;
        return false;
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public Color getPixel(int x, int y){//获得当前坐标颜色信息
        Robot rb = null;
        try {
            rb = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(Paint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rb.getPixelColor(x, y);
    }

    // 画图
    protected void paintComponent(Graphics g2) {
        super.paintComponents(g2);
        g2.drawImage(panelImage, 0, 0, null);
        g2.setColor(new Color(245, 245, 220));
        g2.fillRect(0, 0, 1000, 850);
        g = (Graphics2D) g2;
        Paint oldPaint = g.getPaint();
        Stroke s = g.getStroke();

        for (MyItem Item : Items) {
            g.setStroke(new BasicStroke(MyItem.lineWidth));
            g.setPaint(Color.BLACK);
            GeneralPath shape = Item.shape;
            g.draw(shape);
         /*   Robot rb = null;
            try {
                rb = new Robot();
            } catch (AWTException ex) {
                Logger.getLogger(Paint.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.print(rb.getPixelColor(100, 100));*/
        }

        for (BoundaryFill fill: Fillpencils){
            g.setStroke(new BasicStroke(MyItem.lineWidth));
            g.setPaint(Color.RED);
            GeneralPath shape = fill.shape;
            g.draw(shape);
        }
        // 如果被选择，那么直线的两端应该会有两个蓝色的点
        if (selectedItem != null) {
            MyItem Item = selectedItem;
            g.setPaint(Color.BLACK);
            int r = 6;
            g.drawOval(Item.a0 - r / 2, Item.b0 - r / 2, r, r);
            g.drawOval(Item.a1 - r / 2, Item.b1 - r / 2, r, r);
            g.setPaint(Color.BLUE);
            g.fillOval(Item.a0 - r / 2, Item.b0 - r / 2, r, r);
            g.fillOval(Item.a1 - r / 2, Item.b1 - r / 2, r, r);
            //这里使用了画椭圆的库函数，将两个焦半径设为相同，在端点处填充一个圆
        }

        //画移动时的图元
        if (tmpSelectedItem != null) {
            g.setStroke(new BasicStroke(MyItem.lineWidth));
            g.setPaint(Color.BLACK);
            GeneralPath shape = tmpSelectedItem.shape;
            g.draw(shape);
        }

        // 画正在产生的图元
        if (inItem != null) {
            g.setStroke(new BasicStroke(MyItem.lineWidth));
            g.setPaint(Color.BLACK);
            GeneralPath shape = inItem.shape;
            if (shape != null)
                g.draw(shape);
        }

       /* if (pressIndex == 4 && fillState == true){
            g.setStroke(new BasicStroke(MyItem.lineWidth));
            g.setPaint(Color.RED);
            GeneralPath shape = fillpencil.shape;
         //   shape = new GeneralPath(Path2D.WIND_NON_ZERO);
         //   shape.moveTo(paintSeedx, paintSeedy);
         //   shape.lineTo(paintSeedx-2, paintSeedy-2);
         //   shape.closePath();
            if(shape != null)
                g.draw(shape);
         //   boundaryFill4(paintSeedx, paintSeedy, paintSeedx, paintSeedy);
         //   fillState = false;
        }*/

        g.setPaint(oldPaint);
        g.setStroke(s);
        g2.dispose();
    }
}

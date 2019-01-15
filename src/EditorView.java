import javafx.geometry.HorizontalDirection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.*;
import java.awt.*;


/**
 * Created by Allen on 7/17/2017.
 */
public class EditorView extends JFrame{
    EditPanel EditView;
    Editor MainEdit;
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    JLabel statusLabel = new JLabel("Waiting for Operaitons");
    JButton Save;
    JButton Exit;
    JButton New;
    JPanel Util;


    public void ButtonInit(){
        JPanel x = new JPanel();
        JPanel y = new JPanel();
        JPanel z = new JPanel();
        BListener BL = new BListener();
        New = new JButton("Create Obstacle");
        Exit = new JButton("Close Editor");
        Save = new JButton("Save Map");
        New.addActionListener(BL);
        Exit.addActionListener(BL);
        Save.addActionListener(BL);
        x.add(New);
        y.add(Save);
        z.add(Exit);
        x.setAlignmentX(Component.CENTER_ALIGNMENT);
        y.setAlignmentX(Component.CENTER_ALIGNMENT);
        z.setAlignmentX(Component.CENTER_ALIGNMENT);
        x.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        y.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        z.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Util.add(x,BorderLayout.LINE_START);
        Util.add(y,BorderLayout.CENTER);
        Util.add(z,BorderLayout.LINE_END);


    }

    public EditorView(Editor file){
        MainEdit = file;
        this.setTitle("Editing :" + MainEdit.mapPath);
        EditView = new EditPanel();
        JPanel Wrap = new JPanel();
        Util = new JPanel();
        Util.setLayout(new BorderLayout(5,5));
        ButtonInit();
        Wrap.setLayout(new BoxLayout(Wrap,BoxLayout.PAGE_AXIS));
        JSlider EditSlider = new JSlider(JSlider.HORIZONTAL,0,MainEdit.width,0);
        EditSlider.setMajorTickSpacing(10);
        EditSlider.setMinorTickSpacing(1);
        EditSlider.setPaintTicks(true);
        EditSlider.setPaintLabels(true);
        EditSlider.addChangeListener(new scrollListener());
        this.setSize(new Dimension(960,700));
        this.setResizable(false);
        Wrap.add(EditView);
        Wrap.add(EditSlider);
        JPanel Wrap2 = new JPanel();
        Wrap2.add(statusLabel);
        Wrap2.setMinimumSize(new Dimension(960,20));
        Wrap2.setAlignmentX(Component.CENTER_ALIGNMENT);
        Wrap.add(Wrap2);
        Wrap.add(Util);
        this.add(Wrap);
        this.setVisible(true);
        this.centerWindow();
        EditView.setFocusable(true);
        EditView.setFocusTraversalKeysEnabled(false);
        EditView.requestFocus();
        EditView.startTimer();
    }

    public void moveObject(MouseEvent e){
        for (Editor.Obstacles obs : MainEdit.convObs) {
            if (obs.id == MainEdit.selectedItem) {
                obs.top_leftx = MainEdit.bx;
                obs.top_lefty = MainEdit.by;
                int ox = MainEdit.originx;
                int oy = MainEdit.originy;
                int nx = e.getX();
                int ny = e.getY();
                MainEdit.originx = nx;
                MainEdit.originy = ny;
                int verticalBlockshifts = findUnitShift(ox, nx);
                int horizontalBlockshifts = findUnitShift(oy, ny);
                int resultx = obs.top_leftx + verticalBlockshifts * MainEdit.verticalRatio;;
                int resulty = obs.top_lefty + horizontalBlockshifts * MainEdit.verticalRatio;
                boolean collisionDetected = false;
                for(Editor.Obstacles obs2: MainEdit.convObs){
                    if(obs.id != obs2.id){
                        Rectangle r1 = new Rectangle(resultx,resulty,obs.bottom_rightx,obs.bottom_righty);
                        Rectangle r2 = new Rectangle(obs2.top_leftx,obs2.top_lefty,obs2.bottom_rightx,obs2.bottom_righty);
                        if (r1.intersects(r2)){
                            collisionDetected = true;
                            break;
                        }
                    }
                }
                if (collisionDetected == false) {
                    Editor.Operation old = new Editor.Operation(obs.top_leftx, obs.top_lefty, obs.id, "move");
                    MainEdit.opStack.push(old);
                    String shifts = "Vertical Shifts: " + verticalBlockshifts + " " + "Horizontal Shifts: " + horizontalBlockshifts;
                    int div = MainEdit.verticalRatio;
                    statusLabel.setText("Box ID: " + obs.id + ", origin (" + ox / div + ", " + oy / div + ") destination (" + nx / div + ", " + ny / div + ") " + shifts);
                    obs.top_leftx += verticalBlockshifts * MainEdit.verticalRatio;
                    obs.top_lefty += horizontalBlockshifts * MainEdit.verticalRatio;
                } else {
                    statusLabel.setText("Cannot overlap obstacles, please retry!");
                }
            }
        }
    }

    public void expandObject (MouseEvent e){
        for (Editor.Obstacles obs : MainEdit.convObs) {
            if (obs.id == MainEdit.selectedItem) {
                obs.bottom_rightx = MainEdit.bkwidth;
                obs.bottom_righty = MainEdit.bkheight;
                int ox = MainEdit.originx;
                int oy = MainEdit.originy;
                int nx = e.getX();
                int ny = e.getY();
                int horizontalBlockshifts = findUnitShift(ox, nx);
                int verticalBlockshifts = findUnitShift(oy, ny);
                int bh = obs.bottom_rightx;
                int bw = obs.bottom_righty;
                int nheight = bh + verticalBlockshifts* MainEdit.verticalRatio;
                int nwidth = bw + horizontalBlockshifts* MainEdit.verticalRatio;
                //System.out.println(verticalBlockshifts + " " + horizontalBlockshifts);
                if (nwidth < MainEdit.verticalRatio && nheight >= MainEdit.verticalRatio ) {
                    obs.bottom_rightx  = MainEdit.verticalRatio;
                    obs.bottom_righty += verticalBlockshifts * MainEdit.verticalRatio;
                }else if (nwidth >= MainEdit.verticalRatio && nheight < MainEdit.verticalRatio ) {
                    obs.bottom_rightx  += horizontalBlockshifts *MainEdit.verticalRatio;
                    obs.bottom_righty  = MainEdit.verticalRatio;
                } else if (nwidth < MainEdit.verticalRatio && nheight < MainEdit.verticalRatio ){
                    obs.bottom_rightx  = MainEdit.verticalRatio;
                    obs.bottom_righty  = MainEdit.verticalRatio;
                } else {
                    obs.bottom_rightx += horizontalBlockshifts *MainEdit.verticalRatio;
                    obs.bottom_righty += verticalBlockshifts * MainEdit.verticalRatio;
                }
                int div = MainEdit.verticalRatio;
                String ps = "Box ID: " + obs.id + ", size changed from  ( width: " + bh/div + ", length: " + bw/div  + " ) to ( width: " + obs.bottom_rightx/div  + ", length: " + obs.bottom_righty/div  + ")";
                statusLabel.setText(ps);
                Editor.Operation old = new Editor.Operation(bh, bw, obs.id, "resize");
                MainEdit.opStack.push(old);

            }
        }
    }

    public class scrollListener implements ChangeListener{

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider read = (JSlider) e.getSource();
            statusLabel.setText("Offset now at: " + (int) read.getValue());
            MainEdit.offset = (int) read.getValue()* MainEdit.verticalRatio * -1;
        }
    }

    public void select(MouseEvent e){
        Point currentposition = e.getPoint();
        int x = currentposition.x;
        int y = currentposition.y;
        boolean over = false;
        int overID = -1;
        for(Editor.Obstacles obs : MainEdit.convObs){
            int rightxbound = obs.display_leftx+ obs.bottom_rightx;
            int rightybound = obs.display_lefty+obs.bottom_righty;
            if(y < rightybound && y > obs.display_lefty && x > obs.display_leftx && x < rightxbound){
                overID = obs.id;
                over = true;
            }
        }
        if (over == true){
            MainEdit.selected = true;
            MainEdit.selectedItem = overID;
            statusLabel.setText(overID + " has been selected.");
        } else {
            MainEdit.selected = false;
            MainEdit.selectedItem = -1;
            statusLabel.setText("No object selected");
        }
    }

    public int findUnitShift(int x1, int x2){
        int diff = x2 - x1;
        if(diff < 0){
            return (int)Math.ceil(((double)diff/MainEdit.verticalRatio));
        } else {
            return (int)Math.floor(((double)diff/MainEdit.verticalRatio));
        }
    }


    public void Exit(){
        this.setVisible(false);
        this.dispose();
    }

    public void centerWindow(){
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }

    private class BListener implements ActionListener  {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == New) {
                MainEdit.newObs();
                statusLabel.setText("New Obstacle has been created, located at top left of the screen.");
            } else if (e.getSource() == Exit) {
                Exit();
            } else if (e.getSource() == Save) {
                MainEdit.Save();
                statusLabel.setText(MainEdit.mapPath + " has been successfully overwritten.");

            }
        }
    }


    public class EditPanel extends JPanel implements MouseListener , MouseMotionListener, KeyListener{
        Timer DisplayTimer;
        Timer UpdateTimer;
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        int x = (int) b.getX();
        int y = (int) b.getY();

        public EditPanel (){
            addMouseListener(this);
            addMouseMotionListener(this);
            addKeyListener(this);
            setPreferredSize(new Dimension(960,540));
            setMinimumSize(new Dimension(960,540));
            this.DisplayTimer = new Timer(1000/60, (ActionEvent ae) -> {
                repaint();
            });
            this.UpdateTimer = new Timer(1000/60, (ActionEvent ae) -> {
                for(Editor.Obstacles o : MainEdit.convObs){
                    o.display_leftx = o.top_leftx + MainEdit.offset;
                    o.display_lefty = o.top_lefty;
                }
            });
        }

        public void startTimer(){
            DisplayTimer.start();
            UpdateTimer.start();
        }


        public void paintComponent(Graphics g){
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            g2d.drawLine(0,540,960,540);
            g2d.drawLine(0,640,960,640);
            for(Editor.Obstacles o : MainEdit.convObs){
                if (MainEdit.selectedItem == o.id){
                    g2d.setColor(MainEdit.HLightCol);
                    g2d.fillRect(o.display_leftx-2,o.display_lefty-2,o.bottom_rightx+4,o.bottom_righty+4);
                }
                g2d.setColor(MainEdit.EditObsCol);
                g2d.fillRect(o.display_leftx,o.display_lefty,o.bottom_rightx,o.bottom_righty);
            }

        }


        @Override
        public void mouseClicked(MouseEvent e) {
            //System.out.println("Mouse Clicked");
            select(e);

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (MainEdit.selected == true) {
                MainEdit.originx = e.getX();
                MainEdit.originy = e.getY();
                int x = e.getX();
                int y = e.getY();
                for (Editor.Obstacles obs : MainEdit.convObs) {
                    int rightxbound = obs.display_leftx + obs.bottom_rightx;
                    int rightybound = obs.display_lefty + obs.bottom_righty;
                    if (y < rightybound && y > obs.display_lefty && x > obs.display_leftx && x < rightxbound && obs.id == MainEdit.selectedItem) {
                        MainEdit.initialDragOver = true;
                        MainEdit.bkheight = obs.bottom_righty;
                        MainEdit.bkwidth = obs.bottom_rightx;
                        MainEdit.bx = obs.top_leftx;
                        MainEdit.by = obs.top_lefty;
                    }
                }
            }

        }





        @Override
        public void mouseReleased(MouseEvent e) {
            if (MainEdit.selected == true && MainEdit.initialDragOver == true ){
                MainEdit.initialDragOver = false;
                if (MainEdit.inDrag == true){
                    MainEdit.inDrag = false;
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        moveObject(e);
                    } else if (SwingUtilities.isRightMouseButton(e)){
                        expandObject(e);
                    }
                }
            } else if (MainEdit.selected == true && MainEdit.initialDragOver == false){
                MainEdit.selected = false;
                MainEdit.selectedItem = -1;
                statusLabel.setText("No object selected");
            }
            if (MainEdit.selected== false){
                select(e);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            this.requestFocus();
        }

        @Override
        public void mouseExited(MouseEvent e) {
           //System.out.println("Exited");
        }

        @Override
        public void mouseDragged(MouseEvent e){
            if (MainEdit.inDrag == false) {
                MainEdit.inDrag = true;
            }
            if(SwingUtilities.isRightMouseButton(e) && MainEdit.initialDragOver == true){
                int x = MainEdit.originx;
                int y = MainEdit.originy;
                int x2 = e.getX();
                int y2 = e.getY();
                for(Editor.Obstacles obs : MainEdit.convObs){
                    if (obs.id == MainEdit.selectedItem) {
                        int horizontalBlockshifts = findUnitShift(x, x2);
                        int verticalBlockshifts = findUnitShift(y, y2);
                        int bh = MainEdit.bkheight;
                        int bw = MainEdit.bkwidth;
                        int nheight = bh + verticalBlockshifts* MainEdit.verticalRatio;
                        int nwidth = bw + horizontalBlockshifts* MainEdit.verticalRatio;
                        if (nwidth < MainEdit.verticalRatio && nheight >= MainEdit.verticalRatio ) {
                            obs.bottom_rightx  = MainEdit.verticalRatio;
                            obs.bottom_righty  = MainEdit.bkheight + verticalBlockshifts * MainEdit.verticalRatio;
                        }else if (nwidth >= MainEdit.verticalRatio && nheight < MainEdit.verticalRatio ) {
                            obs.bottom_rightx  = MainEdit.bkwidth + horizontalBlockshifts * MainEdit.verticalRatio;
                            obs.bottom_righty  = MainEdit.verticalRatio;
                        } else if (nwidth < MainEdit.verticalRatio && nheight < MainEdit.verticalRatio ){
                            obs.bottom_rightx  = MainEdit.verticalRatio;
                            obs.bottom_righty  = MainEdit.verticalRatio;
                        } else {
                            obs.bottom_rightx  = MainEdit.bkwidth + horizontalBlockshifts * MainEdit.verticalRatio;
                            obs.bottom_righty  = MainEdit.bkheight + verticalBlockshifts * MainEdit.verticalRatio;
                        }
                    }
                }
            } else if (SwingUtilities.isLeftMouseButton(e)&& MainEdit.initialDragOver == true){
                for(Editor.Obstacles obs : MainEdit.convObs) {
                    if (obs.id == MainEdit.selectedItem){
                        int x = MainEdit.originx;
                        int y = MainEdit.originy;
                        int x2 = e.getX();
                        int y2 = e.getY();
                        //MainEdit.originx = x2;
                        //MainEdit.originy = y2;
                        int verticalBlockshifts = findUnitShift(x, x2);
                        int horizontalBlockshifts = findUnitShift(y, y2);
                        obs.top_leftx = MainEdit.bx + verticalBlockshifts * MainEdit.verticalRatio;
                        obs.top_lefty = MainEdit.by + horizontalBlockshifts * MainEdit.verticalRatio;
                    }
                }
            }

            //statusLabel.setText("Mouse Dragged");
        }

        @Override
        public void mouseMoved(MouseEvent e){
        }
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        public boolean colCheck(Rectangle r, int id){
            for(Editor.Obstacles obs : MainEdit.convObs){
                if(id != obs.id){
                    Rectangle r2 = new Rectangle(obs.top_leftx,obs.top_lefty,obs.bottom_rightx,obs.bottom_righty);
                    if (r.intersects(r2)){
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            if (e.isControlDown()) {
                if(key == KeyEvent.VK_Z ) {
                    if (!MainEdit.opStack.isEmpty()) {
                        Editor.Operation x = MainEdit.opStack.pop();
                        statusLabel.setText(x.type + " undone.");
                        MainEdit.Reverse(x);
                    } else {
                        statusLabel.setText("No more moves to be undone.");
                    }
                } else if (key == KeyEvent.VK_C && MainEdit.selected == true){
                    MainEdit.Copied = true;
                    for(Editor.Obstacles obs: MainEdit.convObs){
                        if(MainEdit.selectedItem == obs.id){
                            MainEdit.copy(obs.bottom_rightx,obs.bottom_righty);
                            statusLabel.setText("Object: " + obs.id + " has been succesfully copied!");
                        }
                    }
                } else if (key == KeyEvent.VK_V ){
                    if (MainEdit.Copied == true){
                        MainEdit.Paste();
                    } else {
                        statusLabel.setText("ERROR! Nothing found on the clipboard, please copy something!");
                    }
                }
            } else {
                if (MainEdit.selected = true) {
                    for(Editor.Obstacles obs: MainEdit.convObs){
                        if (obs.id == MainEdit.selectedItem){
                            if (key == KeyEvent.VK_DELETE) {
                                MainEdit.pendingDelete = obs.id;
                            } else {
                                int xchange = 0;
                                int ychange = 0;
                                String dir = "no where";
                                int unit = MainEdit.verticalRatio;
                                Rectangle r = new Rectangle(obs.top_leftx,obs.top_lefty,obs.bottom_rightx,obs.bottom_righty);
                                if (key == KeyEvent.VK_W) {
                                    r.setRect(obs.top_leftx,obs.top_lefty-unit,obs.bottom_rightx,obs.bottom_righty);
                                    ychange = -MainEdit.verticalRatio;
                                    dir = "Up";
                                } else if (key == KeyEvent.VK_A) {
                                    r.setRect(obs.top_leftx-unit,obs.top_lefty,obs.bottom_rightx,obs.bottom_righty);
                                    xchange = -MainEdit.verticalRatio;
                                    dir = "Left";
                                } else if (key == KeyEvent.VK_S) {
                                    r.setRect(obs.top_leftx,obs.top_lefty+unit,obs.bottom_rightx,obs.bottom_righty);
                                    ychange = MainEdit.verticalRatio;
                                    dir = "Down";
                                } else if (key == KeyEvent.VK_D) {
                                    r.setRect(obs.top_leftx+unit,obs.top_lefty,obs.bottom_rightx,obs.bottom_righty);
                                    xchange = MainEdit.verticalRatio;
                                    dir = "Right";
                                }
                                boolean intsec = colCheck(r,obs.id);
                                if (intsec == false){
                                    MainEdit.opStack.push(new Editor.Operation(obs.top_leftx, obs.top_lefty, obs.id, "move"));
                                    obs.top_lefty += ychange;
                                    obs.top_leftx += xchange;
                                    statusLabel.setText("Shifted " + dir + " by one unit");
                                }


                            }
                        }
                    }
                    if (MainEdit.pendingDelete != -1) {
                        statusLabel.setText("Deleted Box ID : " + MainEdit.pendingDelete);
                        MainEdit.deleteObs();
                        MainEdit.selected = false;
                        MainEdit.selectedItem = -1;
                    }
                }
            }

        }
    }
}

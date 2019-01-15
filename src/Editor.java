/**
 * Created by Allen on 7/17/2017.
 */

import java.awt.*;
import java.awt.image.ConvolveOp;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Editor {

    String mapPath = null;
    public List<Obstacles> mapObs;
    public List<Obstacles> convObs;
    public List<Obstacles> result;
    Obstacles hoveringOver = null;
    int height;
    int width;
    int verticalRatio;
    BufferedReader br;
    int offset;
    int selectedItem = -1;
    boolean selected = false;
    boolean inDrag = false;
    boolean initialDragOver = false;
    int originx;
    int originy;
    int pendingDelete = -1;
    int nextID = -1;

    int bkheight;
    int bkwidth;


    boolean Copied = false;
    int cl;
    int ch;

    public void copy( int l, int h){
        cl = l;
        ch = h;

    }



    int bx;
    int by;
    Color EditObsCol = Color.YELLOW;
    Color HLightCol= Color.BLACK;


    public void convertBackToReal(){
        result = new ArrayList();
        for(Obstacles obs : convObs){
            int x = obs.top_leftx;
            int y = obs.top_lefty;
            int l = obs.bottom_rightx;
            int h = obs.bottom_righty;
            x = x/verticalRatio;
            y = y/verticalRatio;
            l = l/verticalRatio;
            h = h/verticalRatio;
            result.add(new Obstacles(x,y,x+l,y+h,0));
        }
    }

    public void Save(){
        convertBackToReal();
        File map = new File(mapPath);
        try {
            FileWriter Writer = new FileWriter(map, false);
            Writer.write(width + ", " + height);
            Writer.write("\r\n");
            for(Obstacles obs: result){
                String res = obs.top_leftx + ", " +obs.top_lefty + ", " + obs.bottom_rightx + ", " + obs.bottom_righty;
                Writer.write(res);
                Writer.write("\r\n");
            }
            Writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    public void deleteObs(){
        for(int i = 0; i < convObs.size();++i){
            if (convObs.get(i).id == pendingDelete) {
                Obstacles x = convObs.get(i);
                opStack.add(new Operation(x.top_leftx,x.top_lefty,x.bottom_rightx,x.bottom_righty,x.id,"delete"));
                convObs.remove(i);
                pendingDelete =-1;
            }
        }
    }

    public void newObs(){
        int i = nextID;
        this.convObs.add(new Editor.Obstacles(-offset,0,verticalRatio,verticalRatio, i));
        this.selectedItem = i;
        ++nextID;
        Operation x = new Operation(i);
        opStack.push(x);
    }

    public void Paste(){
        convObs.add(new Editor.Obstacles(480-offset,270,cl,ch,nextID));
        Editor.Operation x = new Editor.Operation(nextID);
        opStack.push(x);
        nextID++;
    }




    public Stack<Operation> opStack;


    public Editor(String s) throws IOException {
        mapPath = s;
        System.out.println("Editing: " +  mapPath);
        br = new BufferedReader(new FileReader(mapPath));
        mapObs = new ArrayList();
        convObs = new ArrayList();
        opStack = new Stack();
        this.ImportLevels();
        verticalRatio = 540/height;
        this.convertObsCords();
        //this.printObstacles();
    }

    public void addObs(int a,int b,int c,int d, int id){
        Obstacles o = new Obstacles(a,b,c,d,id);
        mapObs.add(o);
    }

    public void printObstacles(){
        for(Obstacles obs : result){
            obs.showObs();
        }
    };




    public void ImportLevels() throws IOException {
        int i = 0;
        String s;
        boolean acquired = false;
        String[] parts;
        while ((s = br.readLine()) != null) {
            //System.out.println(s);
            if (!s.startsWith("#")) {
                parts = s.split(", ");
                if (acquired == false) {
                    acquired = true;
                    width = Integer.parseInt(parts[0]);
                    height = Integer.parseInt(parts[1]);
                } else {
                    int x1 = Integer.parseInt(parts[0]);
                    int y1 = Integer.parseInt(parts[1]);
                    int x2 = Integer.parseInt(parts[2]);
                    int y2 = Integer.parseInt(parts[3]);
                    if (x1 == x2){
                        x2++;
                    }
                    if (y1 == y2){
                        y2++;
                    }
                    this.addObs(x1,y1,x2,y2,i);
                    ++i;

                }
            }
        }
        nextID = i;

    }

    //private abstract class Operation {
    //
    //
    //}

    public void Reverse(Operation  op){
        int id = op.obsID;
        if (op.type == "delete") {
            Obstacles x = new Obstacles(op.x, op.y, op.l, op.h, op.obsID);
            convObs.add(x);
        } else if (op.type == "new"){
            for(int i = 0; i < convObs.size();++i){
                if (convObs.get(i).id == id) {
                    convObs.remove(i);
                }
            }
        } else {
            for (Obstacles obs : convObs) {
                if (obs.id == id) {
                    if (op.type == "move") {
                        obs.top_leftx = op.x;
                        obs.top_lefty = op.y;
                    } else if (op.type == "resize") {
                        obs.bottom_rightx = op.x;
                        obs.bottom_righty = op.y;
                    }
                }
            }
        }
    }

    public void convertObsCords(){
        for(Obstacles obs : mapObs){
            int x,y,l,h;
            x = obs.top_leftx;
            y = obs.top_lefty;
            l = obs.bottom_rightx-x;
            h = obs.bottom_righty-y;
            Obstacles converted = new Obstacles(x*verticalRatio,y*verticalRatio,l*verticalRatio,h*verticalRatio, obs.id);
            convObs.add(converted);
        }
    }
    public static class Operation {
        String type;
        int obsID;
        int x,y,l,h;
        Operation (int id){
            obsID = id;
            type = "new";
        }
        Operation (int x, int y, int l, int h, int id,String type){  // For new obstacles
            this.x = x;
            this.y = y;
            this.l = l;
            this.h = h;
            this.type = type;
            obsID = id;
        }
        Operation (int x1, int y1, int id, String move){ //Constructor for both resize and move;
            this.x = x1;
            this.y = y1;
            obsID = id;
            type = move;
        }
    }



    public static class Obstacles {
        int id;
        int top_leftx;
        int top_lefty;
        int display_leftx;
        int display_lefty;
        int bottom_rightx;
        int bottom_righty;


        public Obstacles(int tl, int tr, int bl, int br, int i){
            this.top_leftx = tl;
            this.top_lefty = tr;
            this.display_leftx = tl;
            this.display_lefty = tr;
            this.bottom_rightx = bl;
            this.bottom_righty = br;
            this.id = i;

        }

        public void showObs(){
            System.out.println(top_leftx + " " + top_lefty + " " + bottom_rightx + " " + bottom_righty);
        }
        
    }

}

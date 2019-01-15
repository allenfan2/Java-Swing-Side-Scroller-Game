import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.List;

/**
 * Created by Allen on 7/11/2017.
 */


public class Game {
    //private List<Observer> observers;

    int FPS;
    int length;
    int ObsSpeed;
    int PlayerSpeed;

    int win_l = 1024;
    int win_h = 576;

    int height;
    int width;

    int PlayerSize;
    int PlayerPosX;
    int PlayerPosY;
    int verticalRatio;
    int score = 0;


    String state;




    //public List<Observer> observers;
    public List<Obstacles> Obs;


    public Game(){
        this.Obs = new ArrayList();

    }

    public void printObstacles(){
        for(Obstacles obs : Obs){
            obs.showObs();
        }
    };

    public void setHL(int x, int y){
        length = x;
        height = y;
    }
    public void addObs(int a,int b,int c,int d){
        Obstacles o = new Obstacles(a,b,c,d);
        Obs.add(o);
    }

    public void updatePlayerCords(int Xchange, int Ychange){
            int xAfter = PlayerPosX + Xchange;
            int yAfter = PlayerPosY + Ychange;
            int xR = xAfter + PlayerSize;
            int yR = yAfter + PlayerSize;
            if ((xAfter >= 0 && xR <= win_l && yAfter >= 0 && yR <= win_h)||(state == "Win")) {
                PlayerPosX += Xchange;
                PlayerPosY += Ychange;
                if (PlayerPosX > win_l){
                    state = "Exited";
                }
            } else if (xR > win_l){
                PlayerPosX = win_l - PlayerSize;
            } else if (yR > win_h){
                PlayerPosY = win_h - PlayerSize;
            } else if (xAfter < 0){
                state = "GameOver";
            }
    }

    public void deleteObs(){
        for(int i = 0; i < Obs.size(); ++i) {
            if (Obs.get(i).top_leftx + Obs.get(i).bottom_rightx < 0){
                Obs.remove(i);
                score++;
                //System.out.println("Obstacled Deleted!");
            }
        }
        if (Obs.isEmpty()){
            state = "Win";
        }
    }
    public void colTest(){
        for(Obstacles obs: Obs){
            Rectangle rect = new Rectangle(PlayerPosX,PlayerPosY,PlayerSize,PlayerSize);
            if(rect.intersects(obs.top_leftx,obs.top_lefty,obs.bottom_rightx,obs.bottom_righty)){
                state = "GameOver";
            }

        }
    }

    public void convertObsCords(){
        for(Obstacles obs : Obs){
            int x,y,l,h;
            x = obs.top_leftx;
            y = obs.top_lefty;
            l = obs.bottom_rightx-x;
            h = obs.bottom_righty-y;
            obs.top_leftx = x*verticalRatio;
            obs.top_lefty = y*verticalRatio;
            obs.bottom_rightx = l*verticalRatio;
            obs.bottom_righty = h*verticalRatio;
        }
    }
    public String statusUpdate(){
        return PlayerSize + ":" +PlayerPosX + ", " + PlayerPosY;
    }

    public static class Obstacles {
        int top_leftx;
        int top_lefty;
        int display_leftx;
        int display_lefty;
        int bottom_rightx;
        int bottom_righty;

        public Obstacles(int tl, int tr, int bl, int br){
            this.top_leftx = tl;
            this.top_lefty = tr;
            this.display_leftx = tl;
            this.display_lefty = tr;
            this.bottom_rightx = bl;
            this.bottom_righty = br;
        }

        public void updatePos(int a){
                this.top_leftx += a;
        }

        public void showObs(){
            System.out.println(top_leftx + " " + top_lefty + " " + bottom_rightx + " " + bottom_righty);
        }
    }

    //public void addObserver(Observer observer) {
    //    this.observers.add(observer);
    //}
//
    ///**
    // * Remove an observer from this model.
    // */
    //public void removeObserver(Observer observer) {
    //    this.observers.remove(observer);
    //}
//
    //public void notifyObservers() {
    //    for (Observer observer: this.observers) {
    //        observer.update(this,state);
    //    }
    //}



}

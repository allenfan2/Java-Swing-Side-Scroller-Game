


import javax.imageio.ImageIO;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.List;

public class Model {
    int FPS;
    private String curPage;
    private List<Observer> observers;
    Game MainGame;
    String state;
    public static String currentDirectory = new File("").getAbsolutePath();
    BufferedReader br;
    String mapPath = null;
    Editor curEdit;
    int ObsSpeed = 5;
    int PlayerSpeed = 5;

    int GHeight = 1024;
    int GWidth = 576;

    Color ObsCol = Color.RED;

    Color c1 = Color.YELLOW;
    Color c2= Color.BLACK;

    public void updateRes(){
        MainGame.win_h = GHeight;
        MainGame.win_l = GWidth;
    }


    public void upEditCol1(){
        curEdit.EditObsCol = c1;
    }

    public void upEditCol2(){
        curEdit.HLightCol = c2;
    }




    public void UpdateFPS(){
        MainGame.FPS = this.FPS;
        MainGame.ObsSpeed = this.ObsSpeed;
        MainGame.PlayerSpeed= this.PlayerSpeed;
    }

    public void ImportLevels() throws IOException {
        String s;
        boolean acquired = false;
        String [] parts;
        while ((s = br.readLine()) != null) {
            if (!s.startsWith("#")) {
                parts = s.split(", ");
                if (acquired == false){
                    acquired = true;
                    MainGame.setHL(Integer.parseInt(parts[0]),Integer.parseInt(parts[1]));
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
                    MainGame.addObs(x1,y1,x2,y2);
                }
            }
        }
        MainGame.PlayerSize = MainGame.win_h/MainGame.height;
        MainGame.PlayerPosY = MainGame.win_h/2 - MainGame.PlayerSize;
        MainGame.PlayerPosX = MainGame.PlayerSize;
        MainGame.verticalRatio = MainGame.win_h/MainGame.height;

    }

    public void SetMapPath() throws FileNotFoundException {
        br = new BufferedReader(new FileReader(mapPath));
    }

    public void RestartSameLevel() throws IOException {
        br = new BufferedReader(new FileReader(mapPath));
        MainGame = new Game();
        updateRes();
        ImportLevels();
        UpdateFPS();
        MainGame.convertObsCords();
    }

    public void GameCond(){
        this.state = MainGame.state;
        //System.out.println(state);
        this.notifyObservers();
    }

    public int WinHeight(){
        return MainGame.win_h;
    }
    public int WinWidth(){return MainGame.win_l;}




    /**
     * Create a new model.
     */
    public Model() throws IOException {
        MainGame = new Game();
        this.FPS = 60;
        this.state = "Main Menu";
        this.observers = new ArrayList();
    }

    /**
     * Add an observer to be notified when this model changes.
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Remove an observer from this model.
     */
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Notify all observers that the model has changed.
     */

    public void updatePage(String s){
        this.curPage = s;
        System.out.println(s);
    }


    public void notifyObservers() {
        for (Observer observer: this.observers) {
            observer.update(this,state);
        }
    }
}

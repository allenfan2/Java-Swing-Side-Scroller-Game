/**
 * Created by Allen on 7/11/2017.
 */

import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainView extends JFrame implements Observer {

    final static String MAIN_MENU = "Main Menu";
    final static String SETUP_MENU = "Setup Menu";
    final static String GAME_SCREEN = "Game";
    final static String Win_Screen = "Win Screen";
    final static String SETTING_PAGE = "Setting Screen";
    final static String HELP_PAGE = "Help";
    EditorView edit;

    private Model model;
    private JPanel Card;
    private JPanel MenuInterface;
    private JPanel GameSetup;
    private GamePanel GameView;
    private JPanel WinScreen;

    private static String currentDirectory = new File("").getAbsolutePath();

    //Main Menu Buttons
    JButton startButton;
    JButton exitButton;
    JButton helpButton;
    JButton editButton;
    JButton setButton;

    JButton Retry, BackToMenu;  // Retry / Win Window

    JPanel setPanel;
    JPanel helpPanel;
    JButton Return;



    JButton LaunchButton;
    JButton CMap;
    JButton BackToMenuFromSetup;
    JLabel mapLoadStatus;
    JLabel OS;
    JLabel PS;
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    BufferedImage bg;
    BufferedImage helpInfo;
    JButton back2Menu;

    JPanel SetPanel;
    JComboBox obs;
    JComboBox editobs;
    JComboBox edithl;


    String[] cPalet = {"Black","White","Red","Blue","Yellow","Green","Orange","Pink","Cyan"};
    Color[] x = {Color.BLACK,Color.WHITE,Color.RED,Color.BLUE,Color.YELLOW,Color.GREEN, Color.ORANGE,Color.PINK,Color.CYAN};



    public class ColorImp implements ActionListener{
        int i;

        ColorImp(int x){
            i = x;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String s = "red";
            if (i == 0){
                s = (String) obs.getSelectedItem();
            } else if (i == 1){
                s = (String) editobs.getSelectedItem();
            } else if (i == 2){
                s = (String) edithl.getSelectedItem();
            }
            s.toLowerCase();
            Color color;
            color = x[Arrays.asList(cPalet).indexOf(s)];
            if (i == 0){
                model.ObsCol = color;
            } else if (i == 1){
                model.c1 = color;
                model.upEditCol1();
            } else if (i == 2){
                model.c2 = color;
                model.upEditCol2();
            }
        }
    }


    public void SetInit(){

        SetPanel = new JPanel();
        SetPanel.setLayout(new BoxLayout(SetPanel,BoxLayout.PAGE_AXIS));
        JPanel wrap = new JPanel();
        wrap.setLayout(new GridLayout(0,2,5,0));
        back2Menu = new JButton("Return To Menu");
        MainButtonListener x = new MainButtonListener();
        back2Menu.addActionListener(x);
        String[] reso = {"960x540","1024x576","1152x648","1280x720","1366x768","1600x900","1920x1080"};
        String[] obsColor = {"Black","White","Red","Blue","Yellow","Green","Orange","Pink","Cyan"};
        JComboBox resobox = new JComboBox(reso);
        obs = new JComboBox(obsColor);
        editobs = new JComboBox(obsColor);
        edithl = new JComboBox(obsColor);


        ActionListener CBListener1 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = (String) resobox.getSelectedItem();
                String [] parts = s.split("x");
                int l = Integer.parseInt(parts[0]);
                int h = Integer.parseInt(parts[1]);
                model.GHeight = h;
                model.GWidth = l;
            }
        };

        resobox.addActionListener(CBListener1);
        resobox.setSelectedIndex(1);
        obs.setSelectedIndex(2);
        editobs.setSelectedIndex(4);
        edithl.setSelectedIndex(0);

        obs.addActionListener(new ColorImp(0));
        editobs.addActionListener(new ColorImp(1));
        edithl.addActionListener(new ColorImp(2));

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        JPanel p5 = new JPanel();
        p1.add(resobox);
        p2.add(obs);
        p3.add(back2Menu);
        p4.add (editobs);
        p5.add(edithl);
        p1.setBorder(BorderFactory.createTitledBorder("Resolution"));
        p2.setBorder(BorderFactory.createTitledBorder("Game Obstacle Colour"));
        p4.setBorder(BorderFactory.createTitledBorder("Editor Obstacle Colour"));
        p5.setBorder(BorderFactory.createTitledBorder("Editor High-light Colour"));
        p3.setBorder(BorderFactory.createEmptyBorder(0,0 , 0 ,0));
        wrap.setBorder(BorderFactory.createEmptyBorder(0,0 , 0 ,0));
        wrap.add(p1);
        wrap.add(p2);
        wrap.add(p4);
        wrap.add(p5);
        SetPanel.add(wrap);
        SetPanel.add(p3);
        SetPanel.setBorder(BorderFactory.createEmptyBorder(10 , 10 , 5 ,10));
    }



    public void helpInit() throws IOException {
        helpInfo = ImageIO.read(new FileInputStream("Assets/images/help.jpg"));
        helpPanel = new JPanel(new BoxLayout(setPanel,BoxLayout.PAGE_AXIS));
        Return = new JButton("Return to Menu");
        MainButtonListener BL = new MainButtonListener();
        Return.addActionListener(BL);
        JPanel display = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(helpInfo, 0, 0, null);
            }
        };
        display.add(Return);
        display.setPreferredSize(new Dimension(helpInfo.getWidth(), helpInfo.getHeight()));
        JScrollPane scroller = new JScrollPane(display);
        helpPanel.setLayout(new BorderLayout());
        helpPanel.add(scroller, BorderLayout.CENTER);
    }

    public void MenuAddButtons(){
        startButton = new JButton("Game Setup");
        helpButton = new JButton("Controls");
        editButton = new JButton("Edit Maps");
        exitButton = new JButton("Exit");
        setButton = new JButton("Options");
        MainButtonListener ListenForButton = new MainButtonListener();
        startButton.addActionListener(ListenForButton);
        helpButton.addActionListener(ListenForButton);
        editButton.addActionListener(ListenForButton);
        setButton.addActionListener(ListenForButton);
        exitButton.addActionListener(ListenForButton);
        JPanel p1 = new JPanel( new BorderLayout());
        p1.add(startButton);
        JPanel p2 = new JPanel( new BorderLayout());
        p2.add(editButton);
        JPanel p3 = new JPanel( new BorderLayout());
        p3.add(helpButton);
        JPanel p5 = new JPanel( new BorderLayout());
        p5.add(exitButton);
        JPanel p4 = new JPanel( new BorderLayout());
        p4.add(setButton);
        p1.setOpaque(false);
        p2.setOpaque(false);
        p3.setOpaque(false);
        p4.setOpaque(false);
        p5.setOpaque(false);
        p1.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        p2.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        p3.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        p4.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        p5.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        startButton.setAlignmentX(MenuInterface.CENTER_ALIGNMENT);
        helpButton.setAlignmentX(MenuInterface.CENTER_ALIGNMENT);
        editButton.setAlignmentX(MenuInterface.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(MenuInterface.CENTER_ALIGNMENT);
        startButton.setAlignmentX(MenuInterface.CENTER_ALIGNMENT);
        MenuInterface.add(p1);
        MenuInterface.add(p2);
        MenuInterface.add(p3);
        MenuInterface.add(p4);
        MenuInterface.add(p5);
    }

    public void Init() throws IOException {
        bg = ImageIO.read(new FileInputStream("Assets/images/menu.jpg"));
        MenuInterface = new JPanel(){public void paintComponent( Graphics g ) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.drawImage(bg,0,0,null);
        }};
        MenuInterface.setLayout(new BoxLayout(MenuInterface,BoxLayout.PAGE_AXIS));
        MenuInterface.setBorder(BorderFactory.createEmptyBorder(100, 10, 20, 10));
        //status = new JLabel("Awaiting Commands");
        //MenuInterface.add(status);
        MenuAddButtons();
    }

    public void ChooseMap() throws FileNotFoundException {
        JButton open = new JButton();
        JFileChooser jf = new JFileChooser();
        jf.setCurrentDirectory(new java.io.File(model.currentDirectory + "\\Assets\\Maps"));
        jf.setDialogTitle("Choose Map");
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(jf.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
            model.mapPath = jf.getSelectedFile().getAbsolutePath();
            model.SetMapPath();
            LaunchButton.setEnabled(true);
            mapLoadStatus.setText("Current map: " + model.mapPath);
        }
    }


    public void ScrollRate(){
        OS = new JLabel("Obstacle Speed: "+model.ObsSpeed);
        PS = new JLabel("Player Speed: "+model.PlayerSpeed);
        JSlider OSslider = new JSlider(JSlider.HORIZONTAL,1,25,5);
        OSslider.addChangeListener(new OSListener());
        OSslider.setMajorTickSpacing(4);
        OSslider.setMinorTickSpacing(1);
        OSslider.setPaintTicks(true);
        OSslider.setPaintLabels(true);
        JSlider PSslider = new JSlider(JSlider.HORIZONTAL,1,25,5);
        PSslider.addChangeListener(new PSListener());
        PSslider.setMajorTickSpacing(4);
        PSslider.setMinorTickSpacing(1);
        PSslider.setPaintTicks(true);
        PSslider.setPaintLabels(true);
        JPanel Internal = new JPanel();
        Internal.setLayout(new GridLayout(0, 2,-75,34));
        Internal.add(OS);
        Internal.add(OSslider);
        Internal.add(PS);
        Internal.add(PSslider);
        JPanel scroller = new JPanel();
        scroller.add(Internal);
        scroller.setBorder(BorderFactory.createTitledBorder("Player/Obstacle speed(unit per sec)"));
        JPanel outerPadding = new JPanel();
        outerPadding.add(scroller);
        outerPadding.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GameSetup.add(outerPadding, BorderLayout.LINE_END);
    }

    public class OSListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider read = (JSlider) e.getSource();
            OS.setText("Obstacle Speed: "+read.getValue());
            if (!read.getValueIsAdjusting()){
              model.ObsSpeed = (int) read.getValue();
            }
        }

    }

    public class PSListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider read = (JSlider) e.getSource();
            PS.setText("Player Speed: "+(int) read.getValue());
            if (!read.getValueIsAdjusting()){
                model.PlayerSpeed = (int) read.getValue();
            }
        }
    }

    public void loadEditor() throws IOException {
        JButton open = new JButton();
        JFileChooser jf = new JFileChooser();
        jf.setCurrentDirectory(new java.io.File(model.currentDirectory + "\\Assets\\Maps"));
        jf.setDialogTitle("Choose Map");
        jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if(jf.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
            String path = jf.getSelectedFile().getAbsolutePath();
            model.curEdit = new Editor(path);
            edit = new EditorView(model.curEdit);
            model.upEditCol1();
            model.upEditCol2();
        }
    }

    public void FPSSetting(){
        FPSListener BListener = new FPSListener();
        JPanel fps = new JPanel();
        ButtonGroup bg = new ButtonGroup();
        JPanel outerPadding = new JPanel();
        JRadioButton fps30 = new JRadioButton("30 FPS");
        JRadioButton fps45 = new JRadioButton("45 FPS");
        JRadioButton fps60 = new JRadioButton("60 FPS");
        fps60.setSelected(true);
        JRadioButton fps90 = new JRadioButton("90 FPS");
        JRadioButton fps120 = new JRadioButton("120 FPS");
        fps30.addActionListener(BListener);
        fps45.addActionListener(BListener);
        fps60.addActionListener(BListener);
        fps90.addActionListener(BListener);
        fps120.addActionListener(BListener);
        JPanel innerPad = new JPanel();
        innerPad.add(fps30);
        innerPad.add(fps45);
        innerPad.add(fps60);
        innerPad.add(fps90);
        innerPad.add(fps120);
        innerPad.setLayout(new BoxLayout(innerPad, BoxLayout.PAGE_AXIS));
        innerPad.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        fps.add(innerPad);
        fps.setBorder(BorderFactory.createTitledBorder("FPS"));
        bg.add(fps30);
        bg.add(fps45);
        bg.add(fps60);
        bg.add(fps90);
        bg.add(fps120);
        outerPadding.add(fps);
        outerPadding.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        GameSetup.add(outerPadding,BorderLayout.LINE_START);
    }

    public void GameSetupInit(){
        MainButtonListener Listener = new MainButtonListener();
        mapLoadStatus = new JLabel();
        CMap = new JButton("Select Map");
        LaunchButton = new JButton("Launch Game");
        BackToMenuFromSetup = new JButton("Back to Menu");
        CMap.addActionListener(Listener);
        LaunchButton.addActionListener(Listener);
        BackToMenuFromSetup.addActionListener(Listener);
        if (model.mapPath == null){
            LaunchButton.setEnabled(false);
            mapLoadStatus.setText("No map file loaded, please choose a map.");
        }
        GameSetup = new JPanel();
        JPanel Buttons = new JPanel();
        Buttons.setLayout(new BoxLayout(Buttons,BoxLayout.LINE_AXIS));
        GameSetup.setLayout(new BorderLayout());
        JPanel p1 = new JPanel( new BorderLayout());
        p1.add(CMap);
        JPanel p2 = new JPanel( new BorderLayout());
        p2.add(LaunchButton);
        JPanel p3 = new JPanel( new BorderLayout());
        p3.add(BackToMenuFromSetup);
        JPanel p4 = new JPanel( new BorderLayout());
        p4.add(mapLoadStatus);
        p4.setAlignmentX(MenuInterface.CENTER_ALIGNMENT);
        p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        p2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p3.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        p4.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        mapLoadStatus.setAlignmentX(p4.CENTER_ALIGNMENT);
        Buttons.add(p1);
        Buttons.add(p2);
        Buttons.add(p3);
        Buttons.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        GameSetup.add(p4,BorderLayout.PAGE_START);
        GameSetup.add(Buttons,BorderLayout.PAGE_END);
        FPSSetting();
        ScrollRate();

    } //Game Setup

    public void GameInit()throws IOException{
        model.SetMapPath();
        model.MainGame = new Game();
        model.updateRes();
        model.ImportLevels();
        model.UpdateFPS();
        model.MainGame.convertObsCords();
        GameView = new GamePanel();
        Card.add(GameView,GAME_SCREEN);
    } //Game Initialization

    public void WinInit(boolean win){
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        WinScreen = new JPanel();
        WinScreen.setLayout(new BoxLayout(WinScreen,BoxLayout.PAGE_AXIS));
        JLabel status = new JLabel();
        Retry = new JButton("Play Again");
        BackToMenu = new JButton("Return to Menu");
        if (win == true){
            status.setText("You won! Final Score: " + model.MainGame.score);
        }else {
            status.setText("You lost! Final Score: " + model.MainGame.score);
        }
        p1.add(Retry);
        p2.add(BackToMenu);
        p3.add(status);
        p1.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        p2.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        p3.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        WinScreen.add(p3);
        JPanel Grid = new JPanel(new GridLayout(0,2,-4,0));
        Grid.add(p1);
        Grid.add(p2);
        WinScreen.add(Grid);
        MainButtonListener ListenForButton = new MainButtonListener();
        Retry.addActionListener(ListenForButton);
        BackToMenu.addActionListener(ListenForButton);
        Card.add(WinScreen,Win_Screen);
    }

    public void ToMenu(){
        this.setTitle("Contrails");
        this.setSize(new Dimension(300,400));
        CardLayout cl = (CardLayout) Card.getLayout();
        cl.show(Card, MAIN_MENU);
        this.centerWindow();
    }

    public void centerWindow(){
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }


    public void MenuToGame(){
        this.setTitle("Game Setup Screen");
        //this.setMinimumSize(new Dimension(500, 300));
        this.setSize(500, 300);
        CardLayout cl = (CardLayout) Card.getLayout();
        cl.show(Card, SETUP_MENU);
        this.centerWindow();
    }  // Menu to Game Transfer Method

    public void SetupToGame(){
        this.setTitle("Contrails - Currently Playing");
        System.out.println(model.WinWidth()+ " " +model.WinHeight());
        GameView.setPreferredSize(new Dimension(model.WinWidth(),model.WinHeight()));
        helpPanel.setPreferredSize(new Dimension(0,0));
        this.setResizable(false);
        CardLayout cl = (CardLayout) Card.getLayout();
        cl.show(Card, "Game");
        this.pack();
        centerWindow();
        //GameView.start();
    } //Setup to Game Transfer Method

    public void MenuToHelp(){
        this.centerWindow();
        this.setTitle("Contrails - Controls");
        if (GameView != null) {
            GameView.setPreferredSize(new Dimension(0, 0));
        }
        helpPanel.setPreferredSize(new Dimension(519,600));
        CardLayout cl = (CardLayout) Card.getLayout();
        cl.show(Card, HELP_PAGE);
        this.pack();
        centerWindow();
    }

    public void MenuToSet(){
        this.setTitle("Contrails - Options");
        this.setSize(350, 200);
        CardLayout cl = (CardLayout) Card.getLayout();
        cl.show(Card, SETTING_PAGE);
        centerWindow();
    }




    public MainView(Model model) throws IOException {
        // Set up the window.
        this.model = model;
        model.addObserver(this);
        this.setTitle("Contrails");
        //this.setMinimumSize(new Dimension(300, 450));
        this.setSize(300, 450);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        centerWindow();

        Card = new JPanel(new CardLayout());
        Init();
        helpInit();
        GameSetupInit();
        SetInit();
        //GameInit();

        Card.add(MenuInterface, MAIN_MENU);
        Card.add(GameSetup, SETUP_MENU);
        Card.add(helpPanel, HELP_PAGE);
        Card.add(SetPanel, SETTING_PAGE);
        this.add(Card);
        setVisible(true);
    }

    private class FPSListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JRadioButton button = (JRadioButton) e.getSource();
            String SFPS = button.getText();
            String parts [] = SFPS.split(" ");
            int thisFPS = Integer.parseInt(parts[0]);
            //System.out.println(thisFPS);
            model.FPS = thisFPS;
        }
    }

    private class MainButtonListener implements ActionListener  {
        public void actionPerformed(ActionEvent e){
            if (e.getSource() == startButton){
                model.updatePage("Game Setup");
                MenuToGame();
            } else if (e.getSource() == exitButton){
                System.exit(0);
            } else if (e.getSource() == helpButton){
                MenuToHelp();
            } else if (e.getSource() == editButton){
                model.updatePage("Editor");
                try {
                    loadEditor();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if (e.getSource() == Retry){
                try {
                    model.RestartSameLevel();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                SetupToGame();
                GameView.reset();
                GameView.start();
            } else if (e.getSource() == BackToMenu || e.getSource() == BackToMenuFromSetup || e.getSource() == Return || e.getSource() == back2Menu){
                model.updatePage("Main Menu");
                ToMenu();
            } else if (e.getSource() == CMap){
                try {
                    ChooseMap();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            } else if (e.getSource() == LaunchButton){
                model.updateRes();
                try {
                    GameInit();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                SetupToGame();
                GameView.start();
            } else if (e.getSource() == setButton){
                MenuToSet();
            }
        }
    }


    public void DisplayWin(boolean win){
        if (win == true){
            this.setTitle("You won!!!");
        } else {
            this.setTitle("You lost :(");
        }
        GameView.setPreferredSize(new Dimension(0,0));
        SetPanel.setPreferredSize(new Dimension(0,0));
        CardLayout cl = (CardLayout) Card.getLayout();
        cl.show(Card, Win_Screen);
        this.setSize(new Dimension(300,150));
        centerWindow();
    }



    public class GamePanel extends JPanel implements KeyListener{
        int fps = model.MainGame.FPS;
        int ups = model.MainGame.FPS;
        int length = model.MainGame.PlayerSize;
        javax.swing.Timer DisplayTimer;
        javax.swing.Timer UpdateTimer;
        private BufferedImage playerIcon;
        private BufferedImage BG;

        public int x = model.MainGame.PlayerPosX  , velX = 0;
        public int y = model.MainGame.PlayerPosY , velY = 0;
        public int obsvelX;
        public int PlayerVel;

        public GamePanel () throws IOException{
            this.fps = model.MainGame.FPS;
            //System.out.println(this.fps);
            playerIcon = ImageIO.read(new FileInputStream("Assets/Planes/F117.png"));
            BG = ImageIO.read(new FileInputStream("Assets/images/cloud.jpg"));
            //this.setPreferredSize(new Dimension(model.MainGame.win_l,model.MainGame.win_h));
            this.DisplayTimer = new Timer(1000/fps, (ActionEvent ae) -> {
                repaint();
            });
            this.UpdateTimer = new Timer(1000/ups, (ActionEvent ae) -> {
                for(Game.Obstacles o : model.MainGame.Obs){
                    o.updatePos(obsvelX);
                }
                model.MainGame.deleteObs();
                model.MainGame.updatePlayerCords(velX,velY);
                model.MainGame.colTest();
                model.GameCond();
            });
        }
        public void reset(){
            this.fps = model.MainGame.FPS;
            velX = 0;
            velY = 0;
        }
        public void start(){
            this.fps = model.MainGame.FPS;
            model.MainGame.state = "Game Started";
            this.addKeyListener(GameView);
            this.setFocusable(true);
            this.setFocusTraversalKeysEnabled(false);
            this.requestFocus();
            obsvelX =  (int)-Math.ceil(((double)model.MainGame.ObsSpeed*model.MainGame.PlayerSize)/(double)fps)  ;
            PlayerVel = (int)Math.ceil(((double)model.MainGame.PlayerSpeed*model.MainGame.PlayerSize)/fps);
            //System.out.println("Obs Speed: " + obsvelX + ", Player Speed " + PlayerVel);
            //System.out.println(model.MainGame.PlayerSize + " " + fps + " "+model.MainGame.PlayerSpeed + " " + model.MainGame.ObsSpeed);
            this.DisplayTimer.start();
            this.UpdateTimer.start();
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);

            Graphics2D g2d= (Graphics2D) g;
            //g2d.setColor(Color.white);
            //g2d.fillRect(0,0,model.GWidth,model.GHeight);
            g2d.drawImage(BG,0,0,model.MainGame.win_l*(17/16),model.MainGame.win_h,null);
            for(Game.Obstacles o : model.MainGame.Obs){
                g2d.setColor(model.ObsCol);
                g2d.fillRect(o.top_leftx,o.top_lefty,o.bottom_rightx,o.bottom_righty);
            }
            g2d.drawImage(playerIcon,model.MainGame.PlayerPosX,model.MainGame.PlayerPosY,length,length,null);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Verdana",Font.PLAIN,30));
            g2d.drawString("Current Score: "+model.MainGame.score,model.WinWidth()/2-model.MainGame.PlayerSize*2,(model.WinHeight()/10)*9);
            //g2d.drawString(model.MainGame.statusUpdate(),480,270);

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W){
                //System.out.println("W Pressed");
                //velX = 0;
                velY = -PlayerVel;
            } else if (key == KeyEvent.VK_A){
                //System.out.println("A Pressed");
                velX = -PlayerVel*2;
            } else if (key == KeyEvent.VK_S){
                //System.out.println("S Pressed");
                //velX = 0;
                velY = PlayerVel;
            } else if (key == KeyEvent.VK_D){
                //System.out.println("D Pressed");
                velX = PlayerVel;
            }
        }
        public void keyTyped(KeyEvent e) {
        }
        public void keyReleased(KeyEvent e){
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_W){
                //System.out.println("W Release");
                velY = 0;
                //velX = -5;
            } else if (key == KeyEvent.VK_A){
                //System.out.println("A Release");
                velX = obsvelX;
            } else if (key == KeyEvent.VK_S){
                //System.out.println("S Release");
               // velX = -5;
                velY = 0;
            } else if (key == KeyEvent.VK_D){
                //System.out.println("D Release");
                velX = obsvelX;
                //velY = 0;
            }

        }
    }  /// <================================== GAME DISPLAY

    /**
     * Update with data from the model.
     */
    public void update(Object observable, String state) {
        if(state == "GameOver" || state == "Exited"){
            System.out.println("Ended");
            Timer x = GameView.DisplayTimer;
            Timer y = GameView.UpdateTimer;
            x.stop();
            y.stop();
            if (state == "Exited" || state == "GameOver"){
                if (state == "Exited"){
                    WinInit(true);
                    DisplayWin(true);
                }else {
                    WinInit(false);
                    DisplayWin(false);
                }
            }
        } else if (state == "Win"){
            GameView.setFocusable(false);
            GameView.velX = GameView.PlayerVel*2;
            GameView.velY = 0;

        }
        //System.out.println("Model changed!");
    }



}

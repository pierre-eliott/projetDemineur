package Demineur;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.* ;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.awt.Component;
import java.net.Socket;
import java.io.IOException;

public class GUI extends JPanel implements ActionListener{

    private Main main;
    private Matrix matrix;

    private int nbFlags; 

    private Client client;

    Timer timer;
    int elapsedTime = 0;

    private boolean isOnline = false; 
    
    private JButton butQuit;
    //butQuit.setBackground(Color.RED);

    private JButton butNewParty;

    java.awt.Color backgroundColorCaseSelected = new java.awt.Color(192, 192, 192);

    private final static int DIMEASY = 9;
    private final static int DIMMEDIUM = 16;
    private final static int DIMHARD = 25;
    private final static int DIMCUSTOMMIN = 2;
    private final static int DIMCUSTOMMAX = 40;

    private final static int NBMINESEASY = 12;
    private final static int NBMINESMEDIUM = 40;
    private final static int NBMINESHARD = 100;
    private final static int NBMINESCUSTOMMIN = 2;
    private final static int NBMINESCUSTOMMAX = 150;

    private int remainingSquares;

    private JTextField ipAdress = new JTextField("localhost");
    private JTextField port = new JTextField("10000");
    private JTextField playerName = new JTextField("Player Name");

    java.awt.Color backgroundColorStartButton = new java.awt.Color(125, 125, 125);
    java.awt.Color backgroundColorQuitButton = new java.awt.Color(215,215,215);
    java.awt.Color backgroundColorCaseMine = new java.awt.Color(239, 154, 154);

    ImageIcon imageIcon = new ImageIcon(getClass().getResource("img/drapeau.png"));
    Image image = imageIcon.getImage();
    Image imageIconResize = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    ImageIcon imageIconResizeIcon = new ImageIcon(imageIconResize);
    JLabel flagLabel = new JLabel(imageIconResizeIcon);

    private JPanel panel = new JPanel();
    JPanel panelDifficultyMenu = new JPanel();

    private JPanel playersPanel = new JPanel(new BorderLayout());
    JPanel leftPanel = new JPanel(new BorderLayout());
    private JPanel panelButtons = new JPanel();

    JMenuItem mEasy = new JMenuItem("Easy", KeyEvent.VK_Q);
    JMenuItem mMedium = new JMenuItem("Medium", KeyEvent.VK_Q);
    JMenuItem mHard = new JMenuItem("Hard", KeyEvent.VK_Q);
    JMenuItem mCustom = new JMenuItem("Custom", KeyEvent.VK_Q);
    JMenuItem mConnection = new JMenuItem("Connection", KeyEvent.VK_Q);

    JButton connectButton = new JButton("Connect");

    JLabel timerLabel = new JLabel();

    private JLabel difficultyLabel = new JLabel();

    JLabel MinesLabel = new JLabel();

    GUI(){
        this(new Matrix(), new Main());
    }

    GUI(Main main){
        this(new Matrix(), main);
    }

    GUI(Matrix matrix,Main main){

        timer = new Timer(1000, this);
        timer.setInitialDelay(0);
        timer.start();

        nbFlags = 0;
        remainingSquares = matrix.getDim() * matrix.getDim() - matrix.getNbMines();

        this.main = main ;
        this.matrix = matrix;
                
        ImageIcon imageIconLogo = new ImageIcon(getClass().getResource("img/logo.png"));
        Image imageLogo = imageIconLogo.getImage();
        Image imageIconLogoResize = imageLogo.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        ImageIcon imageIconResizeLogoIcon = new ImageIcon(imageIconLogoResize);
        JLabel logoLabel = new JLabel(imageIconResizeLogoIcon);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(logoLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        
        timerLabel.setText(" 00:00:00  ");
        timerLabel.setFont(new Font("Dialog", Font.BOLD, 30));
        timerLabel.setForeground(Color.GRAY);
        topPanel.add(timerLabel, BorderLayout.EAST);

        difficultyLabel.setText("   Medium");
        difficultyLabel.setFont(new Font("Dialog", Font.BOLD, 30));
        difficultyLabel.setForeground(Color.ORANGE);
        topPanel.add(difficultyLabel,(BorderLayout.WEST));

        ImageIcon imageIconBombe = new ImageIcon(getClass().getResource("img/bombe.png"));
        Image imageBombe = imageIconBombe.getImage();
        Image imageIconBombeResize = imageBombe.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon imageIconResizeBombeIcon = new ImageIcon(imageIconBombeResize);
        JLabel bombeLabel = new JLabel(imageIconResizeBombeIcon);

        MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");
        MinesLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        MinesLabel.setForeground(Color.BLACK);

        rightPanel.add(bombeLabel,(BorderLayout.WEST));
        rightPanel.add(MinesLabel,(BorderLayout.CENTER));
        rightPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        butQuit = new JButton("Quit");
        butQuit.setBackground(backgroundColorQuitButton);
        butQuit.setFont(new Font("Dialog", Font.PLAIN, 25));

        butQuit.addActionListener(this);

        butNewParty = new JButton("New Party");
        butNewParty.setBackground(backgroundColorStartButton);
        butNewParty.setFont(new Font("Dialog", Font.PLAIN, 25));
        butNewParty.setForeground(Color.WHITE);

        butNewParty.addActionListener(this);
        
        
        panelButtons.add(butQuit, BorderLayout.EAST);
        panelButtons.add(butNewParty, BorderLayout.WEST);

        panel = createPanel();
   
        leftPanel.add(playersPanel, BorderLayout.CENTER);

        JMenuBar difficultyMenu = new JMenuBar();
        difficultyMenu = createMenu();

        setLayout(new BorderLayout());

        add(panel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(panelButtons,BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);
        add(leftPanel, BorderLayout.WEST);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==butQuit) {
            if(isOnline)
            {
                client.leaveSession();
            }
            else
            {
                main.quit();
            }
        }
        if(e.getSource()==butNewParty)
        {
            startTimer();
            matrix.fillRandomly();
            matrix.display();
            remove(panel);

            nbFlags = 0;
            MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");

            panel = createPanel();
            add(panel, BorderLayout.CENTER);
            main.pack();
        }
        if(e.getSource()==mEasy)
        {
            startTimer();
            matrix.setDim(DIMEASY);
            matrix.setMines(NBMINESEASY);
            matrix.fillRandomly();
            remove(panel);

            nbFlags = 0;
            MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");
            updateRemainingSquares();

            difficultyLabel.setText("   Easy    ");
            difficultyLabel.setForeground(new java.awt.Color(61, 129, 58 )); 

            panel = createPanel();
            add(panel, BorderLayout.CENTER);
            main.pack();
            
        }
        if(e.getSource()==mMedium)
        {
            startTimer();
            matrix.setDim(DIMMEDIUM);
            matrix.setMines(NBMINESMEDIUM);
            matrix.fillRandomly();
            remove(panel);
            
            nbFlags = 0;
            MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");

            updateRemainingSquares();

            difficultyLabel.setText("   Medium    ");
            difficultyLabel.setForeground(Color.ORANGE);

            panel = createPanel();
            add(panel, BorderLayout.CENTER);
            main.pack();
        }
        if(e.getSource()==mHard)
        {
            startTimer();
            matrix.setDim(DIMHARD);
            matrix.setMines(NBMINESHARD);
            matrix.fillRandomly();
            remove(panel);
            
            nbFlags = 0;
            MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");

            updateRemainingSquares();

            difficultyLabel.setText("   Hard    ");
            difficultyLabel.setForeground(Color.RED);

            panel = createPanel();
            add(panel, BorderLayout.CENTER);
            main.pack();
        }
        if(e.getSource()==mCustom)
        {
            //String choiceSize =  JOptionPane.showInputDialog(null, "What size would you like? " );
            //String choiceNbMines = JOptionPane.showInputDialog(null, "How many bombs do you want? " );

            JSlider sliderBar1 = new JSlider(DIMCUSTOMMIN,DIMCUSTOMMAX);
            JSlider sliderBar2 = new JSlider(NBMINESCUSTOMMIN,NBMINESCUSTOMMAX);
            JLabel valueLabel1 = new JLabel("Size: " + sliderBar1.getValue());
            JLabel valueLabel2 = new JLabel("Number of bombs: " + sliderBar2.getValue());

            sliderBar1.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    valueLabel1.setText("Size: " + sliderBar1.getValue());
                    
                }
            });

            sliderBar2.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    valueLabel2.setText("Number of bombs: " + sliderBar2.getValue());
                }
            });

            int reponse = JOptionPane.showOptionDialog(
                    null,
                    new Object[]{valueLabel1, sliderBar1, valueLabel2, sliderBar2},
                    "Customization ",
                    JOptionPane.OK_CANCEL_OPTION, 
                                                  
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null 
            );

            if (reponse == JOptionPane.OK_OPTION) {

                matrix.setDim(sliderBar1.getValue());
                matrix.setMines(sliderBar2.getValue());

                startTimer();

                matrix.fillRandomly();
                remove(panel);
                
                nbFlags = 0;
                MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");

                updateRemainingSquares();

                difficultyLabel.setText("   Custom    ");
                difficultyLabel.setForeground(Color.BLUE);

                panel = createPanel();
                add(panel, BorderLayout.CENTER);
                main.pack();

            }

        }
        if(e.getSource()==timer)
        {
            elapsedTime++;

            int hours = elapsedTime / 3600;
            int minutes = (elapsedTime % 3600) / 60;
            int seconds = elapsedTime % 60;

            String timeString = String.format(" %02d:%02d:%02d      ", hours, minutes, seconds);
            Font vintageFont = new Font("Monospaced", Font.BOLD, 30); // Vous pouvez ajuster la taille de la police ici

            timerLabel.setFont(vintageFont);
            timerLabel.setText(timeString);
        }
        if(e.getSource()==connectButton)
        {
            isOnline = true; 
            remove(panel);

            difficultyLabel.setText("   Online    ");
            difficultyLabel.setForeground(Color.BLUE);

            panelButtons.remove(butNewParty);

            System.out.println("Connexion...");
            
            client = new Client(this);
            client.JoinServer(ipAdress.getText(), Integer.parseInt(port.getText().toString()), playerName.getText().toString());

            /*panel = createPanel();
            add(panel, BorderLayout.CENTER);
            main.pack();*/
        }
    }

    public void startTimer() {
        timer.stop(); // Arrêtez le chronomètre s'il fonctionne déjà
        elapsedTime = 0; // Réinitialisez le temps écoulé
        timer.start(); // Démarrez le chronomètre
    }

    public void stopTimer()
    {
        timer.stop();
    }

    public JPanel createPanel()
    {
        panel = new JPanel(new GridLayout(matrix.getDim(), matrix.getDim()));
        panel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(224, 224, 224), 4)); 
        for(int i=0; i<matrix.getDim(); i++)
        {
            for(int j=0; j<matrix.getDim(); j++)
            {
                Case c = new Case(client, this, i, j);
                panel.add(c);
            }
        }
        return panel;
    }

    public JMenuBar createMenu(){
        JMenuBar menuDifficultyBar = new JMenuBar();
    
        JMenu menuDifficulty = new JMenu("Difficulty");

        //menuDifficulty.setPreferredSize(new Dimension(100, 50));
        menuDifficulty.setFont(new Font("Dialog", Font.BOLD, 15));
        menuDifficulty.setForeground(new java.awt.Color(125,125,125));

        menuDifficultyBar.add(menuDifficulty);
        
        menuDifficulty.add(mEasy) ;
        mEasy.addActionListener(this);

        menuDifficulty.add(mMedium) ;
        mMedium.addActionListener(this);

        menuDifficulty.add(mHard) ;
        mHard.addActionListener(this);
        
        menuDifficulty.add(mCustom) ;
        mCustom.addActionListener(this);

        menuDifficultyBar.setBackground(new java.awt.Color(245, 245, 245));
        //menuDifficulty.setForeground(Color.BLACK);

        JMenu menuConnection = new JMenu("Join a game");
        menuConnection.setFont(new Font("Dialog", Font.BOLD, 15));
        menuConnection.setForeground(new java.awt.Color(125,125,125));

        menuConnection.add(ipAdress);
        menuConnection.add(port);
        menuConnection.add(playerName);

        menuConnection.add(connectButton);
        connectButton.addActionListener(this);
        
        menuDifficultyBar.add(menuConnection);
        mConnection.addActionListener(this);
        
        menuDifficultyBar.setBackground(new java.awt.Color(245, 245, 245));

        main.setJMenuBar(menuDifficultyBar);
        return menuDifficultyBar;
    }

    public boolean getIsOnline(){
        return isOnline;
    }

    public void setIsOnline(){
        isOnline= ! isOnline;
    }

    public boolean isMine(int x, int y)
    {
       return matrix.getCases(x, y);
    }

    public int getMines(int x, int y)
    {
        return matrix.computeMinesNumber(x, y);
    }

    public int getDim()
    {
        return matrix.getDim();
    }

    public void partyFinished()
    {
        matrix.fillRandomly();
        matrix.display();
        remove(panel);

        panel = createPanel();
        add(panel, BorderLayout.CENTER);
        main.pack();
        startTimer();
        nbFlags = 0;

        updateRemainingSquares();
        MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");
    }

    public void setNbFlags()
    {
        nbFlags++;
        MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");
    }

    public void setNbFlagsMinus()
    {
        nbFlags--;
        MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");
    }

    public int getNbFlags()
    {
        return nbFlags;
    }

    public int getNbMines()
    {
        return matrix.getNbMines();
    }

    public void setDim(int n)
    {
        matrix.setDim(n);
    }

    public void setNbMines(int n)
    {
        matrix.setMines(n);
    }

    public void setRemainingSquares()
    {
        remainingSquares--;
    }

    public int getRemainingSquares()
    {
        return remainingSquares;
    }

    public void updateRemainingSquares()
    {
        remainingSquares = matrix.getDim() * matrix.getDim() - matrix.getNbMines();
    }

    public void setMatrixPanel()
    {
        remove(panel);
        panel = createPanel();
        add(panel, BorderLayout.CENTER);
        main.pack();
    } 

    public String getTimer()
    {
        timer.stop();
        int hours = elapsedTime / 3600;
        int minutes = (elapsedTime % 3600) / 60;
        int seconds = elapsedTime % 60;

        String timeString = String.format("%02d:%02d:%02d ", hours, minutes, seconds);

        return timeString;
    }

    public Boolean isValidateCoordinate(int x, int y)
    {
        if(x<=matrix.getDim()-1 && x>=0 && y<=matrix.getDim()-1 && y>=0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void newPlayer(String name){
        //System.out.println(name);

        JLabel newPlayerLabel = new JLabel();
        newPlayerLabel.setText(name+": ");
        newPlayerLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        newPlayerLabel.setForeground(Color.BLACK);

        ImageIcon imageIcon = new ImageIcon(getClass().getResource("img/vert.jpg"));
        Image image = imageIcon.getImage();
        Image imageIconResize = image.getScaledInstance(70, 20, Image.SCALE_SMOOTH);
        ImageIcon imageIconResizeIcon = new ImageIcon(imageIconResize);
        JLabel ledLabel = new JLabel(imageIconResizeIcon);

        JLabel scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        scoreLabel.setForeground(Color.BLACK);

        JPanel playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        playerInfoPanel.add(ledLabel);
        playerInfoPanel.add(newPlayerLabel);
        playerInfoPanel.add(scoreLabel);
        
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        playersPanel.add(playerInfoPanel);
        
        main.pack();
    }

    public void updateCell(int x, int y, int data) {

        Case c = this.getCaseAt(x, y);
        c.setTxt(data);
        c.setBackground(backgroundColorCaseSelected);
        
        if (data == 1) {
            c.setForeground(Color.BLUE);
        } else if (data == 2) {
            c.setForeground(new java.awt.Color(0, 130, 0));
        } else if (data == 3) {
            c.setForeground(Color.RED);
        } else if (data == 4) {
            c.setForeground(Color.BLACK);
        }

        c.setClicked();

        c.repaint();
    }

    public void updateCellMine(int x, int y) {
        Case c = this.getCaseAt(x, y);
        c.setTxt("X"); // chgt du texte à redessiner
        c.setBackground(backgroundColorCaseMine);
        c.repaint();
    }

    public void initNbFlags(int nbMines)
    {
        MinesLabel.setText(0 + "/" +nbMines+"   ");
    }

    public void updateCellFlag(boolean isFlaged, int x, int y, int nbFlagsRemaining, int nbMines)
    {
        Case c = this.getCaseAt(x, y);
        if(isFlaged)
        {
            c.setLayout(new BorderLayout());
            c.add(flagLabel, BorderLayout.CENTER);
            MinesLabel.setText(nbFlagsRemaining + "/" +nbMines+"   ");

            revalidate();
        }
        else{
            c.remove(flagLabel);
            MinesLabel.setText(nbFlagsRemaining + "/" +nbMines+"   ");
            revalidate();
        }
    }

    // A MODIFIER
    public void updateScore(ArrayList<Integer> scores) {
        Component[] components = playersPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JPanel) {
                JPanel playerInfoPanel = (JPanel) components[i];
                Component[] subComponents = playerInfoPanel.getComponents();
                if (subComponents.length >= 2 && subComponents[2] instanceof JLabel) {
                    JLabel scoreLabel = (JLabel) subComponents[2];
                    if (i < scores.size()) {
                        scoreLabel.setText(String.valueOf(scores.get(i)));
                    }
                }
            }
        }
    }

    public void updatePlayersListDisplayed(int id) {
        Component[] components = playersPanel.getComponents();

        for (int i = 0; i < components.length; i++) {

            if (components[i] instanceof JPanel) {
                JPanel playerInfoPanel = (JPanel) components[i];

                if (i == id) {
                    Component[] subComponents = playerInfoPanel.getComponents();

                    JLabel ledLabel = (JLabel) subComponents[0];

                    ImageIcon imageIcon = new ImageIcon(getClass().getResource("img/rouge.jpg"));
                    Image image = imageIcon.getImage();
                    Image imageIconResize = image.getScaledInstance(70, 20, Image.SCALE_SMOOTH);
                    ImageIcon imageIconResizeIcon = new ImageIcon(imageIconResize);
                    ledLabel.setIcon(imageIconResizeIcon);

                }
            }
        }
    }

    public Case getCaseAt(int x, int y)
    {
        Component[] components = panel.getComponents();
        
        for(Component component:  components)
        {
            if(component instanceof Case)
            {
                Case caseComponent = (Case) component;
                if (caseComponent.get_X()==x && caseComponent.get_Y()==y){
                    return caseComponent;
                }
            }
        }
        return null;
    }

    public void newPartyOffline()
    {
        setIsOnline();
        partyFinished();

        leftPanel.removeAll();

        difficultyLabel.setText("   Medium    ");
        difficultyLabel.setForeground(Color.ORANGE);

        panelButtons.add(butNewParty);
        
        main.pack();
    }

    public void endPartyOnline(ArrayList<String> rankList, boolean isLooser) {
        JPanel explosionPanel = new JPanel();

        ImageIcon imageIconExplosion = new ImageIcon(getClass().getResource("img/trophy.png"));
        Image imageExplosion = imageIconExplosion.getImage();
        Image imageIconExplosionResize = imageExplosion.getScaledInstance(150,125, Image.SCALE_SMOOTH);
        ImageIcon imageIconExplosionResizeIcon = new ImageIcon(imageIconExplosionResize);

        JLabel explosionLabel = new JLabel(imageIconExplosionResizeIcon);
        JLabel textLoseLabel = new JLabel();

        if(! isLooser)
        {
            textLoseLabel.setText("End of the game: " + getTimer());
            textLoseLabel.setFont(new Font("Dialog", Font.BOLD, 20));
            textLoseLabel.setForeground(new java.awt.Color(61, 129, 58));
        }
        else
        {
            textLoseLabel.setText("You lost the game: " + getTimer());
            textLoseLabel.setFont(new Font("Dialog", Font.BOLD, 20));
            textLoseLabel.setForeground(Color.RED);
        }

        JPanel rankPanel = new JPanel();
        rankPanel.setLayout(new BoxLayout(rankPanel, BoxLayout.Y_AXIS)); // Utilisation de BoxLayout en mode Y_AXIS

        for (int i = 0; i < rankList.size(); i++) {
            JLabel rankLabel = new JLabel();
            rankLabel.setText(rankList.get(i));
            rankLabel.setFont(new Font("Dialog", Font.BOLD, 15));
            if(i == 0)
            {
                rankLabel.setForeground(Color.ORANGE);
            }
            else if( i == 1)
            {
                rankLabel.setForeground(Color.GRAY);
            }
            else if( i == 2)
            {
                rankLabel.setForeground(new java.awt.Color(204,102,0));
            }
            else{
                rankLabel.setForeground(Color.BLACK);
            }

            rankPanel.add(rankLabel);
        }

        explosionPanel.setLayout(new BorderLayout());
        explosionPanel.add(explosionLabel, BorderLayout.CENTER);
        explosionPanel.add(textLoseLabel, BorderLayout.NORTH);
        explosionPanel.add(rankPanel, BorderLayout.SOUTH);

        int option = JOptionPane.showOptionDialog(this, explosionPanel, "FINISHED", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[] { "OK", "Leave" }, "OK");
        if (option == JOptionPane.OK_OPTION) {
            //setAllCaseFalse(matrix.getDim());
            remove(panel);
            panel = createPanel();
            add(panel, BorderLayout.CENTER);
            main.pack();
            startTimer();
            MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");
        }
        else if (option ==1)
        {
            client.leaveSession();
        }
    }

    public void setAllCaseFalse(int dim)
    {
        for(int i=0; i<dim; i++)
        {
            for(int j=0; j<dim; j++)
            {
                Case c = this.getCaseAt(i, j);
                c.setNotClicked();
                c.repaint();
            }
        }
    }
}   
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

/**
 * Classe GUI qui représente l'interface graphique du jeu de démineur.
 */

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
    private final static int DIMMEDIUM = 12;
    private final static int DIMHARD = 25;
    private final static int DIMCUSTOMMIN = 2;
    private final static int DIMCUSTOMMAX = 35;

    private final static int NBMINESEASY = 12;
    private final static int NBMINESMEDIUM = 25;
    private final static int NBMINESHARD = 75;
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

    /**
     * Constructeur par défaut de la classe GUI.
     */

    GUI(){
        this(new Matrix(), new Main());
    }

    /**
     * Constructeur avec un paramètre de la classe GUI.
     *
     * @param main Instance de la classe Main.
     */

    GUI(Main main){
        this(new Matrix(), main);
    }

    /**
     * Constructeur avec deux paramètres de la classe GUI.
     *
     * @param matrix Instance de la classe Matrix.
     * @param main   Instance de la classe Main.
     */

    GUI(Matrix matrix,Main main){

        timer = new Timer(1000, this);
        timer.setInitialDelay(0);
        timer.start();

        nbFlags = 0;
        remainingSquares = matrix.getDim() * matrix.getDim() - matrix.getNbMines();

        this.main = main ;
        this.matrix = matrix;

        matrix.display();
                
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
        butQuit.setBorder(BorderFactory.createRaisedBevelBorder());

        butQuit.addActionListener(this);

        butNewParty = new JButton("New Party");
        butNewParty.setBackground(backgroundColorStartButton);
        butNewParty.setFont(new Font("Dialog", Font.PLAIN, 25));
        butNewParty.setForeground(Color.WHITE);
        butNewParty.setBorder(BorderFactory.createRaisedBevelBorder());

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

    /**
     * Methode qui permet de gérer les actions des boutons lorsqu'on click dessus.
     *
     * @param e evenement lorsqu'on click sur un bouton
     */

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

                //System.out.println(matrix.getDim());
                //System.out.println(matrix.getNbMines());

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

    /**
     * Méthode qui permet d'arreter le timer, le réinitialiser et le démarer
     */

    public void startTimer() {
        timer.stop(); // Arrêtez le chronomètre s'il fonctionne déjà
        elapsedTime = 0; // Réinitialisez le temps écoulé
        timer.start(); // Démarrez le chronomètre
    }

    /**
     * Méthode qui permet d'arreter le timer
     */

    public void stopTimer()
    {
        timer.stop();
    }

    /**
     * Crée un panneau de jeu avec des cases disposées en grille.
     * Chaque case est représentée par un objet de la classe Case, associée à un
     * client et à l'instance actuelle de la classe.
     * 
     * @return Le panneau de jeu créé avec les cases disposées en grille.
     */

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

    /**
     * Crée et configure la barre de menu pour le jeu Minesweeper.
     * La barre de menu contient deux menus: "Difficulty" (Difficulté) et "Join a
     * game" (Rejoindre une partie).
     * Dans le menu "Difficulty", les options sont "Easy", "Medium", "Hard" et
     * "Custom", chacune déclenchant des actions associées.
     * Dans le menu "Join a game", les champs pour entrer l'adresse IP, le port et
     * le nom du joueur sont inclus, ainsi qu'un bouton de connexion.
     * 
     * @return La barre de menu configurée avec les options de difficulté et les
     *         fonctionnalités de connexion.
     */

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

    /**
     * Renvoie l'état en ligne du jeu. Si le jeu est en ligne, renvoie true ; sinon,
     * renvoie false.
     * 
     * @return true si le jeu est en ligne, sinon false.
     */

    public boolean getIsOnline(){
        return isOnline;
    }

    /**
     * Inverse l'état en ligne du jeu. Si le jeu est actuellement en ligne, il
     * devient hors ligne, et vice versa.
     */

    public void setIsOnline(){
        isOnline= ! isOnline;
    }

    /**
     * Vérifie si la case aux coordonnées spécifiées contient une mine.
     * 
     * @param x La coordonnée x de la case.
     * @param y La coordonnée y de la case.
     * @return true si la case contient une mine, sinon false.
     */

    public boolean isMine(int x, int y)
    {
       return matrix.getCases(x, y);
    }

    /**
     * Renvoie le nombre de mines adjacentes à la case spécifiée par les coordonnées
     * x et y.
     * 
     * @param x La coordonnée x de la case.
     * @param y La coordonnée y de la case.
     * @return Le nombre de mines adjacentes à la case spécifiée.
     */

    public int getMines(int x, int y)
    {
        return matrix.computeMinesNumber(x, y);
    }

    /**
     * Renvoie la dimension du plateau de jeu (nombre de lignes ou de colonnes).
     * 
     * @return La dimension du plateau de jeu.
     */

    public int getDim()
    {
        return matrix.getDim();
    }

    /**
     * Réinitialise et prépare le jeu pour une nouvelle partie. Remplit le plateau
     * de jeu avec de nouvelles mines,
     * réinitialise le chronomètre, réinitialise le nombre de drapeaux placés et met
     * à jour l'affichage des mines.
     */

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

    /**
     * Incrémente le nombre de drapeaux placés et met à jour l'affichage du nombre
     * de drapeaux.
     */

    public void setNbFlags()
    {
        nbFlags++;
        MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");
    }

    /**
     * Décrémente le nombre de drapeaux placés et met à jour l'affichage du nombre
     * de drapeaux.
     */

    public void setNbFlagsMinus()
    {
        nbFlags--;
        MinesLabel.setText(nbFlags + "/" +matrix.getNbMines()+"   ");
    }

    /**
     * Renvoie le nombre de drapeaux placés sur le plateau de jeu.
     * 
     * @return Le nombre de drapeaux placés.
     */

    public int getNbFlags()
    {
        return nbFlags;
    }

    /**
     * Renvoie le nombre total de mines sur le plateau de jeu.
     * 
     * @return Le nombre total de mines.
     */

    public int getNbMines()
    {
        return matrix.getNbMines();
    }

    /**
     * Modifie la dimension du plateau de jeu.
     * 
     * @param n La nouvelle dimension du plateau de jeu.
     */

    public void setDim(int n)
    {
        matrix.setDim(n);
    }

    /**
     * Modifie le nombre de mines sur le plateau de jeu.
     * 
     * @param n Le nouveau nombre de mines.
     */

    public void setNbMines(int n)
    {
        matrix.setMines(n);
    }

    /**
     * Décrémente le nombre de cases non découvertes sur le plateau de jeu.
     */

    public void setRemainingSquares()
    {
        remainingSquares--;
    }

    /**
     * Renvoie le nombre de cases non découvertes sur le plateau de jeu.
     * 
     * @return Le nombre de cases non découvertes.
     */

    public int getRemainingSquares()
    {
        return remainingSquares;
    }

    /**
     * Met à jour le nombre de cases non découvertes sur le plateau de jeu en
     * fonction de la dimension et du nombre de mines.
     */

    public void updateRemainingSquares()
    {
        remainingSquares = matrix.getDim() * matrix.getDim() - matrix.getNbMines();
    }

    /**
     * Met à jour le panneau du plateau de jeu en le recréant et en réinitialisant
     * les composants.
     */

    public void setMatrixPanel()
    {
        remove(panel);
        panel = createPanel();
        add(panel, BorderLayout.CENTER);
        main.pack();
    } 

    /**
     * Renvoie le temps écoulé sous forme de chaîne de caractères au format
     * HH:MM:SS.
     * 
     * @return Le temps écoulé au format HH:MM:SS.
     */

    public String getTimer()
    {
        timer.stop();
        int hours = elapsedTime / 3600;
        int minutes = (elapsedTime % 3600) / 60;
        int seconds = elapsedTime % 60;

        String timeString = String.format("%02d:%02d:%02d ", hours, minutes, seconds);

        return timeString;
    }

    /**
     * Vérifie si les coordonnées (x, y) sont valides sur le plateau de jeu.
     * 
     * @param x Coordonnée x.
     * @param y Coordonnée y.
     * @return Vrai si les coordonnées sont valides, sinon faux.
     */

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

    /**
     * Ajoute un nouveau joueur au panneau des joueurs avec le nom spécifié.
     * 
     * @param name Le nom du nouveau joueur.
     */

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

    /**
     * Met à jour le contenu d'une case à la position (x, y) avec la valeur
     * spécifiée.
     * 
     * @param x    Coordonnée x de la case.
     * @param y    Coordonnée y de la case.
     * @param data Nouvelle valeur de la case.
     */

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

    /**
     * Met à jour le contenu d'une case à la position (x, y) pour afficher une mine.
     * 
     * @param x Coordonnée x de la case.
     * @param y Coordonnée y de la case.
     */

    public void updateCellMine(int x, int y) {
        Case c = this.getCaseAt(x, y);
        c.setTxt("X"); // chgt du texte à redessiner
        c.setBackground(backgroundColorCaseMine);
        c.repaint();
    }

    /**
     * Initialise l'affichage du nombre de drapeaux placés à 0.
     * 
     * @param nbMines Le nombre total de mines sur le plateau de jeu.
     */

    public void initNbFlags(int nbMines)
    {
        MinesLabel.setText(0 + "/" +nbMines+"   ");
    }

    /**
     * Met à jour l'affichage du nombre de drapeaux placés et le nombre de drapeaux
     * restants sur le plateau de jeu.
     * 
     * @param isFlaged         Indique si la case est marquée d'un drapeau.
     * @param x                Coordonnée x de la case.
     * @param y                Coordonnée y de la case.
     * @param nbFlagsRemaining Le nombre de drapeaux restants sur le plateau de jeu.
     * @param nbMines          Le nombre total de mines sur le plateau de jeu.
     */

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

    /**
     * Met à jour les scores des joueurs dans le panneau des joueurs.
     * 
     * @param scores Liste des scores des joueurs.
     */

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

    /**
     * Met à jour l'icône d'état d'un joueur spécifique dans le panneau des joueurs.
     * 
     * @param id Identifiant du joueur.
     */

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

    /**
     * Renvoie la case située aux coordonnées (x, y) sur le plateau de jeu.
     * 
     * @param x Coordonnée x de la case.
     * @param y Coordonnée y de la case.
     * @return La case aux coordonnées spécifiées.
     */

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

    /**
     * Lance une nouvelle partie en mode hors ligne en réinitialisant le plateau de
     * jeu et les composants associés.
     */

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

    /**
     * Affiche le résultat de la partie en ligne, y compris le classement des
     * joueurs et le temps écoulé.
     * 
     * @param rankList Liste contenant le classement des joueurs.
     * @param isLooser Indique si le joueur est le perdant de la partie.
     */

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

    /**
     * Réinitialise l'état de toutes les cases du plateau de jeu en mode hors ligne.
     * 
     * @param dim Dimension du plateau de jeu.
     */

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

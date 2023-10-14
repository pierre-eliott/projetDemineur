package Demineur;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.MatteBorder;

/**
 * Classe représentant une case dans le jeu Démineur.
 * Chaque instance de cette classe est une case dans la grille du jeu.
 */

public class Case extends JPanel implements MouseListener{
    private GUI gui;
    private String txt = "";
    private final static int DIM = 50;

    private int i, j;

    private Client client; 

    private boolean isFlaged;
    private boolean isClicked;

    java.awt.Color backgroundColorCaseDefault = new java.awt.Color(125,125,125);
    java.awt.Color backgroundColorCaseSelected = new java.awt.Color(192,192,192);
    java.awt.Color backgroundColorCaseMine = new java.awt.Color(239, 154, 154);

    ImageIcon imageIcon = new ImageIcon(getClass().getResource("img/drapeau.png"));
    Image image = imageIcon.getImage();
    Image imageIconResize = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    ImageIcon imageIconResizeIcon = new ImageIcon(imageIconResize);
    JLabel flagLabel = new JLabel(imageIconResizeIcon);

    /**
     * Constructeur par défaut de la classe Case.
     * Initialise une case avec des valeurs par défaut.
     */

    Case()
    {
        this(null, new GUI(), 0, 0);
    }

    /**
     * Constructeur de la classe Case.
     * Initialise une case avec les coordonnées spécifiées et l'interface graphique
     * associée.
     * 
     * @param client Le client associé à la case (pour le mode en ligne).
     * @param gui    L'interface graphique du jeu.
     * @param i      L'indice de la ligne de la case dans la grille.
     * @param j      L'indice de la colonne de la case dans la grille.
     */

    Case(Client client, GUI gui, int i, int j) {
        this.gui = gui;
        this.i = i;
        this.j = j;
        this.client = client;

        isFlaged = false; 
        isClicked = false;

        setBackground(backgroundColorCaseDefault);
        //setBorder(BorderFactory.createLineBorder(Color.WHITE,1,true));
        setBorder(BorderFactory.createRaisedBevelBorder());
        setPreferredSize(new Dimension(DIM, DIM)); 
        setFont(new Font("Dialog", Font.BOLD, 25));
        addMouseListener(this);
    }

    /**
     * Méthode pour dessiner le contenu de la case.
     * 
     * @param gc L'objet Graphics utilisé pour dessiner la case.
     */

    @Override
    public void paintComponent(Graphics gc) {
        super.paintComponent(gc); // appel méthode mère (efface le dessin précedent)
        //gc.drawString(txt, 20, 20); // dessin du texte à la position 10, 10

        FontMetrics fm = gc.getFontMetrics(); // Obtenir les informations sur la police actuelle

        // Calculer la largeur et la hauteur du texte
        int textWidth = fm.stringWidth(txt);
        int textHeight = fm.getHeight();

        // Calculer la position pour centrer le texte dans le composant
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2;

        gc.drawString(txt, x, y); // Dessin du texte centré

    }

    /**
     * Méthode appelée lorsqu'une case est cliquée.
     * 
     * @param e L'événement de la souris associé au clic.
     */

    @Override
    public void mousePressed(MouseEvent e) {
        if(gui.getIsOnline())
        {
            if(SwingUtilities.isRightMouseButton(e))
            {
                if(!isFlaged && client.getFlags()<client.getNbMines() && !isClicked)
                {
                    /*isFlaged = true;
                    client.setFlags(1);
                    client.updateCellFlag(isFlaged, i, j);*/

                    isFlaged = true; 

                    setLayout(new BorderLayout());
                    add(flagLabel, BorderLayout.CENTER);

                    revalidate();
                    gui.setNbFlags(); 
                    client.setFlags(1);
                }
                else if(isFlaged)
                {
                    /*isFlaged= false;
                    client.setFlags(-1);
                    client.updateCellFlag(isFlaged, i, j);*/
                    isFlaged = false; 
                    gui.setNbFlagsMinus();
                    this.remove(flagLabel);
                    client.setFlags(-1);
                }
            }
            else
            {
                //System.out.println("ETAT CASE: "+ isClicked);
                if(!isClicked)
                {
                    //System.out.println("TEST ASK CASE");
                    client.askCase(i,j);
                }
            }
        }
        else
        {
            if(SwingUtilities.isRightMouseButton(e))
            {
                if(!isFlaged && !isClicked)
                {
                    if(gui.getNbFlags()< gui.getNbMines())
                    {
                        isFlaged = true; 

                        setLayout(new BorderLayout());
                        add(flagLabel, BorderLayout.CENTER);

                        revalidate();
                        gui.setNbFlags();

                    }
                }
                else if(isFlaged)
                {   
                    isFlaged = false; 
                    gui.setNbFlagsMinus();
                    this.remove(flagLabel);
                }
            }
            else
            {
                if(gui.isMine(i,j))
                {
                    txt = "X"; // chgt du texte à redessiner
                    setBackground(backgroundColorCaseMine);

                    gui.stopTimer();

                    JPanel explosionPanel = new JPanel();

                    ImageIcon imageIconExplosion = new ImageIcon(getClass().getResource("img/logo.png"));
                    Image imageExplosion = imageIconExplosion.getImage();
                    Image imageIconExplosionResize = imageExplosion.getScaledInstance(175, 150, Image.SCALE_SMOOTH);
                    ImageIcon imageIconExplosionResizeIcon = new ImageIcon(imageIconExplosionResize);

                    JLabel explosionLabel = new JLabel(imageIconExplosionResizeIcon);
                    JLabel textLoseLabel = new JLabel();
                    textLoseLabel.setText("YOU LOSED");
                    textLoseLabel.setFont(new Font("Dialog", Font.BOLD, 20));
                    textLoseLabel.setForeground(Color.RED);

                    explosionPanel.add(explosionLabel, BorderLayout.CENTER);
                    explosionPanel.add(textLoseLabel, BorderLayout.SOUTH);

                    int option = JOptionPane.showConfirmDialog(this, explosionPanel,"LOOSER", JOptionPane.CLOSED_OPTION, JOptionPane.CLOSED_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        gui.partyFinished();
                    }
                    
                }
                else
                {
                    if(!isClicked)
                    {
                        isClicked = true;

                        if(gui.getMines(i, j)==0)
                        {
                            AdjacentZeros(i,j);
                        }
                        else
                        {
                            txt = gui.getMines(i, j)+"";
                            if(gui.getMines(i, j)==0)
                            {
                                setForeground(Color.GRAY);
                            }
                            else if(gui.getMines(i, j)==1)
                            {
                                setForeground(Color.BLUE);
                            }
                            else if(gui.getMines(i, j)==2)
                            {
                                setForeground(new java.awt.Color(0, 130, 0));
                            }
                            else if(gui.getMines(i, j)==3)
                            {
                                setForeground(Color.RED);
                            }
                            else if(gui.getMines(i, j)==4)
                            {
                                setForeground(Color.BLACK);
                            }

                            setBackground(backgroundColorCaseSelected);
                            
                            gui.setRemainingSquares();

                            if(gui.getRemainingSquares()==0)
                            {
                                JPanel explosionPanel = new JPanel();

                                ImageIcon imageIconExplosion = new ImageIcon(getClass().getResource("img/logo.png"));
                                Image imageExplosion = imageIconExplosion.getImage();
                                Image imageIconExplosionResize = imageExplosion.getScaledInstance(175, 150, Image.SCALE_SMOOTH);
                                ImageIcon imageIconExplosionResizeIcon = new ImageIcon(imageIconExplosionResize);

                                JLabel explosionLabel = new JLabel(imageIconExplosionResizeIcon);
                                JLabel textLoseLabel = new JLabel();
                                textLoseLabel.setText("YOU WON: "+gui.getTimer() );
                                textLoseLabel.setFont(new Font("Dialog", Font.BOLD, 20));
                                textLoseLabel.setForeground(new java.awt.Color(61, 129, 58));

                                explosionPanel.add(explosionLabel, BorderLayout.CENTER);
                                explosionPanel.add(textLoseLabel, BorderLayout.SOUTH);

                                int option = JOptionPane.showConfirmDialog(this, explosionPanel,"CONGRATULATION", JOptionPane.CLOSED_OPTION, JOptionPane.CLOSED_OPTION);
                                if (option == JOptionPane.OK_OPTION) {
                                    gui.partyFinished();
                                }
                            }
                        }

                    }
                }
            }
        }        
        repaint() ;
    }

    /**
     * Méthode récursive utilisée pour révéler les cases vides adjacentes lorsque
     * le joueur clique sur une case vide (sans mines voisines) dans le jeu
     * Démineur.
     * La méthode explore les cases voisines de manière récursive et les révèle si
     * elles sont vides.
     * 
     * @param x L'indice de la ligne de la case actuelle.
     * @param y L'indice de la colonne de la case actuelle.
     */

    public void AdjacentZeros(int x, int y) {
        gui.setRemainingSquares(); // Met à jour le nombre de cases restantes à découvrir dans l'interface
                                   // graphique.
        txt = " "; // La case devient vide.
        isClicked = true; // La case est marquée comme cliquée.
        setBackground(backgroundColorCaseSelected); // Change la couleur de fond de la case pour indiquer qu'elle a été
                                                    // cliquée.
        setBorder(BorderFactory.createLineBorder(backgroundColorCaseSelected, 1, true)); // Ajoute une bordure à la
                                                                                         // case.

        // Tableaux de déplacements pour explorer les cases voisines.
        int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1 };
        int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1 };

        // Parcourt les cases voisines de la case actuelle.
        for (int i = 0; i < 8; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            // Vérifie si les nouvelles coordonnées sont valides dans la grille du jeu.
            if (gui.isValidateCoordinate(newX, newY)) {
                Case adjacentCase = gui.getCaseAt(newX, newY); // Récupère la case voisine.

                // Vérifie si la case voisine est vide et n'a pas encore été cliquée.
                if (adjacentCase.gui.getMines(newX, newY) == 0 && !adjacentCase.getClicked()) {
                    adjacentCase.AdjacentZeros(newX, newY); // Explore récursivement les cases voisines.
                } else if (adjacentCase.gui.getMines(newX, newY) != 0 && !adjacentCase.getClicked()) {
                    // Si la case voisine a des mines voisines, la révèle et met à jour son
                    // apparence.
                    adjacentCase.gui.setRemainingSquares(); // Met à jour le nombre de cases restantes à découvrir dans
                                                            // l'interface graphique.
                    adjacentCase.setClicked(); // Marque la case comme cliquée.
                    adjacentCase.setBackground(backgroundColorCaseSelected); // Change la couleur de fond de la case.
                    adjacentCase.setTxt(adjacentCase.gui.getMines(newX, newY) + ""); // Affiche le nombre de mines
                                                                                     // voisines.

                    // Change la couleur du texte en fonction du nombre de mines voisines.
                    if (adjacentCase.gui.getMines(newX, newY) == 1) {
                        adjacentCase.setForeground(Color.BLUE);
                    } else if (adjacentCase.gui.getMines(newX, newY) == 2) {
                        adjacentCase.setForeground(new java.awt.Color(0, 130, 0));
                    } else if (adjacentCase.gui.getMines(newX, newY) == 3) {
                        adjacentCase.setForeground(Color.RED);
                    } else if (adjacentCase.gui.getMines(newX, newY) == 4) {
                        adjacentCase.setForeground(Color.BLACK);
                    }

                    adjacentCase.repaint(); // Redessine la case mise à jour.
                }
            }
        }
    }

    /**
     * Obtient l'indice de la ligne de la case dans la grille.
     * 
     * @return L'indice de la ligne de la case.
     */

    public int get_X()
    {
        return i;
    }

    /**
     * Obtient l'indice de la colonne de la case dans la grille.
     * 
     * @return L'indice de la colonne de la case.
     */

    public int get_Y()
    {
        return j; 
    }

    /**
     * Vérifie si la case a été cliquée.
     * 
     * @return true si la case a été cliquée, sinon false.
     */

    public boolean getClicked()
    {
        return isClicked;
    }

    /**
     * Définit l'état de la case comme "cliquée".
     */

    public void setClicked()
    {
        //System.out.println("SET CLICKED");
        isClicked=true;
    }

    /**
     * Définit l'état de la case comme "non cliquée".
     */

    public void setNotClicked()
    {
        //System.out.println("SET NOT CLICKED");
        isClicked = false;
    }

    /**
     * Définit le texte affiché dans la case.
     * 
     * @param s Le texte à afficher.
     */

    public void setTxt(int s)
    {
        txt = s+"";
    }

    /**
     * Définit le texte affiché dans la case.
     * 
     * @param s Le texte à afficher.
     */

    public void setTxt(String s)
    {
        txt = s;
    }

    /**
     * Obtient le client associé à la case.
     * 
     * @return Le client associé à la case.
     */

    public Client getClient()
    {
        return client;
    }

    /**
     * Méthode appelée lorsqu'une partie en ligne est terminée.
     * Affiche un message indiquant si le joueur a gagné ou perdu.
     * 
     * @param isLooser true si le joueur a perdu, sinon false.
     */

    public void onlinePartyFinished(boolean isLooser)
    {
        txt = "X"; // chgt du texte à redessiner
        setBackground(backgroundColorCaseMine);

        if(isLooser)
        {
            JPanel explosionPanel = new JPanel();

            ImageIcon imageIconExplosion = new ImageIcon(getClass().getResource("img/logo.png"));
            Image imageExplosion = imageIconExplosion.getImage();
            Image imageIconExplosionResize = imageExplosion.getScaledInstance(175, 150, Image.SCALE_SMOOTH);
            ImageIcon imageIconExplosionResizeIcon = new ImageIcon(imageIconExplosionResize);

            JLabel explosionLabel = new JLabel(imageIconExplosionResizeIcon);
            JLabel textLoseLabel = new JLabel();
            textLoseLabel.setText("YOU LOSED");
            textLoseLabel.setFont(new Font("Dialog", Font.BOLD, 20));
            textLoseLabel.setForeground(Color.RED);

            explosionPanel.add(explosionLabel, BorderLayout.CENTER);
            explosionPanel.add(textLoseLabel, BorderLayout.SOUTH);

            int option = JOptionPane.showOptionDialog(this, explosionPanel, "LOOSER", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[] { "OK", "Leave" }, "OK");

            if (option == JOptionPane.OK_OPTION) {
                // Le joueur reste sur le serveur
                //System.out.println("TEST OK BUTTON");
            } 
            else if (option == 1) {
                // Le joueur quitte le serveur
                client.leaveSession();
            }
        }
        else
        {
            JPanel explosionPanel = new JPanel();

            ImageIcon imageIconExplosion = new ImageIcon(getClass().getResource("img/logo.png"));
            Image imageExplosion = imageIconExplosion.getImage();
            Image imageIconExplosionResize = imageExplosion.getScaledInstance(175, 150, Image.SCALE_SMOOTH);
            ImageIcon imageIconExplosionResizeIcon = new ImageIcon(imageIconExplosionResize);

            JLabel explosionLabel = new JLabel(imageIconExplosionResizeIcon);
            JLabel textLoseLabel = new JLabel();
            textLoseLabel.setText("YOU WON: "+gui.getTimer() );
            textLoseLabel.setFont(new Font("Dialog", Font.BOLD, 20));
            textLoseLabel.setForeground(new java.awt.Color(61, 129, 58));

            explosionPanel.add(explosionLabel, BorderLayout.CENTER);
            explosionPanel.add(textLoseLabel, BorderLayout.SOUTH);

            int option = JOptionPane.showOptionDialog(this, explosionPanel, "WINNER", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[] { "OK", "Leave" }, "OK");

            if (option == JOptionPane.OK_OPTION) {
                // Le joueur reste sur le serveur
                //System.out.println("TEST OK BUTTON");
            } 
            else if (option == 1) {
                // Le joueur quitte le serveur
                client.leaveSession();
            }
        }
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseReleased'");
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseEntered'");
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseExited'");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'mouseExited'");
    } 
    
}

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

//import org.w3c.dom.events.MouseEvent;

public class Case extends JPanel implements MouseListener{
    private GUI gui;
    private String txt = "";
    private final static int DIM = 50;

    private int i, j;

    private Client client; 

    private boolean isFlaged;
    private boolean isClicked;

    java.awt.Color backgroundColorCaseDefault = new java.awt.Color(125,125,125);
    java.awt.Color backgroundColorCaseSelected = new java.awt.Color(218, 206, 204);
    java.awt.Color backgroundColorCaseMine = new java.awt.Color(239, 154, 154);

    ImageIcon imageIcon = new ImageIcon(getClass().getResource("img/drapeau.png"));
    Image image = imageIcon.getImage();
    Image imageIconResize = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    ImageIcon imageIconResizeIcon = new ImageIcon(imageIconResize);
    JLabel flagLabel = new JLabel(imageIconResizeIcon);

    Case()
    {
        this(null, new GUI(), 0, 0);
    }

    Case(Client client, GUI gui, int i, int j) {
        this.gui = gui;
        this.i = i;
        this.j = j;
        this.client = client;

        isFlaged = false; 
        isClicked = false;

        setBackground(backgroundColorCaseDefault);
        setBorder(BorderFactory.createLineBorder(Color.WHITE,1,true));
        setPreferredSize(new Dimension(DIM, DIM)); 
        setFont(new Font("Dialog", Font.BOLD, 25));
        addMouseListener(this);
        }

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

    @Override
    public void mousePressed(MouseEvent e) {
        if(gui.getIsOnline())
        {
            //System.out.println("TEST ASK CASE");
            client.askCase(i,j);
            
            if(!client.getIsMine())
            {
                //System.out.println("TEST PASSAGE SETSCORE");
                client.setScore();
            }
        }
        else
        {
            if(SwingUtilities.isRightMouseButton(e))
            {
                if(! isFlaged)
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
                else
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

    public void AdjacentZeros(int x, int y)
    {
        gui.setRemainingSquares();
        txt = "0";
        isClicked = true;
        setBackground(backgroundColorCaseSelected);
        repaint();

        int[] dx = {-1,-1,-1,0,0,1,1,1};
        int[] dy = {-1,0,1,-1,1,-1,0,1};

        for(int i=0; i<8;i++)
        {
            int newX = x+dx[i];
            int newY = y+dy[i];

            if(gui.isValidateCoordinate(newX, newY))
            {
                Case adjacentCase = gui.getCaseAt(newX, newY);
                if(adjacentCase.gui.getMines(newX, newY)==0 && adjacentCase.getClicked()==false)
                {
                    adjacentCase.AdjacentZeros(newX, newY);
                }
                else if (adjacentCase.gui.getMines(newX, newY)!=0 && adjacentCase.getClicked()==false){
                    // Afficher le calcul des mines autours.
                }
            }
        }
        
    }

    public int get_X()
    {
        return i;
    }

    public int get_Y()
    {
        return j; 
    }

    public boolean getClicked()
    {
        return isClicked;
    }

    public void setClicked()
    {
        isClicked=true;
    }

    public void setTxt(int s)
    {
        txt = s+"";
    }

    public Client getClient()
    {
        return client;
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

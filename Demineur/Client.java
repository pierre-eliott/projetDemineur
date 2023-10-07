package Demineur;

import java.net.* ;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import java.io.* ;

public class Client implements Runnable{

    private DataOutputStream out;
    private DataInputStream in; 

    private GUI gui; 

    private String playerName;
    private int score=0;

    private int compute;

    private boolean isMine;

    private int x,y;
    
    Client()
    {
        this(new GUI());
    }

    Client(GUI g)
    {
        gui = g; 
    }

    public void JoinServer(String ipAdress, int port, String name)
    {
        playerName = name;

        gui.startTimer();

        try {

            Socket sock = new Socket(ipAdress, port);
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());

            out.writeUTF(playerName);

            Thread th = new Thread(this);
            th.start();
            
        } catch (UnknownHostException error) {
            System.out.println("Adresse IP inconnue");
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public void run()
    {
        while(true)
        {
            try{
                int cmd = in.readInt();
                System.out.println("COMMANDE RECU: "+cmd);

                switch(cmd)
                {
                    case 0:
                        String name = in.readUTF();
                        System.out.println(name+" joined the game"); // Players joined the game
                        gui.newPlayer(name);
                        break;

                    case 1: // dim de la matrice 
                        gui.setDim(in.readInt());
                        System.out.println(gui.getDim());
                        gui.setMatrixPanel();
                        break;

                    case 2: // nb de mines 
                        gui.setNbMines(in.readInt());
                        break;

                    case 3: // etat de la case x,y si pas une bombe
                        //System.out.println("TEST ETAT CASE");
                        compute = in.readInt();

                        x = in.readInt();
                        y = in.readInt();

                        gui.updateCell(x,y, compute);

                        isMine = false;
                        break;

                    case 4: // case x,y est une bombe
                        // A gerer
                        break;

                    case 5: // Nom de tous les joueurs
                        int nbPlayers = in.readInt(); 
                        for(int i=0; i < nbPlayers; i++)
                        {
                            gui.newPlayer(in.readUTF());
                        }
                        break;

                    case 6: // Demande de score du serveur
                        
                        try {
                            out.writeInt(score);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        
                        break;

                    case 7: // Recevoir le score de tous les joueurs;
                        ArrayList<Integer> scoreList = new ArrayList<>();
                        int size = in.readInt(); 
                        
                        //System.out.println("SIZE RECEIVED: "+ size);

                        for (int i = 0; i < size; i++) {
                            scoreList.add(in.readInt()); 
                        }

                        gui.updateScore(scoreList);
                        break;

                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void askCase(int x, int y)
    {
        try{
            out.writeInt(0);
            out.writeInt(x);
            out.writeInt(y);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public int getCompute()
    {
        return compute;
    }

    public void setScore()
    {
        score++;
        // System.out.println("SCORE: " +score);

        try
        {
            out.writeInt(1); // faire demander les scores au serveur.
        }
        catch(IOException e)
        {
            e.printStackTrace();
        } 
    }

    public boolean getIsMine()
    {
        return isMine;
    }
}

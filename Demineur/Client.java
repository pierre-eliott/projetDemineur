package Demineur;

import java.net.* ;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import java.awt.Color;
import java.io.* ;

/**
 * Cette classe représente le client du jeu Démineur en mode multijoueur.
 * Il gère la communication avec le serveur, les interactions avec l'interface utilisateur
 * et les actions du joueur pendant la partie.
 */

public class Client implements Runnable{

    private DataOutputStream out;
    private DataInputStream in; 
    private Socket sock; 

    private GUI gui; 

    private String playerName;
    private int id; 

    private int compute;

    private boolean isMine;

    private int nbFlagsRemaining=0;
    private int nbMines;
    private int dim;

    private int nbPlayersParty = 0;

    private int x,y;

    private ArrayList<String> playerList= new ArrayList<String>();

    private volatile boolean playerProcess = true;

    /**
     * Constructeur par défaut de la classe Client.
     * Initialise une nouvelle instance de GUI pour l'interface utilisateur.
     */
    
    Client()
    {
        this(new GUI());
    }

    /**
     * Constructeur de la classe Client.
     * Initialise le client avec l'interface utilisateur passée en paramètre.
     * 
     * @param g L'interface utilisateur du client.
     */

    Client(GUI g)
    {
        gui = g; 
    }

    /**
     * Rejoint le serveur Démineur avec l'adresse IP, le port et le nom du joueur
     * spécifiés.
     * 
     * @param ipAddress L'adresse IP du serveur.
     * @param port      Le port de communication avec le serveur.
     * @param name      Le nom du joueur.
     */

    public void JoinServer(String ipAdress, int port, String name)
    {
        playerName = name;

        gui.startTimer();

        try {

            sock = new Socket(ipAdress, port);
            out = new DataOutputStream(sock.getOutputStream());
            in = new DataInputStream(sock.getInputStream());

            out.writeUTF(playerName);
            id = in.readInt();

            Thread th = new Thread(this);
            th.start();
            
        } catch (UnknownHostException error) {
            System.out.println("Adresse IP inconnue");
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    /**
     * Gère la logique de communication avec le serveur et les interactions avec
     * l'interface utilisateur.
     */

    public void run()
    {
        while(playerProcess)
        {
            try{
                int cmd = in.readInt();
                //System.out.println("COMMANDE RECU: "+cmd);

                switch(cmd)
                {
                    case 0:
                        String name = in.readUTF();
                        System.out.println(name+" joined the game"); // Players joined the game
                        gui.newPlayer(name);
                        nbPlayersParty++;
                        break;

                    case 1: // dim de la matrice 
                        dim = in.readInt();
                        gui.setDim(dim);
                        //System.out.println(gui.getDim());
                        gui.setMatrixPanel();
                        break;

                    case 2: // nb de mines
                         nbMines = in.readInt();
                        gui.setNbMines(nbMines);
                        gui.initNbFlags(nbMines);
                        break;

                    case 3: // etat de la case x,y si pas une bombe
                        //System.out.println("TEST ETAT CASE");
                        compute = in.readInt();

                        x = in.readInt();
                        y = in.readInt();

                        gui.updateCell(x,y, compute);

                        isMine = false;
                        //System.out.println("etat processPlayer: "+ playerProcess);
                        break;

                    case 4: // case x,y est une bombe
                        x = in.readInt();
                        y = in.readInt();
                        int idLoos = in.readInt();

                        gui.updateCellMine(x, y);

                        gui.stopTimer();                        
                        
                        //isMine = true;

                        ArrayList<String> rankListLoos = new ArrayList<String>();
                        int m = in.readInt();
                        for(int i=0; i<m; i++)
                        {
                            rankListLoos.add(in.readUTF());
                            //System.out.println(in.readUTF());
                        }

                        if(idLoos == id)
                        {
                            gui.endPartyOnline(rankListLoos, true);
                        }
                        else{
                            gui.endPartyOnline(rankListLoos, false);
                        }

                        

                        ArrayList<Integer> newScoreListLoos = new ArrayList<>();

                        for (int i = 0; i < nbPlayersParty; i++) {
                            newScoreListLoos.add(0); 
                        }

                        gui.updateScore(newScoreListLoos);

                        //System.out.println("DIM = "+dim);

                        //gui.setAllCaseFalse(dim);

                        gui.setMatrixPanel();

                        break;

                    case 5: // Nom de tous les joueurs
                        int nbPlayers = in.readInt(); 
                        for(int i=0; i < nbPlayers; i++)
                        {
                            gui.newPlayer(in.readUTF());
                        }
                        break;

                    case 6: // End
                        
                        gui.stopTimer();

                        ArrayList<String> rankList = new ArrayList<String>();
                        int n = in.readInt();
                        for(int i=0; i<n; i++)
                        {
                            rankList.add(in.readUTF());
                            //System.out.println(in.readUTF());
                        }

                        gui.endPartyOnline(rankList, false);

                        ArrayList<Integer> newScoreList = new ArrayList<>();

                        for (int i = 0; i < nbPlayersParty; i++) {
                            newScoreList.add(0); 
                        }

                        gui.updateScore(newScoreList);

                        //System.out.println("DIM = "+dim);

                        //gui.setAllCaseFalse(dim);

                        gui.setMatrixPanel();
                        
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
                    case 8: // update l'affichage des joueurs
                            int idLeave = in.readInt();
                            System.out.println("Palyer: "+ idLeave + " left the game");
                            //playerList.remove(idLeave);
                            gui.updatePlayersListDisplayed(idLeave);
                        break;
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Demande au serveur l'état de la case aux coordonnées spécifiées.
     * 
     * @param x La coordonnée x de la case.
     * @param y La coordonnée y de la case.
     */

    public void askCase(int x, int y)
    {
        try{
            out.writeInt(0);
            out.writeInt(x);
            out.writeInt(y);
            out.writeInt(id);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Obtient la valeur de la case.
     * 
     * @return La valeur de la case.
     */

    public int getCompute()
    {
        return compute;
    }

    /**
     * Indique si la case est une mine ou non.
     * 
     * @return `true` si la case est une mine, `false` sinon.
     */

    public boolean getIsMine()
    {
        return isMine;
    }

    /**
     * Incrémente ou décrémente le nombre de drapeaux restants.
     * 
     * @param n La valeur d'incrémentation ou de décrémentation.
     */

    public void setFlags(int n) // n= 1 ou -1
    {
        nbFlagsRemaining += n;
    }

    /**
     * Obtient le nombre de drapeaux restants.
     * 
     * @return Le nombre de drapeaux restants.
     */

    public int getFlags()
    {
        return nbFlagsRemaining;
    }

    /**
     * Obtient le nombre de mines dans la matrice.
     * 
     * @return Le nombre de mines dans la matrice.
     */

    public int getNbMines()
    {
        return nbMines;
    }

    /**
     * Obtient l'identifiant du joueur.
     * 
     * @return L'identifiant du joueur.
     */

    public int getId()
    {
        return id;
    }

    /**
     * Met à jour l'état de la cellule avec un drapeau.
     * 
     * @param isFlaged `true` si la cellule a un drapeau, `false` sinon.
     * @param x        La coordonnée x de la cellule.
     * @param y        La coordonnée y de la cellule.
     */

    public void updateCellFlag(boolean isFlaged, int x, int y)
    {
        gui.updateCellFlag(isFlaged, x, y, nbFlagsRemaining, nbMines);

    }

    /**
     * Quitte la session de jeu en informant le serveur et fermant les flux de
     * communication.
     */

    public void leaveSession()
    {
        //System.out.println("TEST LEAVE - CLIENT");
        try{
            out.writeInt(2);
            out.writeInt(id);
            in.close();
            out.close();
            sock.close();
            sock = null;
            playerProcess = false; 
            gui.newPartyOffline();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }

}

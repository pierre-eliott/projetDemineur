package Demineur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.Comparator;

/**
 * Cette classe représente le serveur du jeu Démineur en mode multijoueur.
 * Il gère la communication avec les clients, maintient l'état du jeu et 
 * effectue les actions nécessaires en réponse aux commandes des clients.
 */

public class Server implements Runnable {

    final static int PORT = 10000;
    private Thread playerProcess;
    private volatile boolean isRunning = true;

    private Matrix matrix;
    private static ArrayList<DataInputStream> inPutList = new ArrayList<DataInputStream>();
    private static ArrayList<DataOutputStream> outPutList = new ArrayList<DataOutputStream>();

    private ArrayList<String> playerList = new ArrayList<String>();
    private ArrayList<Integer> scoreList = new ArrayList<Integer>();
    private ArrayList<Socket> socketList = new ArrayList<Socket>();

    private int remainingSquares; 

    private ServerSocket serv;

    /**
     * Constructeur de la classe Server. Initialise la matrice de jeu,
     * écoute les connexions clientes et démarre le processus de gestion des
     * joueurs.
     */

    Server() {

        matrix = new Matrix();
        matrix.fillRandomly();
        remainingSquares = matrix.getDim() * matrix.getDim() - matrix.getNbMines();
        System.out.println("Matrice");
        matrix.display();

        try{
            serv = new ServerSocket(PORT);
            System.out.println("Server started on port: "+PORT);
            
            playerProcess = new Thread(this);
            playerProcess.start();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Méthode principale du serveur, appelée lors du démarrage du programme.
     * Initialise et lance le serveur Démineur.
     */

    public static void main(String[] args) {
        System.out.println("Server started");
        new Server();
    }

    /**
     * Gère la logique de communication avec les clients.
     */

    @Override
    public void run() {
        try 
        {
            System.out.println("Waiting for clients");
            Socket clientSock = serv.accept();
            socketList.add(clientSock);

            System.out.println("Client accepted");
            
            playerProcess = new Thread(this);
            playerProcess.start();

            inPutList.add(new DataInputStream(clientSock.getInputStream()));
            outPutList.add(new DataOutputStream(clientSock.getOutputStream()));

            //System.out.println("Size outputList Run: "+outPutList.size());
            
            DataInputStream in = inPutList.get(inPutList.size()-1);
            DataOutputStream out = outPutList.get(inPutList.size()-1);

            String playerName = in.readUTF();
            out.writeInt(playerList.size()); // pour initialiser l'id du nouveau joueur.
            
            out.writeInt(5);
            out.writeInt(playerList.size());

            for(int i=0; i<playerList.size(); i++)
            {
                out.writeUTF(playerList.get(i));
            }

            playerList.add(playerName);
            scoreList.add(0);

            broadcastWelcomeMessage(playerName);

            broadcastDimMessage(matrix.getDim());

            broadcastNbMinesMessage(matrix.getNbMines());

            while (isRunning) {
                try{
                
                    int cmd = in.readInt();
                    //System.out.println("Server- cmb = "+cmd);

                    switch(cmd)
                    {
                        case 0: // requete de la case 
                            int x = in.readInt();
                            int y = in.readInt();

                            int idClientRequest = in.readInt();
                            //System.out.println("TEST GET CASE: "+ x+" "+y);

                            if(!matrix.getCases(x,y) && remainingSquares>0)
                            {
                                remainingSquares--;

                                broadcastCaseStateMessage(matrix.computeMinesNumber(x, y), x, y);
                                scoreList.set(idClientRequest,scoreList.get(idClientRequest)+1);
                                broadcastScoreListMessage();
                                if(remainingSquares==0)
                                {
                                    //System.out.println("Fin partie : 0 cases restantes");
                                    ArrayList<String> rankinList = new ArrayList<String>();
                                    Collections.sort(scoreList, new PlayerComparator());

                                    for (int i = 0; i < playerList.size(); i++) {
                                        String name = playerList.get(i);
                                        int score = scoreList.get(i);
                                        String rankingEntry = (i + 1) + ": " + name + " - " + score + " points";
                                        rankinList.add(rankingEntry);
                                    }

                                    broadcastEndMessage(rankinList);
                                    remainingSquares = matrix.getDim() * matrix.getDim() - matrix.getNbMines();

                                    matrix.fillRandomly();
                                    matrix.display(); 
                                }
                            }
                            else
                            {
                                scoreList.set(idClientRequest,0);

                                ArrayList<String> rankinList = new ArrayList<String>();
                                Collections.sort(scoreList, new PlayerComparator());

                                for (int i = 0; i < playerList.size(); i++) {
                                    String name = playerList.get(i);
                                    int score = scoreList.get(i);
                                    String rankingEntry = (i + 1) + ": " + name + " - " + score + " points";
                                    rankinList.add(rankingEntry);
                                }

                                broadcastMineStateMessage(x, y, rankinList, idClientRequest);
                                remainingSquares = matrix.getDim() * matrix.getDim() - matrix.getNbMines();

                                matrix.fillRandomly();
                                matrix.display(); 

                            }
                            
                            break;

                        case 1: 
                            
                            break;
                        
                        case 2: 
                            int idLeave = in.readInt();
                            broadcastClientLeaveMessage(idLeave);
                            closeClientSession(idLeave);
                            break;
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }

    /**
     * Envoie un message de déconnexion à tous les clients.
     * 
     * @param id L'identifiant du client déconnecté.
     */

    public void closeClientSession(int id)
    {
        System.out.println(playerList.get(id) + " left the game ");
        try{
            outPutList.get(id).close();
            inPutList.get(id).close();
            socketList.get(id).close();
            Thread.currentThread().stop();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Envoie un message de déconnexion à tous les clients.
     * 
     * @param id L'identifiant du client déconnecté.
     */

    public void broadcastClientLeaveMessage(int id)
    {
        int i=0;
        for(DataOutputStream outputStream: outPutList)
        {
            if(outputStream!=null && !socketList.get(i).isClosed())
            {
                try{
                    outputStream.writeInt(8);
                    outputStream.writeInt(id);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            i++;
        }
    }

    /**
     * Envoie un message de bienvenue à tous les clients.
     * 
     * @param message Le message de bienvenue.
     */

    public void broadcastWelcomeMessage(String message)
    {
        int i=0;
        for(DataOutputStream outputStream: outPutList)
        {
            if(outputStream!=null && !socketList.get(i).isClosed())
            {
                try{
                    outputStream.writeInt(0);
                    outputStream.writeUTF(message);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            i++;
        }
    }

    /**
     * Envoie la dimension de la matrice à tous les clients.
     * 
     * @param dim La dimension de la matrice.
     */

    public void broadcastDimMessage(int dim)
    {
        int i =0;
        for(DataOutputStream outputStream: outPutList)
        {
            if(outputStream!=null && !socketList.get(i).isClosed())
            {
                try{
                    outputStream.writeInt(1);
                    outputStream.writeInt(dim);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            i++;
        }
    }

    /**
     * Envoie le nombre de mines à tous les clients.
     * 
     * @param nbMines Le nombre de mines dans la matrice.
     */

    public void broadcastNbMinesMessage(int nbMines)
    {
        int i = 0;
        for(DataOutputStream outputStream: outPutList)
        {
            if(outputStream!=null && !socketList.get(i).isClosed())
            {
            try{
                outputStream.writeInt(2);
                outputStream.writeInt(nbMines);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            }
            i++;
        }
    }

    /**
     * Envoie un message de fin de partie à tous les clients.
     * 
     * @param rankList La liste des classements des joueurs.
     */

    public void broadcastEndMessage(ArrayList<String> rankList)
    {
        int i = 0;
        for(DataOutputStream outputStream: outPutList)
        {
            //System.out.println("TEST broadcastCaseStateMessage");
            if(outputStream!=null && !socketList.get(i).isClosed())
            {
                try{
                    outputStream.writeInt(6);
                    outputStream.writeInt(rankList.size());
                    //System.out.println("RankList size: "+rankList.size());

                    for(int j=0; j<rankList.size(); j++)
                    {
                        outputStream.writeUTF(rankList.get(j));
                    }

                    scoreList.set(i, 0);

                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }   
            }
            i++;
        }
    }

    /**
     * Envoie l'état d'une case à tous les clients.
     * 
     * @param n Le nombre de mines adjacentes à la case.
     * @param x La coordonnée x de la case.
     * @param y La coordonnée y de la case.
     */

    public void broadcastCaseStateMessage(int n, int x, int y)
    {
        int i = 0;
        for(DataOutputStream outputStream: outPutList)
        {
            //System.out.println("TEST broadcastCaseStateMessage");
            if(outputStream!=null && !socketList.get(i).isClosed())
            {
                try{
                    outputStream.writeInt(3);
                    outputStream.writeInt(n);
                    outputStream.writeInt(x);
                    outputStream.writeInt(y);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }   
            }
            i++;
        }
    }

    /**
     * Envoie l'état de la mine à tous les clients.
     * 
     * @param x        La coordonnée x de la mine.
     * @param y        La coordonnée y de la mine.
     * @param rankList La liste des classements des joueurs.
     * @param idLoos   L'identifiant du joueur perdant.
     */

    public void broadcastMineStateMessage(int x, int y, ArrayList<String> rankList, int idLoos)
    {
        int i = 0;
        for(DataOutputStream outputStream: outPutList)
        {
            //System.out.println("TEST broadcastCaseStateMessage");
            if(outputStream!=null && !socketList.get(i).isClosed())
            {
                try{
                    outputStream.writeInt(4);
                    outputStream.writeInt(x);
                    outputStream.writeInt(y);
                    outputStream.writeInt(idLoos);
                    outputStream.writeInt(rankList.size());
                    //System.out.println("RankList size: "+rankList.size());

                    for(int j=0; j<rankList.size(); j++)
                    {
                        outputStream.writeUTF(rankList.get(j));
                    }

                    scoreList.set(i, 0);

                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }   
            }
            i++;
        }
    }

    /**
     * Envoie un message de début de partie à tous les clients.
     */

    public void broadcastMineMessage()
    {
        int i =0;
        for(DataOutputStream outputStream: outPutList)
        {
            if(outputStream!=null && !socketList.get(i).isClosed())
            {
                try{
                    outputStream.writeInt(4);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            i++;
        }
    }

    /**
     * Envoie la liste des scores à tous les clients.
     */

    public void broadcastScoreListMessage() {
        int i =0;
        for (DataOutputStream outputStream : outPutList) {
            if(outputStream!=null && !socketList.get(i).isClosed())
            {
                try {
                    outputStream.writeInt(7);

                    //System.out.println("SIZE SEND: "+scoreList.size());

                    outputStream.writeInt(scoreList.size());
                    
                    for (int score : scoreList) {
                        outputStream.writeInt(score);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            i++;
        }
    }
}

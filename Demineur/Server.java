package Demineur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class Server implements Runnable {

    final static int PORT = 10000;
    private Thread playerProcess;
    private Matrix matrix;
    private static ArrayList<DataInputStream> inPutList = new ArrayList<DataInputStream>();
    private static ArrayList<DataOutputStream> outPutList = new ArrayList<DataOutputStream>();

    private ArrayList<String> playerList = new ArrayList<String>();
    private ArrayList<Integer> scoreList = new ArrayList<Integer>();

    private ServerSocket serv;

    Server() {

        matrix = new Matrix();
        matrix.fillRandomly();
        System.out.println("Matrice");
        matrix.display();

        try{
            serv = new ServerSocket(PORT);
            System.out.println("Server started on port "+PORT);
            
            playerProcess = new Thread(this);
            playerProcess.start();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Server started");
        new Server();
    }

    @Override
    public void run() {
        try 
        {
            System.out.println("Waiting for clients");
            Socket clientSock = serv.accept();

            System.out.println("Client accepted");
            
            playerProcess = new Thread(this);
            playerProcess.start();

            inPutList.add(new DataInputStream(clientSock.getInputStream()));
            outPutList.add(new DataOutputStream(clientSock.getOutputStream()));

            System.out.println("Size outputList Run: "+outPutList.size());
            
            DataInputStream in = inPutList.get(inPutList.size()-1);
            DataOutputStream out = outPutList.get(inPutList.size()-1);

            String playerName = in.readUTF();
            
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

            while (playerProcess!=null) {
                try{
                
                    int cmd = in.readInt();
                    //System.out.println(cmd);

                    switch(cmd)
                    {
                        case 0: // requete de la case 
                            int x = in.readInt();
                            int y = in.readInt();

                            //System.out.println("TEST GET CASE: "+ x+" "+y);

                            if(!matrix.getCases(x,y))
                            {
                                broadcastCaseStateMessage(matrix.computeMinesNumber(x, y), x, y);
                            }
                            else
                            {
                                // Cas ou c'est une bombe
                            }
                            
                            break;

                        case 1: 
                            broadcastScoreMessage();
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

    public void broadcastWelcomeMessage(String message)
    {
        for(DataOutputStream outputStream: outPutList)
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
    }

    public void broadcastDimMessage(int dim)
    {
        for(DataOutputStream outputStream: outPutList)
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
    }

    public void broadcastNbMinesMessage(int nbMines)
    {
        for(DataOutputStream outputStream: outPutList)
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
    }

    public void broadcastCaseStateMessage(int n, int x, int y)
    {
        for(DataOutputStream outputStream: outPutList)
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
    }

    public void broadcastMineMessage()
    {
        for(DataOutputStream outputStream: outPutList)
        {
            try{
                outputStream.writeInt(4);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void broadcastScoreMessage(){
        int i = 0; 
        System.out.println("Size outputList broadcastScoreMessage: "+outPutList.size());
        for (DataOutputStream outputStream : outPutList) {
            try {
                outputStream.writeInt(6);
                System.out.println("Score recu du joeueur "+i + " : "+inPutList.get(i).readInt());
                i++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        broadcastScoreListMessage();
    }

    public void broadcastScoreListMessage() {
        for (DataOutputStream outputStream : outPutList) {
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
    }

}

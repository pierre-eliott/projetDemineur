package Demineur;

import java.util.Random;

public class Matrix {
    private boolean [][] cases; 
    private final static int DIM = 12;
    private final static int NBMINES = 30;
    private int dimSquare;
    private int nbMines;

    Matrix(){
        this(NBMINES);
    }

    Matrix(int nb)
    {
        this(nb, DIM);
    }

    Matrix(int nbMines, int dim)
    {
        this.nbMines = nbMines;
        this.dimSquare = dim;
        cases = new boolean[dim][dim];

        fillRandomly();
    }

    /**
     * place randomly mines
     */

    void fillRandomly(){
        this.setCases(new boolean[dimSquare][dimSquare]);

        Random generator = new Random();
        
        //pick number randomly

        int nb = nbMines;
        while (nb != 0){
            int x = generator.nextInt(cases[0].length);
            int y = generator.nextInt(cases[0].length);
            
            if (!cases[x][y])
            {
                cases[x][y] = true;
                nb--;
            }
        }
    }

    /**
     * display Matrix
    */
    void display(){
        for(int i=0; i<cases.length; i++)
        {
            for(int j=0; j<cases[0].length; j++)
            {
                System.out.print(cases[i][j] ? "x": computeMinesNumber(i,j));
                System.out.print(" ");
                
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * compute nb of mines around
     */
    int computeMinesNumber(int x, int y)
    {
        int nb = 0;
        
        for(int i = Math.max(0,x-1); i<Math.min(cases.length, x+2);i++)
        {
            for(int j = Math.max(0,y-1); j<Math.min(cases.length, y+2);j++)
            {
                {
                    if (cases[i][j])
                    nb++;
                }    
            }
        }

        return nb ;
    }

    int getDim()
    {
        return dimSquare;
    }

    int getNbMines()
    {
        return nbMines;
    }

    boolean getCases(int x, int y)
    {
        return cases[x][y];
    }

    void setCases(boolean[][] cases)
    {
        this.cases = cases;
    }

    void setDim(int d)
    {
        dimSquare = d;
    }

    void setMines(int nMines)
    {
        nbMines = nMines;
    }

}

package Demineur;

import java.util.Random;

/**
 * Classe représentant la matrice du jeu Démineur.
 * Cette classe gère la disposition des mines dans la grille du jeu
 * et calcule le nombre de mines voisines pour chaque case.
 */
public class Matrix {
    private boolean[][] cases; 
    private final static int DIM = 12;
    private final static int NBMINES = 25; 
    private int dimSquare;
    private int nbMines;

    /**
     * Constructeur par défaut de la classe Matrix.
     * Initialise la matrice avec le nombre de mines par défaut.
     */
    Matrix() {
        this(NBMINES);
    }

    /**
     * Constructeur de la classe Matrix.
     * Initialise la matrice avec un nombre spécifié de mines.
     * @param nb Le nombre de mines à placer dans la matrice.
     */
    Matrix(int nb) {
        this(nb, DIM);
    }

    /**
     * Constructeur de la classe Matrix.
     * Initialise la matrice avec un nombre spécifié de mines et une dimension spécifiée.
     * @param nbMines Le nombre de mines à placer dans la matrice.
     * @param dim La dimension de la matrice (carrée).
     */
    Matrix(int nbMines, int dim) {
        this.nbMines = nbMines;
        this.dimSquare = dim;
        cases = new boolean[dim][dim];

        fillRandomly();
    }

    /**
     * Place aléatoirement les mines dans la matrice.
     */
    void fillRandomly() {
        this.setCases(new boolean[dimSquare][dimSquare]);

        Random generator = new Random();
        int nb = nbMines;

        while (nb != 0) {
            int x = generator.nextInt(cases[0].length);
            int y = generator.nextInt(cases[0].length);
            
            if (!cases[x][y]) {
                cases[x][y] = true;
                nb--;
            }
        }
    }

    /**
     * Affiche la matrice dans la console.
     * Les mines sont représentées par "x", et le nombre de mines voisines est affiché pour les autres cases.
     */
    void display() {
        for(int i = 0; i < cases.length; i++) {
            for(int j = 0; j < cases[0].length; j++) {
                System.out.print(cases[i][j] ? "x" : computeMinesNumber(i, j));
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Calcule le nombre de mines voisines pour une case donnée.
     * @param x L'indice de la ligne de la case.
     * @param y L'indice de la colonne de la case.
     * @return Le nombre de mines voisines de la case spécifiée.
     */
    int computeMinesNumber(int x, int y) {
        int nb = 0;
        
        for(int i = Math.max(0, x - 1); i < Math.min(cases.length, x + 2); i++) {
            for(int j = Math.max(0, y - 1); j < Math.min(cases.length, y + 2); j++) {
                if (cases[i][j]) {
                    nb++;
                }    
            }
        }

        return nb;
    }

    /**
     * Obtient la dimension de la matrice.
     * @return La dimension de la matrice.
     */
    int getDim() {
        return dimSquare;
    }

    /**
     * Obtient le nombre de mines dans la matrice.
     * @return Le nombre de mines dans la matrice.
     */
    int getNbMines() {
        return nbMines;
    }

    /**
     * Obtient l'état de la case spécifiée dans la matrice.
     * @param x L'indice de la ligne de la case.
     * @param y L'indice de la colonne de la case.
     * @return true si la case contient une mine, sinon false.
     */
    boolean getCases(int x, int y) {
        return cases[x][y];
    }

    /**
     * Définit l'état de la matrice avec une nouvelle matrice de cases.
     * @param cases La nouvelle matrice de cases.
     */
    void setCases(boolean[][] cases) {
        this.cases = cases;
    }

    /**
     * Définit la dimension de la matrice.
     * @param d La nouvelle dimension de la matrice.
     */
    void setDim(int d) {
        dimSquare = d;
    }

    /**
     * Définit le nombre de mines dans la matrice.
     * @param nMines Le nouveau nombre de mines dans la matrice.
     */
    void setMines(int nMines) {
        nbMines = nMines;
    }
}


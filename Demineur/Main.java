package Demineur;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Programme principal du jeu Démineur.
 * Cette classe représente la fenêtre principale de l'application.
 * Elle hérite de la classe JFrame pour créer une fenêtre graphique.
 * @author Pierre-Eliott Monsch
 * @version 0.0
*/

public class Main extends JFrame {

    /**
     * Constructeur par défaut de la classe Main.
     * Initialise la matrice du jeu et crée l'interface graphique.
     */
    Main() {
        System.out.println("Démarrage du jeu");

        // Initialisation de la matrice du jeu
        Matrix matrix = new Matrix();

        // Affichage de la matrice dans la console
        matrix.display();

        // Création de l'interface graphique en utilisant la classe GUI
        GUI gui = new GUI(this);

        // Définition du contenu de la fenêtre principale
        setContentPane(gui);

        // Redimensionnement automatique de la fenêtre en fonction de son contenu
        pack();

        // Définir le comportement de l'application lorsque la fenêtre est fermée
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Rendre la fenêtre principale visible à l'utilisateur
        setVisible(true);
    }

    /**
     * Méthode principale de l'application.
     * Crée une instance de la classe Main pour démarrer le jeu.
     * @param args[] Les arguments de ligne de commande (non utilisés dans cette application).
     */
    public static void main(String args[]) {
        new Main(); // Crée une nouvelle instance de la classe Main pour démarrer le jeu.
    }

    /**
     * Méthode permettant de quitter l'application.
     * Termine l'exécution du programme en cours.
     */
    public static void quit() {
        System.exit(ABORT);
    }
}


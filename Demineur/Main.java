package Demineur;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
/** 
 * Programme nul
 * @author Pierre-Eliott Monsch
 * @version 0.0
*/

public class Main extends JFrame {
   
    /** 
     * @param args[] not used
     */

    Main()
    {
        System.out.println("Start");
        
        Matrix matrix = new Matrix();

        // display
        matrix.display();

        GUI gui = new GUI(this);

        setContentPane(gui);

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    public static void main(String args[])
    {
        new Main(); 
    }

    public static void quit()
    {
        System.exit(ABORT);
    }

}

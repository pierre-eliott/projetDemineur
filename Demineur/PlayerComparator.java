package Demineur;

import java.util.Comparator;

/**
 * Un comparateur personnalisé pour trier les scores des joueurs par ordre décroissant.
 */
class PlayerComparator implements Comparator<Integer> {
    /**
     * Compare deux scores dans l'ordre décroissant.
     * @param score1 Le premier score à comparer.
     * @param score2 Le deuxième score à comparer.
     * @return Une valeur négative si score1 est supérieur à score2, une valeur positive si score1 est inférieur à score2, 0 si les scores sont égaux.
     */
    @Override
    public int compare(Integer score1, Integer score2) {
        return score2.compareTo(score1); // Triez par ordre décroissant
    }
}


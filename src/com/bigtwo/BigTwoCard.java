package com.bigtwo;

/**
 * This class is a subclass of the com.bigtwo.Card class and specifies the compareTo function to give the correct order of
 * priority of cards based on the rules of the BigTwo game.
 *
 * @author Anmol Gupta
 */
public class BigTwoCard extends Card {

    /**
     *
     * A public constructor method that initializes a com.bigtwo.BigTwoCard based on the suit and the rank provided as parameters.
     *
     * @param suit The suit of the card to be formed.
     * @param rank The rank of the card to be formed.
     */
    public BigTwoCard(int suit, int rank) {
        super(suit, (rank + 2) % 13);
    }

    /**
     *
     * This method overrides the compareTo function of the com.bigtwo.Card class to give the correct order of
     * priority of cards based on the rules of the BigTwo game.
     *
     * @param card the card to be compared
     * @return An integer value specifying which card is better - 1 if current card is better, -1 if the given
     * card is better, 0 is both are the same.
     */
    @Override
    public int compareTo(Card card) {

        int thisRank = (this.rank + 11) % 13;
        int cardRank = (card.rank + 11) % 13;

        if (thisRank > cardRank) {
            return 1;
        } else if (thisRank < cardRank) {
            return -1;
        } else if (this.suit > card.suit) {
            return 1;
        } else if (this.suit < card.suit) {
            return -1;
        } else {
            return 0;
        }
    }
}

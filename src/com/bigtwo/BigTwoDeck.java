package com.bigtwo;

/**
 * This class is a subclass of the com.bigtwo.Deck class and specifies the com.bigtwo.Deck based on the game Big Two.
 * It is a subclass of abstract class com.bigtwo.Hands.Hand.
 *
 * @author Anmol Gupta
 */
public class BigTwoDeck extends Deck {

    /**
     *
     * This method creates 52 cards and adds them to the cards collection of the game.
     *
     */
    @Override
    public void initialize() {
        removeAllCards();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                BigTwoCard bigTwoCard = new BigTwoCard(i, j);
                addCard(bigTwoCard);
            }
        }
    }
}

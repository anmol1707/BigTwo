package com.bigtwo.Hands;

import com.bigtwo.CardGamePlayer;
import com.bigtwo.CardList;

/**
 * This class is a subclass of the com.bigtwo.Hands.Hand class and is used to model
 * and implement a com.bigtwo.Hands.Hand of com.bigtwo.Hands.Pair in a Big Two com.bigtwo.Card game.
 * It is a subclass of abstract class com.bigtwo.Hands.Hand.
 *
 * @author Anmol Gupta
 */
public class Pair extends Hand {

    /**
     *
     * A public constructor function that initializes a hand of type com.bigtwo.Hands.Pair for given player and using the given cards
     *
     * @param player The player who owns this hand
     * @param cards The cards used to create this hand
     */
    public Pair(CardGamePlayer player, CardList cards) {
        super(player, cards);
        super.setHandLevel(1);
    }

    /**
     *
     * This function overrides the abstract function defined in the com.bigtwo.Hands.Hand class and returns whether the given
     * set of cards can actually form a hand of this type
     *
     * @return A boolean value specifying if the cards given can form the hand or not
     */
    @Override
    public boolean isValid() {
        if(size() != 2) {
            return false;
        }
        return cardsHaveSameRank();
    }

    /**
     *
     * This function overrides the abstract function defined in the com.bigtwo.Hands.Hand class and return the type of the com.bigtwo.Hands.Hand.
     *
     * @return A String indicating the type of the hand.
     */
    @Override
    public String getType() {
        return "com.bigtwo.Hands.Pair";
    }
}

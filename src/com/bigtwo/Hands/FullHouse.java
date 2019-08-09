package com.bigtwo.Hands;

import com.bigtwo.Card;
import com.bigtwo.CardGamePlayer;
import com.bigtwo.CardList;

/**
 * This class is a subclass of the com.bigtwo.Hands.Hand class and is used to model
 * and implement a com.bigtwo.Hands.Hand of Full House in a Big Two com.bigtwo.Card game.
 * It is a subclass of abstract class com.bigtwo.Hands.Hand.
 *
 * @author Anmol Gupta
 */
public class FullHouse extends Hand {

    /**
     *
     * A public constructor function that initializes a hand of type Full House for given player and using the given cards
     *
     * @param player The player who owns this hand
     * @param cards The cards used to create this hand
     */
    public FullHouse(CardGamePlayer player, CardList cards) {
        super(player, cards);
        super.setHandLevel(5);
    }

    /**
     *
     * This function overrides the getTopCard function actually declared in the com.bigtwo.Hands.Hand class to specify the top card
     * in a com.bigtwo.Hands.Hand of type Full House
     *
     * @return The top most card in the cards used to create this hand
     */
    @Override
    public Card getTopCard() {
        if(getCard(0).getRank() == getCard(2).getRank() && getCard(3).getRank() == getCard(4).getRank()) {
            return getCard(2);
        }
        if(getCard(0).getRank() == getCard(1).getRank() && getCard(2).getRank() == getCard(4).getRank()) {
            return getCard(4);
        }
        return null;
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
        if (size() != 5) {
            return false;
        }
        if(getCard(0).getRank() == getCard(2).getRank() && getCard(3).getRank() == getCard(4).getRank()) {
            return true;
        }
        if(getCard(0).getRank() == getCard(1).getRank() && getCard(2).getRank() == getCard(4).getRank()) {
            return true;
        }
        return false;
    }

    /**
     *
     * This function overrides the abstract function defined in the com.bigtwo.Hands.Hand class and return the type of the com.bigtwo.Hands.Hand.
     *
     * @return A String indicating the type of the hand.
     */
    @Override
    public String getType() {
        return "com.bigtwo.Hands.FullHouse";
    }
}

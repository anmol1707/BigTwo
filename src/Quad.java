/**
 * This class is a subclass of the Hand class and is used to model
 * and implement a Hand of Quad in a Big Two Card game.
 * It is a subclass of abstract class Hand.
 *
 * @author Anmol Gupta
 */
public class Quad extends Hand {

    /**
     *
     * A public constructor function that initializes a hand of type Quad for given player and using the given cards
     *
     * @param player The player who owns this hand
     * @param cards The cards used to create this hand
     */
    public Quad(CardGamePlayer player, CardList cards) {
        super(player, cards);
        super.setHandLevel(6);
    }

    /**
     *
     * This function overrides the getTopCard function actually declared in the Hand class to specify the top card
     * in a Hand of type Quad
     *
     * @return The top most card in the cards used to create this hand
     */
    @Override
    public Card getTopCard() {
        if(getCard(0).rank == getCard(3).rank) {
            return getCard(3);
        }
        if(getCard(1).rank == getCard(4).rank) {
            return getCard(4);
        }
        return null;
    }

    /**
     *
     * This function overrides the abstract function defined in the Hand class and returns whether the given
     * set of cards can actually form a hand of this type
     *
     * @return A boolean value specifying if the cards given can form the hand or not
     */
    @Override
    public boolean isValid() {
        if (size() != 5) {
            return false;
        }
        if(getCard(0).rank == getCard(3).rank) {
            return true;
        }
        if(getCard(1).rank == getCard(4).rank) {
            return true;
        }
        return false;
    }

    /**
     *
     * This function overrides the abstract function defined in the Hand class and return the type of the Hand.
     *
     * @return A String indicating the type of the hand.
     */
    @Override
    public String getType() {
        return "Quad";
    }
}
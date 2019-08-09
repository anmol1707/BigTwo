/**
 * This class is a subclass of the Hand class and is used to model
 * and implement a Hand of Straight in a Big Two Card game.
 * It is a subclass of abstract class Hand.
 *
 * @author Anmol Gupta
 */
public class Straight extends Hand {

    /**
     *
     * A public constructor function that initializes a hand of type Straight for given player and using the given cards
     *
     * @param player The player who owns this hand
     * @param cards The cards used to create this hand
     */
    public Straight(CardGamePlayer player, CardList cards) {
        super(player, cards);
        super.setHandLevel(3);
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
        return cardsHaveConsecutiveRanks();
    }

    /**
     *
     * This function overrides the abstract function defined in the Hand class and return the type of the Hand.
     *
     * @return A String indicating the type of the hand.
     */
    @Override
    public String getType() {
        return "Straight";
    }
}

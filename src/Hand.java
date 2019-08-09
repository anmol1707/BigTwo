/**
 * This class is an abstract class that defines a structure of a hand. It is inherited by other subclasses
 * which specify the behavior based on the type of the hand./
 *
 * @author Anmol Gupta
 */
public abstract class Hand extends CardList {

    public static void main(String[] args) {
    }

    private CardGamePlayer player; // The player who is playing this hand.
    private int handLevel; // Value indicating the strength of the type of hand
                            // (StraightFlush has highest value while Single has the lowest)

    /**
     *
     * This method is a constructor for building a hand
     * with the specified player and list of cards.
     *
     * @param player The player for whom the hand is being built
     * @param cards The list of cards to be used for creating the hand
     */
    public Hand(CardGamePlayer player, CardList cards) {
        this.player = player;
        removeAllCards();
        for(int i = 0; i < cards.size(); i++) {
            addCard(cards.getCard(i));
        }
    }

    /**
     *
     * This method retrieves the player of this hand
     *
     * @return The player who is playing this hand
     */
    public CardGamePlayer getPlayer() {
        return player;
    }

    /**
     *
     * This method retrieves the top card of this hand.
     *
     * @return The top card of the current hand
     */
    public Card getTopCard() {

        if(size() > 0) {
            return getCard(size() - 1);
        }
        return null;
    }

    /**
     *
     * This method checks if this hand beats a specified hand sent as the parameter
     *
     * @param hand The given hand which is to be checked
     * @return Boolean value specifying which hand wins
     */
    public boolean beats(Hand hand) {
        if(hand.player.equals(this.player)) {
            return true;
        }
        if(this.size() != hand.size()) {
            return false;
        }
        if(this.handLevel < hand.handLevel) {
            return false;
        }
        if(this.handLevel > hand.handLevel) {
            return true;
        }
        if(this.handLevel == 4) {
            if(this.getCard(0).suit > hand.getCard(0).suit) {
                return true;
            }
            if(this.getCard(0).suit < hand.getCard(0).suit) {
                return false;
            }
        }
        return (this.getTopCard()).compareTo(hand.getTopCard()) == 1;
    }

    /**
     *
     * This is an abstract function that just declares a function which is overridden by its child classes and
     * determine whether the given set of cards can form a hand of a specific type
     *
     * @return A boolean value specifying if the cards given can form the hand or not
     */
    public abstract boolean isValid();

    /**
     *
     * This is an abstract function that just declares a function which is overridden by its child classes
     * and returns the type of the Hand.
     *
     * @return A String indicating the type of the hand.
     */
    public abstract String getType();

    protected boolean cardsHaveConsecutiveRanks() {

        if(size() == 0) {
            return false;
        }
        sort();
        for(int i = 0; i < size() - 1; i++) {
            int card1Rank = (((BigTwoCard) getCard(i)).rank + 11) % 13;
            int card2Rank = (((BigTwoCard) getCard(i+1)).rank + 11) % 13;
            if(card1Rank + 1 != card2Rank) {
                return false;
            }
        }
        return true;
    }

    protected boolean cardsHaveSameRank() {
        if(size() == 0) {
            return false;
        }
        int rank = getCard(0).rank;
        for(int i = 1; i < size(); i++) {
            if(getCard(i).rank != rank) {
                return false;
            }
        }
        return true;
    }

    protected boolean cardsHaveSameSuit() {
        if(size() == 0) {
            return false;
        }
        int suit = getCard(0).suit;
        for(int i = 1; i < size(); i++) {
            if(getCard(i).suit != suit) {
                return false;
            }
        }
        return true;
    }

    protected void setHandLevel(int handLevel) {
        this.handLevel = handLevel;
    }
}

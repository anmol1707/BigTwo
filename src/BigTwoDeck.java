/**
 * This class is a subclass of the Deck class and specifies the Deck based on the game Big Two.
 * It is a subclass of abstract class Hand.
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

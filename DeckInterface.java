
/**
 * This is the interface for the deck used by the dealer. This is different than
 * the deck used by the player because the dealer does not care about
 * differences between face cards.
 *
 * @author Thomas Centa
 */
public interface DeckInterface {

  /**
   *
   * @param rank
   *          this is the rank of the card to get the probability of drawing 1
   *          is ace, 2-10 are their numbers and 11 is jack, 12 queen, 13 king.
   * @update this by removing the card with that rank.
   * @return the probability of drawing that rank
   */
  double drawProbability(int rank);

  /**
   * Removes the specified card from the deck.
   *
   * @param rank
   *          The rank of the card to be removed.
   */
  void addCard(int rank);

  /**
   * Removes a card at random from the deck.
   *
   * @return The rank of the card removed
   */
  int removeRandomCard();

  /**
   * Returns the number of cards in the deck with the given rank.
   *
   * @param rank
   *          The rank to check for
   * @return the number of cards with the given rank
   */
  int numCard(int rank);

}
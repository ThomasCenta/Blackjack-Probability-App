
/**
 * This is the interface for the deck used by the dealer. This is different than
 * the deck used by the player because the dealer does not care about
 * differences between face cards.
 *
 * @author Thomas Centa
 */
public interface DealerDeckInterface {

  /**
   *
   * @param rank
   *          this is the rank of the card to get the probability of drawing 1
   *          is ace, 2-10 are their numbers and 10 is also face cards.
   * @return the probability of drawing that rank
   */
  double drawProbability(int rank, int cardsTakenOut, int numRank);

  /**
   * Adds the specified card from the deck.
   *
   * @param rank
   *          The rank of the card to be added.
   */
  void addCard(int rank);

  /**
   * Removes the specified card from the deck.
   *
   * @param rank
   *          The rank of the card to be removed.
   */
  void removeCard(int rank);

  /**
   * Returns the number of cards with the given rank in this.
   *
   * @param rank
   *          the rank in question
   * @return see above.
   */
  int numCardRank(int rank);

  /**
   * Takes cards from the hand out of the deck
   *
   * @param hand
   *          hand to take out of the deck
   * @requires this contains at least one of every card in the hand.
   */
  public void takeOutHand(VariableRankHand hand);

  /**
   * Puts cards in the hand into the deck.
   *
   * @param hand
   *          hand to add into the deck
   */
  public void addHand(VariableRankHand hand);

  int numCardsInDeck();

}
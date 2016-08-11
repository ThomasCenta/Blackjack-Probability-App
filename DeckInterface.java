
/**
 * This is the interface for the deck used by the dealer. This is different than
 * the deck used by the player because the dealer does not care about
 * differences between face cards.
 *
 * @author Thomas Centa
 */
public interface DeckInterface {

  /**
   * does not alter Deck. Has the purpose of telling the probability of drawing
   * a card assuming all cards of a given hand have been taken out. Saves
   * calculation time.
   *
   *
   * @param rank
   *          this is the rank of the card to get the probability of drawing 1
   *          is ace, 2-10 are their numbers - 1 and 10 is jack, 11 queen, 12
   *          king.
   * @param cardsTakenOut
   *          this is the number of cards taken out (hand size)
   * @param numRank
   *          the number of the given rank to take out before calculating
   *          probability (number of that rank in the hand)
   * @return the probability of drawing that rank
   */
  double drawProbability(int rank, int cardsTakenOut, int numRank);

  /**
   * Adds a card of the specified rank to this.
   *
   * @param rank
   *          The rank of the card to be added.
   */
  void addCard(int rank);

  /**
   * Adds a card of the specified rank to this.
   *
   * @requires a card of the specified rank is in this.
   *
   * @param rank
   *          The rank of the card to be added.
   */
  void removeCard(int rank);

  /**
   * Removes a card at random from the deck.
   *
   * @updates this by removing that cards from this
   *
   * @requires there is a card in this.
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

  /**
   * returns the total number of cards in this.
   *
   * @return total number of cards in this.
   */
  public int numCardsInDeck();

  /**
   * Takes cards from the hand out of the deck
   *
   * @param hand
   *          hand to take out of the deck
   * @requires this contains at least one of every card in the hand.
   */
  public void takeOutHand(MinimalHand hand);

  /**
   * Puts cards in the hand into the deck.
   *
   * @param hand
   *          hand to add into the deck
   */
  public void addHand(MinimalHand hand);

}
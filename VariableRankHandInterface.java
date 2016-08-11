/**
 * This will be an interface for the VariableRankHand Class. This class will act
 * as a node for calculations. It will store hands based on both ten ranks and
 * thirteen ranks. Certain functions will operate based on the 13 rank option
 * and other on the 10 rank option. This is to save calculation time by reducing
 * the number of nodes but maintain functionality.
 *
 * @author Thomas Centa
 *
 */
public interface VariableRankHandInterface {

  /**
   * Compares two hands and returns an ordering defined by: going through the
   * ranks from 0 to 12, at the first rank that has a different number of cards
   * between the hands, the hand with fewer of that rank will be considered less
   * than the other hand (ie. gets the -1 value). This comparison bases it off
   * of the 13 rank option of the hand.
   *
   * @param otherHand
   *          other hand to compare to this
   * @return -1 if this is less than otherHand, 0 if equal, 1 if this is greater
   *         than otherHand.
   */
  public int compare13(VariableRankHand otherHand);

  /**
   * Compares two hands and returns an ordering defined by: going through the
   * ranks from 0 to 12, at the first rank that has a different number of cards
   * between the hands, the hand with fewer of that rank will be considered less
   * than the other hand (ie. gets the -1 value). This comparison bases it off
   * of the 10 rank cards.
   *
   * @param otherHand
   *          other hand to compare to this
   * @return -1 if this is less than otherHand, 0 if equal, 1 if this is greater
   *         than otherHand.
   */
  public int compare10(VariableRankHand otherHand);

  /**
   * Sets the nextHand pointer of this reachable by adding one card of the given
   * rank. Takes in ranks 0 <= rank <= 12 but will simplify this to 0 <= rank <=
   * 9 if this has a hand size of 2 or more
   *
   * @param otherHand
   *          The hand to which this.nextHand[nextCardRank] will point to.
   * @param nextCardRank
   *          The rank s.t. this is equivalent to otherHand by adding that rank.
   */
  public void setNextHand(VariableRankHand otherHand, int nextCardRank);

  /**
   * returns a pointer to the next hand reachable by adding nextCardRank to
   * this. Takes in ranks 0 <= rank <= 12 but will simplify this to 0 <= rank <=
   * 9 if this has a hand size of 2 or more
   *
   * @param nextCardRank
   *          the rank s.t. the hand returned is equivalent to this +
   *          nextCardRank.
   * @requires 1 <= nextCardRank <= 12
   * @return the dealerHand equivalent to this + nextCardRank, or null if not
   *         assigned.
   */
  public VariableRankHand getNextHand(int nextCardRank);

  /**
   * Returns the number of the given rank in this
   *
   * @param rank
   *          the rank of the number of cards to return.
   * @requires 0 <= rank <= 12
   * @return the number of cards with rank given in this.
   */
  public int numCardRank13(int rank);

  /**
   * Returns the number of the given rank in this
   *
   * @param rank
   *          the rank of the number of cards to return.
   * @requires 0 <= rank <= 9
   * @return the number of cards with rank given in this.
   */
  public int numCardRank10(int rank);

  /**
   * Returns the blackjack value of this.
   *
   * @return the blackjack value of this.
   */
  public int getHandValue();

  /**
   * adds a card with rank given to this.
   *
   * @param rank
   *          0<= rank <= 12
   */
  public void addCard(int rank);

  /**
   * removes a card with rank given to this.
   *
   * @param rank
   *          0<= rank <= 12
   */
  public void removeCard(int rank);

  /**
   *
   * @return true iff this has an ace valued at 11.
   */
  public boolean getHasAce();

  /**
   *
   * @return current probability assigned to this.
   */
  public double getProbability();

  /**
   * sets the current probability assigned to this.
   *
   * @param prob
   *          probability to assign to this
   */
  public void setCurrentProbability(double prob);

  /**
   * gets the total number of cards in this.
   *
   * @return the number of cards in this.
   */
  public int totalNumCards();

  /**
   * Sets the money made if staying for this hand to the given value.
   *
   * @param money
   *          average money made if staying on this hand.
   */
  public void setMoneyMadeIfStaying(double money);

  /**
   * Sets the money made if hitting for this hand to the given value.
   *
   * @param money
   *          average money made if hitting on this hand.
   */
  public void setMoneyMadeIfHitting(double money);

  /**
   * Sets the money made if staying for this hand to the given value.
   *
   * @param money
   *          average money made if staying on this hand.
   */
  public void setMoneyMadeIfSplitting(double money);

  /**
   * Sets the money made if doubling for this hand to the given value.
   *
   * @param money
   *          average money made if doubling on this hand.
   */
  public void setMoneyMadeIfDoubling(double money);

  /**
   * Gets the money made that was set by previous methods if staying on this
   * hand.
   *
   * @return the money made if staying that was set to this
   */
  public double getMoneyMadeIfStaying();

  /**
   * Gets the money made that was set by previous methods if hitting on this
   * hand.
   *
   * @return the money made if hitting that was set to this
   */
  public double getMoneyMadeIfHitting();

  /**
   * Gets the money made that was set by previous methods if splitting on this
   * hand.
   *
   * @return the money made if splitting that was set to this
   */
  public double getMoneyMadeIfSplitting();

  /**
   * Gets the money made that was set by previous methods if doubling on this
   * hand.
   *
   * @return the money made if doubling that was set to this
   */
  public double getMoneyMadeIfDoubling();

  /**
   * Returns the most money made
   *
   * @return the most money made by any move on average.
   */
  public double getMostMoneyMade();

  /**
   * Setter for the rank to split this hand.
   *
   * @param rankSplitOn
   *          the rank this hand was split on.
   */
  public void setSplitHand(int rankSplitOn);

  /**
   * Getter for the rank this hand was split on.
   *
   * @return the rank this hand was split on. -1 if not split
   */
  public int getRankSplitOn();

}

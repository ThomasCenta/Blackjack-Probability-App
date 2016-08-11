
/**
 * This will have the job of storing VariableRankHands and searching for them
 * quickly. This will store them based on both their 13 ranks and their 10 ranks
 *
 * @author Thomas
 *
 */

public interface HandContainerInterface {

  /**
   * Adds the given hand to this. If the given hand is already in this, a
   * duplicate will NOT be added. NO HAND FOR YOU!
   *
   * @param toAdd
   *          the hand to add to this.
   */
  public void addHand(VariableRankHand toAdd);

  /**
   * Returns a pointer to the hand that is equivalent to the given hand based on
   * the 10 rank version of a hand. Returns null if that hand is not in this.
   *
   * @param equivalentHand
   *          hand that has the same cards as the desired hand.
   * @return a pointer to the hand in this that is equivalent to the one passed
   *         in or null if none exists.
   */
  public VariableRankHand getHand10(MinimalHand equivalentHand);

  /**
   * Returns a pointer to the hand that is equivalent to the given hand based on
   * the 13 rank version of a hand. Returns null if that hand is not in this.
   *
   * @param equivalentHand
   *          hand that has the same cards as the desired hand.
   * @return a pointer to the hand in this that is equivalent to the one passed
   *         in or null if none exists.
   */
  public VariableRankHand getHand13(MinimalHand equivalentHand);

  /**
   * Sets all the probabilities in this to zero.
   */
  public void setProbabilitiesToZero();

}
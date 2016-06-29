
/**
 * This will be the interface for the class responsible for calculating the
 * probability of the dealer getting each type of hand.
 *
 * @author Thomas Centa
 *
 */
public interface DealerProbabilitiesInterface {

  /**
   *
   * @param deck
   *    The deck at its current hand (players and dealers cards taken out)
   * @param initialHand
   *    An array of size 10 where index i corresponds to the number of cards with rank i + 1.
   *    Aces have rank 1, face cards have rank 10, number cards have rank as their number.
   * @return
   */
  double[] getProbabilities(Deck deck, int[] initialHand)

}

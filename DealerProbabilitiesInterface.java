
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
   *          The deck at its current hand (players cards taken out. Not dealers
   *          cards)
   * @param initialHand
   *          An array of size 10 where index i corresponds to the number of
   *          cards with rank i + 1. Aces have rank 1, face cards have rank 10,
   *          number cards have rank as their number.
   * @param rules
   *          rules of the game to follow.
   * @return an array of probabilities for the possible dealer results these go
   *         in order: 17,18,19,20, blackjack, non-natural 21, bust.
   */
  double[] getProbabilities(DealerDeck deck, VariableRankHand startingHand, Rules rules);

}

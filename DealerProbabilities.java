/**
 *
 * @author Thomas Centa
 *
 */

public class DealerProbabilities implements DealerProbabilitiesInterface {

  private class Hand {
    /**
     * The probability of getting this hand. Updated during calculations.
     */
    double currentProbability;

    /**
     * The current value of the hand by blackjack rules (ie. 2 - 21).
     */
    int handValue;

    /**
     * says whether or not this hand has an ace VALUED AT 11.
     */
    boolean hasAce;

    /**
     * An array of size 10 with each index i corresponding to the number of
     * cards in this hand with rank i + 1.
     */
    int[] cardsInHand;

    /**
     * Pointers to hands reachable by adding one card to this. If a hand is at a
     * stand position, does not point to other hands.
     */
    Hand[] nextHands;

  }

  Hand emptyHand;

  private static boolean compare

  /**
   *
   * @param startingHand
   * @param targetHand
   * @requires targetHand is reachable from startingHand by adding cards to
   *           startingHand.
   * @return true if the startingHand has in its tree the hand equivalent of
   *         targetHand (ie. both hands have the same cards).
   */
  private static boolean hasHand(Hand startingHand, Hand targetHand) {
    boolean reachable = true;
    boolean equal = true;
    int nextCardRank = -1;
    for (int i = 0; i < startingHand.cardsInHand.length; i++) {
      if (startingHand.cardsInHand[i] < targetHand.cardsInHand[i]) {
        reachable = false;
        equal = false;
      }else if(startingHand.cardsInHand[i] > targetHand.cardsInHand[i]){
        nextCardRank = i + 1;
        equal = false;
      }
    }
    assert reachable : "target hand is not reachable from starting hand";

    if(equal){
      return true;
    }else if (startingHand.nextHands.length < nextCardRank - 1){
      return false;
    }else{
      Hand
    }

    return false;
  }

  /**
   * Default Constructor. Will create tree of hands assuming hitting on soft 17.
   */
  public DealersProbabilities(){
    int currentValue = 0;


  }

  @Override
  public double[] getProbabilities(Deck deck, int[] initialHand,
      boolean hitOnSoft17) {

    return null;
  }

}
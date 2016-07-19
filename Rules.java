/**
 * This is to store all the rules that are implemented on a given table.
 *
 * @author Thomas Centa
 *
 */

public class Rules {

  private boolean hitOnSoft17;

  private int numTimesAllowedSplitting;
  private int numTimesAllowedSplittingAces;
  private boolean blackjackAfterSplittingAces;
  private double blackjackPayout;
  private boolean noHitSplitAce;
  private int[] doubleHardValuesAllowed;

  Rules(boolean hitOnSoft17, int numTimesAllowedSplitting, int numTimesAllowedSplittingAces,
      boolean blackjackAfterSplittingAces, double blackjackPayout, boolean noHitSplitAces,
      int[] doubleHardValuesAllowed) {

    this.hitOnSoft17 = hitOnSoft17;
    this.numTimesAllowedSplitting = numTimesAllowedSplitting;
    this.numTimesAllowedSplittingAces = numTimesAllowedSplittingAces;
    this.blackjackAfterSplittingAces = blackjackAfterSplittingAces;
    this.blackjackPayout = blackjackPayout;
    this.noHitSplitAce = this.noHitSplitAce;
    int[] copy = new int[doubleHardValuesAllowed.length];
    for (int i = 0; i < copy.length; i++) {
      copy[i] = doubleHardValuesAllowed[i];
    }
    this.doubleHardValuesAllowed = copy;

  }

  /**
   *
   * @param dealerHand
   *          the hand for which this determines whether or not should stay.
   * @return true if dealer stays according to hand and rules.
   */
  public boolean dealerStays(VariableRankHand dealerHand) {

    if (dealerHand.getHandValue() >= 18) {
      return true;
    } else if (dealerHand.getHandValue() == 17 && dealerHand.getHasAce() && this.hitOnSoft17) {
      return false;
    } else if (dealerHand.getHandValue() == 17) {
      return true;
    } else { // handValue < 17
      return true;
    }
  }

  /**
   * Need to look up the rules for this on splitting
   *
   * @param hand
   *          hand that wants to be doubled on
   * @param numTimesSplit
   *          number of times this hand has been split.
   * @return true if this hand can split
   */
  public boolean allowedToDouble(VariableRankHand hand, int numTimesSplit) {

    boolean canDouble = this.doubleHardValuesAllowed.length == 0;
    if (hand.totalNumCards() < 2) { // can't double
      canDouble = false;
    }
    for (int i = 0; i < this.doubleHardValuesAllowed.length; i++) {
      if (hand.getHandValue() == this.doubleHardValuesAllowed[i]) {
        canDouble = true;
      }
    }
    return canDouble;
  }

  /**
   *
   * @param playerHand
   *          hand that is staying
   * @param dealerValues
   *          an array of probabilities for the possible dealer results these go
   *          in order: 17,18,19,20, blackjack, non-natural 21, bust. This is an
   *          array that says how probable each is.
   * @requires the probabilities in dealerValues add to ~1.
   * @return the average money made by staying on a bet of $1
   */
  public double moneyMadeOnStaying(VariableRankHand playerHand, double[] dealerValues) {
    double sum = 0;
    for (int i = 0; i < dealerValues.length; i++) {
      sum += dealerValues[i];
    }
    assert dealerValues.length == 7 : "dealer values has length " + dealerValues.length + ", not 7";
    assert sum > 0.999 && sum < 1.001 : "dealer probabilities add to " + sum + " not 1";

    double moneyMade = 0;
    if (playerHand.getHandValue() > 21) {
      moneyMade = -1;
    } else if (playerHand.getHandValue() == 21 && playerHand.totalNumCards() == 2
        && (this.blackjackAfterSplittingAces || playerHand.getRankSplitOn() != 0)) {
      // player has a blackjack
      for (int i = 0; i < dealerValues.length; i++) {
        if (i != 4) { // dealer also has blackjack on i == 4
          moneyMade += this.blackjackPayout * dealerValues[i];
        }
      }
    } else if (playerHand.getHandValue() == 21) { // non-blackjack 21
      for (int i = 0; i < dealerValues.length; i++) {
        if (i == 4) {
          moneyMade -= dealerValues[4];
        } else if (i != 5) { // this is a push
          moneyMade += dealerValues[i];
        }
      }
    } else { // player has hand value < 21
      for (int i = 0; i < 4; i++) {
        if (playerHand.getHandValue() > i + 17) {
          moneyMade += dealerValues[i];
        } else if (playerHand.getHandValue() < i + 17) {
          moneyMade -= dealerValues[i];
        }
      }
      moneyMade -= dealerValues[4];
      moneyMade -= dealerValues[5];
      moneyMade += dealerValues[6];
    }
    return moneyMade;
  }

  // public boolean allowedToHit(VariableRankHand hand, int numTimesSplit){
  // need to work this one out? probably not worth the hassle

  /**
   * Returns whether or not the hand given can be split given the rules in this.
   *
   * @param hand
   *          the hand that is being determined whether or not it can split.
   * @param numTimesSplit
   *          the number of times the payer has already split his hand.
   * @return true if the player is allowed to split.
   */
  public boolean allowedToSplit(VariableRankHand hand, int numTimesSplit) {
    boolean splittable = false;
    if (hand.totalNumCards() == 2) {
      for (int i = 0; i < 13; i++) {
        if (hand.numCardRank13(i) == 2) {
          if (i == 0) {
            if (this.numTimesAllowedSplittingAces > numTimesSplit) {
              splittable = true;
            }
          } else { // not an ace
            if (this.numTimesAllowedSplitting > numTimesSplit) {
              splittable = true;
            }
          }
        }
      }
    }
    return splittable;
  }

}
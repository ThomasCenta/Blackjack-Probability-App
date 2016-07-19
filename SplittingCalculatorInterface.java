public interface SplittingCalculatorInterface {

  /**
   *
   * @param splittingRank
   *          the rank of the card that is being split.
   * @param handNumber
   *          the split hand number this is. 2 if split only once, 3 if twice, 4
   *          if thrice.
   * @param deck
   * @param hitOnSoft17
   * @param doubleHardValuesAllowed
   * @param blackjackPayout
   * @param numTimesAllowedSplitting
   * @param numTimesSplittingAces
   * @param noHitSplitAce
   * @param blackjackAfterSplitting
   * @return
   */
  public double expectedSplitting(int splittingRank, Deck deck,
      boolean hitOnSoft17, int[] doubleHardValuesAllowed,
      double blackjackPayout, int numTimesAllowedSplitting,
      int numTimesSplittingAces, boolean noHitSplitAce,
      boolean blackjackAfterSplitting);

}
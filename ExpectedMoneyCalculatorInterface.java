
/**
 * This is the interface for ExpectedMoneyCalculator. This class will be
 * responsible for calculating the expected money made for any given hand and
 * set of rules. When instantiated, this will create a tree of hands for use in
 * calculations. This tree will have calculate the staying probabilities given a
 * deck and the calculations will be doen based on that. The staying
 * probabilities will have to be recalculated everytime a new deck is used.
 *
 * @author Thomas
 *
 */
public interface ExpectedMoneyCalculatorInterface {

  /**
   * sets the values for hitting, staying, doubling, and splitting to the values
   * of the equivalent hand in this.
   *
   * @param toFind
   *          hand to find. Uses the 10 card equivalent if it has > 2 cards.
   * @updates toFind
   * @requires toFind is not a bust (ie. value > 21)
   */
  public void getHand(VariableRankHand toFind);

  /**
   * Returns a string corresponding to the best move for toFind given the
   * calculations in this.
   *
   * @param toFind
   *          the equivalent hand to find in this. Bases it off of 10 card ranks
   *          if size <= 2. 13 ranks otherwise.
   * @return "stay", "hit", "split", or "double"
   */
  public String getBestMove(MinimalHand toFind);

  /**
   * Going to assume the hand passed in has not been split before.
   *
   * @param deck
   *          original deck with no cards of either hand taken out.
   * @param startingHand
   *          starting hand to calculate from
   * @param dealerHand
   *          hand of the dealer.
   * @param rules
   *          the rules object corresponding to this game of blackjack.
   */
  public void setMoney(Deck deck, VariableRankHand playerHand, VariableRankHand dealerHand, Rules rules,
      boolean withSplitting, int desiredNumSimulations);

  /**
   * sets the money made on splitting for the hand made when splitting on the
   * rank rankSplitOn. WILL ALSO UPDATE THE HITTING VALUES FOR THE HANDS OF SIZE
   * ONE AND ZERO as these are affected by splitting.
   *
   * @param rankSPlitOn
   *          the rank that the hand is split on which is about to have its
   *          money set.
   * @param moneyMadeOnSplitting
   *          the money made on average when the given hand is split.
   * @param deck
   *          this is the deck with the dealer cards taken out.
   */
  public void setSplitting(int rankSplitOn, double moneyMadeOnSplitting, Deck deck);

}
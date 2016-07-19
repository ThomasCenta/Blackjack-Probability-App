
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
   *
   * @param toFind
   *          hand to find. Uses the 10 card equivalent if it has > 2 cards.
   * @requires toFind is not a bust (ie. value > 21)
   * @return pointer to that hand (presumably with calculations done).
   */
  public VariableRankHand getHand(VariableRankHand toFind);

  /**
   * Going to assume the hand passed in has not been split
   *
   * @param deck
   *          deck with no cards from the hand taken out, but with cards from
   *          the dealer taken out.
   * @param startingHand
   *          starting hand to calculate from
   * @param dealerHand
   *          hand of the dealer.
   * @param rules
   *          the rules object corresponding to this game of blackjack.
   */
  public void setMoney(Deck deck, VariableRankHand playerHand, VariableRankHand dealerHand, Rules rules);

}
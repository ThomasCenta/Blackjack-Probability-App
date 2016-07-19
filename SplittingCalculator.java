import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class SplittingCalculator implements SplittingCalculatorInterface {

  HandContainer allHands;

  DealerProbabilities dealerCalculator;

  /**
   * Helper function
   *
   * @param splittingRank
   * @param handNumber
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
  private double expectedValue(int splittingRank, int handNumber, Deck deck, boolean hitOnSoft17,
      int[] doubleHardValuesAllowed, double blackjackPayout, int numTimesAllowedSplitting, int numTimesSplittingAces,
      boolean noHitSplitAce, boolean blackjackAfterSplitting) {

    VariableRankHand startingHand = new VariableRankHand();
    startingHand.addCard(splittingRank);
    startingHand = this.allHands.getHand13(startingHand);
    assert startingHand != null : "hand not found for splitting calculations.";
    // only going to expand hands related to startingHand
    Queue<VariableRankHand> toExpand = new LinkedList<VariableRankHand>();
    // have to calculate double average starting with greatest hand size
    Stack<VariableRankHand> calculateDouble = new Stack<VariableRankHand>();
    // go through the starting hand first and don't add the same rank
    for (int i = 0; i < 13; i++) {
      if (i == splittingRank) {
        continue;
      } else {
        VariableRankHand temp = new VariableRankHand(startingHand);
        temp.addCard(i);

      }
    }

    toExpand.add(startingHand);
    while (!toExpand.isEmpty()) {
      VariableRankHand next = toExpand.remove();
      calculateDouble.push(next);

      // take the cards in the hand out of the deck for dealer calculations
      DealerDeck copy = new DealerDeck(deck);
      for (int i = 0; i < 10; i++) {
        for (int j = 0; j < next.numCardRank10(i); j++) {
          copy.removeCard(i);
        }
      }
      // assign staying average to next
      double[] dealerProbs = this.dealerCalculator.getProbabilities(copy, next, hitOnSoft17);
      double avgMoney = 0.0;
      if (next.getHandValue() <= 20) {
        for (int i = 0; i <= 3; i++) {
          if (next.getHandValue() < i + 17) {
            avgMoney -= dealerProbs[i];
          } else if (next.getHandValue() > i + 17) {
            avgMoney += dealerProbs[i];
          } // do nothing on push
        }
        for (int i = 4; i < 7; i++) {
          avgMoney -= dealerProbs[i];
        }
      } else if (next.getHandValue() == 21 && next.totalNumCards() == 2) {
        for (int i = 0; i < 7; i++) {
          avgMoney += blackjackPayout * dealerProbs[i];
        }
        avgMoney -= blackjackPayout * dealerProbs[4]; // push on blackjack
      } else if (next.getHandValue() == 21 && next.totalNumCards() != 2) {
        for (int i = 0; i < 7; i++) {
          avgMoney += dealerProbs[i];
        }
        avgMoney -= 2 * dealerProbs[4]; // lose on blackjack
        avgMoney -= dealerProbs[5]; // tie on dealer 21
      } else { // player busted
        avgMoney = -1;
      }
      next.setMoneyMadeIfStaying(avgMoney);
      // now go on to the hands pointing to this one.
      if (next.getHandValue() < 21) { // non-leaf node
        for (int i = 0; i < 13; i++) {
          VariableRankHand temp = new VariableRankHand(next);
          temp.addCard(i);

        }
      }

    }

    // just so style check doesn't hate me
    return 0;

  }

  public SplittingCalculator() {

    // delete this later
    int[] numSizeHand = new int[25];
    int numHands = 0;

    this.dealerCalculator = new DealerProbabilities();

    this.allHands = new HandContainer();
    Queue<VariableRankHand> toExpand = new LinkedList<VariableRankHand>();
    VariableRankHand emptyHand = new VariableRankHand();
    toExpand.add(emptyHand);
    this.allHands.addHand(emptyHand);
    while (!toExpand.isEmpty()) {
      VariableRankHand next = toExpand.remove();

      // delete this later
      numHands++;
      numSizeHand[next.totalNumCards()]++;

      // if player has >= 21, no need to continue
      if (next.getHandValue() >= 21) { // assuming no one wants to hit on 21 or
                                       // bust
        continue;
      }
      for (int i = 0; i < 13; i++) {
        VariableRankHand createHand = new VariableRankHand(next);
        createHand.addCard(i);
        VariableRankHand existingHand;
        if (createHand.totalNumCards() <= 2) {
          existingHand = this.allHands.getHand13(createHand);
        } else {
          existingHand = this.allHands.getHand10(createHand);
        }
        if (existingHand == null) {
          // since this hand is new, add it to the queue
          toExpand.add(createHand);
          this.allHands.addHand(createHand);
        } else {
          createHand = existingHand;
        }
        // the next hand is made, add it to the pointers
        // VariableRankHand methods already take care of 10 and 13 rank
        // differences w.r.t. pointers.
        next.setNextHand(createHand, i);
      }

    }

    System.out.println("Number of Split Hands: " + numHands + ". Of each size: ");
    for (int i = 0; i < numSizeHand.length; i++) {
      System.out.print(numSizeHand[i] + " ");
    }
    System.out.println();

  }

  @Override
  public double expectedSplitting(int splittingRank, Deck deck, boolean hitOnSoft17, int[] doubleHardValuesAllowed,
      double blackjackPayout, int numTimesAllowedSplitting, int numTimesSplittingAces, boolean noHitSplitAce,
      boolean blackjackAfterSplitting) {

    // the goal is to fill this with expected values for the hand to be split.
    // Expected values found according to the rules in the paper.
    double[] moneyMadeOnSplitting;

    return 0;
  }

}
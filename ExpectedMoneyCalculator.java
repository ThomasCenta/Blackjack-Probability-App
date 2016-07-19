import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ExpectedMoneyCalculator implements ExpectedMoneyCalculatorInterface {

  /**
   * this will contain all the hands created for the calculations.
   */
  private HandContainer allHands;

  /**
   * This will be used to calculate dealer probabilities when staying.
   */
  private DealerProbabilities dealerCalculator;

  /**
   * default constructor.
   */
  public ExpectedMoneyCalculator() {
    this.allHands = new HandContainer();
    this.dealerCalculator = new DealerProbabilities();

    Queue<VariableRankHand> toExpand = new LinkedList<VariableRankHand>();
    VariableRankHand emptyHand = new VariableRankHand();
    toExpand.add(emptyHand);
    this.allHands.addHand(emptyHand);

    while (!toExpand.isEmpty()) {
      VariableRankHand next = toExpand.remove();

      // if player has >= 21, no need to continue
      if (next.getHandValue() >= 21) {
        // assuming no one wants to hit on 21 or bust
        continue;
      }
      for (int i = 0; i < 13; i++) { // add a card for each draw chance
        VariableRankHand createHand = new VariableRankHand(next);
        createHand.addCard(i);
        VariableRankHand existingHand = null;
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

  }

  @Override
  public VariableRankHand getHand(VariableRankHand toFind) {
    assert toFind.getHandValue() <= 21 : "Don't pass in a busted hand";
    if (toFind.totalNumCards() <= 2) {
      return this.allHands.getHand13(toFind);
    } else {
      return this.allHands.getHand10(toFind);
    }
  }

  @Override
  public void setMoney(Deck deck, VariableRankHand startingHand, VariableRankHand dealerHand, Rules rules) {

    assert startingHand.getHandValue() <= 21 : "Don't pass in busted hands";
    // this is to keep track of what hands have been processed
    HandContainer handTracker = new HandContainer();

    if (startingHand.totalNumCards() <= 2) {
      startingHand = this.allHands.getHand13(startingHand);
    } else {
      startingHand = this.allHands.getHand10(startingHand);
    }
    assert startingHand != null : "hand not found for calculations.";
    // only going to expand hands related to startingHand
    Queue<VariableRankHand> toExpand = new LinkedList<VariableRankHand>();
    // have to calculate double and hit average starting with greatest hand size
    Stack<VariableRankHand> calculateDouble = new Stack<VariableRankHand>();
    Stack<VariableRankHand> calculateHitting = new Stack<VariableRankHand>();

    toExpand.add(startingHand);
    while (!toExpand.isEmpty()) {
      VariableRankHand next = toExpand.remove();

      handTracker.addHand(next);

      // take the cards in the hand out of the deck for dealer calculations
      DealerDeck copy = new DealerDeck(deck);
      for (int i = 0; i < 10; i++) {
        for (int j = 0; j < next.numCardRank10(i); j++) {
          copy.removeCard(i);
        }
      }
      // assign staying average to next
      double[] dealerProbs = this.dealerCalculator.getProbabilities(copy, dealerHand, rules);
      double avgMoney = rules.moneyMadeOnStaying(next, dealerProbs);

      next.setMoneyMadeIfStaying(avgMoney);
      // now go on to the hands pointing to this one.
      if (next.getHandValue() < 21) { // non-leaf node
        // only need to calculate hitting and double for non-leaf nodes. (why
        // would you double or hit on bust or blackjack?)
        calculateDouble.push(next);
        calculateHitting.push(next);

        for (int i = 0; i < 13; i++) {
          VariableRankHand temp = next.getNextHand(i);

          // THIS IS THE PLACE TO TEST AND CALCULATE SPLITTING

          VariableRankHand existingHand = null;
          if (temp.totalNumCards() <= 2) {
            existingHand = handTracker.getHand13(temp);
          } else {
            existingHand = handTracker.getHand10(temp);
          }
          if (existingHand == null) {
            handTracker.addHand(temp);
            toExpand.add(temp);
          }
        }
      }
    }
    // now all hands have expected money made if staying. Next is doubling.
    while (!calculateDouble.isEmpty()) {
      VariableRankHand next = calculateDouble.pop();
      if (rules.allowedToDouble(next, 0)) {
        boolean found = false;
        for (int i = 0; i < doubleHardValuesAllowed.length; i++) {
          if (doubleHardValuesAllowed[i] == next.getHandValue()) {
            found = true;
          }
        }
        if (!found) {
          continue; // this is not allowed to double so don't bother with it.
        }
      }
      // need to have at least 2 cards to double down
      if (next.totalNumCards() < 2) {
        continue; // just skip this hand
      }
      if (next.getHandValue() >= 21) { // not advised to double on this...
        next.setMoneyMadeIfDoubling(-2); // lose double your money.
      } else {
        double avgMoney = 0;
        for (int i = 0; i < 13; i++) {
          // there is not really a difference for face cards, but to keep the
          // code simple, just do it for all codes (still works out)
          double drawProbability = deck.drawProbability(i, next.totalNumCards(), next.numCardRank13(i));
          avgMoney += 2 * drawProbability * next.getNextHand(i).getMoneyMadeIfStaying();
        }
        next.setMoneyMadeIfDoubling(avgMoney);
      }
    }
    while (!calculateHitting.isEmpty())

    {
      VariableRankHand next = calculateHitting.pop();

      if (next.getHandValue() >= 21) { // not advised to hit on this...
        next.setMoneyMadeIfHitting(-1); // lose your money.
      } else {
        double avgMoney = 0;
        for (int i = 0; i < 13; i++) {
          // there is not really a difference for face cards, but to keep the
          // code simple, just do it for all codes (still works out)
          double drawProbability = deck.drawProbability(i, next.totalNumCards(), next.numCardRank13(i));
          avgMoney += drawProbability * next.getNextHand(i).getMostMoneyMade();
        }
        next.setMoneyMadeIfHitting(avgMoney);
      }
    }
  }

}
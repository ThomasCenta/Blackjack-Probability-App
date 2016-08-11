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
   * This is used for splitting calculations.
   */
  private SplittingMoneyCalculator splittingCalculator;

  /**
   * default constructor.
   */
  public ExpectedMoneyCalculator() {
    this.allHands = new HandContainer();
    this.dealerCalculator = new DealerProbabilities();
    this.splittingCalculator = new SplittingMoneyCalculator();

    Queue<VariableRankHand> toExpand = new LinkedList<VariableRankHand>();
    VariableRankHand emptyHand = new VariableRankHand();
    toExpand.add(emptyHand);
    this.allHands.addHand(emptyHand);

    while (!toExpand.isEmpty()) {
      VariableRankHand next = toExpand.remove();

      // if player has > 21, no need to continue
      if (next.getHandValue() > 21) {
        // assuming no one wants to hit on bust
        continue;
      }
      for (int i = 0; i < 13; i++) { // add a card for each draw chance
        VariableRankHand createHand = new VariableRankHand(next, true);
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
  public void getHand(VariableRankHand toFind) {
    assert toFind.getHandValue() <= 21 : "Don't pass in a busted hand: " + toFind.toString();
    VariableRankHand calculated = null;
    if (toFind.totalNumCards() <= 2) {
      calculated = this.allHands.getHand13(toFind);
    } else {
      calculated = this.allHands.getHand10(toFind);
    }
    assert calculated != null : "could not find hand " + toFind.toString();
    toFind.setMoneyMadeIfDoubling(calculated.getMoneyMadeIfDoubling());
    toFind.setMoneyMadeIfHitting(calculated.getMoneyMadeIfHitting());
    toFind.setMoneyMadeIfStaying(calculated.getMoneyMadeIfStaying());
    toFind.setMoneyMadeIfSplitting(calculated.getMoneyMadeIfSplitting());
  }

  @Override
  public void setMoney(Deck deck, VariableRankHand startingHand, VariableRankHand dealerHand, Rules rules,
      boolean withSplitting, int desiredNumSimulations) {

    assert startingHand.getHandValue() <= 21 : "Don't pass in busted hands";

    // this is to keep track of what hands have been processed
    HandContainer handTracker = new HandContainer();

    for (int i = 0; i < 13; i++) {
      assert deck.numCard(i) >= (startingHand.numCardRank13(i)
          + dealerHand.numCardRank13(i)) : "deck does not have the cards in the hands";
    }

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
      // assign staying average to next
      double[] dealerProbs = this.dealerCalculator.getProbabilities(new DealerDeck(deck), next, dealerHand, rules);
      double avgMoney = rules.moneyMadeOnStaying(next, dealerProbs);

      if (withSplitting && rules.allowedToSplit(next, 0)) {
        Deck splittingDeck = new Deck(deck);
        assert next.totalNumCards() == 2 : "splitting hands doesn't have two cards";
        int rankSplitOn = -1;
        for (int i = 0; i < 13; i++) {
          if (next.numCardRank13(i) == 2) {
            rankSplitOn = i;
          }
        }
        assert rankSplitOn != -1 : "splitting hand is not a pair";
        double splitting2 = this.splittingCalculator.moneyMadeSplittingSimulation(splittingDeck, rankSplitOn, 0,
            dealerHand, rules, desiredNumSimulations);
        next.setMoneyMadeIfSplitting(splitting2);
      }

      next.setMoneyMadeIfStaying(avgMoney);
      // now go on to the hands pointing to this one.
      if (next.getHandValue() <= 21) { // non-leaf node
        // only need to calculate hitting and double for non-leaf nodes. (why
        // would you double or hit on bust or blackjack?)
        calculateDouble.push(next);
        calculateHitting.push(next);

        for (int i = 0; i < 13; i++) {

          int totalCardsInHands = next.totalNumCards() + dealerHand.totalNumCards();
          int numCardiInHands = next.numCardRank13(i) + dealerHand.numCardRank13(i);
          double prob = deck.drawProbability(i, totalCardsInHands, numCardiInHands);
          if (prob > 0) {// only going to go to a hand if it can be drawn.
            VariableRankHand temp = next.getNextHand(i);

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
    }
    // now all hands have expected money made if staying. Next is doubling.
    while (!calculateDouble.isEmpty()) {
      VariableRankHand next = calculateDouble.pop();
      if (rules.allowedToDouble(next)) {
        if (next.getHandValue() > 21) { // not advised to double on this...
          next.setMoneyMadeIfDoubling(-2); // lose double your money.
        } else {
          double avgMoney = 0;
          for (int i = 0; i < 13; i++) {
            int totalCardsInHands = next.totalNumCards() + dealerHand.totalNumCards();
            int numCardiInHands = next.numCardRank13(i) + dealerHand.numCardRank13(i);
            double drawProbability = deck.drawProbability(i, totalCardsInHands, numCardiInHands);
            avgMoney += 2 * drawProbability * next.getNextHand(i).getMoneyMadeIfStaying();
          }
          next.setMoneyMadeIfDoubling(avgMoney);
        }
      }
    }
    while (!calculateHitting.isEmpty()) {
      VariableRankHand next = calculateHitting.pop();

      if (next.getHandValue() > 21) { // hitting on a busted hand...
        next.setMoneyMadeIfHitting(-1); // lose your money.
      } else {
        double avgMoney = 0;
        for (int i = 0; i < 13; i++) {
          // there is not really a difference for face cards, but to keep the
          // code simple, just do it for all codes (still works out)
          int totalCardsInHands = next.totalNumCards() + dealerHand.totalNumCards();
          int numCardiInHands = next.numCardRank13(i) + dealerHand.numCardRank13(i);
          double drawProbability = deck.drawProbability(i, totalCardsInHands, numCardiInHands);
          avgMoney += drawProbability * next.getNextHand(i).getMostMoneyMade();
        }
        next.setMoneyMadeIfHitting(avgMoney);
      }
    }
  }

  @Override
  public void setSplitting(int rankSplitOn, double moneyMadeOnSplitting, Deck deck) {
    VariableRankHand splitHand = new VariableRankHand();
    splitHand.addCard(rankSplitOn);
    splitHand.addCard(rankSplitOn);
    splitHand = this.allHands.getHand13(splitHand);
    splitHand.setMoneyMadeIfSplitting(moneyMadeOnSplitting);
    if (splitHand.getBestMove().equals("split")) {
      // only change prior if splitting is best option.
      VariableRankHand priorHand = new VariableRankHand();
      priorHand.addCard(rankSplitOn);
      priorHand = this.allHands.getHand13(priorHand);
      if (priorHand.getNextHand(rankSplitOn) != splitHand) {
        assert false : "hand not pointed to properly";
      }
      double avgMoney = 0;
      for (int i = 0; i < 13; i++) {
        double drawProbability = deck.drawProbability(i, 1, priorHand.numCardRank13(i));
        avgMoney += drawProbability * priorHand.getNextHand(i).getMostMoneyMade();
      }
      priorHand.setMoneyMadeIfHitting(avgMoney);
    }
  }

  @Override
  public String getBestMove(MinimalHand toFind) {
    if (toFind.totalNumCards() <= 2) {
      return this.allHands.getHand13(toFind).getBestMove();
    } else {
      return this.allHands.getHand10(toFind).getBestMove();
    }
  }

}
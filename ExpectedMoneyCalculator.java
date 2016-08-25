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
   * Going to assume this is the players first time splitting
   *
   * @param deck
   *          deck without dealer
   * @param rankSplitOn
   * @param dealerHand
   * @param rules
   * @param desiredNumSimulations
   * @param playerAid
   *          needs this to have hands all hands, except the empty hand,
   *          calculated for double, hit, and stay.
   * @return
   */
  private static double splittingCalculator(Deck deck, int rankSplitOn, VariableRankHand dealerHand, Rules rules,
      int desiredNumSimulations, ExpectedMoneyCalculator originalAid) {

    // finish this startingHands thing later.
    VariableRankHand playersFirstHand = new VariableRankHand();
    playersFirstHand.addCard(rankSplitOn);
    playersFirstHand.setSplitHand(rankSplitOn);
    ExpectedMoneyCalculator[] playerAid = null;
    int deckAllowsSplit = deck.numCard(rankSplitOn) - dealerHand.numCardRank13(rankSplitOn) - 1;
    if (rankSplitOn == 0) {
      int rulesSplit = rules.numTimesAllowedToSplitAces();
      int canSplit = Math.min(deckAllowsSplit, rulesSplit);
      playerAid = new ExpectedMoneyCalculator[canSplit];
    } else {
      int canSplit = Math.min(deckAllowsSplit, rules.numTimesAllowedToSplitAces());
      playerAid = new ExpectedMoneyCalculator[canSplit];
    }
    for (int i = 0; i < playerAid.length; i++) {
      playerAid[i] = new ExpectedMoneyCalculator(originalAid);
    }
    // These queues will store finished hands in the simulations. They can be
    // reused so declare them now to save a small amount of time.
    Queue<MinimalHand> finishedHands = new LinkedList<MinimalHand>();
    Queue<Double> finishedHandBets = new LinkedList<Double>();
    // this will be the dealer object used in the simulation.
    Dealer dealer = new Dealer();
    // empty hand will be needed for calculations
    MinimalHand emptyHand = new MinimalHand();
    /**
     * now for the simulations, notice that playing three hands may have the
     * option to split into four. To make that decision, knowledge must be known
     * about the money made by playing four hands. So this will start at the max
     * number of hands and work its way down to two.
     */
    for (int startingNumHands = playerAid.length + 1; startingNumHands >= 2; startingNumHands--) {
      // start by making a copy of the deck and taking the cards in the hand
      // out.
      Deck noHandsDeck = new Deck(deck);
      for (int j = 0; j < startingNumHands; j++) {
        noHandsDeck.removeCard(rankSplitOn); // take one out for each hand.
      }
      double moneyMade = 0;
      int currentNumSimulations = 0;
      while (currentNumSimulations < desiredNumSimulations) {
        Deck currentDeck = new Deck(noHandsDeck);
        int numHandsPlaying = startingNumHands;
        Queue<MinimalHand> handsToPlay = new LinkedList<MinimalHand>();
        for (int j = 0; j < startingNumHands; j++) {
          MinimalHand randomStart = new MinimalHand(playersFirstHand);
          randomStart.addCard(currentDeck.removeRandomCard());
          handsToPlay.add(randomStart);
        }

        // now the initial hands and deck have been set, so play through them.
        while (!handsToPlay.isEmpty()) {
          MinimalHand currentPlayerHand = handsToPlay.poll();
          String bestMove = playerAid[numHandsPlaying - 2].getBestMove(currentPlayerHand);
          double currentBet = 1.0;
          boolean forceStay = false;
          while (!bestMove.equals("stay") && !forceStay) {
            if (bestMove.equals("hit")) {
              currentPlayerHand.addCard(currentDeck.removeRandomCard());
              if (currentPlayerHand.getHandValue() >= 21) {
                forceStay = true;
              } else {
                bestMove = playerAid[numHandsPlaying - 2].getBestMove(currentPlayerHand);
              }
            } else if (bestMove.equals("double")) {
              currentPlayerHand.addCard(currentDeck.removeRandomCard());
              forceStay = true;
              currentBet = 2;
            } else if (bestMove.equals("split")) {
              MinimalHand newHand = new MinimalHand();
              newHand.addCard(rankSplitOn);
              newHand.addCard(currentDeck.removeRandomCard());
              handsToPlay.add(newHand);
              // take away one of the pair and add a random card.
              currentPlayerHand.removeCard(rankSplitOn);
              currentPlayerHand.addCard(currentDeck.removeRandomCard());
              numHandsPlaying++;
              bestMove = playerAid[numHandsPlaying - 2].getBestMove(currentPlayerHand);
            } else {
              assert false : "something went wrong"; // idk why I put this here.
            }
            if (bestMove == null) {
              System.out.println(currentPlayerHand);
              System.out.println(currentDeck);
            }
          }
          // this hand is completed. Add it to the queue of finished hands.
          finishedHands.add(currentPlayerHand);
          finishedHandBets.add(currentBet);
        }
        // all player hands played. Get the Dealers hand.
        currentDeck.addHand(dealerHand);
        boolean[] temp = dealer.getDealerValue(currentDeck, rules, dealerHand, emptyHand);
        double[] dealerResults = new double[7]; // for compatibility with rules.
        for (int i = 0; i < 7; i++) {
          if (temp[i]) {
            dealerResults[i] = 1.0;
          }
        }
        while (!finishedHands.isEmpty()) {
          double moneyResult = rules.moneyMadeOnStaying(finishedHands.poll(), dealerResults);
          moneyMade += finishedHandBets.poll() * moneyResult;
        }
        assert finishedHandBets.isEmpty() : "bets queue not empty!";
        currentNumSimulations++;
      }
      double avgMoneyMade = moneyMade / desiredNumSimulations;

      if (startingNumHands == 2) {
        return avgMoneyMade;
      } else {
        Deck splittingDeck = new Deck(deck);
        splittingDeck.takeOutHand(dealerHand);
        playerAid[startingNumHands - 3].setSplitting(rankSplitOn, avgMoneyMade, splittingDeck);
      }

    }
    assert false : "should not have reached this far";
    return 0;

  }

  /**
   * default constructor.
   */
  public ExpectedMoneyCalculator(Deck deck, VariableRankHand startingHand, VariableRankHand dealerHand, Rules rules,
      boolean withSplitting, int desiredNumSimulations) {
    this.allHands = new HandContainer();
    this.dealerCalculator = new DealerProbabilities();

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
    this.setMoney(deck, startingHand, dealerHand, rules, withSplitting, desiredNumSimulations);

  }

  public ExpectedMoneyCalculator(ExpectedMoneyCalculator otherCalc) {
    this.allHands = new HandContainer();
    this.dealerCalculator = new DealerProbabilities();

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
          otherCalc.getHand(createHand);
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

  /**
   * Does not set splitting. This should not be called twice on the same object.
   *
   * @param deck
   *          original deck with no cards of either hand taken out.
   * @param startingHand
   *          starting hand to calculate from
   * @param dealerHand
   *          hand of the dealer.
   * @param rules
   *          the rules object corresponding to this game of blackjack.
   * @requires desiredNumSimulations > 0
   */
  private void setMoney(Deck deck, VariableRankHand startingHand, VariableRankHand dealerHand, Rules rules,
      boolean withSplitting, int desiredNumSimulations) {
    assert desiredNumSimulations > 0 : "what's the point of zero simulations";

    assert startingHand.getHandValue() <= 21 : "Don't pass in busted hands";

    // if this hand can be split, then the starting hand must be one card lower
    if (withSplitting && rules.allowedToSplit(startingHand, 0)) {
      boolean split = false;
      for (int i = 0; i < 13 && !split; i++) {
        if (startingHand.numCardRank13(i) == 2) {
          startingHand.removeCard(i);
          split = true;
        }
      }
      assert split : "hand " + startingHand + " was not split";
    }

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
    Queue<VariableRankHand> calculateSplitting = new LinkedList<VariableRankHand>();

    toExpand.add(startingHand);
    while (!toExpand.isEmpty()) {
      VariableRankHand next = toExpand.remove();

      handTracker.addHand(next);
      // assign staying average to next
      double[] dealerProbs = this.dealerCalculator.getProbabilities(new DealerDeck(deck), next, dealerHand, rules);
      double avgMoney = rules.moneyMadeOnStaying(next, dealerProbs);

      if (withSplitting && rules.allowedToSplit(next, 0)) {
        calculateSplitting.add(next);
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
    while (!calculateSplitting.isEmpty()) {
      VariableRankHand next = calculateSplitting.poll();
      Deck splittingDeck = new Deck(deck);
      splittingDeck.takeOutHand(dealerHand);
      int rankSplitOn = -1;
      for (int i = 0; i < 13 && rankSplitOn == -1; i++) {
        if (next.numCardRank13(i) == 2) {
          rankSplitOn = i;
        }
      }
      assert rankSplitOn != -1 : "hand " + startingHand + " was not a pair";
      double moneyMadeOnSplitting = splittingCalculator(splittingDeck, rankSplitOn, dealerHand, rules,
          desiredNumSimulations, this);
      this.setSplitting(rankSplitOn, moneyMadeOnSplitting, splittingDeck);
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
      // now change the empty hand
      VariableRankHand emptyHand = new VariableRankHand();
      emptyHand = this.allHands.getHand13(emptyHand);
      if (emptyHand != null) {
        double avgEmptyMoney = 0;
        for (int i = 0; i < 13; i++) {
          double drawProbability = deck.drawProbability(i, 0, 0);
          avgEmptyMoney += drawProbability * emptyHand.getNextHand(i).getMostMoneyMade();
        }
        emptyHand.setMoneyMadeIfHitting(avgEmptyMoney);
      }
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
import java.util.LinkedList;
import java.util.Queue;

/**
 * This really has not business being its own class anymore with the changes
 * I've made (its only a function now), but it would be too big a pain in the
 * ass to take it away.
 *
 * @author Thomas Centa
 *
 */
public class SplittingMoneyCalculator {

  /**
   * default constructor.
   */
  public SplittingMoneyCalculator() {
  }

  /**
   * It will make player decisions based on the ExpectedMoneyCalculator after
   * taking out (numHandsSplit - 1) of rankSplitOn from the deck. IMPORTANT:
   * THIS FUNCTION ASSUMES CARDS FROM THE OTHER HANDS THE PLAYER IS PLAYING ARE
   * TAKEN OUT OF THE DECK. IE. IF THE PLAYER HAS TWO HANDS AND IS DECIDING
   * WHETHER OR NOT TO SPLIT, THAT OTHER HAND BETTER BE TAKEN OUT. This function
   * starts by making two hands with one card rankSplitOn and plays those two
   * hands, so those two cards better not be taken out of the deck. Plays
   * assuming split hands are dealt a new card IMMEDIATELY. In terms of the
   * other function header: the dealerHand and current player hand (which should
   * just be a pair) should be IN the deck, while other player hands should be
   * OUT.
   *
   * @param deck
   * @param rankSplitOn
   * @param dealerHand
   * @param rules
   * @return
   */
  public double moneyMadeSplittingSimulation(Deck deck, int rankSplitOn, int timesSplitAlready,
      VariableRankHand dealerHand, Rules rules, int desiredNumSimulations) {

    // finish this startingHands thing later.
    VariableRankHand playersFirstHand = new VariableRankHand();
    playersFirstHand.addCard(rankSplitOn);
    playersFirstHand.setSplitHand(rankSplitOn);
    ExpectedMoneyCalculator[] playerAid = null;
    int deckAllowsSplit = deck.numCard(rankSplitOn) - dealerHand.numCardRank13(rankSplitOn) - 1;
    if (rankSplitOn == 0) {
      int rulesSplit = rules.numTimesAllowedToSplitAces() - timesSplitAlready;
      int canSplit = Math.min(deckAllowsSplit, rulesSplit);
      playerAid = new ExpectedMoneyCalculator[canSplit];
    } else {
      int canSplit = Math.min(deckAllowsSplit, rules.numTimesAllowedToSplitAces());
      playerAid = new ExpectedMoneyCalculator[canSplit];
    }
    for (int i = 0; i < playerAid.length; i++) {
      playerAid[i] = new ExpectedMoneyCalculator();
      deck.removeCard(rankSplitOn);
      playerAid[i].setMoney(deck, playersFirstHand, dealerHand, rules, false, 0);
    }
    for (int i = 0; i < playerAid.length; i++) { // restore deck
      deck.addCard(rankSplitOn);
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
      noHandsDeck.takeOutHand(dealerHand);
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

}
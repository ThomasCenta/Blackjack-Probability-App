public class VariableRankHand extends MinimalHand {

  /**
   * The probability of getting this hand. Updated during calculations.
   */
  private double currentProbability;

  /**
   * Pointers to hands reachable by adding one card to this. If a hand is at a
   * stand position, does not point to other hands. Has size 10 if this has hand
   * size < 2, 13 otherwise.
   */
  private VariableRankHand[] nextHands;

  /**
   * average money by splitting this hand (if possible) made on a bet of $1.
   */
  private double moneyMadeIfSplitting;

  /**
   * average money by staying this hand made on a bet of $1.
   */
  private double moneyMadeIfStaying;

  /**
   * average money by hitting this hand made on a bet of $1.
   */
  private double moneyMadeIfHitting;

  /**
   * average money by staying this hand made on a bet of $1.
   */
  private double moneyMadeIfDoubling;

  /**
   * best move based on money set to this. "split", "double", "hit", "stay", or
   * null if no money has been set.
   */
  private String bestMove;

  /**
   * keeps track of the most money made (by using bestMove).
   */
  private double mostMoneyMade;

  /**
   * Default constructor. Makes an empty hand. Sets all money made to -3
   */
  public VariableRankHand() {
    super();
    this.moneyMadeIfDoubling = -3;
    this.moneyMadeIfHitting = -3;
    this.moneyMadeIfSplitting = -3;
    this.moneyMadeIfStaying = -3;
    this.mostMoneyMade = -3;
    this.nextHands = new VariableRankHand[13];
    this.bestMove = null;
  }

  /**
   * Copy Constructor. Only copies the cards.
   *
   * @param otherHand
   *          hand to copy from.
   * @param cardsOnly
   *          true if only the card values and splitting value are to be copied
   *          (not the money stuff)
   */
  public VariableRankHand(VariableRankHand otherHand, boolean cardsOnly) {
    this.cardsInHand13 = new int[13];
    this.cardsInHand10 = new int[10];
    for (int i = 0; i < 13; i++) {
      this.cardsInHand13[i] = otherHand.numCardRank13(i);
      if (i <= 9) {
        this.cardsInHand10[i] = otherHand.numCardRank10(i);
      }
    }
    this.handValue = otherHand.getHandValue();
    this.hasAce = otherHand.getHasAce();
    if (!cardsOnly) {
      this.moneyMadeIfDoubling = otherHand.getMoneyMadeIfDoubling();
      this.moneyMadeIfHitting = otherHand.getMoneyMadeIfHitting();
      this.moneyMadeIfSplitting = otherHand.getMoneyMadeIfSplitting();
      this.moneyMadeIfStaying = otherHand.getMoneyMadeIfStaying();
      this.mostMoneyMade = otherHand.getMostMoneyMade();
      this.bestMove = otherHand.getBestMove();
    } else {
      this.moneyMadeIfDoubling = -3;
      this.moneyMadeIfHitting = -3;
      this.moneyMadeIfSplitting = -3;
      this.moneyMadeIfStaying = -3;
      this.mostMoneyMade = -3;
      this.bestMove = null;
    }

    this.nextHands = new VariableRankHand[13];
    this.numCards = otherHand.totalNumCards();
    this.rankSplitOn = otherHand.getRankSplitOn();
  }

  public String getBestMove() {
    return this.bestMove;
  }

  public void setNextHand(VariableRankHand otherHand, int nextCardRank) {
    assert nextCardRank >= 0 && nextCardRank <= 12 : "invalid card rank";
    if (nextCardRank > 9 && this.numCards >= 2) {
      this.nextHands[9] = otherHand;
    } else { // either rank <= 9 or its in 13 rank mode (0 or 1 cards)
      this.nextHands[nextCardRank] = otherHand;
    }
  }

  public VariableRankHand getNextHand(int nextCardRank) {
    assert nextCardRank >= 0 && nextCardRank <= 12 : "invalid card rank";
    if (nextCardRank > 9 && this.numCards >= 2) {
      return this.nextHands[9];
    } else {
      return this.nextHands[nextCardRank];
    }
  }

  public double getProbability() {
    return this.currentProbability;
  }

  public void setCurrentProbability(double prob) {
    this.currentProbability = prob;
  }

  public void setMoneyMadeIfStaying(double money) {
    this.moneyMadeIfStaying = money;
    if (money > this.mostMoneyMade) {
      this.mostMoneyMade = money;
      this.bestMove = "stay";
    }
  }

  public void setMoneyMadeIfHitting(double money) {
    this.moneyMadeIfHitting = money;
    if (money > this.mostMoneyMade) {
      this.mostMoneyMade = money;
      this.bestMove = "hit";
    }
  }

  public void setMoneyMadeIfSplitting(double money) {
    this.moneyMadeIfSplitting = money;
    if (money > this.mostMoneyMade) {
      this.mostMoneyMade = money;
      this.bestMove = "split";
    }
  }

  public void setMoneyMadeIfDoubling(double money) {
    this.moneyMadeIfDoubling = money;
    if (money > this.mostMoneyMade) {
      this.mostMoneyMade = money;
      this.bestMove = "double";
    }
  }

  public double getMoneyMadeIfStaying() {
    return this.moneyMadeIfStaying;
  }

  public double getMoneyMadeIfHitting() {
    return this.moneyMadeIfHitting;
  }

  public double getMoneyMadeIfSplitting() {
    return this.moneyMadeIfSplitting;
  }

  public double getMoneyMadeIfDoubling() {
    return this.moneyMadeIfDoubling;
  }

  public double getMostMoneyMade() {
    return this.mostMoneyMade;
  }

  @Override
  public String toString() {
    String toReturn = "";
    for (int i = 0; i < 12; i++) {
      toReturn += this.cardsInHand13[i] + " ";
    }
    toReturn += this.cardsInHand13[12];
    return toReturn;
  }

}
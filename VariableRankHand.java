public class VariableRankHand implements VariableRankHandInterface {

  /**
   * The probability of getting this hand. Updated during calculations.
   */
  private double currentProbability;

  /**
   * The current value of the hand by blackjack rules (ie. 0 - 21).
   */
  private int handValue;

  /**
   * says whether or not this hand has an ace VALUED AT 11.
   */
  private boolean hasAce;

  /**
   * An array of size 13 with each index i corresponding to the number of cards
   * in this hand with rank i + 1.
   */
  private int[] cardsInHand13;

  /**
   * An array of size 10 with each index i corresponding to the number of cards
   * in this hand with rank i + 1. Faces are rank 10
   */
  private int[] cardsInHand10;

  /**
   * Pointers to hands reachable by adding one card to this. If a hand is at a
   * stand position, does not point to other hands. Has size 10 if this has hand
   * size < 2, 13 otherwise.
   */
  private VariableRankHand[] nextHands;

  /**
   * total number of cards in this.
   */
  private int numCards;

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
   * the rank of the card this hand was split on. -1 if not a split hand.
   */
  private int rankSplitOn;

  /**
   * Default constructor. Makes an empty hand. Sets all money made to -3
   */
  public VariableRankHand() {
    this.cardsInHand10 = new int[10];
    this.cardsInHand13 = new int[13];
    this.handValue = 0;
    this.hasAce = false;
    this.moneyMadeIfDoubling = -3;
    this.moneyMadeIfHitting = -3;
    this.moneyMadeIfSplitting = -3;
    this.moneyMadeIfStaying = -3;
    this.nextHands = new VariableRankHand[13];
    this.numCards = 0;
    this.rankSplitOn = -1;
  }

  /**
   * Copy Constructor. Only copies the cards.
   *
   * @param otherHand
   *          hand to copy from.
   */
  public VariableRankHand(VariableRankHand otherHand) {
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
    this.moneyMadeIfDoubling = -1;
    this.moneyMadeIfHitting = -1;
    this.moneyMadeIfSplitting = -1;
    this.moneyMadeIfStaying = -1;
    this.nextHands = new VariableRankHand[13];
    this.numCards = otherHand.totalNumCards();
  }

  @Override
  public int compare13(VariableRankHand otherHand) {
    int toReturn = 0;
    for (int i = 0; i < 13 && toReturn == 0; i++) {
      if (this.numCardRank13(i) < otherHand.numCardRank13(i)) {
        toReturn = -1;
      } else if (this.numCardRank13(i) > otherHand.numCardRank13(i)) {
        toReturn = 1;
      }
    }
    return toReturn;
  }

  @Override
  public int compare10(VariableRankHand otherHand) {
    int toReturn = 0;
    for (int i = 0; i < 10 && toReturn == 0; i++) {
      if (this.numCardRank10(i) < otherHand.numCardRank10(i)) {
        toReturn = -1;
      } else if (this.numCardRank10(i) > otherHand.numCardRank10(i)) {
        toReturn = 1;
      }
    }
    return toReturn;
  }

  @Override
  public void setNextHand(VariableRankHand otherHand, int nextCardRank) {
    assert nextCardRank >= 0 && nextCardRank <= 12 : "invalid card rank";
    if (nextCardRank > 9 && this.numCards >= 2) {
      this.nextHands[9] = otherHand;
    } else { // either rank <= 9 or its in 13 rank mode (0 or 1 cards)
      this.nextHands[nextCardRank] = otherHand;
    }
  }

  @Override
  public VariableRankHand getNextHand(int nextCardRank) {
    assert nextCardRank >= 0 && nextCardRank <= 12 : "invalid card rank";
    if (nextCardRank > 9 && this.numCards >= 2) {
      return this.nextHands[9];
    } else {
      return this.nextHands[nextCardRank];
    }
  }

  @Override
  public int numCardRank13(int rank) {
    assert rank <= 12 && rank >= 0 : "invalid card rank";
    return this.cardsInHand13[rank];
  }

  @Override
  public int numCardRank10(int rank) {
    assert rank <= 9 && rank >= 0 : "invalid card rank";
    return this.cardsInHand10[rank];
  }

  @Override
  public int getHandValue() {
    return this.handValue;
  }

  @Override
  public void addCard(int rank) {
    assert rank <= 12 && rank >= 0 : "invalid card rank";
    this.numCards++;
    this.cardsInHand13[rank]++;
    rank = Math.min(9, rank);
    this.cardsInHand10[rank]++;

    int numAces = 0;
    if (this.hasAce == true) {
      numAces = 1;
    }
    if (rank == 0) {
      numAces++;
      this.handValue += 11;
    } else {
      this.handValue += rank + 1;
    }
    while (this.handValue > 21 && numAces > 0) {
      numAces--;
      this.handValue -= 10;
    }
    this.hasAce = numAces > 0;

  }

  @Override
  public boolean getHasAce() {
    return this.hasAce;
  }

  @Override
  public double getProbability() {
    return this.currentProbability;
  }

  @Override
  public void setCurrentProbability(double prob) {
    this.currentProbability = prob;
  }

  @Override
  public int totalNumCards() {
    return this.numCards;
  }

  @Override
  public void setMoneyMadeIfStaying(double money) {
    this.moneyMadeIfStaying = money;
  }

  @Override
  public void setMoneyMadeIfHitting(double money) {
    this.moneyMadeIfHitting = money;
  }

  @Override
  public void setMoneyMadeIfSplitting(double money) {
    this.moneyMadeIfSplitting = money;
  }

  @Override
  public void setMoneyMadeIfDoubling(double money) {
    this.moneyMadeIfDoubling = money;
  }

  @Override
  public double getMoneyMadeIfStaying() {
    return this.moneyMadeIfStaying;
  }

  @Override
  public double getMoneyMadeIfHitting() {
    return this.moneyMadeIfHitting;
  }

  @Override
  public double getMoneyMadeIfSplitting() {
    return this.moneyMadeIfSplitting;
  }

  @Override
  public double getMoneyMadeIfDoubling() {
    return this.moneyMadeIfDoubling;
  }

  @Override
  public double getMostMoneyMade() {
    double max = this.moneyMadeIfHitting;
    if (this.moneyMadeIfStaying > max) {
      max = this.moneyMadeIfStaying;
    }
    if (this.moneyMadeIfDoubling > max) {
      max = this.moneyMadeIfDoubling;
    }
    if (this.moneyMadeIfSplitting > max) {
      max = this.moneyMadeIfSplitting;
    }
    return max;
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

  @Override
  public void setSplitHand(int rankSplitOn) {
    this.rankSplitOn = rankSplitOn;
  }

  @Override
  public int getRankSplitOn() {
    return this.rankSplitOn;
  }

}
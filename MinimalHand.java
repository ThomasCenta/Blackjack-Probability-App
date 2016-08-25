/**
 * This is going to be a hand with the minimal amount of data stored for the
 * simulations.
 *
 * @author Thomas
 *
 */
public class MinimalHand {

  /**
   * The current value of the hand by blackjack rules (ie. 0 - 21).
   */
  protected int handValue;

  /**
   * says whether or not this hand has an ace VALUED AT 11.
   */
  protected boolean hasAce;

  /**
   * An array of size 13 with each index i corresponding to the number of cards
   * in this hand with rank i + 1.
   */
  protected int[] cardsInHand13;

  /**
   * An array of size 10 with each index i corresponding to the number of cards
   * in this hand with rank i + 1. Faces are rank 10
   */
  protected int[] cardsInHand10;

  /**
   * total number of cards in this.
   */
  protected int numCards;

  /**
   * the rank this hand has been split on. -1 if not split.
   */
  protected int rankSplitOn;

  /**
   * Default constructor. Makes an empty hand. Sets all money made to -3
   */
  public MinimalHand() {
    this.cardsInHand10 = new int[10];
    this.cardsInHand13 = new int[13];
    this.handValue = 0;
    this.hasAce = false;
    this.numCards = 0;
    this.rankSplitOn = -1;
  }

  /**
   * Copy Constructor. Only copies the cards.
   *
   * @param otherHand
   *          hand to copy from.
   */
  public MinimalHand(MinimalHand otherHand) {
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
    this.numCards = otherHand.totalNumCards();
    this.rankSplitOn = otherHand.getRankSplitOn();
  }

  public void setSplitHand(int rankSplitOn) {
    this.rankSplitOn = rankSplitOn;
  }

  public int getRankSplitOn() {
    return this.rankSplitOn;
  }

  public int compare13(MinimalHand otherHand) {
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

  public int compare10(MinimalHand otherHand) {
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

  public int numCardRank13(int rank) {
    assert rank <= 12 && rank >= 0 : "invalid card rank";
    return this.cardsInHand13[rank];
  }

  public int numCardRank10(int rank) {
    assert rank <= 9 && rank >= 0 : "invalid card rank";
    return this.cardsInHand10[rank];
  }

  public int getHandValue() {
    return this.handValue;
  }

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

  public void removeCard(int rank) {
    assert rank <= 12 && rank >= 0 : "invalid card rank";
    assert this.cardsInHand13[rank] > 0 : "no cards of rank " + rank;
    this.numCards--;
    this.cardsInHand13[rank]--;
    rank = Math.min(9, rank);
    this.cardsInHand10[rank]--;

    if (this.hasAce) {
      this.handValue -= 10;
      this.hasAce = false;
    }
    this.handValue -= rank + 1;
    if (this.handValue <= 11 && this.cardsInHand10[0] > 0) {
      this.handValue += 10;
      this.hasAce = true;
    }

  }

  public boolean getHasAce() {
    return this.hasAce;
  }

  public int totalNumCards() {
    return this.numCards;
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

  public MinimalHand splitHand() {
    assert this.numCards == 2 : "splitting hand has " + this.numCards + " cards";
    MinimalHand newHand = null;
    // now find the splitting rank (the one with two cards).
    for (int i = 0; i < 13; i++) {
      if (this.cardsInHand13[i] == 2) {
        newHand = new MinimalHand();
        newHand.addCard(i);
        newHand.setSplitHand(i);
        this.rankSplitOn = i;
        this.removeCard(i); // take one of this rank out.
      }
    }
    assert newHand != null : "pair not found in splitting hand.";
    return newHand;
  }

}
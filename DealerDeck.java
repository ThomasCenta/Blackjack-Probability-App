/**
 *
 * @author Thomas Centa
 *
 */
public class DealerDeck implements DealerDeckInterface {

  /**
   * total number of cards in the deck.
   */
  int numCards;

  /**
   * index i corresponds to the number of cards with rank i + 1 in the deck.
   * aces are rank 1
   */
  int[] cardsInDeck;

  /**
   * This creates a DealerDeck from a regular 13 rank deck.
   *
   * @param regularDeck
   *          This is a regular 13 rank deck to make the dealers deck from
   */
  public DealerDeck(Deck regularDeck) {
    this.numCards = 0;
    this.cardsInDeck = new int[10];
    for (int i = 0; i < 13; i++) {
      this.numCards += regularDeck.numCard(i + 1);
      if (i > 9) {
        this.cardsInDeck[9] += regularDeck.numCard(i + 1);
      } else {
        this.cardsInDeck[i] += regularDeck.numCard(i + 1);
      }
    }
  }

  @Override
  public double drawProbability(int rank) {
    double toReturn = 0.0;
    if (this.cardsInDeck[rank - 1] > 0) {
      toReturn = this.cardsInDeck[rank - 1] * 1.0 / this.numCards;
      this.cardsInDeck[rank]--;
      this.numCards--;
    }
    return toReturn;
  }

  @Override
  public void addCard(int rank) {
    this.cardsInDeck[rank]++;
    this.numCards++;
  }

}

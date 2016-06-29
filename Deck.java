/**
 * The Implementation of the Deck class.
 *
 * @author Thomas Centa
 *
 */
public class Deck implements DeckInterface {

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
   * Constructor using number of decks.
   *
   * @param numDecks
   *          The number of decks to create the deck with
   */
  public Deck(int numDecks) {
    this.cardsInDeck = new int[13];
    this.numCards = 52 * numDecks;
    for (int i = 0; i < this.cardsInDeck.length; i++) {
      this.cardsInDeck[i] = 4 * numDecks;
    }
  }

  @Override
  public double drawProbability(int rank) {
    double toReturn = 0.0;
    if (this.cardsInDeck[rank - 1] > 0) {
      toReturn = this.cardsInDeck[rank - 1] * 1.0 / this.numCards;
      this.cardsInDeck[rank - 1]--;
      this.numCards--;
    }
    return toReturn;
  }

  @Override
  public void addCard(int rank) {
    this.cardsInDeck[rank - 1]++;
    this.numCards++;
  }

  @Override
  public int removeRandomCard() {
    // Add this one later
    return 0;
  }

  @Override
  public int numCard(int rank) {
    return this.cardsInDeck[rank - 1];
  }

}

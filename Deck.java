import java.util.Random;

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
  private int numCards;

  /**
   * index i corresponds to the number of cards with rank i in the deck. aces
   * are rank 0. 2s are 1, ..., 10s are 9, and faces are also 9. This notation
   * is done to save computation time.
   */
  private int[] cardsInDeck;

  /**
   * Default Constructor. Makes an empty deck
   */
  public Deck() {
    this.cardsInDeck = new int[13];
    this.numCards = 0;
  }

  /**
   * copy constructor.
   */
  public Deck(Deck otherDeck) {
    this.cardsInDeck = new int[13];
    this.numCards = 0;
    for (int i = 0; i < 13; i++) {
      this.cardsInDeck[i] = otherDeck.numCard(i);
      this.numCards += otherDeck.numCard(i);
    }
  }

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
  public double drawProbability(int rank, int cardsTakenOut, int numRank) {
    double toReturn = 0.0;
    if (this.cardsInDeck[rank] > numRank) {
      toReturn = (this.cardsInDeck[rank] - numRank) * 1.0
          / (this.numCards - cardsTakenOut);
    }
    return toReturn;
  }

  @Override
  public void addCard(int rank) {
    this.cardsInDeck[rank]++;
    this.numCards++;
  }

  @Override
  public void removeCard(int rank) {
    assert this.cardsInDeck[rank] > 0 : "No card in the deck of rank " + rank;
    this.cardsInDeck[rank]--;
    this.numCards--;
  }

  @Override
  public int removeRandomCard() {
    Random rand = new Random();
    int nextCard = rand.nextInt(this.numCards);
    int sum = -1;
    int currentIndex = 0;
    while (currentIndex < 13) {
      sum += this.cardsInDeck[currentIndex];
      if (sum >= nextCard) {
        this.cardsInDeck[currentIndex]--;
        this.numCards--;
        return currentIndex;
      }
      currentIndex++;
    }
    //should never reach here, would mean a mismatch in the sum of all cards.
    assert false : "failed to find a random card in deck";
    return -1;
  }

  @Override
  public int numCard(int rank) {
    return this.cardsInDeck[rank];
  }

}

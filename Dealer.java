
/**
 * This class is really just here so multiple other classes can use one
 * function.
 *
 * @author Thomas
 *
 */
public class Dealer {

  public Dealer() {
  }

  /**
   * Assumes cards from both the player and dealer are NOT taken out of the
   * deck. Does not alter any of the inputs. (will just take both hands out of
   * the deck before calculations)
   *
   * @param deck
   *          original deck of the game.
   * @param rules
   *          rules to follow
   * @return an array of size 7 that contains the result as the true value (all
   *         others false). The positions go in order of results: 17, 18, 19,20,
   *         blackjack, non-blackjack 21, bust
   */
  public boolean[] getDealerValue(Deck deck, Rules rules, MinimalHand dealer, MinimalHand player) {

    Deck copyDeck = new Deck(deck);
    copyDeck.takeOutHand(dealer);
    copyDeck.takeOutHand(player);

    MinimalHand copy = new MinimalHand(dealer);
    while (!rules.dealerStays(copy)) {
      int rank = copyDeck.removeRandomCard();
      copy.addCard(rank);
    }
    boolean[] result = new boolean[7];
    for (int i = 0; i < result.length; i++) {
      result[i] = false;
    }
    if (copy.getHandValue() < 21) {
      result[copy.getHandValue() - 17] = true;
    } else if (copy.getHandValue() > 21) {
      result[6] = true;
    } else if (copy.totalNumCards() == 2) {
      result[4] = true;
    } else { // non-natural 21
      result[5] = true;
    }
    return result;
  }

}
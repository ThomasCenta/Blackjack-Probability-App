import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Thomas Centa
 *
 */

public class DealerProbabilities implements DealerProbabilitiesInterface {

  HandContainer allHands;

  HandContainer allHandsTest;

  /**
   * Default Constructor. Will create tree of hands assuming hitting on soft 17.
   */
  public DealerProbabilities() {
    this.allHands = new HandContainer();
    Queue<VariableRankHand> toExpand = new LinkedList<VariableRankHand>();
    VariableRankHand emptyHand = new VariableRankHand();
    this.allHands.addHand(emptyHand);
    toExpand.add(emptyHand);

    while (!toExpand.isEmpty()) {
      VariableRankHand next = toExpand.remove();

      // if dealer has >= 18 or hard 17, will have to stand
      if (next.getHandValue() >= 18 || (next.getHandValue() == 17 && !next.getHasAce())) {
        continue; // otherwise going to do that same thing
      }
      for (int i = 0; i < 10; i++) {
        VariableRankHand createHand = new VariableRankHand(next, true);
        createHand.addCard(i);
        VariableRankHand existingHand = this.allHands.getHand10(createHand);
        if (existingHand == null) {
          // since this hand is new, add it to the queue
          toExpand.add(createHand);
          this.allHands.addHand(createHand);
        } else { // set create hand to the existing hand.
          createHand = existingHand;
        }
        // the hand is made, add it to the pointers of the previous.
        next.setNextHand(createHand, i);
      }

    }

  }

  @Override
  public double[] getProbabilities(DealerDeck deck, VariableRankHand playerHand, VariableRankHand dealerHand,
      Rules rules) {

    deck.takeOutHand(playerHand);

    // set all hands probabilities = 0 to start
    this.allHands.setProbabilitiesToZero();
    // dealerHand no longer points to the input hand. Can change as needed
    dealerHand = this.allHands.getHand10(dealerHand);
    dealerHand.setCurrentProbability(1.0);
    // index 0-3 are values 21 - handValue, 4 is natural, 5 is other 21, 6 is
    // bust.
    double[] probabilities = new double[7];

    Queue<VariableRankHand> toExpand = new LinkedList<VariableRankHand>();

    toExpand.add(dealerHand);
    while (!toExpand.isEmpty()) {
      VariableRankHand next = toExpand.remove();
      if (rules.dealerStays(next)) { // leaf node
        int handValue = next.getHandValue(); // saves a bit of looking up.
        assert handValue >= 17 : "dealer stays on hand value of " + handValue;
        if (handValue < 21) {
          probabilities[handValue - 17] += next.getProbability();
        } else if (handValue > 21) {
          probabilities[6] += next.getProbability();
        } else if (handValue == 21 && next.totalNumCards() == 2) {
          probabilities[4] += next.getProbability();
        } else {
          probabilities[5] += next.getProbability();
        }
      } else { // not a leaf node
        for (int i = 0; i < 10; i++) {
          VariableRankHand pointedHand = next.getNextHand(i);
          assert pointedHand != null : "non-leaf node has too few children";

          double pointedProb = pointedHand.getProbability();
          double drawProbability = next.getProbability()
              * deck.drawProbability(i, next.totalNumCards(), next.numCardRank10(i));
          pointedHand.setCurrentProbability(pointedProb + drawProbability);

          if (pointedProb == 0.0 && drawProbability > 0.0) { // need to expand
                                                             // this node again.
            // adding this drawProbability can quicken up runtime.
            toExpand.add(pointedHand);
          }

        }
      }
    }

    deck.addHand(playerHand);

    return probabilities;

  }

}
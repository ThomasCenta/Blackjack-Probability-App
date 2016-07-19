import java.util.ArrayList;

public class HandContainer implements HandContainerInterface {

  ArrayList<VariableRankHand> tenContainer;

  ArrayList<VariableRankHand> thirteenContainer;

  /**
   * BINARY SEARCH ALGORITHM!!!!! Looking for a hand in the container based on
   * 10 ranks. Returns an array of two values. The first is the index of the
   * needle in this (-1 if not found). The second is the index of the hand that
   * is directly above the needle in this list (or is directly above where it
   * should be) or upperBound + 1 if needle is larger than all hands. This is so
   * it doesn't have to search twice. If hand is less than all hands, return -1
   *
   * @param haystack
   *          container to search for
   * @param needle
   *          hand to be found.
   * @param lowerBound
   *          lower bound (inclusive) on the index to check for
   * @param upperBound
   *          upper bound (inclusive) on the index to check for
   *
   * @requires 0 <= lowerBound <= upperBound <= haystack.size() - 1 unless
   *           haystack.size() == 0, then any bounds can be used.
   * @return an array of two ints as described above.
   */
  private static int[] find10(ArrayList<VariableRankHand> haystack, VariableRankHand needle, int lowerBound,
      int upperBound) {
    assert (lowerBound <= upperBound) || haystack.size() == 0 : "passed a lower bound " + lowerBound
        + " smaller than upper bound " + upperBound;

    int[] toReturn = new int[2];

    if (haystack.size() == 0) { // have to cover this case
      toReturn[0] = -1;
      toReturn[1] = 0;
    } else if (lowerBound >= upperBound - 1) { // lower and upper bound next to
                                               // each other.
      if (haystack.get(lowerBound).compare10(needle) == 0) {
        toReturn[0] = lowerBound;
        toReturn[1] = lowerBound + 1;
      } else if (haystack.get(upperBound).compare10(needle) == 0) {
        toReturn[0] = upperBound;
        toReturn[1] = upperBound + 1;
      } else { // needle not in this
        toReturn[0] = -1;
        if (needle.compare10(haystack.get(upperBound)) > 0) {
          toReturn[1] = upperBound + 1;
        } else if (needle.compare10(haystack.get(lowerBound)) < 0) {
          toReturn[1] = lowerBound;
        } else { // needle between lower and upper bound
          toReturn[1] = upperBound;
        }
      }
    } else {
      int toTest = (lowerBound + upperBound) / 2;
      int comparison = needle.compare10(haystack.get(toTest));
      if (comparison <= 0) { // needle is below or at midpoint
        toReturn = find10(haystack, needle, lowerBound, toTest);
      } else {
        toReturn = find10(haystack, needle, toTest, upperBound);
      }
    }
    return toReturn;
  }

  /**
   * BINARY SEARCH ALGORITHM!!!!! Looking for a hand in the container based on
   * 10 ranks. Returns an array of two values. The first is the index of the
   * needle in this (-1 if not found). The second is the index of the hand that
   * is directly above the needle in this list (or is directly above where it
   * should be). This is so it doesn't have to search twice.
   *
   * @param haystack
   *          container to search for
   * @param needle
   *          hand to be found.
   * @param lowerBound
   *          lower bound (inclusive) on the index to check for
   * @param upperBound
   *          upper bound (inclusive) on the index to check for
   * @return an array of two ints as described above.
   */
  private static int[] find13(ArrayList<VariableRankHand> haystack, VariableRankHand needle, int lowerBound,
      int upperBound) {
    assert (lowerBound <= upperBound) || haystack.size() == 0 : "passed a lower bound " + lowerBound
        + " smaller than upper bound " + upperBound;

    int[] toReturn = new int[2];

    if (haystack.size() == 0) { // have to cover this case
      toReturn[0] = -1;
      toReturn[1] = 0;
    } else if (lowerBound >= upperBound - 1) { // lower and upper bound next to
                                               // each other.
      if (haystack.get(lowerBound).compare13(needle) == 0) {
        toReturn[0] = lowerBound;
        toReturn[1] = lowerBound + 1;
      } else if (haystack.get(upperBound).compare13(needle) == 0) {
        toReturn[0] = upperBound;
        toReturn[1] = upperBound + 1;
      } else { // needle not in this
        toReturn[0] = -1;
        if (needle.compare13(haystack.get(upperBound)) > 0) {
          toReturn[1] = upperBound + 1;
        } else if (needle.compare13(haystack.get(lowerBound)) < 0) {
          toReturn[1] = lowerBound;
        } else { // needle between lower and upper bound
          toReturn[1] = upperBound;
        }
      }
    } else {
      int toTest = (lowerBound + upperBound) / 2;
      int comparison = needle.compare13(haystack.get(toTest));
      if (comparison <= 0) { // needle is below or at midpoint
        toReturn = find13(haystack, needle, lowerBound, toTest);
      } else {
        toReturn = find13(haystack, needle, toTest, upperBound);
      }
    }
    return toReturn;
  }

  public HandContainer() {
    this.tenContainer = new ArrayList<VariableRankHand>();
    this.thirteenContainer = new ArrayList<VariableRankHand>();
  }

  @Override
  public void addHand(VariableRankHand toAdd) {

    int[] spotInList10 = find10(this.tenContainer, toAdd, 0, this.tenContainer.size() - 1);
    if (spotInList10[0] == -1) { // only add if not in there
      this.tenContainer.add(spotInList10[1], toAdd);
    }
    int[] spotInList13 = find13(this.thirteenContainer, toAdd, 0, this.thirteenContainer.size() - 1);
    if (spotInList13[0] == -1) { // only add if not in there
      this.thirteenContainer.add(spotInList13[1], toAdd);
    }
  }

  @Override
  public VariableRankHand getHand10(VariableRankHand equivalentHand) {

    int[] spotInList10 = find10(this.tenContainer, equivalentHand, 0, this.tenContainer.size() - 1);
    if (spotInList10[0] == -1) {
      return null;
    } else {
      return this.tenContainer.get(spotInList10[0]);
    }
  }

  @Override
  public VariableRankHand getHand13(VariableRankHand equivalentHand) {
    int[] spotInList13 = find13(this.thirteenContainer, equivalentHand, 0, this.thirteenContainer.size() - 1);
    if (spotInList13[0] == -1) {
      return null;
    } else {
      return this.thirteenContainer.get(spotInList13[0]);
    }

  }

  @Override
  public void setProbabilitiesToZero() {
    for (int i = 0; i < this.tenContainer.size(); i++) {
      this.tenContainer.get(i).setCurrentProbability(0.0);
    }
    for (int i = 0; i < this.thirteenContainer.size(); i++) {
      this.thirteenContainer.get(i).setCurrentProbability(0.0);
    }

  }

}
import java.util.LinkedList;
import java.util.Queue;

public class HandContainer implements HandContainerInterface {

  Node emptyNode10;

  Node emptyNode13;

  Queue<VariableRankHand> allHands13;

  Queue<VariableRankHand> allHands10;

  private class Node {
    VariableRankHand hand;
    Node[] nextHands;
  }

  /**
   * Instantiates an empty container.
   */
  public HandContainer() {
    this.emptyNode10 = new Node();
    this.emptyNode10.hand = null;
    this.emptyNode10.nextHands = new Node[10];
    this.emptyNode13 = new Node();
    this.emptyNode13.hand = null;
    this.emptyNode13.nextHands = new Node[13];
    this.allHands13 = new LinkedList<VariableRankHand>();
    this.allHands10 = new LinkedList<VariableRankHand>();
  }

  @Override
  public void addHand(VariableRankHand toAdd) {
    // first add it to the 10 tree
    Node currentNode = this.emptyNode10;
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < toAdd.numCardRank10(i); j++) {
        if (currentNode.nextHands[i] == null) {
          // need to create a new node
          Node newNode = new Node();
          newNode.hand = null;
          newNode.nextHands = new Node[10];
          currentNode.nextHands[i] = newNode;
          currentNode = newNode;
        } else {
          currentNode = currentNode.nextHands[i];
        }
      }
    }
    if (currentNode.hand == null) { // only add if it hasnt been added yet
      this.allHands10.add(toAdd);
      currentNode.hand = toAdd;
    }
    // now add it to the 13 tree
    currentNode = this.emptyNode13;
    for (int i = 0; i < 13; i++) {
      for (int j = 0; j < toAdd.numCardRank13(i); j++) {
        if (currentNode.nextHands[i] == null) {
          // need to create a new node
          Node newNode = new Node();
          newNode.hand = null;
          newNode.nextHands = new Node[13];
          currentNode.nextHands[i] = newNode;
          currentNode = newNode;
        } else {
          currentNode = currentNode.nextHands[i];
        }
      }
    }
    if (currentNode.hand == null) {
      this.allHands13.add(toAdd);
      currentNode.hand = toAdd;
    }
  }

  @Override
  public VariableRankHand getHand10(MinimalHand equivalentHand) {
    Node currentNode = this.emptyNode10;
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < equivalentHand.numCardRank10(i); j++) {
        if (currentNode.nextHands[i] == null) {
          return null; // hand not created yet
        } else {
          currentNode = currentNode.nextHands[i];
        }
      }
    }
    if (currentNode.hand != null) {
      assert equivalentHand.compare10(currentNode.hand) == 0 : "hands not the same!";
    }
    return currentNode.hand; // note it may be a null hand
  }

  @Override
  public VariableRankHand getHand13(MinimalHand equivalentHand) {
    Node currentNode = this.emptyNode13;
    for (int i = 0; i < 13; i++) {
      for (int j = 0; j < equivalentHand.numCardRank13(i); j++) {
        if (currentNode.nextHands[i] == null) {
          return null;
        } else {
          currentNode = currentNode.nextHands[i];
        }
      }
    }
    if (currentNode.hand != null) {
      assert equivalentHand.compare13(currentNode.hand) == 0 : "hands not the same!";
    }
    return currentNode.hand; // may be null if hand hasn't been set

  }

  @Override
  public void setProbabilitiesToZero() {
    for (int i = 0; i < this.allHands10.size(); i++) {
      VariableRankHand next = this.allHands10.poll();
      next.setCurrentProbability(0.0);
      this.allHands10.add(next);
    }
    for (int i = 0; i < this.allHands13.size(); i++) {
      VariableRankHand next = this.allHands13.poll();
      next.setCurrentProbability(0.0);
      this.allHands13.add(next);
    }

  }

}
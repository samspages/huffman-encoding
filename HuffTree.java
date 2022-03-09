/**
 * HuffTree class. Implements compare method and constructors for both node types.
 *
 * @author Samuel H. Page
 * @version 12/1/2021
 */
public class HuffTree {
  private HuffBaseNode root;

  HuffTree(byte element, int weight) {
    root = new HuffLeafNode(element, weight);
  }

  HuffTree(HuffBaseNode l, HuffBaseNode r, int weight) {
    root = new HuffInternalNode(l, r, weight);
  }

  HuffBaseNode root() {
    return root;
  }

  int weight() {
    return root.weight();
  }

  int compareTo(Object t) {
    HuffTree that = (HuffTree) t;
    int res = 0;
    if (root.weight() < that.weight()) {
      res = -1;
    }
    if (root.weight() > that.weight()) {
      res = 1;
    }
    return res;
  }
}
/*
 * This work complies with the JMU Honor Code.
 * References and Acknowledgments: I received no outside help with this
 * programming assignment.
 */

/**
 * HuffInternalNode subclass provides left/right node getters and proper constructor.
 *
 * @author Samuel H Page
 * @version 12/1/21
 */
class HuffInternalNode extends HuffBaseNode {
  private HuffBaseNode left;
  private HuffBaseNode right;
  private int weight;

  public HuffInternalNode(HuffBaseNode l, HuffBaseNode r, int weight) {
    left = l;
    right = r;
    this.weight = weight;
  }

  public HuffBaseNode getLeft() {
    return left;
  }

  public HuffBaseNode getRight() {
    return right;
  }

  public void setLeft(HuffBaseNode node) {
    left = node;
  }

  public void setRight(HuffBaseNode node) {
    right = node;
  }

  @Override boolean isleaf() {
    return false;
  }

  @Override int weight() {
    return weight;
  }
}
/*
 * This work complies with the JMU Honor Code.
 * References and Acknowledgments: I received no outside help with this
 * programming assignment.
 */

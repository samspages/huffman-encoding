/**
 * HuffLeafNode inner class provides get value and proper constructor.
 *
 * @author Samuel H Page
 * @version 12/1/21
 */
class HuffLeafNode extends HuffBaseNode {
  private byte element;
  private int weight;

  public HuffLeafNode(byte element, int weight) {
    this.element = element;
    this.weight = weight;
  }

  byte value() {
    return element;
  }

  @Override boolean isleaf() {
    return true;
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

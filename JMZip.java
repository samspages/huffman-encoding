import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * JMZip class creates compressed .jmz files from the given text file.
 *
 * @author Samuel H Page.
 * @version 12/1/21
 */
public class JMZip {

  /**
   * Encode method entrance to recursive encode helper.
   * @param byteSequence byte[] to encode
   * @param root node to start from
   * @return BitSequence encoding
   */
  public static BitSequence encode(byte[] byteSequence, HuffBaseNode root) {
    BitSequence encoding = new BitSequence();
    HashMap<Byte, BitSequence> hm = new HashMap<Byte, BitSequence>();
    encodeHelper(hm, root, "");

    for (int i = 0; i < byteSequence.length; i++) {
      encoding.appendBits(hm.get(byteSequence[i]));
    }

    return encoding;
  }

  /**
   * Recursive encoding helper.
   * @param hm map of bytes to their respective encoding path.
   * @param root node to start from.
   * @param s string for use in building encoding path.
   */
  public static void encodeHelper(HashMap<Byte, BitSequence> hm, HuffBaseNode root, String s) {

    if (root.isleaf()) {
      HuffLeafNode leafNode = (HuffLeafNode) root;
      BitSequence bits = new BitSequence();
      bits.appendBits(s);
      hm.put(leafNode.value(), bits);
      return;
    }

    HuffInternalNode internalNode = (HuffInternalNode) root;
    encodeHelper(hm, internalNode.getLeft(), s + "0");
    encodeHelper(hm, internalNode.getRight(), s + "1");
  }

  /**
   * buildHuffTree method constructs tree from array list of sorted nodes.
   * @param nodes to build with
   * @return single hufftree element
   */
  public static HuffTree buildHuffTree(ArrayList<HuffTree> nodes) {
    HuffTree tree = null;
    HuffTree tmp1;
    HuffTree tmp2;
    sort(nodes);
    while (nodes.size() != 1) {
      tmp1 = nodes.get(0);
      tmp2 = nodes.get(1);
      tree = new HuffTree(tmp1.root(), tmp2.root(), tmp1.weight() + tmp2.weight());
      nodes.remove(tmp1);
      nodes.remove(tmp2);
      nodes.add(tree);
      sort(nodes);
    }
    return tree;
  }

  /**
   * Insertion sorter, ascending order.
   * @param nodes to sort
   */
  public static void sort(ArrayList<HuffTree> nodes) {
    for (int i = 1; i < nodes.size(); i++) {
      HuffTree cur = nodes.get(i);
      int n = i - 1;
      while ((n > -1) && nodes.get(n).compareTo(cur) == 1) {
        nodes.set(n + 1, nodes.get(n));
        n--;
      }
      nodes.set(n + 1, cur);
    }
  }

  /**
   * getFrequencies returns map of frequencies to byte elements.
   * @param elements to map
   * @return hashmap of byte elements and integer frequencies
   */
  public static HashMap<Byte, Integer> getFrequencies(byte[] elements) {
    HashMap<Byte, Integer> hm = new HashMap<Byte, Integer>();
    if (elements.length < 1) {
      return hm;
    }
    for (byte b : elements) {
      if (!hm.containsKey(b)) {
        hm.put(b, 1);
      } else {
        Integer iteratedValue = hm.get(b) + 1;
        hm.replace(b, iteratedValue);
      }
    }
    return hm;
  }

  /**
   * Read file into byte array.
   * @param file to read
   * @return byte array
   */
  public static byte[] byterize(File file) {
    byte[] byteSequence;
    byteSequence = new byte[(int) file.length()];
    BufferedInputStream input = null;
    // create input stream from given file
    try {
      input = new BufferedInputStream(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    // read to byte array
    try {
      if (input != null) {
        byteSequence = input.readAllBytes();
        input.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return byteSequence;
  }

  /**
   * Main.
   * @param args filenames/paths
   */
  public static void main(String[] args) {
    //if (args.length == 0) {
    //  System.out.println("No argument specified");
    //  return;
    //}

    //if (args.length > 2 || args.length == 1) {
    //  System.out.println("Incorrect number of arguments");
    //  return;
    //}
    // get file path
    File openFile = new File("C:\\Users\\Sam\\CS 240 Algorithms\\PA4_Huffman\\src\\test.txt");


    // test path = "C:\Users\Sam\CS 240 Algorithms\PA4_Huffman\src\test.txt"
    //create array of all bytes in file
    byte[] byteSequence = byterize(openFile);

    if (byteSequence.length <= 0) {
      System.out.println("Empty File Error.");
      try {
        HuffmanSave huffSave = new HuffmanSave(new BitSequence(), new HashMap<>());
        FileOutputStream fileOut = new FileOutputStream(args[1]);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(huffSave);
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    }

    // map the bytes and their frequencies
    HashMap<Byte, Integer> hashy = (HashMap<Byte, Integer>) getFrequencies(byteSequence);
    ArrayList<HuffTree> treeNodes = new ArrayList<HuffTree>(byteSequence.length);

    // create huff tree nodes for each index of byte array
    for (Map.Entry<Byte, Integer> item : hashy.entrySet()) {
      treeNodes.add(new HuffTree(item.getKey(), item.getValue()));
    }

    // sort list and build tree
    BitSequence bitSet = null;
    if (treeNodes.size() < 1) {
      System.out.println("Unreadable File");
      return;
    }  else if (treeNodes.size() > 1) {
      HuffTree tree = buildHuffTree(treeNodes);
      bitSet = encode(byteSequence, tree.root());
    }


    // serialize tree instance and bit sequence
    HuffmanSave huffSave = new HuffmanSave(bitSet, hashy);
    try {
      FileOutputStream fileOut = new FileOutputStream("test.jmz");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(huffSave);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
/*
 * This work complies with the JMU Honor Code.
 * References and Acknowledgments: I received no outside help with this
 * programming assignment.
 */

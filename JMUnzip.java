import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * JMUnzip class.
 *
 * @author Samuel H. Page
 * @version 12/1/21
 */
public class JMUnzip {

  /**
   * Decode method builds arraylist of decoded bits.
   * @param biterator iterator for Bitsequence
   * @param decodedBits arraylist to construct
   * @param root node to start with
   * @param bits to decode
   */
  public static void decode(Iterator<Integer> biterator, ArrayList<Byte> decodedBits,
      HuffBaseNode root, BitSequence bits) {

    // base case, if root is a leaf add its byte element to the list.
    if (root.isleaf()) {
      HuffLeafNode leafNode = (HuffLeafNode) root;
      decodedBits.add(leafNode.value());
      return;
    }
    HuffInternalNode internalNode = (HuffInternalNode) root;
    // if next bit is 0 recurse left, otherwise right. Explicit check for 1 results in SO(?).
    if (biterator.hasNext()) {
      if (biterator.next() == 0) {
        decode(biterator, decodedBits, ((HuffInternalNode) root).getLeft(), bits);
      } else {
        decode(biterator, decodedBits, ((HuffInternalNode) root).getRight(), bits);
      }
    }
  }

  /**
   * Main method.
   * @param args filenames/paths
   */
  public static void main(String[] args) {
    //if (args.length < 1) {
    //  System.out.println("0 provided arguments");
    //  return;
    //}
    //if (args.length > 2) {
    //  System.out.println("Incorrect number of arguments");
    //  return;
    //}
    HuffmanSave treeSave = null;
    // deserialize huffman tree object
    try {
      FileInputStream fileIn = new FileInputStream("test.jmz");
      ObjectInputStream objIn = new ObjectInputStream(fileIn);
      treeSave = (HuffmanSave) objIn.readObject();
      objIn.close();
      fileIn.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    if (treeSave == null) {
      System.out.println("Unreadable file");
      return;
    }
    // build huffman tree from map of bytes and frequencies
    // create huff tree nodes for each index of byte array
    HashMap<Byte, Integer> frequencyMap = treeSave.getFrequencies();
    ArrayList<HuffTree> treeNodes = new ArrayList<HuffTree>();

    //check null BitSequence & empty file, write empty array to file
    if (treeSave.getEncoding() == null || treeSave.getEncoding().length() == 0) {
      try (FileOutputStream stream = new FileOutputStream(args[1])) {
        byte[] empty = new byte[0];
        stream.write(empty);
        stream.close();

      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    }
    // check 1 byte
    // TODO fix this.
    if (treeSave.getEncoding().length() == 1) {
      try (FileOutputStream stream = new FileOutputStream(args[1])) {
        BitSequence oneByteFile = treeSave.getEncoding();
        byte[] bits = new byte[oneByteFile.length()];
        for (int i = 0; i < bits.length; i++) {
          stream.write(oneByteFile.getBit(i));
        }
        stream.close();

      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // create arraylist of tree nodes from frequency maps
    for (Map.Entry<Byte, Integer> item : frequencyMap.entrySet()) {
      treeNodes.add(new HuffTree(item.getKey(), item.getValue()));
    }

    HuffTree root = JMZip.buildHuffTree(treeNodes);
    ArrayList<Byte> bytes = new ArrayList<Byte>();

    // build file from bitsequence w/ tree
    Iterator<Integer> biterator = treeSave.getEncoding().iterator();
    while (biterator.hasNext()) {
      decode(biterator, bytes, root.root(), treeSave.getEncoding());
    }

    // create byte array from decoded sequence
    byte[] fileBytes = new byte[bytes.size()];
    for (int i = 0; i < fileBytes.length; i++) {
      fileBytes[i] = bytes.get(i);
    }

    // save to args[1]
    try (FileOutputStream stream = new FileOutputStream("unzip.txt")) {
      stream.write(fileBytes);
      stream.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
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

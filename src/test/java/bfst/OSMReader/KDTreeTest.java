package bfst.OSMReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import bfst.addressparser.Address;
import bfst.canvas.CanvasElement;
import bfst.canvas.LinePath;
import bfst.canvas.Range;
import javafx.geometry.Point2D;

public class KDTreeTest {

  @Test
  public void NearestNeighborForLinePaths() {
    ArrayList<CanvasElement> elements = new ArrayList<>();
    
    // expected element
    LinePath expected = new LinePath(new Node(1l, 0.4f, 0.4f), new Node(2l, 0.6f, 0.6f));
    elements.add(expected);

    // other elements
    for (int i = 0; i < KDTree.maxNumOfElements; i++) {
      elements.add(new LinePath(new Node(123l, 0.1f, 0.1f), new Node(123l, 0.2f, 0.1f)));
    }
    
    KDTree kdTree = new KDTree(elements, new Range(0,0,1,1));

    CanvasElement actual = kdTree.nearestNeighbor(new Point2D(0.6, 0.5));

    assert(elements.size() == KDTree.maxNumOfElements+1);
    assert(kdTree.low.depth == 1);
    assertEquals(expected, actual);
  }

  @Test
  public void NearestNeighborForAddresses() {
    ArrayList<CanvasElement> elements = new ArrayList<>();
    
    // expected element
    LinePath expected = new LinePath(new Node(1l, 0.4f, 0.4f), new Node(2l, 0.6f, 0.6f));
    elements.add(expected);

    // other elements
    for (int i = 0; i < KDTree.maxNumOfElements; i++) {
      elements.add(new LinePath(new Node(123l, 0.1f, 0.1f), new Node(123l, 0.2f, 0.1f)));
    }
    
    KDTree kdTree = new KDTree(elements, new Range(0,0,1,1));

    CanvasElement actual = kdTree.nearestNeighbor(new Point2D(0.6, 0.5));

    assert(elements.size() == KDTree.maxNumOfElements+1);
    assert(kdTree.low.depth == 1);
    assertEquals(expected, actual);
  }

}
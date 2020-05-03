package bfst.OSMReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import bfst.canvas.CanvasElement;
import bfst.canvas.LinePath;
import bfst.canvas.Range;
import bfst.routeFinding.Edge;
import bfst.routeFinding.Street;
import javafx.geometry.Point2D;

public class KDTreeTest {

  @Test
  public void NearestNeighborForLinePaths() {
    ArrayList<CanvasElement> elements = new ArrayList<>();
    
    // expected element
    LinePath expected = new LinePath(new Node(1l, 0.4f, 0.4f), new Node(2l, 0.6f, 0.6f));
    elements.add(expected);

    // Add others such that 'elements' has maxNumOfElements+1 elements
    for (int i = 0; i < KDTree.maxNumOfElements; i++) {
      elements.add(new LinePath(new Node(123l, 0.1f, 0.1f), new Node(123l, 0.2f, 0.1f)));
    }
    
    KDTree kdTree = new KDTree(elements, new Range(0,0,1,1));

    CanvasElement actual = kdTree.nearestNeighbor(new Point2D(0.6, 0.5));
    assertEquals(expected, actual);

    assert(elements.size() == KDTree.maxNumOfElements+1);
    assert(kdTree.low.depth == 1);
  }

  @Test
  public void NearestNeighborForLinePaths2() {
    ArrayList<CanvasElement> elements = new ArrayList<>();
    
    // expected element
    LinePath expected = new LinePath(new Node(1l, 0.4f, 0.4f), new Node(2l, 0.6f, 0.6f));
    elements.add(expected);

    // Add others such that 'elements' has maxNumOfElements+1 elements
    for (int i = 0; i < KDTree.maxNumOfElements*2; i++) {
      elements.add(new LinePath(new Node(123l, 0.0f, 0.0f), new Node(123l, 0.1f, 0.1f)));
      elements.add(new LinePath(new Node(123l, 1.0f, 1.0f), new Node(123l, 0.9f, 0.9f)));
      elements.add(new LinePath(new Node(123l, 0.0f, 1.0f), new Node(123l, 0.1f, 0.9f)));
      elements.add(new LinePath(new Node(123l, 1.0f, 0.0f), new Node(123l, 0.9f, 0.1f)));
    }

    KDTree kdTree = new KDTree(elements, new Range(0,0,1,1));

    CanvasElement actual = kdTree.nearestNeighbor(new Point2D(0.6, 0.5));
    assertEquals(expected, actual);

    assert(kdTree.low.low.depth == 2);
  }

  @Test
  public void NearestNeighborForLinePaths3() {
    ArrayList<CanvasElement> elements = new ArrayList<>();
    
    // expected element
    LinePath expected = new LinePath(new Node(1l, 0.49f, 1f), new Node(2l, 0.49f, 1f));
    elements.add(expected);

    // Add others such that 'elements' has maxNumOfElements+1 elements
    for (int i = 0; i < KDTree.maxNumOfElements*2; i++) {
      elements.add(new LinePath(new Node(123l, 0.0f, 0.0f), new Node(123l, 0.1f, 0.1f)));
      elements.add(new LinePath(new Node(123l, 1.0f, 1.0f), new Node(123l, 0.9f, 0.9f)));
      elements.add(new LinePath(new Node(123l, 0.0f, 1.0f), new Node(123l, 0.1f, 0.9f)));
      elements.add(new LinePath(new Node(123l, 1.0f, 0.0f), new Node(123l, 0.9f, 0.1f)));
    }

    KDTree kdTree = new KDTree(elements, new Range(0,0,1,1));

    CanvasElement actual = kdTree.nearestNeighbor(new Point2D(0.51, 0.51));
    assertEquals(expected, actual);

    assert(kdTree.low.low.depth == 2);
  }

  @Test
  public void EmptyNearestNeighbor() {
    ArrayList<CanvasElement> elements = new ArrayList<>();
    KDTree kdTree = new KDTree(elements, new Range(0,0,1,1));

    CanvasElement actual = kdTree.nearestNeighbor(new Point2D(0.5, 0.5));
    assertEquals(null, actual);

    assert(kdTree.depth == 0);
  }

  @Test
  public void EmptyBoundingRangeOf() {
    try {
      CanvasElement.boundingRangeOf(new ArrayList<CanvasElement>());
    } catch (RuntimeException e) {
      assertEquals("Empty list cannot have bounding range", e.getMessage());
    }
  }

  @Test
  public void RangeSearch() {
    ArrayList<CanvasElement> elements = new ArrayList<>();
    
    elements.add(new LinePath(new Node(0l,0f,0f), new Node(0l,0f,0f)));
    elements.add(new LinePath(new Node(0l,0.5f,0f), new Node(0l,0f,0.5f)));
    elements.add(new LinePath(new Node(0l,0f,0f), new Node(0l,0f,1.5f)));

    for (int i = 0; i < KDTree.maxNumOfElements*4; i++) {
      elements.add(new LinePath(new Node(0l,2f,2f), new Node(0l,2f,2f)));
    }

    KDTree kdTree = new KDTree(elements, new Range(-2,-2,2,2));

    List<? extends CanvasElement> actual = kdTree.rangeSearch(new Range(-1f, -1f, 1f, 1f));
    assertEquals(3, actual.size());
  }

  @Test
  public void RangeSearch2() {
    ArrayList<CanvasElement> elements = new ArrayList<>();
    
    elements.add(new LinePath(new Node(0l,0f,0f), new Node(0l,0f,0f)));
    elements.add(new LinePath(new Node(0l,0.5f,0f), new Node(0l,0f,0.5f)));
    elements.add(new LinePath(new Node(0l,0f,0f), new Node(0l,0f,1.5f)));

    for (int i = 0; i < KDTree.maxNumOfElements*2; i++) {
      elements.add(new LinePath(new Node(0l,0.5f,0.5f), new Node(0l,0f,0f)));
      elements.add(new LinePath(new Node(0l,0f,0f), new Node(0l,0.5f,0.5f)));
    }

    for (int i = 0; i < KDTree.maxNumOfElements*4; i++) {
      elements.add(new LinePath(new Node(0l,2f,2f), new Node(0l,2f,2f)));
    }

    KDTree kdTree = new KDTree(elements, new Range(-2,-2,2,2));

    List<? extends CanvasElement> actual = kdTree.rangeSearch(new Range(-1f, -1f, 1f, 1f));
    assertEquals(3 + KDTree.maxNumOfElements*4, actual.size());
  }

  @Test
  public void EmptyRangeSearch() {
    ArrayList<CanvasElement> elements = new ArrayList<>();
    KDTree kdTree = new KDTree(elements, new Range(-2,-2,2,2));
    elements.add(new LinePath(new Node(0l,5f,5f), new Node(0l,5f,5f)));
    List<? extends CanvasElement> actual = kdTree.rangeSearch(new Range(-1f, -1f, 1f, 1f));
    assertEquals(0, actual.size());
  }

  @Test
  public void ForEdges() {
    ArrayList<Edge> edges = new ArrayList<>();

    String[] tags = new String[]{"highway","unclassified"};
    ArrayList<String> arrayList = new ArrayList<>();
    Collections.addAll(arrayList, tags);
    edges.add(new Edge(new Node(0l, 1f, 1f), new Node(1l, 0.5f, 0.5f), new Street(arrayList, 50)));

    for (int i = 0; i < KDTree.maxNumOfElements*2; i++) {
      edges.add(new Edge(new Node(0l, -1f, -1f), new Node(0l, -1f, -1f), new Street(arrayList, 50)));
      edges.add(new Edge(new Node(0l, 1f, 1f), new Node(0l, 1f, 1f), new Street(arrayList, 50)));
      edges.add(new Edge(new Node(0l, 1f, -1f), new Node(0l, 1f, -1f), new Street(arrayList, 50)));
      edges.add(new Edge(new Node(0l, -1f, 1f), new Node(0l, -1f, 1f), new Street(arrayList, 50)));
    }

    tags = new String[]{"highway","cycleway"};
    arrayList = new ArrayList<>();
    Collections.addAll(arrayList, tags); 
    edges.add(new Edge(new Node(0l, 1f, 1f), new Node(2l, 0.3f, 0.3f), new Street(arrayList, 50)));

    tags = new String[]{"highway","crossing"};
    arrayList = new ArrayList<>();
    Collections.addAll(arrayList, tags); 
    edges.add(new Edge(new Node(0l, 1f, 1f), new Node(3l, 0.1f, 0.1f), new Street(arrayList, 50)));

    KDTree kdTree = new KDTree(edges, new Range(-2,-2,2,2));

    Node actual = kdTree.nearestNeighborForEdges(new Point2D(0f, 0f), "Car");
    assertEquals(1l, actual.getAsLong());
    
    actual = kdTree.nearestNeighborForEdges(new Point2D(0f, 0f), "Bicycle");
    assertEquals(2l, actual.getAsLong());

    actual = kdTree.nearestNeighborForEdges(new Point2D(0f, 0f), "Walk");
    assertEquals(3l, actual.getAsLong());

  }

  @Test
  public void EmptyForEdges() {
    ArrayList<Edge> edges = new ArrayList<>();
    KDTree kdTree = new KDTree(edges, new Range(-2,-2,2,2));
    assertEquals(null, kdTree.nearestNeighborForEdges(new Point2D(0f, 0f), "Car"));
  }

}
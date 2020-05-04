package bong.OSMReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * NodeTest
 */
public class NodeTest {

    @Test
    public void nodeTestZero() {
        Node node = new Node(0, 0, 0);
        assertEquals(0, node.getAsLong());
        assertEquals(0, node.getLon());
        assertEquals(0, node.getLat());
    }
    @Test
    public void nodeTestPositive() {
        Node node = new Node(1, 2, 3);
        assertEquals(1, node.getAsLong());
        assertEquals(2, node.getLon());
        assertEquals(3, node.getLat());
    }
    @Test
    public void nodeTestNegative() {
        Node node = new Node(-1, -2, -3);
        assertEquals(-1, node.getAsLong());
        assertEquals(-2, node.getLon());
        assertEquals(-3, node.getLat());
    }
    @Test
    public void nodeTestDecimal() {
        Node node = new Node(1, -2.5f, 3.5f);
        assertEquals(1, node.getAsLong());
        assertEquals(-2.5, node.getLon());
        assertEquals(3.5, node.getLat());
    }
}
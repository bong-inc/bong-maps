package bfst.OSMReader;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelationTest {

    @Test
    public void RelationIDTest(){
        Relation r = new Relation(513);

        assertEquals(513, r.getAsLong());
    }

    @Test
    public void RelationNodeTest(){
        Relation r = new Relation();
        r.addNode(new Node(0, 10, 10));
        r.addNode(new Node(1, 20, 20));

        assertEquals(2, r.getNodes().size());
        assertEquals(0, r.getNodes().get(0).getAsLong());
        assertEquals(1, r.getNodes().get(1).getAsLong());
    }

    @Test
    public void RelationWayTest(){
        Way w1 = new Way();
        Node nd0 = new Node(0, 10, 10);
        Node nd1 = new Node(1, 20, 20);
        w1.addNode(nd0.getAsLong());
        w1.addNode(nd1.getAsLong());
        Relation r = new Relation();
        r.addWay(w1);

        assertEquals(1, r.getWays().size());
        assertEquals(w1, r.getWays().get(0));
    }

    @Test
    public void RelationRelationTest(){
        HashMap<Long, Relation> relationHashMap = new HashMap<>();
        Relation r1 = new Relation(1);
        relationHashMap.put(r1.getAsLong(), r1);

        Relation r = new Relation();
        r.addRefId(1L);
        assertEquals(1L, r.getIds().get(0));

        r.addRelation(relationHashMap.get(1L));
        r.nullifyIdArray();
        assertNull(r.getIds());

        assertEquals(r1, r.getRelations().get(0));
    }
}

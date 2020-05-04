package bong.OSMReader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SortedArrayListTest {
    @Test
    public void getterTest(){
        SortedArrayList<Node> sal = new SortedArrayList<>();
        sal.add(new Node(-9999,-9999,-9999));
        sal.add(new Node(-1,-1,-1));
        sal.add(new Node(0,0,0));
        sal.add(new Node(1,1,1));
        sal.add(new Node(9999,9999,9999));

        assertEquals(-9999, sal.get(-9999).getAsLong());
        assertEquals(-1, sal.get(-1).getAsLong());
        assertEquals(0, sal.get(0).getAsLong());
        assertEquals(1, sal.get(1).getAsLong());
        assertEquals(9999, sal.get(9999).getAsLong());

        assertNull(sal.get(-2));
    }

    @Test
    public void iterationTest(){
        SortedArrayList<Node> sal = new SortedArrayList<>();
        for(int i = 0; i < 11; i++){
            sal.add(new Node(i,i,i));
        }

        int cnt = 0;
        for(Node n : sal){
            cnt++;
        }

        assertEquals(11, cnt);
    }
}

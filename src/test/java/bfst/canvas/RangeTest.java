package bfst.canvas;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RangeTest {
  
    @Test
    public void testIsEnclosedBy() {
      assertTrue(new Range(0f,0f,0f,0f).isEnclosedBy(new Range(0f,0f,0f,0f)));

      assertTrue(new Range(0f,0f,0f,0f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-0.5f,-0.5f,0.5f,0.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));

      assertFalse(new Range(-1f,-1f,1f,1f).isEnclosedBy(new Range(0f,0f,0f,0f)));
      assertFalse(new Range(-1f,-1f,1f,1f).isEnclosedBy(new Range(-0.5f,-0.5f,0.5f,0.5f)));

      assertFalse(new Range(-1.5f,-0.5f,0.5f,0.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertFalse(new Range(-0.5f,-1.5f,0.5f,0.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertFalse(new Range(-0.5f,-0.5f,1.5f,0.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertFalse(new Range(-0.5f,-0.5f,0.5f,1.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));

      assertFalse(new Range(-1.5f,-1.5f,0.5f,0.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertFalse(new Range(-0.5f,-1.5f,1.5f,0.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertFalse(new Range(-0.5f,-0.5f,1.5f,1.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertFalse(new Range(-1.5f,-0.5f,0.5f,1.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));

      assertFalse(new Range(-1.5f,-1.5f,1.5f,0.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertFalse(new Range(-0.5f,-1.5f,1.5f,1.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertFalse(new Range(-1.5f,-0.5f,1.5f,1.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
      assertFalse(new Range(-1.5f,-1.5f,0.5f,1.5f).isEnclosedBy(new Range(-1f,-1f,1f,1f)));
    }

    @Test
    public void testOverlapsWith(){
      assertTrue(new Range(0f,0f,0f,0f).isEnclosedBy(new Range(0f,0f,0f,0f)));

      assertTrue(new Range(0f,0f,0f,0f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-0.5f,-0.5f,0.5f,0.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));

      assertTrue(new Range(-1f,-1f,1f,1f).overlapsWith(new Range(0f,0f,0f,0f)));
      assertTrue(new Range(-1f,-1f,1f,1f).overlapsWith(new Range(-0.5f,-0.5f,0.5f,0.5f)));

      assertTrue(new Range(-1.5f,-0.5f,0.5f,0.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-0.5f,-1.5f,0.5f,0.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-0.5f,-0.5f,1.5f,0.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-0.5f,-0.5f,0.5f,1.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));

      assertTrue(new Range(-1.5f,-1.5f,0.5f,0.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-0.5f,-1.5f,1.5f,0.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-0.5f,-0.5f,1.5f,1.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-1.5f,-0.5f,0.5f,1.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));

      assertTrue(new Range(-1.5f,-1.5f,1.5f,0.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-0.5f,-1.5f,1.5f,1.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-1.5f,-0.5f,1.5f,1.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));
      assertTrue(new Range(-1.5f,-1.5f,0.5f,1.5f).overlapsWith(new Range(-1f,-1f,1f,1f)));

      assertFalse(new Range(0,0,0,0).overlapsWith(new Range(1,1,1,1)));
      assertFalse(new Range(0,0,1,1).overlapsWith(new Range(2,2,3,3)));

      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(rangeFromCenterPoint(0,0)));    // top left
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(rangeFromCenterPoint(1.5f,0))); // top center
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(rangeFromCenterPoint(3,0)));    // top right
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(rangeFromCenterPoint(3,1.5f))); // middle right
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(rangeFromCenterPoint(3,3)));    // bottom right
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(rangeFromCenterPoint(1.5f,3))); // bottom center
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(rangeFromCenterPoint(0,3)));    // bottom left
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(rangeFromCenterPoint(0,1.5f))); // middle left

      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(0,0,3,0.1f)));
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(0,0,0.1f,3)));
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(3,0.1f,3,3)));
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(0.1f,3,3,3)));

      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(0,0,1.5f,0.1f)));
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(1.5f,0,3,0.1f)));
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(0,3,1.5f,3.1f)));
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(1.5f,3,3,3.1f)));

      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(0,0,0.1f,1.5f)));
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(0,1.5f,0.1f,3)));
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(3,0,3.1f,1.5f)));
      assertFalse(new Range(1f,1f,2f,2f).overlapsWith(new Range(3,1.5f,3.1f,3)));
    }

    public Range rangeFromCenterPoint(float x, float y){
      float diff = 0.1f;
      return new Range(x-diff, y-diff, x+diff, y+diff);
    }

}
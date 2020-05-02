package bfst.canvas;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndicatorTest {

    Indicator indicator = new Pin(1, 1, 2);

    @Test
    void scaleSvgPathTest() {
        String path = "c-9.9,0,-18,7.8,-18,17.4c0,3.8,1.3,7.4,3.6,10.4l13.6,17.8c0.3,0.4,1,0.5,1.4,0.2c0.1,-0.1,0.1,-0.1,0.2,-0.2l13.6,-17.8c2.3,-3,3.6,-6.6,3.6,-10.4c0,-9.6,-8.1,-17.4,-18,-17.4z" +
                "m-1.4,24.2c-3.8,-0.8,-6.3,-4.4,-5.5,-8.2c0.8,-3.8,4.4,-6.3,8.2,-5.5c2.8,0.6,5,2.7,5.5,5.5c0.7,3.8,-1.8,7.5,-5.5,8.2c-0.9,0.2,-1.8,0.2,-2.7,0z";

        String expected = "c-19.8 0.0 -36.0 15.6 -36.0 34.8 c0.0 7.6 2.6 14.8 7.2 20.8 l27.2 35.6 c0.6 0.8 2.0 1.0 2.8 0.4 c0.2 -0.2 0.2 -0.2 0.4 -0.4 l27.2 -35.6 c4.6 -6.0 7.2 -13.2 7.2 -20.8 c0.0 -19.2 -16.2 -34.8 -36.0 -34.8 zm-2.8 48.4 c-7.6 -1.6 -12.6 -8.8 -11.0 -16.4 c1.6 -7.6 8.8 -12.6 16.4 -11.0 c5.6 1.2 10.0 5.4 11.0 11.0 c1.4 7.6 -3.6 15.0 -11.0 16.4 c-1.8 0.4 -3.6 0.4 -5.4 0.0 z";
        String actual = indicator.scaleSvgPath(path, 2);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void circlePathTest() {
        String expected = "M 3.0 5.0 m -10.0, 0 a 10.0,10.0 0 1,0 20.0,0 a 10.0,10.0 0 1,0 -20.0,0";
        String actual = indicator.circlePath(3,5, 10);
        Assertions.assertEquals(expected, actual);
    }
}
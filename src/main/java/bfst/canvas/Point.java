package bfst.canvas;

import java.io.Serializable;

public class Point implements Serializable {
    public long x;
    public long y;

    public Point(long x, long y){
        this.x = x;
        this.y = y;
    }
}

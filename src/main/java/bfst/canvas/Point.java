package bfst.canvas;

import java.io.Serializable;

public class Point implements Serializable {
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public long x;
    public long y;

    public Point(long x, long y){
        this.x = x;
        this.y = y;
    }
}

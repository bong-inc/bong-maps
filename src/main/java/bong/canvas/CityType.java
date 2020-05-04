package bong.canvas;

public enum CityType {

    CITY(0.001000246664f, 0.024f, 15),
    TOWN(0.003000739993f, 0.45f, 10),
    HAMLET(0.05001233322f, 0.8f, 10),
    OTHER(0.02400591994f, 0.45f, 10);
    
    private final float minMxx;
    private final float maxMxx;
    private final int fontSize;

    public float getMinMxx() {
        return minMxx;
    }

    public float getMaxMxx() {
        return maxMxx;
    }

    public int getFontSize() {
        return fontSize;
    }

    CityType(float minMxx, float maxMxx, int fontSize) {
        this.minMxx = minMxx;
        this.maxMxx = maxMxx;
        this.fontSize = fontSize;
    }


}

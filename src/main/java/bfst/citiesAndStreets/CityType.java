package bfst.citiesAndStreets;

import java.io.Serializable;

public enum CityType implements Serializable {

    CITY(200, 4800, 20),
    TOWN(600, 90000, 10),
    HAMLET(10000, 90000, 10),
    OTHER(4800, 90000, 10);


    public int getMinMxx() {
        return minMxx;
    }

    public int getMaxMxx() {
        return maxMxx;
    }

    public int getFontSize() {
        return fontSize;
    }

    private final int minMxx;
    private final int maxMxx;
    private final int fontSize;

    CityType(int minMxx, int maxMxx, int fontSize) {
        this.minMxx = minMxx;
        this.maxMxx = maxMxx;
        this.fontSize = fontSize;
    }


}

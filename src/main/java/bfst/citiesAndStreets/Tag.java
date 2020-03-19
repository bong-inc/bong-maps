package bfst.citiesAndStreets;

public class Tag {
    private String key;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    private String value;

    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

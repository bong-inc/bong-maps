package bong.addressparser;

import bong.canvas.CanvasElement;
import bong.canvas.Drawer;
import bong.canvas.Range;
import javafx.geometry.Point2D;
import java.io.Serializable;
import java.util.regex.*;

public class Address extends CanvasElement implements Serializable, Comparable<Address> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final String street, house, postcode, city, municipality;

    private final float lat, lon;
    private Range boundingBox;

    public Address(
            String _street,
            String _house,
            String _postcode,
            String _city,
            String _municipality,
            float _lat,
            float _lon
    ) {
        if (_street != null) {
            street = _street.intern();
        } else {
            street = _street;
        }

        if (_house != null) {
            house = _house.intern();
        } else {
            house = _house;
        }

        if (_postcode != null) {
            postcode = _postcode.intern();
        } else {
            postcode = _postcode;
        }

        if (_city !=  null) {
            city = _city.intern();
        } else {
            city = _city;
        }

        if (_municipality != null) {
            municipality = _municipality.intern();
        } else {
            municipality = _municipality;
        }

        lat = _lat;
        lon = _lon;

        setBoundingBox();
    }

    public String toString() {
        return (
                (street != null ? street + " " : "") +
                        (house != null ? house + ", " : "") +
                        (postcode != null ? postcode + " " : "") +
                        city
        );
    }

    static String regex =
    "^ *(?<street>(?:\\d+\\. ?)?[a-zæøåÆØÅé\\-\\. ]+(?<! ))(?: (?<house>[\\da-z]+(?:\\-\\d)?)?)?,?(?: (?<floor>(?:st)|(?:\\d{1,2}(?!\\d)))?(?:\\.|,| )? ?)?(?:(?<side>(?:tv|th|mf)|(?:\\d{1,3}))\\.?)?(?:[\\.|,| ])*(?<postcode>\\d{4})? ?(?<city>[a-zæøåÆØÅ\\-\\.]+[a-zæøåÆØÅ\\-\\. ]*?[a-zæøåÆØÅ\\-\\.]*)? *$";
    static Pattern pattern = Pattern.compile(
            regex,
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    public static Address parse(String input) throws InvalidAddressException {
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            return new Builder()
                    .street(matcher.group("street"))
                    .house(matcher.group("house"))
                    .postcode(matcher.group("postcode"))
                    .city(matcher.group("city"))
                    .build();
        } else {
            throw new InvalidAddressException(input);
        }
    }

    @Override
    public int compareTo(Address that) {
        // street house floor side postcode city
        
        String this_street = (this.street != null) ? this.street.toLowerCase() : null;
        String that_street = (that.street != null) ? that.street.toLowerCase() : null;
        if(this_street == null && that_street == null) return 0;
        if(this_street == null) return -1;
        if(that_street == null) return 1;
        if(0 != this_street.compareTo(that_street)) return this_street.compareTo(that_street);

        String this_house = (this.house != null) ? this.house.toLowerCase() : null;
        String that_house = (that.house != null) ? that.house.toLowerCase() : null;
        if(this_house == null && that_house == null) return 0;
        if(this_house == null) return -1;
        if(that_house == null) return 1;
        if(0 != this_house.compareTo(that_house)) return this_house.compareTo(that_house);

        String this_city = (this.city != null) ? this.city.toLowerCase() : null;
        String that_city = (that.city != null) ? that.city.toLowerCase() : null;
        if(this_city == null && that_city == null) return 0;
        if(this_city == null) return -1;
        if(that_city == null) return 1;
        return this_city.compareTo(that_city);
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCity() {
        return city;
    }

    public String getMunicipality() {
        return municipality;
    }

    @Override
    public Point2D getCentroid() {
        return new Point2D(this.lon, this.lat);
    }

    @Override
    public Range getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox() {
        this.boundingBox = new Range(this.lon, this.lat, this.lon, this.lat);
    }

    @Override
    public void draw(Drawer gc, double scale, boolean smartTrace) {

    }

    public static class Builder {
        private String street, house, postcode, city, municipality;
        private float lat, lon;
        private boolean isEmpty = true;

        public boolean isEmpty() {
            return isEmpty;
        }

        public Builder street(String _street) {
            street = _street;
            isEmpty = false;
            return this;
        }

        public Builder house(String _house) {
            house = _house;
            return this;
        }

        public Builder floor(String _floor) {
            return this;
        }

        public Builder side(String _side) {
            return this;
        }

        public Builder postcode(String _postcode) {
            postcode = _postcode;
            return this;
        }

        public Builder city(String _city) {
            city = _city;
            return this;
        }

        public Builder municipality(String _municipality) {
            municipality = _municipality;
            return this;
        }

        public Builder lat(float _lat) {
            lat = _lat;
            return this;
        }

        public Builder lon(float _lon) {
            lon = _lon;
            return this;
        }

        public Address build() {
            return new Address(street, house, postcode, city, municipality, lat, lon);
        }
    }
}
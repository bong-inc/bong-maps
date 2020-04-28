package bfst.routeFinding;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StreetTest {

    @Test
    void testStreet() {

        ArrayList<String> tags = new ArrayList<>();
        tags.add("highway");
        tags.add("motorway");
        tags.add("sidewalk");
        tags.add("");
        tags.add("maxspeed");
        tags.add("65");
        tags.add("name");
        tags.add("testroad");

        Street street = new Street(tags, 80);

        Assertions.assertEquals(true, street.isWalking());
        Assertions.assertEquals(false, street.isBicycle());
        Assertions.assertEquals(true, street.isCar());
        Assertions.assertEquals(false, street.isOnewayBicycle());
        Assertions.assertEquals(false, street.isOnewayCar());
        Assertions.assertEquals(65, street.getMaxspeed());
        Assertions.assertEquals(Street.Role.MOTORWAY, Street.Role.MOTORWAY);
        Assertions.assertEquals("testroad", street.getName());

        tags.clear();
        tags.add("highway");
        tags.add("tertiary");
        street = new Street(tags, 80);
        Assertions.assertEquals(80, street.getMaxspeed());

        tags.clear();
        tags.add("foot");
        tags.add("designated");
        street = new Street(tags, 50);
        Assertions.assertEquals(true, street.isWalking());
    }

}
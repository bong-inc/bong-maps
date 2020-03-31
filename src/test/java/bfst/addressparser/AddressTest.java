package bfst.addressparser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class AddressTest {

    @Test
    public void testAddress() throws InvalidAddressException {
        Map<String, Address> addressTestCases = new HashMap<>();

        addressTestCases.put(
                "Rued Langgaards Vej 7, 2300 København S",
                new Address.Builder()
                        .street("Rued Langgaards Vej")
                        .house("7")
                        .postcode("2300")
                        .city("København S")
                        .build()
        );

        addressTestCases.put(
                "General Bahnsons Vej 17, st.th 2000 Frederiksberg",
                new Address.Builder()
                        .street("General Bahnsons Vej")
                        .house("17")
                        .floor("st")
                        .side("th")
                        .postcode("2000")
                        .city("Frederiksberg")
                        .build()
        );
        addressTestCases.put(
                "General Bahnsons Vej 17, st.th. 2000 Frederiksberg",
                new Address.Builder()
                        .street("General Bahnsons Vej")
                        .house("17")
                        .floor("st")
                        .side("th")
                        .postcode("2000")
                        .city("Frederiksberg")
                        .build()
        );

        addressTestCases.put(
                "General Bahnsons Vej 17, st mf, 2000 Frederiksberg",
                new Address.Builder()
                        .street("General Bahnsons Vej")
                        .house("17")
                        .floor("st")
                        .side("mf")
                        .postcode("2000")
                        .city("Frederiksberg")
                        .build()
        );

        addressTestCases.put(
                "Ringerbakken 14, 2830 Virum",
                new Address.Builder()
                        .street("Ringerbakken")
                        .house("14")
                        .floor(null)
                        .side(null)
                        .postcode("2830")
                        .city("Virum")
                        .build()
        );


        Set<Entry<String, Address>> set = addressTestCases.entrySet();
        for (Entry<String, Address> entry : set) {
            assertEquals(
                    Address.parse(entry.getKey()).toString(),
                    entry.getValue().toString()
            );
        }
    }

    public void testAddressParts(String[] addressParts)
            throws InvalidAddressException {
        var inputAddress = addressParts[0];
        var onlyStreet = addressParts[1];
        var withHouse = onlyStreet + addressParts[2];
        var withFloor = withHouse + addressParts[3];
        var withSide = withFloor + addressParts[4];
        var withPostcode = withSide + addressParts[5];
        var withCity = withPostcode + addressParts[6];

        Address parsed;
        parsed = Address.parse(inputAddress);
        parsed = Address.parse(onlyStreet);
        assertEquals(addressParts[1], parsed.street);
        parsed = Address.parse(withHouse);
        assertEquals(addressParts[2], parsed.house);
        parsed = Address.parse(withFloor);
        /*assertEquals(addressParts[3], parsed.floor);
        parsed = Address.parse(withSide);
        assertEquals(addressParts[4], parsed.side);*/
        parsed = Address.parse(withPostcode);
        assertEquals(addressParts[5], parsed.postcode);
        parsed = Address.parse(withCity);
        assertEquals(addressParts[6], parsed.city);
    }
}

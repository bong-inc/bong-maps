package bfst.addressparser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class AddressTest {

	@Test
    public void testAddress() throws InvalidAddressException {
        Map<String, Address> addressTestCases = new HashMap<>();

        addressTestCases.put("Rued Langgaards Vej 7, 2300 København S", new Address.Builder()
					.street("Rued Langgaards Vej")
					.house("7")
					.postcode("2300")
					.city("København S")
					.build()
        );

        addressTestCases.put("General Bahnsons Vej 17, st.th 2000 Frederiksberg", new Address.Builder()
					.street("General Bahnsons Vej")
					.house("17")
					.floor("st")
					.side("th")
					.postcode("2000")
					.city("Frederiksberg")
					.build()
				);
				
        addressTestCases.put("General Bahnsons Vej 17, st.th. 2000 Frederiksberg", new Address.Builder()
					.street("General Bahnsons Vej")
					.house("17")
					.floor("st")
					.side("th")
					.postcode("2000")
					.city("Frederiksberg")
					.build()
        );

        addressTestCases.put("General Bahnsons Vej 17, st mf, 2000 Frederiksberg", new Address.Builder()
					.street("General Bahnsons Vej")
					.house("17")
					.floor("st")
					.side("mf")
					.postcode("2000")
					.city("Frederiksberg")
					.build()
        );

        addressTestCases.put("Ringerbakken 14, 2830 Virum", new Address.Builder()
					.street("Ringerbakken")
					.house("14")
					.floor(null)
					.side(null)
					.postcode("2830")
					.city("Virum")
					.build()
				);
				
				addressTestCases.put("1. junivej 1a 4 th 2900 københavn", new Address.Builder()
					.street("1. junivej")
					.house("1a")
					.floor("4")
					.side("th")
					.postcode("2900")
					.city("københavn")
					.build()
				);

				addressTestCases.put("   jagtvej 1a 4 th 2900 københavn", new Address.Builder()
					.street("jagtvej")
					.house("1a")
					.floor("4")
					.side("th")
					.postcode("2900")
					.city("københavn")
					.build()
				);

				addressTestCases.put("jagtvej 1a 4th 2900 københavn", new Address.Builder()
					.street("jagtvej")
					.house("1a")
					.floor("4")
					.side("th")
					.postcode("2900")
					.city("københavn")
					.build()
				);

				addressTestCases.put("jagtvej 1a 4th 2900 københavn", new Address.Builder()
					.street("jagtvej")
					.house("1a")
					.floor("4")
					.side("th")
					.postcode("2900")
					.city("københavn").build()
				);

				addressTestCases.put("General Bahnsons Vej 17, st.th 2000 Frederiksberg", new Address.Builder()
					.street("General Bahnsons Vej")
					.house("17")
					.floor("st")
					.side("th")
					.postcode("2000")
					.city("Frederiksberg").build()
				);

				addressTestCases.put("General Bahnsons Vej 17", new Address.Builder()
					.street("General Bahnsons Vej")
					.house("17")
					.build()
				);

				addressTestCases.put("jagtvej 1, 2900 københavn", new Address.Builder()
					.street("jagtvej")
					.house("1")
					.postcode("2900")
					.city("københavn").build()
				);

				addressTestCases.put("jagtvej 1, 2900 ", new Address.Builder()
					.street("jagtvej")
					.house("1")
					.postcode("2900").build()
				);

				addressTestCases.put("jagtvej 1, 2900", new Address.Builder()
					.street("jagtvej")
					.house("1")
					.postcode("2900").build()
				);

				addressTestCases.put("jagtvej 1", new Address.Builder()
					.street("jagtvej")
					.house("1").build());

				addressTestCases.put("jagtvej 1, 2.", new Address.Builder()
					.street("jagtvej")
					.house("1")
					.floor("2").build()
				);

				addressTestCases.put("jagtvej 1, 2.th", new Address.Builder()
					.street("jagtvej")
					.house("1")
					.floor("2")
					.side("th").build()
				);

				addressTestCases.put("Blegdamsvej 29a 5 628, 2100 København Ø", new Address.Builder()
					.street("Blegdamsvej")
					.house("29a")
					.floor("5")
					.side("628")
					.postcode("2100")
					.city("København Ø").build()
				);

				addressTestCases.put("Blegdamsvej 29a 5 628 2100 København Ø", new Address.Builder()
					.street("Blegdamsvej")
					.house("29a")
					.floor("5")
					.side("628")
					.postcode("2100")
					.city("København Ø").build()
				);

				addressTestCases.put("Blegdamsvej 29a 5 628 ", new Address.Builder()
					.street("Blegdamsvej")
					.house("29a")
					.floor("5")
					.side("628").build()
				);

				addressTestCases.put("jagtvej 1, københavn", new Address.Builder()
					.street("jagtvej")
					.house("1")
					.city("københavn").build()
				);

				addressTestCases.put("jagtvej 1 københavn H", new Address.Builder()
					.street("jagtvej")
					.house("1")
					.city("københavn H").build()
				);

				addressTestCases.put("jagtvej 1 københavn H ", new Address.Builder()
					.street("jagtvej")
					.house("1")
					.city("københavn H").build()
				);

				addressTestCases.put("1.junivej 1, st th 2800 københavn", new Address.Builder()
					.street("1.junivej")
					.house("1")
					.floor("st")
					.side("th")
					.postcode("2800")
					.city("københavn").build()
				);

				addressTestCases.put("1.junivej 1, st th 2800 ", new Address.Builder()
					.street("1.junivej")
					.house("1")
					.floor("st")
					.side("th")
					.postcode("2800").build()
				);

				addressTestCases.put("1.junivej 1, st th 2800", new Address.Builder()
					.street("1.junivej")
					.house("1")
					.floor("st")
					.side("th")
					.postcode("2800").build()
				);

				addressTestCases.put("1.junivej 1, st th ", new Address.Builder()
					.street("1.junivej")
					.house("1")
					.floor("st")
					.side("th").build()
				);

				addressTestCases.put("1.junivej 1, st ", new Address.Builder()
					.street("1.junivej")
					.house("1")
					.floor("st").build()
				);

				addressTestCases.put("1.junivej 1, ", new Address.Builder()
					.street("1.junivej")
					.house("1").build()
				);

				addressTestCases.put("1.junivej 1,", new Address.Builder()
					.street("1.junivej")
					.house("1").build()
				);

				addressTestCases.put("1.junivej 1", new Address.Builder()
					.street("1.junivej")
					.house("1").build()
				);

				addressTestCases.put("1.junivej", new Address.Builder()
					.street("1.junivej").build()
				);


        Set<Entry<String, Address>> set = addressTestCases.entrySet();
        for (Entry<String, Address> entry : set) {
            assertEquals(
                    Address.parse(entry.getKey()).toString(),
                    entry.getValue().toString()
            );
        }
    }

	public void testAddressParts(String[] addressParts) throws InvalidAddressException {
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
		assertEquals(addressParts[1], parsed.getStreet());
		parsed = Address.parse(withHouse);
		assertEquals(addressParts[2], parsed.getHouse());
		parsed = Address.parse(withFloor);

		// assertEquals(addressParts[3], parsed.floor);
		parsed = Address.parse(withSide);
		// assertEquals(addressParts[4], parsed.side);

		parsed = Address.parse(withPostcode);
		assertEquals(addressParts[5], parsed.getPostcode());
		parsed = Address.parse(withCity);
		assertEquals(addressParts[6], parsed.getCity());
	}

	@Test
	public void testCompareTo(){
		List<Address> addresses = new ArrayList<>();

		addresses.add(new Address(null, null, null, null, null, 0f, 0f));
		addresses.add(new Address("Vibevej", "2", "2400", "København NV", null, 0f, 0f));
		addresses.add(new Address("Vibevej", "2", null, null, null, 0f, 0f));
		addresses.add(new Address(null, null, null, null, null, 0f, 0f));
		addresses.add(new Address("Aarhus vejen", "2", null, null, null, 0f, 0f));
		addresses.add(new Address("Aarhus vejen", null, null, null, null, 0f, 0f));
		addresses.add(new Address("Aarhus vej", "2", null, null, null, 0f, 0f));
		addresses.add(new Address("Aarhus vej", null, null, null, null, 0f, 0f));
		addresses.add(new Address("Aarhus vej", "3", null, null, null, 0f, 0f));



    Collections.sort(addresses);

		assertEquals(null, addresses.get(0).getStreet());
		assertEquals(null, addresses.get(1).getStreet());
		assertEquals("Aarhus vej", addresses.get(2).getStreet());
		assertEquals(null, addresses.get(2).getHouse());
		assertEquals("Aarhus vej", addresses.get(3).getStreet());
		assertEquals("2", addresses.get(3).getHouse());
		assertEquals("Aarhus vej", addresses.get(4).getStreet());
		assertEquals("3", addresses.get(4).getHouse());
		assertEquals("Aarhus vejen", addresses.get(5).getStreet());
		assertEquals(null, addresses.get(5).getHouse());
		assertEquals("Aarhus vejen", addresses.get(6).getStreet());
		assertEquals("2", addresses.get(6).getHouse());
		assertEquals("Vibevej", addresses.get(7).getStreet());
		assertEquals(null, addresses.get(7).getCity());
		assertEquals("Vibevej", addresses.get(8).getStreet());
		assertEquals("København NV", addresses.get(8).getCity());


	}
}

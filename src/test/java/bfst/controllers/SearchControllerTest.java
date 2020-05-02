package bfst.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bfst.addressparser.Address;
import bfst.addressparser.InvalidAddressException;

public class SearchControllerTest {
  SearchController sc;

  @BeforeEach
  public void setup() {
    sc = new SearchController();
  }

  @Test
  public void testGetBestMatches1() {
    List<Address> addresses = new ArrayList<>();
    
    addresses.add(new Address("Jagtvej", null, null, null, null, 0f, 0f));

    List<Address> bestMatches = sc.getBestMatches("Jagtvej", addresses, 5);
    
    assertEquals(1, bestMatches.size());
    assertEquals("Jagtvej", bestMatches.get(0).getStreet());
  }

  @Test
  public void testGetBestMatches2() {
    List<Address> addresses = new ArrayList<>();

    addresses.add(new Address("Jagtvej", "1", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "1", "2100", "København", null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "1A", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "10", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "100", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "11", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "13", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "2", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "3", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "3", null, null, null, 0f, 0f));
    Collections.sort(addresses);
    
    List<Address> bestMatches = sc.getBestMatches("Jagtvej 10", addresses, 5);
    
    assertEquals(5, bestMatches.size());
    assertEquals("Jagtvej", bestMatches.get(0).getStreet());
    assertEquals("10", bestMatches.get(0).getHouse());

    bestMatches = sc.getBestMatches("Jagtvej 12", addresses, 1);
    
    assertEquals("Jagtvej", bestMatches.get(0).getStreet());
    assertEquals("13", bestMatches.get(0).getHouse());

  }

  @Test
  public void testGetBestMatches3() {
    List<Address> addresses = new ArrayList<>();

    addresses.add(new Address("Vibevej", "1", "2400", "København NV", null, 0f, 0f));
    addresses.add(new Address("Vibevej", "2", "2400", "København NV", null, 0f, 0f));
    addresses.add(new Address("Vibevej", "3", "2400", "København NV", null, 0f, 0f));
    addresses.add(new Address("Vibevej", "4", "2400", "København NV", null, 0f, 0f));

    addresses.add(new Address("Vibevej", "1", "2650", "Hvidovre", null, 0f, 0f));
    addresses.add(new Address("Vibevej", "2", "2650", "Hvidovre", null, 0f, 0f));
    addresses.add(new Address("Vibevej", "3", "2650", "Hvidovre", null, 0f, 0f));
    addresses.add(new Address("Vibevej", "4", "2650", "Hvidovre", null, 0f, 0f));

    addresses.add(new Address("Vibevej", "1", "2791", "Dragør", null, 0f, 0f));
    addresses.add(new Address("Vibevej", "2", "2791", "Dragør", null, 0f, 0f));
    addresses.add(new Address("Vibevej", "3", "2791", "Dragør", null, 0f, 0f));
    addresses.add(new Address("Vibevej", "4", "2791", "Dragør", null, 0f, 0f));

    Collections.sort(addresses);
    List<Address> bestMatches = sc.getBestMatches("Vibevej 1, Dragør", addresses, 5);

    assertEquals("Vibevej", bestMatches.get(0).getStreet());
    assertEquals("Dragør", bestMatches.get(0).getCity());

  }

}
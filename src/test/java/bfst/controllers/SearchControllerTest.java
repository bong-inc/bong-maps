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
    
    assertEquals("Jagtvej", bestMatches.get(0).getStreet());
  }

  @Test
  public void testGetBestMatches2() {
    List<Address> addresses = new ArrayList<>();
    addresses.add(new Address("Jagtvej a", null, null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej b", null, null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej c", null, null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej d", null, null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej e", null, null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej f", null, null, null, null, 0f, 0f));

    List<Address> bestMatches = sc.getBestMatches("Jagtvej", addresses, 5);
    
    assertEquals("Jagtvej a", bestMatches.get(0).getStreet());
    assertEquals(5, bestMatches.size());

    bestMatches = sc.getBestMatches("Jag", addresses, 5);

    assertEquals("Jagtvej a", bestMatches.get(0).getStreet());
    assertEquals("Jagtvej b", bestMatches.get(1).getStreet());
    assertEquals("Jagtvej c", bestMatches.get(2).getStreet());
    assertEquals("Jagtvej d", bestMatches.get(3).getStreet());
    assertEquals("Jagtvej e", bestMatches.get(4).getStreet());

    bestMatches = sc.getBestMatches("Jag", addresses, 5);

    assertEquals("Jagtvej a", bestMatches.get(0).getStreet());
    assertEquals("Jagtvej b", bestMatches.get(1).getStreet());
    assertEquals("Jagtvej c", bestMatches.get(2).getStreet());
    assertEquals("Jagtvej d", bestMatches.get(3).getStreet());
    assertEquals("Jagtvej e", bestMatches.get(4).getStreet());

    bestMatches = sc.getBestMatches("Jagtvej d", addresses, 5);

    assertEquals(3, bestMatches.size());
    assertEquals("Jagtvej d", bestMatches.get(0).getStreet());
    assertEquals("Jagtvej e", bestMatches.get(1).getStreet());
    assertEquals("Jagtvej f", bestMatches.get(2).getStreet());

  }

  @Test
  public void testGetBestMatches3() {
    List<Address> addresses = new ArrayList<>();

    addresses.add(new Address("Jagtvej", "1", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "1A", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "10", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "100", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "11", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "2", null, null, null, 0f, 0f));
    addresses.add(new Address("Jagtvej", "3", null, null, null, 0f, 0f));

    Collections.sort(addresses);

    List<Address> bestMatches = sc.getBestMatches("Jagtve", addresses, 3);
    
    assertEquals("Jagtvej", bestMatches.get(0).getStreet());
    assertEquals("1", bestMatches.get(0).getHouse());

    // bestMatches = sc.getBestMatches("Jagtvej", addresses, 5);
    
    // assertEquals("Jagtvej", bestMatches.get(0).getStreet());
    // assertEquals("1", bestMatches.get(0).getHouse());

  }
}
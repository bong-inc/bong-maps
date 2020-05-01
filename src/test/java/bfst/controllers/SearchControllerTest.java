package bfst.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
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
    try {
      addresses.add(Address.parse("Jagtvej"));
    } catch (InvalidAddressException e) {
      // ignore
    }

    List<Address> bestMatches = sc.getBestMatches("Jagtvej", addresses, 5);
    
    assertEquals("Jagtvej", bestMatches.get(0).getStreet());
  }

  @Test
  public void testGetBestMatches2() {
    List<Address> addresses = new ArrayList<>();
    try {
      addresses.add(Address.parse("Jagtvej a"));
      addresses.add(Address.parse("Jagtvej b"));
      addresses.add(Address.parse("Jagtvej c"));
      addresses.add(Address.parse("Jagtvej d"));
      addresses.add(Address.parse("Jagtvej e"));
      addresses.add(Address.parse("Jagtvej f"));
    } catch (InvalidAddressException e) {
      // ignore
    }

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
}
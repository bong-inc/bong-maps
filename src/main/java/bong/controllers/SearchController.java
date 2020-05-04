package bong.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bong.addressparser.Address;
import bong.addressparser.InvalidAddressException;

public class SearchController {

  private String currentQuery = "";

  public String getCurrentQuery() {
    return currentQuery;
  }

  public void setCurrentQuery(String newCurrentQuery) {
    this.currentQuery = newCurrentQuery;
  }

  public ArrayList<Address> getBestMatches(String query, List<Address> addresses, int numberOfMatches) {
    Address inputAdress = null;
    ArrayList<Address> tempBest = new ArrayList<>();

    try {
      inputAdress = Address.parse(query);
      int index = Collections.binarySearch(addresses, inputAdress);
      tempBest = new ArrayList<>();
      for (int i = 0; i < numberOfMatches; i++) {
        if (index < 0) {
          if (-index - 1 + i >= addresses.size()) break;
          tempBest.add(addresses.get(-index - 1 + i));
        } else {
          if (index + i >= addresses.size()) break;
          tempBest.add(addresses.get(index + i));
        }
      }

    } catch (InvalidAddressException ignored) {}

    return tempBest;
  }
}
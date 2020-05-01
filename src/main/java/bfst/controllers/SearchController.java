package bfst.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bfst.OSMReader.Model;
import bfst.addressparser.Address;
import bfst.addressparser.InvalidAddressException;

public class SearchController {

  private String tempQuery = "";

  public SearchController() {

  }

  public String getTempQuery() {
    return tempQuery;
  }

  public void setTempQuery(String newTempQuery) {
    this.tempQuery = newTempQuery;
  }

  public ArrayList<Address> getBestMatches(String query, List<Address> addresses) {
    query = query.toLowerCase();
    Address inputAdress = null;
    ArrayList<Address> tempBest = new ArrayList<>();
    
    try {
      inputAdress = Address.parse(query);
      int index = Collections.binarySearch(addresses, inputAdress);
      tempBest = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        if (index < 0) {
          tempBest.add(addresses.get(-index - 1 + i));
        } else {
          tempBest.add(addresses.get(index + i));
        }
      }
      
    } catch (InvalidAddressException e) {
      System.out.println("invalid address");
    }

    return tempBest;
  }
}
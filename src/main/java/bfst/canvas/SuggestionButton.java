package bfst.canvas;

import bfst.addressparser.Address;
import javafx.scene.control.Button;

public class SuggestionButton extends Button {
  public Address address;
  public String addressString;

  public SuggestionButton(Address address) {
    this.address = address;
    setText(address.toString());
    getStyleClass().add("suggestion");
  }

  public Address getAddress() {
    return address;
  }
  
}
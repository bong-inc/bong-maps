package bfst.canvas;

import bfst.addressparser.Address;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class SuggestionButton extends Button {
  public Address address;
  public String addressString;

  public SuggestionButton(Address address, String addressString) {
    this.address = address;
    setText(address.toString());
    getStyleClass().add("suggestion");
  }

  public Address getAddress() {
    return address;
  }
  
}
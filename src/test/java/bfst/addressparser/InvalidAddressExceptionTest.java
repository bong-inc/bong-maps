package bfst.addressparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvalidAddressExceptionTest {

    @Test
    public void InvalidAddressExceptionTest(){

        try{
            Address.parse("0");
        } catch (InvalidAddressException e) {
            assertEquals("0", e.getAddressString());
            assertEquals("Cannot parse address 0", e.getMessage());
            assertEquals("InvalidAddressException: Cannot parse address 0", e.toString());
        }
    }
}

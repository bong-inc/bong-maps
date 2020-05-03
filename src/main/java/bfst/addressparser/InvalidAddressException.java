package bfst.addressparser;

/**
 * InvalidAddressException
 */
public class InvalidAddressException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String addressString;

    public InvalidAddressException(String addressString) {
        this.addressString = addressString;
    }

    /**
     * @return the addressString
     */
    public String getAddressString() {
        return addressString;
    }

    @Override
    public String getMessage() {
        return "Cannot parse address " + getAddressString();
    }

    @Override
    public String toString() {
        return "InvalidAddressException: " + getMessage();
    }
}
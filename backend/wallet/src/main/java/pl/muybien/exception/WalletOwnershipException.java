package pl.muybien.exception;

public class WalletOwnershipException extends RuntimeException {
    public WalletOwnershipException(String message) {
        super(message);
    }
}

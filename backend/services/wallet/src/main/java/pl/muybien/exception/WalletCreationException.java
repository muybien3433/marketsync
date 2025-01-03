package pl.muybien.exception;

public class WalletCreationException extends RuntimeException {
    public WalletCreationException(String message) {
        super(message);
    }
}

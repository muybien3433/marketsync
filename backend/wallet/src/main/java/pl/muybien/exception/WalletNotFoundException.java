package pl.muybien.exception;

public class WalletNotFoundException extends ServiceException {
    public WalletNotFoundException(String message) {
        super(404, "WALLET_NOT_FOUND", message);
    }
}

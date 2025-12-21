package pl.muybien.exception;

public class WalletOwnershipException extends ServiceException {
    public WalletOwnershipException(String message) {
        super(403, "WALLET_OWNERSHIP_VIOLATION", message);
    }
}

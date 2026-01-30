package pl.muybien.exception;

import io.platform.exception.ServiceException;

public class WalletNotFoundException extends ServiceException {
    public WalletNotFoundException(String message) {
        super(404, "WALLET_NOT_FOUND", message);
    }
}

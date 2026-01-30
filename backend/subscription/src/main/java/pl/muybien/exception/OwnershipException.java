package pl.muybien.exception;

import io.platform.exception.ServiceException;

public class OwnershipException extends ServiceException {
    public OwnershipException(String message) {
        super(403, "OWNERSHIP_VIOLATION", message);
    }
}

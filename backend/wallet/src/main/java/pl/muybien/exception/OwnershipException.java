package pl.muybien.exception;

public class OwnershipException extends ServiceException {
    public OwnershipException(String message) {
        super(403, "OWNERSHIP_VIOLATION", message);
    }
}

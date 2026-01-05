package pl.muybien.exception;

public class SupportDetailsNotFoundException extends ServiceException {
    public SupportDetailsNotFoundException(String message) {
        super(404, "SUPPORT_DETAILS_NOT_FOUND", message);
    }
}

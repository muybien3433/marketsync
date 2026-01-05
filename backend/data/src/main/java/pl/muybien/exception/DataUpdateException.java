package pl.muybien.exception;

public class DataUpdateException extends ServiceException {
    public DataUpdateException(String message, Throwable cause) {
        super(500, "DATA_UPDATE_FAILED", message, cause);
    }
}

package pl.muybien.exception;

public class DataUpdateException extends RuntimeException {
    public DataUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

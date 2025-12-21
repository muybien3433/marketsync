package pl.muybien.exception;

public class TransferServiceException extends ServiceException {

    public TransferServiceException(String message) {
        super(502, "TRANSFER_SERVICE_ERROR", message);
    }

    public TransferServiceException(String message, Throwable cause) {
        super(502, "TRANSFER_SERVICE_ERROR", message, cause);
    }
}

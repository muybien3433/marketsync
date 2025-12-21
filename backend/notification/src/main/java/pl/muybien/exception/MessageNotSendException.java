package pl.muybien.exception;

public class MessageNotSendException extends ServiceException {

    public MessageNotSendException(String message) {
        super(502, "MESSAGE_NOT_SENT", message);
    }

    public MessageNotSendException(String message, Throwable cause) {
        super(502, "MESSAGE_NOT_SENT", message, cause);
    }
}

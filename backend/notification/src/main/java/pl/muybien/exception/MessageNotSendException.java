package pl.muybien.exception;

import io.platform.exception.ServiceException;

public class MessageNotSendException extends ServiceException {

    public MessageNotSendException(String message) {
        super(502, "MESSAGE_NOT_SENT", message);
    }

    public MessageNotSendException(String message, Throwable cause) {
        super(502, "MESSAGE_NOT_SENT", message, cause);
    }
}

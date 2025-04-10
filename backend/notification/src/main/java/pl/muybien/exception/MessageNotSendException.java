package pl.muybien.exception;

public class MessageNotSendException extends RuntimeException {
    public MessageNotSendException(String message) {
        super(message);
    }
}

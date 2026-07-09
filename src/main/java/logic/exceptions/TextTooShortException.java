package logic.exceptions;

public class TextTooShortException extends RuntimeException {
    public TextTooShortException(String message) {
        super(message);
    }

}

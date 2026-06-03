package logic.exceptions;

public class TextTooLongException extends RuntimeException {
  public TextTooLongException(String message) {
    super(message);
  }
}

package logic.exceptions;

public class NoRequest extends RuntimeException {
  public NoRequest(String message) {
    super(message);
  }
}

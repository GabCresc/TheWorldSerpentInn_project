package logic.exceptions;

public class InvalidValueException extends Exception{

    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(String message, Throwable cause){
        super(message + cause.getMessage());
    }
}
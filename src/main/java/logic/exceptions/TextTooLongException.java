package logic.exceptions;

public class TextTooLongException extends Exception{
    private final String cause;

    public TextTooLongException(String message){
        this.cause = message;
    }

    @Override
    public String getMessage(){
        return this.cause;
    }
}
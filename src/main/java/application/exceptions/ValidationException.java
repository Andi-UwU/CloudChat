package application.exceptions;

/**
 * Exceptions related to the validation of domain objects
 */
public class ValidationException extends Exception{

    public ValidationException(String message){
        super(message);
    }
}
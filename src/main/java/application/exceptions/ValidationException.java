package application.exceptions;

/**
 * Exceptions related to the validation of domain objects
 */
public class ValidationException extends Exception{
    /**
     * Creates an exception with a specific message
     * @param message String
     */
    public ValidationException(String message){
        super(message);
    }
}
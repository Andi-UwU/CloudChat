package application.exceptions;

/**
 * Exceptions related to the repository level of the application
 */
public class RepositoryException extends Exception{

    /**
     * Creates an exception with a specific message
     * @param message String
     */
    public RepositoryException(String message){
        super(message);
    }
}

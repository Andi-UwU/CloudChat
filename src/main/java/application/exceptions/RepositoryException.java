package application.exceptions;

/**
 * Exceptions related to the repository level of the application
 */
public class RepositoryException extends Exception{

    public RepositoryException(String message){
        super(message);
    }
}

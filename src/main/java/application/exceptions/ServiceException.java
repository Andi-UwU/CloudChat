package application.exceptions;

/**
 * Exceptions related to the service level of the application
 */
public class ServiceException extends Exception{
    /**
     * Creates an exception with a specific message
     * @param message String
     */
    public ServiceException(String message){
            super(message);
        }
}

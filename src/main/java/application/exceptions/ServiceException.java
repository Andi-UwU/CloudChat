package application.exceptions;

/**
 * Exceptions related to the service level of the application
 */
public class ServiceException extends Exception{

    public ServiceException(String message){
            super(message);
        }
}

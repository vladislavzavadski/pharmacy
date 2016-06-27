package by.training.pharmacy.dao.connection_pool.exception;

/**
 * Created by vladislav on 09.06.16.
 */
public class ConnectionPoolException extends Exception {

    public ConnectionPoolException(String message, Exception ex){
        super(message, ex);
    }

    public ConnectionPoolException(String message){
        super(message);
    }
}

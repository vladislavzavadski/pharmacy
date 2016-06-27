package by.training.pharmacy.dao.exception;

/**
 * Created by vladislav on 14.06.16.
 */
public class DaoException extends Exception {


    public DaoException(String message, Exception ex){
        super(message, ex);
    }

    public DaoException(String message){
        super(message);
    }

    public DaoException(Exception ex){
        super(ex);
    }
}

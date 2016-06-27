package by.training.pharmacy.dao.impl.database;


import by.training.pharmacy.dao.*;
import by.training.pharmacy.dao.exception.DaoException;

/**
 * Created by vladislav on 13.06.16.
 */
public class DatabaseDAOFactory extends DaoFactory{

    public DatabaseDAOFactory(){}

    public UserDAO getUserDAO() throws DaoException {
        return new DatabaseUserDAO();
    }

    public DrugDAO getDrugDAO() throws DaoException{
        return new DatabaseDrugDAO();
    }

    public UserDescriptionDAO getUserDescriptionDao() throws DaoException {
        return new DatabaseUserDescriptionDAO();
    }

    public OrderDAO getOrderDao() throws DaoException {
        return new DatabaseOrderDAO();
    }

    public PrescriptionDAO getPrescriptionDAO() throws DaoException {
        return new DatabasePrescriptionDAO();
    }

    public DrugClassDAO getDrugClassDAO() throws DaoException {
        return new DatabaseDrugClassDAO();
    }

    public DrugManufacturerDAO getDrugManufacturerDAO() throws DaoException {
        return new DatabaseDrugManufacturerDao();
    }

    public RequestForPrescriptionDAO getRequestForPrescriptionDAO() throws DaoException {
        return new DatabaseRequestForPrescriptionDAO();
    }



}


package by.training.pharmacy.dao;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.dao.impl.database.*;

/**
 * Created by vladislav on 19.06.16.
 */
public abstract class DaoFactory {
    public static final int DATABASE_DAO_IMPL = 1;
    private static final DaoFactory databaseDaoFactory = new DatabaseDAOFactory();

    public static DaoFactory takeFactory(int whichFactory){
        switch (whichFactory){
            case DATABASE_DAO_IMPL:{
                return databaseDaoFactory;
            }
            default:{
                return null;
            }
        }
    }

    public abstract UserDAO getUserDAO() throws DaoException;

    public abstract DrugDAO getDrugDAO() throws DaoException;

    public abstract UserDescriptionDAO getUserDescriptionDao() throws DaoException;

    public abstract OrderDAO getOrderDao() throws DaoException;

    public abstract PrescriptionDAO getPrescriptionDAO() throws DaoException;

    public abstract DrugClassDAO getDrugClassDAO() throws DaoException;

    public abstract DrugManufacturerDAO getDrugManufacturerDAO() throws DaoException;

    public abstract RequestForPrescriptionDAO getRequestForPrescriptionDAO() throws DaoException;
}

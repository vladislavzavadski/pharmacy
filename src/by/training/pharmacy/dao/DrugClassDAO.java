package by.training.pharmacy.dao;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.drug.DrugClass;

/**
 * Created by vladislav on 19.06.16.
 */
public interface DrugClassDAO {
    DrugClass getDrugClassByName(String name) throws DaoException;
    void insertDrugClass(DrugClass drugClass) throws DaoException;
    void updateDrugClass(DrugClass drugClass, String oldDrugClassName) throws DaoException;
    void deleteDrugClass(String name) throws DaoException;
}

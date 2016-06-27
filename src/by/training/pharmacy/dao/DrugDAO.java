package by.training.pharmacy.dao;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.drug.Drug;

import java.util.List;

/**
 * Created by vladislav on 13.06.16.
 */
public interface DrugDAO {
    Drug getDrugById(int drugId) throws DaoException;
    List<Drug> getDrugsByName(String name, int limit, int startFrom) throws DaoException;
    List<Drug> getDrugsByClass(String drugClass, int limit, int startFrom) throws DaoException;
    List<Drug> getDrugsByActiveSubstance(String activeSubstance, int limit, int startFrom) throws DaoException;
    void insertDrug(Drug drug) throws DaoException;
    void updateDrug(Drug drug) throws DaoException;
    void deleteDrug(int drugId) throws DaoException;
}

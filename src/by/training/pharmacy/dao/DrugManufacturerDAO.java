package by.training.pharmacy.dao;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.drug.DrugManufacturer;

import java.util.List;

/**
 * Created by vladislav on 19.06.16.
 */
public interface DrugManufacturerDAO {
    List<DrugManufacturer> getManufacturesByCountry(String country, int limit, int startFrom) throws DaoException;
    List<DrugManufacturer> getManufacturesByName(String name, int limit, int startFrom) throws DaoException;
    DrugManufacturer getManufacturerById(int manufactureId) throws DaoException;
    void insertDrugManufacturer(DrugManufacturer drugManufacturer) throws DaoException;
    void updateManufacturer(DrugManufacturer drugManufacturer) throws DaoException;
    void deleteManufacturer(int manufacturerId) throws DaoException;
}

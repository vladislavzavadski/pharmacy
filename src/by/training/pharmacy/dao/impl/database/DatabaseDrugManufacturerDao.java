package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.DrugManufacturerDAO;
import by.training.pharmacy.dao.connection_pool.ConnectionPool;
import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;
import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.dao.impl.database.util.DatabaseOperation;
import by.training.pharmacy.domain.drug.DrugManufacturer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav on 19.06.16.
 */
public class DatabaseDrugManufacturerDao implements DrugManufacturerDAO {
    private static final String GET_MANUFACTURER_BY_COUNTRY_QUERY = "select dm_id, dm_name, dm_country, dm_description from drugs_manufactures where dm_country=? LIMIT ?, ?";
    private static final String GET_MANUFACTURER_BY_NAME_QUERY = "select dm_id, dm_name, dm_country, dm_description from drugs_manufactures where dm_name LIKE ? LIMIT ?, ?";
    private static final String GET_MANUFACTURER_BY_ID_QUERY = "select dm_id, dm_name, dm_country, dm_description from drugs_manufactures where dm_id=? LIMIT 1;";
    private static final String INSERT_MANUFACTURER_QUERY = "INSERT INTO drugs_manufactures (dm_name, dm_country, dm_description) VALUES (?, ?, ?);";
    private static final String UPDATE_MANUFACTURE_QUERY = "UPDATE drugs_manufactures SET dm_name=?, dm_country=?, dm_description=? where dm_id=?";
    private static final String DELETE_MANUFACTURER_QUERY = "DELETE FROM drugs_manufactures where dm_id=?;";
    private static final Logger logger = LogManager.getLogger(DatabaseDrugManufacturerDao.class);

    protected DatabaseDrugManufacturerDao() throws DaoException {

    }

    @Override
    public List<DrugManufacturer> getManufacturesByCountry(String country, int limit, int startFrom) throws DaoException {
        List<DrugManufacturer> drugManufacturers = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_MANUFACTURER_BY_COUNTRY_QUERY, country, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            drugManufacturers = resultSetToDrugManufacturer(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugManufacturerDao.getManufacturesByCountry", e);
            throw new DaoException("Can not load manufacturer with country = \'"+country+"\'", e);
        }
        return drugManufacturers;
    }

    @Override
    public List<DrugManufacturer> getManufacturesByName(String name, int limit, int startFrom) throws DaoException {
        List<DrugManufacturer> drugManufacturers = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_MANUFACTURER_BY_NAME_QUERY, '%'+name+'%', limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            drugManufacturers = resultSetToDrugManufacturer(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugManufacturerDao.getManufacturesByName", e);
            throw new DaoException("Can not load manufacturer with name LIKE \'"+name+"\'", e);
        }
        return drugManufacturers;
    }

    @Override
    public DrugManufacturer getManufacturerById(int manufactureId) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_MANUFACTURER_BY_ID_QUERY, manufactureId)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            List<DrugManufacturer> result = resultSetToDrugManufacturer(resultSet);
            if(!result.isEmpty()){
                return result.get(0);
            }
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugManufacturerDao.getManufacturerById", e);
            throw new DaoException("Can not load manufacturer with id = \'"+ manufactureId+"\'", e);
        }
        return null;
    }

    @Override
    public void insertDrugManufacturer(DrugManufacturer drugManufacturer) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(INSERT_MANUFACTURER_QUERY, drugManufacturer.getName(), drugManufacturer.getCountry(), drugManufacturer.getDescription())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugManufacturerDao.insertDrugManufacturer", e);
            throw new DaoException("Can not insert manufacturer "+drugManufacturer, e);
        }
    }

    @Override
    public void updateManufacturer(DrugManufacturer drugManufacturer) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(UPDATE_MANUFACTURE_QUERY, drugManufacturer.getName(), drugManufacturer.getCountry(), drugManufacturer.getDescription(), drugManufacturer.getId())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugManufacturerDao.updateManufacturer", e);
            throw new DaoException("Can not update drugManufacturer "+drugManufacturer);
        }
    }

    @Override
    public void deleteManufacturer(int manufacturerId) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(DELETE_MANUFACTURER_QUERY, manufacturerId)){
            databaseOperation.invokeReadOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugManufacturerDao.deleteManufacturer", e);
            throw new DaoException("Can not delete manufacturer with id = "+manufacturerId, e);
        }
    }

    private List<DrugManufacturer> resultSetToDrugManufacturer(ResultSet resultSet) throws SQLException {
        List<DrugManufacturer> result = new ArrayList<>();
        while (resultSet.next()) {
            DrugManufacturer drugManufacturer = new DrugManufacturer();
            drugManufacturer.setId(resultSet.getInt("dm_id"));
            drugManufacturer.setName(resultSet.getString("dm_name"));
            drugManufacturer.setDescription(resultSet.getString("dm_description"));
            drugManufacturer.setCountry(resultSet.getString("dm_country"));
            result.add(drugManufacturer);
        }
        return result;
    }


}

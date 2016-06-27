package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.DrugClassDAO;
import by.training.pharmacy.dao.connection_pool.ConnectionPool;
import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;
import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.dao.impl.database.util.DatabaseOperation;
import by.training.pharmacy.domain.drug.DrugClass;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by vladislav on 19.06.16.
 */
public class DatabaseDrugClassDAO implements DrugClassDAO {
    private static final String GET_CLASS_BY_NAME_QUERY = "select dr_class_name, dr_class_description from drug_classes where dr_class_name=? LIMIT 1;";
    private static final String INSERT_DRUG_CLASS_QUERY = "insert into drug_classes (dr_class_name, dr_class_description) VALUES(?,?);";
    private static final String UPDATE_DRUG_CLASS_QUERY = "update drug_classes set dr_class_name=?, dr_class_description=? WHERE dr_class_name=?;";
    private static final String DELETE_DRUG_CLASS_QUERY = "delete from drug_classes where dr_class_name=?;";
    private static final Logger logger = LogManager.getLogger(DatabaseDrugClassDAO.class);
    protected DatabaseDrugClassDAO() throws DaoException {

    }

    @Override
    public DrugClass getDrugClassByName(String name) throws DaoException {
        try(DatabaseOperation databaseOperation = new DatabaseOperation(GET_CLASS_BY_NAME_QUERY, name)) {
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            List<DrugClass> result = resultSetToDrugClass(resultSet);
            if(!result.isEmpty()){
                return result.get(0);
            }
        } catch (Exception ex) {
            logger.error("Method: DatabaseDrugClassDAO.getDrugClassByName", ex);
            throw new DaoException("Can not load drug class from database with name = \'"+name+"\'", ex);
        }
        return null;
    }

    @Override
    public void insertDrugClass(DrugClass drugClass) throws DaoException {
        try(DatabaseOperation databaseOperation = new DatabaseOperation(INSERT_DRUG_CLASS_QUERY, drugClass.getName(), drugClass.getDescription());) {
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugClassDAO.insertDrugClass", e);
            throw new DaoException("Can not insert drug class " + drugClass, e);
        }
    }

    @Override
    public void updateDrugClass(DrugClass drugClass, String oldDrugName) throws DaoException {
        try(DatabaseOperation databaseOperation  = new DatabaseOperation(UPDATE_DRUG_CLASS_QUERY, drugClass.getName(), drugClass.getDescription(), oldDrugName)) {

            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugClassDAO.updateDrugClass", e);
            throw new DaoException("Can not update drug class " +drugClass, e);
        }

    }

    @Override
    public void deleteDrugClass(String name) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(DELETE_DRUG_CLASS_QUERY, name)){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugClassDAO.deleteDrugClass", e);
            throw new DaoException("Can not delete drug class with name = \'"+name+"\'", e);
        }
    }

    private List<DrugClass> resultSetToDrugClass(ResultSet resultSet) throws SQLException {
        List<DrugClass> result = new ArrayList<>();
        while (resultSet.next()) {
            DrugClass drugClass = new DrugClass();
            drugClass.setName(resultSet.getString("dr_class_name"));
            drugClass.setDescription(resultSet.getString("dr_class_description"));
            result.add(drugClass);
        }
        return result;

    }
}

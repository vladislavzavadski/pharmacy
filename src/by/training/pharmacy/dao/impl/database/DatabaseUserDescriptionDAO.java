package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.UserDescriptionDAO;
import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;
import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.dao.impl.database.util.DatabaseOperation;
import by.training.pharmacy.domain.user.UserDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by vladislav on 18.06.16.
 */
public class DatabaseUserDescriptionDAO implements UserDescriptionDAO {
    private static final String INSERT_DESCRIPTION_QUERY = "INSERT INTO staff_descriptions (sd_user_login, sd_specialization, sd_description) VALUES (?, ?, ?);";
    private static final String GET_DESCRIPTION_QUERY = "SELECT sd_user_login, sd_specialization, sd_description FROM staff_descriptions WHERE sd_user_login=? limit 1;";
    private static final String UPDATE_DESCRIPTION_QUERY = "UPDATE staff_descriptions SET sd_specialization=?, sd_description=? WHERE sd_user_login=?;";
    private static final String DELETE_DESCRIPTION_QUERY = "DELETE FROM staff_descriptions WHERE sd_user_login=?;";
    private static final Logger logger = LogManager.getLogger(DatabaseUserDescriptionDAO.class);

    public DatabaseUserDescriptionDAO() throws DaoException {

    }

    @Override
    public void insertUserDescription(UserDescription userDescription) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(INSERT_DESCRIPTION_QUERY, userDescription.getUserLogin(), userDescription.getSpecialization(), userDescription.getDescription())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDescriptionDAO.insertUserDescription", e);
            throw new DaoException("Can not insert new description "+userDescription, e);
        }
    }

    @Override
    public UserDescription getUserDescriptionByLogin(String userLogin) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_DESCRIPTION_QUERY, userLogin)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            return resultSetToUserDescription(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDescriptionDAO.getUserDescriptionByLogin", e);
            throw new DaoException("Can load user description with login = \'"+userLogin+"\'", e);
        }
    }

    @Override
    public void updateUserDescription(UserDescription userDescription) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(UPDATE_DESCRIPTION_QUERY, userDescription.getSpecialization(), userDescription.getDescription(), userDescription.getUserLogin())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDescriptionDAO.updateUserDescription", e);
            throw new DaoException("Cannot update user's description with login = \'"+userDescription.getUserLogin()+"\'", e);
        }
    }

    @Override
    public void deleteUserDescription(String userLogin) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(DELETE_DESCRIPTION_QUERY, userLogin)){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDescriptionDAO.deleteUserDescription", e);
            throw new DaoException("Can not delete user description with login = \'"+userLogin+"\'", e);
        }
    }


    private UserDescription resultSetToUserDescription(ResultSet resultSet) throws SQLException {
        UserDescription userDescription = new UserDescription();
        userDescription.setUserLogin(resultSet.getString("sd_user_login"));
        userDescription.setSpecialization(resultSet.getString("sd_specialization"));
        userDescription.setDescription(resultSet.getString("sd_description"));

        return userDescription;
    }
}

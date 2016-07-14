package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.UserDAO;
import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;
import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.dao.impl.database.util.DatabaseOperation;
import by.training.pharmacy.domain.user.User;
import by.training.pharmacy.domain.user.UserDescription;
import by.training.pharmacy.domain.user.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav on 14.06.16.
 */
public class DatabaseUserDAO implements UserDAO {

    private static final String GET_USER_QUERY = "select us_login, us_first_name, us_second_name, us_image, us_mail, us_phone, us_group from users WHERE  us_login=? and us_password=md5(?);";
    private static  final String GET_USER_BY_LOGIN_QUERY = "SELECT us_login, us_first_name, us_second_name, us_group, us_mail, us_phone, us_image, sd_specialization, sd_description FROM users LEFT JOIN staff_descriptions ON users.us_login = staff_descriptions.sd_user_login WHERE us_login=?;";
    private static final String SEARCH_USER_BY_ROLE_QUERY = "SELECT us_login, us_first_name, us_second_name, us_mail, us_phone, us_image, sd_specialization, sd_description FROM users LEFT JOIN staff_descriptions ON us_login=sd_user_login WHERE us_group=? LIMIT ?, ?;";
    private static final String SEARCH_USERS_QUERY = "select us_login, us_first_name, us_second_name, us_image, us_mail, us_phone, us_group, sd_specialization, sd_description from users LEFT JOIN staff_descriptions on sd_user_login=us_login where us_first_name LIKE ? and us_second_name LIKE ? LIMIT ?, ?;";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (us_login, us_password, us_first_name, us_second_name, us_group, us_image, us_mail, us_phone) VALUES (?,md5(?),?,?,?,?,?,?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET us_password=md5(?), us_first_name=?, us_second_name=?, us_image=?, us_mail=?, us_phone=? WHERE us_login=?;";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE us_login=?";
    private static final String SEARCH_USER_BY_SPECIALIZATION_QUERY = "SELECT us_first_name, us_second_name, us_image, us_group, us_mail, us_phone sd_specialization, sd_description FROM users INNER JOIN staff_descriptions ON users.us_login = staff_descriptions.sd_user_login WHERE sd_specialization=? LIMIT ?, ?;";
    private static final Logger logger = LogManager.getLogger(DatabaseUserDAO.class);

    public DatabaseUserDAO() throws DaoException{

    }
    @Override
    public User userAuthentication(String login, String password) throws DaoException {

        User user = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_USER_QUERY, login, password)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            List<User> result = resultSetToUser(resultSet);
            if(!result.isEmpty()){
                user = result.get(0);
            }
            return user;
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDAO.userAuthentication", e);
            throw new DaoException("Cannot load user from database with login = \'"+login+"\' and password = \'"+password+"\'",e);
        }

    }

    @Override
    public User getUserByLogin(String login) throws DaoException {
        User user = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_USER_BY_LOGIN_QUERY, login)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            List<User> result = resultSetToUser(resultSet);
            if(!result.isEmpty()){
                user = result.get(0);
            }
            return user;
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDAO.getUserByLogin", e);
            throw new DaoException("Can not get user with login = \'"+login+"\'", e);
        }

    }

    @Override
    public List<User> searchUsersByRole(UserRole userRole, int limit, int startFrom) throws DaoException {

        List<User> users;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(SEARCH_USER_BY_ROLE_QUERY, userRole.toString().toLowerCase(), limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            users = resultSetToUser(resultSet);
            return users;
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDAO.searchUsersByRole", e);
            throw new DaoException("Can not search user with role \'"+userRole+"\'", e);
        }

    }

    @Override
    public List<User> searchUsersByName(String firstName, String secondName, int limit, int startFrom) throws DaoException {

        List<User> users;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(SEARCH_USERS_QUERY, firstName, secondName, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            users = resultSetToUser(resultSet);
            return users;
        } catch (Exception ex) {
            logger.error("Method: DatabaseUserDAO.searchUsersByName", ex);
            throw new DaoException("Can not search users in database", ex);
        }
    }

    @Override
    public void insertUser(User user) throws DaoException {

        try (DatabaseOperation databaseOperation = new DatabaseOperation(INSERT_USER_QUERY, user.getLogin(), user.getPassword(),
                user.getFirstName(), user.getSecondName(), user.getUserRole().toString().toLowerCase(),
                user.getUserImage(), user.getMail(), user.getPhone())){
            databaseOperation.invokeWriteOperation();

        } catch (Exception e) {
            logger.error("Method: DatabaseUserDAO.insertUser", e);
            throw new DaoException("Can not insert user to database", e);
        }
    }

    @Override
    public void updateUser(User user) throws DaoException {

        try (DatabaseOperation databaseOperation = new DatabaseOperation(UPDATE_USER_QUERY, user.getPassword(),
                user.getFirstName(), user.getSecondName(),
                user.getUserImage(), user.getMail(), user.getPhone(), user.getLogin())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDAO.updateUser", e);
            throw new DaoException("Cannot update user", e);
        }
    }

    @Override
    public void deleteUser(String login) throws DaoException {

        try (DatabaseOperation databaseOperation = new DatabaseOperation(DELETE_USER_QUERY, login)){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDAO.deleteUser", e);
            throw new DaoException("Can not delete user with login \'"+login+"\'", e);
        }
    }

    @Override
    public List<User> getUsersBySpecialization(String specialization, int limit, int startFrom) throws DaoException {

        List<User> users;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(SEARCH_USER_BY_SPECIALIZATION_QUERY, specialization, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            users = resultSetToUser(resultSet);
            return users;
        } catch (Exception e) {
            logger.error("Method: DatabaseUserDAO.getUsersBySpecialization", e);
            throw new DaoException("Can not search users with specialization = \'"+specialization+"\'", e);
        }
    }
    private List<User> resultSetToUser(ResultSet resultSet) throws SQLException {
        List<User> result = new ArrayList<>();
        while (resultSet.next()) {
            User user = new User();
            UserDescription userDescription = new UserDescription();
            user.setUserDescription(userDescription);
            user.setLogin(resultSet.getString(TableColumn.USER_LOGIN));
            user.setUserRole(UserRole.valueOf(resultSet.getString(TableColumn.USER_GROUP).toUpperCase()));
            user.setFirstName(resultSet.getString(TableColumn.USER_FIRST_NAME));
            user.setSecondName(resultSet.getString(TableColumn.USER_SECOND_NAME));
            user.setMail(resultSet.getString(TableColumn.USER_MAIL));
            user.setPhone(resultSet.getString(TableColumn.USER_PHONE));
            user.setUserImage(resultSet.getBytes(TableColumn.USER_IMAGE));
            userDescription.setDescription(resultSet.getString(TableColumn.USER_DESCRIPTION));
            userDescription.setSpecialization(resultSet.getString(TableColumn.USER_SPECIALIZATION));
        }
        return result;
    }
}

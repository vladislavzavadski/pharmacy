package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.domain.user.User;
import by.training.pharmacy.domain.user.UserDescription;
import by.training.pharmacy.domain.user.UserRole;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by vladislav on 20.06.16.
 */
public class DatabaseUserDAOTest {

    @Test
    public void userAuthenticationTest()throws Exception{
        DatabaseUserDAO databaseUserDAO = new DatabaseUserDAO();
        User actual = databaseUserDAO.userAuthentication(DatabaseDAOTestConstant.USER_LOGIN_2, DatabaseDAOTestConstant.USER_PASSWORD);
        User expected = getUniqueUser(DatabaseDAOTestConstant.USER_AUTHENTICATON_QUERY, DatabaseDAOTestConstant.USER_LOGIN_2, DatabaseDAOTestConstant.USER_PASSWORD);

        assertEquals(expected, actual);
    }

    @Test
    public void getUserByLoginTest() throws Exception{
        DatabaseUserDAO databaseUserDAO = new DatabaseUserDAO();
        User actual = databaseUserDAO.getUserByLogin(DatabaseDAOTestConstant.USER_LOGIN_2);
        User expected = getUniqueUser(DatabaseDAOTestConstant.GET_USER_BY_LOGIN_QUERY, DatabaseDAOTestConstant.USER_LOGIN_2);
        assertEquals(expected, actual);
    }

    @Test
    public void searchUserByRoleTest() throws Exception{
        DatabaseUserDAO databaseUserDAO = new DatabaseUserDAO();
        List<User> actual = databaseUserDAO.searchUsersByRole(UserRole.CLIENT, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        List<User> expected = getUserList(DatabaseDAOTestConstant.SEARCH_USERS_BY_ROLE_QUERY,UserRole.CLIENT.toString().toLowerCase(), DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);

    }

    @Test
    public void getUserBySpecializationTest() throws Exception{
        DatabaseUserDAO databaseUserDAO = new DatabaseUserDAO();
        List<User> actual = databaseUserDAO.getUsersBySpecialization(DatabaseDAOTestConstant.USER_SPECIALIZATION_2, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        List<User> expected = getUserList(DatabaseDAOTestConstant.GET_USER_BY_SPECIALIZATION_QUERY, DatabaseDAOTestConstant.USER_SPECIALIZATION_2, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);
    }

    @Test
    public void searchUserByNameTest() throws Exception{
        DatabaseUserDAO databaseUserDAO = new DatabaseUserDAO();
        List<User> actual = databaseUserDAO.searchUsersByName(DatabaseDAOTestConstant.USER_FIRST_NAME_2, DatabaseDAOTestConstant.USER_SECOND_NAME_2, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        List<User> expected = getUserList(DatabaseDAOTestConstant.SEARCH_USER_BY_NAME_QUERY, "%"+DatabaseDAOTestConstant.USER_FIRST_NAME_2+"%", "%"+DatabaseDAOTestConstant.USER_SECOND_NAME_2+"%", DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);
    }

    @Test
    public void insertUserTest() throws Exception{
        DatabaseUserDAO databaseUserDAO = new DatabaseUserDAO();
        User expected = databaseUserDAO.getUserByLogin(DatabaseDAOTestConstant.USER_LOGIN_2);
        expected.setLogin(DatabaseDAOTestConstant.USER_LOGIN_5);
        expected.setFirstName(DatabaseDAOTestConstant.USER_FIRST_NAME_3);
        expected.setSecondName(DatabaseDAOTestConstant.USER_SECOND_NAME_3);
        expected.setPassword(DatabaseDAOTestConstant.USER_PASSWORD_2);
        databaseUserDAO.insertUser(expected);
        expected.setLogin(null);
        expected.setPassword(null);
        User actual = databaseUserDAO.getUserByLogin(DatabaseDAOTestConstant.USER_LOGIN_5);
        assertEquals(expected, actual);
        databaseUserDAO.deleteUser(DatabaseDAOTestConstant.USER_LOGIN_5);
    }

    @Test
    public void updateUserTest() throws Exception{
        DatabaseUserDAO databaseUserDAO = new DatabaseUserDAO();
        User expected = databaseUserDAO.getUserByLogin(DatabaseDAOTestConstant.USER_LOGIN_2);
        expected.setFirstName(DatabaseDAOTestConstant.USER_FIRST_NAME_3);
        databaseUserDAO.updateUser(expected);
        User actual = databaseUserDAO.getUserByLogin(DatabaseDAOTestConstant.USER_LOGIN_2);
        assertEquals(expected, actual);
        expected.setFirstName(DatabaseDAOTestConstant.USER_FIRST_NAME);
        databaseUserDAO.updateUser(expected);
    }

    @Test
    public void deleteUserTest() throws Exception{
        DatabaseUserDAO databaseUserDAO = new DatabaseUserDAO();
        User temp = databaseUserDAO.getUserByLogin(DatabaseDAOTestConstant.USER_LOGIN);
        temp.setPassword(DatabaseDAOTestConstant.USER_PASSWORD);
        databaseUserDAO.deleteUser(DatabaseDAOTestConstant.USER_LOGIN);
        User result = databaseUserDAO.getUserByLogin(DatabaseDAOTestConstant.USER_LOGIN);
        assertNull(result);
        databaseUserDAO.insertUser(temp);

    }

    public User resultSetToUser(ResultSet resultSet){
        User user = new User();
        UserDescription userDescription = new UserDescription();
        user.setUserDescription(userDescription);

        try {
            user.setLogin(resultSet.getString("us_login"));
        } catch (SQLException e) {
            user.setLogin(null);
        }
        try {
            user.setPassword(resultSet.getString("us_password"));
        } catch (SQLException e) {
            user.setPassword(null);
        }
        try {
            user.setUserRole(UserRole.valueOf(resultSet.getString("us_group").toUpperCase()));
        } catch (SQLException e) {
            user.setUserRole(null);
        }
        try {
            user.setFirstName(resultSet.getString("us_first_name"));
        } catch (SQLException e) {
            user.setFirstName(null);
        }
        try {
            user.setSecondName(resultSet.getString("us_second_name"));
        } catch (SQLException e) {
            user.setSecondName(null);
        }
        try {
            user.setMail(resultSet.getString("us_mail"));
        } catch (SQLException e) {
            user.setMail(null);
        }
        try {
            user.setPhone(resultSet.getString("us_phone"));
        } catch (SQLException e) {
            user.setPhone(null);
        }
        try {
            user.setUserImage(resultSet.getBytes("us_image"));
        } catch (SQLException e) {
            user.setUserImage(null);
        }
        try {
            userDescription.setDescription(resultSet.getString("sd_description"));
        } catch (SQLException e) {
            userDescription.setDescription(null);
        }
        try {
            userDescription.setSpecialization(resultSet.getString("sd_specialization"));
        } catch (SQLException e) {
            userDescription.setSpecialization(null);
        }
        return user;
    }

    public User getUniqueUser(String query, Object ... params){
        User result = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DatabaseDAOTestConstant.DATABASE_URL, DatabaseDAOTestConstant.DATABASE_USER, DatabaseDAOTestConstant.DATABASE_PASSWORD);
            preparedStatement = connection.prepareStatement(query);
            for(int i=0; i<params.length; i++)
                preparedStatement.setObject(i+1, params[i]);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                result = resultSetToUser(resultSet);
            }
            return result;

        } catch (SQLException e) {
            return null;
        }
        finally {
            try {
                if(resultSet!=null)
                    resultSet.close();
                if(preparedStatement!=null)
                    preparedStatement.close();
                if(connection!=null)
                    connection.close();
            } catch (SQLException e) {
                return null;
            }
        }
    }

    public List<User> getUserList(String query, Object... params){
        List<User> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DatabaseDAOTestConstant.DATABASE_URL, DatabaseDAOTestConstant.DATABASE_USER, DatabaseDAOTestConstant.DATABASE_PASSWORD);
            preparedStatement = connection.prepareStatement(query);
            for (int i=0; i<params.length; i++){
                preparedStatement.setObject(i+1, params[i]);
            }
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSetToUser(resultSet));
            }
            return result;

        } catch (SQLException e) {
            return null;
        }
        finally {
            try {
                if(resultSet!=null)
                    resultSet.close();
                if(preparedStatement!=null)
                    preparedStatement.close();
                if(connection!=null)
                    connection.close();
            } catch (SQLException e) {
                return null;
            }
        }
    }
}

package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.domain.user.UserDescription;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by vladislav on 20.06.16.
 */
public class DatabaseUserDescriptionTest {

    @Test
    public void insertUserDescriptionTest() throws Exception{
        DatabaseUserDescriptionDAO databaseUserDescriptionDAO = new DatabaseUserDescriptionDAO();
        UserDescription userDescription = new UserDescription();
        userDescription.setUserLogin(DatabaseDAOTestConstant.USER_LOGIN_3);
        userDescription.setDescription(DatabaseDAOTestConstant.USER_DESCRIPTION);
        userDescription.setSpecialization(DatabaseDAOTestConstant.USER_SPECIALIZATION);
        databaseUserDescriptionDAO.insertUserDescription(userDescription);
        UserDescription userDescription1 = databaseUserDescriptionDAO.getUserDescriptionByLogin(DatabaseDAOTestConstant.USER_LOGIN_3);
        assertEquals(userDescription, userDescription1);
        databaseUserDescriptionDAO.deleteUserDescription(DatabaseDAOTestConstant.USER_LOGIN_3);
    }

    @Test
    public void getUserDescriptionByLoginTest() throws Exception{
        DatabaseUserDescriptionDAO databaseUserDescriptionDAO = new DatabaseUserDescriptionDAO();
        UserDescription actual = databaseUserDescriptionDAO.getUserDescriptionByLogin(DatabaseDAOTestConstant.USER_LOGIN_4);
        UserDescription expected = getUniqueUserDescription(DatabaseDAOTestConstant.GET_USER_DESCRIPTION_BY_LOGIN_QUERY, DatabaseDAOTestConstant.USER_LOGIN_4);
        assertEquals(expected, actual);
    }

    @Test
    public void updateUserDescriptionTest() throws Exception{
        DatabaseUserDescriptionDAO databaseUserDAO = new DatabaseUserDescriptionDAO();
        UserDescription expected = databaseUserDAO.getUserDescriptionByLogin(DatabaseDAOTestConstant.USER_LOGIN_4);
        expected.setDescription(DatabaseDAOTestConstant.USER_DESCRIPTION_2);
        databaseUserDAO.updateUserDescription(expected);
        UserDescription actual = databaseUserDAO.getUserDescriptionByLogin(DatabaseDAOTestConstant.USER_LOGIN_4);
        assertEquals(expected, actual);
    }

    @Test
    public void deleteUserDescriptionTest() throws Exception{
        DatabaseUserDescriptionDAO databaseUserDescriptionDAO = new DatabaseUserDescriptionDAO();
        UserDescription temp = databaseUserDescriptionDAO.getUserDescriptionByLogin(DatabaseDAOTestConstant.USER_LOGIN_4);
        databaseUserDescriptionDAO.deleteUserDescription(DatabaseDAOTestConstant.USER_LOGIN_4);
        UserDescription result = databaseUserDescriptionDAO.getUserDescriptionByLogin(DatabaseDAOTestConstant.USER_LOGIN_4);
        assertNull(result);
        databaseUserDescriptionDAO.insertUserDescription(temp);
    }

    protected UserDescription resultSetToUserDescription(ResultSet resultSet) {
        UserDescription userDescription = new UserDescription();
        try {
            userDescription.setUserLogin(resultSet.getString("sd_user_login"));
        } catch (SQLException e) {
            try {
                userDescription.setUserLogin(resultSet.getString("us_login"));
            } catch (SQLException ex){
                userDescription.setUserLogin(null);
            }
        }

        try {
            userDescription.setSpecialization(resultSet.getString("sd_specialization"));
        } catch (SQLException e) {
            userDescription.setSpecialization(null);
        }

        try {
            userDescription.setDescription(resultSet.getString("sd_description"));
        } catch (SQLException e) {
            userDescription.setDescription(null);
        }

        return userDescription;
    }

    public UserDescription getUniqueUserDescription(String query, Object ... params){
        UserDescription result = null;
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
                result = resultSetToUserDescription(resultSet);
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

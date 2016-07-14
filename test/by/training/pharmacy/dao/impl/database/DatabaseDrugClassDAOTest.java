package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.domain.drug.DrugClass;
import org.junit.Test;

import java.sql.*;

import static junit.framework.Assert.*;

/**
 * Created by vladislav on 19.06.16.
 */
public class DatabaseDrugClassDAOTest {
    @Test
    public void getDrugClassByNameTest() throws Exception{
        DatabaseDrugClassDAO drugClassDAO = new DatabaseDrugClassDAO();
        DrugClass factResult = drugClassDAO.getDrugClassByName(DatabaseDAOTestConstant.CLASS_NAME);

        DrugClass expectedResult = getDrugClassByName(DatabaseDAOTestConstant.CLASS_NAME);
        assertEquals(expectedResult, factResult);
    }

    @Test
    public void insertDrugClassTest() throws Exception{
        DrugClass expectedResult = new DrugClass();
        DrugClass actualResult;
        expectedResult.setName(DatabaseDAOTestConstant.CLASS_NAME_2);
        expectedResult.setDescription(DatabaseDAOTestConstant.CLASS_DESCRIPTION);
        DatabaseDrugClassDAO databaseDrugClassDAO = new DatabaseDrugClassDAO();
        databaseDrugClassDAO.insertDrugClass(expectedResult);
        actualResult = databaseDrugClassDAO.getDrugClassByName(DatabaseDAOTestConstant.CLASS_NAME_2);
        assertEquals(expectedResult, actualResult);
        databaseDrugClassDAO.deleteDrugClass(DatabaseDAOTestConstant.CLASS_NAME_2);
    }

    @Test
    public void updateDrugClassTest() throws Exception{
        DatabaseDrugClassDAO databaseDrugClassDAO = new DatabaseDrugClassDAO();
        DrugClass expectedResult = new DrugClass();
        DrugClass oldResult;
        DrugClass actualResult;
        oldResult = databaseDrugClassDAO.getDrugClassByName(DatabaseDAOTestConstant.CLASS_NAME_3);
        expectedResult.setName(DatabaseDAOTestConstant.CLASS_NAME_3);
        expectedResult.setDescription(DatabaseDAOTestConstant.CLASS_DESCRIPTION_2);
        databaseDrugClassDAO.updateDrugClass(expectedResult, DatabaseDAOTestConstant.CLASS_NAME_4);
        actualResult = databaseDrugClassDAO.getDrugClassByName(DatabaseDAOTestConstant.CLASS_NAME_3);
        assertEquals(expectedResult, actualResult);
        databaseDrugClassDAO.updateDrugClass(oldResult, actualResult.getName());
    }

    @Test
    public void deleteDrugClassTest() throws Exception{
        DatabaseDrugClassDAO databaseDrugClassDAO = new DatabaseDrugClassDAO();
        DrugClass drugClass = databaseDrugClassDAO.getDrugClassByName(DatabaseDAOTestConstant.CLASS_NAME);
        databaseDrugClassDAO.deleteDrugClass(DatabaseDAOTestConstant.CLASS_NAME);
        DrugClass result = databaseDrugClassDAO.getDrugClassByName(DatabaseDAOTestConstant.CLASS_NAME);
        assertNull(result);
        databaseDrugClassDAO.insertDrugClass(drugClass);
    }

    private DrugClass getDrugClassByName(String name){
        DrugClass result = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DatabaseDAOTestConstant.DATABASE_URL, DatabaseDAOTestConstant.DATABASE_USER, DatabaseDAOTestConstant.DATABASE_PASSWORD);
            preparedStatement = connection.prepareStatement(DatabaseDAOTestConstant.GET_DRUG_CLASS_BY_NAME_QUERY);
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                result = new DrugClass();
                result.setName(resultSet.getString(1));
                result.setDescription(resultSet.getString(2));
            }
            return result;

        } catch (SQLException e) {
            return null;
        }
        finally {
                try {
                    if(resultSet!=null) {
                        resultSet.close();
                    }
                    if(preparedStatement!=null) {
                        preparedStatement.close();
                    }
                    if(connection!=null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    return null;
                }
        }

    }
}

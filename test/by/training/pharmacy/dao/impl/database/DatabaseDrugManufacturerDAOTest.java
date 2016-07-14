package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.drug.DrugManufacturer;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by vladislav on 20.06.16.
 */
public class DatabaseDrugManufacturerDAOTest {

    @Test
    public void getManufacturesByCountryTest() throws Exception{
        List<DrugManufacturer> expected = getManufacturerList(DatabaseDAOTestConstant.GET_MANUFACTURES_BY_COUNTRY_QUERY, DatabaseDAOTestConstant.MANUFACTURER_COUNTRY_2, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        DatabaseDrugManufacturerDao databaseDrugManufacturerDao = new DatabaseDrugManufacturerDao();
        List<DrugManufacturer> actual = databaseDrugManufacturerDao.getManufacturesByCountry(DatabaseDAOTestConstant.MANUFACTURER_COUNTRY_2, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);
    }

    @Test
    public void getManufacturesByNameTest() throws Exception{
        List<DrugManufacturer> expected = getManufacturerList(DatabaseDAOTestConstant.GET_MANUFACTURES_BY_NAME_QUERY, DatabaseDAOTestConstant.MANUFACTURER_NAME_2, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        DatabaseDrugManufacturerDao databaseDrugManufacturerDao = new DatabaseDrugManufacturerDao();
        List<DrugManufacturer> actual = databaseDrugManufacturerDao.getManufacturesByName(DatabaseDAOTestConstant.MANUFACTURER_NAME_2, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);
    }

    @Test
    public void getManufacturerByIdTest()throws Exception{
        DrugManufacturer expected = getManufacturerById(DatabaseDAOTestConstant.ID_1);
        DatabaseDrugManufacturerDao databaseDrugManufacturerDao = new DatabaseDrugManufacturerDao();
        DrugManufacturer actual = databaseDrugManufacturerDao.getManufacturerById(DatabaseDAOTestConstant.ID_1);
        assertEquals(expected, actual);
    }

    @Test
    public void insertDrugManufacturerTest() throws Exception{
        DrugManufacturer expected = getManufacturerById(DatabaseDAOTestConstant.ID_1);
        expected.setCountry(DatabaseDAOTestConstant.MANUFACTURER_COUNTRY_3);
        expected.setName(DatabaseDAOTestConstant.MANUFACTURER_NAME_3);
        DatabaseDrugManufacturerDao databaseDrugManufacturerDao = new DatabaseDrugManufacturerDao();
        databaseDrugManufacturerDao.insertDrugManufacturer(expected);
        DrugManufacturer actual = databaseDrugManufacturerDao.getManufacturesByName(DatabaseDAOTestConstant.MANUFACTURER_NAME_3, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM).get(0);
        actual.setId(DatabaseDAOTestConstant.ID_1);
        assertEquals(expected, actual);
    }

    @Test
    public void updateManufacturerTest() throws Exception{
        DrugManufacturer expected = getManufacturerById(DatabaseDAOTestConstant.ID_1);
        expected.setCountry(DatabaseDAOTestConstant.MANUFACTURER_COUNTRY_3);
        expected.setName(DatabaseDAOTestConstant.MANUFACTURER_NAME_3);
        DatabaseDrugManufacturerDao databaseDrugManufacturerDao = new DatabaseDrugManufacturerDao();
        databaseDrugManufacturerDao.updateManufacturer(expected);
        DrugManufacturer actual = databaseDrugManufacturerDao.getManufacturerById(DatabaseDAOTestConstant.ID_1);
        assertEquals(expected, actual);
    }

    @Test
    public void deleteManufacturerTest() throws Exception{
        DatabaseDrugManufacturerDao databaseDrugManufacturerDao = new DatabaseDrugManufacturerDao();
        DrugManufacturer drugManufacturer = databaseDrugManufacturerDao.getManufacturesByCountry(DatabaseDAOTestConstant.MANUFACTURER_COUNTRY_3, DatabaseDAOTestConstant.LIMIT ,DatabaseDAOTestConstant.START_FROM).get(0);
        databaseDrugManufacturerDao.deleteManufacturer(drugManufacturer.getId());
        DrugManufacturer result = databaseDrugManufacturerDao.getManufacturerById(drugManufacturer.getId());
        assertNull(result);
        databaseDrugManufacturerDao.insertDrugManufacturer(drugManufacturer);
    }

    private List<DrugManufacturer> resultSetToDrugManufacturer(ResultSet resultSet) throws SQLException {
        List<DrugManufacturer> result = new ArrayList<>();
        while (resultSet.next()) {
            DrugManufacturer drugManufacturer = new DrugManufacturer();
            drugManufacturer.setId(resultSet.getInt(TableColumn.DRUG_MANUFACTURE_ID));
            drugManufacturer.setName(resultSet.getString(TableColumn.DRUG_MANUFACTURE_NAME));
            drugManufacturer.setDescription(resultSet.getString(TableColumn.DRUG_MANUFACTURE_DESCRIPTION));
            drugManufacturer.setCountry(resultSet.getString(TableColumn.DRUG_MANUFACTURE_COUNTRY));
            result.add(drugManufacturer);
        }
        return result;
    }

    private DrugManufacturer getManufacturerById(int drugId){
        DrugManufacturer result = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DatabaseDAOTestConstant.DATABASE_URL, DatabaseDAOTestConstant.DATABASE_USER, DatabaseDAOTestConstant.DATABASE_PASSWORD);
            preparedStatement = connection.prepareStatement(DatabaseDAOTestConstant.GET_MANUFACTURER_BY_ID_QUERY);
            preparedStatement.setInt(1, drugId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                result = resultSetToDrugManufacturer(resultSet).get(0);
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

    private List<DrugManufacturer> getManufacturerList(String query, String name, int limit, int startFrom){
        List<DrugManufacturer> result;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DatabaseDAOTestConstant.DATABASE_URL, DatabaseDAOTestConstant.DATABASE_USER, DatabaseDAOTestConstant.DATABASE_PASSWORD);
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, limit);
            preparedStatement.setInt(3, startFrom);
            resultSet = preparedStatement.executeQuery();
            result = resultSetToDrugManufacturer(resultSet);
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

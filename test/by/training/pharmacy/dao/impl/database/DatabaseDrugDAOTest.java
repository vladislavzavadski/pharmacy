package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.DaoFactory;
import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.drug.Drug;
import static junit.framework.Assert.*;

import by.training.pharmacy.domain.drug.DrugClass;
import by.training.pharmacy.domain.drug.DrugManufacturer;
import by.training.pharmacy.domain.drug.DrugType;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladislav on 19.06.16.
 */
public class DatabaseDrugDAOTest {
    @Test
    public void getDrugByIdTest() throws Exception{
        DatabaseDrugDAO databaseDrugDAO = new DatabaseDrugDAO();
        Drug actualResult = databaseDrugDAO.getDrugById(DatabaseDAOTestConstant.ID_1);
        Drug expectedResult = getDrugById(DatabaseDAOTestConstant.ID_1);
        assertEquals(expectedResult, actualResult);

    }
    @Test
    public void getDrugsByNameTest() throws Exception {

        List<Drug> expectedResult = getDrugList(DatabaseDAOTestConstant.GET_DRUGS_BY_NAME_QUERY,'%'+DatabaseDAOTestConstant.DRUG_NAME+'%', DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        DatabaseDrugDAO databaseDrugDAO = new DatabaseDrugDAO();
        List<Drug> actualResult = databaseDrugDAO.getDrugsByName(DatabaseDAOTestConstant.DRUG_NAME, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getDrugsByClassTest() throws Exception{

        List<Drug> expectedResult = getDrugList(DatabaseDAOTestConstant.GET_DRUGS_BY_CLASS_QUERY, DatabaseDAOTestConstant.CLASS_NAME_3, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        DatabaseDrugDAO databaseDrugDAO = new DatabaseDrugDAO();
        List<Drug> actualResult = databaseDrugDAO.getDrugsByClass(DatabaseDAOTestConstant.CLASS_NAME_3, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void getDrugsByActiveSubstanceTest() throws Exception{
        List<Drug> expectedResult = getDrugList(DatabaseDAOTestConstant.GET_DRUGS_BY_ACTIVE_SUBSTANCE_QUERY, DatabaseDAOTestConstant.ACTIVE_SUBSTANCE_NAME, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        DatabaseDrugDAO databaseDrugDAO = new DatabaseDrugDAO();
        List<Drug> actualResult = databaseDrugDAO.getDrugsByActiveSubstance(DatabaseDAOTestConstant.ACTIVE_SUBSTANCE_NAME, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void insertDrugTest() throws Exception{
        Drug expectedResult = new Drug();
        DatabaseDrugDAO databaseDrugDAO = new DatabaseDrugDAO();
        List<Integer> dosages = new ArrayList<>();
        DrugClass drugClass = new DrugClass();
        drugClass.setName(DatabaseDAOTestConstant.CLASS_NAME_5);
        drugClass.setDescription(DatabaseDAOTestConstant.CLASS_DESCRIPTION_3);
        expectedResult.setDrugClass(drugClass);
        dosages.add(DatabaseDAOTestConstant.DRUG_DOSAGE_1);
        dosages.add(DatabaseDAOTestConstant.DRUG_DOSAGE_2);
        expectedResult.setDescription(DatabaseDAOTestConstant.CLASS_DESCRIPTION_4);
        expectedResult.setName(DatabaseDAOTestConstant.CLASS_NAME_6);
        expectedResult.setInStock(false);
        expectedResult.setPrescriptionEnable(true);
        DrugManufacturer drugManufacturer = new DrugManufacturer();
        drugManufacturer.setId(DatabaseDAOTestConstant.MANUFACTURER_ID);
        drugManufacturer.setName(DatabaseDAOTestConstant.MANUFACTURER_NAME);
        drugManufacturer.setCountry(DatabaseDAOTestConstant.MANUFACTURER_COUNTRY);
        drugManufacturer.setDescription(DatabaseDAOTestConstant.MANUFACTURER_DESCRIPTION);
        expectedResult.setActiveSubstance(DatabaseDAOTestConstant.ACTIVE_SUBSTANCE_NAME);
        expectedResult.setPrice(DatabaseDAOTestConstant.DRUG_PRICE);
        expectedResult.setType(DrugType.CANDLE);
        expectedResult.setDosages(dosages);
        expectedResult.setDrugManufacturer(drugManufacturer);
        databaseDrugDAO.insertDrug(expectedResult);
        Drug actualResult = databaseDrugDAO.getDrugsByName(DatabaseDAOTestConstant.CLASS_NAME_6, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM).get(0);
        actualResult.setId(0);
        assertEquals(expectedResult, actualResult);
        databaseDrugDAO.deleteDrug(actualResult.getId());
    }

    @Test
    public void updateDrugTest() throws Exception{
        DatabaseDrugDAO databaseDrugDAO = new DatabaseDrugDAO();
        Drug expected = databaseDrugDAO.getDrugById(DatabaseDAOTestConstant.ID_6);
        expected.setDescription(DatabaseDAOTestConstant.CLASS_DESCRIPTION_4);
        expected.setInStock(!expected.isInStock());
        databaseDrugDAO.updateDrug(expected);
        Drug actual = databaseDrugDAO.getDrugById(DatabaseDAOTestConstant.ID_6);
        assertEquals(expected, actual);
    }

    @Test
    public void deleteDrugTest() throws Exception{
        DatabaseDrugDAO databaseDrugDAO = new DatabaseDrugDAO();
        Drug temp = databaseDrugDAO.getDrugById(DatabaseDAOTestConstant.ID_1);
        databaseDrugDAO.deleteDrug(DatabaseDAOTestConstant.ID_1);
        assertNull(databaseDrugDAO.getDrugById(DatabaseDAOTestConstant.ID_1));
        databaseDrugDAO.insertDrug(temp);
    }

    public Drug getDrugById(int drugId){
        Drug result = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DatabaseDAOTestConstant.DATABASE_URL, DatabaseDAOTestConstant.DATABASE_USER, DatabaseDAOTestConstant.DATABASE_PASSWORD);
            preparedStatement = connection.prepareStatement(DatabaseDAOTestConstant.GET_DRUG_BY_ID_QUERY);
            preparedStatement.setInt(1, drugId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                result = resultSetToDrug(resultSet);
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

    public List<Drug> getDrugList(String query, String name, int limit, int startFrom){
        List<Drug> result = new ArrayList<>();
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
            while (resultSet.next()) {
                result.add(resultSetToDrug(resultSet));
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

    public Drug resultSetToDrug(ResultSet resultSet){
        Drug drug = new Drug();

        try {
            drug.setName(resultSet.getString("dr_name"));
        } catch (SQLException e) {
            drug.setName(null);
        }

        try {
            drug.setId(resultSet.getInt("dr_id"));
        } catch (SQLException e) {
            drug.setId(0);
        }

        try {
            drug.setPrescriptionEnable(resultSet.getBoolean("dr_prescription_enable"));
        } catch (SQLException e) {
            drug.setPrescriptionEnable(false);
        }

        try {
            drug.setDescription(resultSet.getString("dr_description"));
        } catch (SQLException e) {
            drug.setDescription(null);
        }

        try {
            drug.setActiveSubstance(resultSet.getString("dr_active_substance"));
        } catch (SQLException e) {
            drug.setActiveSubstance(null);
        }
        try {
            DatabaseDAO<DrugClass> databaseDAO = new DatabaseDrugClassDAO();
            drug.setDrugClass(databaseDAO.resultSetToDomain(resultSet));
        }catch (DaoException e){
            drug.setDrugClass(null);
        }

        try {
            drug.setType(DrugType.valueOf(resultSet.getString("dr_type").toUpperCase()));
        } catch (SQLException e) {
            drug.setType(null);
        }

        try {
            String[] dosages = resultSet.getString("dr_dosage").split(",");
            for(String dosage:dosages){
                drug.getDosages().add(Integer.parseInt(dosage));
            }
        } catch (SQLException e) {
            drug.setDosages(null);
        }

        try {
            drug.setPrice(resultSet.getFloat("dr_price"));
        } catch (SQLException e) {
            drug.setPrice(0);
        }

        try {
            drug.setDrugImage(resultSet.getBytes("dr_image"));
        } catch (SQLException e) {
            drug.setDrugImage(null);
        }

        try {
            DatabaseDAO<DrugManufacturer> databaseDAO = new DatabaseDrugManufacturerDao();
            drug.setDrugManufacturer(databaseDAO.resultSetToDomain(resultSet));
        } catch (DaoException e) {
            e.printStackTrace();
        }
        try {
            drug.setInStock(resultSet.getBoolean("dr_in_stock"));
        } catch (SQLException e) {
            drug.setInStock(false);
        }

        return drug;

    }
}

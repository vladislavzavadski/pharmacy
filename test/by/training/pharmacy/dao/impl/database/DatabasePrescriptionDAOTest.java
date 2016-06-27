package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.order.Order;
import by.training.pharmacy.domain.prescription.Prescription;
import by.training.pharmacy.domain.user.User;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by vladislav on 20.06.16.
 */
public class DatabasePrescriptionDAOTest {
    @Test
    public void getUsersPrescriptionsTest() throws Exception{
        List<Prescription> expected = getPrescriptionList(DatabaseDAOTestConstant.GET_USER_PRESCRIPTIONS_QUERY, DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        DatabasePrescriptionDAO databasePrescriptionDAO = new DatabasePrescriptionDAO();
        List<Prescription> actual = databasePrescriptionDAO.getUsersPrescriptions(DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);
    }

    @Test
    public void getPrescriptionsByDrugIdTest() throws Exception{
        List<Prescription> expected = getPrescriptionList(DatabaseDAOTestConstant.GET_PRESCRIPTIONS_BY_DRUG_ID_QUERY, DatabaseDAOTestConstant.ID_8, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);

        DatabasePrescriptionDAO databasePrescriptionDAO = new DatabasePrescriptionDAO();
        List<Prescription> actual = databasePrescriptionDAO.getPrescriptionsByDrugId(DatabaseDAOTestConstant.ID_8, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);
    }

    @Test
    public void insertPrescriptionTest() throws Exception{
        DatabasePrescriptionDAO databasePrescriptionDAO = new DatabasePrescriptionDAO();
        Prescription prescription = databasePrescriptionDAO.getPrescriptionByPrimaryKey(DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.ID_8);
        prescription.getClient().setLogin(DatabaseDAOTestConstant.USER_LOGIN_2);
        prescription.getClient().setFirstName(DatabaseDAOTestConstant.USER_FIRST_NAME);
        prescription.getClient().setSecondName(DatabaseDAOTestConstant.USER_SECOND_NAME);
        databasePrescriptionDAO.insertPrescription(prescription);
        Prescription prescription1 = databasePrescriptionDAO.getPrescriptionByPrimaryKey(DatabaseDAOTestConstant.USER_LOGIN_2, DatabaseDAOTestConstant.ID_8);
        assertEquals(prescription, prescription1);
        databasePrescriptionDAO.deletePrescription(DatabaseDAOTestConstant.USER_LOGIN_2, DatabaseDAOTestConstant.ID_8);
    }

    @Test
    public void updatePrescriptionTest() throws Exception{
        DatabasePrescriptionDAO databasePrescriptionDAO = new DatabasePrescriptionDAO();
        Prescription prescription = databasePrescriptionDAO.getPrescriptionByPrimaryKey(DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.ID_8);
        prescription.setDrugCount((short) (prescription.getDrugCount()+2));
        databasePrescriptionDAO.updatePrescription(prescription);
        Prescription prescription1 = databasePrescriptionDAO.getPrescriptionByPrimaryKey(DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.ID_8);
        assertEquals(prescription, prescription1);

    }

    @Test
    public void deletePrescriptionTest() throws Exception{
        DatabasePrescriptionDAO databasePrescriptionDAO = new DatabasePrescriptionDAO();
        Prescription temp = databasePrescriptionDAO.getPrescriptionByPrimaryKey(DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.ID_8);
        databasePrescriptionDAO.deletePrescription(DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.ID_8);
        Prescription prescription = databasePrescriptionDAO.getPrescriptionByPrimaryKey(DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.ID_8);
        assertNull(prescription);
        databasePrescriptionDAO.insertPrescription(temp);
    }

    public Prescription resultSetToPrescription(ResultSet resultSet) {
        Prescription prescription = new Prescription();
        User doctor = new User();
        prescription.setDoctor(doctor);
        try {
            prescription.setAppointmentDate(resultSet.getDate("pr_appointment_date"));
        } catch (SQLException e) {
            prescription.setAppointmentDate(null);
        }

        try {
            prescription.setExpirationDate(resultSet.getDate("pr_expiration_date"));
        } catch (SQLException e) {
            prescription.setExpirationDate(null);
        }

        try {
            prescription.setDrugCount(resultSet.getShort("pr_drug_count"));
        } catch (SQLException e) {
            prescription.setDrugCount((short) 0);
        }

        try {
            prescription.setDrugDosage(resultSet.getShort("pr_drug_dosage"));
        } catch (SQLException e) {
            prescription.setDrugDosage((short) 0);
        }

        try {
            DatabaseDAO<User> databaseDAO = new DatabaseUserDAO();
            prescription.setClient(databaseDAO.resultSetToDomain(resultSet));
        } catch (DaoException e) {
            prescription.setClient(null);
        }

        try {
            DatabaseDAO<Drug> databaseDAO = new DatabaseDrugDAO();
            prescription.setDrug(databaseDAO.resultSetToDomain(resultSet));
        } catch (DaoException e) {
            prescription.setDrug(null);
        }

        try {
            doctor.setFirstName(resultSet.getString("doc_first_name"));
        } catch (SQLException e) {
            doctor.setFirstName(null);
        }

        try {
            doctor.setSecondName(resultSet.getString("doc_second_name"));
        } catch (SQLException e) {
            doctor.setSecondName(null);
        }
        try {
            doctor.setLogin(resultSet.getString("doc_login"));
        } catch (SQLException e) {
            doctor.setLogin(null);
        }

        return prescription;
    }

    public List<Prescription> getPrescriptionList(String query, Object param, int limit, int startFrom){
        List<Prescription> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DatabaseDAOTestConstant.DATABASE_URL, DatabaseDAOTestConstant.DATABASE_USER, DatabaseDAOTestConstant.DATABASE_PASSWORD);
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, param);
            preparedStatement.setInt(2, limit);
            preparedStatement.setInt(3, startFrom);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSetToPrescription(resultSet));
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

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

    private List<Prescription> resultSetToPrescription(ResultSet resultSet) throws SQLException {
        List<Prescription> result = new ArrayList<>();
        while (resultSet.next()) {
            Prescription prescription = new Prescription();
            User doctor = new User();
            User client = new User();
            Drug drug = new Drug();
            prescription.setDoctor(doctor);
            prescription.setClient(client);
            prescription.setDoctor(doctor);
            prescription.setAppointmentDate(resultSet.getDate(TableColumn.PRESCRIPTION_APPOINTMENT_DATE));
            prescription.setExpirationDate(resultSet.getDate(TableColumn.PRESCRIPTION_EXPIRATION_DATE));
            prescription.setDrugCount(resultSet.getShort(TableColumn.PRESCRIPTION_DRUG_COUNT));
            prescription.setDrugDosage(resultSet.getShort(TableColumn.PRESCRIPTION_DRUG_DOSAGE));
            doctor.setFirstName(resultSet.getString(TableColumn.DOCTOR_FIRST_NAME));
            doctor.setSecondName(resultSet.getString(TableColumn.DOCTOR_SECOND_NAME));
            doctor.setLogin(resultSet.getString(TableColumn.DOCTOR_LOGIN));
            drug.setName(resultSet.getString(TableColumn.DRUG_NAME));
            drug.setId(resultSet.getInt(TableColumn.DRUG_ID));
            client.setLogin(resultSet.getNString(TableColumn.USER_LOGIN));
            client.setFirstName(resultSet.getString(TableColumn.USER_FIRST_NAME));
            client.setSecondName(resultSet.getString(TableColumn.USER_SECOND_NAME));
            result.add(prescription);
        }
        return result;

    }

    private List<Prescription> getPrescriptionList(String query, Object param, int limit, int startFrom){
        List<Prescription> result;
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

            result = resultSetToPrescription(resultSet);

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

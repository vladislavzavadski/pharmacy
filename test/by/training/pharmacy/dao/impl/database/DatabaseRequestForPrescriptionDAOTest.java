package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.prescription.RequestForPrescription;
import by.training.pharmacy.domain.prescription.RequestStatus;
import by.training.pharmacy.domain.user.User;
import org.junit.Test;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by vladislav on 20.06.16.
 */
public class DatabaseRequestForPrescriptionDAOTest {
    @Test
    public void getRequestsByClientTest() throws Exception{
        DatabaseRequestForPrescriptionDAO databaseRequestForPrescriptionDAO = new DatabaseRequestForPrescriptionDAO();
        List<RequestForPrescription> actual = databaseRequestForPrescriptionDAO.getRequestsByClient(DatabaseDAOTestConstant.USER_LOGIN_2, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        List<RequestForPrescription> expected = getRequstList(DatabaseDAOTestConstant.GET_USER_REQUESTS_QUERY, DatabaseDAOTestConstant.USER_LOGIN_2, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);

    }

    @Test
    public void getRequestsByDrugIdTest() throws Exception{
        DatabaseRequestForPrescriptionDAO databaseRequestForPrescriptionDAO = new DatabaseRequestForPrescriptionDAO();
        List<RequestForPrescription> actual = databaseRequestForPrescriptionDAO.getRequestsByDrugId(DatabaseDAOTestConstant.ID_3, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        List<RequestForPrescription> expected = getRequstList(DatabaseDAOTestConstant.GET_REQUESTS_BY_DRUG_ID, DatabaseDAOTestConstant.ID_3, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);
    }

    @Test
    public void getRequestsByDoctorTest() throws Exception{
        DatabaseRequestForPrescriptionDAO databaseRequestForPrescriptionDAO = new DatabaseRequestForPrescriptionDAO();
        List<RequestForPrescription> actual = databaseRequestForPrescriptionDAO.getRequestsByDoctor(DatabaseDAOTestConstant.USER_LOGIN_6, DatabaseDAOTestConstant.START_FROM, DatabaseDAOTestConstant.LIMIT);
        List<RequestForPrescription> expected = getRequstList(DatabaseDAOTestConstant.GET_PRESCRIPTION_BY_DOCTOR, DatabaseDAOTestConstant.USER_LOGIN_6, DatabaseDAOTestConstant.START_FROM, DatabaseDAOTestConstant.LIMIT);
        assertEquals(expected, actual);
    }

    @Test
    public void getRequestsByStatusTest() throws Exception{
        DatabaseRequestForPrescriptionDAO databaseRequestForPrescriptionDAO = new DatabaseRequestForPrescriptionDAO();
        List<RequestForPrescription> actual = databaseRequestForPrescriptionDAO.getRequestsByStatus(RequestStatus.CONFIRMED, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        List<RequestForPrescription> expected = getRequstList(DatabaseDAOTestConstant.GET_REQUESTS_BY_STATUS_QUERY, RequestStatus.CONFIRMED.toString().toLowerCase(), DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);
    }

    @Test
    public void getRequestByIdTest() throws Exception{
        DatabaseRequestForPrescriptionDAO databaseRequestForPrescriptionDAO = new DatabaseRequestForPrescriptionDAO();
        RequestForPrescription actual = databaseRequestForPrescriptionDAO.getRequestById(DatabaseDAOTestConstant.ID_4);
        RequestForPrescription expected = getUniqueRequest(DatabaseDAOTestConstant.GET_REQUEST_BY_ID_QUERY, DatabaseDAOTestConstant.ID_4);
        assertEquals(expected, actual);
    }

    @Test
    public void insertRequestTest() throws Exception{
        DatabaseRequestForPrescriptionDAO databaseRequestForPrescriptionDAO = new DatabaseRequestForPrescriptionDAO();
        RequestForPrescription expected = databaseRequestForPrescriptionDAO.getRequestById(DatabaseDAOTestConstant.ID_4);
        expected.setRequestStatus(RequestStatus.DENIED);
        expected.setProlongDate(new Date());
        expected.setRequestDate(new Date());
        expected.setId(DatabaseDAOTestConstant.ID_645);
        databaseRequestForPrescriptionDAO.insertRequest(expected);
        RequestForPrescription actual = databaseRequestForPrescriptionDAO.getRequestById(DatabaseDAOTestConstant.ID_645);
        assertEquals(expected, actual);
        databaseRequestForPrescriptionDAO.deleteRequest(DatabaseDAOTestConstant.ID_645);
    }

    @Test
    public void updateRequestTest() throws Exception{
        DatabaseRequestForPrescriptionDAO databaseRequestForPrescriptionDAO = new DatabaseRequestForPrescriptionDAO();
        RequestForPrescription expected = databaseRequestForPrescriptionDAO.getRequestById(DatabaseDAOTestConstant.ID_4);
        expected.setRequestStatus(RequestStatus.DENIED);
        databaseRequestForPrescriptionDAO.updateRequest(expected);
        RequestForPrescription actual = databaseRequestForPrescriptionDAO.getRequestById(DatabaseDAOTestConstant.ID_4);
        assertEquals(expected, actual);
    }

    @Test
    public void deleteRequestTest() throws Exception{
        DatabaseRequestForPrescriptionDAO databaseRequestForPrescriptionDAO = new DatabaseRequestForPrescriptionDAO();
        RequestForPrescription temp = databaseRequestForPrescriptionDAO.getRequestById(DatabaseDAOTestConstant.ID_5);
        databaseRequestForPrescriptionDAO.deleteRequest(DatabaseDAOTestConstant.ID_5);
        RequestForPrescription requestForPrescription = databaseRequestForPrescriptionDAO.getRequestById(DatabaseDAOTestConstant.ID_5);
        assertNull(requestForPrescription);
        databaseRequestForPrescriptionDAO.insertRequest(temp);
    }

    private List<RequestForPrescription> resultSetToRequest(ResultSet resultSet) throws SQLException {
        List<RequestForPrescription> result = new ArrayList<>();
        while (resultSet.next()) {
            RequestForPrescription requestForPrescription = new RequestForPrescription();
            User doctor = new User();
            User client = new User();
            Drug drug = new Drug();
            requestForPrescription.setClient(client);
            requestForPrescription.setDrug(drug);
            requestForPrescription.setDoctor(doctor);
            requestForPrescription.setId(resultSet.getInt(TableColumn.REQUEST_ID));
            requestForPrescription.setProlongDate(resultSet.getDate(TableColumn.REQUEST_PROLONG_TO));
            requestForPrescription.setRequestDate(resultSet.getDate(TableColumn.REQUEST_DATE));
            requestForPrescription.setRequestStatus(RequestStatus.valueOf(resultSet.getString(TableColumn.REQUEST_STATUS).toUpperCase()));
            requestForPrescription.setClientComment(resultSet.getString(TableColumn.REQUEST_CLIENT_COMMENT));
            requestForPrescription.setDoctorComment(resultSet.getString(TableColumn.REQUEST_DOCTOR_COMMENT));
            doctor.setFirstName(resultSet.getString(TableColumn.DOCTOR_FIRST_NAME));
            doctor.setSecondName(resultSet.getString(TableColumn.DOCTOR_SECOND_NAME));
            doctor.setLogin(resultSet.getString(TableColumn.DOCTOR_LOGIN));
            client.setLogin(resultSet.getString(TableColumn.USER_LOGIN));
            client.setFirstName(resultSet.getString(TableColumn.USER_FIRST_NAME));
            client.setSecondName(resultSet.getString(TableColumn.USER_SECOND_NAME));
            drug.setId(resultSet.getInt(TableColumn.DRUG_ID));
            drug.setName(resultSet.getString(TableColumn.DRUG_NAME));
            result.add(requestForPrescription);
        }
        return result;
    }

    private RequestForPrescription getUniqueRequest(String query, Object ... params){
        RequestForPrescription result = null;
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
                result = resultSetToRequest(resultSet).get(0);
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

    private List<RequestForPrescription> getRequstList(String query, Object... params){
        List<RequestForPrescription> result;
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

            result = resultSetToRequest(resultSet);

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

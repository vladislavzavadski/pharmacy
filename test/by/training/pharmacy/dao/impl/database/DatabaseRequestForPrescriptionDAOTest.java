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
        List<RequestForPrescription> actual = databaseRequestForPrescriptionDAO.getRequestsByDoctor("andrei_leshkevich", 0, 10);
        List<RequestForPrescription> expected = getRequstList("select cl.us_login, dr_id, re_doctor, re_id, re_prolong_to, re_request_date, re_clients_comment, re_doctors_comment, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from requests_for_prescriptions inner join drugs on dr_id = re_drug_id inner join users as cl on re_client_login = cl.us_login inner join users as doc on re_doctor = doc.us_login WHERE re_doctor=? LIMIT ?, ?;", "andrei_leshkevich", 0, 10);
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

    public RequestForPrescription resultSetToRequest(ResultSet resultSet) {
        RequestForPrescription requestForPrescription = new RequestForPrescription();
        User doctor = new User();
        requestForPrescription.setDoctor(doctor);
        try {
            requestForPrescription.setId(resultSet.getInt("re_id"));
        } catch (SQLException e) {
            requestForPrescription.setId(0);
        }

        try {
            requestForPrescription.setProlongDate(resultSet.getDate("re_prolong_to"));
        } catch (SQLException e) {
            requestForPrescription.setProlongDate(null);
        }

        try {
            requestForPrescription.setRequestDate(resultSet.getDate("re_request_date"));
        } catch (SQLException e) {
            requestForPrescription.setRequestDate(null);
        }

        try {
            requestForPrescription.setRequestStatus(RequestStatus.valueOf(resultSet.getString("re_status").toUpperCase()));
        } catch (SQLException e) {
            requestForPrescription.setRequestStatus(null);
        }

        try {
            requestForPrescription.setClientComment(resultSet.getString("re_clients_comment"));
        } catch (SQLException e) {
            requestForPrescription.setClientComment(null);
        }
        try {
            requestForPrescription.setDoctorComment(resultSet.getString("re_doctors_comment"));
        } catch (SQLException e) {
            requestForPrescription.setDoctorComment(null);
        }
        try {
            DatabaseDAO<User> databaseDAO = new DatabaseUserDAO();
            requestForPrescription.setClient(databaseDAO.resultSetToDomain(resultSet));
        } catch (DaoException e) {
            requestForPrescription.setClient(null);
        }

        try {
            DatabaseDAO<Drug> databaseDAO = new DatabaseDrugDAO();
            requestForPrescription.setDrug(databaseDAO.resultSetToDomain(resultSet));
        } catch (DaoException e) {
            requestForPrescription.setDrug(null);
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
        return requestForPrescription;
    }

    public RequestForPrescription getUniqueRequest(String query, Object ... params){
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
                result = resultSetToRequest(resultSet);
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

    public List<RequestForPrescription> getRequstList(String query, Object... params){
        List<RequestForPrescription> result = new ArrayList<>();
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
                result.add(resultSetToRequest(resultSet));
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

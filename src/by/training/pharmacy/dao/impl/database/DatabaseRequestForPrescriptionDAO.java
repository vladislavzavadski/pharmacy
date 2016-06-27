package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.RequestForPrescriptionDAO;
import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;
import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.dao.impl.database.util.DatabaseOperation;
import by.training.pharmacy.domain.Period;
import by.training.pharmacy.domain.prescription.RequestForPrescription;
import by.training.pharmacy.domain.prescription.RequestStatus;
import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by vladislav on 19.06.16.
 */
public class DatabaseRequestForPrescriptionDAO implements RequestForPrescriptionDAO {
    private static final String GET_REQUESTS_BY_CLIENT_QUERY =  "select cl.us_login, dr_id, re_doctor, re_id, re_prolong_to, re_request_date, re_clients_comment, re_doctors_comment, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from requests_for_prescriptions inner join drugs on dr_id = re_drug_id inner join users as cl on re_client_login = cl.us_login inner join users as doc on re_doctor = doc.us_login WHERE re_client_login=? LIMIT ?, ?;";
    private static final String GET_REQUESTS_BY_DRUG_ID_QUERY = "select cl.us_login, dr_id, re_doctor, re_id, re_prolong_to, re_request_date, re_clients_comment,  re_doctors_comment, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from requests_for_prescriptions inner join drugs on dr_id = re_drug_id inner join users as cl on re_client_login = cl.us_login inner join users as doc on re_doctor = doc.us_login WHERE re_drug_id=? LIMIT ?, ?;";
    private static final String GET_REQUESTS_BY_DOCTOR_QUERY = "select cl.us_login, dr_id, re_doctor, re_id, re_prolong_to, re_request_date, re_clients_comment, re_doctors_comment, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from requests_for_prescriptions inner join drugs on dr_id = re_drug_id inner join users as cl on re_client_login = cl.us_login inner join users as doc on re_doctor = doc.us_login WHERE re_doctor=? LIMIT ?, ?;";
    private static final String GET_REQUESTS_BY_STATUS_QUERY = "select cl.us_login, dr_id, re_doctor, re_id, re_prolong_to, re_request_date, re_clients_comment, re_doctors_comment, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from requests_for_prescriptions inner join drugs on dr_id = re_drug_id inner join users as cl on re_client_login = cl.us_login inner join users as doc on re_doctor = doc.us_login WHERE re_status=? LIMIT ?, ?;";
    private static final String GET_REQUESTS_BY_DATE_BEFORE_QUERY = "select cl.us_login, dr_id, re_doctor, re_id, re_prolong_to, re_request_date, re_clients_comment, re_doctors_comment, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from requests_for_prescriptions inner join drugs on dr_id = re_drug_id inner join users as cl on re_client_login = cl.us_login inner join users as doc on re_doctor = doc.us_login WHERE re_request_date<? LIMIT ?, ?;";
    private static final String GET_REQUESTS_BY_DATE_AFTER_QUERY = "select cl.us_login, dr_id, re_doctor, re_id, re_prolong_to, re_request_date, re_clients_comment, re_doctors_comment, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from requests_for_prescriptions inner join drugs on dr_id = re_drug_id inner join users as cl on re_client_login = cl.us_login inner join users as doc on re_doctor = doc.us_login WHERE re_request_date>? LIMIT ?, ?;";
    private static final String GET_REQUESTS_BY_DATE_CURRENT_QUERY = "select cl.us_login, dr_id, re_doctor, re_id, re_prolong_to, re_request_date, re_clients_comment, re_doctors_comment, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from requests_for_prescriptions inner join drugs on dr_id = re_drug_id inner join users as cl on re_client_login = cl.us_login inner join users as doc on re_doctor = doc.us_login WHERE re_request_date=? LIMIT ?, ?;";
    private static final String GET_REQUESTS_BY_ID = "select re_status, cl.us_login, dr_id, re_doctor, re_id, re_prolong_to, re_request_date, re_clients_comment, re_doctors_comment, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from requests_for_prescriptions inner join drugs on dr_id = re_drug_id inner join users as cl on re_client_login = cl.us_login inner join users as doc on re_doctor = doc.us_login WHERE re_id=? LIMIT 1;";
    private static final String INSERT_REQUEST_QUERY = "INSERT INTO requests_for_prescriptions (re_id, re_client_login, re_drug_id, re_doctor, re_prolong_to, re_status, re_clients_comment, re_doctors_comment, re_request_date) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_REQUEST_QUERY = "UPDATE requests_for_prescriptions SET re_drug_id=?, re_doctor=?, re_prolong_to=?, re_status=?, re_clients_comment=?, re_doctors_comment=? WHERE re_id=?;";
    private static final String DELETE_REQUEST_QUERY = "DELETE FROM requests_for_prescriptions WHERE re_id=?;";
    private static final Logger logger = LogManager.getLogger(DatabaseRequestForPrescriptionDAO.class);

    public DatabaseRequestForPrescriptionDAO() throws DaoException {

    }

    @Override
    public List<RequestForPrescription> getRequestsByClient(String clientLogin, int limit, int startFrom) throws DaoException {
        List<RequestForPrescription> requestForPrescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_REQUESTS_BY_CLIENT_QUERY, clientLogin, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            requestForPrescriptions = resultSetToRequest(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabaseRequestForPrescriptionDAO.getRequestsByClient", e);
            throw new DaoException("Can not load requests for user with login = \'"+clientLogin+"\'"+clientLogin, e);
        }
        return requestForPrescriptions;
    }

    @Override
    public List<RequestForPrescription> getRequestsByDrugId(int drugId, int limit, int startFrom) throws DaoException {
        List<RequestForPrescription> requestForPrescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_REQUESTS_BY_DRUG_ID_QUERY, drugId, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            requestForPrescriptions = resultSetToRequest(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabaseRequestForPrescriptionDAO.getRequestsByDrugId", e);
            throw new DaoException("Can not load requests for drugId = \'"+drugId+"\'", e);
        }
        return requestForPrescriptions;
    }

    @Override
    public List<RequestForPrescription> getRequestsByDoctor(String doctorLogin, int limit, int startFrom) throws DaoException {
        List<RequestForPrescription> requestForPrescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_REQUESTS_BY_DOCTOR_QUERY, doctorLogin, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            requestForPrescriptions = resultSetToRequest(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabaseRequestForPrescriptionDAO.getRequestsByDoctor", e);
            throw new DaoException("Can not load requests with doctorLogin = \'"+doctorLogin+"\'", e);
        }
        return requestForPrescriptions;
    }

    @Override
    public List<RequestForPrescription> getRequestsByStatus(RequestStatus requestStatus, int limit, int startFrom) throws DaoException {
        List<RequestForPrescription> requestForPrescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_REQUESTS_BY_STATUS_QUERY, requestStatus.toString().toLowerCase(), limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            requestForPrescriptions = resultSetToRequest(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabaseRequestForPrescriptionDAO.getRequestsByStatus", e);
            throw new DaoException("Can not load requests with requestStatus \'"+requestStatus+"\'", e);
        }
        return requestForPrescriptions;
    }

    @Override
    public List<RequestForPrescription> getRequestsByDate(Date requestDate, Period period, int limit, int startFrom) throws DaoException {
        List<RequestForPrescription> requestForPrescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation()){

            switch (period) {
                case AFTER_DATE: {
                    databaseOperation.init(GET_REQUESTS_BY_DATE_AFTER_QUERY, requestDate, limit, startFrom);
                    break;
                }

                case BEFORE_DATE: {
                    databaseOperation.init(GET_REQUESTS_BY_DATE_BEFORE_QUERY, requestDate, limit, startFrom);
                    break;
                }

                case CURRENT_DATE: {
                    databaseOperation.init(GET_REQUESTS_BY_DATE_CURRENT_QUERY, requestDate, limit, startFrom);
                    break;
                }
            }
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            requestForPrescriptions = resultSetToRequest(resultSet);
        }catch (Exception e){
            logger.error("Method: DatabaseRequestForPrescriptionDAO.getRequestsByDate", e);
            throw new DaoException("Can not load requests from database with requestDate = \'"+requestDate+"\'", e);
        }
        return requestForPrescriptions;
    }

    @Override
    public RequestForPrescription getRequestById(int requestId) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_REQUESTS_BY_ID, requestId)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            List<RequestForPrescription> results = resultSetToRequest(resultSet);
            if(!results.isEmpty()){
                return results.get(0);
            }
        } catch (Exception e) {
            logger.error("Method: DatabaseRequestForPrescriptionDAO.getRequestById", e);
            throw new DaoException("Can not load request with id = \'"+requestId+"\'", e);
        }
        return null;
    }

    @Override
    public void insertRequest(RequestForPrescription requestForPrescription) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(INSERT_REQUEST_QUERY, requestForPrescription.getId(), requestForPrescription.getClient().getLogin(), requestForPrescription.getDrug().getId(), requestForPrescription.getDoctor().getLogin(), requestForPrescription.getProlongDate(), requestForPrescription.getRequestStatus().toString().toLowerCase(), requestForPrescription.getClientComment(), requestForPrescription.getDoctorComment(), requestForPrescription.getRequestDate())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseRequestForPrescriptionDAO.insertRequest", e);
            throw new DaoException("Can not insert new request "+ requestForPrescription, e);
        }
    }

    @Override
    public void updateRequest(RequestForPrescription requestForPrescription) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(UPDATE_REQUEST_QUERY, requestForPrescription.getDrug().getId(), requestForPrescription.getDoctor().getLogin(), requestForPrescription.getProlongDate(), requestForPrescription.getRequestStatus().toString().toLowerCase(), requestForPrescription.getClientComment(), requestForPrescription.getDoctorComment(), requestForPrescription.getId())){
//"UPDATE requests_for_prescriptions re_drug_id=?, re_doctor=?, re_prolong_to=?, re_status=?, re_clients_comment=?, re_doctors_comment=? WHERE re_id=?;";
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseRequestForPrescriptionDAO.updateRequest", e);
            throw new DaoException("Can not update request "+requestForPrescription,e);
        }
    }

    @Override
    public void deleteRequest(int requestId) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(DELETE_REQUEST_QUERY, requestId)){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseRequestForPrescriptionDAO.deleteRequest", e);
            throw new DaoException("Can not delete request with requestId = \'"+requestId+"\'");
        }
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
            requestForPrescription.setId(resultSet.getInt("re_id"));
            requestForPrescription.setRequestStatus(RequestStatus.valueOf(resultSet.getString("re_status").toUpperCase()));
            requestForPrescription.setProlongDate(resultSet.getDate("re_prolong_to"));
            requestForPrescription.setRequestDate(resultSet.getDate("re_request_date"));
            requestForPrescription.setRequestStatus(RequestStatus.valueOf(resultSet.getString("re_status").toUpperCase()));
            requestForPrescription.setClientComment(resultSet.getString("re_clients_comment"));
            requestForPrescription.setDoctorComment(resultSet.getString("re_doctors_comment"));
            doctor.setFirstName(resultSet.getString("doc_first_name"));
            doctor.setSecondName(resultSet.getString("doc_second_name"));
            doctor.setLogin(resultSet.getString("doc_login"));
            client.setLogin(resultSet.getString("us_login"));
            client.setFirstName(resultSet.getString("us_first_name"));
            client.setSecondName(resultSet.getString("us_second_name"));
            drug.setId(resultSet.getInt("dr_id"));
            drug.setName(resultSet.getString("dr_name"));
            result.add(requestForPrescription);
        }
        return result;
    }
}

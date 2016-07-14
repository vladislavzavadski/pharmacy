package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.PrescriptionDAO;
import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;
import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.dao.impl.database.util.DatabaseOperation;
import by.training.pharmacy.domain.Period;
import by.training.pharmacy.domain.prescription.Prescription;
import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.pattern.RegexReplacement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vladislav on 19.06.16.
 */
public class DatabasePrescriptionDAO implements PrescriptionDAO {
    private static final String GET_USERS_PRESCRIPTIONS_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_client_login=? limit ?, ?;";
    private static final String GET_PRESCRIPTIONS_BY_ID_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_drug_id=? limit ?, ?;";
    private static final String GET_DOCTORS_PRESCRIPTIONS_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_doctor=? limit ?, ?;";
    private static final String GET_PRESCRIPTIONS_BY_PRIMARY_KEY_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_client_login=? and pr_drug_id=? limit 1;";
    private static final String GET_PRESCRIPTIONS_BY_APPOINTMENT_DATE_BEFORE_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_appointment_date<? limit ?, ?;";
    private static final String GET_PRESCRIPTIONS_BY_APPOINTMENT_DATE_AFTER_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_appointment_date>? limit ?, ?;";
    private static final String GET_PRESCRIPTIONS_BY_APPOINTMENT_DATE_CURRENT_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_appointment_date=? limit ?, ?;";
    private static final String GET_PRESCRIPTIONS_BY_EXPIRATION_DATE_BEFORE_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_expiration_date<? limit ?, ?;";
    private static final String GET_PRESCRIPTIONS_BY_EXPIRATION_DATE_AFTER_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_expiration_date>? limit ?, ?;";
    private static final String GET_PRESCRIPTIONS_BY_EXPIRATION_DATE_CURRENT_QUERY = "select cl.us_login, dr_id, pr_doctor, pr_drug_count, pr_drug_dosage, pr_appointment_date, pr_expiration_date, dr_name, cl.us_first_name, cl.us_second_name, doc.us_first_name as doc_first_name, doc.us_second_name as doc_second_name, doc.us_login as doc_login  from prescriptions inner join drugs on dr_id = pr_drug_id inner join users as cl on pr_client_login = cl.us_login inner join users as doc on pr_doctor = doc.us_login where pr_expiration_date=? limit ?, ?;";
    private static final String INSERT_PRESCRIPTION_QUERY = "INSERT INTO prescriptions (pr_client_login, pr_drug_id, pr_doctor, pr_appointment_date, pr_expiration_date, pr_drug_count, pr_drug_dosage) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String UPDATE_PRESCRIPTION_QUERY = "UPDATE prescriptions SET pr_doctor=?, pr_appointment_date=?, pr_expiration_date=?, pr_drug_count=?, pr_drug_dosage=? WHERE pr_client_login=? and pr_drug_id=?";
    private static final String DELETE_PRESCRIPTION_QUERY = "DELETE FROM prescriptions WHERE pr_client_login=? and pr_drug_id=?;";
    private static final Logger logger = LogManager.getLogger(DatabasePrescriptionDAO.class);

    public DatabasePrescriptionDAO() throws DaoException {

    }

    @Override
    public List<Prescription> getUsersPrescriptions(String clientLogin, int limit, int startFrom) throws DaoException {
        List<Prescription> prescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_USERS_PRESCRIPTIONS_QUERY, clientLogin, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            prescriptions = resultSetToPrescription(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabasePrescriptionDAO.getUsersPrescriptions", e);
            throw new DaoException("Can not load users prescriptions with login = \'" + clientLogin + "\' from database", e);
        }
        return prescriptions;
    }

    @Override
    public List<Prescription> getPrescriptionsByDrugId(int drugId, int limit, int startFrom) throws DaoException {
        List<Prescription> prescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_PRESCRIPTIONS_BY_ID_QUERY, drugId, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            prescriptions = resultSetToPrescription(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabasePrescriptionDAO.getPrescriptionsByDrugId", e);
            throw new DaoException("Can not load prescriptions with drugId = \'" + drugId + "\' from database", e);
        }
        return prescriptions;
    }

    @Override
    public List<Prescription> getDoctorsPrescriptions(String doctorLogin, int limit, int startFrom) throws DaoException {
        List<Prescription> prescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_DOCTORS_PRESCRIPTIONS_QUERY, doctorLogin, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            prescriptions = resultSetToPrescription(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabasePrescriptionDAO.getDoctorsPrescriptions", e);
            throw new DaoException("Can not load prescriptions with doctor login = \'" + doctorLogin + "\' from database", e);
        }
        return prescriptions;
    }

    @Override
    public Prescription getPrescriptionByPrimaryKey(String userLogin, int drugId) throws DaoException {
        List<Prescription> prescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_PRESCRIPTIONS_BY_PRIMARY_KEY_QUERY, userLogin, drugId)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            prescriptions = resultSetToPrescription(resultSet);
            if (!prescriptions.isEmpty()) {
                return prescriptions.get(0);
            }
        } catch (Exception e) {
            logger.error("Method: DatabasePrescriptionDAO.getPrescriptionByPrimaryKey", e);
            throw new DaoException("Can not load prescriptions with login = \'" + userLogin + "\' and drugId = \'" + drugId + "\' from database", e);
        }
        return null;
    }

    @Override
    public List<Prescription> getPrescriptionsByAppointmentDate(Date date, Period period, int limit, int startFrom) throws DaoException {
        List<Prescription> prescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation()){
            switch (period) {
                case AFTER_DATE: {
                    databaseOperation.init(GET_PRESCRIPTIONS_BY_APPOINTMENT_DATE_AFTER_QUERY, date, limit, startFrom);
                    break;
                }

                case BEFORE_DATE: {
                    databaseOperation.init(GET_PRESCRIPTIONS_BY_APPOINTMENT_DATE_BEFORE_QUERY, date, limit, startFrom);
                    break;
                }

                case CURRENT_DATE: {
                    databaseOperation.init(GET_PRESCRIPTIONS_BY_APPOINTMENT_DATE_CURRENT_QUERY, date, limit, startFrom);
                    break;
                }
            }
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            prescriptions = resultSetToPrescription(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabasePrescriptionDAO.getPrescriptionsByAppointmentDate", e);
            throw new DaoException("Can not load prescriptions by appointment date = \'" + date + "\'", e);
        }
        return prescriptions;
    }

    @Override
    public List<Prescription> getPrescriptionsByExpirationDate(Date date, Period period, int limit, int startFrom) throws DaoException {
        List<Prescription> prescriptions = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation()){
            switch (period) {
                case AFTER_DATE: {
                    databaseOperation.init(GET_PRESCRIPTIONS_BY_EXPIRATION_DATE_AFTER_QUERY, date, limit, startFrom);
                    break;
                }

                case BEFORE_DATE: {
                    databaseOperation.init(GET_PRESCRIPTIONS_BY_EXPIRATION_DATE_BEFORE_QUERY, date, limit, startFrom);
                    break;
                }

                case CURRENT_DATE: {
                    databaseOperation.init(GET_PRESCRIPTIONS_BY_EXPIRATION_DATE_CURRENT_QUERY, date, limit, startFrom);
                    break;
                }
            }
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            prescriptions = resultSetToPrescription(resultSet);
        } catch (Exception e) {
            logger.error("Method: DatabasePrescriptionDAO.getPrescriptionsByExpirationDate", e);
            throw new DaoException("Can not load prescriptions by appointment date = \'" + date + "\'", e);
        }
        return prescriptions;
    }

    @Override
    public void insertPrescription(Prescription prescription) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(INSERT_PRESCRIPTION_QUERY, prescription.getClient().getLogin(), prescription.getDrug().getId(), prescription.getDoctor().getLogin(), prescription.getAppointmentDate(), prescription.getExpirationDate(), prescription.getDrugCount(), prescription.getDrugDosage())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabasePrescriptionDAO.insertPrescription", e);
            throw new DaoException("Can not insert prescription " + prescription, e);
        }
    }

    @Override
    public void updatePrescription(Prescription prescription) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(UPDATE_PRESCRIPTION_QUERY, prescription.getDoctor().getLogin(), prescription.getAppointmentDate(), prescription.getExpirationDate(), prescription.getDrugCount(), prescription.getDrugDosage(), prescription.getClient().getLogin(), prescription.getDrug().getId())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabasePrescriptionDAO.updatePrescription", e);
            throw new DaoException("Can not update prescription " + prescription, e);
        }
    }

    @Override
    public void deletePrescription(String clientLogin, int drugId) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(DELETE_PRESCRIPTION_QUERY, clientLogin, drugId)){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabasePrescriptionDAO.deletePrescription", e);
            throw new DaoException("Can not delete prescription with clientLogin = \'" + clientLogin + "\' and drugId = \'" + drugId, e);
        }
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
}
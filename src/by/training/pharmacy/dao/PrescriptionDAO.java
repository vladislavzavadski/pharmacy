package by.training.pharmacy.dao;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.Period;
import by.training.pharmacy.domain.prescription.Prescription;

import java.util.Date;
import java.util.List;

/**
 * Created by vladislav on 13.06.16.
 */
public interface PrescriptionDAO {
    List<Prescription> getUsersPrescriptions(String clientLogin, int limit, int startFrom) throws DaoException;
    List<Prescription> getPrescriptionsByDrugId(int drugId, int limit, int startFrom) throws DaoException;
    List<Prescription> getDoctorsPrescriptions(String doctorLogin, int limit, int startFrom) throws DaoException;
    Prescription getPrescriptionByPrimaryKey(String userLogin, int drugId) throws DaoException;
    List<Prescription> getPrescriptionsByAppointmentDate(Date date, Period period, int limit, int startFrom) throws DaoException;
    List<Prescription> getPrescriptionsByExpirationDate(Date date, Period period, int limit, int startFrom) throws DaoException;
    void insertPrescription(Prescription prescription) throws DaoException;
    void updatePrescription(Prescription prescription) throws DaoException;
    void deletePrescription(String clientLogin, int drugId) throws DaoException;
}

package by.training.pharmacy.domain.prescription;

import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.user.User;

import java.util.Date;

/**
 * Created by vladislav on 19.06.16.
 */
public class Prescription {
    private User client;
    private Drug drug;
    private User doctor;
    private Date appointmentDate;
    private Date expirationDate;
    private short drugCount;
    private short drugDosage;

    @Override
    public String toString() {
        return "Prescription{" +
                "client=" + client +
                ", drug=" + drug +
                ", doctor=" + doctor +
                ", appointmentDate=" + appointmentDate +
                ", expirationDate=" + expirationDate +
                ", drugCount=" + drugCount +
                ", drugDosage=" + drugDosage +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prescription that = (Prescription) o;

        if (drugCount != that.drugCount) return false;
        if (drugDosage != that.drugDosage) return false;
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (drug != null ? !drug.equals(that.drug) : that.drug != null) return false;
        if (doctor != null ? !doctor.equals(that.doctor) : that.doctor != null) return false;
        if (appointmentDate != null ? !appointmentDate.equals(that.appointmentDate) : that.appointmentDate != null)
            return false;
        return expirationDate != null ? expirationDate.equals(that.expirationDate) : that.expirationDate == null;

    }

    @Override
    public int hashCode() {
        int result = client != null ? client.hashCode() : 0;
        result = 31 * result + (drug != null ? drug.hashCode() : 0);
        result = 31 * result + (doctor != null ? doctor.hashCode() : 0);
        result = 31 * result + (appointmentDate != null ? appointmentDate.hashCode() : 0);
        result = 31 * result + (expirationDate != null ? expirationDate.hashCode() : 0);
        result = 31 * result + (int) drugCount;
        result = 31 * result + (int) drugDosage;
        return result;
    }

    public User getClient() {

        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public short getDrugCount() {
        return drugCount;
    }

    public void setDrugCount(short drugCount) {
        this.drugCount = drugCount;
    }

    public short getDrugDosage() {
        return drugDosage;
    }

    public void setDrugDosage(short drugDosage) {
        this.drugDosage = drugDosage;
    }
}

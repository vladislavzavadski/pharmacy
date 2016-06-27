package by.training.pharmacy.domain.prescription;

import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vladislav on 19.06.16.
 */
public class RequestForPrescription {
    private int id;
    private User client;
    private Drug drug;
    private User doctor;
    private Date prolongDate;
    private RequestStatus requestStatus;
    private String clientComment;
    private String doctorComment;
    private Date requestDate;

    @Override
    public String toString() {
        return "RequestForPrescription{" +
                "id=" + id +
                ", client=" + client +
                ", drug=" + drug +
                ", doctor=" + doctor +
                ", prolongDate=" + prolongDate +
                ", requestStatus=" + requestStatus +
                ", clientComment='" + clientComment + '\'' +
                ", doctorComment='" + doctorComment + '\'' +
                ", requestDate=" + requestDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestForPrescription that = (RequestForPrescription) o;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (id != that.id) return false;
        if (client != null ? !client.equals(that.client) : that.client != null) return false;
        if (drug != null ? !drug.equals(that.drug) : that.drug != null) return false;
        if (doctor != null ? !doctor.equals(that.doctor) : that.doctor != null) return false;
        if (prolongDate != null ? !simpleDateFormat.format(prolongDate).equals(simpleDateFormat.format(that.prolongDate)) : that.prolongDate != null) return false;
        if (requestStatus != that.requestStatus) return false;
        if (clientComment != null ? !clientComment.equals(that.clientComment) : that.clientComment != null)
            return false;
        if (doctorComment != null ? !doctorComment.equals(that.doctorComment) : that.doctorComment != null)
            return false;
        return requestDate != null ? simpleDateFormat.format(requestDate).equals(simpleDateFormat.format(that.requestDate)) : that.requestDate == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (drug != null ? drug.hashCode() : 0);
        result = 31 * result + (doctor != null ? doctor.hashCode() : 0);
        result = 31 * result + (prolongDate != null ? simpleDateFormat.format(prolongDate).hashCode() : 0);
        result = 31 * result + (requestStatus != null ? requestStatus.hashCode() : 0);
        result = 31 * result + (clientComment != null ? clientComment.hashCode() : 0);
        result = 31 * result + (doctorComment != null ? doctorComment.hashCode() : 0);
        result = 31 * result + (requestDate != null ? simpleDateFormat.format(requestDate).hashCode() : 0);
        return result;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getProlongDate() {
        return prolongDate;
    }

    public void setProlongDate(Date prolongDate) {
        this.prolongDate = prolongDate;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getClientComment() {
        return clientComment;
    }

    public void setClientComment(String clientComment) {
        this.clientComment = clientComment;
    }

    public String getDoctorComment() {
        return doctorComment;
    }

    public void setDoctorComment(String doctorComment) {
        this.doctorComment = doctorComment;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }
}

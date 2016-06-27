package by.training.pharmacy.domain.order;


import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.user.User;

import java.util.Date;

/**
 * Created by vladislav on 18.06.16.
 */
public class Order {
    private int id;
    private User client;
    private Drug drug;
    private double drugCount;
    private short drugDosage;
    private OrderStatus orderStatus;
    private Date orderDate;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", client=" + client +
                ", drug=" + drug +
                ", drugCount=" + drugCount +
                ", drugDosage=" + drugDosage +
                ", orderStatus=" + orderStatus +
                ", orderDate=" + orderDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != order.id) return false;
        if (Double.compare(order.drugCount, drugCount) != 0) return false;
        if (drugDosage != order.drugDosage) return false;
        if (client != null ? !client.equals(order.client) : order.client != null) return false;
        if (drug != null ? !drug.equals(order.drug) : order.drug != null) return false;
        if (orderStatus != order.orderStatus) return false;
        return orderDate != null ? orderDate.equals(order.orderDate) : order.orderDate == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (drug != null ? drug.hashCode() : 0);
        temp = Double.doubleToLongBits(drugCount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) drugDosage;
        result = 31 * result + (orderStatus != null ? orderStatus.hashCode() : 0);
        result = 31 * result + (orderDate != null ? orderDate.hashCode() : 0);
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

    public double getDrugCount() {
        return drugCount;
    }

    public void setDrugCount(double drugCount) {
        this.drugCount = drugCount;
    }

    public short getDrugDosage() {
        return drugDosage;
    }

    public void setDrugDosage(short drugDosage) {
        this.drugDosage = drugDosage;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}

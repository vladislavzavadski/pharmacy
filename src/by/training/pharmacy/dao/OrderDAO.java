package by.training.pharmacy.dao;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.order.Order;
import by.training.pharmacy.domain.order.OrderStatus;
import by.training.pharmacy.domain.Period;

import java.util.Date;
import java.util.List;

/**
 * Created by vladislav on 19.06.16.
 */
public interface OrderDAO {
    List<Order> getUserOrders(String userLogin, int limit, int startFrom) throws DaoException;
    Order getOrderById(int orderId) throws DaoException;
    List<Order> getOrdersByStatus(OrderStatus orderStatus, int limit, int startFrom) throws DaoException;
    List<Order> getOrdersByDrugId(int drugId, int limit, int startFrom) throws DaoException;
    List<Order> getOrdersByDate(Date date, Period period, int limit, int startFrom) throws DaoException;
    void updateOrder(Order order) throws DaoException;
    void insertOrder(Order order) throws DaoException;
    void deleteOrder(int orderId) throws DaoException;
}

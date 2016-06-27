package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.drug.DrugManufacturer;
import by.training.pharmacy.domain.order.Order;
import by.training.pharmacy.domain.order.OrderStatus;
import by.training.pharmacy.domain.user.User;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by vladislav on 20.06.16.
 */
public class DatabaseOrderDAOTest {

    @Test
    public void getUserOrdersTest() throws Exception{
        DatabaseOrderDAO databaseOrderDAO = new DatabaseOrderDAO();
        List<Order> ordersActual = databaseOrderDAO.getUserOrders(DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        List<Order> ordersExpected = getOrderList(DatabaseDAOTestConstant.GET_USERS_ORDERS_QUERY, DatabaseDAOTestConstant.USER_LOGIN, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(ordersExpected, ordersActual);
    }

    @Test
    public void getOrderByIdTest() throws Exception{
        DatabaseOrderDAO databaseOrderDAO = new DatabaseOrderDAO();
        Order actual = getOrderById(DatabaseDAOTestConstant.ID_1);
        Order expected = databaseOrderDAO.getOrderById(DatabaseDAOTestConstant.ID_1);
        assertEquals(expected, actual);
    }

    @Test
    public void getOrdersByDrugIdTest() throws Exception{
        DatabaseOrderDAO databaseOrderDAO = new DatabaseOrderDAO();
        List<Order> expected = getOrderList(DatabaseDAOTestConstant.GET_ORDERS_BY_DRUG_ID_QUERY, DatabaseDAOTestConstant.ID_4, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        List<Order> actual = databaseOrderDAO.getOrdersByDrugId(DatabaseDAOTestConstant.ID_4, DatabaseDAOTestConstant.LIMIT, DatabaseDAOTestConstant.START_FROM);
        assertEquals(expected, actual);
    }

    @Test
    public void updateOrderTest() throws Exception{
        Order expected = getOrderById(DatabaseDAOTestConstant.ID_4);
        expected.setOrderStatus(OrderStatus.CANCELED);
        expected.setDrugCount(expected.getDrugCount()+2);
        DatabaseOrderDAO databaseOrderDAO = new DatabaseOrderDAO();
        databaseOrderDAO.updateOrder(expected);
        Order actual = databaseOrderDAO.getOrderById(DatabaseDAOTestConstant.ID_4);
        assertEquals(expected, actual);

    }

    @Test
    public void insertOrderTest() throws Exception{
        Order expected = getOrderById(DatabaseDAOTestConstant.ID_6);
        expected.setOrderStatus(OrderStatus.PAID);
        expected.setId(DatabaseDAOTestConstant.ID_645);
        DatabaseOrderDAO databaseOrderDAO = new DatabaseOrderDAO();
        databaseOrderDAO.insertOrder(expected);
        Order actual = databaseOrderDAO.getOrderById(DatabaseDAOTestConstant.ID_645);
        assertEquals(expected, actual);
        databaseOrderDAO.deleteOrder(DatabaseDAOTestConstant.ID_645);
    }

    @Test
    public void deleteOrderTest() throws Exception{
        DatabaseOrderDAO databaseOrderDAO = new DatabaseOrderDAO();
        Order temp = databaseOrderDAO.getOrderById(DatabaseDAOTestConstant.ID_6);
        databaseOrderDAO.deleteOrder(DatabaseDAOTestConstant.ID_6);
        Order result = databaseOrderDAO.getOrderById(DatabaseDAOTestConstant.ID_6);
        assertNull(result);
        databaseOrderDAO.insertOrder(temp);
    }

    public Order resultSetToOrder(ResultSet resultSet) {
        Order order = new Order();

        try {
            order.setId(resultSet.getInt("or_id"));
        } catch (SQLException e) {
            order.setId(0);
        }

        try {
            order.setDrugCount(resultSet.getDouble("or_drug_count"));
        } catch (SQLException e) {
            order.setDrugCount(0.0);
        }

        try {
            order.setDrugDosage(resultSet.getShort("or_drug_dosage"));
        } catch (SQLException e) {
            order.setDrugDosage((short) 0);
        }

        try {
            order.setOrderDate(resultSet.getDate("or_date"));
        } catch (SQLException e) {
            order.setOrderDate(null);
        }

        try {
            order.setOrderStatus(OrderStatus.valueOf(resultSet.getString("or_status").toUpperCase()));
        } catch (SQLException e) {
            order.setOrderStatus(null);
        }
        try {
            DatabaseDAO<User> userDAO = new DatabaseUserDAO();
            order.setClient(userDAO.resultSetToDomain(resultSet));
        } catch (DaoException e) {
            order.setClient(null);
        }

        try {
            DatabaseDAO<Drug> drugDAO = new DatabaseDrugDAO();
            order.setDrug(drugDAO.resultSetToDomain(resultSet));
        } catch (DaoException e) {
            order.setDrug(null);
        }
        return order;
    }

    public Order getOrderById(int drugId){
        Order result = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DatabaseDAOTestConstant.DATABASE_URL, DatabaseDAOTestConstant.DATABASE_USER, DatabaseDAOTestConstant.DATABASE_PASSWORD);
            preparedStatement = connection.prepareStatement(DatabaseDAOTestConstant.GET_ORDER_BY_ID_QUERY);
            preparedStatement.setInt(1, drugId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                result = resultSetToOrder(resultSet);
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

    public List<Order> getOrderList(String query, Object param, int limit, int startFrom){
        List<Order> result = new ArrayList<>();
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
            while (resultSet.next()) {
                result.add(resultSetToOrder(resultSet));
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

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

    private List<Order> resultSetToOrder(ResultSet resultSet) throws SQLException {
        List<Order> result = new ArrayList<>();
        while (resultSet.next()) {
            Order order = new Order();
            Drug drug = new Drug();
            User user = new User();
            order.setClient(user);
            order.setDrug(drug);
            order.setId(resultSet.getInt(TableColumn.ORDER_ID));
            order.setDrugCount(resultSet.getDouble(TableColumn.ORDER_DRUG_COUNT));
            order.setDrugDosage(resultSet.getShort(TableColumn.ORDER_DRUG_DOSAGE));
            order.setOrderDate(resultSet.getDate(TableColumn.ORDER_DATE));
            order.setOrderStatus(OrderStatus.valueOf(resultSet.getString(TableColumn.ORDER_STATUS).toUpperCase()));
            user.setLogin(resultSet.getString(TableColumn.USER_LOGIN));
            user.setFirstName(resultSet.getString(TableColumn.USER_FIRST_NAME));
            user.setSecondName(resultSet.getString(TableColumn.USER_SECOND_NAME));
            result.add(order);
        }
        return result;
    }

    private Order getOrderById(int drugId){
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
                result = resultSetToOrder(resultSet).get(0);
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

    private List<Order> getOrderList(String query, Object param, int limit, int startFrom){
        List<Order> result;
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

            result = resultSetToOrder(resultSet);

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

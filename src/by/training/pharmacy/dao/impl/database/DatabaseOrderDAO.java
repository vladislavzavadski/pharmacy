package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.OrderDAO;
import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;
import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.dao.impl.database.util.DatabaseOperation;
import by.training.pharmacy.domain.order.Order;
import by.training.pharmacy.domain.order.OrderStatus;
import by.training.pharmacy.domain.Period;
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
public class DatabaseOrderDAO implements OrderDAO {
    private static final String GET_ORDER_BY_ID_QUERY = "SELECT us_login, us_first_name, us_second_name, dr_id, dr_name, or_id, or_drug_count, or_drug_dosage, or_status, or_date from orders inner join users on us_login = or_client_login inner join drugs on dr_id = or_drug_id where or_id=? LIMIT 1;";
    private static final String GET_USER_ORDERS_QUERY = "SELECT us_login, us_first_name, us_second_name, dr_id, dr_name, or_id, or_drug_count, or_drug_dosage, or_status, or_date from orders inner join users on us_login = or_client_login inner join drugs on dr_id = or_drug_id where or_client_login=? LIMIT ?, ?;";
    private static final String GET_ORDERS_BY_STATUS_QUERY = "SELECT us_login, us_first_name, us_second_name, dr_id, dr_name, or_id, or_drug_count, or_drug_dosage, or_status, or_date from orders inner join users on us_login = or_client_login inner join drugs on dr_id = or_drug_id where or_status=? LIMIT ?, ?;";
    private static final String GET_ORDERS_BY_DRUG_ID_QUERY = "SELECT us_login, us_first_name, us_second_name, dr_id, dr_name, or_id, or_drug_count, or_drug_dosage, or_status, or_date from orders inner join users on us_login = or_client_login inner join drugs on dr_id = or_drug_id where or_drug_id=? LIMIT ?, ?;";
    private static final String GET_ORDERS_BY_DATE_BEFORE_QUERY = "SELECT us_login, us_first_name, us_second_name, dr_id, dr_name, or_id, or_drug_count, or_drug_dosage, or_status, or_date from orders inner join users on us_login = or_client_login inner join drugs on dr_id = or_drug_id where or_date<? LIMIT ?, ?;";
    private static final String GET_ORDERS_BY_DATE_AFTER_QUERY = "SELECT us_login, us_first_name, us_second_name, dr_id, dr_name, or_id, or_drug_count, or_drug_dosage, or_status, or_date from orders inner join users on us_login = or_client_login inner join drugs on dr_id = or_drug_id where or_date>? LIMIT ?, ?;";
    private static final String GET_ORDERS_BY_DATE_CURRENT_QUERY = "SELECT us_login, us_first_name, us_second_name, dr_id, dr_name, or_id, or_drug_count, or_drug_dosage, or_status, or_date from orders inner join users on us_login = or_client_login inner join drugs on dr_id = or_drug_id where or_date=? LIMIT ?, ?;";
    private static final String INSERT_ORDER_QUERY = "INSERT INTO orders (or_id, or_client_login, or_drug_id, or_drug_count, or_drug_dosage, or_status, or_date) VALUES(?, ?, ?, ?, ?, ?, ?);";
    private static final String DELETE_ORDER_QUERY = "delete from orders where or_id=?;";
    private static final String UPDATE_ORDER_QUERY = "UPDATE orders SET or_drug_id=?, or_drug_count=?, or_drug_dosage=?, or_status=? WHERE or_id=?";
    private static final Logger logger = LogManager.getLogger(DatabaseOrderDAO.class);
    protected DatabaseOrderDAO() throws DaoException {

    }

    @Override
    public List<Order> getUserOrders(String userLogin, int limit, int startFrom) throws DaoException {
        List<Order> orders = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_USER_ORDERS_QUERY, userLogin, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            orders = resultSetToOrder(resultSet);
            return orders;
        } catch (Exception e) {
            logger.error("Method: DatabaseOrderDAO.getUserOrders", e);
            throw new DaoException("Cannot load orders with or_client_login = \'"+userLogin+"\' from database", e);
        }

    }

    @Override
    public Order getOrderById(int orderId) throws DaoException {
        try(DatabaseOperation databaseOperation = new DatabaseOperation(GET_ORDER_BY_ID_QUERY, orderId)) {
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            List<Order> result = resultSetToOrder(resultSet);
            if(!result.isEmpty()){
                return result.get(0);
            }
        } catch (Exception e) {
            logger.error("Method: DatabaseOrderDAO.getOrderById", e);
            throw new DaoException("Can not read order with id = \'"+orderId+"\' from database", e);
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus orderStatus, int limit, int startFrom) throws DaoException {
        List<Order> orders = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_ORDERS_BY_STATUS_QUERY, orderStatus.toString().toLowerCase(), limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            orders = resultSetToOrder(resultSet);
            return orders;
        } catch (Exception e) {
            logger.error("Method: DatabaseOrderDAO.getOrdersByStatus", e);
            throw new DaoException("Can not load orders with status = \'"+orderStatus+"\' from database", e);
        }
    }

    @Override
    public List<Order> getOrdersByDrugId(int drugId, int limit, int startFrom) throws DaoException {
        List<Order> orders = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_ORDERS_BY_DRUG_ID_QUERY, drugId, limit, startFrom)){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            orders = resultSetToOrder(resultSet);
            return orders;
        } catch (Exception e) {
            logger.error("Method: DatabaseOrderDAO.getOrdersByDrugId", e);
            throw new DaoException("Can not load orders with drugId = \'"+drugId+"\'", e);
        }
    }

    @Override
    public List<Order> getOrdersByDate(Date date, Period period, int limit, int startFrom) throws DaoException {
        List<Order> orders = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation()){
            switch (period) {
                case AFTER_DATE: {
                    break;
                }
                case BEFORE_DATE: {
                    databaseOperation.init(GET_ORDERS_BY_DATE_BEFORE_QUERY, date, limit, startFrom);
                    break;
                }
                case CURRENT_DATE: {
                    databaseOperation.init(GET_ORDERS_BY_DATE_CURRENT_QUERY, date, limit, startFrom);
                    break;
                }
            }
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            orders = resultSetToOrder(resultSet);
        }catch (Exception e){
            logger.error("Method: DatabaseOrderDAO.getOrdersByDate", e);
            throw new DaoException("Can not load orders from database", e);
        }

        return orders;
    }

    @Override
    public void updateOrder(Order order) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(UPDATE_ORDER_QUERY, order.getDrug().getId(), order.getDrugCount(), order.getDrugDosage(), order.getOrderStatus().toString().toLowerCase(), order.getId())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseOrderDAO.updateOrder", e);
            throw new DaoException("Can not update order "+order, e);
        }
    }



    @Override
    public void insertOrder(Order order) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(INSERT_ORDER_QUERY, order.getId(), order.getClient().getLogin(), order.getDrug().getId(), order.getDrugCount(), order.getDrugDosage(), order.getOrderStatus().toString().toLowerCase(), order.getOrderDate())){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseOrderDAO.insertOrder", e);
            throw new DaoException("Can not insert new order "+ order +" to database", e);
        }
    }

    @Override
    public void deleteOrder(int orderId) throws DaoException {
        try (DatabaseOperation databaseOperation = new DatabaseOperation(DELETE_ORDER_QUERY, orderId)){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseOrderDAO.deleteOrder", e);
            throw new DaoException("Can not delete order with id = \'"+orderId+"\'", e);
        }
    }

    private List<Order> resultSetToOrder(ResultSet resultSet) throws SQLException {
        List<Order> result = new ArrayList<>();
        while (resultSet.next()) {
            Order order = new Order();
            Drug drug = new Drug();
            User user = new User();
            order.setClient(user);
            order.setDrug(drug);
            order.setId(resultSet.getInt("or_id"));
            order.setDrugCount(resultSet.getDouble("or_drug_count"));
            order.setDrugDosage(resultSet.getShort("or_drug_dosage"));
            order.setOrderDate(resultSet.getDate("or_date"));
            order.setOrderStatus(OrderStatus.valueOf(resultSet.getString("or_status").toUpperCase()));
            user.setLogin(resultSet.getString("us_login"));
            user.setFirstName(resultSet.getString("us_first_name"));
            user.setSecondName(resultSet.getString("us_second_name"));
            result.add(order);
        }
        return result;
    }
}

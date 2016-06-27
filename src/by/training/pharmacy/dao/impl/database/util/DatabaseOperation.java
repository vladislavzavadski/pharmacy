package by.training.pharmacy.dao.impl.database.util;

import by.training.pharmacy.dao.connection_pool.ConnectionPool;
import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by vladislav on 23.06.16.
 */
public class DatabaseOperation implements AutoCloseable {

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private static ConnectionPool connectionPool;

    public DatabaseOperation(String sqlQuery, Object ... queryParams) throws ConnectionPoolException, SQLException {
        this();
        connection = connectionPool.takeConnection();
        preparedStatement = initPreparedStatement(sqlQuery, queryParams);
    }

    public DatabaseOperation() throws ConnectionPoolException {
        if(connectionPool!=null) {
            connectionPool = ConnectionPool.getInstance();
        }
    }
    public void init(String sqlQuery, Object ... queryParams) throws ConnectionPoolException, SQLException {
        connection = connectionPool.takeConnection();
        preparedStatement = initPreparedStatement(sqlQuery, queryParams);
    }

    public int invokeWriteOperation() throws SQLException {
        return preparedStatement.executeUpdate();
    }

    private PreparedStatement initPreparedStatement(String query, Object[] params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for(int i=0; i<params.length; i++){
            preparedStatement.setObject(i+1, params[i]);
        }

        return preparedStatement;
    }

    public ResultSet invokeReadOperation() throws SQLException {
        resultSet = preparedStatement.executeQuery();
        return resultSet;

    }

    @Override
    public void close() throws Exception {
        if(resultSet!=null){
            resultSet.close();
        }
        if(preparedStatement!=null){
            preparedStatement.close();
        }
        if(connection!=null){
            connection.close();
        }
    }
}

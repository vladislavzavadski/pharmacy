package by.training.pharmacy.dao.connection_pool;

import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vladislav on 09.06.16.
 */
public class ConnectionPool {

    private static ConnectionPool connectionPool = null;

    private List<Connection> freeConnections = new ArrayList<>();
    private List<Connection> usedConnections = new ArrayList<>();

    private String databaseURL;
    private String username;
    private String password;
    private String driver;
    private int poolSize;

    public static ConnectionPool getInstance() throws ConnectionPoolException {
        if(connectionPool==null) {
            synchronized (ConnectionPool.class) {
                if(connectionPool==null) {
                    connectionPool = new ConnectionPool();
                }
            }
        }
        return connectionPool;
    }

    private ConnectionPool() throws ConnectionPoolException {
        DBResourceManager dbResourceManager = DBResourceManager.getInstance();
        databaseURL = dbResourceManager.getProperty(DBParameter.DB_URL);
        username = dbResourceManager.getProperty(DBParameter.DB_USERNAME);
        password = dbResourceManager.getProperty(DBParameter.DB_PASSWORD);
        driver = dbResourceManager.getProperty(DBParameter.DB_DRIVER);
        try {
            poolSize = Integer.parseInt(dbResourceManager.getProperty(DBParameter.DB_POOL_SIZE));
        }
        catch (NumberFormatException ex){
            poolSize = 5;
        }
        initConnectionPool();
    }

    private void initConnectionPool() throws ConnectionPoolException{
        try {
            Class.forName(driver);
            for(int i=0; i<poolSize; i++) {
                Connection connection = DriverManager.getConnection(databaseURL, username, password);
                PooledConnection pooledConnection = new PooledConnection(connection);
                freeConnections.add(pooledConnection);
            }

        } catch (ClassNotFoundException e) {
            throw new ConnectionPoolException("Database driver was not found.", e);
        } catch (SQLException e) {
            throw new ConnectionPoolException("Can't create database connection", e);
        }

    }

    public Connection takeConnection() throws ConnectionPoolException {
        Connection connection;
        synchronized (freeConnections) {
            if (freeConnections.isEmpty()) {
                try {
                    freeConnections.wait();
                } catch (InterruptedException e) {
                    throw new ConnectionPoolException("Can not take connection.");
                }
                //throw new ConnectionPoolException("There are no free connections");
            }
            connection = freeConnections.remove(freeConnections.size() - 1);
        }
        synchronized (usedConnections) {
            usedConnections.add(connection);
        }
        return connection;
    }

    public synchronized void dispose(){
        clearConnectionList();
    }

    private void clearConnectionList(){
        try {
            synchronized (freeConnections) {
                closeConnectionList(freeConnections);
            }
            synchronized (usedConnections) {
                closeConnectionList(usedConnections);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeConnectionList(List<Connection> connectionList) throws SQLException {
        for(int i=0; i<connectionList.size(); i++){
            Connection connection = connectionList.remove(connectionList.size()-1);
            if(!connection.getAutoCommit()){
                connection.commit();
            }
            ((PooledConnection)connection).reallyClose();
        }
    }

    private class PooledConnection implements Connection{

        private Connection connection;

        public PooledConnection(Connection connection) {
            this.connection = connection;
        }

        public void reallyClose() throws SQLException {
            connection.close();
        }

        @Override
        public void close() throws SQLException {

            if(connection.isClosed()) {
                throw new SQLException("Given connection already closed");
            }

            if(connection.isReadOnly()) {
                connection.setReadOnly(false);
            }
            synchronized (usedConnections) {
                if (!usedConnections.remove(this)) {
                    throw new SQLException("Error deleting connection from used connections pool");
                }
            }
            synchronized (freeConnections) {
                if (!freeConnections.add(this)) {
                    throw new SQLException("Error adding connection to free connections pool");
                }
                freeConnections.notify();
            }
        }

        @Override
        public Statement createStatement() throws SQLException {
            return connection.createStatement();
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return connection.prepareStatement(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return connection.prepareCall(sql);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return connection.nativeSQL(sql);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            connection.setAutoCommit(autoCommit);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return connection.getAutoCommit();
        }

        @Override
        public void commit() throws SQLException {
            connection.commit();
        }

        @Override
        public void rollback() throws SQLException {
            connection.commit();
        }



        @Override
        public boolean isClosed() throws SQLException {
            return connection.isClosed();
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return connection.getMetaData();
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            connection.setReadOnly(readOnly);
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return connection.isReadOnly();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            connection.setCatalog(catalog);
        }

        @Override
        public String getCatalog() throws SQLException {
            return connection.getCatalog();
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            connection.setTransactionIsolation(level);
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return connection.getTransactionIsolation();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return connection.getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            connection.clearWarnings();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return connection.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return connection.getTypeMap();
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            connection.setTypeMap(map);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            connection.setHoldability(holdability);
        }

        @Override
        public int getHoldability() throws SQLException {
            return connection.getHoldability();
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return connection.setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return connection.setSavepoint(name);
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            connection.rollback(savepoint);
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            connection.releaseSavepoint(savepoint);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return connection.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return connection.prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return connection.prepareStatement(sql, columnNames);
        }

        @Override
        public Clob createClob() throws SQLException {
            return connection.createClob();
        }

        @Override
        public Blob createBlob() throws SQLException {
            return connection.createBlob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            return connection.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return connection.createSQLXML();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return connection.isValid(timeout);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            connection.setClientInfo(name, value);
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            connection.setClientInfo(properties);
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return connection.getClientInfo(name);
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return connection.getClientInfo();
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return connection.createArrayOf(typeName, elements);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return connection.createStruct(typeName, attributes);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            connection.setSchema(schema);
        }

        @Override
        public String getSchema() throws SQLException {
            return connection.getSchema();
        }

        @Override
        public void abort(Executor executor) throws SQLException {
            connection.abort(executor);
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            connection.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return connection.getNetworkTimeout();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return connection.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return connection.isWrapperFor(iface);
        }
    }


}

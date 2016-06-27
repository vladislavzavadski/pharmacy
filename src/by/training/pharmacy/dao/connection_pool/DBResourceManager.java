package by.training.pharmacy.dao.connection_pool;

import java.util.ResourceBundle;

/**
 * Created by vladislav on 09.06.16.
 */
public class DBResourceManager {
    private static DBResourceManager instance = new DBResourceManager();
    private ResourceBundle resourceBundle;
    private DBResourceManager(){
        resourceBundle = ResourceBundle.getBundle("resource.connection_pool.db");
    }

    public static DBResourceManager getInstance(){
        return instance;
    }

    public String getProperty(String name){
        return resourceBundle.getString(name);
    }
}

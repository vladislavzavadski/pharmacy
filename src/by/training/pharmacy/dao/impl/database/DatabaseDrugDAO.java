package by.training.pharmacy.dao.impl.database;

import by.training.pharmacy.dao.DrugDAO;
import by.training.pharmacy.dao.connection_pool.ConnectionPool;
import by.training.pharmacy.dao.connection_pool.exception.ConnectionPoolException;
import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.dao.impl.database.util.DatabaseOperation;
import by.training.pharmacy.domain.drug.Drug;
import by.training.pharmacy.domain.drug.DrugClass;
import by.training.pharmacy.domain.drug.DrugManufacturer;
import by.training.pharmacy.domain.drug.DrugType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by vladislav on 15.06.16.
 */
public class DatabaseDrugDAO implements DrugDAO {

    private static final String GET_DRUGS_BY_ID_QUERY = "SELECT dr_id, dr_class, dr_description, dr_image, dr_in_stock, dr_manufacturer, dr_name, dr_prescription_enable, dr_price, dr_type,  dr_dosage, dr_active_substance, dm_id,  dm_name, dm_country, dm_description, dr_class_name, dr_class_description FROM drugs inner join drugs_manufactures on dr_manufacturer = dm_id inner join drug_classes on dr_class = dr_class_name WHERE dr_id=?;";
    private static final String GET_DRUGS_BY_NAME_QUERY = "SELECT dr_id, dr_class, dr_description, dr_image, dr_in_stock, dr_manufacturer, dr_name, dr_prescription_enable, dr_price, dr_type,  dr_dosage, dr_active_substance, dm_id,  dm_name, dm_country, dm_description, dr_class_name, dr_class_description FROM drugs inner join drugs_manufactures on dr_manufacturer = dm_id inner join drug_classes on dr_class = dr_class_name WHERE dr_name LIKE ? LIMIT ?, ?;";
    private static final String GET_DRUGS_BY_CLASS_QUERY = "SELECT dr_id, dr_class, dr_description, dr_image, dr_in_stock, dr_manufacturer, dr_name, dr_prescription_enable, dr_price, dr_type,  dr_dosage, dr_active_substance, dm_id, dm_name, dm_country, dm_description, dr_class_name, dr_class_description FROM drugs inner join drugs_manufactures on dr_manufacturer = dm_id inner join drug_classes on dr_class = dr_class_name WHERE dr_class=? LIMIT ?, ?;";
    private static final String GET_DRUGS_BY_ACTIVE_SUBSTANCE_QUERY = "SELECT dr_id, dr_class, dr_description, dr_image, dr_in_stock, dr_manufacturer, dr_name, dr_prescription_enable, dr_price, dr_type,  dr_dosage, dr_active_substance, dm_id, dm_name, dm_country, dm_description, dr_class_name, dr_class_description FROM drugs inner join drugs_manufactures on dr_manufacturer = dm_id inner join drug_classes on dr_class = dr_class_name WHERE dr_active_substance=? LIMIT ?, ?;";
    private static final String INSERT_DRUG_QUERY = "insert into drugs (dr_class, dr_description, dr_image, dr_in_stock, dr_manufacturer, dr_name, dr_prescription_enable, dr_price, dr_type,  dr_dosage, dr_active_substance) values (?,?,?,?,?,?,?,?,?,?,?);";
    private static final String UPDATE_DRUG_QUERY = "update drugs set dr_description = ?, dr_image = ?, dr_in_stock = ?, dr_prescription_enable = ?, dr_dosage = ? where dr_id=?;";
    private static final String DELETE_DRUG_QUERY = "delete from drugs where dr_id=?;";
    private static final Logger logger = LogManager.getLogger(DatabaseDrugClassDAO.class);


    @Override
    public Drug getDrugById(int drugId) throws DaoException {

        Drug drug = null;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_DRUGS_BY_ID_QUERY, drugId)){

            ResultSet resultSet = databaseOperation.invokeReadOperation();
            List<Drug> result = resultSetToDrug(resultSet);
            if(!result.isEmpty()){
                drug = result.get(0);
            }
            return drug;
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugDAO.getDrugById", e);
            throw new DaoException("Can not load drug with id = \'"+drugId+"\' from database", e);
        }

    }

    @Override
    public List<Drug> getDrugsByName(String name, int limit, int startFrom) throws DaoException {
        List<Drug> drugs;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_DRUGS_BY_NAME_QUERY, name, limit, startFrom);){
            ResultSet resultSet = databaseOperation.invokeReadOperation();
            drugs = resultSetToDrug(resultSet);
            return drugs;
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugDAO.getDrugsByName", e);
            throw new DaoException("Can not load drugs with name like \'"+name+"\' from database", e);
        }
    }

    @Override
    public List<Drug> getDrugsByClass(String drugClass, int limit, int startFrom) throws DaoException {
        List<Drug> drugs;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_DRUGS_BY_CLASS_QUERY, drugClass, limit, startFrom);){

            ResultSet resultSet = databaseOperation.invokeReadOperation();
            drugs = resultSetToDrug(resultSet);
            return drugs;
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugDAO.getDrugsByClass", e);
            throw new DaoException("Can not load drugs with class = \'"+drugClass+"\' from database", e);
        }

    }

    @Override
    public List<Drug> getDrugsByActiveSubstance(String activeSubstance, int limit, int startFrom) throws DaoException {
        List<Drug> drugs;
        try (DatabaseOperation databaseOperation = new DatabaseOperation(GET_DRUGS_BY_ACTIVE_SUBSTANCE_QUERY, activeSubstance, limit, startFrom)){

            ResultSet resultSet = databaseOperation.invokeReadOperation();
            drugs = resultSetToDrug(resultSet);
            return drugs;
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugDAO.getDrugsByActiveSubstance", e);
            throw new DaoException("Can not load drugs with activeSubstance = \'"+activeSubstance+"\' from database", e);
        }

    }


    @Override
    public void insertDrug(Drug drug) throws DaoException {
        String dosages = "";
        for(int i:drug.getDosages()){
            dosages+=","+i;
        }
        try(DatabaseOperation databaseOperation = new DatabaseOperation(INSERT_DRUG_QUERY, drug.getDrugClass().getName(), drug.getDescription(), drug.getDrugImage(), drug.isInStock()
                ,drug.getDrugManufacturer().getId(), drug.getName(), drug.isPrescriptionEnable(), drug.getPrice(), drug.getType().toString().toLowerCase(), dosages.substring(1), drug.getActiveSubstance())) {

            databaseOperation.invokeWriteOperation();

        } catch (Exception e) {
            logger.error("Method: DatabaseDrugDAO.insertDrug", e);
            throw new DaoException("Can not insert drug into database", e);
        }

    }

    @Override
    public void updateDrug(Drug drug) throws DaoException {
        String dosages = "";
        for (int i : drug.getDosages()) {
            dosages += "," + i;
        }
        try (DatabaseOperation databaseOperation = new DatabaseOperation(UPDATE_DRUG_QUERY, drug.getDescription(), drug.getDrugImage(), drug.isInStock(), drug.isPrescriptionEnable(), dosages.substring(1), drug.getId());){
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugDAO.updateDrug", e);
            throw new DaoException("Can not update drug into database", e);
        }

    }

    @Override
    public void deleteDrug(int drugId) throws DaoException {
        try(DatabaseOperation databaseOperation = new DatabaseOperation(DELETE_DRUG_QUERY, drugId)) {
            databaseOperation.invokeWriteOperation();
        } catch (Exception e) {
            logger.error("Method: DatabaseDrugDAO.deleteDrug", e);
            throw new DaoException("Can not delete drug with id = \'"+drugId+"\' from database", e);
        }

    }


    private List<Drug> resultSetToDrug(ResultSet resultSet) throws SQLException {
        List<Drug> result = new ArrayList<>();
        while (resultSet.next()) {
            Drug drug = new Drug();
            DrugManufacturer drugManufacturer = new DrugManufacturer();
            DrugClass drugClass = new DrugClass();
            drug.setDrugManufacturer(drugManufacturer);
            drug.setDrugClass(drugClass);
            drug.setId(resultSet.getInt(TableColumn.DRUG_ID));
            drug.setName(resultSet.getString(TableColumn.DRUG_NAME));
            drug.setDrugImage(resultSet.getBytes(TableColumn.DRUG_IMAGE));
            drug.setDescription(resultSet.getString(TableColumn.DRUG_DESCRIPTION));
            drug.setPrice(resultSet.getFloat(TableColumn.DRUG_PRICE));
            drug.setActiveSubstance(resultSet.getString(TableColumn.DRUG_ACTIVE_SUBSTANCE));
            drug.setPrescriptionEnable(resultSet.getBoolean(TableColumn.DRUG_PRESCRIPTION_ENABLE));
            drug.setInStock(resultSet.getBoolean(TableColumn.DRUG_IN_STOCK));
            drug.setType(DrugType.valueOf(resultSet.getString(TableColumn.DRUG_TYPE).toUpperCase()));
            String[] dosages = resultSet.getString(TableColumn.DRUG_DOSAGE).split(",");
            for (String dosage : dosages) {
                drug.getDosages().add(Integer.parseInt(dosage));
            }
            drugManufacturer.setId(resultSet.getInt(TableColumn.DRUG_MANUFACTURE_ID));
            drugManufacturer.setName(resultSet.getString(TableColumn.DRUG_MANUFACTURE_NAME));
            drugManufacturer.setDescription(resultSet.getString(TableColumn.DRUG_MANUFACTURE_DESCRIPTION));
            drugManufacturer.setCountry(resultSet.getString(TableColumn.DRUG_MANUFACTURE_COUNTRY));
            drugClass.setName(resultSet.getString(TableColumn.DRUG_CLASS_NAME));
            drugClass.setDescription(resultSet.getString(TableColumn.DRUG_CLASS_DESCRIPTION));
            result.add(drug);
        }
        return result;

    }
}

package by.training.pharmacy.dao;

import by.training.pharmacy.dao.exception.DaoException;
import by.training.pharmacy.domain.user.UserDescription;

/**
 * Created by vladislav on 18.06.16.
 */
public interface UserDescriptionDAO {
    void insertUserDescription(UserDescription userDescription) throws DaoException;
    UserDescription getUserDescriptionByLogin(String userLogin) throws DaoException;
    void updateUserDescription(UserDescription userDescription) throws DaoException;
    void deleteUserDescription(String userLogin) throws DaoException;
}

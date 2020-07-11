package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.AdminDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private AdminDao adminDao;

    /* Checks if the current user is Admin or not. */
    private boolean confirmAdmin(final String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthToken = userBusinessService.getUserbyToken(accessToken);

        if(userAuthToken.getUserId().getRole().equals("admin")){
            return true;
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }
    }

    /* If the Uuid of the user to be deleted is present in the DB, then delete that user. */
    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(String accessToken, String userId) throws UserNotFoundException, AuthorizationFailedException {
        UserEntity userById = userBusinessService.getUserById(userId);
        if(this.confirmAdmin(accessToken)) {
            adminDao.deleteUserByUuid(userId);
        }
        return userId;
    }

}

package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /* Check if the accessToken is present in DB or not. */
    public UserAuthEntity getUserbyToken(final String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthToken = userDao.getUserAuthByToken(accessToken);

        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthToken.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        return userAuthToken;
    }

    /* Check if the UserUUID is present in DB or not. */
    public UserEntity getUserById(final String userUuid) throws UserNotFoundException {
        UserEntity userId = userDao.getUserById(userUuid);

        if(userId == null){
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        return userId;
    }

    /* Get the userProfile */
    public UserEntity getUserProfile(final String userUuid, final String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        getUserbyToken(accessToken);
        UserEntity userById = getUserById(userUuid);
        return userById;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException  {
        if(userDao.isUsernameExists(userEntity.getUserName())) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        if(userDao.isEmailExists(userEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        UserEntity signUpUser = userDao.createUser(userEntity);

        return signUpUser;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signin(final String username, final String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserByUsername(username);
        if(userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthEntity = new UserAuthEntity();

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthEntity.setUuid(UUID.randomUUID().toString());
            userAuthEntity.setUserId(userEntity);
            userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthEntity.setExpiresAt(expiresAt);
            userAuthEntity.setLoginAt(now);

            userDao.createAuthToken(userAuthEntity);

            return userAuthEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String signout(final String accessToken) throws SignOutRestrictedException {
        ZonedDateTime currentTime = ZonedDateTime.now();
        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(accessToken);

        if(userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        userDao.updateUserLogoutByToken(accessToken, currentTime);
        return userAuthEntity.getUserId().getUuid();
    }
}

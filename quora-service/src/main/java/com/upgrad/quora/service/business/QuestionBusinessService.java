package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    /* Creating a question */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException {

        // Login Validations
        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
        isUserSignedIn(userAuthEntity);
        if(userAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        questionEntity.setUserId(userAuthEntity.getUserId());
        return questionDao.createQuestion(questionEntity);
    }

    /* Get All Questions */
    public List<QuestionEntity> getAllQuestions(final String authorizationToken) throws AuthorizationFailedException {

        // Login Validations
        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
        isUserSignedIn(userAuthEntity);
        if(userAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }

        return questionDao.getAllQuestions();
    }

    /* Update/edit a question */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(QuestionEntity questionEntity, String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        // Login Validations
        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
        isUserSignedIn(userAuthEntity);
        if(userAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }

        // Question Validations
        QuestionEntity existingQuestion = questionDao.getQuestionById(questionEntity.getUuid());
        isValidQuestion(existingQuestion);
        isValidOwner(userAuthEntity, existingQuestion);

        questionEntity.setId(existingQuestion.getId());
        questionEntity.setDateCreated(existingQuestion.getDateCreated());
        questionEntity.setUserId(existingQuestion.getUserId());

        return questionDao.editQuestionContent(questionEntity);
    }

    /* Delete a question */
    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestion(String uuid, String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {

        // Login Validations
        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
        isUserSignedIn(userAuthEntity);
        if(userAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        // Question Validations
        QuestionEntity existingQuestion = questionDao.getQuestionById(uuid);
        isValidQuestion(existingQuestion);
        isOwnerOrAdmin(userAuthEntity, existingQuestion);

        return questionDao.deleteQuestion(existingQuestion);
    }

    /* Get questions by Id */
    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {

        // Login Validations
        UserAuthEntity userAuthEntity = userDao.getUserAuthByToken(authorizationToken);
        isUserSignedIn(userAuthEntity);
        if(userAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        // User Validation
        UserEntity existingUser = userDao.getUserById(userId);
        if(existingUser == null){
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        return questionDao.getAllQuestionsByUser(existingUser);
    }

    /* *************************** */
    /* Auxiliary (private) Methods */
    /* *************************** */
    private void isUserSignedIn(UserAuthEntity userAuthToken) throws AuthorizationFailedException {
        if(userAuthToken == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
    }

    private void isValidQuestion (QuestionEntity existingQuestion) throws InvalidQuestionException {
        if (existingQuestion == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
    }

    private void isValidOwner (UserAuthEntity userAuthEntity, QuestionEntity existingQuestion) throws AuthorizationFailedException {
        if (!userAuthEntity.getUserId().equals(existingQuestion.getUserId())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
    }

    private void isOwnerOrAdmin (UserAuthEntity userAuthEntity, QuestionEntity existingQuestion) throws AuthorizationFailedException {
        boolean isOwner = userAuthEntity.getUserId().equals(existingQuestion.getUserId());
        boolean isAdmin = userAuthEntity.getUserId().getRole().equals("admin");
        if (!isOwner && !isAdmin) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
    }
}

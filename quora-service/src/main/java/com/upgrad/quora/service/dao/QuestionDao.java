package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    /* Create a question */
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /* Get all questions */
    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /* Update/edit a question */
    public QuestionEntity editQuestionContent (final QuestionEntity questionEntity) {
        return entityManager.merge(questionEntity);
    }

    /* Get question by Id */
    public QuestionEntity getQuestionById(final String uuid) {
        try {
            return entityManager.createNamedQuery("getQuestionByUuid", QuestionEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /* Delete a question by Id */
    public String deleteQuestion(final QuestionEntity existingQuestion) {
        entityManager.remove(existingQuestion);
        return existingQuestion.getUuid();
    }

    /* Get questions by User */
    public List<QuestionEntity> getAllQuestionsByUser(UserEntity userEntity) {
        try {
            return entityManager.createNamedQuery("QuestionByUserId", QuestionEntity.class).setParameter("user", userEntity).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}

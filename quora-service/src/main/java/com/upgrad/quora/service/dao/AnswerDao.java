package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /*This method creates an answer in the database*/

    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /*This method updates/edits an answer in the database*/

    public AnswerEntity editAnswer(AnswerEntity answerEntity){
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    /*This method deletes an answer from the database*/

    public AnswerEntity deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    /*This method returns an answer from the database based on the answer ID*/

    public AnswerEntity getAnswerByUuid(final String answerUuid) {
        try{
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class)
                    .setParameter("answerUuid", answerUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /*This method returns a list of all the answers from the database based on the question ID*/

    public List<AnswerEntity> getAllAnswersByQuestion(final String questionUuid) {
        try{
            return entityManager.createNamedQuery("getAnswersByQuestion", AnswerEntity.class)
                    .setParameter("questionUuid", questionUuid).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}

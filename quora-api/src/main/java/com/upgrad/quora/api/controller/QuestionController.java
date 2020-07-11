package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    /* Create Question Controller*/
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        final QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDateCreated(ZonedDateTime.now());

        final QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity, authorization);
        QuestionResponse createQuestionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(createQuestionResponse, HttpStatus.CREATED);
    }

    /* Get All Questions*/
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        final List<QuestionEntity> allQuestionsList = questionBusinessService.getAllQuestions(authorization);
        ArrayList<QuestionDetailsResponse> questionDetailsResponses = convertToQuestionDetailsResponseArray(allQuestionsList);
        return new ResponseEntity<>(questionDetailsResponses, HttpStatus.OK);
    }

    /* Edit Question Controller */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest,
                                                             @PathVariable("questionId") final String questionId,
                                                             @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(questionId);
        questionEntity.setContent(questionEditRequest.getContent());

        QuestionEntity updatedQuestion = questionBusinessService.editQuestionContent(questionEntity, authorization);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse()
                .id(updatedQuestion.getUuid())
                .status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    /* Delete Question Controller */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId,
                                                                 @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {
        String deletedQuestionId = questionBusinessService.deleteQuestion(questionId, authorization);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse()
                .id(deletedQuestionId).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    /* Get All Questions of a user */
    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList> getAllQuestionsByUser(@PathVariable("userId") final String userId,
                                                           @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {

        final List<QuestionEntity> allQuestionsList = questionBusinessService.getAllQuestionsByUser(userId, authorization);

        ArrayList<QuestionDetailsResponse> questionDetailsResponses = convertToQuestionDetailsResponseArray(allQuestionsList);
        return new ResponseEntity<>(questionDetailsResponses, HttpStatus.OK);
    }

    /* *************************** */
    /* Auxiliary (private) Methods */
    /* *************************** */
    private ArrayList<QuestionDetailsResponse> convertToQuestionDetailsResponseArray(List<QuestionEntity> allQuestionsList) {

        ArrayList<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();
        for (QuestionEntity question: allQuestionsList) {
            QuestionDetailsResponse questionDetails = new QuestionDetailsResponse();
            questionDetails.setId(question.getUuid());
            questionDetails.setContent(question.getContent());
            questionDetailsResponses.add(questionDetails);
        }

        return questionDetailsResponses;
    }
}

package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    /* Signup method for endpoint "/user/signup" */
    @RequestMapping(method= RequestMethod.POST, path="signup", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        final UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("1234abc");
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutme(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        //After setting the attributes of userEntity, pass it to signup() method, to add this object to a persistent context
        final UserEntity createdUserEntity = userBusinessService.signup(userEntity);

        //Declaring an object of SignupUserResponse type and set its attributes using createdUserEntity.
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    /* SignIn method for endpoint "/user/signin". */
    @RequestMapping(method = RequestMethod.POST, path = "signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decoded);
        String[] decodedArray = decodedText.split(":");

        UserAuthEntity userAuthToken = userBusinessService.signin(decodedArray[0], decodedArray[1]);

        SigninResponse signinResponse = new SigninResponse();

        signinResponse.setId(userAuthToken.getUserId().getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", userAuthToken.getAccessToken());

        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK );
    }

    /* SignIn method for endpoint "/user/signout". */
    @RequestMapping(method = RequestMethod.POST, path = "signout")
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {
        String signedOutUser = userBusinessService.signout(accessToken);
        SignoutResponse signoutResponse = new SignoutResponse().id(signedOutUser).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}

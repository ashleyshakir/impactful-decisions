package com.example.impactfuldecisions.controller;

import com.example.impactfuldecisions.models.User;
import com.example.impactfuldecisions.models.request.LoginRequest;
import com.example.impactfuldecisions.models.response.LoginResponse;
import com.example.impactfuldecisions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/auth/users")
public class UserController {

    private UserService userService;
    Logger logger = Logger.getLogger(UserController.class.getName());
    static HashMap<String, Object> message = new HashMap<>();

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/hello/")
    public ResponseEntity<?> getHello() {
        message.put("message", "Hello");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping(path = "/register/")
    public ResponseEntity<?> registerUser(@RequestBody User userObject){
        User newUser = userService.registerUser(userObject);
        if (newUser != null) {
            message.put("message", "success");
            message.put("data", newUser);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } else {
            message.put("message", "email already exists");
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }
    @PostMapping(path = "/login/")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        Optional<String> jwtToken = userService.loginUser(loginRequest);
        User loggedInUser = userService.findByUserEmailAddress(loginRequest.getEmailAddress());
        if(jwtToken.isPresent()){
            logger.info("Authentication is good for user " + loginRequest.getEmailAddress());
            return ResponseEntity.ok(new LoginResponse(jwtToken.get(),loggedInUser));
        }
        else{
            logger.warning("Authentication failed for user " + loginRequest.getEmailAddress());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Authentication failed",null));
        }
    }

}

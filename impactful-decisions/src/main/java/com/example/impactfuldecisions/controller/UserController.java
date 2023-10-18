package com.example.impactfuldecisions.controller;

import com.example.impactfuldecisions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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


}

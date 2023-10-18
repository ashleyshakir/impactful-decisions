package com.example.impactfuldecisions.controller;

import com.example.impactfuldecisions.service.DecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping(path = "/api")
public class DecisionController {

    private final DecisionService decisionService;
    static HashMap<String, Object> message = new HashMap<>();

    @Autowired
    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }
}

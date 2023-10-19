package com.example.impactfuldecisions.controller;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.service.DecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping(path = "/decisions/")
    public ResponseEntity<?> createDecision(@RequestBody Decision decisionObject) {
        try {
            Decision decision = decisionService.createDecision(decisionObject);
            message.put("message", "success, decision created");
            message.put("data", decision);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (DecisionExistsException e) {
            message.put("message", e.getMessage());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }
}

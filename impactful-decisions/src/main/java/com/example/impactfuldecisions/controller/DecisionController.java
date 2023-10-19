package com.example.impactfuldecisions.controller;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.models.Criteria;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.service.DecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

    @GetMapping(path = "/decisions/{decisionId}/")
    public ResponseEntity<?> getDecision(@PathVariable(value = "decisionId") Long decisionId){
        Optional<Decision> decision = Optional.of(decisionService.getDecision(decisionId));
        if(decision.isPresent()){
            message.put("message","success, decision was retrieved");
            message.put("data",decision.get());
            return new ResponseEntity<>(message,HttpStatus.OK);
        } else {
            message.put("message","cannot find decision");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/decisions/")
    public ResponseEntity<?> getUserDecisions(){
        List<Decision> decisionList = decisionService.getUserDecisions();
        if(decisionList.isEmpty()){
            message.put("message", "There are no decisions to be made :)");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "Able to retrieve decisions");
            message.put("data", decisionList);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }
    @PutMapping(path = "/decisions/{decisionId}/")
    public ResponseEntity<?> updateDecision(@PathVariable(value = "decisionId")Long decisionId, @RequestBody Decision decisionObject){
        Optional<Decision> decision = Optional.ofNullable(decisionService.updateDecision(decisionId, decisionObject));
        if(decision.isPresent()){
            message.put("message", "Decision updated successfully");
            message.put("data",decision.get());
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            message.put("message", "Decision not found");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping(path = "/decisions/{decisionId}/")
    public ResponseEntity<?> deleteDecision(@PathVariable(value = "decisionId")Long decisionId){
        Optional<Decision> decision = decisionService.deleteDecision(decisionId);
        if(decision.isEmpty()){
            message.put("message","Decision not found");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "Decision successfully deleted");
            message.put("data",decision.get());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @PostMapping(path = "decisions/{decisionId}/criteria/")
    public ResponseEntity<?> addCriteria(@PathVariable(value = "decisionId")Long decisionId, @RequestBody Criteria criteriaObject){
        Optional<Criteria> criteria = Optional.ofNullable(decisionService.addCriteria(decisionId, criteriaObject));
        if(criteria.isEmpty()){
            message.put("message", "unable to create criteria.");
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            message.put("message", "success, criteria added to decision");
            message.put("data", criteria);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
    }







}

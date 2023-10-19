package com.example.impactfuldecisions.controller;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.models.Criteria;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.models.Option;
import com.example.impactfuldecisions.models.ProCon;
import com.example.impactfuldecisions.repository.CriteriaRepository;
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
    private final CriteriaRepository criteriaRepository;

    @Autowired
    public DecisionController(DecisionService decisionService, CriteriaRepository criteriaRepository) {
        this.decisionService = decisionService;
        this.criteriaRepository = criteriaRepository;
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

    @PutMapping(path = "decisions/{decisionId}/criteria/{criteriaId}/")
    public ResponseEntity<?> updateCriteria(@PathVariable(value = "decisionId") Long decisionId, @PathVariable(value = "criteriaId")Long criteriaId, @RequestBody Criteria criteriaObject){
        Optional<Criteria> criteria = Optional.ofNullable(decisionService.updateCriteria(decisionId, criteriaId, criteriaObject));
        if(criteria.isEmpty()){
            message.put("message", "cannot find criteria");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success, criteria updated");
            message.put("data",criteria.get());
            return new ResponseEntity<>(message,HttpStatus.OK);
        }
    }

    @DeleteMapping(path = "decisions/{decisionId}/criteria/{criteriaId}/")
    public ResponseEntity<?> deleteCriteria(@PathVariable(value = "decisionId") Long decisionId, @PathVariable(value = "criteriaId")Long criteriaId){
        Optional<Criteria> criteria = decisionService.deleteCriteria(decisionId, criteriaId);
        if(criteria.isEmpty()){
            message.put("message", "cannot find criteria");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success, criteria deleted");
            message.put("data",criteria.get());
            return new ResponseEntity<>(message,HttpStatus.OK);
        }
    }

    @PostMapping(path = "decisions/{decisionId}/options/")
    public ResponseEntity<?> addOption(@PathVariable(value = "decisionId") Long decisionId, @RequestBody Option optionObject) {
        Optional<Option> option = Optional.ofNullable(decisionService.addOption(decisionId, optionObject));
        if (option.isEmpty()) {
            message.put("message", "unable to create option.");
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            message.put("message", "success, option added to decision");
            message.put("data", option);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
    }

    @PutMapping(path = "decisions/{decisionId}/options/{optionId}/")
    public ResponseEntity<?> updateOption(@PathVariable(value = "decisionId") Long decisionId, @PathVariable(value = "optionId") Long optionId, @RequestBody Option optionObject) {
        Optional<Option> option = Optional.ofNullable(decisionService.updateOption(decisionId, optionId, optionObject));
        if (option.isEmpty()) {
            message.put("message", "cannot find option");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success, option updated");
            message.put("data", option.get());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @DeleteMapping(path = "decisions/{decisionId}/options/{optionId}/")
    public ResponseEntity<?> deleteOption(@PathVariable(value = "decisionId") Long decisionId, @PathVariable(value = "optionId") Long optionId) {
        Optional<Option> option = decisionService.deleteOption(decisionId, optionId);
        if (option.isEmpty()) {
            message.put("message", "cannot find option");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success, option deleted");
            message.put("data", option.get());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @PostMapping(path = "/decisions/{decisionId}/options/{optionId}/procons/")
    public ResponseEntity<?> addProCon (
            @PathVariable(value = "decisionId")Long decisionId,
            @PathVariable(value = "optionId")Long optionId,
            @RequestBody ProCon proConObject,
            @RequestParam("criteriaId") Long criteriaId) {
        Optional<Criteria> criteria = criteriaRepository.findById(criteriaId);
        if(criteria.isPresent()){
            Optional<ProCon> proCon = Optional.ofNullable(decisionService.addProCon(decisionId, optionId, criteria.get(),proConObject));
            if(proCon.isEmpty()){
                message.put("message", "unable to create pro or con");
                return new ResponseEntity<>(message,HttpStatus.OK);
            } else {
                message.put("message", "success, pro or con was added to option");
                message.put("data", proCon);
                return new ResponseEntity<>(message, HttpStatus.CREATED);
            }
        } else {
            message.put("message", "unable to assign pro or con to criteria with id " + criteriaId);
            return new ResponseEntity<>(message,HttpStatus.OK);
        }
    }

    @PutMapping(path = "/decisions/{decisionId}/options/{optionId}/procons/{proconId}")
    public ResponseEntity<?> updateProCon (
            @PathVariable(value = "decisionId")Long decisionId,
            @PathVariable(value = "optionId")Long optionId,
            @PathVariable(value = "proconId")Long proconId,
            @RequestBody ProCon proConObject){
        Optional<ProCon> proCon = Optional.ofNullable(decisionService.updateProCon(decisionId,optionId,proconId,proConObject));
        if (proCon.isEmpty()) {
            message.put("message", "cannot find pro or con");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success, pro or con was updated");
            message.put("data", proCon.get());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }





}

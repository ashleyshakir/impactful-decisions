package com.example.impactfuldecisions.controller;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.models.Criteria;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.models.Option;
import com.example.impactfuldecisions.models.ProCon;
import com.example.impactfuldecisions.models.analysis.RecommendedOption;
import com.example.impactfuldecisions.repository.CriteriaRepository;
import com.example.impactfuldecisions.service.DecisionAnalysisService;
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
    private final DecisionAnalysisService decisionAnalysisService;
    static HashMap<String, Object> message = new HashMap<>();
    private final CriteriaRepository criteriaRepository;

    @Autowired
    public DecisionController(DecisionService decisionService, DecisionAnalysisService decisionAnalysisService, CriteriaRepository criteriaRepository) {
        this.decisionService = decisionService;
        this.decisionAnalysisService = decisionAnalysisService;
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
        Optional<List<Decision>> decisionList = Optional.ofNullable(decisionService.getUserDecisions());
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
    public ResponseEntity<?> addCriteria(@PathVariable(value = "decisionId")Long decisionId, @RequestBody Criteria[] criteriaObjects){
        Optional<List<Criteria>> criteria = Optional.ofNullable(decisionService.addCriteria(decisionId, criteriaObjects));
        if(criteria.isEmpty()){
            message.put("message", "unable to create criteria.");
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            message.put("message", "success, criteria added to decision with id: "+ decisionId);
            message.put("data", criteria.get());
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
    }

    @GetMapping(path="decisions/{decisionId}/criteria/")
    public ResponseEntity<?> getDecisionCriteria(@PathVariable(value = "decisionId")Long decisionId) {
        List<Criteria> criteriaList = decisionService.getDecisionCriteria(decisionId);
        if(criteriaList.isEmpty()){
            message.put("message", "cannot find criteria for decision with id "+ decisionId);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success");
            message.put("data", criteriaList);
            return new ResponseEntity<>(message, HttpStatus.OK);
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
    public ResponseEntity<?> addOptions(@PathVariable(value = "decisionId") Long decisionId, @RequestBody Option[] optionObjects) {
        Optional<List<Option>> options = Optional.ofNullable(decisionService.addOptions(decisionId, optionObjects));
        if (options.isEmpty()) {
            message.put("message", "unable to create option.");
            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            message.put("message", "success, option added to decision with id: "+ decisionId);
            message.put("data", options.get());
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }
    }

    @GetMapping(path="decisions/{decisionId}/options/")
    public ResponseEntity<?> getDecisionOptions(@PathVariable(value = "decisionId")Long decisionId) {
        List<Option> optionList = decisionService.getDecisionOptions(decisionId);
        if(optionList.isEmpty()){
            message.put("message", "cannot find options for decision with id "+ decisionId);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success");
            message.put("data", optionList);
            return new ResponseEntity<>(message, HttpStatus.OK);
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
            @RequestParam("criteriaName") String criteriaName) {
        Optional<Criteria> criteria = Optional.ofNullable(criteriaRepository.findByNameAndDecisionId(criteriaName, decisionId));
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
            message.put("message", "unable to assign pro or con to criteria with name " + criteriaName);
            return new ResponseEntity<>(message,HttpStatus.OK);
        }
    }

    @GetMapping(path = "/decisions/{decisionId}/options/{optionId}/procons/")
    public ResponseEntity<?> getOptionProCons (@PathVariable (value = "decisionId") Long decisionId, @PathVariable (value = "optionId") Long optionId){
        List<ProCon> proConList = decisionService.getOptionProCons(decisionId,optionId);
        if(proConList.isEmpty()){
            message.put("message", "cannot find pros or cons for option with id "+ optionId);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success");
            message.put("data", proConList);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @PutMapping(path = "/decisions/{decisionId}/options/{optionId}/procons/{proconId}")
    public ResponseEntity<?> updateProCon (
            @PathVariable(value = "decisionId")Long decisionId,
            @PathVariable(value = "optionId")Long optionId,
            @PathVariable(value = "proconId")Long proconId,
            @RequestBody ProCon proConObject,
            @RequestParam("criteriaName") String criteriaName){
        Optional<ProCon> proCon = Optional.ofNullable(decisionService.updateProCon(decisionId, optionId, proconId, proConObject, criteriaName));
        if (proCon.isEmpty()) {
            message.put("message", "cannot find pro or con");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success, pro or con was updated");
            message.put("data", proCon.get());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @DeleteMapping(path = "/decisions/{decisionId}/options/{optionId}/procons/{proconId}")
    public ResponseEntity<?> deleteProCon (@PathVariable(value = "decisionId")Long decisionId, @PathVariable(value = "optionId")Long optionId, @PathVariable(value = "proconId")Long proconId) {
        Optional<ProCon> proCon = decisionService.deleteProCon(decisionId,optionId, proconId);
        if (proCon.isEmpty()) {
            message.put("message", "cannot find pro or con");
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            message.put("message", "success, pro or con was deleted");
            message.put("data", proCon.get());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/decisions/{decisionId}/recommendation/")
    public ResponseEntity<RecommendedOption> getDecisionRecommendation(@PathVariable(value = "decisionId") Long decisionId) {
        RecommendedOption recommendation = decisionAnalysisService.calculateAllOptionScores(decisionId);
        return new ResponseEntity<>(recommendation, HttpStatus.OK);
    }





}

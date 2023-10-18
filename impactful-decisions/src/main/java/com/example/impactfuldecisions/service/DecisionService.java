package com.example.impactfuldecisions.service;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.exceptions.InformationNotFoundException;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.repository.DecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DecisionService {

    private DecisionRepository decisionRepository;

    @Autowired
    public void setDecisionRepository(DecisionRepository decisionRepository) {
        this.decisionRepository = decisionRepository;
    }

    // Testing the business logic for creating a decision
    public Decision createDecision(Decision decisionObject) {
        if (decisionRepository.findByTitle(decisionObject.getTitle()) != null) {
            throw new DecisionExistsException("You have already created a decision with the title: " + decisionObject.getTitle());
        } else {
            return decisionRepository.save(decisionObject);
        }
    }

    // Testing the business logic for viewing a decision
    public Decision getDecision(Long decisionId) {
        Optional<Decision> decision = Optional.ofNullable(decisionRepository.findByIdAndUserId(decisionId, 1L));
        if (decision.isPresent()) {
            return decision.get();
        } else {
            throw new InformationNotFoundException("A decision with the id of " + decisionId + " does not exist.");
        }
    }



}

package com.example.impactfuldecisions.service;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.repository.DecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}

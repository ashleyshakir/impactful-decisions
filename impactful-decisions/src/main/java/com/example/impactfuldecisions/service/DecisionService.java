package com.example.impactfuldecisions.service;

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

}

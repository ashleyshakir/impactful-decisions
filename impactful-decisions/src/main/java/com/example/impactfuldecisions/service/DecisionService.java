package com.example.impactfuldecisions.service;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.exceptions.InformationNotFoundException;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.models.User;
import com.example.impactfuldecisions.repository.DecisionRepository;
import com.example.impactfuldecisions.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DecisionService {

    private DecisionRepository decisionRepository;

    @Autowired
    public void setDecisionRepository(DecisionRepository decisionRepository) {
        this.decisionRepository = decisionRepository;
    }
    /**
     * Get the currently logged-in user
     *
     * @return The current user
     */
    public static User getCurrentLoggedInUser() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    // Testing the business logic for creating a decision
    public Decision createDecision(Decision decisionObject) {
        if (decisionRepository.findByTitle(decisionObject.getTitle()) != null) {
            throw new DecisionExistsException("You have already created a decision with the title: " + decisionObject.getTitle());
        } else {
            decisionObject.setUser(DecisionService.getCurrentLoggedInUser());
            return decisionRepository.save(decisionObject);
        }
    }

    // Testing the business logic for viewing a decision
    public Decision getDecision(Long decisionId) {
        Optional<Decision> decision = Optional.ofNullable(decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId()));
        if (decision.isPresent()) {
            return decision.get();
        } else {
            throw new InformationNotFoundException("A decision with the id of " + decisionId + " does not exist.");
        }
    }

    // Testing the business logic for viewing a list of decisions
    public List<Decision> getUserDecisions() {
        List<Decision> decisionList = decisionRepository.findByUserId(DecisionService.getCurrentLoggedInUser().getId());
        if (decisionList.isEmpty()) {
            throw new InformationNotFoundException("You have no decisions!");
        }
        return decisionList;
    }

    // Testing the business logic for updating a decision - may change to just updating a title and description and make a separate one for updating isResolved
    public Decision updateDecision(Long decisionId, Decision decisionObject) {
        Optional<Decision> decision = Optional.ofNullable(decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId()));
        if (decision.isEmpty()) {
            throw new InformationNotFoundException("You cannot update a decision that does not exist!");
        }
        Decision existingDecision = decision.get();
        existingDecision.setResolved(decisionObject.isResolved());
        existingDecision.setTitle(decisionObject.getTitle());
        existingDecision.setDescription(decisionObject.getDescription());
        return decisionRepository.save(existingDecision);
    }

    // Testing the business logic for deleting a decision
    public Optional<Decision> deleteDecision(Long decisionId) {
        Optional<Decision> decision = Optional.ofNullable(decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId()));
        if (decision.isEmpty()) {
            throw new InformationNotFoundException("You cannot delete a decision that does not exist!");
        }
        decisionRepository.deleteById(decisionId);
        return decision;
    }



}

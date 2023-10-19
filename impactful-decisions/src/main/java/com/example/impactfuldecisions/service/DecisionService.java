package com.example.impactfuldecisions.service;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.exceptions.InformationNotFoundException;
import com.example.impactfuldecisions.models.Criteria;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.models.Option;
import com.example.impactfuldecisions.models.User;
import com.example.impactfuldecisions.repository.CriteriaRepository;
import com.example.impactfuldecisions.repository.DecisionRepository;
import com.example.impactfuldecisions.repository.OptionRepository;
import com.example.impactfuldecisions.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DecisionService {

    private DecisionRepository decisionRepository;
    private CriteriaRepository criteriaRepository;
    private OptionRepository optionRepository;

    @Autowired
    public void setDecisionRepository(DecisionRepository decisionRepository,
                                      CriteriaRepository criteriaRepository,
                                      OptionRepository optionRepository) {
        this.decisionRepository = decisionRepository;
        this.criteriaRepository = criteriaRepository;
        this.optionRepository = optionRepository;
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

    // Testing the business logic for adding decision criteria
    public Criteria addCriteria(Long decisionId, Criteria criteriaObject) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        }
        criteriaObject.setDecision(decision);
        return criteriaRepository.save(criteriaObject);
    }

    // Testing the business logic for updating decision criteria
    public Criteria updateCriteria(Long decisionId, Long criteriaId, Criteria criteriaObject) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        } else {
            Optional<Criteria> criteriaOptional = criteriaRepository.findByDecisionId(decisionId)
                    .stream().filter(criteria -> criteria.getId().equals(criteriaId)).findFirst();
            if (criteriaOptional.isEmpty()) {
                throw new InformationNotFoundException("criteria does not exist");
            } else {
                Criteria existingCriteria = criteriaOptional.get();
                existingCriteria.setName(criteriaObject.getName());
                existingCriteria.setWeight(criteriaObject.getWeight());
                return criteriaRepository.save(existingCriteria);
            }
        }
    }

    // Testing the business logic for deleting decision criteria
    public Optional<Criteria> deleteCriteria(Long decisionId, Long criteriaId) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        } else {
            Optional<Criteria> criteriaOptional = criteriaRepository.findByDecisionId(decisionId)
                    .stream().filter(criteria -> criteria.getId().equals(criteriaId)).findFirst();
            if (criteriaOptional.isEmpty()) {
                throw new InformationNotFoundException("criteria does not exist");
            } else {
                criteriaRepository.deleteById(criteriaId);
                return criteriaOptional;
            }
        }
    }

    // Testing the business logic for adding decision options
    public Option addOption(Long decisionId, Option optionObject) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        }
        optionObject.setDecision(decision);
        return optionRepository.save(optionObject);
    }

    // Testing the business logic for updating decision Option
    public Option updateOption(Long decisionId, Long optionId, Option optionObject) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, 1L);
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        } else {
            Optional<Option> optionOptional = optionRepository.findByDecisionId(decisionId)
                    .stream().filter(option -> option.getId().equals(optionId)).findFirst();
            if (optionOptional.isEmpty()) {
                throw new InformationNotFoundException("option does not exist");
            } else {
                Option existingOption = optionOptional.get();
                existingOption.setName(optionObject.getName());
                return optionRepository.save(existingOption);
            }
        }
    }

    // Testing the business logic for deleting decision option
    public Optional<Option> deleteOption(Long decisionId, Long optionId) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, 1L);
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        } else {
            Optional<Option> optionOptional = optionRepository.findByDecisionId(decisionId)
                    .stream().filter(option -> option.getId().equals(optionId)).findFirst();
            if (optionOptional.isEmpty()) {
                throw new InformationNotFoundException("Option does not exist");
            } else {
                optionRepository.deleteById(optionId);
                return optionOptional;
            }
        }
    }


}

package com.example.impactfuldecisions.service;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.exceptions.InformationNotFoundException;
import com.example.impactfuldecisions.models.*;
import com.example.impactfuldecisions.repository.CriteriaRepository;
import com.example.impactfuldecisions.repository.DecisionRepository;
import com.example.impactfuldecisions.repository.OptionRepository;
import com.example.impactfuldecisions.repository.ProConRepository;
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
    private ProConRepository proConRepository;

    @Autowired
    public void setDecisionRepository(DecisionRepository decisionRepository,
                                      CriteriaRepository criteriaRepository,
                                      OptionRepository optionRepository,
                                      ProConRepository proConRepository) {
        this.decisionRepository = decisionRepository;
        this.criteriaRepository = criteriaRepository;
        this.optionRepository = optionRepository;
        this.proConRepository = proConRepository;
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

    /**
     * Creates a new Decision based on the provided Decision object.
     *
     * @param decisionObject The Decision object containing the details of the decision to be created.
     * @return The created Decision object after it's saved in the repository.
     * @throws DecisionExistsException If a decision with the same title already exists.
     */
    public Decision createDecision(Decision decisionObject) {
        if (decisionRepository.findByTitle(decisionObject.getTitle()) != null) {
            throw new DecisionExistsException("You have already created a decision with the title: " + decisionObject.getTitle());
        } else {
            decisionObject.setUser(DecisionService.getCurrentLoggedInUser());
            return decisionRepository.save(decisionObject);
        }
    }

    /**
     * Retrieves a Decision based on the provided decisionId.
     *
     * @param decisionId The unique identifier for the Decision to be retrieved.
     * @return The Decision object if found.
     * @throws InformationNotFoundException If no decision with the given id exists for the current logged-in user.
     */
    public Decision getDecision(Long decisionId) {
        Optional<Decision> decision = Optional.ofNullable(decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId()));
        if (decision.isPresent()) {
            return decision.get();
        } else {
            throw new InformationNotFoundException("A decision with the id of " + decisionId + " does not exist.");
        }
    }

    /**
     * Retrieves a list of Decisions associated with the current logged-in user.
     *
     * @return A list of Decision objects related to the current user.
     * @throws InformationNotFoundException If the user has no associated decisions.
     */
    public List<Decision> getUserDecisions() {
        List<Decision> decisionList = decisionRepository.findByUserId(DecisionService.getCurrentLoggedInUser().getId());
        if (decisionList.isEmpty()) {
            throw new InformationNotFoundException("You have no decisions!");
        }
        return decisionList;
    }

    /**
     * Updates an existing Decision based on the provided decisionId and Decision object.
     *
     * This method may change the title, description, and isResolved status of the Decision.
     *
     * @param decisionId The unique identifier for the Decision to be updated.
     * @param decisionObject The Decision object containing the updated details.
     * @return The updated Decision object after it's saved in the repository.
     * @throws InformationNotFoundException If no decision with the given id exists for the current logged-in user.
     */
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

    /**
     * Deletes an existing Decision based on the provided decisionId.
     *
     * @param decisionId The unique identifier for the Decision to be deleted.
     * @return An Optional containing the deleted Decision object, if it was successfully deleted.
     * @throws InformationNotFoundException If no decision with the given id exists for the current logged-in user.
     */
    public Optional<Decision> deleteDecision(Long decisionId) {
        Optional<Decision> decision = Optional.ofNullable(decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId()));
        if (decision.isEmpty()) {
            throw new InformationNotFoundException("You cannot delete a decision that does not exist!");
        }
        decisionRepository.deleteById(decisionId);
        return decision;
    }

    /**
     * Adds criteria to an existing Decision based on the provided decisionId and Criteria object.
     *
     * @param decisionId The unique identifier for the Decision to which the criteria will be added.
     * @param criteriaObject The Criteria object containing the details of the criteria to be added.
     * @return The added Criteria object after it's saved in the repository.
     * @throws InformationNotFoundException If no decision with the given id exists for the current logged-in user.
     */
    public Criteria addCriteria(Long decisionId, Criteria criteriaObject) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        }
        criteriaObject.setDecision(decision);
        return criteriaRepository.save(criteriaObject);
    }

    /**
     * Updates an existing Criteria based on the provided decisionId, criteriaId, and Criteria object.
     *
     * @param decisionId The unique identifier for the Decision associated with the criteria to be updated.
     * @param criteriaId The unique identifier for the Criteria to be updated.
     * @param criteriaObject The Criteria object containing the updated details.
     * @return The updated Criteria object after it's saved in the repository.
     * @throws InformationNotFoundException If no decision with the given decisionId exists for the current logged-in user,
     * or if no criteria with the given criteriaId exists.
     */
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

    /**
     * Deletes an existing Criteria based on the provided decisionId and criteriaId.
     *
     * @param decisionId The unique identifier for the Decision associated with the criteria to be deleted.
     * @param criteriaId The unique identifier for the Criteria to be deleted.
     * @return An Optional containing the deleted Criteria object, if it was successfully deleted.
     * @throws InformationNotFoundException If no decision with the given decisionId exists for the current logged-in user,
     * or if no criteria with the given criteriaId exists.
     */
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

    /**
     * Adds an option to an existing Decision based on the provided decisionId and Option object.
     *
     * @param decisionId The unique identifier for the Decision to which the option will be added.
     * @param optionObject The Option object containing the details of the option to be added.
     * @return The added Option object after it's saved in the repository.
     * @throws InformationNotFoundException If no decision with the given id exists for the current logged-in user.
     */
    public Option addOption(Long decisionId, Option optionObject) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        }
        optionObject.setDecision(decision);
        return optionRepository.save(optionObject);
    }

    /**
     * Updates an existing Option based on the provided decisionId, optionId, and Option object.
     *
     * @param decisionId The unique identifier for the Decision associated with the option to be updated.
     * @param optionId The unique identifier for the Option to be updated.
     * @param optionObject The Option object containing the updated details.
     * @return The updated Option object after it's saved in the repository.
     * @throws InformationNotFoundException If no decision with the given decisionId exists for the current logged-in user,
     * or if no option with the given optionId exists.
     */
    public Option updateOption(Long decisionId, Long optionId, Option optionObject) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
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

    /**
     * Deletes an existing Option based on the provided decisionId and optionId.
     *
     * @param decisionId The unique identifier for the Decision associated with the option to be deleted.
     * @param optionId The unique identifier for the Option to be deleted.
     * @return An Optional containing the deleted Option object, if it was successfully deleted.
     * @throws InformationNotFoundException If no decision with the given decisionId exists for the current logged-in user,
     * or if no option with the given optionId exists.
     */
    public Optional<Option> deleteOption(Long decisionId, Long optionId) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
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

    // Testing the business logic for adding option pros and cons
    public ProCon addProCon(Long decisionId, Long optionId, Criteria criteria, ProCon proConObject) {
        Option option = optionRepository.findByIdAndDecisionId(optionId, decisionId);
        if (option == null) {
            throw new InformationNotFoundException("Option not found");
        }
        proConObject.setOption(option);
        proConObject.setCriteria(criteria);
        return proConRepository.save(proConObject);
    }

    // Testing the business logic for updating decision Option
    public ProCon updateProCon(Long decisionId, Long optionId, Long proConId, ProCon proConObject) {
        Option option = optionRepository.findByIdAndDecisionId(optionId, decisionId);
        if (option == null) {
            throw new InformationNotFoundException("Option not found");
        } else {
            Optional<ProCon> proConOptional = proConRepository.findByOptionId(optionId)
                    .stream().filter(proCon -> proCon.getId().equals(proConId)).findFirst();
            if (proConOptional.isEmpty()) {
                throw new InformationNotFoundException("A Pro or Con for that option does not exist");
            } else {
                ProCon existingProCon = proConOptional.get();
                existingProCon.setType(proConObject.getType());
                existingProCon.setRating(proConObject.getRating());
                existingProCon.setDescription(proConObject.getDescription());
                return proConRepository.save(existingProCon);
            }
        }
    }


}

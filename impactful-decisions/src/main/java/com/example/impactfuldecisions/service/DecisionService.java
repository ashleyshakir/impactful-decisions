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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        if (decisionRepository.findByTitleAndUserId(decisionObject.getTitle(), DecisionService.getCurrentLoggedInUser().getId()) != null) {
            throw new DecisionExistsException("You have already created a decision with the title: " + decisionObject.getTitle());
        } else {
            decisionObject.setCreationDate(LocalDateTime.now());
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
     * Adds criteria to an existing Decision based on the provided decisionId and array of Criteria objects.
     *
     * @param decisionId The unique identifier for the Decision to which the criteria will be added.
     * @param criteriaObjects The Criteria objects containing the details of each criterion to be added.
     * @return The added Criteria objects after they are saved in the repository.
     * @throws InformationNotFoundException If no decision with the given id exists for the current logged-in user.
     */
    public List<Criteria> addCriteria(Long decisionId, Criteria[] criteriaObjects) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        }
        List<Criteria> savedCriteria = new ArrayList<>();
        for(Criteria criteria : criteriaObjects){
            criteria.setDecision(decision);
            Criteria savedCriterion = criteriaRepository.save(criteria);
            savedCriteria.add(savedCriterion);
        }
        return savedCriteria;
    }

    /**
     * Retrieves a list of criteria belonging to the specified decision ID for the current logged-in user.
     *
     * @param decisionId The unique identifier of the decision.
     * @return A list of criterion objects associated with the specified decision ID.
     * @throws InformationNotFoundException If the decision with the given ID doesn't exist.
     */
    public List<Criteria> getDecisionCriteria(Long decisionId) {
        Optional<Decision> optionalDecision = Optional.ofNullable(decisionRepository.findByIdAndUserId(decisionId, getCurrentLoggedInUser().getId()));
        if(optionalDecision.isPresent()){
            return optionalDecision.get().getCriteriaList();
        } else {
            throw new InformationNotFoundException("Decision with id: " + decisionId + " not found.");
        }
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
     * Adds an array of options to an existing Decision based on the provided decisionId and Option objects.
     *
     * @param decisionId The unique identifier for the Decision to which the option will be added.
     * @param optionObjects The Option object array contains the details of the options to be added.
     * @return The added Option object after it's saved in the repository.
     * @throws InformationNotFoundException If no decision with the given id exists for the current logged-in user.
     */
    public List<Option> addOptions(Long decisionId, Option[] optionObjects) {
        Decision decision = decisionRepository.findByIdAndUserId(decisionId, DecisionService.getCurrentLoggedInUser().getId());
        if (decision == null) {
            throw new InformationNotFoundException("Decision not found");
        }
        List<Option> savedOptions = new ArrayList<>();
        for(Option option : optionObjects){
            option.setDecision(decision);
            Option savedOption = optionRepository.save(option);
            savedOptions.add(savedOption);
        }
        return savedOptions;
    }

    /**
     * Retrieves a list of options belonging to the specified decision ID for the current logged-in user.
     *
     * @param decisionId The unique identifier of the decision.
     * @return A list of option objects associated with the specified decision ID.
     * @throws InformationNotFoundException If the decision with the given ID doesn't exist.
     */
    public List<Option> getDecisionOptions(Long decisionId) {
        Optional<Decision> optionalDecision = Optional.ofNullable(decisionRepository.findByIdAndUserId(decisionId, getCurrentLoggedInUser().getId()));
        if(optionalDecision.isPresent()){
            return optionalDecision.get().getOptionList();
        } else {
            throw new InformationNotFoundException("Decision with id: " + decisionId + " not found.");
        }
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

    /**
     * Adds a ProCon to an existing Option based on the provided decisionId, optionId, Criteria object, and ProCon object.
     *
     * @param decisionId The unique identifier for the Decision associated with the Option.
     * @param optionId The unique identifier for the Option to which the ProCon will be added.
     * @param criteria The Criteria object to which the ProCon is related.
     * @param proConObject The ProCon object containing the details of the pro or con to be added.
     * @return The added ProCon object after it's saved in the repository.
     * @throws InformationNotFoundException If no Option with the given decisionId and optionId exists.
     */
    public ProCon addProCon(Long decisionId, Long optionId, Criteria criteria, ProCon proConObject) {
        Option option = optionRepository.findByIdAndDecisionId(optionId, decisionId);
        if (option == null) {
            throw new InformationNotFoundException("Option not found");
        }
        proConObject.setOption(option);
        proConObject.setCriteria(criteria);
        return proConRepository.save(proConObject);
    }

    /**
     * Updates an existing ProCon object based on the provided decisionId, optionId, proConId, and ProCon object.
     *
     * @param decisionId The unique identifier for the Decision associated with the Option.
     * @param optionId The unique identifier for the Option to which the ProCon is related.
     * @param proConId The unique identifier for the ProCon to be updated.
     * @param proConObject The ProCon object containing the updated details for the pro or con.
     * @return The updated ProCon object after it's saved in the repository.
     * @throws InformationNotFoundException If no Option with the given decisionId and optionId exists, or if no ProCon with the given proConId exists.
     */
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

    /**
     * Deletes a ProCon object based on the provided decisionId, optionId, and proConId.
     *
     * @param decisionId The unique identifier for the Decision associated with the Option.
     * @param optionId The unique identifier for the Option to which the ProCon is related.
     * @param proConId The unique identifier for the ProCon to be deleted.
     * @return An Optional containing the deleted ProCon object, or an empty Optional if the ProCon was not found.
     * @throws InformationNotFoundException If no Option with the given decisionId and optionId exists, or if no ProCon with the given proConId exists.
     */
    public Optional<ProCon> deleteProCon(Long decisionId, Long optionId, Long proConId) {
        Option option = optionRepository.findByIdAndDecisionId(optionId, decisionId);
        if (option == null) {
            throw new InformationNotFoundException("Option not found");
        } else {
            Optional<ProCon> proConOptional = proConRepository.findByOptionId(optionId)
                    .stream().filter(proCon -> proCon.getId().equals(proConId)).findFirst();
            if (proConOptional.isEmpty()) {
                throw new InformationNotFoundException("A Pro or Con for that option does not exist");
            } else {
                proConRepository.deleteById(proConId);
                return proConOptional;
            }
        }
    }
}

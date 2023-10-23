package com.example.impactfuldecisions.service;

import com.example.impactfuldecisions.models.Criteria;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.models.Option;
import com.example.impactfuldecisions.models.ProCon;
import com.example.impactfuldecisions.models.analysis.RecommendedOption;
import com.example.impactfuldecisions.repository.DecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DecisionAnalysisService {

    private final DecisionRepository decisionRepository;

    @Autowired
    public DecisionAnalysisService(DecisionRepository decisionRepository) {
        this.decisionRepository = decisionRepository;
    }

    /**
     * Calculates the overall score of a specific Option based on its associated pros and cons.
     * Each pro and con has a rating and is tied to a Criteria object that has a weight.
     * The overall score is calculated as the sum of weighted pro ratings minus the sum of weighted con ratings.
     *
     * @param decisionId The unique identifier for the Decision that contains the Option.
     * @param optionId The unique identifier for the Option whose score needs to be calculated.
     * @return The calculated overall score for the specified Option.
     */
    public double calculateOptionScore(Long decisionId, Long optionId){
        Decision decision = decisionRepository.findById(decisionId).get();
        Option optionObject = decision.getOptionList().stream().filter(option -> option.getId().equals(optionId)).findFirst().get();
        List<ProCon> pros = optionObject.getProConList().stream().filter(pro ->"pro".equals(pro.getType()))
                .collect(Collectors.toList());
        List<ProCon> cons = optionObject.getProConList().stream().filter(pro ->"con".equals(pro.getType()))
                .collect(Collectors.toList());

        double totalProsScore = 0.0;
        double totalConsScore = 0.0;
        double overallOptionScore = 0.0;

        for (ProCon pro : pros){
            // Retrieve the associated criteria and its weight
            Criteria criteria = pro.getCriteria();
            double criteriaWeight = criteria.getWeight() / 100;

            // retrieve the rating of the pro
            double proRating = pro.getRating();

            // Calculate the weighted score for this pro
            double proScore = criteriaWeight * proRating;

            // Accumulate the score
            totalProsScore += proScore;
        }
        for(ProCon con : cons){
            // Retrieve the associated criteria and its weight
            Criteria criteria = con.getCriteria();
            double criteriaWeight = criteria.getWeight() / 100;

            // retrieve the rating of the con
            double conRating = con.getRating();

            // Calculate the weighted score for this con
            double conScore = criteriaWeight * conRating;

            // Accumulate the score
            totalConsScore += conScore;
        }
        overallOptionScore = totalProsScore - totalConsScore;

        return overallOptionScore;
    }

    /**
     * Calculates the scores for all Options associated with a specific Decision.
     * After calculating each Option's score, this method determines the Option with the highest score
     * and returns it along with the scores of all Options.
     *
     * @param decisionId The unique identifier for the Decision whose Options' scores need to be calculated.
     * @return An object of type RecommendedOption that contains the Option with the highest score and a map of all Option scores.
     */
    public RecommendedOption calculateAllOptionScores(Long decisionId) {
        Decision decision = decisionRepository.findById(decisionId).get();
        Map<Long, Double> optionScores = new HashMap<>();
        Option recommendedOption = null;
        double highestScore = Double.NEGATIVE_INFINITY;
        for (Option option : decision.getOptionList()) {
            double optionScore = calculateOptionScore(decisionId, option.getId());
            optionScores.put(option.getId(), optionScore);

            if (optionScore > highestScore) {
                highestScore = optionScore;
                recommendedOption = option;
            }
        }
        return new RecommendedOption(recommendedOption, optionScores);
    }
}

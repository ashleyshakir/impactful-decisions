package com.example.impactfuldecisions.service;

import com.example.impactfuldecisions.models.Criteria;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.models.Option;
import com.example.impactfuldecisions.models.ProCon;
import com.example.impactfuldecisions.repository.DecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DecisionAnalysisService {

    private final DecisionRepository decisionRepository;

    @Autowired
    public DecisionAnalysisService(DecisionRepository decisionRepository) {
        this.decisionRepository = decisionRepository;
    }

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
            double criteriaWeight = criteria.getWeight();

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
            double criteriaWeight = criteria.getWeight();

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
}

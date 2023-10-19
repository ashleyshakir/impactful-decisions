package com.example.impactfuldecisions.models.analysis;

import com.example.impactfuldecisions.models.Option;

import java.util.Map;

/**
 * Represents a recommendation result containing the highest scoring Option and the scores for all Options.
 * This class acts as a container for analysis results related to Decision-making.
 *
 */
public class RecommendedOption {
    private Option recommendedOption;
    private Map<Long, Double> optionScores;

    public RecommendedOption(Option recommendedOption, Map<Long, Double> optionScores) {
        this.recommendedOption = recommendedOption;
        this.optionScores = optionScores;
    }

    public Option getRecommendedOption() {
        return recommendedOption;
    }

    public void setRecommendedOption(Option recommendedOption) {
        this.recommendedOption = recommendedOption;
    }

    public Map<Long, Double> getOptionScores() {
        return optionScores;
    }

    public void setOptionScores(Map<Long, Double> optionScores) {
        this.optionScores = optionScores;
    }
}

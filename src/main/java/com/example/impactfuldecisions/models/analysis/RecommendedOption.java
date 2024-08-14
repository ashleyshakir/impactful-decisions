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
    private Map<String, Double> optionScores;

    public RecommendedOption(Option recommendedOption, Map<String, Double> optionScores) {
        this.recommendedOption = recommendedOption;
        this.optionScores = optionScores;
    }

    public Option getRecommendedOption() {
        return recommendedOption;
    }

    public void setRecommendedOption(Option recommendedOption) {
        this.recommendedOption = recommendedOption;
    }

    public Map<String, Double> getOptionScores() {
        return optionScores;
    }

    public void setOptionScores(Map<String, Double> optionScores) {
        this.optionScores = optionScores;
    }
}

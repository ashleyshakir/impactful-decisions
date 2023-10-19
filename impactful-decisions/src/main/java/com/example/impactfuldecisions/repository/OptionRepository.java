package com.example.impactfuldecisions.repository;

import com.example.impactfuldecisions.models.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    Option findByName (String name);
    List<Option> findByDecisionId(Long decisionId);
    Option findByIdAndDecisionId(Long optionId, Long decisionId);


}

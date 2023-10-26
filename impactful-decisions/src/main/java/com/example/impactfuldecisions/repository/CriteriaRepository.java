package com.example.impactfuldecisions.repository;

import com.example.impactfuldecisions.models.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
    List<Criteria> findByDecisionId(Long decisionId);
    Criteria findByNameAndDecisionId(String name, Long decisionId);
}

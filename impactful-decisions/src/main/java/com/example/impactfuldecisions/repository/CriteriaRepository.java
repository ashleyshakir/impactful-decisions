package com.example.impactfuldecisions.repository;

import com.example.impactfuldecisions.models.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
    Criteria findByName (String name);
    List<Criteria> findByDecisionId(Long decisionId);
}

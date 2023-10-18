package com.example.impactfuldecisions.repository;

import com.example.impactfuldecisions.models.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DecisionRepository extends JpaRepository<Decision,Long> {
    Decision findByTitle(String title);
    Decision findByIdAndUserId(Long decisionId, Long userId);
    List<Decision> findByUserId(Long userId);
}

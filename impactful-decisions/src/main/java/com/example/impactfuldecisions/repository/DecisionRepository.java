package com.example.impactfuldecisions.repository;

import com.example.impactfuldecisions.models.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionRepository extends JpaRepository<Decision,Long> {

}

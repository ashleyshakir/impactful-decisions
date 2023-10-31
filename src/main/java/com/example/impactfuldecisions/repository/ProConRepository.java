package com.example.impactfuldecisions.repository;

import com.example.impactfuldecisions.models.ProCon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProConRepository extends JpaRepository<ProCon, Long> {
    List<ProCon> findByOptionId(Long optionId);
}

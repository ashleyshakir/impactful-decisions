package com.example.impactfuldecisions.repository;

import com.example.impactfuldecisions.models.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
    Option findByName (String name);

}
package com.padshift.sonic.repository;

import com.padshift.sonic.entities.PersonalityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Regil on 28/11/2018.
 */
@Repository("personalitycriteriaRepository")
@Transactional
public interface PersonalityCriteriaRepository extends JpaRepository<PersonalityCriteria, Long> {
    PersonalityCriteria findByPersonalitycriteriaId(int i);
}

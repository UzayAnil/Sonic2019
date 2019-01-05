package com.padshift.sonic.repository;

import com.padshift.sonic.entities.AgeCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Created by Regil on 14/11/2018.
 */

@Repository("agecriteriaRepository")
@Transactional
public interface AgeCriteriaRepository extends JpaRepository<AgeCriteria, Long> {
    ArrayList<AgeCriteria> findAll();
    AgeCriteria findByAgecriteriaId(int agegroup);
}

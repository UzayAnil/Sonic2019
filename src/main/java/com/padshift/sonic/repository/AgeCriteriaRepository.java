package com.padshift.sonic.repository;

import com.padshift.sonic.entities.AgeCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT SUM(alternativeMusic+countryMusic+hiphopMusic+houseMusic+popMusic+reggaeMusic+religiousMusic+rnbMusic+rockMusic) FROM AgeCriteria WHERE agecriteriaId = :agegroup")
    Float sumOfgenrebyAgegroup(@Param("agegroup") int agegroup);

    @Query("SELECT SUM(alternativeMusic) FROM AgeCriteria")
    Float alternativeAgecount();

    @Query("SELECT SUM(countryMusic) FROM AgeCriteria")
    Float countryAgecount();

    @Query("SELECT SUM(hiphopMusic) FROM AgeCriteria")
    Float hiphopAgecount();

    @Query("SELECT SUM(houseMusic) FROM AgeCriteria")
    Float houseAgecount();

    @Query("SELECT SUM(popMusic) FROM AgeCriteria")
    Float popAgecount();

    @Query("SELECT SUM(reggaeMusic) FROM AgeCriteria")
    Float reggaeAgecount();

    @Query("SELECT SUM(religiousMusic) FROM AgeCriteria")
    Float religiousAgecount();

    @Query("SELECT SUM(rnbMusic) FROM AgeCriteria")
    Float rnbAgecount();

    @Query("SELECT SUM(rockMusic) FROM AgeCriteria")
    Float rockAgecount();

    @Query("SELECT SUM(alternativeMusic+countryMusic+hiphopMusic+houseMusic+popMusic+reggaeMusic+religiousMusic+rnbMusic+rockMusic) FROM AgeCriteria")
    Float AllViews();
}

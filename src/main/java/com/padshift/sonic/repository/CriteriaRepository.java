package com.padshift.sonic.repository;

import com.padshift.sonic.entities.Criteria;
import com.padshift.sonic.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ruzieljonm on 01/10/2018.
 */

@Repository("criteriaRepository")
@Transactional
public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
    void deleteByCriteriaId(int deletethis);

    Criteria findByCriteriaName(String userinput);


    Criteria findByCriteriaId(int editthis);
}

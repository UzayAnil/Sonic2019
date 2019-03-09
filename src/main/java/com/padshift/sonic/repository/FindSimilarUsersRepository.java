package com.padshift.sonic.repository;

import com.padshift.sonic.entities.FindSimilarUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Created by Regil on 09/03/2019.
 */
@Repository("findsimilarusersRepository")
@Transactional
public interface FindSimilarUsersRepository extends JpaRepository<FindSimilarUsers, Long>{
    @Query("DELETE FROM FindSimilarUsers")
    void deleteFindsimilarTable();

    @Query("SELECT f FROM FindSimilarUsers f WHERE f.userId <> :currentuser")
    ArrayList<FindSimilarUsers> findotherusers(@Param("currentuser") int currentuser);

    @Query(value = "SELECT * FROM FindSimilarUsers WHERE user_id != :currentuser ORDER BY distance ASC LIMIT 2", nativeQuery = true)
    ArrayList<FindSimilarUsers> similarusers(@Param("currentuser") int currentuser);

    FindSimilarUsers findByUserId(int currentuserId);
}

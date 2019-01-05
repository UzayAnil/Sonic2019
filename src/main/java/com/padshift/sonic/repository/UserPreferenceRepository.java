package com.padshift.sonic.repository;

import com.padshift.sonic.entities.UserPreference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Created by ruzieljonm on 19/07/2018.
 */
@Repository("userPreferenceRepository")
public interface UserPreferenceRepository extends JpaRepository<UserPreference,Long> {


    UserPreference findByUserId(int userId);

    ArrayList<UserPreference> findAllByUserId(int userId);

    UserPreference findByUserIdAndGenreId(int userId, int i);

    @Query("select distinct genreId from UserPreference")
    ArrayList<Integer> findDistinctGenre();

    UserPreference findByGenreId(int s);

    @Query("select distinct userId from UserPreference where userId = :currentuserid")
    int findUserIdByUserId(@Param("currentuserid") int currentuserId);

    @Query("select distinct userId from UserPreference where userId <> :currentuserid")
    ArrayList<Integer> findDistinctUserfromUserPref(@Param("currentuserid") int currentuserId);

    UserPreference findByGenreNameAndUserId(String genre, int s);
}

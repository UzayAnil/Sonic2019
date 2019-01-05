package com.padshift.sonic.repository;

import com.padshift.sonic.entities.UserPreference;
import com.padshift.sonic.entities.VidRatings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Created by Regil on 10/11/2018.
 */
@Repository("vidRatingsRepository")
public interface VidRatingsRepository extends JpaRepository<VidRatings,Long> {
    VidRatings findByUserIdAndVideoid(String userId, String videoid);

    @Query("select distinct videoid from VidRatings")
    ArrayList<String> findDistinctVid();

    @Query("select distinct userId from VidRatings where userId <> :currentuserid")
    ArrayList<String> findDistinctUser(@Param("currentuserid") String currentuserId);

    @Query("select distinct rating from VidRatings where userId = :userid and videoid = :vidid")
    String findRatingByUserIdAndVideoid(@Param("userid") String userId, @Param("vidid") String vidId);
}

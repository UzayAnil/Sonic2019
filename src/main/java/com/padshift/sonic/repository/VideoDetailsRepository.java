package com.padshift.sonic.repository;

import com.padshift.sonic.entities.VideoDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JUNSEM97 on 7/20/2018.
 */

@Repository("videoDetailsRepository")
public interface VideoDetailsRepository extends JpaRepository<VideoDetails,Long> {
    ArrayList<VideoDetails> findAllByGenre(String s);


    VideoDetails findByVideoid(String vididtoplay);


    @Query("select distinct genre from VideoDetails")
    ArrayList<String> findDistinctGenre();
//
//    @Query("SELECT  FROM VideoDetails WHERE title LIKE q = :query")
//    ArrayList<VideoDetails> findVideoWhereTitleContains(@Param("query") String vidQuery);

    @Query("SELECT v FROM VideoDetails v WHERE v.title LIKE CONCAT('%',:query,'%')")
    ArrayList<VideoDetails> findVideoWhereTitleContains(@Param("query") String videoQuery);


    ArrayList<VideoDetails> findByGenre(String genreName);




//    @Query("UPDATE VideoDetails v SET v.vidDuration = :duration WHERE v.videoid = :videoid")
//    static void updateVidDur(@Param("videoid") String videoid, @Param("duration") String duration){
//
//    }
//
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE VideoDetails v SET v.vidDuration = :duration WHERE v.videoid = :videoid",nativeQuery = true)
//    void updateVidDur(@Param("videoid") String videoid, @Param("duration") String duration);


}

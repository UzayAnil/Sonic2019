package com.padshift.sonic.repository;

import com.padshift.sonic.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;


/**
 * Created by ruzieljonm on 03/07/2018.
 */
@Repository("videoRepository")
public interface VideoRepository extends JpaRepository<Video,Long> {
    List<Video> findAll();

}

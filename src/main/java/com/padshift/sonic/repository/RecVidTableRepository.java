package com.padshift.sonic.repository;

import com.padshift.sonic.entities.RecVidTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Created by ruzieljonm on 19/02/2019.
 */
@Repository("recVidTableRepository")
public interface RecVidTableRepository extends JpaRepository<RecVidTable,Long> {
    ArrayList<RecVidTable> findByUserId(String userid);
}

package com.padshift.sonic.repository;

import com.padshift.sonic.entities.Genre;
import com.padshift.sonic.entities.VideoDetails;
import org.hibernate.engine.spi.SessionDelegatorBaseImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;

/**
 * Created by ruzieljonm on 06/08/2018.
 */
@Repository("genreRepository")
public interface GenreRepository extends JpaRepository<Genre,Long>{



    Genre findByGenreId(int i);


    Genre findByGenreName(String genre);
}

package com.padshift.sonic.repository;

import com.padshift.sonic.entities.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ruzieljonm on 06/02/2019.
 */
@Repository("playlistRepository")
public interface PlaylistRepository extends JpaRepository<Playlist,Long> {
}

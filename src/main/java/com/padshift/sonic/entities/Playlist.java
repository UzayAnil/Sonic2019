package com.padshift.sonic.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ruzieljonm on 06/02/2019.
 */
@Entity
@Table(name="playlist")
public class Playlist implements Serializable {

    @Id
    @GeneratedValue
    @Column(name="pid")
    private int pid;


    @Column(name="playlistID")
    private String playlistID;

    @Column(name="videoID")
    private String videoID;

    public Playlist() {
    }

    public Playlist(String playlistID, String videoID) {
        this.playlistID = playlistID;
        this.videoID = videoID;
    }

    public String getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }
}

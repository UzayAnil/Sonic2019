package com.padshift.sonic.service;

import com.padshift.sonic.entities.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruzieljonm on 03/07/2018.
 */
@Service
public interface VideoService {


    void saveVideo(Video newVideo);

    List<Video> findAll();

    void saveVideoDetails(VideoDetails newMVDetails);

    ArrayList<VideoDetails> findAllByGenre(String s);


    VideoDetails findByVideoid(String vididtoplay);


    List<UserHistory> findAllByUserId(String userid);

    ArrayList<String> findDistinctGenre();

    void saveGenre(Genre genre);

    ArrayList<VideoDetails> findAllVideoDetails();

    ArrayList<Genre> findAllGenre();


    ArrayList<VideoDetails> findAllVideoDetailsByGenre(String genreName);

    List<UserHistory> findAllByUserIdandVideoid(String currentuser, String vididtoplay);

    ArrayList<String> findDistinctVid();

    String findByUserIdandVideoid(String userId, String vidId);

    VidRatings findVidRatByUserIdandVideoid(String userId, String vidId);

    void saveVidrating(VidRatings newrating);

    Genre findByGenreName(String genre);

    ArrayList<String> findDistinctVidfromVidrating();

    ArrayList<String> findDistinctUser(String currentuserId);

    UserPreference findgenreWeightByGenreNameandUserId(String genre, String s);

    VidRatings findRatingByUserIdandVideoid(String s, String videoid);

    Status[] updateMV();

    VideoDetails findVideoDetailsByVideoid(String videoWatched);

    void savePlaylist(Playlist pl);

    ArrayList<String> findDistinctPlaylistID();

    ArrayList<Playlist> findAllPlaylistByPlaylistID(String plIDs);

    Video findVideoByVideoid(String s);

    ArrayList<RecVidTable> findRecVidTableByUserId(String userid);

    ArrayList<Video> findAllVideo();

    int getGenre(String videoid);
}
package com.padshift.sonic.service.impl;

import com.padshift.sonic.controller.YoutubeAPIController;
import com.padshift.sonic.entities.*;
import com.padshift.sonic.repository.*;
import com.padshift.sonic.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruzieljonm on 03/07/2018.
 */
@Service("videoService")
public class VideoServiceImpl implements VideoService {

    @Autowired
    public VideoRepository videoRepository;
    @Autowired
    public VideoDetailsRepository videoDetailsRepository;

    @Autowired
    public GenreRepository genreRepository;

    @Autowired
    public UserHistoryRepository userHistoryRepository;

    @Autowired
    public VidRatingsRepository vidRatingsRepository;

    @Autowired
    public UserPreferenceRepository userPreferenceRepository;
    @Autowired
    VideoService videoService;

    @Autowired
    YoutubeAPIController youtubeAPIController;

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    RecVidTableRepository recVidTableRepository;


    @Override
    public void saveVideo(Video video) {
        videoRepository.save(video);
    }

    @Override
    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    @Override
    public void saveVideoDetails(VideoDetails newMVDetails) {
        videoDetailsRepository.save(newMVDetails);
    }

    @Override
    public ArrayList<VideoDetails> findAllByGenre(String s) {
        return videoDetailsRepository.findAllByGenre(s);
    }

    @Override
    public VideoDetails findByVideoid(String vididtoplay) {
        return videoDetailsRepository.findByVideoid(vididtoplay);
    }

    @Override
    public List<UserHistory> findAllByUserId(String userid) {
        return userHistoryRepository.findByuserId(userid);
    }

    @Override
    public ArrayList<String> findDistinctGenre() {
        return videoDetailsRepository.findDistinctGenre();
    }

    @Override
    public void saveGenre(Genre genre) {
        genreRepository.save(genre);
    }

    @Override
    public ArrayList<VideoDetails> findAllVideoDetails() {
        return (ArrayList<VideoDetails>) videoDetailsRepository.findAll();
    }

    @Override
    public ArrayList<Genre> findAllGenre() {
        return (ArrayList<Genre>) genreRepository.findAll();
    }



    @Override
    public ArrayList<VideoDetails> findAllVideoDetailsByGenre(String genreName) {
        return videoDetailsRepository.findByGenre(genreName);
    }

    @Override
    public List<UserHistory> findAllByUserIdandVideoid(String currentuser, String vididtoplay) {
        return userHistoryRepository.findAllByUserIdAndVideoid(currentuser, vididtoplay);
    }


    @Override
    public ArrayList<String> findDistinctVid() {
        return userHistoryRepository.findDistinctVid();
    }

    @Override
    public String findByUserIdandVideoid(String userId, String vidId) {
        return vidRatingsRepository.findRatingByUserIdAndVideoid(userId, vidId);
    }

    @Override
    public VidRatings findVidRatByUserIdandVideoid(String userId, String vidId) {
        return vidRatingsRepository.findByUserIdAndVideoid(userId, vidId);
    }

    @Override
    public void saveVidrating(VidRatings newrating) {
        vidRatingsRepository.save(newrating);
    }

    @Override
    public Genre findByGenreName(String genre) {
        return genreRepository.findByGenreName(genre);
    }

    @Override
    public ArrayList<String> findDistinctVidfromVidrating() {
        return vidRatingsRepository.findDistinctVid();
    }

    @Override
    public ArrayList<String> findDistinctUser(String currentuserId) {
        return vidRatingsRepository.findDistinctUser(currentuserId);
    }

    @Override
    public UserPreference findgenreWeightByGenreNameandUserId(String genre, String s) {
        return userPreferenceRepository.findByGenreNameAndUserId(genre, Integer.parseInt(s));
    }

    @Override
    public VidRatings findRatingByUserIdandVideoid(String s, String videoid) {
        return vidRatingsRepository.findByUserIdAndVideoid(s, videoid);
    }

    @Override
    public Status[] updateMV() {
        ArrayList<Genre> genre = videoService.findAllGenre();
        Status[] stat = new Status[genre.size()];

        for(int i=0; i<stat.length; i++){
            stat[i] = new Status();
        }

        for(int i=0; i<genre.size(); i++){
            stat[i].setGenre(genre.get(i).getGenreName());
            int c = youtubeAPIController.updateFetchMusicVideos(genre.get(i).getGenreName());
            stat[i].setUpdateStatusCount(c);
        }


        for(Status upStat : stat){
            System.out.println(upStat.getGenre() + " : " + upStat.getUpdateStatusCount());
        }
        return stat;
    }

    @Override
    public VideoDetails findVideoDetailsByVideoid(String videoWatched) {
        return videoDetailsRepository.findByVideoid(videoWatched);
    }

    @Override
    public void savePlaylist(Playlist pl) {
        playlistRepository.save(pl);
        System.out.println("S A V E D : " + pl);
    }

    @Override
    public ArrayList<String> findDistinctPlaylistID() {
        return playlistRepository.findDistinctPlaylistID();
    }

    @Override
    public ArrayList<Playlist> findAllPlaylistByPlaylistID(String plIDs) {
        return playlistRepository.findAllByPlaylistID(plIDs);
    }

    @Override
    public Video findVideoByVideoid(String s) {
        return videoRepository.findByVideoid(s);
    }

    @Override
    public ArrayList<RecVidTable> findRecVidTableByUserId(String userid) {
        return recVidTableRepository.findByUserId(userid);
    }

    @Override
    public ArrayList<Video> findAllVideo() {
        return (ArrayList<Video>) videoRepository.findAll();
    }

    @Override
    public ArrayList<VideoDetails> findVideoWhereTitleContains(String queryString) {
        return videoDetailsRepository.findVideoWhereTitleContains(queryString);
    }

    @Override
    public ArrayList<String> findAllVideoId() {
        return videoRepository.findAllVideoId();
    }

    @Override
    public int getGenre(String videoid) {
        return genreRepository.getGenre(videoid);
    }
}


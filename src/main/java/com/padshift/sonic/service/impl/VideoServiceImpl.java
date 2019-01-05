package com.padshift.sonic.service.impl;

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




}

package com.padshift.sonic.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by JUNSEM97 on 7/20/2018.
 */
@Entity
@Table(name="videodetails")
public class VideoDetails implements Serializable,Comparable<VideoDetails>{
    @Id
    @Column(name="videoid")
    private String videoid;

    @Column(name="title")
    private String title;

    @Column(name="artist")
    private String artist;


    @Column(name="date")
    private String date;

    @Column(name="genre")
    private String genre;


    @Column(name="viewCount")
    private String viewCount;

    @Column(name="likes")
    private String likes;


    @Column(name="dislikes")
    private String dislikes;

    @Column(name="vidDuration")
    private long vidDuration;

    public VideoDetails() {
    }

    public VideoDetails(String videoid, String title, String artist, String date, String genre, String viewCount, String likes, String dislikes, long vidDuration) {
        this.videoid = videoid;
        this.title = title;
        this.artist = artist;
        this.date = date;
        this.genre = genre;
        this.viewCount = viewCount;
        this.likes = likes;
        this.dislikes = dislikes;
        this.vidDuration = vidDuration;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
    }

    public long getVidDuration() {
        return vidDuration;
    }

    public void setVidDuration(long vidDuration) {
        this.vidDuration = vidDuration;
    }

    @Override
    public int compareTo(VideoDetails videt) {
        float compareViews = Float.parseFloat(((VideoDetails) videt).getViewCount().toString())/2;

        float temp = Float.parseFloat(this.viewCount.toString())/2;
        //desce order
        return  Math.round(compareViews-temp);

        //descending order
        //return compareQuantity - this.quantity;
    }



}
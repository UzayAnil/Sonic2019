package com.padshift.sonic.entities;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * Created by ruzieljonm on 30/09/2018.
 */
public class RecVid implements Comparable<RecVid>{

    private String videoid;
    private String title;
    private String artist;
    private String genre;
    public String thumbnail;
    private String viewCount;
    private float weight;


    public RecVid() {

    }

    public RecVid(String videoid, String title, String artist, String genre, String thumbnail) {
        this.videoid = videoid;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.thumbnail = thumbnail;

    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }



    @Override
    public int compareTo(RecVid rec) {
        float compareWeight = ((RecVid) rec).getWeight();

        //desce order
        return  Math.round(compareWeight-this.weight);

    }

}

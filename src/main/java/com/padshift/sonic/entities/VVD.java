package com.padshift.sonic.entities;

/**
 * Created by ruzieljonm on 22/07/2018.
 */
public class VVD {

    private String videoid;
    private String title;
    private String artist;
    private String date;
    private String genre;
    private String thumbnail;

    public VVD(String videoid, String title, String artist, String date, String genre, String thumbnail) {
        this.videoid = videoid;
        this.title = title;
        this.artist = artist;
        this.date = date;
        this.genre = genre;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}

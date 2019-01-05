package com.padshift.sonic.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ruzieljonm on 03/07/2018.
 */
@Entity
@Table(name="video")
public class Video implements Serializable {
    @Id
    @Column(name="videoid")
    private String videoid;

    @Column(name="mvtitle")
    private String mvtitle;




    @Column(name="thumbnail")
    private String thumbnail;

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getMvtitle() {
        return mvtitle;
    }

    public void setMvtitle(String mvtitle) {
        this.mvtitle = mvtitle;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }



}

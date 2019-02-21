package com.padshift.sonic.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ruzieljonm on 19/02/2019.
 */
@Entity
@Table(name="recvidtable")
public class RecVidTable implements Serializable{

    @Id
    @GeneratedValue
    @Column(name="rvtid")
    private int recvidtabid;


    @Column(name="userId")
    private String userId;

    @Column(name="videoid")
    private String videoid;

    @Column(name="recScore")
    private float recScore;

    public RecVidTable() {
    }

    public RecVidTable(String userId, String videoid, float recScore) {
        this.userId = userId;
        this.videoid = videoid;
        this.recScore = recScore;
    }

    public float getRecScore() {
        return recScore;
    }

    public void setRecScore(float recScore) {
        this.recScore = recScore;
    }

    public int getRecvidtabid() {
        return recvidtabid;
    }

    public void setRecvidtabid(int recvidtabid) {
        this.recvidtabid = recvidtabid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }
}

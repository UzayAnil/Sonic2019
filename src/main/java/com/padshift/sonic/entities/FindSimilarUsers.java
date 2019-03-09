package com.padshift.sonic.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Regil on 09/03/2019.
 */
@Entity
@Table(name="findsimilarusers")
public class FindSimilarUsers implements Serializable{
    @Id
    @GeneratedValue
    @Column(name="findsimilarId")
    private int findsimilarId;

    @Column(name="userId")
    private int userId;

    @Column(name="distance")
    private float distance = 0;

    @Column(name="houseMusic")
    private float houseMusic = 0;

    @Column(name="alternativeMusic")
    private float alternativeMusic = 0;

    @Column(name="reggaeMusic")
    private float reggaeMusic = 0;

    @Column(name="rnbMusic")
    private float rnbMusic = 0;

    @Column(name="religiousMusic")
    private float religiousMusic = 0;

    @Column(name="countryMusic")
    private float countryMusic = 0;

    @Column(name="popMusic")
    private float popMusic = 0;

    @Column(name="rockMusic")
    private float rockMusic = 0;

    @Column(name="hiphopMusic")
    private float hiphopMusic = 0;

    public int getFindsimilarId() {
        return findsimilarId;
    }

    public void setFindsimilarId(int findsimilarId) {
        this.findsimilarId = findsimilarId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getHouseMusic() {
        return houseMusic;
    }

    public void setHouseMusic(float houseMusic) {
        this.houseMusic = houseMusic;
    }

    public float getAlternativeMusic() {
        return alternativeMusic;
    }

    public void setAlternativeMusic(float alternativeMusic) {
        this.alternativeMusic = alternativeMusic;
    }

    public float getReggaeMusic() {
        return reggaeMusic;
    }

    public void setReggaeMusic(float reggaeMusic) {
        this.reggaeMusic = reggaeMusic;
    }

    public float getRnbMusic() {
        return rnbMusic;
    }

    public void setRnbMusic(float rnbMusic) {
        this.rnbMusic = rnbMusic;
    }

    public float getReligiousMusic() {
        return religiousMusic;
    }

    public void setReligiousMusic(float religiousMusic) {
        this.religiousMusic = religiousMusic;
    }

    public float getCountryMusic() {
        return countryMusic;
    }

    public void setCountryMusic(float countryMusic) {
        this.countryMusic = countryMusic;
    }

    public float getPopMusic() {
        return popMusic;
    }

    public void setPopMusic(float popMusic) {
        this.popMusic = popMusic;
    }

    public float getRockMusic() {
        return rockMusic;
    }

    public void setRockMusic(float rockMusic) {
        this.rockMusic = rockMusic;
    }

    public float getHiphopMusic() {
        return hiphopMusic;
    }

    public void setHiphopMusic(float hiphopMusic) {
        this.hiphopMusic = hiphopMusic;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}

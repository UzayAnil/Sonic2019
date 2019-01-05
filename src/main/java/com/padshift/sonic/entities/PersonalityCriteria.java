package com.padshift.sonic.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Regil on 28/11/2018.
 */
@Entity
@Table(name="personalitycriteria")
public class PersonalityCriteria {
    @Id
    @Column(name="personalitycriteriaId")
    private int personalitycriteriaId;

    @Column(name="personalityGroup")
    private String personalityGroup;

    @Column(name="houseMusic", nullable = false)
    private int houseMusic = 1;

    @Column(name="alternativeMusic", nullable = false)
    private int alternativeMusic = 1;

    @Column(name="reggaeMusic", nullable = false)
    private int reggaeMusic = 1;

    @Column(name="rnbMusic", nullable = false)
    private int rnbMusic = 1;

    @Column(name="religiousMusic", nullable = false)
    private int religiousMusic = 1;

    @Column(name="countryMusic", nullable = false)
    private int countryMusic = 1;

    @Column(name="popMusic", nullable = false)
    private int popMusic = 1;

    @Column(name="rockMusic", nullable = false)
    private int rockMusic = 1;

    @Column(name="hiphopMusic", nullable = false)
    private int hiphopMusic = 1;

    public int getPersonalitycriteriaId() {
        return personalitycriteriaId;
    }

    public void setPersonalitycriteriaId(int personalitycriteriaId) {
        this.personalitycriteriaId = personalitycriteriaId;
    }

    public String getPersonalityGroup() {
        return personalityGroup;
    }

    public void setPersonalityGroup(String personalityGroup) {
        this.personalityGroup = personalityGroup;
    }

    public int getHouseMusic() {
        return houseMusic;
    }

    public void setHouseMusic(int houseMusic) {
        this.houseMusic = houseMusic;
    }

    public int getAlternativeMusic() {
        return alternativeMusic;
    }

    public void setAlternativeMusic(int alternativeMusic) {
        this.alternativeMusic = alternativeMusic;
    }

    public int getReggaeMusic() {
        return reggaeMusic;
    }

    public void setReggaeMusic(int reggaeMusic) {
        this.reggaeMusic = reggaeMusic;
    }

    public int getRnbMusic() {
        return rnbMusic;
    }

    public void setRnbMusic(int rnbMusic) {
        this.rnbMusic = rnbMusic;
    }

    public int getReligiousMusic() {
        return religiousMusic;
    }

    public void setReligiousMusic(int religiousMusic) {
        this.religiousMusic = religiousMusic;
    }

    public int getCountryMusic() {
        return countryMusic;
    }

    public void setCountryMusic(int countryMusic) {
        this.countryMusic = countryMusic;
    }

    public int getPopMusic() {
        return popMusic;
    }

    public void setPopMusic(int popMusic) {
        this.popMusic = popMusic;
    }

    public int getRockMusic() {
        return rockMusic;
    }

    public void setRockMusic(int rockMusic) {
        this.rockMusic = rockMusic;
    }

    public int getHiphopMusic() {
        return hiphopMusic;
    }

    public void setHiphopMusic(int hiphopMusic) {
        this.hiphopMusic = hiphopMusic;
    }
}

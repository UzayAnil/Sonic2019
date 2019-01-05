package com.padshift.sonic.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ruzieljonm on 26/06/2018.
 */

@Entity
@Table(name="user")
public class User implements Serializable {

    @Id
    @GeneratedValue
    @Column(name="userId")
    private int userId;

    @Column(name="userName")
    private String userName;


    @Column(name="userPass")
    private String userPass;

    @Column(name="userEmail")
    private String userEmail;

    @Column(name="userPersonality")
    private String userPersonality;

    @Column(name = "userAge")
    private int userAge;

    @Column(name = "agecriteriaId")
    private int agecriteriaId;

    @Column(name = "personalitycriteriaId")
    private int personalitycriteriaId;

    public String getUserPersonality() {
        return userPersonality;
    }

    public void setUserPersonality(String userPersonality) {
        this.userPersonality = userPersonality;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public int getAgecriteriaId() {
        return agecriteriaId;
    }

    public void setAgecriteriaId(int agecriteriaId) {
        this.agecriteriaId = agecriteriaId;
    }

    public int getPersonalitycriteriaId() {
        return personalitycriteriaId;
    }

    public void setPersonalitycriteriaId(int personalitycriteriaId) {
        this.personalitycriteriaId = personalitycriteriaId;
    }
}

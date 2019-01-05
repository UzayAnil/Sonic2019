package com.padshift.sonic.entities;

import javax.persistence.*;

/**
 * Created by Regil on 10/11/2018.
 */
@Entity
@Table(name="videoratings")
public class VidRatings {
    @Id
    @GeneratedValue
    @Column(name="uservidRatingId")
    private int userRatingId;

    @Column(name="userName")
    private String userName;

    @Column(name="userId")
    private String userId;

    @Column(name="videoid")
    private String videoid;

    @Column(name="rating")
    private  String rating;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

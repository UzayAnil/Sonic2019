package com.padshift.sonic.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by ruzieljonm on 22/07/2018.
 */

@Entity
@Table(name="userhistory")
public class UserHistory implements Serializable, Comparable<UserHistory>{
    @Id
    @GeneratedValue
    @Column(name="whid")
    private int whid;

    @Column(name="userId")
    private String userId;

//    @Column(name="userName")
//    private String userName;

    @Column(name="videoid")
    private String videoid;

    @Column(name="seqid")
    private String seqid;

    @Column(name="viewingStatus")
    private String viewingStatus;

    @Column(name="timeSpent")
    private String timeSpent;


    @Column(name = "viewingDate")
    private String viewingDate;

    @Column(name="viewingTime")
    private String viewingTime;

    public UserHistory() {
    }

    public UserHistory(String seqid, String userId, String videoid, String timeSpent, String viewingDate, String viewingStatus, String viewingTime) {
        this.seqid = seqid;
        this.userId = userId;
        this.videoid = videoid;
        this.timeSpent = timeSpent;
        this.viewingDate = viewingDate;
        this.viewingStatus = viewingStatus;
        this.viewingTime = viewingTime;
    }

    public String getSeqid() {
        return seqid;
    }

    public void setSeqid(String seqid) {
        this.seqid = seqid;
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

//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }

    public String getViewingStatus() {
        return viewingStatus;
    }

    public void setViewingStatus(String viewingStatus) {
        this.viewingStatus = viewingStatus;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(String timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getViewingDate() {
        return viewingDate;
    }

    public void setViewingDate(String viewingDate) {
        this.viewingDate = viewingDate;
    }

    public String getViewingTime() {
        return viewingTime;
    }

    public void setViewingTime(String viewingTime) {
        this.viewingTime = viewingTime;
    }

    @Override
    public int compareTo(UserHistory o) {
//        int compviewingTime=((UserHistory)o).getViewingTime();
//        /* For Ascending order*/
//        return this.viewingTime-compviewingTime;
        return 0;
    }

    public static Comparator<UserHistory> TimeComparator
            = new Comparator<UserHistory>() {

        public int compare(UserHistory fruit1, UserHistory fruit2) {

            String fruitName1 = fruit1.getViewingTime().toUpperCase();
            String fruitName2 = fruit2.getViewingTime().toUpperCase();

            //ascending order
            return fruitName1.compareTo(fruitName2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };
}

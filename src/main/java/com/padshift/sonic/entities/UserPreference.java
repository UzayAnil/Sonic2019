package com.padshift.sonic.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by ruzieljonm on 19/07/2018.
 */
@Entity
@Table(name="userpreference")
public class UserPreference implements Serializable,Comparable<UserPreference>{

    @Id
    @GeneratedValue
    @Column(name="userprefId")
    private int userprefid;

    @Column(name="userName")
    private String userName;

    @Column(name="userId")
    private int userId;

    @Column(name="genreName")
    private String genreName;

    @Column(name="genreId")
    private int genreId;

    @Column(name="prefWeight")
    private float prefWeight;

    @Column(name="genreWeight")
    private float genreWeight;

    public float getGenreWeight() {
        return genreWeight;
    }

    public void setGenreWeight(float genreWeight) {
        this.genreWeight = genreWeight;
    }

    public int getUserprefid() {
        return userprefid;
    }

    public void setUserprefid(int userprefid) {
        this.userprefid = userprefid;
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

    public int getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public float getPrefWeight() {
        return prefWeight;
    }

    public void setPrefWeight(float prefWeight) {
        this.prefWeight = prefWeight;
    }

    public static Comparator<UserPreference> PrefWeightComparator
            = new Comparator<UserPreference>() {

        public int compare(UserPreference pref1, UserPreference pref2) {

            String prefw1 = Float.toString(pref1.getPrefWeight());
            String prefw2 = Float.toString(pref2.getPrefWeight());

            //ascending order
//            return fruitName1.compareTo(fruitName2);

            //descending order
            return prefw2.compareTo(prefw1);
        }

    };

    @Override
    public int compareTo(UserPreference o) {
        int comparePref = Math.round(((UserPreference) o).getPrefWeight());

        //ascending order
        return Math.round(this.prefWeight) - comparePref;

        //descending order
        //return compareQuantity - this.quantity;
    }
}

package com.padshift.sonic.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by ruzieljonm on 01/10/2018.
 */

@Entity
@Table(name="criteria")
public class Criteria implements Serializable {

    @Id
    @GeneratedValue
    @Column(name="criteriaId")
    private int criteriaId;

    @Column(name="criteriaName")
    private String criteriaName;

    @Column(name="criteriaPercentage")
    private float criteriaPercentage;

    public int getCriteriaId() {
        return criteriaId;
    }

    public void setCriteriaId(int criteriaId) {
        this.criteriaId = criteriaId;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public float getCriteriaPercentage() {
        return criteriaPercentage;
    }

    public void setCriteriaPercentage(float criteriaPercentage) {
        this.criteriaPercentage = criteriaPercentage;
    }
}

package com.padshift.sonic.repository;

import com.padshift.sonic.entities.User;
import com.padshift.sonic.entities.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Created by ruzieljonm on 28/06/2018.
 */

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User,Long>  {
    User findByUserNameAndUserPass(String userName, String userPass);

    User findByUserName(String userName);

    User findByUserId(int userid);

    @Query("select u from User u where u.userName <> :currentuser")
    ArrayList<User> findOtherUser(@Param("currentuser") String currentuser);

    ArrayList<User> findAll();

    @Query("select distinct agecriteriaId from User")
    ArrayList<Integer> findDistinctAgeGroup();

    @Query("select distinct personalitycriteriaId from User")
    ArrayList<Integer> findDistinctPersonalityGroup();
}
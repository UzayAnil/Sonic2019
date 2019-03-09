package com.padshift.sonic.service;

import com.padshift.sonic.entities.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by ruzieljonm on 28/06/2018.
 */
@Service
public interface UserService {

    void saveUser(User user);





    void saveUserPreference(UserPreference userpref);



    UserPreference findUserPreferenceByUserId(int userId);

    void saveUserHistory(UserHistory userhist);

    User findByUsername(String username);


    User findByUsernameAndPassword(String userName, String userPass);


    User findByUserId(int userid);

    ArrayList<UserPreference> findAllGenrePreferenceByUserId(int userid);

    UserPreference findUserPreferenceByUserIdAndGenreId(int userId, int i);

    void saveCriteria(Criteria criteria);

    ArrayList<Criteria> findAllCriteria();


    void deleteCriteriaByCriteriaId(int deletethis);

    Criteria findCriteriaByCriteriaName(String userinput);

    Criteria findCriteriaByCriteriaId(int editthis);

    ArrayList<String> findDistinctUser(String currentuserId);

    ArrayList<User> findAll();

    String findCurrentByUserId(String currentuserId);

    ArrayList<Integer> findDistinctGenre();

    UserPreference findByGenreId(int s);

    int findUserIdByUserId(int currentuserId);

    ArrayList<Integer> findDistinctUserfromUserPref(int currentuserId);

    ArrayList<AgeCriteria> findAllAgeCriteria();

    ArrayList<Integer> findDistinctAgeGroup();

    void saveAgeCriteria(AgeCriteria agecriteria);

    AgeCriteria findByAgeCriteriaId(int agegroup);

    PersonalityCriteria findByPersonalityCriteriaId(int i);

    ArrayList<Integer> findDistinctPersonalityGroup();

    void savePersonalityCriteria(PersonalityCriteria personalitycriteria);

    ArrayList<PersonalityCriteria> findAllPersonality();

    ArrayList<String> findDistinctSequenceId();

    ArrayList<UserHistory> findUserHistoryBySeqid(String s);

    void saveRecVidTable(RecVidTable v);

    float sumOfgenrebyAgegroup(int agegroup);

    float sumOfgenrebypersonality(int personality);

    float popAgecount();

    float rockAgecount();

    float alternativeAgecount();

    float rnbAgecount();

    float countryAgecount();


    float houseAgecount();

    float reggaeAgecount();

    float religiousAgecount();

    float hiphopAgecount();

    float AllViews();

    double genweightbygenreanduserid(int i, String s);

    ArrayList<Integer> distinctUserIdPref();

    float getGenWeight(int userid, int genreid);

    void saveFindSimilarUsers(FindSimilarUsers newSim);

    void deleteFindsimilarTable();

    ArrayList<FindSimilarUsers> findotherusers(int currentuserId);

    FindSimilarUsers findCurrentUserByUserId(int currentuserId);

    ArrayList<FindSimilarUsers> similarusers(int currentuser);
}

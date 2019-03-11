package com.padshift.sonic.service.impl;

import com.padshift.sonic.entities.*;
import com.padshift.sonic.repository.*;
import com.padshift.sonic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by ruzieljonm on 27/06/2018.
 */
@Service("userService")
public class UserServiceImpl implements UserService {


    @Autowired
    public UserRepository userRepository;

    @Autowired
    public UserPreferenceRepository userPreferenceRepository;

    @Autowired
    public UserHistoryRepository userHistoryRepository;

    @Autowired
    CriteriaRepository criteriaRepository;

    @Autowired
    AgeCriteriaRepository agecriteriaRepository;

    @Autowired
    PersonalityCriteriaRepository personalitycriteriaRepository;

    @Autowired
    RecVidTableRepository recVidTableRepository;

    @Autowired
    FindSimilarUsersRepository findsimilarusersRepsitory;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void saveUserPreference(UserPreference userpref) {
        userPreferenceRepository.save(userpref);
    }

    @Override
    public UserPreference findUserPreferenceByUserId(int userId) {
        return userPreferenceRepository.findByUserId(userId);
    }

    @Override
    public void saveUserHistory(UserHistory userhist) {
        userHistoryRepository.save(userhist);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    @Override
    public User findByUsernameAndPassword(String userName, String userPass) {
        return userRepository.findByUserNameAndUserPass(userName, userPass);
    }

    @Override
    public User findByUserId(int userid) {
        return userRepository.findByUserId(userid);
    }

    @Override
    public ArrayList<UserPreference> findAllGenrePreferenceByUserId(int userid) {
        return userPreferenceRepository.findAllByUserId(userid);
    }

    @Override
    public UserPreference findUserPreferenceByUserIdAndGenreId(int userId, int i) {
        return userPreferenceRepository.findByUserIdAndGenreId(userId,i);
    }

    @Override
    public void saveCriteria(Criteria criteria) {
        criteriaRepository.save(criteria);
    }

    @Override
    public ArrayList<Criteria> findAllCriteria() {
        return (ArrayList<Criteria>) criteriaRepository.findAll();
    }

    @Override
    public void deleteCriteriaByCriteriaId(int deletethis) {
        criteriaRepository.deleteByCriteriaId(deletethis);
        System.out.println("DELETING");
    }

    @Override
    public Criteria findCriteriaByCriteriaName(String userinput) {
        return criteriaRepository.findByCriteriaName(userinput);
    }

    @Override
    public Criteria findCriteriaByCriteriaId(int editthis) {
        return criteriaRepository.findByCriteriaId(editthis);
    }

    @Override
    public ArrayList<String> findDistinctUser(String currentuserId) {
        return userHistoryRepository.findDistinctUser(currentuserId);
    }

    @Override
    public ArrayList<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public String findCurrentByUserId(String currentuserId) {
        return userHistoryRepository.findCurrentByUserId(currentuserId);
    }

    @Override
    public ArrayList<Integer> findDistinctGenre() {
        return userPreferenceRepository.findDistinctGenre();
    }

    @Override
    public UserPreference findByGenreId(int s) {
        return userPreferenceRepository.findByGenreId(s);
    }

    @Override
    public int findUserIdByUserId(int currentuserId) {
        return userPreferenceRepository.findUserIdByUserId(currentuserId);
    }

    @Override
    public ArrayList<Integer> findDistinctUserfromUserPref(int currentuserId) {
        return userPreferenceRepository.findDistinctUserfromUserPref(currentuserId);
    }

    @Override
    public ArrayList<AgeCriteria> findAllAgeCriteria() {
        return (ArrayList<AgeCriteria>) agecriteriaRepository.findAll();
    }

    @Override
    public ArrayList<Integer> findDistinctAgeGroup() {
        return userRepository.findDistinctAgeGroup();
    }

    @Override
    public void saveAgeCriteria(AgeCriteria agecriteria) {
        agecriteriaRepository.save(agecriteria);
    }

    @Override
    public AgeCriteria findByAgeCriteriaId(int agegroup) {
        return agecriteriaRepository.findByAgecriteriaId(agegroup);
    }

    @Override
    public PersonalityCriteria findByPersonalityCriteriaId(int i) {
        return personalitycriteriaRepository.findByPersonalitycriteriaId(i);
    }

    @Override
    public ArrayList<Integer> findDistinctPersonalityGroup() {
        return userRepository.findDistinctPersonalityGroup();
    }

    @Override
    public void savePersonalityCriteria(PersonalityCriteria personalitycriteria) {
        personalitycriteriaRepository.save(personalitycriteria);
    }

    @Override
    public ArrayList<PersonalityCriteria> findAllPersonality() {
        return (ArrayList<PersonalityCriteria>) personalitycriteriaRepository.findAll();
    }

    @Override
    public ArrayList<String> findDistinctSequenceId() {
        return userHistoryRepository.findDistinctSequenceId();
    }

    @Override
    public ArrayList<UserHistory> findUserHistoryBySeqid(String s) {
        return userHistoryRepository.findBySeqid(s);
    }

    @Override
    public void saveRecVidTable(RecVidTable v) {
         recVidTableRepository.save(v);
    }

    @Override
    public ArrayList<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public ArrayList<UserHistory> findByViewingTimeStartingWith(String substring) {
        return userHistoryRepository.findByViewingTimeStartingWith(substring);
    }

    @Override
    public ArrayList<String> findAllDistinctSequenceID(String ctime) {
        return userHistoryRepository.findAllDistinctSequenceID(ctime);
    }

    @Override
    public ArrayList<UserHistory> findAllUserHistory() {
        return (ArrayList<UserHistory>) userHistoryRepository.findAll();
    }

    @Override
    public float sumOfgenrebyAgegroup(int agegroup) {
        return agecriteriaRepository.sumOfgenrebyAgegroup(agegroup);
    }

    @Override
    public float sumOfgenrebypersonality(int personality) {
        return personalitycriteriaRepository.sumOfgenrebypersonality(personality);
    }

    @Override
    public float popAgecount() {
        return agecriteriaRepository.popAgecount();
    }

    @Override
    public float rockAgecount() {
        return agecriteriaRepository.rockAgecount();
    }

    @Override
    public float alternativeAgecount() {
        return agecriteriaRepository.alternativeAgecount();
    }

    @Override
    public float rnbAgecount() {
        return agecriteriaRepository.rnbAgecount();
    }

    @Override
    public float countryAgecount() {
        return agecriteriaRepository.countryAgecount();
    }

    @Override
    public float houseAgecount() {
        return agecriteriaRepository.houseAgecount();
    }

    @Override
    public float reggaeAgecount() {
        return agecriteriaRepository.reggaeAgecount();
    }

    @Override
    public float religiousAgecount() {
        return agecriteriaRepository.religiousAgecount();
    }

    @Override
    public float hiphopAgecount() {
        return agecriteriaRepository.hiphopAgecount();
    }

    @Override
    public float AllViews() {
        return agecriteriaRepository.AllViews();
    }

    @Override
    public double genweightbygenreanduserid(int i, String s) {
        return userPreferenceRepository.genweightbygenreanduserid(i,s);
    }

    @Override
    public ArrayList<Integer> distinctUserIdPref() {
        return userPreferenceRepository.distinctUserIdPref();
    }

    @Override
    public float getGenWeight(int userid, int genreid) {
        return userPreferenceRepository.getGenWeight(userid, genreid);
    }

    @Override
    public void saveFindSimilarUsers(FindSimilarUsers newSim) {
        findsimilarusersRepsitory.save(newSim);
    }

    @Override
    public void deleteFindsimilarTable() {
        findsimilarusersRepsitory.deleteAll();
    }

    @Override
    public ArrayList<FindSimilarUsers> findotherusers(int currentuserId) {
        return findsimilarusersRepsitory.findotherusers(currentuserId);
    }

    @Override
    public FindSimilarUsers findCurrentUserByUserId(int currentuserId) {
        return findsimilarusersRepsitory.findByUserId(currentuserId);
    }

    @Override
    public ArrayList<FindSimilarUsers> similarusers(int currentuser) {
        return findsimilarusersRepsitory.similarusers(currentuser);
    }

//    @Override
//    public ArrayList<UserHistory>[] findUserHistoryByTimeAndSeqid(String substring) {
//        return userHistoryRepository.findUserHistoryByTimeAndSeqid(substring);
//    }
}

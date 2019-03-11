package com.padshift.sonic.controller;

import com.ibm.watson.developer_cloud.assistant.v1.model.Intent;
import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.Class;
import com.padshift.sonic.entities.*;
import com.padshift.sonic.service.GenreService;
import com.padshift.sonic.service.UserService;
import com.padshift.sonic.service.VideoService;

import java.io.*;
import java.sql.Time;
import java.util.Random;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.mapping.Array;
import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import radams.gracenote.webapi.GracenoteException;
import radams.gracenote.webapi.GracenoteMetadata;
import radams.gracenote.webapi.GracenoteWebAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.LinkedHashSet;

/**
 * Created by ruzieljonm on 26/06/2018.
 */


@SuppressWarnings("Duplicates")
@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    VideoService videoService;

    @Autowired
    GenreService genreService;


    @RequestMapping("/sonic")
    public String showLoginPage(HttpSession session, Model model) {
        if(session.isNew()) {
            return "signinsignup";
        }else{
            return showHomepage(model,session);
        }
    }

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public String generalSigninPost(HttpServletRequest request, Model model, HttpSession session) {
        String userName = request.getParameter("inputUserName1");
        String userPass = request.getParameter("inputPassword1");

        if(userName.equals("admin") && userPass.equals("admin")){ //admin log-in
            return "HomePageAdmin";
        }else {

            User checkUser = userService.findByUsernameAndPassword(userName, userPass);

            if (checkUser != null) {
                session.setAttribute("userid", checkUser.getUserId());
                session.setAttribute("username", checkUser.getUserName());
                session.setAttribute("sessionid", checkUser.getUserId()+getSaltString());
                session.setAttribute("useragegroup", checkUser.getAgecriteriaId());
                session.setAttribute("userpersonalitygroup", checkUser.getPersonalitycriteriaId());
                System.out.println(checkUser.getUserId() + " " + checkUser.getUserName());

                System.out.println("S E S S I O N " + session.getAttribute("sessionid"));

                return showHomepage(model, session);
            } else {
                return "signinsignup";
            }
        }
    }

    @RequestMapping("/trygoogle")
    public String trygoogle(){
        return "trygoogle";
    }



    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String generalSignup(HttpServletRequest request, Model model, HttpSession session) {
        User newUser = new User();
        int age;
        int ageGroup, personalityGroup;
        String personality;
        newUser.setUserName(request.getParameter("inputUserName"));
        newUser.setUserPass(request.getParameter("inputPassword"));
        newUser.setUserEmail(request.getParameter("inputEmail"));

        Calendar now = Calendar.getInstance();   // Gets the current date and time
        int year = now.get(Calendar.YEAR);       // The current year

        System.out.println("BIRTHDAY CYST : " + request.getParameter("bday"));
        System.out.println("TYPE CYST : " + request.getParameter("radio"));

        String bday = request.getParameter("bday");
        String upToNCharacters = bday.substring(0, Math.min(bday.length(), 4));
        System.out.println(upToNCharacters);

        age = year-Integer.parseInt(upToNCharacters);
        newUser.setUserAge(year-Integer.parseInt(upToNCharacters));
        newUser.setUserPersonality(request.getParameter("radio"));

        if(age<=24){
            ageGroup = 1;
        }else if(age >=25 && age<=34){
            ageGroup = 2;
        }else if(age >=35 && age <=44) {
            ageGroup = 3;
        }else if(age >=45 && age <=54) {
            ageGroup = 4;
        }else if(age >=55 && age <=64) {
            ageGroup = 5;
        }else{
            ageGroup = 6;
        }
        newUser.setAgecriteriaId(ageGroup);

        personality = request.getParameter("radio");
        if(personality.equals("introvert")){
            personalityGroup = 1;
        }else{
            personalityGroup = 2;
        }

        newUser.setPersonalitycriteriaId(personalityGroup);

        userService.saveUser(newUser);
        User checkUser = userService.findByUsername(request.getParameter("inputUserName"));
        session.setAttribute("userid", checkUser.getUserId());
        session.setAttribute("username", request.getParameter("inputUserName"));
        session.setAttribute("sessionid", checkUser.getUserId()+getSaltString());
        session.setAttribute("useragegroup", checkUser.getAgecriteriaId());
        session.setAttribute("userpersonalitygroup", checkUser.getPersonalitycriteriaId());
        return showGenreSelection(model,session);
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    public static String getTime(){
        final long CURRENT_TIME_MILLIS = System.currentTimeMillis();
        Date instant = new Date(CURRENT_TIME_MILLIS);
        SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm:ss" );
        String time = sdf.format( instant );
        System.out.println( "Time: " + time );
        return time.toString();
    }

    @RequestMapping("/genreselection")
    public String showGenreSelection(Model model, HttpSession session){
        updatepersonalitycriteria();
        updateagecriteria();
        ArrayList<Genre> genres = genreService.findAll();

        for (int i=0; i<genres.size(); i++){
            System.out.println(genres.get(i).getGenreName());
        }

        model.addAttribute("genres", genres);
        model.addAttribute("starname", "pota");

        return "GenreSelection";
    }

    @RequestMapping(value = "/submitpref", method = RequestMethod.POST)
    public String submitPreference(HttpServletRequest request, HttpSession session, Model model) {
        System.out.println(session.getAttribute("userid") + "usah id");

        ArrayList<Genre> genres = genreService.findAll();
        UserPreference[] genrePreference = new UserPreference[genres.size()];

        for (int i=0; i<genrePreference.length; i++){
            genrePreference[i] = new UserPreference();
        }
        int userid = Integer.parseInt(session.getAttribute("userid").toString());
        String username = (String) session.getAttribute("username");

        for (int i=0; i<genrePreference.length; i++){
            genrePreference[i].setUserId(userid);
            float temp;
            if(request.getParameter(genres.get(i).getGenreName().toString())==null){
                temp=0;
            }else {
                temp = Float.parseFloat(request.getParameter(genres.get(i).getGenreName().toString()));
            }
            genrePreference[i].setPrefWeight(temp);
            genrePreference[i].setGenreId(genres.get(i).getGenreId());
            genrePreference[i].setUserName(username);
            genrePreference[i].setGenreName(genres.get(i).getGenreName());
            System.out.println(genrePreference[i].getUserId() + "-" + genrePreference[i].getGenreId() + "-" + genrePreference[i].getPrefWeight());

            userService.saveUserPreference(genrePreference[i]);

            updategenreWeight(genres.get(i).getGenreId(), userid, temp);
        }

        return showHomepageAfterReg(model,session);

    }




    @RequestMapping("/gotoProfile")
    public String gotoProfile(){
        return "testing";
    }

    @RequestMapping("/gotoPlaylistExp")
    public String gotoPlaylistExplorer(){
        return "PlaylistExplorer";
    }

    @RequestMapping("/logoutUser")
    public String logoutUser(HttpSession session){

        session.invalidate();
        return "signinsignup";
    }

    public void updategenreWeight(int genreid, int userid, float temp){
        User user = userService.findByUserId(userid);

        ArrayList<PersonalityCriteria> personalities = userService.findAllPersonality();
        int agegroup = user.getAgecriteriaId();
        int personality = user.getPersonalitycriteriaId();
        AgeCriteria useragegroup = userService.findByAgeCriteriaId(agegroup);
        PersonalityCriteria personalitygroup = userService.findByPersonalityCriteriaId(personality);
        float agetotalviews = userService.sumOfgenrebyAgegroup(agegroup);
        float personalitytotalviews = userService.sumOfgenrebypersonality(personality);
        float totalviews = userService.AllViews();
        float genweight;
        float genreAge = 0;
        float genrePT =0;
        float genreWT = 0;

        System.out.println("AGE TOTAL VIEWS: "+ agetotalviews);
        System.out.println("PERSONALITY TOTAL VIEWS: "+ personalitytotalviews);
        if(totalviews != 0){
            switch(genreid){
                case 1: genreAge = useragegroup.getPopMusic();
                        genrePT = personalitygroup.getPopMusic();
                        genreWT = userService.popAgecount();
                        break;
                case 2: genreAge = useragegroup.getRockMusic();
                        genrePT = personalitygroup.getRockMusic();
                        genreWT = userService.rockAgecount();
                        break;
                case 3: genreAge = useragegroup.getAlternativeMusic();
                        genrePT = personalitygroup.getAlternativeMusic();
                        genreWT = userService.alternativeAgecount();
                        break;
                case 4: genreAge = useragegroup.getRnbMusic();
                        genrePT = personalitygroup.getRnbMusic();
                        genreWT = userService.rnbAgecount();
                        break;
                case 5: genreAge = useragegroup.getCountryMusic();
                        genrePT = personalitygroup.getCountryMusic();
                        genreWT = userService.countryAgecount();
                        break;
                case 6: genreAge = useragegroup.getHouseMusic();
                        genrePT = personalitygroup.getHouseMusic();
                        genreWT = userService.houseAgecount();
                        break;
                case 7: genreAge = useragegroup.getReggaeMusic();
                        genrePT = personalitygroup.getReggaeMusic();
                        genreWT = userService.reggaeAgecount();
                        break;
                case 8: genreAge = useragegroup.getReligiousMusic();
                        genrePT = personalitygroup.getReligiousMusic();
                        genreWT = userService.religiousAgecount();
                        break;
                case 9: genreAge = useragegroup.getHiphopMusic();
                        genrePT = personalitygroup.getHiphopMusic();
                        genreWT = userService.hiphopAgecount();
                        break;
            }
        }
        System.out.println("Genre ID: "+genreid);
        System.out.println("Total Views: "+totalviews);
        System.out.println("Personal Total Views"+personalitytotalviews);
        System.out.println("UserInput: "+temp);
        System.out.println("genreAge: "+genreAge);
        System.out.println("genrePT: "+genrePT);

        System.out.println((temp/10)*.4);
        System.out.println((genreAge/agetotalviews)*.25);
        System.out.println((genrePT/personalitytotalviews)*.25);
        System.out.println((genreWT/totalviews)*.1);

        if(agetotalviews == 0){
            agetotalviews = 1;
        }
        if(personalitytotalviews == 0){
            personalitytotalviews = 1;
        }
        if(totalviews == 0){
            totalviews = 1;
        }
        genweight = (float) (((temp/10)*.4)+((genreAge/agetotalviews)*.25)+((genrePT/personalitytotalviews)*.25)+((genreWT/totalviews)*.1));

        UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(userid, genreid);
        userpref.setGenreWeight(genweight);
        userService.saveUserPreference(userpref);
    }
    String topgenre=null;

    public String showHomepageAfterReg(Model model, HttpSession session){
        String userid = session.getAttribute("userid").toString();
        System.out.println("" + userid);
        User user = userService.findByUserId(Integer.parseInt(userid));

        System.out.println(user.getUserId() + " "+ user.getUserName());


        ArrayList<VideoDetails> videos = videoService.findAllVideoDetails();
        ArrayList<RecVid> recVideos = new ArrayList<>();

        for( VideoDetails vid : videos){
            RecVid rv = new RecVid();
            rv.setVideoid(vid.getVideoid());
            rv.setTitle(vid.getTitle());
            rv.setArtist(vid.getArtist());
            rv.setGenre(vid.getGenre());
            rv.setViewCount(vid.getViewCount());

            rv.setWeight(computeInitialVideoWeight(vid,user));
            recVideos.add(rv);
        }

        Collections.sort(recVideos);

        for(int i=0; i<30; i++){
            RecVidTable v = new RecVidTable(session.getAttribute("userid").toString(),recVideos.get(i).getVideoid(), recVideos.get(i).getWeight());
            userService.saveRecVidTable(v);
        }




        for(int i=0; i<10; i++){
            System.out.println(recVideos.get(i).getTitle() + " : " + recVideos.get(i).getWeight());
        }

        ArrayList<RecVid> vr1 = new ArrayList<RecVid>();
        ArrayList<RecVid> vr2 = new ArrayList<RecVid>();
        ArrayList<RecVid> vr3 = new ArrayList<RecVid>();
        ArrayList<RecVid> vr4 = new ArrayList<RecVid>();

        for(int i=0; i<=5; i++) {
            RecVid vid1 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr1.add(vid1);
            vid1 = null;
        }

        for(int i=6; i<=11; i++) {
            RecVid vid2 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr2.add(vid2);
            vid2 = null;
        }

        for(int i=12; i<=17; i++) {
            RecVid vid3 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr3.add(vid3);
            vid3 = null;
        }


        for(int i=18; i<=23; i++) {
            RecVid vid4 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr4.add(vid4);
            vid4 = null;


        }

        findsimilarUsers(session.getAttribute("userid").toString(), session);

        ArrayList<FindSimilarUsers> users = (ArrayList<FindSimilarUsers>) session.getAttribute("similarusers");
        System.out.println("SIMILAR USERS");
        for (int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i).getUserId()+"======");
        }

        System.out.println(" S E S S I O N : " + session.getAttribute("sessionid"));
        model.addAttribute("r1", vr1);
        model.addAttribute("r2", vr2);
        model.addAttribute("r3", vr3);
        model.addAttribute("r4", vr4);


        return "Homepage";



    }

    @RequestMapping("/homepagev2")
    public String showHomepage(Model model, HttpSession session) {
        String userid = session.getAttribute("userid").toString();
        System.out.println("" + userid);
        User user = userService.findByUserId(Integer.parseInt(userid));

        System.out.println(user.getUserId() + " "+ user.getUserName());



        ArrayList<RecVid> recVideos = new ArrayList<>();
        ArrayList<RecVidTable> videos = videoService.findRecVidTableByUserId(userid);
        for( RecVidTable vid : videos){
            VideoDetails v = videoService.findByVideoid(vid.getVideoid());
            RecVid rv = new RecVid();
            rv.setVideoid(v.getVideoid());
            rv.setTitle(v.getTitle());
            rv.setArtist(v.getArtist());
            rv.setGenre(v.getGenre());
            rv.setViewCount(v.getViewCount());
            rv.setWeight(vid.getRecScore());
            recVideos.add(rv);

        }

        Collections.shuffle(recVideos);


        for(int i=0; i<10; i++){
            System.out.println(recVideos.get(i).getTitle() + " : " + recVideos.get(i).getWeight());
        }

        ArrayList<RecVid> vr1 = new ArrayList<RecVid>();
        ArrayList<RecVid> vr2 = new ArrayList<RecVid>();
        ArrayList<RecVid> vr3 = new ArrayList<RecVid>();
        ArrayList<RecVid> vr4 = new ArrayList<RecVid>();

        for(int i=0; i<=5; i++) {
            RecVid vid1 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr1.add(vid1);
            vid1 = null;
        }

        for(int i=6; i<=11; i++) {
            RecVid vid2 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr2.add(vid2);
            vid2 = null;
        }

        for(int i=12; i<=17; i++) {
            RecVid vid3 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr3.add(vid3);
            vid3 = null;
        }


        for(int i=18; i<=23; i++) {
            RecVid vid4 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr4.add(vid4);
            vid4 = null;


        }


        findsimilarUsers(session.getAttribute("userid").toString(), session);

        ArrayList<FindSimilarUsers> users = (ArrayList<FindSimilarUsers>) session.getAttribute("similarusers");
        System.out.println("SIMILAR USERS");
        for (int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i).getUserId()+"======");
        }

        System.out.println(" S E S S I O N : " + session.getAttribute("sessionid"));
        model.addAttribute("r1", vr1);
        model.addAttribute("r2", vr2);
        model.addAttribute("r3", vr3);
        model.addAttribute("r4", vr4);


        return "Homepage";



    }

    public void refreshcriterias(HttpSession session){

        ArrayList<Genre> genres = genreService.findAll();
        for (int i = 0; i < genres.size(); i++) {
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId((Integer) session.getAttribute("userid"), genres.get(i).getGenreId());
            updategenreWeight(genres.get(i).getGenreId(), (Integer) session.getAttribute("userid"), userpref.getPrefWeight());
        }
    }

    public void updatepersonalitycriteria(){
        try{
            for (int i = 1; i <= 2; i++) {
                PersonalityCriteria personality = userService.findByPersonalityCriteriaId(i);
                System.out.println("BOBO"+personality.getPersonalityGroup());
            }
        }catch (Exception e){
            ArrayList<Integer> personalitygroups = userService.findDistinctPersonalityGroup();

            for (int i = 0; i < personalitygroups.size(); i++) {
                boolean personality1 = personalitygroups.get(i).equals(1);
                boolean personality2 = personalitygroups.get(i).equals(2);


                if(personality1 == true){
                    PersonalityCriteria personalitycriteria = new PersonalityCriteria();
                    personalitycriteria.setPersonalityGroup("Introvert");
                    personalitycriteria.setPersonalitycriteriaId(1);
                    userService.savePersonalityCriteria(personalitycriteria);
                    personalitycriteria = null;
                }

                if(personality2 == true){
                    PersonalityCriteria personalitycriteria = new PersonalityCriteria();
                    personalitycriteria.setPersonalityGroup("Extrovert");
                    personalitycriteria.setPersonalitycriteriaId(2);
                    userService.savePersonalityCriteria(personalitycriteria);
                    personalitycriteria = null;
                }

            }
        }
    }

    public void updateagecriteria(){
        try{
            for (int i = 1; i <= 6; i++) {
                AgeCriteria age = userService.findByAgeCriteriaId(i);
                System.out.println("BOBO"+age.getAgecriteriaId());
            }
        }catch (Exception e){
            ArrayList<Integer> agegroups = userService.findDistinctAgeGroup();

            for (int i = 0; i < agegroups.size(); i++) {
                boolean group1 = agegroups.get(i).equals(1);
                boolean group2 = agegroups.get(i).equals(2);
                boolean group3 = agegroups.get(i).equals(3);
                boolean group4 = agegroups.get(i).equals(4);
                boolean group5 = agegroups.get(i).equals(5);
                boolean group6 = agegroups.get(i).equals(6);

                if(group1 == true){
                    AgeCriteria agecriteria = new AgeCriteria();
                    agecriteria.setAgeGroup("Age Group 1");
                    agecriteria.setAgecriteriaId(1);
                    userService.saveAgeCriteria(agecriteria);
                    agecriteria = null;
                }

                if(group2 == true){
                    AgeCriteria agecriteria = new AgeCriteria();
                    agecriteria.setAgeGroup("Age Group 2");
                    agecriteria.setAgecriteriaId(2);
                    userService.saveAgeCriteria(agecriteria);
                    agecriteria = null;
                }

                if(group3 == true){
                    AgeCriteria agecriteria = new AgeCriteria();
                    agecriteria.setAgeGroup("Age Group 3");
                    agecriteria.setAgecriteriaId(3);
                    userService.saveAgeCriteria(agecriteria);
                    agecriteria = null;
                }

                if(group4 == true){
                    AgeCriteria agecriteria = new AgeCriteria();
                    agecriteria.setAgeGroup("Age Group 4");
                    agecriteria.setAgecriteriaId(4);
                    userService.saveAgeCriteria(agecriteria);
                    agecriteria = null;
                }

                if(group5 == true){
                    AgeCriteria agecriteria = new AgeCriteria();
                    agecriteria.setAgeGroup("Age Group 5");
                    agecriteria.setAgecriteriaId(5);
                    userService.saveAgeCriteria(agecriteria);
                    agecriteria = null;
                }

                if(group6 == true){
                    AgeCriteria agecriteria = new AgeCriteria();
                    agecriteria.setAgeGroup("Age Group 6");
                    agecriteria.setAgecriteriaId(6);
                    userService.saveAgeCriteria(agecriteria);
                    agecriteria = null;
                }
            }
        }
    }

    public void findsimilarUsers(String currentuserId, HttpSession session){
        ArrayList<Integer> users = userService.distinctUserIdPref();
        float genweight;
        System.out.println(currentuserId);
        userService.deleteFindsimilarTable();
        System.out.println("()()()()()");
        for (int i = 0; i < users.size(); i++) {
            FindSimilarUsers newSim = new FindSimilarUsers();
            System.out.println(users.get(i));
            for (int j = 1; j <= 9; j++) {
                newSim.setUserId(users.get(i));
                genweight = userService.getGenWeight(users.get(i), j);
                switch (j){
                    case 1: newSim.setPopMusic(genweight);
                        break;
                    case 2: newSim.setRockMusic(genweight);
                        break;
                    case 3: newSim.setAlternativeMusic(genweight);
                        break;
                    case 4: newSim.setRnbMusic(genweight);
                        break;
                    case 5: newSim.setCountryMusic(genweight);
                        break;
                    case 6: newSim.setHouseMusic(genweight);
                        break;
                    case 7: newSim.setReggaeMusic(genweight);
                        break;
                    case 8: newSim.setReligiousMusic(genweight);
                        break;
                    case 9: newSim.setHiphopMusic(genweight);
                }
                userService.saveFindSimilarUsers(newSim);
            }
        }
        finddistanceValue(Integer.parseInt(currentuserId), session);
    }

    public float finddistanceValue(int currentuserId, HttpSession session){
        FindSimilarUsers current = userService.findCurrentUserByUserId(currentuserId);
        ArrayList<FindSimilarUsers> other = userService.findotherusers(currentuserId);
        System.out.println("CURRENT USER" +currentuserId);
        float dist = 0;
        float distance = 0;
        float pop=0, rock=0, country=0, hiphop=0, rnb=0, alternative=0, reggae=0, religious=0, house=0;
        for (int i = 0; i < other.size(); i++) {
            pop = current.getPopMusic() - other.get(i).getPopMusic();
            rock = current.getRockMusic() - other.get(i).getRockMusic();
            country = current.getCountryMusic() - other.get(i).getCountryMusic();
            hiphop = current.getHiphopMusic() - other.get(i).getHiphopMusic();
            rnb = current.getRnbMusic() - other.get(i).getRnbMusic();
            alternative = current.getAlternativeMusic() - other.get(i).getAlternativeMusic();
            reggae = current.getReggaeMusic() - other.get(i).getReggaeMusic();
            religious = current.getReligiousMusic() - other.get(i).getReligiousMusic();
            house = current.getHouseMusic() - other.get(i).getHouseMusic();
            dist = (pop*pop)+(rock*rock)+(country*country)+(hiphop*hiphop)+(rnb*rnb)+(alternative*alternative)+(reggae*reggae)+(religious*religious)+(house*house);
            distance = (float) Math.sqrt(dist);
            other.get(i).setDistance(distance);
            userService.saveFindSimilarUsers(other.get(i));
        }
        ArrayList<FindSimilarUsers> similarusers = userService.similarusers(currentuserId);
        session.setAttribute("similarusers", similarusers);
        return 0;
    }

    public float computeInitialVideoWeight(VideoDetails video, User user){
        System.out.println(video.getTitle()+"==========="+video.getLikes());
        int agegroup = user.getAgecriteriaId();
        int personality = user.getPersonalitycriteriaId();
        AgeCriteria useragegroup = userService.findByAgeCriteriaId(agegroup);
        PersonalityCriteria personalitygroup = userService.findByPersonalityCriteriaId(personality);
        float agetotalviews = userService.sumOfgenrebyAgegroup(agegroup);
        float personalitytotalviews = userService.sumOfgenrebypersonality(personality);
        float totalviews = userService.AllViews();
        float genweight;
        float genreAge = 0;
        float genrePT =0;
        float genreWT = 0;
        float userInput = 0;
        float vidWeight;
        int genre = videoService.getGenre(video.getVideoid());
        if(totalviews != 0){
            switch(genre){
                case 1: userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),1).getPrefWeight();
                        genreAge = useragegroup.getPopMusic();
                        genrePT = personalitygroup.getPopMusic();
                        genreWT = userService.popAgecount();
                        break;
                case 2: userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),2).getPrefWeight();
                        genreAge = useragegroup.getRockMusic();
                        genrePT = personalitygroup.getRockMusic();
                        genreWT = userService.rockAgecount();
                        break;
                case 3: userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),3).getPrefWeight();
                        genreAge = useragegroup.getAlternativeMusic();
                        genrePT = personalitygroup.getAlternativeMusic();
                        genreWT = userService.alternativeAgecount();
                        break;
                case 4: userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),4).getPrefWeight();
                        genreAge = useragegroup.getRnbMusic();
                        genrePT = personalitygroup.getRnbMusic();
                        genreWT = userService.rnbAgecount();
                        break;
                case 5: userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),5).getPrefWeight();
                        genreAge = useragegroup.getCountryMusic();
                        genrePT = personalitygroup.getCountryMusic();
                        genreWT = userService.countryAgecount();
                        break;
                case 6: userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),6).getPrefWeight();
                        genreAge = useragegroup.getHouseMusic();
                        genrePT = personalitygroup.getHouseMusic();
                        genreWT = userService.houseAgecount();
                        break;
                case 7: userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),7).getPrefWeight();
                        genreAge = useragegroup.getReggaeMusic();
                        genrePT = personalitygroup.getReggaeMusic();
                        genreWT = userService.reggaeAgecount();
                        break;
                case 8: userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),8).getPrefWeight();
                        genreAge = useragegroup.getReligiousMusic();
                        genrePT = personalitygroup.getReligiousMusic();
                        genreWT = userService.religiousAgecount();
                        break;
                case 9: userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),9).getPrefWeight();
                        genreAge = useragegroup.getHiphopMusic();
                        genrePT = personalitygroup.getHiphopMusic();
                        genreWT = userService.hiphopAgecount();
                        break;
            }
        }

        if(agetotalviews == 0){
            agetotalviews = 1;
        }
        if(personalitytotalviews == 0){
            personalitytotalviews = 1;
        }
        if(totalviews == 0){
            totalviews = 1;
        }

        float uipercent, agepercent,pertypepercent;
        Criteria ui = userService.findCriteriaByCriteriaName("userinput");
        System.out.println("++++++ LOOL - "+ ui.toString());
        if(ui.toString()!=null){
            uipercent=ui.getCriteriaPercentage();
        }else{
            uipercent=0;
        }

        Criteria age = userService.findCriteriaByCriteriaName("age");
        if(age!=null){
            agepercent = age.getCriteriaPercentage();
        }else{
            agepercent=0;
        }

        Criteria pertype = userService.findCriteriaByCriteriaName("personality");
        if(pertype!=null){
            pertypepercent = pertype.getCriteriaPercentage();
        }else{
            pertypepercent=0;
        }
        float likes;
        float views = Float.parseFloat(video.getViewCount().toString());
        if(video.getLikes().equals("0")){
            likes =1;
        }else{
            likes = Float.parseFloat(video.getLikes().toString());
        }

        genweight = (float) (((userInput/10)*.4)+((genreAge/agetotalviews)*.25)+((genrePT/personalitytotalviews)*.25)+((genreWT/totalviews)*.1));
        System.out.println("Total: "+totalviews);
        System.out.println(userInput);
        System.out.println(genreAge);

        System.out.println(userInput*.5);
        System.out.println(genreAge*.5);

        System.out.println(genweight);
        vidWeight= (genweight * likes);
        System.out.println("VID WEIGHT : " + vidWeight);

        return vidWeight;
    }



    @RequestMapping("/gotoPlayer")
    public String gotoPlayer(HttpServletRequest request, Model model, HttpSession session){
        String vididtoplay = request.getParameter("clicked");
        String videoWatched = request.getParameter("videoWatched");

//        String[] simusers = (String[]) session.getAttribute("similarusers");
        ArrayList<FindSimilarUsers> simusers = (ArrayList<FindSimilarUsers>) session.getAttribute("similarusers");

        if(videoWatched!=null && Float.parseFloat(request.getParameter("timeSpent").toString())>0){
            UserHistory userhist = new UserHistory();
            userhist.setUserId(session.getAttribute("userid").toString());
            userhist.setVideoid(videoWatched);
            userhist.setSeqid(session.getAttribute("sessionid").toString());
            userhist.setViewingDate(getLocalDate().toString());
            userhist.setViewingTime(getTime());
            userhist.setTimeSpent(request.getParameter("timeSpent").toString());

            VideoDetails curviddur = videoService.findVideoDetailsByVideoid(videoWatched);
            if(Float.parseFloat(request.getParameter("timeSpent").toString())>curviddur.getVidDuration()/2) {
                userhist.setViewingStatus("1");
            }else{
                userhist.setViewingStatus("0");
            }
            userService.saveUserHistory(userhist);
        }

        session.setAttribute("vididtoplay", vididtoplay);

        System.out.println("video id : " + vididtoplay);
        System.out.println("aaaaaaaaaaa" + session.getAttribute("userid"));




        VideoDetails playvid = videoService.findByVideoid(vididtoplay);
        ArrayList<VideoDetails> upnext = (ArrayList<VideoDetails>) videoService.findAllVideoDetails();
//        Collections.sort(upnext);
        Collections.shuffle(upnext);
        System.out.println(playvid.getTitle() + " " + playvid.getArtist());

        String url = "https://www.youtube.com/embed/" + playvid.getVideoid();

        String thumbnail1 = "https://i.ytimg.com/vi/" + upnext.get(1).getVideoid() +"/mqdefault.jpg";
        String thumbnail2 = "https://i.ytimg.com/vi/" + upnext.get(2).getVideoid() +"/mqdefault.jpg";
        String thumbnail3 = "https://i.ytimg.com/vi/" + upnext.get(3).getVideoid() +"/mqdefault.jpg";

        ArrayList<VideoDetails> videoList = new ArrayList<VideoDetails>();
        videoList = videoService.findAllVideoDetails();
        VideoDetails recommVids = new VideoDetails();

        ArrayList<VVD> vr1 = new ArrayList<VVD>();

        Random rand = new Random();
        String[] recommendedVids = cosineMatrix(session.getAttribute("userid").toString(), simusers);
        System.out.println(recommendedVids[0]);
        int j =0;
        for (int i = 0; i < 6; i++) {
            if(recommendedVids[j] != null && i < recommendedVids.length){
                recommVids = videoService.findByVideoid(recommendedVids[i]);
                VVD vid = new VVD(recommVids.getVideoid(), recommVids.getTitle(), recommVids.getArtist(), recommVids.getGenre(), recommVids.getDate(),"https://i.ytimg.com/vi/" + recommVids.getVideoid() + "/mqdefault.jpg");
                vr1.add(vid);
                vid = null;
                j++;
            }
            else{
                int random = rand.nextInt(videoList.size());
                VVD vid = new VVD(videoList.get(random).getVideoid(), videoList.get(random).getTitle(), videoList.get(random).getArtist(), videoList.get(random).getGenre(), videoList.get(random).getDate(),"https://i.ytimg.com/vi/" + videoList.get(random).getVideoid() + "/mqdefault.jpg");
                vr1.add(vid);
                vid = null;
            }
        }

        model.addAttribute("r1", vr1);

        model.addAttribute("emblink", url);
        model.addAttribute("vididtoplay", playvid.getVideoid());
        model.addAttribute("vidtitle", playvid.getTitle());
        model.addAttribute("vidviews", concat(playvid.getViewCount()));
        model.addAttribute("vidlikes", concat(playvid.getLikes()));

//        model.addAttribute("upnext")
        model.addAttribute("upnext1", upnext.get(1));
        model.addAttribute("upnext2", upnext.get(2));
        model.addAttribute("upnext3", upnext.get(3));
        model.addAttribute("vidid", vididtoplay);

        model.addAttribute("tn1", thumbnail1);
        model.addAttribute("tn2", thumbnail2);
        model.addAttribute("tn3", thumbnail3);

        return "VideoPlayerV2";

    }

    @RequestMapping("/submitrating")
    public String submitRating(HttpServletRequest request, Model model, HttpSession session){
        int useragegroup = (Integer) session.getAttribute("useragegroup");
        int personalitygroup = (Integer) session.getAttribute("userpersonalitygroup");
        ArrayList<FindSimilarUsers> simusers = (ArrayList<FindSimilarUsers>) session.getAttribute("similarusers");
        String vididtoplay = request.getParameter("current");
        String vididnexttoplay = request.getParameter("clicked");
        String vidrating = request.getParameter("rating");
        VideoDetails video = videoService.findByVideoid(vididtoplay);
        String videoWatched = request.getParameter("videoWatched");

        if(videoWatched!=null && Float.parseFloat(request.getParameter("timeSpent").toString())>0){
            UserHistory userhist = new UserHistory();
            userhist.setUserId(session.getAttribute("userid").toString());
            userhist.setVideoid(videoWatched);
            userhist.setSeqid(session.getAttribute("sessionid").toString());
            userhist.setViewingDate(getLocalDate().toString());
            userhist.setViewingTime(getTime());
            userhist.setTimeSpent(request.getParameter("timeSpent").toString());

            VideoDetails curviddur = videoService.findVideoDetailsByVideoid(videoWatched);
            if(Float.parseFloat(request.getParameter("timeSpent").toString())>curviddur.getVidDuration()/2) {
                userhist.setViewingStatus("1");
            }else{
                userhist.setViewingStatus("0");
            }
            userService.saveUserHistory(userhist);
        }
        System.out.println("video id : " + vididnexttoplay);

        int genre = videoService.getGenre(video.getVideoid());
        incrementagegroup(useragegroup, genre);
        incrementpersonalitygroup(personalitygroup, genre);

        ArrayList<Genre> genres = genreService.findAll();
        for (int i = 0; i < genres.size(); i++) {
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId((Integer) session.getAttribute("userid"), genres.get(i).getGenreId());
            updategenreWeight(genres.get(i).getGenreId(), (Integer) session.getAttribute("userid"), userpref.getPrefWeight());
        }
        List<UserHistory> currentuserhist = videoService.findAllByUserIdandVideoid(session.getAttribute("userid").toString(), vididtoplay);

        try{
            VidRatings uservideorating = videoService.findVidRatByUserIdandVideoid(session.getAttribute("userid").toString(), vididtoplay);
            if(!vidrating.equals("0")){
                uservideorating.setRating(vidrating);
                videoService.saveVidrating(uservideorating);
            }
        }catch (Exception e){
            VidRatings newrating = new VidRatings();
            newrating.setUserId(session.getAttribute("userid").toString());
            newrating.setUserName(session.getAttribute("username").toString());
            newrating.setVideoid(vididtoplay);
            newrating.setRating(vidrating);
            videoService.saveVidrating(newrating);
        }

        VideoDetails playvid = videoService.findByVideoid(vididnexttoplay);
        ArrayList<VideoDetails> upnext = (ArrayList<VideoDetails>) videoService.findAllVideoDetails();
//        Collections.sort(upnext);
        Collections.shuffle(upnext);
//        System.out.println(playvid.getTitle() + " " + playvid.getArtist());

        String url = "https://www.youtube.com/embed/" + playvid.getVideoid();

        String thumbnail1 = "https://i.ytimg.com/vi/" + upnext.get(1).getVideoid() +"/mqdefault.jpg";
        String thumbnail2 = "https://i.ytimg.com/vi/" + upnext.get(2).getVideoid() +"/mqdefault.jpg";
        String thumbnail3 = "https://i.ytimg.com/vi/" + upnext.get(3).getVideoid() +"/mqdefault.jpg";

        ArrayList<VideoDetails> videoList = new ArrayList<VideoDetails>();
        videoList = videoService.findAllVideoDetails();
        VideoDetails recommVids = new VideoDetails();

        ArrayList<VVD> vr1 = new ArrayList<VVD>();

        Random rand = new Random();
        String[] recommendedVids = cosineMatrix(session.getAttribute("userid").toString(), simusers);
        System.out.println(recommendedVids[0]);
        int j =0;
        for (int i = 0; i < 6; i++) {
            if(recommendedVids[j] != null && i < recommendedVids.length){
                recommVids = videoService.findByVideoid(recommendedVids[i]);
                VVD vid = new VVD(recommVids.getVideoid(), recommVids.getTitle(), recommVids.getArtist(), recommVids.getGenre(), recommVids.getDate(),"https://i.ytimg.com/vi/" + recommVids.getVideoid() + "/mqdefault.jpg");
                vr1.add(vid);
                vid = null;
                j++;
            }
            else{
                int random = rand.nextInt(videoList.size());
                VVD vid = new VVD(videoList.get(random).getVideoid(), videoList.get(random).getTitle(), videoList.get(random).getArtist(), videoList.get(random).getGenre(), videoList.get(random).getDate(),"https://i.ytimg.com/vi/" + videoList.get(random).getVideoid() + "/mqdefault.jpg");
                vr1.add(vid);
                vid = null;
            }
        }



        model.addAttribute("r1", vr1);

        model.addAttribute("emblink", url);
        model.addAttribute("vididtoplay", playvid.getVideoid());
        model.addAttribute("vidtitle", playvid.getTitle());
        model.addAttribute("vidviews", concat(playvid.getViewCount()));
        model.addAttribute("vidlikes", concat(playvid.getLikes()));
        model.addAttribute("vidid", vididnexttoplay);

//        model.addAttribute("upnext")
        model.addAttribute("upnext1", upnext.get(1));
        model.addAttribute("upnext2", upnext.get(2));
        model.addAttribute("upnext3", upnext.get(3));

        model.addAttribute("tn1", thumbnail1);
        model.addAttribute("tn2", thumbnail2);
        model.addAttribute("tn3", thumbnail3);

        return "VideoPlayerV2";
    }

//    //before merging ruz
//    @RequestMapping("/gotoPlayer")
//    public String gotoPlayer(HttpServletRequest request, Model model, HttpSession session){
//        String vididtoplay = request.getParameter("clicked");
//        String videoWatched = request.getParameter("videoWatched");
//
//        if(videoWatched!=null && Float.parseFloat(request.getParameter("timeSpent").toString())>0){
//            UserHistory userhist = new UserHistory();
//            userhist.setUserId(session.getAttribute("userid").toString());
//            userhist.setVideoid(videoWatched);
//            userhist.setSeqid(session.getAttribute("sessionid").toString());
//            userhist.setViewingDate(getLocalDate().toString());
//            userhist.setViewingTime(getTime());
//            userhist.setTimeSpent(request.getParameter("timeSpent").toString());
//
//            VideoDetails curviddur = videoService.findVideoDetailsByVideoid(videoWatched);
//            if(Float.parseFloat(request.getParameter("timeSpent").toString())>curviddur.getVidDuration()/2) {
//                userhist.setViewingStatus("1");
//            }else{
//                userhist.setViewingStatus("0");
//            }
//            userService.saveUserHistory(userhist);
//        }
//
//        session.setAttribute("vididtoplay", vididtoplay);
//
//        System.out.println("video id : " + vididtoplay);
//        System.out.println("aaaaaaaaaaa" + session.getAttribute("userid"));
//
//
//
//
//        VideoDetails playvid = videoService.findByVideoid(vididtoplay);
//        ArrayList<VideoDetails> upnext = (ArrayList<VideoDetails>) videoService.findAllVideoDetails();
////        Collections.sort(upnext);
//        Collections.shuffle(upnext);
//        System.out.println(playvid.getTitle() + " " + playvid.getArtist());
//
//        String url = "https://www.youtube.com/embed/" + playvid.getVideoid();
//
//        String thumbnail1 = "https://i.ytimg.com/vi/" + upnext.get(1).getVideoid() +"/mqdefault.jpg";
//        String thumbnail2 = "https://i.ytimg.com/vi/" + upnext.get(2).getVideoid() +"/mqdefault.jpg";
//        String thumbnail3 = "https://i.ytimg.com/vi/" + upnext.get(3).getVideoid() +"/mqdefault.jpg";
//
//        ArrayList<VideoDetails> videoList = new ArrayList<VideoDetails>();
//        videoList = videoService.findAllVideoDetails();
//
//        ArrayList<VVD> vr1 = new ArrayList<VVD>();
//
//        for (int i = 0; i < 6; i++) {
//            VVD vid = new VVD(videoList.get(i).getVideoid(), videoList.get(i).getTitle(), videoList.get(i).getArtist(), videoList.get(i).getGenre(), videoList.get(i).getDate(),"https://i.ytimg.com/vi/" + videoList.get(i).getVideoid() + "/mqdefault.jpg");
//            vr1.add(vid);
//            vid = null;
//        }
//
//        model.addAttribute("r1", vr1);
//
//        model.addAttribute("emblink", url);
//        model.addAttribute("vididtoplay", playvid.getVideoid());
//        model.addAttribute("vidtitle", playvid.getTitle());
//        model.addAttribute("vidviews", concat(playvid.getViewCount()));
//        model.addAttribute("vidlikes", concat(playvid.getLikes()));
//
////        model.addAttribute("upnext")
//        model.addAttribute("upnext1", upnext.get(1));
//        model.addAttribute("upnext2", upnext.get(2));
//        model.addAttribute("upnext3", upnext.get(3));
//        model.addAttribute("vidid", vididtoplay);
//
//        model.addAttribute("tn1", thumbnail1);
//        model.addAttribute("tn2", thumbnail2);
//        model.addAttribute("tn3", thumbnail3);
//
//        return "VideoPlayerV2";
//
//    }

//    //before merging ruz
//    @RequestMapping("/submitrating")
//    public String submitRating(HttpServletRequest request, Model model, HttpSession session){
//        int useragegroup = (Integer) session.getAttribute("useragegroup");
//        int personalitygroup = (Integer) session.getAttribute("userpersonalitygroup");
//        String[] simusers = (String[]) session.getAttribute("similarusers");
//        String vididtoplay = request.getParameter("current");
//        String vididnexttoplay = request.getParameter("clicked");
//        String vidrating = request.getParameter("rating");
//        VideoDetails video = videoService.findByVideoid(vididtoplay);
//        String videoWatched = request.getParameter("videoWatched");
//
//        if(videoWatched!=null && Float.parseFloat(request.getParameter("timeSpent").toString())>0){
//            UserHistory userhist = new UserHistory();
//            userhist.setUserId(session.getAttribute("userid").toString());
//            userhist.setVideoid(videoWatched);
//            userhist.setSeqid(session.getAttribute("sessionid").toString());
//            userhist.setViewingDate(getLocalDate().toString());
//            userhist.setViewingTime(getTime());
//            userhist.setTimeSpent(request.getParameter("timeSpent").toString());
//
//            VideoDetails curviddur = videoService.findVideoDetailsByVideoid(videoWatched);
//            if(Float.parseFloat(request.getParameter("timeSpent").toString())>curviddur.getVidDuration()/2) {
//                userhist.setViewingStatus("1");
//            }else{
//                userhist.setViewingStatus("0");
//            }
//            userService.saveUserHistory(userhist);
//        }
//
//        try{
//            Genre findgenre = videoService.findByGenreName(video.getGenre());
//            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId((Integer) session.getAttribute("userid"), findgenre.getGenreId());
//            incrementagegroup(useragegroup, video.getGenre());
//            incrementpersonalitygroup(personalitygroup, video.getGenre());
//            updategenreWeight(findgenre.getGenreId(), (Integer) session.getAttribute("userid"), userpref.getPrefWeight());
//
//        }catch (Exception e){
//
//        }
//        List<UserHistory> currentuserhist = videoService.findAllByUserIdandVideoid(session.getAttribute("userid").toString(), vididtoplay);
//
//        try{
//            VidRatings uservideorating = videoService.findVidRatByUserIdandVideoid(session.getAttribute("userid").toString(), vididtoplay);
//            if(!vidrating.equals("0")){
//                uservideorating.setRating(vidrating);
//                videoService.saveVidrating(uservideorating);
//            }
//        }catch (Exception e){
//            VidRatings newrating = new VidRatings();
//            newrating.setUserId(session.getAttribute("userid").toString());
//            newrating.setUserName(session.getAttribute("username").toString());
//            newrating.setVideoid(vididtoplay);
//            newrating.setRating(vidrating);
//            videoService.saveVidrating(newrating);
//        }
//
//        VideoDetails playvid = videoService.findByVideoid(vididnexttoplay);
//        ArrayList<VideoDetails> upnext = (ArrayList<VideoDetails>) videoService.findAllVideoDetails();
////        Collections.sort(upnext);
//        Collections.shuffle(upnext);
////        System.out.println(playvid.getTitle() + " " + playvid.getArtist());
//
//        String url = "https://www.youtube.com/embed/" + playvid.getVideoid();
//
//        String thumbnail1 = "https://i.ytimg.com/vi/" + upnext.get(1).getVideoid() +"/mqdefault.jpg";
//        String thumbnail2 = "https://i.ytimg.com/vi/" + upnext.get(2).getVideoid() +"/mqdefault.jpg";
//        String thumbnail3 = "https://i.ytimg.com/vi/" + upnext.get(3).getVideoid() +"/mqdefault.jpg";
//
//        ArrayList<VideoDetails> videoList = new ArrayList<VideoDetails>();
//        videoList = videoService.findAllVideoDetails();
//        VideoDetails recommVids = new VideoDetails();
//
//        ArrayList<VVD> vr1 = new ArrayList<VVD>();
//
//        String[] recommendedVids = cosineMatrix(session.getAttribute("userid").toString(), simusers);
//        System.out.println(recommendedVids[0]);
//        int j =0;
//        for (int i = 0; i < 6; i++) {
//            if(recommendedVids[j] != null && i < recommendedVids.length){
//                recommVids = videoService.findByVideoid(recommendedVids[i]);
//                VVD vid = new VVD(recommVids.getVideoid(), recommVids.getTitle(), recommVids.getArtist(), recommVids.getGenre(), recommVids.getDate(),"https://i.ytimg.com/vi/" + recommVids.getVideoid() + "/mqdefault.jpg");
//                vr1.add(vid);
//                vid = null;
//                j++;
//            }
//            else{
//                VVD vid = new VVD(videoList.get(i).getVideoid(), videoList.get(i).getTitle(), videoList.get(i).getArtist(), videoList.get(i).getGenre(), videoList.get(i).getDate(),"https://i.ytimg.com/vi/" + videoList.get(i).getVideoid() + "/mqdefault.jpg");
//                vr1.add(vid);
//                vid = null;
//            }
//        }
//
////        for (int i = 0; i < 6; i++) {
////            VVD vid = new VVD(videoList.get(i).getVideoid(), videoList.get(i).getTitle(), videoList.get(i).getArtist(), videoList.get(i).getGenre(), videoList.get(i).getDate(),"https://i.ytimg.com/vi/" + videoList.get(i).getVideoid() + "/mqdefault.jpg");
////            vr1.add(vid);
////            vid = null;
////        }
//
//        model.addAttribute("r1", vr1);
//
//        model.addAttribute("emblink", url);
//        model.addAttribute("vididtoplay", playvid.getVideoid());
//        model.addAttribute("vidtitle", playvid.getTitle());
//        model.addAttribute("vidviews", concat(playvid.getViewCount()));
//        model.addAttribute("vidlikes", concat(playvid.getLikes()));
//        model.addAttribute("vidid", vididnexttoplay);
//
////        model.addAttribute("upnext")
//        model.addAttribute("upnext1", upnext.get(1));
//        model.addAttribute("upnext2", upnext.get(2));
//        model.addAttribute("upnext3", upnext.get(3));
//
//        model.addAttribute("tn1", thumbnail1);
//        model.addAttribute("tn2", thumbnail2);
//        model.addAttribute("tn3", thumbnail3);
//
//        return "VideoPlayerV2";
//
//    }

    private void incrementpersonalitygroup(int personalitygroup, int genre) {
        PersonalityCriteria personalitycriteria = userService.findByPersonalityCriteriaId(personalitygroup);
        switch(genre){
            case 1: personalitycriteria.setPopMusic(personalitycriteria.getPopMusic()+1);
                break;
            case 2: personalitycriteria.setRockMusic(personalitycriteria.getRockMusic()+1);
                break;
            case 3: personalitycriteria.setAlternativeMusic(personalitycriteria.getAlternativeMusic()+1);
                break;
            case 4: personalitycriteria.setRnbMusic(personalitycriteria.getRnbMusic()+1);
                break;
            case 5: personalitycriteria.setCountryMusic(personalitycriteria.getCountryMusic()+1);
                break;
            case 6: personalitycriteria.setHouseMusic(personalitycriteria.getHouseMusic()+1);
                break;
            case 7: personalitycriteria.setReggaeMusic(personalitycriteria.getReggaeMusic()+1);
                break;
            case 8: personalitycriteria.setReligiousMusic(personalitycriteria.getReligiousMusic()+1);
                break;
            case 9: personalitycriteria.setHiphopMusic(personalitycriteria.getHiphopMusic()+1);
                break;
        }
        userService.savePersonalityCriteria(personalitycriteria);
    }

    public void incrementagegroup(int agegroup, int genre){
        AgeCriteria agecriteria = userService.findByAgeCriteriaId(agegroup);
        switch(genre){
            case 1: agecriteria.setPopMusic(agecriteria.getPopMusic()+1);
                break;
            case 2: agecriteria.setRockMusic(agecriteria.getRockMusic()+1);
                break;
            case 3: agecriteria.setAlternativeMusic(agecriteria.getAlternativeMusic()+1);
                break;
            case 4: agecriteria.setRnbMusic(agecriteria.getRnbMusic()+1);
                break;
            case 5: agecriteria.setCountryMusic(agecriteria.getCountryMusic()+1);
                break;
            case 6: agecriteria.setHouseMusic(agecriteria.getHouseMusic()+1);
                break;
            case 7: agecriteria.setReggaeMusic(agecriteria.getReggaeMusic()+1);
                break;
            case 8: agecriteria.setReligiousMusic(agecriteria.getReligiousMusic()+1);
                break;
            case 9: agecriteria.setHiphopMusic(agecriteria.getHiphopMusic()+1);
                break;
        }
        userService.saveAgeCriteria(agecriteria);
    }

    public String[] cosineMatrix(String currentuserId, ArrayList<FindSimilarUsers> simusers){
        ArrayList<String> allusers = new ArrayList<>();
//        ArrayList<User> users = userService.findOtherUser(currentuser);
        ArrayList<String> users = videoService.findDistinctUser(currentuserId);
        ArrayList<String> vidhistID = videoService.findDistinctVidfromVidrating();
        ArrayList<String> videohist = new ArrayList<>();
        ArrayList<String> videorating = new ArrayList<>();
        String uhist;
        int current = userService.findUserIdByUserId(Integer.parseInt(currentuserId));
        String currentU = String.valueOf(current);
        System.out.println(currentU);
        allusers.add(currentU);

        int count = 0;

        for (int i=0; i < simusers.size(); i++){
            allusers.add(String.valueOf(simusers.get(i).getUserId()));
        }

        for (int i = 0; i < allusers.size(); i++) {
            System.out.println(allusers.get(i));
        }

        for(int j=0; j < vidhistID.size(); j++){
            VideoDetails vid = videoService.findByVideoid(vidhistID.get(j));
            videohist.add(vid.getVideoid());
            System.out.println("ASDSAD "+videohist.get(j));
        }
        String[] currentuserRow = new String[videohist.size()];
        for(int i=0; i < allusers.size(); i++){
            System.out.print(allusers.get(i));
            for(int j=0; j < vidhistID.size(); j++){
                VideoDetails vid = videoService.findByVideoid(vidhistID.get(j));
                UserPreference genweight = videoService.findgenreWeightByGenreNameandUserId(vid.getGenre(), allusers.get(i));
                try{
                    String rating = videoService.findRatingByUserIdandVideoid(allusers.get(i),vid.getVideoid()).getRating();
                }catch(Exception e){
                    VidRatings newvidrate = new VidRatings();
                    User user = userService.findByUserId(Integer.parseInt(allusers.get(i)));
                    newvidrate.setRating("0");
                    newvidrate.setUserId(String.valueOf(user.getUserId()));
                    newvidrate.setUserName(user.getUserName());
                    newvidrate.setVideoid(vid.getVideoid());
                    videoService.saveVidrating(newvidrate);
                }
                String rating = videoService.findRatingByUserIdandVideoid(allusers.get(i),vid.getVideoid()).getRating();
                if(rating.equals("0")){
                    rating = "0";
                }
                uhist = String.valueOf(((Float.parseFloat(rating)/5)*.5)+(Float.valueOf(genweight.getGenreWeight())*.25)+(Float.valueOf(vid.getLikes())/Float.valueOf(vid.getViewCount())*.25));
                if(rating.equals("0")){
                    uhist = "0";
                }
                videorating.add(uhist);
            }
        }

        System.out.println(videohist.size());
        System.out.println(videorating.size());
        String[] otherRow = new String[videorating.size()];

        for (int i=0; i < videohist.size(); i++){
            System.out.printf("%15s", videohist.get(i));
        }
        System.out.println();
        for (int i=0; i < allusers.size(); i++){
            System.out.print(allusers.get(i));
            for (int j = 0; j < videohist.size(); j++) {
                System.out.printf("// %15s", videorating.get(count));
                if(i == 0){
                    currentuserRow[j] = videorating.get(count);
                }
                else{
                    otherRow[count] = videorating.get(count);
                }
                count++;
            }
            System.out.println("");
        }

        String[] otheruserRow = new String[videohist.size()];

        count = 0;

        float[] cosineValue = new float[allusers.size()];
        double similarUser = 0;
        float temp;
        String[] arrUser = new String[allusers.size()];
        String simUser = "", tempotheruser;
        for (int i = 0; i < currentuserRow.length; i++) {
            System.out.print(currentuserRow[count]+" ");
            count++;
        }
        for (int i=1; i < allusers.size(); i++){
            for (int j = 0; j < videohist.size(); j++) {
                otheruserRow[j] = otherRow[count];
                System.out.print(otheruserRow[j]+" ");
                count++;
            }
            cosineValue[i] = cosineSimilarity(currentuserRow, otheruserRow, allusers.get(0), allusers.get(i), videohist);
            System.out.println("");
        }
        String[] similarUserRow = new String[videohist.size()];
        count = 0;

        for (int i = 0; i < currentuserRow.length; i++) {
            System.out.print(currentuserRow[count]+" ");
            count++;
        }
        for (int i = 0; i < 3; i++) {
            arrUser[i] = allusers.get(i);
        }

//        for (int i = 0; i < cosineValue.length; i++)
//        {
//            for (int j = i + 1; j < cosineValue.length; j++)
//            {
//                if (cosineValue[i] < cosineValue[j])
//                {
//                    temp = cosineValue[i];
//                    cosineValue[i] = cosineValue[j];
//                    cosineValue[j] = temp;
//                }
//            }
//        }

        for (int i = 0; i < arrUser.length; i++) {
            System.out.println(arrUser[i]);
        }
        System.out.println("OOOOOO - "+cosineValue.length);
        for (int i = 0; i < arrUser.length; i++) {
            for (int j = i + 1; j < cosineValue.length; j++)
            {
                if (cosineValue[i] < cosineValue[j])
                {
                    temp = cosineValue[i];
                    tempotheruser = arrUser[i];
                    cosineValue[i] = cosineValue[j];
                    arrUser[i] = arrUser[j];
                    cosineValue[j] = temp;
                    arrUser[j] = tempotheruser;
                }
            }
        }
        System.out.println("cosine values: ");
        for (int i = 0; i < arrUser.length; i++) {
            System.out.println(arrUser[i]+": "+cosineValue[i]);
        }
//        System.out.println(simUser);
//        System.out.println(similarUser);
        String[] recommVids = ratingPrediction(currentuserRow, allusers.get(0), arrUser, cosineValue, videohist);
        return recommVids;
    }

    public float cosineSimilarity(String[] currentuserRatings, String[] otheruserRatings, String currentuser, String otheruser, ArrayList<String> allvideo){
        float nume = 0;
        float denum = 0;
        float cosineresult = 0;
        float multiplier1 = 0, multiplier2 = 0;
        System.out.println(allvideo.size()+" "+currentuserRatings.length+" "+otheruserRatings.length);
        System.out.println("=====================================================");
        for (int i=0; i < allvideo.size(); i++){
            System.out.printf("%15s", allvideo.get(i));
        }
        System.out.println();
        System.out.print(currentuser);
//        for (int i = 0; i < currentuserRatings.length; i++) {
//            double likeOverviews = Double.parseDouble(videoService.findByVideoid(allvideo.get(i)).getLikes())/Double.parseDouble(videoService.findByVideoid(allvideo.get(i)).getViewCount());
//            double currentgenweight = userService.genweightbygenreanduserid(Integer.parseInt(currentuser), videoService.findByVideoid(allvideo.get(i)).getGenre().toString());
//            System.out.printf("%13f WEW %s", (((Double.parseDouble(currentuserRatings[i])/5)*0.5)+(likeOverviews*0.25)+(currentgenweight*0.25)), currentuser);
//        }
        System.out.println();
        System.out.print(otheruser);
//        for (int i = 0; i < otheruserRatings.length; i++) {
//            double likeOverviews = Double.parseDouble(videoService.findByVideoid(allvideo.get(i)).getLikes())/Double.parseDouble(videoService.findByVideoid(allvideo.get(i)).getViewCount());
//            double othergenweight = userService.genweightbygenreanduserid(Integer.parseInt(otheruser), videoService.findByVideoid(allvideo.get(i)).getGenre().toString());
//            System.out.printf("%13f WEW %s", (((Double.parseDouble(otheruserRatings[i])/5)*0.5)+(likeOverviews*0.25)+(othergenweight*0.25)), otheruser);
//        }

        for (int i = 0; i < allvideo.size(); i++) {
            nume += Float.parseFloat(currentuserRatings[i])*Float.parseFloat(otheruserRatings[i]);
            multiplier1 += Float.parseFloat(currentuserRatings[i]) * Float.parseFloat(currentuserRatings[i]);
            multiplier2 += Float.parseFloat(otheruserRatings[i]) * Float.parseFloat(otheruserRatings[i]);
        }

        System.out.println("");
        System.out.println("NUMERATOR: " + nume);
        System.out.println("MULTIPLIER1: "+ multiplier1);
        System.out.println("MULTIPLIER2: "+ multiplier2);
        System.out.println("SQUARE1: "+ Math.sqrt(multiplier1));
        System.out.println("SQUARE2: "+ Math.sqrt(multiplier2));
        denum = (float) (Math.sqrt(multiplier1) * Math.sqrt(multiplier2));
        System.out.println("DENUMERATOR: "+ denum);
        cosineresult = nume/denum;
        System.out.println("RESULT: "+ cosineresult);
        System.out.println();

//        System.out.println((int)Math.sqrt(25));

        return cosineresult;
    }

    public String[] ratingPrediction(String[] currentuserRatings, String currentuser, String[] otheruser, float[] cosineValue, ArrayList<String> allvideo){
        String uhist;
        float nume = 0;
        float denum = 0;
        float[] predictedRate = new float[allvideo.size()];
        String[] predictedVidId = new String[allvideo.size()];
        String[][] otherUserRating = new String[otheruser.length-1][allvideo.size()];
        ArrayList<String> videorating = new ArrayList<>();
        for(int i=0; i < otheruser.length-1; i++){
            System.out.print(otheruser[i]);
            for(int j=0; j < allvideo.size(); j++){
                VideoDetails vid = videoService.findByVideoid(allvideo.get(j));
                System.out.println("XXXXX "+otheruser[i]+" "+vid.getVideoid()+" XXXXXX");
                uhist =  videoService.findByUserIdandVideoid(otheruser[i],vid.getVideoid());
                System.out.println(uhist);
                if(uhist == null){
                    uhist = "0";
                }
                otherUserRating[i][j] = uhist;
                videorating.add(uhist);
            }
        }

        for (int i = 0; i < otheruser.length-1; i++) {
            System.out.println("SIM("+currentuser+","+otheruser[i]+"): "+ cosineValue[i]);
        }
        System.out.println("RATING PREDICTION: ");
        for (int i=0; i < allvideo.size(); i++){
            System.out.printf("%15s", allvideo.get(i));
        }
        System.out.println();
        System.out.print(currentuser);
        for (int i = 0; i < currentuserRatings.length; i++) {
            System.out.printf("//%13s", currentuserRatings[i]);
        }
        System.out.println();
        for (int i = 0; i < otheruser.length-1; i++) {
            System.out.print(otheruser[i]);
            for (int j = 0; j < allvideo.size(); j++) {
                System.out.printf("//%13s", otherUserRating[i][j]);
            }
            System.out.println(" ");
        }

        for (int i = 0; i < allvideo.size(); i++) {
            for (int j = 0; j < otheruser.length-1; j++) {
                if(Double.parseDouble(currentuserRatings[i]) == 0){
                    nume += cosineValue[j] * Double.parseDouble(otherUserRating[j][i]);
                    denum += cosineValue[j];
                    predictedRate[i] = nume/denum;
                    predictedVidId[i] = allvideo.get(i);
                }
            }
            nume = 0;
            denum = 0;
        }
        float temp = 0;
        String tempvidId = "";
        for (int i = 0; i < predictedVidId.length; i++) {
//            System.out.println(predictedVidId[i]+": "+predictedRate[i]);
            for (int j = i + 1; j < predictedVidId.length; j++) {
                if (predictedRate[i] < predictedRate[j]) {
                    temp = predictedRate[i];
                    tempvidId = predictedVidId[i];
                    predictedRate[i] = predictedRate[j];
                    predictedVidId[i] = predictedVidId[j];
                    predictedRate[j] = temp;
                    predictedVidId[j] = tempvidId;
                }
            }
        }
        for (int i = 0; i < predictedVidId.length; i++) {
            System.out.println(predictedVidId[i]+": "+predictedRate[i]);
        }

        DecimalFormat numberFormat = new DecimalFormat("#");
//        System.out.println(numberFormat.format(predictedRate[1])+"KASGDKSJGADJKGASD");
//        updateRating(predictedVidId, predictedRate, currentuser);
        return predictedVidId;
    }

    @RequestMapping("/vplayer")
    public String showVideoPlayer() {
        return "VideoPlayerV2";
    }


    @RequestMapping("/profile")
    public String showUserProfile(HttpServletRequest request, Model model) {
        List<Video> videoList = videoService.findAll();
        for (int i = 0; i < videoList.size(); i++) {
            System.out.println(videoList.get(i).getVideoid());
        }
        model.addAttribute("vids", videoList);
        return "UserProfile";
    }



    public void saveMV(String vidId, String title, String url) {
        Video newVideo = new Video();
        newVideo.setVideoid(vidId);
        newVideo.setMvtitle(title);
        newVideo.setThumbnail(url);
        videoService.saveVideo(newVideo);
    }




    @RequestMapping("/homefeed")
    public String showHomeFeed(Model model){
        return "HomeFeed";
    }


    @RequestMapping("/searchVideo")
    public String searchVideo(HttpServletRequest request, Model model){
        String queryString = "%" + request.getParameter("search") + "%";
        System.out.println("Query this : " + queryString);

        ArrayList<VideoDetails> videoResults = videoService.findVideoWhereTitleContains(queryString);
        ArrayList<RecVid> videos = new ArrayList<>();
        for(VideoDetails v : videoResults){
            System.out.println("[result] : " + v.getTitle());
            RecVid rv = new RecVid(v.getVideoid(),v.getTitle(), v.getArtist(), v.getGenre(), "https://i.ytimg.com/vi/" + v.getVideoid() + "/mqdefault.jpg");
            System.out.println(v.getVideoid() + v.getTitle() +  v.getArtist() + v.getGenre() + "https://i.ytimg.com/vi/" + v.getVideoid() + "/mqdefault.jpg");
            videos.add(rv);
        }






        model.addAttribute("results", videos);


        return "Search";
    }

    @RequestMapping("/showResult")
    public String showResult(){
        return "Search";
    }

    @RequestMapping("/explore")
    public String showExplore(Model model){
        ArrayList<Genre> genres = videoService.findAllGenre();

        model.addAttribute("genre", genres);

        return "Explore";
    }

    @RequestMapping("/admin")
    public String Admin(HttpServletRequest request, Model model){
        return "AdminPage";
    }

    public String concat(String x){
        NumberFormat val = NumberFormat.getNumberInstance(Locale.US);;
        String out = val.format(Long.valueOf(x));
        return out;
    }


    @RequestMapping("/gotoExplore")
    public String showLatestExplore(HttpSession session, Model model){
        String username = session.getAttribute("username").toString();
        String userid = session.getAttribute("userid").toString();


//        ArrayList<VideoDetails> recVideos = videoService.findAllVideoDetails();
        ArrayList<RecVidTable> recVideostemp = videoService.findRecVidTableByUserId(userid);

        ArrayList<VideoDetails> recVideos = new ArrayList<>();

        for(RecVidTable r : recVideostemp){
            recVideos.add(videoService.findVideoDetailsByVideoid(r.getVideoid()));
        }


        Collections.shuffle(recVideos);
        ArrayList<RecVid> vr1 = new ArrayList<>();
        for(int i=0; i<=5; i++) {
            RecVid vid1 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr1.add(vid1);
            vid1 = null;
        }

        model.addAttribute("r1", vr1);
        model.addAttribute("username", username);
        return "LatestExplore";
    }


    //seqrule

    @RequestMapping("/gotoplaylistMorn")
    public String playMornPlaylist(HttpServletRequest request, Model model, HttpSession session){
        ArrayList<Playlist> playListVideos = new ArrayList<>();

        playListVideos = videoService.findAllPlaylistByPlaylistID("plmornaft");

        ArrayList<Video> videos = new ArrayList<>();
        for(Playlist p: playListVideos){
            videos.add(videoService.findVideoByVideoid(p.getVideoID()));
            System.out.println("[PL] - " + p.getVideoID());
        }



        model.addAttribute("vididtoplay", videos.get(0).getVideoid());
        model.addAttribute("plvids", videos);

        return "VideoPlayerWithPlaylist";
    }

    @RequestMapping("/gotoplaylistEve")
    public String playEvePlaylist(HttpServletRequest request, Model model, HttpSession session){
        ArrayList<Playlist> playListVideos = new ArrayList<>();


        playListVideos = videoService.findAllPlaylistByPlaylistID("plevening");

        ArrayList<Video> videos = new ArrayList<>();
        for(Playlist p: playListVideos){
            videos.add(videoService.findVideoByVideoid(p.getVideoID()));
            System.out.println("[PL] - " + p.getVideoID());
        }



        model.addAttribute("vididtoplay", videos.get(0).getVideoid());
        model.addAttribute("plvids", videos);

        return "VideoPlayerWithPlaylist";
    }


    @RequestMapping("/populate")
    public String populate(Model model){

        ArrayList<Video> allvids = videoService.findAllVideo();
        ArrayList<VideoDetails> vids = new ArrayList<>();
        for(Video s: allvids) {
            VideoDetails v = videoService.findVideoDetailsByVideoid(s.getVideoid());
            vids.add(v);
        }

        Collections.sort(vids);
        ArrayList<VideoDetails> plvids = new ArrayList<>();
        for(int i=0; i<50; i++){
            plvids.add(vids.get(i));
        }
        ArrayList<Video> plvidsf = new ArrayList<>();

        for(VideoDetails v : plvids){
            plvidsf.add(videoService.findVideoByVideoid(v.getVideoid()));
        }


        model.addAttribute("vididtoplay", plvidsf.get(0).getVideoid());
        model.addAttribute("plvids", plvidsf);



        return "VideoPlayerWithPlaylist";


    }

    @RequestMapping("/playPLItemPopu")
    public String playPLItemPopu(HttpServletRequest request, HttpSession session, Model model){


        String playthisvid = null;
        for(int i=0; i<20; i++) {
            if(request.getParameter("clicked"+i)!=null && !request.getParameter("clicked"+i).isEmpty()) {
                playthisvid = request.getParameter("clicked"+i);
            }
        }

        String timeSpent = request.getParameter("timeSpent");
        String videoWatched = request.getParameter("videoWatched");
        System.out.println("Music Video Watched : " + videoWatched);
        System.out.println("Play on Next : " + playthisvid);
        System.out.println("Time Spent : " + timeSpent);


        if(videoWatched!=null){
            UserHistory userhist = new UserHistory();
            userhist.setUserId(session.getAttribute("userid").toString());
            userhist.setVideoid(videoWatched);
            userhist.setSeqid(session.getAttribute("sessionid").toString());
            userhist.setViewingDate(getLocalDate().toString());
            userhist.setViewingTime(getTime());
            userhist.setTimeSpent(request.getParameter("timeSpent").toString());

            VideoDetails curviddur = videoService.findVideoDetailsByVideoid(videoWatched);
            System.out.println("CURRENT TITLE : " + curviddur.getTitle());
            if(Float.parseFloat(timeSpent.toString())>curviddur.getVidDuration()/2) {
                userhist.setViewingStatus("1");
            }else{
                userhist.setViewingStatus("0");
            }
            userService.saveUserHistory(userhist);
        }


        ArrayList<Video> video = videoService.findAllVideo();
        ArrayList<Video> plvidsf = new ArrayList<>();
        Collections.shuffle(video);
        for(int i=0; i<50; i++){
            plvidsf.add(video.get(i));
        }


        model.addAttribute("vididtoplay", plvidsf.get(0).getVideoid());
        model.addAttribute("plvids", plvidsf);

        return "VideoPlayerWithPlaylist";
    }



    @RequestMapping("/playPLItem")
    public String playPLItem(HttpServletRequest request, HttpSession session, Model model){


        String playthisvid = null;
        for(int i=0; i<20; i++) {
            if(request.getParameter("clicked"+i)!=null && !request.getParameter("clicked"+i).isEmpty()) {
                playthisvid = request.getParameter("clicked"+i);
            }
        }

        String timeSpent = request.getParameter("timeSpent");
        String videoWatched = request.getParameter("videoWatched");
        System.out.println("Music Video Watched : " + videoWatched);
        System.out.println("Play on Next : " + playthisvid);
        System.out.println("Time Spent : " + timeSpent);


        if(videoWatched!=null){
            UserHistory userhist = new UserHistory();
            userhist.setUserId(session.getAttribute("userid").toString());
            userhist.setVideoid(videoWatched);
            userhist.setSeqid(session.getAttribute("sessionid").toString());
            userhist.setViewingDate(getLocalDate().toString());
            userhist.setViewingTime(getTime());
            userhist.setTimeSpent(request.getParameter("timeSpent").toString());

            VideoDetails curviddur = videoService.findVideoDetailsByVideoid(videoWatched);
            System.out.println("CURRENT TITLE : " + curviddur.getTitle());
            if(Float.parseFloat(timeSpent.toString())>curviddur.getVidDuration()/2) {
                userhist.setViewingStatus("1");
            }else{
                userhist.setViewingStatus("0");
            }
            userService.saveUserHistory(userhist);
        }


        ArrayList<Video> video = videoService.findAllVideo();
        ArrayList<Video> plvids = new ArrayList<>();
        Collections.shuffle(video);
        for(int i=0; i<50; i++){
            plvids.add(video.get(i));
        }


        model.addAttribute("vididtoplay", playthisvid);
        model.addAttribute("plvids", plvids);

        return "VideoPlayerWithPlaylist";
    }
    public ArrayList<String> removeDuplicates(ArrayList<String> list) {

        // Store unique items in result.
        ArrayList<String> result = new ArrayList<>();

        // Record encountered Strings in HashSet.
        HashSet<String> set = new HashSet<>();

        // Loop over argument list.
        for (String item : list) {

            // If String is not in set, add it to the list and the set.
            if (!set.contains(item)) {
                result.add(item);
                set.add(item);
            }
        }
        return result;
    }

    @RequestMapping("/revisedPLG")
    public String genPL(){

//        String currentTime = getTime();
        String currentTime = "9:20:12";
        ArrayList<String>  distinctID = userService.findAllDistinctSequenceID(currentTime.substring(0, currentTime.length() - 6));
        System.out.println(distinctID.size());

        ArrayList<UserHistory>[] seqRules = (ArrayList<UserHistory>[])new ArrayList[distinctID.size()];
        for(int i=0; i<distinctID.size();i++){
            seqRules[i] = userService.findUserHistoryBySeqid(distinctID.get(i).toString());
            Collections.sort(seqRules[i], UserHistory.TimeComparator);
        }

        for(int i=0; i<distinctID.size();i++){
            System.out.print("[" + distinctID.get(i).toString() + "]     ");
            for(int j=0; j<seqRules[i].size(); j++){
                System.out.print(seqRules[i].get(j).getVideoid() + ", ");
            }
            System.out.println();
        }



        ArrayList<String> uniqueVideoIDs = getUniqueVideoIDs(seqRules,distinctID);
        ArrayList<String> databaseRules = buildDBRules(seqRules,distinctID);


        ArrayList<ClassSequentialRules>[] seqrules = (ArrayList<ClassSequentialRules>[])new ArrayList[10];
        seqrules[0] = new ArrayList<>();
        for(String id: uniqueVideoIDs){
            ClassSequentialRules seq = new ClassSequentialRules(id.toString(),calculateSupport(databaseRules,id.toString()),0);
            seqrules[0].add(seq);
        }

        seqrules[0] = removeUnqualifiedForThreshold(seqrules[0]);
        displaySeqrulesMeasures(seqrules[0]);

        boolean flag= true;
        int srIndex=1;
        do{
            seqrules[srIndex] = new ArrayList<>();
            seqrules[srIndex] = buildRuleCombination(seqrules[srIndex-1],uniqueVideoIDs,databaseRules);
            seqrules[srIndex] = removeUnqualifiedForThreshold(seqrules[srIndex]);
            displaySeqrulesMeasures(seqrules[srIndex]);
            if(seqrules[srIndex].get(0).getSupport()==0){
                flag=false;

            }else{
                srIndex++;
            }

        }while (flag==true);
        String[] parts = null;
        for(int i=0; i<seqrules[srIndex-1].size(); i++){
//            System.out.println("The sequence that made it : " + seqrules[srIndex-1].get(i).getVideoIds() );
            parts = seqrules[srIndex-1].get(0).getVideoIds().toString().split(", ");
        }


        for (String p : parts){
            System.out.println("[pl]" + p.toString());
        }


        ArrayList<String> finalList = new ArrayList<String>(Arrays.asList(parts));

        for(int i=0; i<seqRules.length; i++){
            for(int j=0; j<seqRules[i].size(); j++){
                if(!finalList.contains(seqRules[i].get(j).getVideoid().toString())){
                    finalList.add(seqRules[i].get(j).getVideoid());
                }
            }
        }
        ArrayList<String> finalListest = new ArrayList<>();

        finalListest = removeDuplicates(finalList);
//        for(int i=0; i<20; i++){
//            if(!finalListest.contains(finalList.get(i).toString())){
//                finalListest.add(finalList.get(i).toString());
//            }
//        }

        ArrayList<Video> vids = new ArrayList<>();


        for(int i=0; i<20; i++){
            System.out.println("[ f ] - " + finalListest.get(i).toString() );

            Playlist p = new Playlist(currentTime.substring(0, currentTime.length() - 6),finalListest.get(i).toString());
            videoService.savePlaylist(p);
            System.out.println("S A V E D  @ " + getTime());
//            Video v = videoService.findVideoByVideoid(finalListest.get(i).toString());
//            Video nv = new Video(v.getVideoid(), v.getMvtitle(), v.getThumbnail());
//            vids.add(nv);
//            System.out.println(v.getMvtitle());

        }



////        for(UserHistory h: uh){
////            System.out.println(h.getSeqid() + "  " + h.getVideoid() + "  " + h.getViewingTime());
////        }
//
//        ArrayList<UserHistory>[] seqRules = (ArrayList<UserHistory>[])new ArrayList[uh.size()];
//
//        for(int i=0; i<uh.size();i++){
//            seqRules[i] = userService.findUserHistoryBySeqid(sequenceids.get(i).toString());
//            Collections.sort(seqRules[i], UserHistory.TimeComparator);
//        }
//
//        ArrayList<String> uniqueVideoIDs = getUniqueVideoIDs(seqRules,sequenceids);
//
//        ArrayList<String> databaseRules = buildDBRules(seqRules,sequenceids);




        return "testing";
    }

//untouched
//    @RequestMapping("/revisedPLG")
//    public String genPL(){
//
////        String currentTime = getTime();
//        String currentTime = "09:20:12";
////        ArrayList<UserHistory> uh = userService.findByViewingTimeStartingWith(currentTime.substring(0, currentTime.length() - 6));
//        ArrayList<String>  distinctID = userService.findAllDistinctSequenceID(currentTime.substring(0, currentTime.length() - 6));
//        System.out.println(distinctID.size());
//
//        ArrayList<UserHistory>[] seqRules = (ArrayList<UserHistory>[])new ArrayList[distinctID.size()];
//
//        for(int i=0; i<distinctID.size();i++){
//            seqRules[i] = userService.findUserHistoryBySeqid(distinctID.get(i).toString());
//            Collections.sort(seqRules[i], UserHistory.TimeComparator);
//        }
//
//        //revised for shorter and faster
//
//
////        ArrayList<UserHistory>[] seqRules = userService.findUserHistoryByTimeAndSeqid(currentTime.substring(0, currentTime.length() - 6));
////
////
////        for(int i=0; i<distinctID.size();i++){
////
////            Collections.sort(seqRules[i], UserHistory.TimeComparator);
////        }
//
//
//        for(int i=0; i<distinctID.size();i++){
//            System.out.print("[" + distinctID.get(i).toString() + "]     ");
//            for(int j=0; j<seqRules[i].size(); j++){
//                System.out.print(seqRules[i].get(j).getVideoid() + ", ");
//            }
//            System.out.println();
//        }
//
//
//
//        ArrayList<String> uniqueVideoIDs = getUniqueVideoIDs(seqRules,distinctID);
//
//        ArrayList<String> databaseRules = buildDBRules(seqRules,distinctID);
//
//
//        ArrayList<ClassSequentialRules>[] seqrules = (ArrayList<ClassSequentialRules>[])new ArrayList[10];
//        seqrules[0] = new ArrayList<>();
//        for(String id: uniqueVideoIDs){
//            ClassSequentialRules seq = new ClassSequentialRules(id.toString(),calculateSupport(databaseRules,id.toString()),0);
//            seqrules[0].add(seq);
//        }
//
//        seqrules[0] = removeUnqualifiedForThreshold(seqrules[0]);
//        displaySeqrulesMeasures(seqrules[0]);
//
//        boolean flag= true;
//        int srIndex=1;
//        do{
//            seqrules[srIndex] = new ArrayList<>();
//            seqrules[srIndex] = buildRuleCombination(seqrules[srIndex-1],uniqueVideoIDs,databaseRules);
//            seqrules[srIndex] = removeUnqualifiedForThreshold(seqrules[srIndex]);
//            displaySeqrulesMeasures(seqrules[srIndex]);
//            if(seqrules[srIndex].get(0).getSupport()==0){
//                flag=false;
//
//            }else{
//                srIndex++;
//            }
//
//        }while (flag==true);
//        String[] parts = null;
//        for(int i=0; i<seqrules[srIndex-1].size(); i++){
////            System.out.println("The sequence that made it : " + seqrules[srIndex-1].get(i).getVideoIds() );
//            parts = seqrules[srIndex-1].get(0).getVideoIds().toString().split(", ");
//        }
//
//
//        for (String p : parts){
//            System.out.println("[pl]" + p.toString());
//        }
//
//
//        ArrayList<String> finalList = new ArrayList<String>(Arrays.asList(parts));
//
//        for(int i=0; i<seqRules.length; i++){
//            for(int j=0; j<seqRules[i].size(); j++){
//                if(!finalList.contains(seqRules[i].get(j).getVideoid().toString())){
//                    finalList.add(seqRules[i].get(j).getVideoid());
//                }
//            }
//        }
//        ArrayList<String> finalListest = new ArrayList<>();
//
//        finalListest = removeDuplicates(finalList);
////        for(int i=0; i<20; i++){
////            if(!finalListest.contains(finalList.get(i).toString())){
////                finalListest.add(finalList.get(i).toString());
////            }
////        }
//
//        ArrayList<Video> vids = new ArrayList<>();
//        for(int i=0; i<20; i++){
//            System.out.println("[ f ] - " + finalListest.get(i).toString() );
//            Video v = videoService.findVideoByVideoid(finalListest.get(i).toString());
//            Video nv = new Video(v.getVideoid(), v.getMvtitle(), v.getThumbnail());
//            vids.add(nv);
//            System.out.println(v.getMvtitle());
//
//        }
//
//
//
//////        for(UserHistory h: uh){
//////            System.out.println(h.getSeqid() + "  " + h.getVideoid() + "  " + h.getViewingTime());
//////        }
////
////        ArrayList<UserHistory>[] seqRules = (ArrayList<UserHistory>[])new ArrayList[uh.size()];
////
////        for(int i=0; i<uh.size();i++){
////            seqRules[i] = userService.findUserHistoryBySeqid(sequenceids.get(i).toString());
////            Collections.sort(seqRules[i], UserHistory.TimeComparator);
////        }
////
////        ArrayList<String> uniqueVideoIDs = getUniqueVideoIDs(seqRules,sequenceids);
////
////        ArrayList<String> databaseRules = buildDBRules(seqRules,sequenceids);
//
//
//
//
//        return "testing";
//    }

//    @RequestMapping("/write")
//    public void usingFileWriter() throws IOException
//    {
//        File fout = new File("C:/Users/ruzieljonm/Desktop/out.txt");
//        FileOutputStream fos = new FileOutputStream(fout);
//
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
//
//        for (int i = 0; i < 10; i++) {
//            bw.write("something");
//            bw.newLine();
//        }
//
//        bw.close();
//
//    }
//
//    @RequestMapping("/displayrawid")
//    public void displayrawid() throws IOException{
//        //file writing
//        File fout = new File("C:/Users/ruzieljonm/Desktop/rawid_distinct.txt");
//        FileOutputStream fos = new FileOutputStream(fout);
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
//
//        //read tsv
//        StringTokenizer st ;
//        BufferedReader TSVFile = new BufferedReader(new FileReader("C:/Users/ruzieljonm/Desktop/raw.tsv"));
//        String dataRow = TSVFile.readLine(); // Read first line.
//        ArrayList<Video> videos = videoService.findAllVideo();
//
//        ArrayList<User> users = userService.findAllUsers();
//
//        ArrayList<String> rawid = new ArrayList<>();
//        while (dataRow != null){
//            st = new StringTokenizer(dataRow,"\t");
//            List<String> dataArray = new ArrayList<String>() ;
//            while(st.hasMoreElements()){
//                dataArray.add(st.nextElement().toString());
//            }
//
//            dataArray.remove(0);
//
//            for (String item:dataArray) {
////                System.out.print(item + "  ");
//                if(!rawid.contains(item)) {
//                    rawid.add(item);
//                    System.out.println("writing : " + item);
//                }
//
//            }
//            System.out.println(); // Print the data line.
//            dataRow = TSVFile.readLine(); // Read next line of data.
//        }
//
//        System.out.println("raw id size : " + rawid.size());
//
//
//        // Close the file once all data has been read.
//        TSVFile.close();
//
//        for (int i = 0; i < rawid.size(); i++) {
//            bw.write(rawid.get(i));
//            bw.newLine();
//        }
//
//        bw.close();
//
//        // End the printout with a blank line.
//        System.out.println();
//
//    }

    @RequestMapping("/combine")
    public void combineRawID() throws IOException {

        ArrayList<String> videoids = videoService.findAllVideoId();


        File fout = new File("C:/Users/ruzieljonm/Documents/Fouth Year/2019/Sonic2019/src/main/resources/combination.csv");
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        try (BufferedReader br = new BufferedReader(new FileReader("C:/Users/ruzieljonm/Desktop/rawid_distinct.txt"))) {
            String line;

            int i=0;
            while ((line = br.readLine()) != null) {
                if(i==videoids.size()-1){
                    i=0;
                }else{
                    System.out.println(i);
                    bw.write(line.toString() + "," + videoids.get(i));
                    bw.newLine();
                    i++;
                }

            }
        }
        bw.close();



    }

//    @RequestMapping("/readComCSV")
//    public void readCombinations() throws IOException {
//        String csvFile = "C:/Users/ruzieljonm/Desktop/combination.csv";
//        BufferedReader br = null;
//        String line = "";
//        String cvsSplitBy = ",";
//
//        ArrayList<Equivalent> equival = new ArrayList<>();
//
//        try {
//
//            br = new BufferedReader(new FileReader(csvFile));
//            while ((line = br.readLine()) != null) {
//
//                // use comma as separator
//                String[] country = line.split(cvsSplitBy);
//
////                System.out.println("Video ID [raw= " + country[0] + " , videoid_sonic=" + country[1] + "]");
//                Equivalent e = new Equivalent(country[0],country[1]);
//                equival.add(e);
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        //scan tsv
//        StringTokenizer st ;
//        BufferedReader TSVFile = new BufferedReader(new FileReader("C:/Users/ruzieljonm/Downloads/agegroup_4A.tsv"));
//        String dataRow = TSVFile.readLine(); // Read first line.
//        ArrayList<Video> videos = videoService.findAllVideo();
//
//        ArrayList<User> users = userService.findAllUsers();
//        while (dataRow != null){
//            st = new StringTokenizer(dataRow,"\t");
//            List<String> dataArray = new ArrayList<String>() ;
//            while(st.hasMoreElements()){
//                dataArray.add(st.nextElement().toString());
//            }
//
//
//
//
//
//
////            dataArray.remove(1);
//            Random rand = new Random();
////            videos.get(rand.nextInt(videos.size()));
//
//            dataArray.add("72223");
//            dataArray.add(videos.get(rand.nextInt(videos.size())).getVideoid());
//
//            Random r = new Random();
//            float random = 200 + r.nextFloat() * (250 - 200);
//            dataArray.add(String.valueOf(random));
//            dataArray.add(getLocalDate().toString());
//
//
//            final Random randomt = new Random();
//            final int millisInDay = 24*60*60*1000;
//            Time time = new Time((long)randomt.nextInt(millisInDay));
//
//            dataArray.add(time.toString());
//
//
//            UserHistory u = new UserHistory(dataArray.get(0), dataArray.get(1), dataArray.get(2), dataArray.get(3), dataArray.get(4), "1", dataArray.get(5));
////            u.setSeqid(dataArray.get(0));
////            u.setUserId(dataArray.get(1));
////            u.setVideoid(dataArray.get(2));
////            u.se
//
//            VideoDetails video = videoService.findVideoDetailsByVideoid(dataArray.get(2));
//            userService.saveUserHistory(u);
//            incrementagegroup(4, video.getGenre());
//            incrementpersonalitygroup(1, video.getGenre());
//
////            VidRatings vr = new VidRatings("3596", "wavemayols", dataArray.get(2), Integer.toString(ThreadLocalRandom.current().nextInt(1, 5)));
////            videoService.saveVidrating(vr);
//
//
//
//
//
//            for (String item:dataArray) {
//                System.out.print(item + "  ");
//            }
//            System.out.println(); // Print the data line.
//            dataRow = TSVFile.readLine(); // Read next line of data.
//        }
//        // Close the file once all data has been read.
//        TSVFile.close();
//
//        // End the printout with a blank line.
//        System.out.println();
//
//
////
////        try {
////            try (BufferedReader txtbr = new BufferedReader(new FileReader("C:/Users/ruzieljonm/Desktop/rawid_distinct.txt"))) {
////                String txtline;
////                while ((txtline = br.readLine()) != null) {
////                    Equivalent a = equival.stream()
////                            .filter(eq -> txtline.equals(eq.getJam_id()))
////                            .findAny()
////                            .orElse(null);
////
////
////
////
////                    UserHistory u = new UserHistory()//===============
////                }
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        //read tsv
////        StringTokenizer st ;
////        BufferedReader TSVFile = new BufferedReader(new FileReader("C:/Users/ruzieljonm/Downloads/agegroup_4A.tsv"));
////        String dataRow = TSVFile.readLine(); // Read first line.
//////        ArrayList<Video> videos = videoService.findAllVideo();
////
////        ArrayList<User> users = userService.findAllUsers();
////        while (dataRow != null){
////            st = new StringTokenizer(dataRow,"\t");
////            List<String> dataArray = new ArrayList<String>() ;
////            while(st.hasMoreElements()){
////                dataArray.add(st.nextElement().toString());
////            }
////
////            dataArray.remove(1);
////            Random rand = new Random();
//////            videos.get(rand.nextInt(videos.size()));
////
////            dataArray.add("72223");
////
////            dataArray.add(videos.get(rand.nextInt(videos.size())).getVideoid());
////
////            Random r = new Random();
////            float random = 200 + r.nextFloat() * (250 - 200);
////            dataArray.add(String.valueOf(random));
////            dataArray.add(getLocalDate().toString());
////
////
////            final Random randomt = new Random();
////            final int millisInDay = 24*60*60*1000;
////            Time time = new Time((long)randomt.nextInt(millisInDay));
////
////            dataArray.add(time.toString());
////
////
////            UserHistory u = new UserHistory(dataArray.get(0), dataArray.get(1), dataArray.get(2), dataArray.get(3), dataArray.get(4), "1", dataArray.get(5));
//////            u.setSeqid(dataArray.get(0));
//////            u.setUserId(dataArray.get(1));
//////            u.setVideoid(dataArray.get(2));
//////            u.se
////
////            VideoDetails video = videoService.findVideoDetailsByVideoid(dataArray.get(2));
////            userService.saveUserHistory(u);
////            incrementagegroup(4, video.getGenre());
////            incrementpersonalitygroup(1, video.getGenre());
////
//////            VidRatings vr = new VidRatings("3596", "wavemayols", dataArray.get(2), Integer.toString(ThreadLocalRandom.current().nextInt(1, 5)));
//////            videoService.saveVidrating(vr);
////
////
////
////
////
////            for (String item:dataArray) {
////                System.out.print(item + "  ");
////            }
////            System.out.println(); // Print the data line.
////            dataRow = TSVFile.readLine(); // Read next line of data.
////        }
////        // Close the file once all data has been read.
////        TSVFile.close();
//
//        // End the printout with a blank line.
//        System.out.println();
//
//
//
//
//
////        String findme = "TRZHIYD128F92DE932";
////        int pos = equival.indexOf(findme);
////        System.out.println("pos : " + pos);
////
////        System.out.println(equival.get(pos).getJam_id() + " --- " + equival.get(pos).getSonic_id());
//
////        Equivalent james = equival.stream()
////                .filter(eq -> findme.equals(eq.getJam_id()))
////                .findAny()
////                .orElse(null);
////
////        System.out.println("result : " + james.getJam_id() + "--" + james.getSonic_id());
//
//
//
//
//
//
//
//
//    }
//    @RequestMapping("/gotoplaylistF")
//    public String gotoPlaylistF(HttpSession session, Model model){
////        String currentTime = getTime();
//        String currentTime = "2:20:12";
//
//        ArrayList<String> sequenceids = userService.findDistinctSequenceId();
//        ArrayList<UserHistory>[] seqRules = (ArrayList<UserHistory>[])new ArrayList[sequenceids.size()];
//
//        for(int i=0; i<sequenceids.size();i++){
//            seqRules[i] = userService.findUserHistoryBySeqid(sequenceids.get(i).toString());
//            Collections.sort(seqRules[i], UserHistory.TimeComparator);
//        }
//
//
//        for(int i=0; i<sequenceids.size();i++){
//            boolean found=false;
//            for (int j = 0; j < seqRules[i].size(); j++) {
//                if (seqRules[i].get(j).getViewingTime().equals(currentTime.substring(0, currentTime.length() - 6))) {
//                    found = true;
//                    break;
//                } else {
//                    found = false;
//                }
//            }
//
//
//            if(found==false){
//                sequenceids.remove(i);
//            }
//        }
//
//        ArrayList<UserHistory>[] seqRulesFiltered = (ArrayList<UserHistory>[])new ArrayList[sequenceids.size()];
//
//        for(int i=0; i<sequenceids.size();i++){
//            seqRulesFiltered[i] = userService.findUserHistoryBySeqid(sequenceids.get(i).toString());
//            Collections.sort(seqRulesFiltered[i], UserHistory.TimeComparator);
//        }
//
//
//
//
//        for(int i=0; i<sequenceids.size();i++){
//            System.out.print("[" + sequenceids.get(i).toString() + "]     ");
//
//            for(int j=0; j<seqRulesFiltered[i].size(); j++){
//                if(seqRulesFiltered[i].get(j).getViewingStatus().equals("0")){
//                    seqRulesFiltered[i].remove(j);
//                }
//            }
//
//            for(int j=0; j<seqRules[i].size(); j++){
//                System.out.print(seqRules[i].get(j).getVideoid() + ", ");
//            }
//            System.out.println();
//        }
//
//
//
//        System.out.println("SIZE AFTER FILTERING : " + seqRulesFiltered.length);
//
//
//        ArrayList<String> uniqueVideoIDs = getUniqueVideoIDs(seqRules,sequenceids);
//
//        ArrayList<String> databaseRules = buildDBRules(seqRules,sequenceids);
//
//        ArrayList<ClassSequentialRules>[] seqrules = (ArrayList<ClassSequentialRules>[])new ArrayList[10];
//        seqrules[0] = new ArrayList<>();
//        for(String id: uniqueVideoIDs){
//            ClassSequentialRules seq = new ClassSequentialRules(id.toString(),calculateSupport(databaseRules,id.toString()),0);
//            seqrules[0].add(seq);
//        }
//
//        seqrules[0] = removeUnqualifiedForThreshold(seqrules[0]);
//        displaySeqrulesMeasures(seqrules[0]);
//
//        boolean flag= true;
//        int srIndex=1;
//        do{
//            seqrules[srIndex] = new ArrayList<>();
//            seqrules[srIndex] = buildRuleCombination(seqrules[srIndex-1],uniqueVideoIDs,databaseRules);
//            seqrules[srIndex] = removeUnqualifiedForThreshold(seqrules[srIndex]);
//            displaySeqrulesMeasures(seqrules[srIndex]);
//            if(seqrules[srIndex].get(0).getSupport()==0){
//                flag=false;
//
//            }else{
//                srIndex++;
//            }
//
//        }while (flag==true);
//        String[] parts = null;
//        for(int i=0; i<seqrules[srIndex-1].size(); i++){
////            System.out.println("The sequence that made it : " + seqrules[srIndex-1].get(i).getVideoIds() );
//            parts = seqrules[srIndex-1].get(0).getVideoIds().toString().split(", ");
//        }
//
//
//        for (String p : parts){
//            System.out.println("[pl]" + p.toString());
//        }
//
//
//        ArrayList<String> finalList = new ArrayList<String>(Arrays.asList(parts));
//
//        for(int i=0; i<seqRulesFiltered.length; i++){
//            for(int j=0; j<seqRulesFiltered[i].size(); j++){
//                if(!finalList.contains(seqRulesFiltered[i].get(j).getVideoid().toString())){
//                    finalList.add(seqRulesFiltered[i].get(j).getVideoid());
//                }
//            }
//        }
//        ArrayList<String> finalListest = new ArrayList<>();
//
//        finalListest = removeDuplicates(finalList);
////        for(int i=0; i<20; i++){
////            if(!finalListest.contains(finalList.get(i).toString())){
////                finalListest.add(finalList.get(i).toString());
////            }
////        }
//
//        ArrayList<Video> vids = new ArrayList<>();
//        for(int i=0; i<20; i++){
//            System.out.println("[ f ] - " + finalListest.get(i).toString() );
//            Video v = videoService.findVideoByVideoid(finalListest.get(i).toString());
//            Video nv = new Video(v.getVideoid(), v.getMvtitle(), v.getThumbnail());
//            vids.add(nv);
//            System.out.println(v.getMvtitle());
//
//        }
//
//
//        model.addAttribute("vididtoplay", vids.get(0).getVideoid());
//        model.addAttribute("plvids", vids);
//        return "VideoPlayerWithPlaylist";
//
//    }

    public float calculateConfidence(ArrayList<String> databaseRules, String seqToCheck, ArrayList<ClassSequentialRules> prevSeqrules, String findInPrev){
        float confidence=0;

        int support=0;
        for(int i=0; i<databaseRules.size(); i++){
            String newStr = seqToCheck.replaceAll(",", ".*");
            String tempPat = ".*" + newStr + ".*";
            Pattern p = Pattern.compile(tempPat);
            boolean b = false;


            Matcher m = p.matcher(databaseRules.get(i).toString());
            b = m.matches();
            if (b == true) {
                support++;
            }
        }
//        System.out.println( seqToCheck + "present:" + ((float)support/(float)databaseRules.size()) +"/" + supportPrevSet);
        float prevSup=0;
        for(ClassSequentialRules s: prevSeqrules){
            if(findInPrev.equals(s.getVideoIds())){
                prevSup = s.getSupport();
            }
        }
//        System.out.println(support + "/" + databaseRules.size() + "/" + prevSup);
        confidence = (support/(float)databaseRules.size())/prevSup;

//        System.out.println("STOP");
        return confidence;
    }


    public ArrayList<ClassSequentialRules> buildRuleCombination(ArrayList<ClassSequentialRules> seqrules, ArrayList<String> uniqueVideoIDs, ArrayList<String> databaseRules){
        ArrayList<ClassSequentialRules> seqres = new ArrayList<>();
        for(int j=0; j<seqrules.size(); j++){
//
//           String[] parts = seqrules.get(j).toString().split(", ");
//            if(Arrays.asList(parts).contains(   ))

            for(int i=0; i<uniqueVideoIDs.size(); i++) {
//                if(!s.getVideoIds().toString().contains(uniqueVideoIDs.get(i))) {
                ClassSequentialRules seq = new ClassSequentialRules(seqrules.get(j).getVideoIds() + " , " + uniqueVideoIDs.get(i),
                        calculateSupport(databaseRules, seqrules.get(j).getVideoIds() + " , " + uniqueVideoIDs.get(i)),
                        calculateConfidence(databaseRules, seqrules.get(j).getVideoIds() + " , " + uniqueVideoIDs.get(i), seqrules, seqrules.get(j).getVideoIds()));
                seqres.add(seq);
//                }
//                System.out.println(seqrules.get(j).getVideoIds() +", " +uniqueVideoIDs.get(i));
            }
        }
        return seqres;
    }

    public void displaySeqrulesMeasures(ArrayList<ClassSequentialRules> seqrules){
        for(int i=0; i<seqrules.size(); i++){
//            if(seqrules.get(i).getSupport()!=0) {
            System.out.println("{" + seqrules.get(i).getVideoIds() + "} : " + " support : " + seqrules.get(i).getSupport() +
                    "      confidence : " + seqrules.get(i).getConfidence()  );
//            }
        }

    }

    public ArrayList<ClassSequentialRules> removeUnqualifiedForThreshold(ArrayList<ClassSequentialRules> seqrules){
        ArrayList<ClassSequentialRules> res = new ArrayList<>();
        float threshold=0;
        ArrayList<Float> findMax= new ArrayList<>();
        for(int i=0; i<seqrules.size(); i++){
            threshold += seqrules.get(i).getSupport();
            findMax.add(seqrules.get(i).getSupport());
        }


        //threshold = (threshold/seqrules.size())*((float)4);
        threshold = Collections.max(findMax);
//        threshold = getSecMax(findMax);
//        threshold = threshold/seqrules.size();


        for(int i=0; i<seqrules.size(); i++){
            if(seqrules.get(i).getSupport()>=(threshold)){
                res.add(seqrules.get(i));
            }
        }
        System.out.println("T H R E S H O L D : " + threshold);

        return res;
    }

    public float getSecMax(ArrayList<Float> list){
        float secondLargest = (float) list.get(0);
        float largest = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if(list.get(i) > largest) {
                secondLargest = largest;
                largest = list.get(i);
            }
            if(list.get(i) > secondLargest && list.get(i) != largest) {
                secondLargest = list.get(i);
            }
        }
        System.out.print("Second biggest number ");
        return secondLargest;
    }

    public ArrayList<String> getUniqueVideoIDs(ArrayList<UserHistory>[] seqRules,  ArrayList<String> sequenceids){
        ArrayList<String> uniqueVidIDS = new ArrayList<>();

        for(int i=0; i<sequenceids.size();i++){
            for(int j=0; j<seqRules[i].size(); j++){
                if(uniqueVidIDS.contains(seqRules[i].get(j).getVideoid())){
//                    System.out.println("Already Existed!");
                }else{
                    uniqueVidIDS.add(seqRules[i].get(j).getVideoid());
                }

            }
        }

        return uniqueVidIDS;
    }

    public ArrayList<String> buildDBRules(ArrayList<UserHistory>[] seqRules,  ArrayList<String> sequenceids){
        ArrayList<String> databaseRules = new ArrayList<>();
        for(int i=0; i<sequenceids.size();i++){
            String asOne ="";
            for(int j=0; j<seqRules[i].size(); j++) {
                asOne = asOne + seqRules[i].get(j).getVideoid() + " , ";
            }
            databaseRules.add(asOne);

        }

        return  databaseRules;
    }

    public float calculateSupport(ArrayList<String> databaseRules, String seqToCheck ){
        int support=0;
        for(int i=0; i<databaseRules.size(); i++){
            String newStr = seqToCheck.replaceAll(",", ".*");
            String tempPat = ".*" + newStr + ".*";
            Pattern p = Pattern.compile(tempPat);
            boolean b = false;

//            System.out.println(tempPat);

            Matcher m = p.matcher(databaseRules.get(i).toString());
            b = m.matches();
            if (b == true) {
                support++;
            }
        }

        float fsupport = (float)support/(float)databaseRules.size();
//        System.out.println("dbrules size" + databaseRules.size());
//        System.out.println("SUPPORT : " + fsupport);
        return fsupport;

    }


    @RequestMapping("/outp")
    public String outPlaylist(){

        ArrayList<Playlist> p = videoService.findAllPlaylistByPlaylistID("plevening");

        ArrayList<Video> pl = new ArrayList<>();
        for(Playlist pli : p){
            pl.add(videoService.findVideoByVideoid(pli.getVideoID()));
        }

        for(Video v: pl){
            System.out.println(v.getMvtitle());
        }
        return "testing";
    }










    public class upnextItem{
        String order;
        String tag;

        public upnextItem(String order, String tag) {
            this.order = order;
            this.tag = tag;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    public class ClassSequentialRules {
        String videoIds;
        float support;
        float confidence;

        public ClassSequentialRules(String videoIds, float support, float confidence) {
            this.videoIds = videoIds;
            this.support = support;
            this.confidence = confidence;

        }

        public float getConfidence() {
            return confidence;
        }

        public void setConfidence(float confidence) {
            this.confidence = confidence;
        }



        public float getSupport() {
            return support;
        }

        public void setSupport(float support) {
            this.support = support;
        }

        public String getVideoIds() {
            return videoIds;
        }

        public void setVideoIds(String videoIds) {
            this.videoIds = videoIds;
        }
    }




//    @RequestMapping("/vplayerpl")
//    public String vplayerPL(){
//        return "VideoPlayerWithPlaylist";
//    }

}
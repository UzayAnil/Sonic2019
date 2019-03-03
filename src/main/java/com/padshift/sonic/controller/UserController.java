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
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.mapping.Array;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        if(userName.equals("admin") && userPass.equals("admin")){
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


    @RequestMapping("/gotoExplore")
    public String gotoExplore(Model model){
        ArrayList<Genre> genres = videoService.findAllGenre();

        model.addAttribute("genre", genres);
        return showExplore(model);
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
        float agetotalviews = useragegroup.getAlternativeMusic() + useragegroup.getCountryMusic() + useragegroup.getHiphopMusic() + useragegroup.getHouseMusic() + useragegroup.getPopMusic() + useragegroup.getReggaeMusic() + useragegroup.getReligiousMusic() + useragegroup.getRnbMusic() + useragegroup.getRockMusic();
        float personalitytotalviews = personalitygroup.getAlternativeMusic() + personalitygroup.getCountryMusic() + personalitygroup.getHiphopMusic() + personalitygroup.getHouseMusic() + personalitygroup.getPopMusic() + personalitygroup.getReggaeMusic() + personalitygroup.getReligiousMusic() + personalitygroup.getRnbMusic() + personalitygroup.getRockMusic();
        float totalpersonality1 = personalities.get(0).getAlternativeMusic() + personalities.get(0).getCountryMusic() + personalities.get(0).getHiphopMusic() + personalities.get(0).getHouseMusic() + personalities.get(0).getPopMusic() + personalities.get(0).getReggaeMusic() +personalities.get(0).getReligiousMusic() + personalities.get(0).getRnbMusic() + personalities.get(0).getRockMusic();
        float totalpersonality2 = personalities.get(1).getAlternativeMusic() + personalities.get(1).getCountryMusic() + personalities.get(1).getHiphopMusic() + personalities.get(1).getHouseMusic() + personalities.get(1).getPopMusic() + personalities.get(1).getReggaeMusic() +personalities.get(1).getReligiousMusic() + personalities.get(1).getRnbMusic() + personalities.get(1).getRockMusic();
        float totalviews = totalpersonality1 + totalpersonality2;
        float userInput = temp;
        float genreAgePop,genreAgeRock, genreAgeAlt, genreAgeRBS, genreAgeCntry, genreAgeHouse, genreAgeReg, genreAgeRel, genreAgeHH;
        float genweight;

        float genrePTPop,genrePTRock, genrePTAlt, genrePTRBS, genrePTCntry, genrePTHouse, genrePTReg, genrePTRel, genrePTHH;

        float genreAge = 0;
        float genrePT =0;
        float genreWT = 0;

        if(genreid == 1 && agetotalviews > 9){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),1).getPrefWeight();
            genreAge = (float) useragegroup.getPopMusic()/agetotalviews;
            genrePT = (float) personalitygroup.getPopMusic()/personalitytotalviews;
            genreWT = (float) (personalities.get(0).getPopMusic()+personalities.get(1).getPopMusic())/totalviews;
        }
        if(genreid == 2 && agetotalviews > 9){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),2).getPrefWeight();
            genreAge = (float) useragegroup.getRockMusic()/agetotalviews;
            genrePT = (float) personalitygroup.getRockMusic()/personalitytotalviews;
            genreWT = (float) (personalities.get(0).getPopMusic()+personalities.get(1).getPopMusic())/totalviews;
        }

        if(genreid == 3  && agetotalviews > 9){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),3).getPrefWeight();
            genreAge = (float) useragegroup.getAlternativeMusic()/agetotalviews;
            genrePT = (float) personalitygroup.getAlternativeMusic()/personalitytotalviews;
            genreWT = (float) (personalities.get(0).getPopMusic()+personalities.get(1).getPopMusic())/totalviews;
        }
        if(genreid == 4  && agetotalviews > 9){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),4).getPrefWeight();
            genreAge = (float) useragegroup.getRnbMusic()/agetotalviews;
            genrePT = (float) personalitygroup.getRnbMusic()/personalitytotalviews;
            genreWT = (float) (personalities.get(0).getPopMusic()+personalities.get(1).getPopMusic())/totalviews;
        }
        if(genreid == 5  && agetotalviews > 9){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),5).getPrefWeight();
            genreAge = (float) useragegroup.getCountryMusic()/agetotalviews;
            genrePT = (float) personalitygroup.getCountryMusic()/personalitytotalviews;
            genreWT = (float) (personalities.get(0).getPopMusic()+personalities.get(1).getPopMusic())/totalviews;
        }
        if(genreid == 6  && agetotalviews > 9){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),6).getPrefWeight();
            genreAge = (float) useragegroup.getCountryMusic()/agetotalviews;
            genrePT = (float) personalitygroup.getCountryMusic()/personalitytotalviews;
            genreWT = (float) (personalities.get(0).getPopMusic()+personalities.get(1).getPopMusic())/totalviews;
        }
        if(genreid == 7  && agetotalviews > 9){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),7).getPrefWeight();
            genreAge = (float) useragegroup.getReggaeMusic()/agetotalviews;
            genrePT = (float) personalitygroup.getReggaeMusic()/personalitytotalviews;
            genreWT = (float) (personalities.get(0).getPopMusic()+personalities.get(1).getPopMusic())/totalviews;
        }
        if(genreid == 8  && agetotalviews > 9){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),8).getPrefWeight();
            genreAge = (float) useragegroup.getReligiousMusic()/agetotalviews;
            genrePT = (float) personalitygroup.getReligiousMusic()/personalitytotalviews;
            genreWT = (float) (personalities.get(0).getPopMusic()+personalities.get(1).getPopMusic())/totalviews;
        }
        if(genreid == 9  && agetotalviews > 9){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),9).getPrefWeight();
            genreAge = (float) useragegroup.getHiphopMusic()/agetotalviews;
            genrePT = (float) personalitygroup.getHiphopMusic()/personalitytotalviews;
            genreWT = (float) (personalities.get(0).getPopMusic()+personalities.get(1).getPopMusic())/totalviews;
        }

        if(agetotalviews == 9){
            genreAge = 1;
        }
        if(personalitytotalviews == 9){
            genrePT = 1;
        }
        if(totalviews == 18){
            genreWT = 1;
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

        int likes = 0;
        System.out.println("Genre ID: "+genreid);
        System.out.println("Total Views: "+agetotalviews);
        System.out.println("Personal Total Views"+personalitytotalviews);
        System.out.println("UserInput: "+temp);
        System.out.println("genreAge: "+genreAge);
        System.out.println("genrePT: "+genrePT);

        System.out.println((temp/10)*.4);
        System.out.println((genreAge)*.25);
        System.out.println((genrePT)*.25);
        System.out.println((genreWT)*.1);

        genweight = (float) (((temp/10)*.4)+((genreAge)*.25)+((genrePT)*.25)+((genreWT)*.1));

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

        String[] users = (String[]) session.getAttribute("similarusers");
        System.out.println("SIMILAR USERS");
        for (int i = 0; i < users.length; i++) {
            System.out.println(users[i]+"======");
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


//        ArrayList<VideoDetails> videos = videoService.findAllVideoDetails();
//        ArrayList<RecVid> recVideos = new ArrayList<>();
//
//        for( VideoDetails vid : videos){
//            RecVid rv = new RecVid();
//            rv.setVideoid(vid.getVideoid());
//            rv.setTitle(vid.getTitle());
//            rv.setArtist(vid.getArtist());
//            rv.setGenre(vid.getGenre());
//            rv.setViewCount(vid.getViewCount());
//
//            rv.setWeight(computeInitialVideoWeight(vid,user));
//            recVideos.add(rv);
//        }
//
//        Collections.sort(recVideos);
//
//        //SAVE THE RECVIDEOS
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

        String[] users = (String[]) session.getAttribute("similarusers");
        System.out.println("SIMILAR USERS");
        for (int i = 0; i < users.length; i++) {
            System.out.println(users[i]+"======");
        }

        System.out.println(" S E S S I O N : " + session.getAttribute("sessionid"));
        model.addAttribute("r1", vr1);
        model.addAttribute("r2", vr2);
        model.addAttribute("r3", vr3);
        model.addAttribute("r4", vr4);


        return "Homepage";



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
        ArrayList<String> allusers = new ArrayList<>();
//        ArrayList<User> users = userService.findOtherUser(currentuser);
        ArrayList<Integer> users = userService.findDistinctUserfromUserPref(Integer.parseInt(currentuserId));
        ArrayList<Integer> genreIds = userService.findDistinctGenre();
        ArrayList<Float> genreweights = new ArrayList<>();
        float gweight = 0;
        int current = userService.findUserIdByUserId(Integer.parseInt(currentuserId));
        String currentU = String.valueOf(current);
        System.out.println(currentU);
        allusers.add(currentU);

        for (int i=0; i < users.size(); i++){
            allusers.add(users.get(i).toString());
        }

        for (int i = 0; i < allusers.size(); i++) {
            System.out.println(allusers.get(i));
        }

        for (int i = 0; i < genreIds.size(); i++) {
            System.out.printf("%15s", genreIds.get(i));
        }
        System.out.println();
        for (int i = 0; i < allusers.size(); i++) {
            System.out.println(allusers.get(i));
            for (int j = 0; j < genreIds.size(); j++) {
                UserPreference genweight = userService.findUserPreferenceByUserIdAndGenreId(Integer.parseInt(allusers.get(i)), genreIds.get(j));
                gweight = genweight.getGenreWeight();
                System.out.println("-----"+allusers.get(i)+" xx "+genreIds.get(j)+" xx "+ gweight);
//                if(gweight == null){
//                    gweight = 0;
//                }
                genreweights.add(gweight);
            }
        }

        float[] currentrow = new float[genreIds.size()];
        float[] otherrow = new float[genreweights.size()];

        int count = 0;

        for (int i = 0; i < genreIds.size(); i++) {
            System.out.printf("%15s", genreIds.get(i));
        }
        System.out.println();
        for (int i = 0; i < allusers.size(); i++) {
            System.out.printf("%s", allusers.get(i));
            for (int j = 0; j < genreIds.size(); j++) {
                System.out.printf("%15f", genreweights.get(count));
                if( i == 0 ){
                    currentrow[j] = genreweights.get(count);
                }
                else{
                    otherrow[count] = genreweights.get(count);
                }
                count++;
            }
            System.out.println();
        }
        float[] distancevalue = new float[allusers.size()];
        float[] otheruserrow = new float[genreweights.size()];
        count = genreIds.size();
        for (int i = 1; i < allusers.size(); i++) {
            for (int j = 0; j < genreIds.size(); j++) {
                otheruserrow[j] = otherrow[count];
                count++;
            }
            distancevalue[i] =finddistanceValue(currentrow, otheruserrow, currentuserId, allusers.get(i), genreIds);
        }
        String[] arrUser = new String[allusers.size()];
        for (int i = 0; i < arrUser.length; i++) {
            arrUser[i] = allusers.get(i);
        }
        float tempGenweight = 0;
        String tempuser;
        ArrayList<String> similarUsers = new ArrayList<>();
        for (int i = 0; i < allusers.size(); i++) {
            for (int j = i+1; j < arrUser.length; j++) {
                if (distancevalue[i] > distancevalue[j])
                {
                    tempGenweight = distancevalue[i];
                    tempuser = arrUser[i];
                    distancevalue[i] = distancevalue[j];
                    arrUser[i] = arrUser[j];
                    distancevalue[j] = tempGenweight;
                    arrUser[j] = tempuser;
                }
            }
        }

        String[] twosimusers = new String[2];
        float[] twodistancevalue = new float[2];
        int j=0;
        for (int i = 0; i < 2; i++) {
            twodistancevalue[j] = distancevalue[i+1];
            twosimusers[j] = arrUser[i+1];
            j++;
        }

        for (int i = 0; i < distancevalue.length; i++) {
            System.out.println(arrUser[i]+"xxxx"+distancevalue[i]);
        }
        session.setAttribute("similarusers", twosimusers);
        session.setAttribute("distancevalue", twodistancevalue);
    }

    public float finddistanceValue(float[] currentuserGenweight, float[] otheruserGenweight, String currentuser, String otheruser, ArrayList<Integer> genIds){
        float genWeight = 0;
        float sum = 0, sub1, square;
        for (int i = 0; i < genIds.size(); i++) {
            System.out.printf("%15s", genIds.get(i));
        }
        System.out.println();
        System.out.printf("%s", currentuser);
        for (int i = 0; i < currentuserGenweight.length; i++) {
            System.out.printf("%15f", currentuserGenweight[i]);
        }
        System.out.println();
        System.out.printf("%s", otheruser);
        for (int i = 0; i < genIds.size(); i++) {
            System.out.printf("%15f", otheruserGenweight[i]);
        }
        System.out.println();
        for (int i = 0; i < genIds.size(); i++) {
            sub1 = currentuserGenweight[i] - otheruserGenweight[i];
            sum += sub1 * sub1;
        }
        System.out.println(Math.sqrt(sum));
        float ret = (float) Math.sqrt(sum);

        return ret;
    }

    public float computeInitialVideoWeight(VideoDetails video, User user){
        System.out.println(video.getTitle()+"==========="+video.getLikes());
        int agegroup = user.getAgecriteriaId();
        AgeCriteria useragegroup = userService.findByAgeCriteriaId(agegroup);
        int personality = user.getPersonalitycriteriaId();
        PersonalityCriteria personalitygroup = userService.findByPersonalityCriteriaId(personality);
        float totalviews = useragegroup.getAlternativeMusic() + useragegroup.getCountryMusic() + useragegroup.getHiphopMusic() + useragegroup.getHouseMusic() + useragegroup.getPopMusic() + useragegroup.getReggaeMusic() + useragegroup.getReligiousMusic() + useragegroup.getRnbMusic() + useragegroup.getRockMusic();
        float personalitytotalviews = personalitygroup.getAlternativeMusic() + personalitygroup.getCountryMusic() + personalitygroup.getHiphopMusic() + personalitygroup.getHouseMusic() + personalitygroup.getPopMusic() + personalitygroup.getReggaeMusic() + personalitygroup.getReligiousMusic() + personalitygroup.getRnbMusic() + personalitygroup.getRockMusic();
        float vidWeight;
        float userInput = 0;

        float genreAgePop,genreAgeRock, genreAgeAlt, genreAgeRBS, genreAgeCntry, genreAgeHouse, genreAgeReg, genreAgeRel, genreAgeHH;

        float genrePTPop,genrePTRock, genrePTAlt, genrePTRBS, genrePTCntry, genrePTHouse, genrePTReg, genrePTRel, genrePTHH;

        ArrayList<UserPreference> userPref = userService.findAllGenrePreferenceByUserId(user.getUserId());

        float genreAge = 0;
        float genrePT =0, genweight=0;
        int genreid = 0;
//
//        userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),1);
//        System.out.println("prefweight:" + userInput.getPrefWeight());
//
//        float upPop, upRock, upAlt, upRB, upCntry, upHouse, upReg, upRel, upHH;
//        upPop = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),1).getPrefWeight();
//

        if(video.getGenre().equals("Pop Music")){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),1).getPrefWeight();
            genreid = 1;
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(), genreid);
            genweight = userpref.getGenreWeight();
            genreAge = (float) useragegroup.getPopMusic()/totalviews;
            genrePT = (float) personalitygroup.getPopMusic()/personalitytotalviews;
        }
        if(video.getGenre().equals("Rock Music")){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),2).getPrefWeight();
            genreid = 2;
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(), genreid);
            genweight = userpref.getGenreWeight();
            genreAge = (float) useragegroup.getRockMusic()/totalviews;
            genrePT = (float) personalitygroup.getRockMusic()/personalitytotalviews;
        }

        if(video.getGenre().equals("Alternative Music")){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),3).getPrefWeight();
            genreid = 3;
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(), genreid);
            genweight = userpref.getGenreWeight();
            genreAge = (float) useragegroup.getAlternativeMusic()/totalviews;
            genrePT = (float) personalitygroup.getAlternativeMusic()/personalitytotalviews;
        }
        if(video.getGenre().equals("R&B/Soul Music")){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),4).getPrefWeight();
            genreid = 4;
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(), genreid);
            genweight = userpref.getGenreWeight();
            genreAge = (float) useragegroup.getRnbMusic()/totalviews;
            genrePT = (float) personalitygroup.getRnbMusic()/personalitytotalviews;
        }
        if(video.getGenre().equals("Country Music")){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),5).getPrefWeight();
            genreid = 5;
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(), genreid);
            genweight = userpref.getGenreWeight();
            genreAge = (float) useragegroup.getCountryMusic()/totalviews;
            genrePT = (float) personalitygroup.getCountryMusic()/personalitytotalviews;
        }
        if(video.getGenre().equals("House Music")){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),6).getPrefWeight();
            genreid = 6;
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(), genreid);
            genweight = userpref.getGenreWeight();
            genreAge = (float) useragegroup.getCountryMusic()/totalviews;
            genrePT = (float) personalitygroup.getHouseMusic()/personalitytotalviews;
        }
        if(video.getGenre().equals("Reggae Music")){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),7).getPrefWeight();
            genreid = 7;
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(), genreid);
            genweight = userpref.getGenreWeight();
            genreAge = (float) useragegroup.getReggaeMusic()/totalviews;
            genrePT = (float) personalitygroup.getReggaeMusic()/personalitytotalviews;
        }
        if(video.getGenre().equals("Religious Music")){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),8).getPrefWeight();
            genreid = 8;
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(), genreid);
            genweight = userpref.getGenreWeight();
            genreAge = (float) useragegroup.getReligiousMusic()/totalviews;
            genrePT = (float) personalitygroup.getReligiousMusic()/personalitytotalviews;
        }
        if(video.getGenre().equals("Hip-Hop/Rap Music")){
            userInput = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(),9).getPrefWeight();
            genreid = 9;
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId(user.getUserId(), genreid);
            genweight = userpref.getGenreWeight();
            genreAge = (float) useragegroup.getHiphopMusic()/totalviews;
            genrePT = (float) personalitygroup.getHiphopMusic()/personalitytotalviews;
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

        if(genweight == 0){
            genweight = (float) (((userInput/10)*.5)+((genreAge)*.25)+((genrePT)*.25));
        }
        System.out.println("Total: "+totalviews);
        System.out.println(userInput);
        System.out.println(genreAge);

        System.out.println(userInput*.5);
        System.out.println(genreAge*.5);

        System.out.println(genweight);
        vidWeight= (float) (genweight * likes);
        System.out.println("VID WEIGHT : " + vidWeight);

        return vidWeight;
    }



    @RequestMapping("/gotoPlayer")
    public String gotoPlayer(HttpServletRequest request, Model model, HttpSession session){
        String vididtoplay = request.getParameter("clicked");
        String videoWatched = request.getParameter("videoWatched");
        String[] simusers = (String[]) session.getAttribute("similarusers");

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
                VVD vid = new VVD(videoList.get(i).getVideoid(), videoList.get(i).getTitle(), videoList.get(i).getArtist(), videoList.get(i).getGenre(), videoList.get(i).getDate(),"https://i.ytimg.com/vi/" + videoList.get(i).getVideoid() + "/mqdefault.jpg");
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
        String[] simusers = (String[]) session.getAttribute("similarusers");
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
        try{
            Genre findgenre = videoService.findByGenreName(video.getGenre());
            UserPreference userpref = userService.findUserPreferenceByUserIdAndGenreId((Integer) session.getAttribute("userid"), findgenre.getGenreId());
            incrementagegroup(useragegroup, video.getGenre());
            incrementpersonalitygroup(personalitygroup, video.getGenre());
            updategenreWeight(findgenre.getGenreId(), (Integer) session.getAttribute("userid"), userpref.getPrefWeight());

        }catch (Exception e){

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
                VVD vid = new VVD(videoList.get(i).getVideoid(), videoList.get(i).getTitle(), videoList.get(i).getArtist(), videoList.get(i).getGenre(), videoList.get(i).getDate(),"https://i.ytimg.com/vi/" + videoList.get(i).getVideoid() + "/mqdefault.jpg");
                vr1.add(vid);
                vid = null;
            }
        }

//        for (int i = 0; i < 6; i++) {
//            VVD vid = new VVD(videoList.get(i).getVideoid(), videoList.get(i).getTitle(), videoList.get(i).getArtist(), videoList.get(i).getGenre(), videoList.get(i).getDate(),"https://i.ytimg.com/vi/" + videoList.get(i).getVideoid() + "/mqdefault.jpg");
//            vr1.add(vid);
//            vid = null;
//        }

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

    private void incrementpersonalitygroup(int personalitygroup, String genre) {
        PersonalityCriteria personalitycriteria = userService.findByPersonalityCriteriaId(personalitygroup);
        if(genre.contains("Pop")){
            personalitycriteria.setPopMusic(personalitycriteria.getPopMusic()+1);
        }
        if(genre.contains("House")){
            personalitycriteria.setPopMusic(personalitycriteria.getHouseMusic()+1);
        }
        if(genre.contains("Alternative")){
            personalitycriteria.setPopMusic(personalitycriteria.getAlternativeMusic()+1);
        }
        if(genre.contains("Reggae")){
            personalitycriteria.setPopMusic(personalitycriteria.getReggaeMusic()+1);
        }
        if(genre.contains("R&B/Soul")){
            personalitycriteria.setPopMusic(personalitycriteria.getRnbMusic()+1);
        }
        if(genre.contains("Religious")){
            personalitycriteria.setPopMusic(personalitycriteria.getReligiousMusic()+1);
        }
        if(genre.contains("Country")){
            personalitycriteria.setPopMusic(personalitycriteria.getCountryMusic()+1);
        }
        if(genre.contains("Rock")){
            personalitycriteria.setPopMusic(personalitycriteria.getRockMusic()+1);
        }
        if(genre.contains("Hip-Hop/Rap")){
            personalitycriteria.setPopMusic(personalitycriteria.getHiphopMusic()+1);
        }
        userService.savePersonalityCriteria(personalitycriteria);
    }

    public void incrementagegroup(int agegroup, String genre){
        AgeCriteria agecriteria = userService.findByAgeCriteriaId(agegroup);
        if(genre.contains("Pop")){
            agecriteria.setPopMusic(agecriteria.getPopMusic()+1);
        }
        if(genre.contains("House")){
            agecriteria.setPopMusic(agecriteria.getHouseMusic()+1);
        }
        if(genre.contains("Alternative")){
            agecriteria.setPopMusic(agecriteria.getAlternativeMusic()+1);
        }
        if(genre.contains("Reggae")){
            agecriteria.setPopMusic(agecriteria.getReggaeMusic()+1);
        }
        if(genre.contains("R&B/Soul")){
            agecriteria.setPopMusic(agecriteria.getRnbMusic()+1);
        }
        if(genre.contains("Religious")){
            agecriteria.setPopMusic(agecriteria.getReligiousMusic()+1);
        }
        if(genre.contains("Country")){
            agecriteria.setPopMusic(agecriteria.getCountryMusic()+1);
        }
        if(genre.contains("Rock")){
            agecriteria.setPopMusic(agecriteria.getRockMusic()+1);
        }
        if(genre.contains("Hip-Hop/Rap")){
            agecriteria.setPopMusic(agecriteria.getHiphopMusic()+1);
        }
        userService.saveAgeCriteria(agecriteria);
    }

    public String[] cosineMatrix(String currentuserId, String[] simusers){
        ArrayList<String> allusers = new ArrayList<>();
//        ArrayList<User> users = userService.findOtherUser(currentuser);
        ArrayList<String> users = videoService.findDistinctUser(currentuserId);
        ArrayList<String> vidhistID = videoService.findDistinctVidfromVidrating();
        ArrayList<String> videohist = new ArrayList<>();
        ArrayList<String> videorating = new ArrayList<>();
        String uhist;
        String currentU = userService.findCurrentByUserId(currentuserId);
        System.out.println(currentU);
        allusers.add(currentU);

        int count = 0;

        for (int i=0; i < simusers.length; i++){
            allusers.add(simusers[i]);
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
                System.out.printf("%15s", videorating.get(count));
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

        double[] cosineValue = new double[allusers.size()];
        double similarUser = 0;
        double temp;
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
        for (int i = 0; i < arrUser.length; i++) {
            System.out.println(arrUser[i]+": "+cosineValue[i]);
        }
//        System.out.println(simUser);
//        System.out.println(similarUser);
        String[] recommVids = ratingPrediction(currentuserRow, allusers.get(0), arrUser, cosineValue, videohist);
        return recommVids;
    }

    public double cosineSimilarity(String[] currentuserRatings, String[] otheruserRatings, String currentuser, String otheruser, ArrayList<String> allvideo){
        double nume = 0;
        double denum = 0;
        double cosineresult = 0;
        double multiplier1 = 0, multiplier2 = 0;
        System.out.println(allvideo.size()+" "+currentuserRatings.length+" "+otheruserRatings.length);
        System.out.println("=====================================================");
        for (int i=0; i < allvideo.size(); i++){
            System.out.printf("%15s", allvideo.get(i));
        }
        System.out.println();
        System.out.print(currentuser);
        for (int i = 0; i < currentuserRatings.length; i++) {
            System.out.printf("%13s", currentuserRatings[i]);
        }
        System.out.println();
        System.out.print(otheruser);
        for (int i = 0; i < otheruserRatings.length; i++) {
            System.out.printf("%13s", otheruserRatings[i]);
        }

        for (int i = 0; i < allvideo.size(); i++) {
            nume += Double.parseDouble(currentuserRatings[i])*Double.parseDouble(otheruserRatings[i]);
            multiplier1 += Double.parseDouble(currentuserRatings[i]) * Double.parseDouble(currentuserRatings[i]);
            multiplier2 += Double.parseDouble(otheruserRatings[i]) * Double.parseDouble(otheruserRatings[i]);
        }

        System.out.println("");
        System.out.println("NUMERATOR: " + nume);
        System.out.println("MULTIPLIER1: "+ multiplier1);
        System.out.println("MULTIPLIER2: "+ multiplier2);
        System.out.println("SQUARE1: "+ Math.sqrt(multiplier1));
        System.out.println("SQUARE2: "+ Math.sqrt(multiplier2));
        denum = Math.sqrt(multiplier1) * Math.sqrt(multiplier2);
        System.out.println("DENUMERATOR: "+ denum);
        cosineresult = nume/denum;
        System.out.println("RESULT: "+ cosineresult);
        System.out.println();

//        System.out.println((int)Math.sqrt(25));

        return cosineresult;
    }

    public String[] ratingPrediction(String[] currentuserRatings, String currentuser, String[] otheruser, double[] cosineValue, ArrayList<String> allvideo){
        String uhist;
        double nume = 0;
        double denum = 0;
        double[] predictedRate = new double[allvideo.size()];
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
            System.out.printf("%13s", currentuserRatings[i]);
        }
        System.out.println();
        for (int i = 0; i < otheruser.length-1; i++) {
            System.out.print(otheruser[i]);
            for (int j = 0; j < allvideo.size(); j++) {
                System.out.printf("%13s", otherUserRating[i][j]);
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
        double temp = 0;
        String tempvidId = "";
        for (int i = 0; i < predictedVidId.length; i++) {
//            System.out.println(predictedVidId[i]+": "+predictedRate[i]);
            for (int j = i + 1; j < predictedVidId.length; j++)
            {
                if (predictedRate[i] < predictedRate[j])
                {
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


    @RequestMapping("/latestExplore")
    public String showLatestExplore(HttpSession session, Model model){
//        String username = session.getAttribute("username").toString();
//        model.addAttribute("username", username);

        ArrayList<VideoDetails> recVideos = videoService.findAllVideoDetails();

        Collections.shuffle(recVideos);
        ArrayList<RecVid> vr1 = new ArrayList<>();
        for(int i=0; i<=5; i++) {
            RecVid vid1 = new RecVid(recVideos.get(i).getVideoid(), recVideos.get(i).getTitle(), recVideos.get(i).getArtist(), recVideos.get(i).getGenre(), "https://i.ytimg.com/vi/" + recVideos.get(i).getVideoid() + "/mqdefault.jpg");
            vr1.add(vid1);
            vid1 = null;
        }

        model.addAttribute("r1", vr1);

        return "LatestExplore";
    }


    //seqrule

    @RequestMapping("/playPlaylist")
    public String playThisPlaylist(HttpServletRequest request, Model model, HttpSession session){
        String choiceOfPlaylist = request.getParameter("choice");
        ArrayList<Playlist> playListVideos = new ArrayList<>();
        if(choiceOfPlaylist.equals("1")){
            System.out.println("M O R N I N G - A F T  E R N O O N   P L  A Y L I S T");
            playListVideos = videoService.findAllPlaylistByPlaylistID("plmornaft");
        }else if(choiceOfPlaylist.equals("2")){
            System.out.println("E V E N I N G   P L  A Y L I S T");
            playListVideos = videoService.findAllPlaylistByPlaylistID("plevening");
        }else{
            playListVideos = videoService.findAllPlaylistByPlaylistID("plgeneral");
        }

        ArrayList<Video> videos = new ArrayList<>();
        for(Playlist p: playListVideos){
            videos.add(videoService.findVideoByVideoid(p.getVideoID()));
            System.out.println("[PL] - " + p.getVideoID());
        }





        model.addAttribute("vididtoplay", videos.get(0).getVideoid());

//        model.addAttribute("vidtitle", videos.get(0).getMvtitle());
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


//        ArrayList<String> plIDs = videoService.findDistinctPlaylistID();
//        ArrayList<Playlist> vidids = videoService.findAllPlaylistByPlaylistID(plIDs.get(0).toString());
//
//        ArrayList<Video> plvids = new ArrayList<>();
//        for(Playlist p : vidids){
//            Video v = new Video();
//            v= videoService.findVideoByVideoid(p.getVideoID().toString());
//            plvids.add(v);
//        }

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

//        ArrayList<Video> allvids = videoService.findAllVideo();
//        ArrayList<VideoDetails> vids = new ArrayList<>();
//        for(Video s: allvids) {
//            VideoDetails v = videoService.findVideoDetailsByVideoid(s.getVideoid());
//            vids.add(v);
//        }

////        Collections.sort(vids);
//        ArrayList<VideoDetails> plvids = new ArrayList<>();
//        for(int i=0; i<50; i++){
//            plvids.add(vids.get(i));
//        }
//        ArrayList<Video> plvidsf = new ArrayList<>();
//
//        for(VideoDetails v : plvids){
//            plvidsf.add(( videoService.findVideoByVideoid(v.getVideoid()));
//        }

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


//        ArrayList<String> plIDs = videoService.findDistinctPlaylistID();
//        ArrayList<Playlist> vidids = videoService.findAllPlaylistByPlaylistID(plIDs.get(0).toString());
//
//        ArrayList<Video> plvids = new ArrayList<>();
//        for(Playlist p : vidids){
//            Video v = new Video();
//            v= videoService.findVideoByVideoid(p.getVideoID().toString());
//            plvids.add(v);
//        }

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




//        Video v = videoService.findVideoByVideoid(playthisvid);

//        model.addAttribute("vidtitle", v.getMvtitle());

        model.addAttribute("vididtoplay", playthisvid);

        model.addAttribute("plvids", plvids);

        return "VideoPlayerWithPlaylist";
    }


    @RequestMapping("/gotoplaylistF")
    public String gotoPlaylistF(HttpSession session, Model model){
        String currentTime = getTime();


        ArrayList<String> sequenceids = userService.findDistinctSequenceId();
        ArrayList<UserHistory>[] seqRules = (ArrayList<UserHistory>[])new ArrayList[sequenceids.size()];

        for(int i=0; i<sequenceids.size();i++){
            seqRules[i] = userService.findUserHistoryBySeqid(sequenceids.get(i).toString());
            Collections.sort(seqRules[i], UserHistory.TimeComparator);
        }


        for(int i=0; i<sequenceids.size();i++){
            boolean found=false;
                for (int j = 0; j < seqRules[i].size(); j++) {
                    if (seqRules[i].get(j).getViewingTime().equals(currentTime.substring(0, currentTime.length() - 6))) {
                        found = true;
                        break;
                    } else {
                        found = false;
                    }
                }


            if(found==false){
               sequenceids.remove(i);
            }
        }

        ArrayList<UserHistory>[] seqRulesFiltered = (ArrayList<UserHistory>[])new ArrayList[sequenceids.size()];

        for(int i=0; i<sequenceids.size();i++){
            seqRulesFiltered[i] = userService.findUserHistoryBySeqid(sequenceids.get(i).toString());
            Collections.sort(seqRulesFiltered[i], UserHistory.TimeComparator);
        }
        //CHECK BALIK SA EVLUATION SA O PLSSSSSSSSSSSSS



        for(int i=0; i<sequenceids.size();i++){
            System.out.print("[" + sequenceids.get(i).toString() + "]     ");

            for(int j=0; j<seqRulesFiltered[i].size(); j++){
                if(seqRulesFiltered[i].get(j).getViewingStatus().equals("0")){
                    seqRulesFiltered[i].remove(j);
                }
            }

            for(int j=0; j<seqRules[i].size(); j++){
                System.out.print(seqRules[i].get(j).getVideoid() + ", ");
            }
            System.out.println();
        }



        System.out.println("SIZE AFTER FILTERING : " + seqRulesFiltered.length);


        ArrayList<String> uniqueVideoIDs = getUniqueVideoIDs(seqRules,sequenceids);

        ArrayList<String> databaseRules = buildDBRules(seqRules,sequenceids);

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
        ArrayList<String> finalListest = new ArrayList<>();
        for(int i=0; i<seqRulesFiltered.length; i++){
            for(int j=0; j<seqRulesFiltered[i].size(); j++){
                if(!finalList.contains(seqRulesFiltered[i].get(j).getVideoid().toString())){
                    finalList.add(seqRulesFiltered[i].get(j).getVideoid());
                }
            }
        }

        for(String s: finalList){
            if(!finalListest.contains(s.toString())){
                finalListest.add(s);
            }
        }


        ArrayList<Video> vids = new ArrayList<>();
        for(int i=0; i<20; i++){
            System.out.println("[ f ] - " + finalList.get(i).toString() );
            Video v = videoService.findVideoByVideoid(finalList.get(i).toString());
            Video nv = new Video(v.getVideoid(), v.getMvtitle(), v.getThumbnail());
            vids.add(nv);

        }






//        return "testing";
        model.addAttribute("vididtoplay", vids.get(0).getVideoid());
        model.addAttribute("plvids", vids);
        return "VideoPlayerWithPlaylist";

    }

    public float calculateConfidence(ArrayList<String> databaseRules, String seqToCheck, ArrayList<ClassSequentialRules> prevSeqrules, String findInPrev){
        float confidence=0;

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
        for(ClassSequentialRules s: seqrules){
            for(int i=0; i<uniqueVideoIDs.size(); i++) {
                ClassSequentialRules seq = new ClassSequentialRules(s.getVideoIds() +" , " +uniqueVideoIDs.get(i),
                        calculateSupport(databaseRules,s.getVideoIds() +" , " +uniqueVideoIDs.get(i)),
                        calculateConfidence(databaseRules,s.getVideoIds() +" , " +uniqueVideoIDs.get(i),seqrules,s.getVideoIds()));
                seqres.add(seq);
//                System.out.println(s.getVideoIds() +", " +uniqueVideoIDs.get(i));
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

//        for(int i=0; i<seqrules.size(); i++){
//            if(seqrules.get(i).getSupport()>(threshold)){
//                res.add(seqrules.get(i));
//            }
//        }

        for(int i=0; i<seqrules.size(); i++){
            if(seqrules.get(i).getSupport()==(threshold)){
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
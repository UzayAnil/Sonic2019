package com.padshift.sonic.controller;

import com.padshift.sonic.entities.*;

import com.padshift.sonic.repository.PlaylistRepository;
import com.padshift.sonic.service.GenreService;
import com.padshift.sonic.service.UserService;
import com.padshift.sonic.service.VideoService;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.hibernate.mapping.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by ruzieljonm on 26/09/2018.
 */
@SuppressWarnings("Duplicates")
@Controller
public class AdminController {

    @Autowired
    VideoService videoService;

    @Autowired
    UserService userService;

    @Autowired
    GracenoteAPIController gracenoteAPIController;

    @Autowired
    GenreService genreService;

    @Autowired
    UserController userController;

    @Autowired
    PlaylistRepository playlistRepository;



    @RequestMapping("/adminHomePage")
    public String showAdminHomePage(){
        return "HomePageAdmin";
    }


    @RequestMapping(value = "/querypage", method = RequestMethod.GET)
    public String showQueryPage(Model model){
        ArrayList<Genre> genres =  videoService.findAllGenre();
        model.addAttribute("genre", genres);
        return "QueryAdmin";
    }

    @RequestMapping(value = "/updateMV")
    public String updateMV(Model model){
        Status[] stat = videoService.updateMV();
        model.addAttribute("upstat", stat);
        return "UpdateStatus";
    }

    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public String config(HttpServletRequest request, Model model){
        String configChoice = request.getParameter("config");
        System.out.println(configChoice);
        if(configChoice.equals("1")){
            return updateMV(model);
        }else if(configChoice.equals("2")){
            System.out.println("GOOD MORNING");
            return gracenoteAPIController.showmetadata();
        }else if(configChoice.equals("3")) {
            return showCriteria(model);

        }else if(configChoice.equals("4")) {
            return saveGenretoDB();

        }else if(configChoice.equals("5")) {
            return updateTopMusic();

        }else if(configChoice.equals("6")) {
            return updateGenreTags();

        }else{
            return "testing";
        }
    }

    @RequestMapping(value = "/byartist", method = RequestMethod.POST)
    public String byArtist(){
        return "ByArtist";
    }


    @RequestMapping(value = "/bysinglevideo", method = RequestMethod.POST)
    public String bySingleVideo(){
        return "BySingleVideo";
    }


    @RequestMapping("/criteria")
    public String showCriteria(Model model){
        ArrayList<Criteria> criteria = userService.findAllCriteria();
        model.addAttribute("criteria", criteria);
        return "Criteria";

    }

    @RequestMapping(value = "/addcriteria", method = RequestMethod.POST)
    public String addCriteria(HttpServletRequest request, Model model){
        String criteriaName = request.getParameter("criteriaName");
        Float criteriaPercentage = Float.valueOf(request.getParameter("criteriaPercentage"));

        Criteria criteria = new Criteria();
        criteria.setCriteriaName(criteriaName);
        criteria.setCriteriaPercentage(criteriaPercentage);

        userService.saveCriteria(criteria);

        ArrayList<Criteria> crit = userService.findAllCriteria();
        model.addAttribute("criteria", crit);
        return "Criteria";

    }

    @RequestMapping("/removecriteria")
    public String deleteCriteria(HttpServletRequest request, Model model){
        int deletethis = Integer.parseInt(request.getParameter("crite").toString());
        userService.deleteCriteriaByCriteriaId(deletethis);

        ArrayList<Criteria> crit = userService.findAllCriteria();
        model.addAttribute("criteria", crit);
        return "Criteria";

    }


    @RequestMapping("/editcriteria")
    public String editCriteria(HttpServletRequest request, Model model){
        int editthis = Integer.parseInt(request.getParameter("crite").toString());
        Criteria crit = userService.findCriteriaByCriteriaId(editthis);

        model.addAttribute("critname", crit.getCriteriaName());
        model.addAttribute("critper", crit.getCriteriaPercentage());
        userService.deleteCriteriaByCriteriaId(editthis);
        ArrayList<Criteria> crite = userService.findAllCriteria();
        model.addAttribute("criteria", crite);
        return "CriteriaUpdate";



    }


    @RequestMapping("/updateTopMusic")
    public String updateTopMusic(){

        ArrayList<Genre> genres = videoService.findAllGenre();

        for(int i=0; i<genres.size(); i++){
            ArrayList<VideoDetails> videos = videoService.findAllVideoDetailsByGenre(genres.get(i).getGenreName());
            Collections.sort(videos);
            String topmusiclist = videos.get(0).getTitle() + "," + videos.get(1).getTitle() + "," + videos.get(2).getTitle();
            Genre genre = new Genre(genres.get(i).getGenreId(),genres.get(i).getGenreName(), genres.get(i).getGenrePhoto(), genres.get(i).getExplorePhoto(), topmusiclist);
            videoService.saveGenre(genre);
            genre = null;

        }


        return "testing";
    }



    @RequestMapping("/savegenre")
    public String saveGenretoDB(){
        ArrayList<String> genres = videoService.findDistinctGenre();
        for (int i=0; i<genres.size(); i++){

            CharSequence pop = "Pop";
            boolean boolpop = genres.get(i).toString().contains(pop);

            CharSequence rock = "Rock";
            boolean boolrock= genres.get(i).toString().contains(rock);

            CharSequence alt = "Alternative";
            boolean boolalt = genres.get(i).toString().contains(alt);

            CharSequence rnb = "R&B";
            boolean boolrnb = genres.get(i).toString().contains(rnb);

            CharSequence country = "Country";
            boolean boolcountry = genres.get(i).toString().contains(country);

            CharSequence house = "House";
            boolean boolhouse = genres.get(i).toString().contains(house);

            CharSequence metal = "Metal";
            boolean boolmetal = genres.get(i).toString().contains(metal);

            CharSequence reggae = "Reggae";
            boolean boolreggae = genres.get(i).toString().contains(reggae);

            CharSequence relig = "Religious";
            boolean boolrelig= genres.get(i).toString().contains(relig);

            CharSequence hiphop = "Hip-Hop";
            boolean boolhiphop= genres.get(i).toString().contains(hiphop);


            if(boolpop==true){
                Genre genre = new Genre();
                genre.setGenreId(1);
                genre.setGenreName("Pop Music");
                genre.setGenrePhoto("/images/pop.png");
                genre.setExplorePhoto("/images/popcube.png");
                genre.setTopMusicList("");
                videoService.saveGenre(genre);
                genre=null;
            }

            if(boolrock==true || boolmetal==true){
                Genre genre = new Genre();
                genre.setGenreId(2);
                genre.setGenreName("Rock Music");
                genre.setGenrePhoto("/images/rock.png");
                genre.setExplorePhoto("/images/rockcube.png");
                genre.setTopMusicList("");
                videoService.saveGenre(genre);
                genre=null;
            }

            if(boolalt==true){
                Genre genre = new Genre();
                genre.setGenreId(3);
                genre.setGenreName("Alternative Music");
                genre.setGenrePhoto("/images/alternative.png");
                genre.setExplorePhoto("/images/alternativecube.png");
                genre.setTopMusicList("");
                videoService.saveGenre(genre);
                genre=null;
            }

            if(boolrnb==true){
                Genre genre = new Genre();
                genre.setGenreId(4);
                genre.setGenreName("R&B/Soul Music");
                genre.setGenrePhoto("/images/rnb.png");
                genre.setExplorePhoto("/images/rnbcube.png");
                genre.setTopMusicList("");
                videoService.saveGenre(genre);
                genre=null;
            }

            if(boolcountry==true){
                Genre genre = new Genre();
                genre.setGenreId(5);
                genre.setGenreName("Country Music");
                genre.setGenrePhoto("/images/country.png");
                genre.setExplorePhoto("/images/countrycube.png");
                genre.setTopMusicList("");
                videoService.saveGenre(genre);
                genre=null;
            }

            if(boolhouse==true){
                Genre genre = new Genre();
                genre.setGenreId(6);
                genre.setGenreName("House Music");
                genre.setGenrePhoto("/images/house.png");
                genre.setExplorePhoto("/images/housecube.png");
                genre.setTopMusicList("");
                videoService.saveGenre(genre);
                genre=null;
            }



            if(boolreggae==true){
                Genre genre = new Genre();
                genre.setGenreId(7);
                genre.setGenreName("Reggae Music");
                genre.setGenrePhoto("/images/reggae.png");
                genre.setExplorePhoto("/images/reggaecube.png");
                genre.setTopMusicList("");
                videoService.saveGenre(genre);
                genre=null;
            }

            if(boolrelig==true){
                Genre genre = new Genre();
                genre.setGenreId(8);
                genre.setGenreName("Religious Music");
                genre.setGenrePhoto("/images/religious.png");
                genre.setExplorePhoto("/images/religiouscube.png");
                genre.setTopMusicList("");
                videoService.saveGenre(genre);
                genre=null;
            }

            if(boolhiphop==true){
                Genre genre = new Genre();
                genre.setGenreId(9);
                genre.setGenreName("Hip-Hop/Rap Music");
                genre.setGenrePhoto("/images/hiprap.png");
                genre.setExplorePhoto("/images/hiprapcube.png");
                genre.setTopMusicList("");
                videoService.saveGenre(genre);
                genre=null;
            }

        }


        return "testing";
    }


    @RequestMapping("/changeGenre")
    public String updateGenreTags(){

        ArrayList<VideoDetails> videos = videoService.findAllVideoDetails();
        for(VideoDetails vid: videos){
            if(vid.getGenre().toString().toLowerCase().contains("pop")){
                VideoDetails videt = new VideoDetails(vid.getVideoid(), vid.getTitle(), vid.getArtist(), vid.getDate(), "Pop Music", vid.getViewCount(), vid.getLikes(), vid.getDislikes(), vid.getVidDuration());
                videoService.saveVideoDetails(videt);
            }

            if(vid.getGenre().toString().toLowerCase().contains("rock") || vid.getGenre().toString().toLowerCase().contains("metal")){
                VideoDetails videt = new VideoDetails(vid.getVideoid(), vid.getTitle(), vid.getArtist(), vid.getDate(), "Rock Music", vid.getViewCount(), vid.getLikes(), vid.getDislikes(), vid.getVidDuration());
                videoService.saveVideoDetails(videt);
            }

            if(vid.getGenre().toString().toLowerCase().contains("alternative")){
                VideoDetails videt = new VideoDetails(vid.getVideoid(), vid.getTitle(), vid.getArtist(), vid.getDate(), "Alternative Music", vid.getViewCount(), vid.getLikes(), vid.getDislikes(), vid.getVidDuration());
                videoService.saveVideoDetails(videt);
            }

            if(vid.getGenre().toString().toLowerCase().contains("r&b") || vid.getGenre().toString().toLowerCase().contains("soul")){
                VideoDetails videt = new VideoDetails(vid.getVideoid(), vid.getTitle(), vid.getArtist(), vid.getDate(), "R&B/Soul Music", vid.getViewCount(), vid.getLikes(), vid.getDislikes(), vid.getVidDuration());
                videoService.saveVideoDetails(videt);
            }

            if(vid.getGenre().toString().toLowerCase().contains("country")){
                VideoDetails videt = new VideoDetails(vid.getVideoid(), vid.getTitle(), vid.getArtist(), vid.getDate(), "Country Music", vid.getViewCount(), vid.getLikes(), vid.getDislikes(), vid.getVidDuration());
                videoService.saveVideoDetails(videt);
            }

            if(vid.getGenre().toString().toLowerCase().contains("house")){
                VideoDetails videt = new VideoDetails(vid.getVideoid(), vid.getTitle(), vid.getArtist(), vid.getDate(), "House Music", vid.getViewCount(), vid.getLikes(), vid.getDislikes(), vid.getVidDuration());
                videoService.saveVideoDetails(videt);
            }

            if(vid.getGenre().toString().toLowerCase().contains("reggae")){
                VideoDetails videt = new VideoDetails(vid.getVideoid(), vid.getTitle(), vid.getArtist(), vid.getDate(), "Reggae Music", vid.getViewCount(), vid.getLikes(), vid.getDislikes(), vid.getVidDuration());
                videoService.saveVideoDetails(videt);
            }

            if(vid.getGenre().toString().toLowerCase().contains("religious")){
                VideoDetails videt = new VideoDetails(vid.getVideoid(), vid.getTitle(), vid.getArtist(), vid.getDate(), "Religious Music", vid.getViewCount(), vid.getLikes(), vid.getDislikes(), vid.getVidDuration());
                videoService.saveVideoDetails(videt);
            }

            if(vid.getGenre().toString().toLowerCase().contains("hip-hop/rap")){
                VideoDetails videt = new VideoDetails(vid.getVideoid(), vid.getTitle(), vid.getArtist(), vid.getDate(), "Hip-Hop/Rap Music", vid.getViewCount(), vid.getLikes(), vid.getDislikes(), vid.getVidDuration());
                videoService.saveVideoDetails(videt);
            }

        }
        return "testing";
    }


    @RequestMapping("/runseq")
    public String runSeq(){
        ArrayList<String> generatedPlaylist = new ArrayList<>();
        ArrayList<String> sequenceids = userService.findDistinctSequenceId();

        System.out.println("SEQ IDS SIZE : " + sequenceids.size());


        ArrayList<UserHistory>[] seqRules = (ArrayList<UserHistory>[])new ArrayList[sequenceids.size()];

        for(int i=0; i<sequenceids.size();i++){
            seqRules[i] = userService.findUserHistoryBySeqid(sequenceids.get(i).toString());
            Collections.sort(seqRules[i], UserHistory.TimeComparator);
        }


//        seqRules = isViewingTimeValid(seqRules,sequenceids);  //check time if viewing status is 0

//        seqRules = isMorningAfternoon(seqRules,sequenceids); // check if belongs to morning or afternoon

        seqRules = isEvening(seqRules,sequenceids);


//        displaySeqFromDatabase(seqRules,sequenceids);

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

//        parts = new HashSet<String>(Arrays.asList(parts)).toArray(new String[0]);

//        Set<Integer> set = new LinkedHashSet<>(parts);
//
//        for(String s : parts){
//            System.out.println("-" + s.toString());
//        }

//        int length = parts.length;
//        length = removeDuplicateElements(parts, length);

//
//        ArrayList<String> resultingPlaylist = new ArrayList<>();
//
//        for(int j=0; j<parts.length; j++){
//
//            if(Arrays.asList(parts).contains(parts[j].toString())) {
//
//                System.out.println("[PL]" + parts[j].toString());
//            }else{
//                resultingPlaylist.add(parts[j].toString());
//                System.out.println("[PL pasok]" + parts[j].toString());
//            }
//
//        }



//        for(String uID : uniqueVideoIDs){
//            if(resultingPlaylist.contains(uID.toString())){
//                System.out.println("Already Part of the Playlist!");
//            }else{
//                resultingPlaylist.add(uID.toString());
//            }
//        }
//
//        Set<String> norep = new HashSet<String>(resultingPlaylist);
//
//        ArrayList<String> resFinal = new ArrayList<>();
//
//        for(String eOut: norep){
//            resFinal.add(eOut);
//        }
//
//        for(String f :resFinal){
//            System.out.println("-" + f.toString());
//        }
//
//
//
//
//        System.out.println("------------1--------------------");
//        seqrules[1] = new ArrayList<>();
//        seqrules[1] = buildRuleCombination(seqrules[0],uniqueVideoIDs,databaseRules);
//        seqrules[1] = removeUnqualifiedForThreshold(seqrules[1]);
//        displaySeqrulesMeasures(seqrules[1]);
//
//        System.out.println("------------2--------------------");
//        seqrules[2] = new ArrayList<>();
//        seqrules[2] = buildRuleCombination(seqrules[1],uniqueVideoIDs,databaseRules);
//        seqrules[2] = removeUnqualifiedForThreshold(seqrules[2]);
//        displaySeqrulesMeasures(seqrules[2]);
//
//        System.out.println("------------3--------------------");
//        seqrules[3] = new ArrayList<>();
//        seqrules[3] = buildRuleCombination(seqrules[2],uniqueVideoIDs,databaseRules);
//        seqrules[3] = removeUnqualifiedForThreshold(seqrules[3]);
//        displaySeqrulesMeasures(seqrules[3]);
////
//        System.out.println("------------4--------------------");
//        seqrules[4] = new ArrayList<>();
//        seqrules[4] = buildRuleCombination(seqrules[3],uniqueVideoIDs,databaseRules);
//        seqrules[4] = removeUnqualifiedForThreshold(seqrules[4]);
//        displaySeqrulesMeasures(seqrules[4]);
////
////
//        System.out.println("------------5--------------------");
//        seqrules[5] = new ArrayList<>();
//        seqrules[5] = buildRuleCombination(seqrules[4],uniqueVideoIDs,databaseRules);
//        seqrules[5] = removeUnqualifiedForThreshold(seqrules[5]);
//        displaySeqrulesMeasures(seqrules[5]);
//
//        System.out.println("------------6--------------------");
//        seqrules[6] = new ArrayList<>();
//        seqrules[6] = buildRuleCombination(seqrules[5],uniqueVideoIDs,databaseRules);
//        seqrules[6] = removeUnqualifiedForThreshold(seqrules[6]);
//        displaySeqrulesMeasures(seqrules[6]);
//
//        for(ClassSequentialRules s: seqrules[0]){
//            s.setSupport(calculateSupport(databaseRules,s.getVideoIds()));
//        }
//        seqrules[0] = removeUnqualifiedForThreshold(seqrules[0]);
//        displaySeqrulesMeasures(seqrules[0]);
//
//        displaySeqrulesMeasures(seqrules[0]);
//
//        System.out.println("seq1 size : " + seqrules[0].size());
//
//
//        seqrules[1] = new ArrayList<>();
//        System.out.println("------------1--------------------");
//        seqrules[1] = buildRuleCombination(seqrules[0],uniqueVideoIDs);
//
//
//
//        for(ClassSequentialRules s: seqrules[1]){
//            for(int i=0; i<databaseRules.size(); i++){
//                String newStr = s.getVideoIds().replaceAll(",", ".*");
//                String tempPat = ".*" + newStr + ".*";
//                Pattern p = Pattern.compile(tempPat);
//                boolean b = false;
//
////                System.out.println(tempPat);
//
//                Matcher m = p.matcher(databaseRules.get(i).toString());
//                b = m.matches();
//                if (b == true) {
//                    s.setSupport(1);
//                }
////        s.setSupport(1);
//        }
//
//        for(ClassSequentialRules s: seqrules[1]){
//            System.out.println(s.getVideoIds() + s.getSupport());
//        }
//
//
//
//        for (int i=0; i<seqrules[0].size(); i++){
//            seqrules[1].get(i).setSupport(1);
//        }
////        seqrules[1] = removeUnqualifiedForThreshold(seqrules[1]);
//        displaySeqrulesMeasures(seqrules[1]);
//


//        seqrules[2] = new ArrayList<>();
//        System.out.println("-----------2---------------------");
//        seqrules[2] = buildRuleCombination(seqrules[1],uniqueVideoIDs, databaseRules);
////        seqrules[2] = removeUnqualifiedForThreshold(seqrules[2]);
////        displaySeqrulesMeasures(seqrules[2]);
//
//        for(int i=0; i<seqrules[2].size(); i++){
//            seqrules[2].get(i).setSupport(calculateSupport(databaseRules,uniqueVideoIDs));
//        }



        return "testing";

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



    public ArrayList<UserHistory>[] isViewingTimeValid(ArrayList<UserHistory>[] seqRules,  ArrayList<String> sequenceids){
        for(int i=0; i<sequenceids.size();i++){
            for(int j=0; j<seqRules[i].size(); j++){
                if(seqRules[i].get(j).getViewingStatus().equals("0")){
                    seqRules[i].remove(j);
                }
            }
        }

        return seqRules;
    }

    public ArrayList<UserHistory>[] isMorningAfternoon(ArrayList<UserHistory>[] seqRules,  ArrayList<String> sequenceids){
        for(int i=0; i<sequenceids.size();i++){
            for(int j=0; j<seqRules[i].size(); j++){
                if(Integer.parseInt(seqRules[i].get(j).getViewingTime().substring(0,seqRules[i].get(j).getViewingTime().length()-6))<18
                        && seqRules[i].get(j).getViewingStatus().equals("0")){

                    seqRules[i].remove(j);
                }
            }
        }

        return seqRules;
    }



    public ArrayList<UserHistory>[] isEvening(ArrayList<UserHistory>[] seqRules,  ArrayList<String> sequenceids){
        for(int i=0; i<sequenceids.size();i++){
            for(int j=0; j<seqRules[i].size(); j++){
                if(Integer.parseInt(seqRules[i].get(j).getViewingTime().substring(0,seqRules[i].get(j).getViewingTime().length()-6))>=18
                        && seqRules[i].get(j).getViewingStatus().equals("0")){

                    seqRules[i].remove(j);
                }
            }
        }

        return seqRules;
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



    public void displaySeqFromDatabase(ArrayList<UserHistory>[] seqRules,  ArrayList<String> sequenceids){
        System.out.println("S E Q U E N C E S  F R O M  T H E  D A T A B A S E");
        for(int i=0; i<sequenceids.size();i++){
            System.out.print("[" + sequenceids.get(i).toString() +"] : ");
            for(int j=0; j<seqRules[i].size(); j++){
                System.out.print(" " + seqRules[i].get(j).getVideoid() +", ");
            }
            System.out.println("");
        }
    }






    @RequestMapping("/generatePlaylist")
    public String generatePlaylist(){
        ArrayList<String> generatedPlaylist = new ArrayList<>();

        ArrayList<String> seqIDs = userService.findDistinctSequenceId();
        System.out.println("unique sequence ids : ");
        for( String seq: seqIDs){
            System.out.println(seq);
        }

        ArrayList<UserHistory>[] seqRules = (ArrayList<UserHistory>[])new ArrayList[seqIDs.size()];

        for(int i=0; i<seqIDs.size();i++){
            seqRules[i] = userService.findUserHistoryBySeqid(seqIDs.get(i).toString());
            Collections.sort(seqRules[i], UserHistory.TimeComparator);
        }

        ArrayList<String> elements = new ArrayList<>();

        System.out.println("[SequenceID]      [Video IDs]");
        ArrayList<String>[] sequencedIDs = (ArrayList<String>[])new ArrayList[seqIDs.size()];


        for(int i=0; i<seqIDs.size();i++){
            System.out.print("[" + seqIDs.get(i).toString() + "]     ");

            for(int j=0; j<seqRules[i].size(); j++){
                if(seqRules[i].get(j).getViewingStatus().equals("0")){
                    seqRules[i].remove(j);
                }
            }

            for(int j=0; j<seqRules[i].size(); j++){
                System.out.print(seqRules[i].get(j).getVideoid() + ", ");
                elements.add(seqRules[i].get(j).getVideoid());
            }
            System.out.println();
        }

        Set<String> uniqueElements = new HashSet<String>(elements);

        ArrayList<String> singE = new ArrayList<>();
        ArrayList<sequenceRule> firstPass = new ArrayList<>();
        for(String eOut: uniqueElements){
            sequenceRule temp = new sequenceRule(eOut,0);
            firstPass.add(temp);
            singE.add(eOut);
        }
        System.out.println("Elements in the rules : " + singE.size());

        for(int i=0; i<sequencedIDs.length; i++){
            sequencedIDs[i] = new ArrayList<>();
        }

        for(int i=0; i<seqIDs.size();i++){
            for(int j=0; j<seqRules[i].size(); j++) {
                sequencedIDs[i].add(seqRules[i].get(j).getVideoid().toString() + ", ");
            }
//            System.out.println();
        }

        for(int i=0; i<sequencedIDs.length; i++){
            System.out.println(sequencedIDs[i].toString());
        }


        float totalSequences = sequencedIDs.length;
        System.out.println("TOTAL SEQUENCES : " + totalSequences);


        for(int i=0; i<sequencedIDs.length;i++) {
            System.out.println("ey = " + sequencedIDs[i].toString());
            for(int j=0; j<firstPass.size(); j++){
                String tempPat = ".*" + firstPass.get(j).getVideoIds() + ".*";
                Pattern p = Pattern.compile(tempPat);
                boolean b = false;

                Matcher m = p.matcher(sequencedIDs[i].toString());
                b = m.matches();
                if(b==true){
                    firstPass.get(j).setSupport(firstPass.get(j).getSupport()+1);
                }
            }
        }

        float supportSum1 =0;
        for(int i=0; i<firstPass.size(); i++){
            firstPass.get(i).setSupport(firstPass.get(i).getSupport()/totalSequences);
            supportSum1+=firstPass.get(i).getSupport();
        }
        float firstPassThreshold = supportSum1/firstPass.size();
        System.out.println("firstPassThreshold : " + firstPassThreshold);

        ArrayList<sequenceRule> secondPass = new ArrayList<>();
        System.out.println("Qualified the Threshold : ");
        for(int i=0; i<firstPass.size(); i++){
            if(firstPass.get(i).getSupport()>firstPassThreshold) {
                System.out.println(firstPass.get(i).getVideoIds() + " -- " + firstPass.get(i).getSupport());
                sequenceRule temp = new sequenceRule(firstPass.get(i).getVideoIds(), 0);
                secondPass.add(temp);
            }
        }

        System.out.println("Second Pass Size" + secondPass.size());
        System.out.println();


        ArrayList<sequenceRule> sectemp = new ArrayList<>();
        for(int j=0; j<singE.size(); j++){
            for(int k=0; k<secondPass.size(); k++) {
                sequenceRule temp = new sequenceRule(singE.get(j).toString() + ", " + secondPass.get(k).getVideoIds(),0);
                sectemp.add(temp);
            }
        }

        sectemp = checkIfExist(sectemp,sequencedIDs);

        for(int i=0; i<sectemp.size(); i++){
            sectemp.get(i).setSupport(sectemp.get(i).getSupport()/totalSequences);
            if(sectemp.get(i).getSupport()>0) {
                secondPass.add(sectemp.get(i));
            }
        }


        float secPassThreshold = computeThreshold(secondPass);

        System.out.println("secPassThreshold : " + secPassThreshold );
        System.out.println("Qualified the 2nd Threshold : ");

        ArrayList<sequenceRule> thirdPass = new ArrayList<>();
        ArrayList<sequenceRule> thirdPassTemp = new ArrayList<>();
        for(sequenceRule seq: secondPass) {
            if(seq.getSupport()>secPassThreshold){
                System.out.println(seq.getVideoIds() +"," + seq.getSupport());
                for(int i=0; i<singE.size(); i++) {
                    sequenceRule tmp = new sequenceRule(seq.getVideoIds() +", "+ singE.get(i).toString(),0);
                    thirdPassTemp.add(tmp);
                }
            }
        }
        System.out.println("THIRD PASSSSS:");

        thirdPassTemp = checkIfExist(thirdPassTemp,sequencedIDs);

        float thirdThreshold = computeThreshold(thirdPassTemp);

        System.out.println("thirdThreshold : " + thirdThreshold);
        System.out.println("thirdpass temp eval");

        ArrayList<sequenceRule> fourthPassTemp = new ArrayList<>();
        ArrayList<sequenceRule> fourthPass = new ArrayList<>();

        for(sequenceRule s: thirdPassTemp) {
            if (s.getSupport() > thirdThreshold) {
                System.out.println(s.getVideoIds() + "," + s.getSupport());
                for(int i=0; i<singE.size(); i++) {
                    sequenceRule tempo = new sequenceRule(s.getVideoIds() +", "+ singE.get(i).toString(),0);
                    fourthPassTemp.add(tempo);
                }
            }
        }


        System.out.println("Fourth Pass Level");
        fourthPassTemp = checkIfExist(fourthPassTemp,sequencedIDs);




        float fourthThreshold = computeThreshold(fourthPassTemp);
        System.out.println("fourthThreshold : " + fourthThreshold);
        System.out.println("fou temp eval");


        for(sequenceRule s: fourthPassTemp) {
            if(s.getSupport()>fourthThreshold){
                System.out.println(s.getVideoIds() + "," + s.getSupport());
                fourthPass.add(s);
            }
        }
        ArrayList<sequenceRule> fifthPassTemp = new ArrayList<>();
        for(sequenceRule s: fourthPass){
//            System.out.println(s.getVideoIds() + "," + s.getSupport());
            for(int i=0; i<singE.size(); i++) {
                sequenceRule tempo = new sequenceRule(s.getVideoIds() +", "+ singE.get(i).toString(),0);
                fifthPassTemp.add(tempo);
            }

        }

//        displaySequenceRules(fifthPassTemp);
        fifthPassTemp=checkIfExist(fifthPassTemp,sequencedIDs);
        System.out.println("-------------------------------------");
//        displaySequenceRules(fifthPassTemp);
        float fifthThresh =computeThreshold(fifthPassTemp);

        ArrayList<sequenceRule> fifthPass = new ArrayList<>();
        System.out.println("fifthThresh " + fifthThresh);
        System.out.println("qualified the 5th Threshold: ");
        for(sequenceRule s: fifthPassTemp){
            if(s.getSupport()>=fifthThresh){
                fifthPass.add(s);
            }
        }

        if(fifthPass.size()==0){
            generatedPlaylist = createPlaylist(fifthPassTemp);
        }else {
            displaySequenceRules(fifthPass);
        }
        ArrayList<sequenceRule> sixthPassTemp = new ArrayList<>();
        for(sequenceRule s: fifthPass){
            for(int i=0; i<singE.size(); i++) {
                sequenceRule tempo = new sequenceRule(s.getVideoIds() +", "+ singE.get(i).toString(),0);
                sixthPassTemp.add(tempo);
            }

        }

        sixthPassTemp=checkIfExist(sixthPassTemp,sequencedIDs);
        float sixthThresh =computeThreshold(sixthPassTemp);

        ArrayList<sequenceRule> sixthPass = new ArrayList<>();
        System.out.println("sixthThresh " + sixthThresh);
        System.out.println("qualified the 6th Threshold: ");
//        for(sequenceRule s: sixthPassTemp){
//            if(s.getSupport()>=sixthThresh){
//                sixthPass.add(s);
//            }
//        }
        sixthPass=evaluateSeqRules(sixthPassTemp,sixthThresh);
        if(sixthPass.size()==0){
            System.out.println("Im here ate 6th pass");
            generatedPlaylist = createPlaylist(sixthPassTemp);
            endPass(generatedPlaylist,singE,"plgeneral");
            return "testing";



        }else{
            displaySequenceRules(sixthPass);
        }






        ArrayList<sequenceRule> sevPasstTemp = new ArrayList<>();
        for(sequenceRule s: sixthPass){
            for(int i=0; i<singE.size(); i++) {
                sequenceRule tempo = new sequenceRule(s.getVideoIds() +", "+ singE.get(i).toString(),0);
                sevPasstTemp.add(tempo);
            }

        }


        displaySequenceRules(sevPasstTemp);
        sevPasstTemp = checkIfExist(sevPasstTemp,sequencedIDs);
        float sevThresh =computeThreshold(sevPasstTemp);

        ArrayList<sequenceRule> sevPass = new ArrayList<>();
        for(sequenceRule s: sevPasstTemp){
            if(s.getSupport()>=sevThresh){
                sevPass.add(s);
            }
        }
        System.out.println("sevThresh : " + sevThresh);


        for(sequenceRule s: sevPass){
            System.out.println("aaahhh" + s.getVideoIds());
        }

        System.out.println("Sev Temporary : ");
        displaySequenceRules(sevPasstTemp);
        System.out.println("Seven Pass : ");
        displaySequenceRules(sevPass);

        if(sevPass.size()==0){
            generatedPlaylist = createPlaylist(sevPasstTemp);
        }



        System.out.println("G E N E R A T E D   P L A Y L I S T : ");
        for(String id : generatedPlaylist){
            System.out.println(id.toString());
        }

        System.out.println("Added:");

        for(String si : singE){
            if(!generatedPlaylist.contains(singE)){
                generatedPlaylist.add(si);
            }
        }

        for(String id : generatedPlaylist){
            System.out.println(id.toString());
        }

        //20 SONGS RA SIZZYYY

        String plid = "plgeneral";
        for(int i=0; i<20; i++){
            Playlist pl = new Playlist(plid, generatedPlaylist.get(i).toString());
            videoService.savePlaylist(pl);
        }


        return "testing";
    }

    public static boolean isBetween(LocalTime candidate, LocalTime start, LocalTime end) {
        return !candidate.isBefore(start) && !candidate.isAfter(end);  // Inclusive.
    }

    public static String[] GetStringArray(ArrayList<String> arr)
    {

        // declaration and initialise String Array
        String str[] = new String[arr.size()];

        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {

            // Assign each value to String array
            str[j] = arr.get(j);
        }

        return str;
    }

    public void endPass(ArrayList<String> generatedPlaylist, ArrayList<String> singE, String plid ){
        System.out.println("G E N E R A T E D   P L A Y L I S T : ");
        for(String id : generatedPlaylist){
            System.out.println(id.toString());
        }

        System.out.println("Added:");
        String[] str = GetStringArray(generatedPlaylist);
        for(String si : singE){
            if(!Arrays.asList(str).contains(si.toString())) {
                generatedPlaylist.add(si.toString());
            }
        }


        int sncnt=0;
        for(String plv: generatedPlaylist){
            if(sncnt<20) {
                System.out.println("ADDED : " + plv.toString());
                Playlist pl = new Playlist(plid, plv.toString());
                videoService.savePlaylist(pl);
                sncnt++;
            }else{
                break;
            }
        }

    }


    //    @RequestMapping("/generatePlaylistEvening")
    @RequestMapping("/generatePlaylistMorAft")
    public String generatePlaylistTimeBased(){
        ArrayList<String> generatedPlaylist = new ArrayList<>();

        ArrayList<String> seqIDs = userService.findDistinctSequenceId();
        System.out.println("unique sequence ids : ");
        for( String seq: seqIDs){
            System.out.println(seq);
        }

        ArrayList<UserHistory>[] seqRules = (ArrayList<UserHistory>[])new ArrayList[seqIDs.size()];

        for(int i=0; i<seqIDs.size();i++){
            seqRules[i] = userService.findUserHistoryBySeqid(seqIDs.get(i).toString());
            Collections.sort(seqRules[i], UserHistory.TimeComparator);
        }

        ArrayList<String> elements = new ArrayList<>();

        System.out.println("[SequenceID]      [Video IDs]");
        ArrayList<String>[] sequencedIDs = (ArrayList<String>[])new ArrayList[seqIDs.size()];


        for(int i=0; i<seqIDs.size();i++){
            System.out.print("[" + seqIDs.get(i).toString() + "]     ");

            for(int j=0; j<seqRules[i].size(); j++){
                if(Integer.parseInt(seqRules[i].get(j).getViewingTime().substring(0,seqRules[i].get(j).getViewingTime().length()-6))<18
                        && seqRules[i].get(j).getViewingStatus().equals("0")){
                    seqRules[i].remove(j);
                }
            }

            for(int j=0; j<seqRules[i].size(); j++){
                System.out.print(seqRules[i].get(j).getVideoid() + ", ");
                elements.add(seqRules[i].get(j).getVideoid());
            }
            System.out.println();
        }

        Set<String> uniqueElements = new HashSet<String>(elements);

        ArrayList<String> singE = new ArrayList<>();
        ArrayList<sequenceRule> firstPass = new ArrayList<>();
        for(String eOut: uniqueElements){
            sequenceRule temp = new sequenceRule(eOut,0);
            firstPass.add(temp);
            singE.add(eOut);
        }
        System.out.println("Elements in the rules : " + singE.size());

        for(int i=0; i<sequencedIDs.length; i++){
            sequencedIDs[i] = new ArrayList<>();
        }

        for(int i=0; i<seqIDs.size();i++){
            for(int j=0; j<seqRules[i].size(); j++) {
                sequencedIDs[i].add(seqRules[i].get(j).getVideoid().toString() + ", ");
            }
//            System.out.println();
        }

        for(int i=0; i<sequencedIDs.length; i++){
            System.out.println(sequencedIDs[i].toString());
        }


        float totalSequences = sequencedIDs.length;
        System.out.println("TOTAL SEQUENCES : " + totalSequences);


        for(int i=0; i<sequencedIDs.length;i++) {
            System.out.println("ey = " + sequencedIDs[i].toString());
            for(int j=0; j<firstPass.size(); j++){
                String tempPat = ".*" + firstPass.get(j).getVideoIds() + ".*";
                Pattern p = Pattern.compile(tempPat);
                boolean b = false;

                Matcher m = p.matcher(sequencedIDs[i].toString());
                b = m.matches();
                if(b==true){
                    firstPass.get(j).setSupport(firstPass.get(j).getSupport()+1);
                }
            }
        }

        float supportSum1 =0;
        for(int i=0; i<firstPass.size(); i++){
            firstPass.get(i).setSupport(firstPass.get(i).getSupport()/totalSequences);
            supportSum1+=firstPass.get(i).getSupport();
        }
        float firstPassThreshold = supportSum1/firstPass.size();
        System.out.println("firstPassThreshold : " + firstPassThreshold);

        ArrayList<sequenceRule> secondPass = new ArrayList<>();
        System.out.println("Qualified the Threshold : ");
        for(int i=0; i<firstPass.size(); i++){
            if(firstPass.get(i).getSupport()>firstPassThreshold) {
                System.out.println(firstPass.get(i).getVideoIds() + " -- " + firstPass.get(i).getSupport());
                sequenceRule temp = new sequenceRule(firstPass.get(i).getVideoIds(), 0);
                secondPass.add(temp);
            }
        }

        System.out.println("Second Pass Size" + secondPass.size());
        System.out.println();


        ArrayList<sequenceRule> sectemp = new ArrayList<>();
        for(int j=0; j<singE.size(); j++){
            for(int k=0; k<secondPass.size(); k++) {
                sequenceRule temp = new sequenceRule(singE.get(j).toString() + ", " + secondPass.get(k).getVideoIds(),0);
                sectemp.add(temp);
            }
        }

        sectemp = checkIfExist(sectemp,sequencedIDs);

        for(int i=0; i<sectemp.size(); i++){
            sectemp.get(i).setSupport(sectemp.get(i).getSupport()/totalSequences);
            if(sectemp.get(i).getSupport()>0) {
                secondPass.add(sectemp.get(i));
            }
        }


        float secPassThreshold = computeThreshold(secondPass);

        System.out.println("secPassThreshold : " + secPassThreshold );
        System.out.println("Qualified the 2nd Threshold : ");

        ArrayList<sequenceRule> thirdPass = new ArrayList<>();
        ArrayList<sequenceRule> thirdPassTemp = new ArrayList<>();
        for(sequenceRule seq: secondPass) {
            if(seq.getSupport()>secPassThreshold){
                System.out.println(seq.getVideoIds() +"," + seq.getSupport());
                for(int i=0; i<singE.size(); i++) {
                    sequenceRule tmp = new sequenceRule(seq.getVideoIds() +", "+ singE.get(i).toString(),0);
                    thirdPassTemp.add(tmp);
                }
            }
        }
        System.out.println("THIRD PASSSSS:");

        thirdPassTemp = checkIfExist(thirdPassTemp,sequencedIDs);

        float thirdThreshold = computeThreshold(thirdPassTemp);

        System.out.println("thirdThreshold : " + thirdThreshold);
        System.out.println("thirdpass temp eval");

        ArrayList<sequenceRule> fourthPassTemp = new ArrayList<>();
        ArrayList<sequenceRule> fourthPass = new ArrayList<>();

        for(sequenceRule s: thirdPassTemp) {
            if (s.getSupport() > thirdThreshold) {
                System.out.println(s.getVideoIds() + "," + s.getSupport());
                for(int i=0; i<singE.size(); i++) {
                    sequenceRule tempo = new sequenceRule(s.getVideoIds() +", "+ singE.get(i).toString(),0);
                    fourthPassTemp.add(tempo);
                }
            }
        }


        System.out.println("Fourth Pass Level");
        fourthPassTemp = checkIfExist(fourthPassTemp,sequencedIDs);




        float fourthThreshold = computeThreshold(fourthPassTemp);
        System.out.println("fourthThreshold : " + fourthThreshold);
        System.out.println("fou temp eval");


        for(sequenceRule s: fourthPassTemp) {
            if(s.getSupport()>fourthThreshold){
                System.out.println(s.getVideoIds() + "," + s.getSupport());
                fourthPass.add(s);
            }
        }
        ArrayList<sequenceRule> fifthPassTemp = new ArrayList<>();
        for(sequenceRule s: fourthPass){
//            System.out.println(s.getVideoIds() + "," + s.getSupport());
            for(int i=0; i<singE.size(); i++) {
                sequenceRule tempo = new sequenceRule(s.getVideoIds() +", "+ singE.get(i).toString(),0);
                fifthPassTemp.add(tempo);
            }

        }

//        displaySequenceRules(fifthPassTemp);
        fifthPassTemp=checkIfExist(fifthPassTemp,sequencedIDs);
        System.out.println("-------------------------------------");
//        displaySequenceRules(fifthPassTemp);
        float fifthThresh =computeThreshold(fifthPassTemp);

        ArrayList<sequenceRule> fifthPass = new ArrayList<>();
        System.out.println("fifthThresh " + fifthThresh);
        System.out.println("qualified the 5th Threshold: ");
        for(sequenceRule s: fifthPassTemp){
            if(s.getSupport()>=fifthThresh){
                fifthPass.add(s);
            }
        }

        if(fifthPass.size()==0){
            generatedPlaylist = createPlaylist(fifthPassTemp);
//            endPass(generatedPlaylist,singE,"plevening");
            endPass(generatedPlaylist,singE,"plmornaft");
            return "testing";
            //returrrrrrrrrrrrrrrrrrrrrrrrrrn
        }else {
            displaySequenceRules(fifthPass);
        }
        ArrayList<sequenceRule> sixthPassTemp = new ArrayList<>();
        for(sequenceRule s: fifthPass){
            for(int i=0; i<singE.size(); i++) {
                sequenceRule tempo = new sequenceRule(s.getVideoIds() +", "+ singE.get(i).toString(),0);
                sixthPassTemp.add(tempo);
            }

        }

        sixthPassTemp=checkIfExist(sixthPassTemp,sequencedIDs);
        float sixthThresh =computeThreshold(sixthPassTemp);

        ArrayList<sequenceRule> sixthPass = new ArrayList<>();
        System.out.println("sixthThresh " + sixthThresh);
        System.out.println("qualified the 6th Threshold: ");
//        for(sequenceRule s: sixthPassTemp){
//            if(s.getSupport()>=sixthThresh){
//                sixthPass.add(s);
//            }
//        }
        sixthPass=evaluateSeqRules(sixthPassTemp,sixthThresh);
        if(sixthPass.size()==0){
            System.out.println("Im here at 6th pass");
            generatedPlaylist = createPlaylist(sixthPassTemp);
//            endPass(generatedPlaylist,singE,"plevening");
            endPass(generatedPlaylist,singE,"plmornaft");

            return "testing";



        }else{
            displaySequenceRules(sixthPass);
        }






        ArrayList<sequenceRule> sevPasstTemp = new ArrayList<>();
        for(sequenceRule s: sixthPass){
            for(int i=0; i<singE.size(); i++) {
                sequenceRule tempo = new sequenceRule(s.getVideoIds() +", "+ singE.get(i).toString(),0);
                sevPasstTemp.add(tempo);
            }

        }


        displaySequenceRules(sevPasstTemp);
        sevPasstTemp = checkIfExist(sevPasstTemp,sequencedIDs);
        float sevThresh =computeThreshold(sevPasstTemp);

        ArrayList<sequenceRule> sevPass = new ArrayList<>();
        for(sequenceRule s: sevPasstTemp){
            if(s.getSupport()>=sevThresh){
                sevPass.add(s);
            }
        }
        System.out.println("sevThresh : " + sevThresh);


        for(sequenceRule s: sevPass){
            System.out.println("aaahhh" + s.getVideoIds());
        }

        System.out.println("Sev Temporary : ");
        displaySequenceRules(sevPasstTemp);
        System.out.println("Seven Pass : ");
        displaySequenceRules(sevPass);

        if(sevPass.size()==0){
            generatedPlaylist = createPlaylist(sevPasstTemp);
        }



        System.out.println("G E N E R A T E D   P L A Y L I S T : ");
        for(String id : generatedPlaylist){
            System.out.println(id.toString());
        }

        System.out.println("Added:");

        for(String si : singE){
            if(!generatedPlaylist.contains(singE)){
                generatedPlaylist.add(si);
            }
        }

        for(String id : generatedPlaylist){
            System.out.println(id.toString());
        }

        //20 SONGS RA SIZZYYY

        String plid = "plmornaft";
//        String plid = "plevening";
        for(int i=0; i<20; i++){
            Playlist pl = new Playlist(plid, generatedPlaylist.get(i).toString());
            videoService.savePlaylist(pl);
        }


        return "testing";
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }


    public ArrayList<String> createPlaylist(ArrayList<sequenceRule> seq){
        ArrayList<String> playlist = new ArrayList<>();
        ArrayList<sequenceRule> seqtemp = new ArrayList<>();
        for(int i=0; i<seq.size(); i++){
            if(seq.get(i).getSupport()<=0){
                seq.remove(i);
            }
        }
        String[] parts = seq.get(0).getVideoIds().toString().split(", ");
        for(int j=0; j<parts.length; j++){
            playlist.add(parts[j].toString());
        }

//        Arrays.asList(str.split("\\s*,\\s*"));

        return playlist;
    }

    public ArrayList<sequenceRule> evaluateSeqRules(ArrayList<sequenceRule> seq, float thresh){
        ArrayList<sequenceRule> res = new ArrayList<>();
        for(sequenceRule s: seq){
            if(s.getSupport()>=thresh){
                res.add(s);
            }
        }
        return res;
    }



    public float computeThreshold(ArrayList<sequenceRule> seq){
        float threshold=0;
        int cnt=0;
        for(sequenceRule s: seq){
            if(s.getSupport()>0){
                cnt++;
                threshold+=s.getSupport();
            }
        }
        return threshold/cnt;
    }

    public ArrayList<sequenceRule> combine(ArrayList<sequenceRule> s, ArrayList<String> singE){
        for(int j=0; j<s.size(); j++){
            for(int i=0; i<singE.size(); i++) {
                sequenceRule tempo = new sequenceRule(s.get(j).getVideoIds() +", "+ singE.get(i).toString(),0);
                s.add(tempo);
            }

        }

        return s;
    }

    public void displaySequenceRules(ArrayList<sequenceRule> seq){
        for(sequenceRule s : seq){
            System.out.println("comb  "+ s.getVideoIds() + " -- " +s.getSupport());
        }
    }

    public ArrayList<sequenceRule> checkIfExist(ArrayList<sequenceRule> nthSeq,  ArrayList<String>[] sequencedIDs ) {
        for (sequenceRule s : nthSeq) {
            for (int i = 0; i < sequencedIDs.length; i++) {
                String newStr = s.getVideoIds().replaceAll(",", ".*");
                String tempPat = ".*" + newStr + ".*";
                Pattern p = Pattern.compile(tempPat);
                boolean b = false;

                Matcher m = p.matcher(sequencedIDs[i].toString());
                b = m.matches();
                if (b == true) {
                    s.setSupport(s.getSupport() + 1);
                }
            }
        }

        for(sequenceRule s: nthSeq) {
            if(s.getSupport()>0) {

                s.setSupport(s.getSupport() / sequencedIDs.length);
            }
        }

        return nthSeq;
    }

    public ArrayList<sequenceRule> possibleCombination(ArrayList<sequenceRule> seqtemp, ArrayList<String> singE){
        for(int j=0; j<seqtemp.size(); j++) {
            for(int i=0; i<singE.size(); i++) {
                sequenceRule tempo = new sequenceRule(seqtemp.get(j).getVideoIds() +", "+ singE.get(i).toString(),0);
                System.out.println("----" + seqtemp.get(j).getVideoIds() +", "+ singE.get(i).toString());
                seqtemp.add(tempo);
            }

        }
        return seqtemp;
    }

    @RequestMapping("/testrun")
    public String equivalentSonicID(){
        String csvFile = "C:/Users/ruzieljonm/Desktop/combination.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        ArrayList<Equivalent> equival = new ArrayList<>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);

                System.out.println("Video ID [raw= " + country[0] + " , videoid_sonic=" + country[1] + "]");
                Equivalent e = new Equivalent(country[0],country[1]);
                equival.add(e);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String findme = "TROIMGU128E0782ABA";

        Equivalent james = equival.stream()
                .filter(eq -> findme.equals(eq.getJam_id()))
                .findAny()
                .orElse(null);

        System.out.println("result : " + james.getJam_id() + "--" + james.getSonic_id());

        return james.getSonic_id();

    }

    @RequestMapping("/displaytsv")
    public void displatTSV() throws IOException {
        //read combinations
        String csvFile = "C:/Users/ruzieljonm/Documents/Fouth Year/2019/Sonic2019/src/main/resources/combination.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        ArrayList<Equivalent> equival = new ArrayList<>();

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);

//                System.out.println("Video ID [raw= " + country[0] + " , videoid_sonic=" + country[1] + "]");
                Equivalent e = new Equivalent(country[0],country[1]);
                equival.add(e);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //end read combinations

        StringTokenizer st ;
        BufferedReader TSVFile = new BufferedReader(new FileReader("C:/Users/ruzieljonm/Documents/Fouth Year/2019/Sonic2019/src/main/resources/agegroup_3_extro2nd.tsv")); //need pa ni ir un
        String dataRow = TSVFile.readLine(); // Read first line.
        ArrayList<Video> videos = videoService.findAllVideo();
        ArrayList<String> timeRand = new ArrayList<>();
        try (BufferedReader brtime = new BufferedReader(new FileReader("C:/Users/ruzieljonm/Documents/Fouth Year/2019/Sonic2019/src/main/resources/time.txt"))) {
            String linetime;
            while ((linetime = brtime.readLine()) != null) {
                // process the line.
                timeRand.add(linetime);
            }
        }

        ArrayList<User> users = userService.findAllUsers();

        int timeRandIndex =0;
        while (dataRow != null){

            st = new StringTokenizer(dataRow,"\t");
            List<String> dataArray = new ArrayList<String>() ;
            while(st.hasMoreElements()){
                dataArray.add(st.nextElement().toString());
            }


            dataArray.add("4336"); //user id [2]

            Random r = new Random();
            float random = 200 + r.nextFloat() * (250 - 200);
            dataArray.add(String.valueOf(random)); // time spent [3]
            dataArray.add(getLocalDate().toString());  // viewing date [4]

//
//            final Random randomt = new Random();
//            final int millisInDay = 24*60*60*1000;
//            Time time = new Time((long)randomt.nextInt(millisInDay));
//
//            dataArray.add(time.toString()); // viewing time [5]

        // start find equi
            String findme = dataArray.get(1);

            Equivalent resEq = equival.stream()
                    .filter(eq -> findme.equals(eq.getJam_id()))
                    .findAny()
                    .orElse(null);

            // end find equi

            if(timeRandIndex==timeRand.size()-1){
                timeRandIndex=0;
            }

//            System.out.println(dataArray.get(0));
//            System.out.println(resEq.getSonic_id());
//            System.out.println(dataArray.get(2));
//            System.out.println(dataArray.get(3));
//            System.out.println(dataArray.get(4));
//            System.out.println(dataArray.get(5));

            UserHistory u = new UserHistory(dataArray.get(0), resEq.getSonic_id(), dataArray.get(2), dataArray.get(3), "1", dataArray.get(4),  timeRand.get(timeRandIndex));
            userService.saveUserHistory(u);

            int genre = videoService.getGenre(resEq.getSonic_id());
            incrementagegroup(3, genre);
            incrementpersonalitygroup(2, genre);
            timeRandIndex++;







//            for (String item:dataArray) {
//                System.out.print(item + "  ");
//            }
            System.out.println(); // Print the data line.
            dataRow = TSVFile.readLine(); // Read next line of data.
        }
        // Close the file once all data has been read.
        TSVFile.close();

        // End the printout with a blank line.
        System.out.println();

    }

    public static String getTime(){
        final long CURRENT_TIME_MILLIS = System.currentTimeMillis();
        Date instant = new Date(CURRENT_TIME_MILLIS);
        SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm:ss" );
        String time = sdf.format( instant );
        System.out.println( "Time: " + time );
        return time.toString();
    }


    @RequestMapping("/updatetime")
    public void udpateTime(){
//        ArrayList<UserHistory> uh = userService.findAllUserHistory();
        for(int i=0; i<10; i++){
            System.out.println(getTime());
        }
    }

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

    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    public class singleElement{
        String videoId;
        float support;

        public singleElement(String videoId, float support) {
            this.videoId = videoId;
            this.support = support;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public float getSupport() {
            return support;
        }

        public void setSupport(float support) {
            this.support = support;
        }
    }

    public class sequenceRule {
        String videoIds;
        float support;

        public sequenceRule(String videoIds, float support) {
            this.videoIds = videoIds;
            this.support = support;
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

    public class Equivalent {

        String jam_id;
        String sonic_id;


        public Equivalent(String jam_id, String sonic_id) {
            this.jam_id = jam_id;
            this.sonic_id = sonic_id;
        }

        public String getJam_id() {
            return jam_id;
        }

        public void setJam_id(String jam_id) {
            this.jam_id = jam_id;
        }

        public String getSonic_id() {
            return sonic_id;
        }

        public void setSonic_id(String sonic_id) {
            this.sonic_id = sonic_id;
        }
    }











}

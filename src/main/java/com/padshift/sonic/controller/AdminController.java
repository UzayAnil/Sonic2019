package com.padshift.sonic.controller;

import com.padshift.sonic.entities.*;

import com.padshift.sonic.service.GenreService;
import com.padshift.sonic.service.UserService;
import com.padshift.sonic.service.VideoService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.hibernate.mapping.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.util.*;

/**
 * Created by ruzieljonm on 26/09/2018.
 */
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

    @RequestMapping("/generatePlaylist")
    public String generatePlaylist(){

        //eyyyyy i was here


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

        ArrayList<singleElement> singE = new ArrayList<>();
        for(String eOut: uniqueElements){
            singleElement temp = new singleElement(eOut,0);
            singE.add(temp);
        }

        System.out.println(singE.size());


//
        for(int i=0; i<sequencedIDs.length; i++){
            sequencedIDs[i] = new ArrayList<>();
        }

        for(int i=0; i<seqIDs.size();i++){
            for(int j=0; j<seqRules[i].size(); j++) {
               sequencedIDs[i].add(seqRules[i].get(j).getVideoid().toString());
            }
            System.out.println();
        }

        float totalSequences = seqIDs.size();
        System.out.println("TOTAL SEQUENCES : " + totalSequences);


            for(int j=0; j<singE.size(); j++){
                for(int i=0; i<seqIDs.size();i++){
                    if(sequencedIDs[i].contains(singE.get(j).getVideoId())){

                        singE.get(j).setSupport((singE.get(j).getSupport()+1));
                    }
                }
            }
        System.out.println("COUNT");
        for(int i=0; i<singE.size(); i++) {
            System.out.println( singE.get(i).getVideoId() + ","+singE.get(i).getSupport());
        }
        float totalSupport =0;
        for(int i=0; i<singE.size(); i++) {
            singE.get(i).setSupport( singE.get(i).getSupport()/totalSequences);
            totalSupport+=singE.get(i).getSupport();
        }
        System.out.println("SUPPORT");
        ArrayList<sequenceRule> seqrul = new ArrayList<>();
        for(int i=0; i<singE.size(); i++) {
           if(singE.get(i).getSupport()>(totalSupport/singE.size())) {
               System.out.println(" " + singE.get(i).getVideoId() + "," + singE.get(i).getSupport());
               sequenceRule temp = new sequenceRule(singE.get(i).getVideoId(),singE.get(i).getSupport());
               seqrul.add(temp);
           }
        }

//        //set rule from baseform + next element
//        for(int i=0; i<seqrul.size(); i++){
//            for (int j=0; j<1; j++) {
//
//                sequenceRule temp = new sequenceRule(seqrul.get(i).getVideoIds()+ ","+singE.get(j).getVideoId(),0);
//                System.out.println(temp.getVideoIds() + temp.getSupport());
//                seqrul.add(temp);
//
//            }
//
//        }

        System.out.println("SINGLE ITEM COUNT : " + singE.size());
        System.out.println("SEQUENTIAL RULES : ");
        for(int i=0; i<seqrul.size(); i++){
            System.out.println(seqrul.get(i).getVideoIds());
        }

        for(int i=0; i<seqrul.size(); i++){
            for (int j=0; j<singE.size(); j++) {
                System.out.println(seqrul.get(i).getVideoIds() + singE.get(j).getVideoId());
            }
        }









        return "testing";
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


    @RequestMapping("/vplayerpl")
    public String vplayerPL(){
        return "VideoPlayerWithPlaylist";
    }







}

package com.infinite.virtualmusicplayer.model;

import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;

import java.io.File;
import java.util.ArrayList;

public class Music {

    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String id;
    private String year;
    private String size;



    public static class Playlist{
        public String name;
        public String createdBy;
        public String createdOn;
        public ArrayList<Music> myPlaylist;
    }


    public static class MusicPlaylist{
        public ArrayList<Playlist> ref = new ArrayList<>();
    }



    public Music(String path, String title, String artist, String album, String duration, String id, String year, String size) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.id = id;
        this.year = year;
        this.size = size;
//        this.bitrate = bitrate;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }



    public static int favouriteChecker(String id) {
        MusicPlayerActivity.isFav = false;
        for (int i = 0; i < Favourite.favouriteSongs.size(); i++) {
            if (id.equals(Favourite.favouriteSongs.get(i).getId())) {
                MusicPlayerActivity.isFav = true;
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<Music> checkAndSetValidSongs(ArrayList<Music> playlist) {
        for (int i = 0; i < playlist.size(); i++) {
            File file = new File(playlist.get(i).getPath());
            if (!file.exists()){
                playlist.remove(i--);
            }
        }
        return playlist;
    }




//    public void exitApplication() {
//        if (musicService != null){
//            try {
//                audioManager.abandonAudioFocus(musicService);
//                musicService.stopForeground(true);
//                musicService.stop();
//                musicService.release();
//                musicService = null;
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }


//    public String getBitrate() {
//        return bitrate;
//    }
//
//    public void setBitrate(String bitrate) {
//        this.bitrate = bitrate;
//    }




}

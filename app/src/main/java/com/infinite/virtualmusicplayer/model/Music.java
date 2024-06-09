package com.infinite.virtualmusicplayer.model;

import com.infinite.virtualmusicplayer.activities.Favourite;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.services.MusicService;

import java.io.File;
import java.util.ArrayList;

public class Music {

    static Music music;
    MusicService musicService;
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String id;
    private String year;
    private String size;

//    private String bitrate;



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


//    public Music() {
//
//    }

    static class Playlist {
        private String name;
        private ArrayList<Music> playlist;
        private String createdBy;
        private String createdOn;

        public void setName(String name) {
            this.name = name;
        }

        public void setPlaylist(ArrayList<Music> playlist) {
            this.playlist = playlist;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        public String getName() {
            return name;
        }

        public ArrayList<Music> getPlaylist() {
            return playlist;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public String getCreatedOn() {
            return createdOn;
        }
    }

    public static class MusicPlaylist {
        private ArrayList<Playlist> ref = new ArrayList<>();

        public void setRef(ArrayList<Playlist> ref) {
            this.ref = ref;
        }

        public ArrayList<Playlist> getRef() {
            return ref;
        }
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

//    public byte[] getImageArt(String uri){
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(uri);
//        return retriever.getEmbeddedPicture();
//    }

//    public String getBitrate() {
//        return bitrate;
//    }
//
//    public void setBitrate(String bitrate) {
//        this.bitrate = bitrate;
//    }



//    public int favouriteChecker(String id) {
//        for (int index = 0; index < favouriteSongs.size(); index++){
//            MusicPlayerActivity.isFav = false;
//            music = favouriteSongs.get(index);
//            if (id.equals(music.id)){
//                MusicPlayerActivity.isFav = true;
//                return index;
//            }
//        }
//        return -1;
//    }


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


    public static ArrayList<Music> checkPlaylist(ArrayList<Music> playlist) {
        for (int i = 0; i < playlist.size(); i++) {
            File file = new File(playlist.get(i).getPath());
            if (!file.exists())
                playlist.remove(i--);
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



}

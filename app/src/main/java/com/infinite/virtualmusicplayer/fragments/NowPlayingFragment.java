package com.infinite.virtualmusicplayer.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.infinite.virtualmusicplayer.activities.MainActivity.ARTIST_TO_FRAG;
import static com.infinite.virtualmusicplayer.activities.MainActivity.PATH_TO_FRAG;
import static com.infinite.virtualmusicplayer.activities.MainActivity.SHOW_MINI_PLAYER;
import static com.infinite.virtualmusicplayer.activities.MainActivity.SONG_NAME_TO_FRAG;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isPlaying;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.position;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.thumb;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.services.MusicService;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MainActivity;

import java.io.IOException;


public class NowPlayingFragment extends Fragment implements ServiceConnection {

    @SuppressLint("StaticFieldLeak")
    public static ImageView playPauseBtn;
    @SuppressLint("StaticFieldLeak")
    public static ImageView nextBtn;
    @SuppressLint("StaticFieldLeak")
    public static ImageView prevBtn;
    @SuppressLint("StaticFieldLeak")
    public static ImageView miniPlayerCoverArt;
    @SuppressLint("StaticFieldLeak")
    public static TextView artistName;
    @SuppressLint("StaticFieldLeak")
    public static TextView songName;
    @SuppressLint("StaticFieldLeak")
    boolean isExpanded = false;
    public static RelativeLayout mini_player;
    public static LinearProgressIndicator mini_player_progressBar;
    View view;
    MusicService musicService;
    MainActivity mainActivity;

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";


    public NowPlayingFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        songName = view.findViewById(R.id.song_name_mini_player);
        artistName = view.findViewById(R.id.song_artist_mini_player);
        miniPlayerCoverArt = view.findViewById(R.id.bottom_art);
        nextBtn = view.findViewById(R.id.nextBtn_bottom);
        prevBtn = view.findViewById(R.id.previousBtn_bottom);
        playPauseBtn = view.findViewById(R.id.play_pause_miniplayer);
        mini_player = view.findViewById(R.id.mini_player);
        mini_player_progressBar = view.findViewById(R.id.mini_player_progressBar);

        songName.setSelected(true);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (musicService != null) {
                        try {
                            musicService.nextBtnClicked();
                            isPlaying = true;
                            showMiniPlayer();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();

                }
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (musicService != null) {
                        try {
                            musicService.prevBtnClicked();
                            isPlaying = true;
                            showMiniPlayer();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();

                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (musicService != null  &&  SHOW_MINI_PLAYER) {
                        try {
                            musicService.playPauseBtnClicked();
                        }catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();

                        }

                        isPlaying = musicService.isPlaying();

//                        showMiniPlayer();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();

                }
            }
        });


        mini_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (musicService != null  &&  SHOW_MINI_PLAYER){
                        try {
                            Intent intent = new Intent(requireContext(), MusicPlayerActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("nowPlaying","NowPlaying");
                            ContextCompat.startActivity(requireContext(), intent, null);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();

                        }
                    }
                    else {
                        try {
                            Toast.makeText(getContext(), "No Composition Found ", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(getContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();

                }
            }
        });



        return view;

    }



    public void showMiniPlayer() {
        if (getActivity() != null){
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE).edit();
            editor.putString(MUSIC_FILE, MusicService.musicServiceFiles.get(MusicService.currentSongIndex).getPath());
            editor.putString(ARTIST_NAME, MusicService.musicServiceFiles.get(MusicService.currentSongIndex).getArtist());
            editor.putString(SONG_NAME, MusicService.musicServiceFiles.get(MusicService.currentSongIndex).getTitle());
            editor.apply();

            SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
            String path = preferences.getString(MUSIC_FILE, null);
            String artistName = preferences.getString(ARTIST_NAME, null);
            String song_name = preferences.getString(SONG_NAME, null);
            if (path != null){
                SHOW_MINI_PLAYER = true;
                PATH_TO_FRAG = path;
                ARTIST_TO_FRAG = artistName;
                SONG_NAME_TO_FRAG = song_name;
            }
            else {
                SHOW_MINI_PLAYER = false;
                PATH_TO_FRAG = null;
                ARTIST_TO_FRAG = null;
                SONG_NAME_TO_FRAG = null;
            }

            if (SHOW_MINI_PLAYER){

                if (thumb != null){
                    Glide.with(requireContext()).load(thumb).into(miniPlayerCoverArt);
                }
                else {
                    Glide.with(requireContext()).load(R.drawable.music_note).into(miniPlayerCoverArt);
                }
                songName.setText(SONG_NAME_TO_FRAG);
                NowPlayingFragment.artistName.setText(ARTIST_TO_FRAG);

                if (musicService != null){
                    if (musicService.isPlaying()){
                        playPauseBtn.setImageResource(R.drawable.pause_bottom);
                    }
                    else {
                        playPauseBtn.setImageResource(R.drawable.play_bottom);
                    }
                }
                else {
                    playPauseBtn.setImageResource(R.drawable.play_bottom);
                }


            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (SHOW_MINI_PLAYER){
            if (PATH_TO_FRAG != null){
                byte[] art = getAlbumArt(PATH_TO_FRAG);
                if (art != null){
                    Glide.with(requireContext()).load(art).into(miniPlayerCoverArt);
                }
                else {
                    Glide.with(requireContext()).load(R.drawable.music_note).into(miniPlayerCoverArt);
                }
                songName.setText(SONG_NAME_TO_FRAG);
                artistName.setText(ARTIST_TO_FRAG);


                Intent intent = new Intent(getContext(), MusicService.class);
                if (getContext() != null){
                    getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
                }
            }
        }
    }



    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        byte[] art = null;
        try {
            retriever.setDataSource(uri);
            art = retriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return art;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

//    @Override
//    public void playPauseBtnClicked() {
//
//    }
//
//    @Override
//    public void nextBtnClicked() {
//        showMiniPlayer();
//    }
//
//    @Override
//    public void prevBtnClicked() {
//        showMiniPlayer();
//    }
}
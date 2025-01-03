package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.tracks;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validSongs;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.adapters.MusicAdapter;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SongsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView noSongsFound;
    private FloatingActionButton shuffleExtendedFab, fabUp;
    private boolean isFabUpVisible = false;
//    private LinearLayoutManager layoutManager;

    int mainPosition = 0;

    @SuppressLint("StaticFieldLeak")
    public static MusicAdapter musicAdapter;

    public SongsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize views
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);
        noSongsFound = view.findViewById(R.id.no_songs_found);

        shuffleExtendedFab = view.findViewById(R.id.shuffleFab);
        fabUp = view.findViewById(R.id.UpFab);

        // Validate songs
        validSongs = Music.checkAndSetValidSongs(tracks);

        musicAdapter = new MusicAdapter(getContext(), validSongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setHasFixedSize(true);

        // Initial setup for songs
        setUpMusic();

        // Swipe-to-refresh setup
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);

        shuffleExtendedFab.setOnClickListener(view1 -> playAllSongs());


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                validSongs = Music.checkAndSetValidSongs(tracks);

                Toast.makeText(getContext(), validSongs.size() + "Songs found", Toast.LENGTH_SHORT).show();

                swipeRefreshLayout.setRefreshing(false);
            }
        });


        // Scroll listener for RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    hideFab();

                } else if (dy < 0) {
                    showFab();
                }

//                if (layoutManager.findLastCompletelyVisibleItemPosition() > 7) {
//                    if (!isFabUpVisible) showFabUp();
//                } else {
//                    if (isFabUpVisible) hideFabUp();
//                }
            }
        });

        // Scroll-to-top button
        fabUp.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));

        return view;
    }



    @SuppressLint("NotifyDataSetChanged")
    private void setUpMusic() {
        validSongs = Music.checkAndSetValidSongs(tracks);

        if (validSongs != null && !validSongs.isEmpty()) {
            noSongsFound.setVisibility(View.GONE);
            shuffleExtendedFab.setVisibility(View.VISIBLE);

            // Update adapter and RecyclerView
            musicAdapter = new MusicAdapter(getContext(), validSongs);
            recyclerView.setAdapter(musicAdapter);
            musicAdapter.notifyDataSetChanged();

            int cacheSize = validSongs.size();
            recyclerView.setItemViewCacheSize(cacheSize);
        }
        else {
            shuffleExtendedFab.setVisibility(View.INVISIBLE);
            noSongsFound.setVisibility(View.VISIBLE);
        }
    }


    private void playAllSongs() {
        isShuffle = true;
        if (!isLoop) {
            mainPosition = getRandom(tracks.size() - 1);
        }
        if (!tracks.isEmpty()) {
            Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
            intent.putExtra("position", mainPosition);
            intent.putExtra("mainPlayIntent", "MainPlayIntent");
            startActivity(intent);
        }
        else {
            Toast.makeText(getContext(), "You have not any song", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideFab() {
        ObjectAnimator.ofFloat(shuffleExtendedFab, "translationX", 0).start();
    }

    private void showFab() {
        ObjectAnimator.ofFloat(shuffleExtendedFab, "translationY", 0).start();
    }

//    private void hideFabUp() {
//        isFabUpVisible = false;
//        fabUp.setVisibility(View.GONE);
//    }
//
//    private void showFabUp() {
//        isFabUpVisible = true;
//        fabUp.setVisibility(View.VISIBLE);
//    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView == null){
            setUpMusic();
        }
    }

}
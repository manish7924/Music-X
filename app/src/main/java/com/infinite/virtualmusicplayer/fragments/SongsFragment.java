package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.musicFiles;
import static com.infinite.virtualmusicplayer.activities.MainActivity.tracks;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validSongs;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.adapters.MusicAdapter;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.Random;

public class SongsFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView noSongsFound;
    private ExtendedFloatingActionButton shuffleExtendedFab;
    private FloatingActionButton fabUp;
//    private boolean isFabVisible = true;
    private boolean isFabUpVisible = false;
    private LinearLayoutManager layoutManager;
//    private ViewGroup alertdialog;
    int mainPosition = 0;

    @SuppressLint("StaticFieldLeak")
    public static MusicAdapter musicAdapter;

    public SongsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);
        noSongsFound = view.findViewById(R.id.no_songs_found);

        shuffleExtendedFab = view.findViewById(R.id.shuffleFab);
        fabUp = view.findViewById(R.id.UpFab);


        validSongs = Music.checkAndSetValidSongs(musicFiles);

        musicAdapter = new MusicAdapter(getContext(), validSongs);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setHasFixedSize(true);

        setUpMusic();

        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);
        shuffleExtendedFab.setAnimateShowBeforeLayout(true);

        shuffleExtendedFab.setOnClickListener(view1 -> playAllSongs());


        swipeRefreshLayout.setOnRefreshListener(() -> {
            validSongs = Music.checkAndSetValidSongs(tracks);

            swipeRefreshLayout.setRefreshing(false);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) hideFab();
                else if (dy < 0) showFab();
                if (layoutManager.findLastCompletelyVisibleItemPosition() > 7) {
                    if (!isFabUpVisible) showFabUp();
                } else {
                    if (isFabUpVisible) hideFabUp();
                }
            }
        });

        fabUp.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));

        return view;
    }

    public void setUpMusic() {
        if (musicFiles != null && !musicFiles.isEmpty()) {
            noSongsFound.setVisibility(View.GONE);
            shuffleExtendedFab.setVisibility(View.VISIBLE);

            int cacheSize = musicFiles.size();
            recyclerView.setItemViewCacheSize(cacheSize);
        } else {
            shuffleExtendedFab.setVisibility(View.INVISIBLE);
            noSongsFound.setVisibility(View.VISIBLE);
        }
    }

    private void playAllSongs() {
        isShuffle = true;
        if (!isLoop) mainPosition = getRandom(musicFiles.size() - 1);
        if (!musicFiles.isEmpty()) {
            Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
            intent.putExtra("position", mainPosition);
            intent.putExtra("mainPlayIntent", "MainPlayIntent");
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "You have not any song", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideFab() {
        shuffleExtendedFab.setExtended(false);
        ObjectAnimator.ofFloat(shuffleExtendedFab, "translationX", 0).start();
    }

    private void showFab() {
        shuffleExtendedFab.setExtended(true);
        ObjectAnimator.ofFloat(shuffleExtendedFab, "translationY", 0).start();
    }

    private void hideFabUp() {
        isFabUpVisible = false;
        fabUp.setVisibility(View.GONE);
    }

    private void showFabUp() {
        isFabUpVisible = true;
        fabUp.setVisibility(View.VISIBLE);
    }


    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }


}
package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.albums;
import static com.infinite.virtualmusicplayer.activities.MainActivity.artists;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.adapters.ArtistAdapter;

import java.util.Random;

public class ArtistFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;
    public static ProgressBar progressBarArtist;

    RecyclerView recyclerView;
    TextView noArtistsFound;
    FloatingActionButton artistShuffleBtn;

    public static ArtistAdapter artistAdapter;
    int mainPosition = 0;

    public ArtistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBarArtist = view.findViewById(R.id.progressbarArtist);
        noArtistsFound = view.findViewById(R.id.no_artists_found);
        artistShuffleBtn = view.findViewById(R.id.artistShuffleBtn);


        artistAdapter = new ArtistAdapter(getContext(), artists);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(artistAdapter);
        recyclerView.setHasFixedSize(true);

        // set color
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);


        artistShuffleBtn.setOnClickListener(view1 -> playAllSongs());


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {
                    CountDownTimer count = new CountDownTimer(100, 400) {
                        @Override
                        public void onTick(long l) {
//                            progressBarArtist.setVisibility(View.VISIBLE);
                            recyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));

                        }

                        @Override
                        public void onFinish() {
//                            progressBarArtist.setVisibility(View.GONE);
                            recyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

                            Toast.makeText(getContext(), artists.size() +  " Artists Found", Toast.LENGTH_SHORT).show();

//                            Snackbar.make(swipeRefreshLayout, musicFiles.size()+" Songs Found", Snackbar.LENGTH_SHORT)
//                                    .setAction("OK", view -> {}).setActionTextColor(Color.parseColor("#00B0FF")).show();

                        }

                    };
                    count.start();
                } catch (Exception e){
                    e.printStackTrace();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });

//            ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(artistAdapter);
//            scaleInAnimationAdapter.setDuration(40);
//            scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
//            scaleInAnimationAdapter.setFirstOnly(true);
//            recyclerView.setAdapter(scaleInAnimationAdapter);


        if (artists != null  &&  artists.size() >= 1)
        {
            noArtistsFound.setVisibility(View.GONE);
            int cacheSize = artists.size();
            recyclerView.setItemViewCacheSize(cacheSize);
//        recyclerView.setHorizontalScrollBarEnabled(true);
            recyclerView.setVerticalScrollBarEnabled(true);
            recyclerView.setScrollbarFadingEnabled(true);
            recyclerView.setScrollContainer(true);

        }
        else {
//            playMainBtn.setVisibility(View.INVISIBLE);
            noArtistsFound.setVisibility(View.VISIBLE);
        }


        return view;
    }


    private void playAllSongs() {
        isShuffle = true;
        if (!isLoop) {
            mainPosition = getRandom(artists.size() - 1);
        }
        if (!(artists.size() < 1)){
            Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
            intent.putExtra("position", mainPosition);
            intent.putExtra("mainPlayIntent", "MainPlayIntent");
            startActivity(intent);


        }else {
            Toast.makeText(getContext(), "You hava not any song", Toast.LENGTH_SHORT).show();
        }



    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }
}

package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.musicFiles;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.adapters.MusicAdapter;

import java.util.Random;


public class SongsFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    TextView noSongsFound;

    private ViewGroup alertdialog;
    int mainPosition = 0;
    static FloatingActionButton playMainBtn;

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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);
        playMainBtn = view.findViewById(R.id.playMainBtn);
        noSongsFound = view.findViewById(R.id.no_songs_found);


        musicAdapter = new MusicAdapter(getContext(), musicFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(musicAdapter);
        recyclerView.setHasFixedSize(true);





        playMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAllSongs();
            }
        });




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {
                    CountDownTimer count = new CountDownTimer(0, 100) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            swipeRefreshLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_refresh));

                            Snackbar.make(swipeRefreshLayout, musicFiles.size()+" Songs Found", Snackbar.LENGTH_SHORT)
                                    .setAction("OK", view -> {}).setActionTextColor(Color.parseColor("#00B0FF")).show();


                        }

                    };
                    count.start();
                } catch (Exception e){
                    e.printStackTrace();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });






//        networkStreamBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                customNetworkStreamDialog();
//            }
//        });


        //            if (musicService != null){
//                playMainBtn.setVisibility(View.INVISIBLE);
//            }
//            else {
//                playMainBtn.setVisibility(View.VISIBLE);
//            }


//            ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(musicAdapter);
//            scaleInAnimationAdapter.setDuration(40);
//            scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
//            scaleInAnimationAdapter.setFirstOnly(true);
//            recyclerView.setAdapter(scaleInAnimationAdapter);




        if (musicFiles != null  &&  musicFiles.size() >= 1)
        {
            noSongsFound.setVisibility(View.GONE);

            int cacheSize = musicFiles.size();
            recyclerView.setItemViewCacheSize(cacheSize);
            recyclerView.setVerticalScrollBarEnabled(true);
            recyclerView.setScrollbarFadingEnabled(true);
            recyclerView.setScrollContainer(true);

        }

        else {
            playMainBtn.setVisibility(View.INVISIBLE);
            noSongsFound.setVisibility(View.VISIBLE);
        }
        return view;
    }



    private void playAllSongs() {
        isShuffle = true;
        if (!isLoop) {
            mainPosition = getRandom(musicFiles.size() - 1);
        }
        if (!(musicFiles.size() < 1)){
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

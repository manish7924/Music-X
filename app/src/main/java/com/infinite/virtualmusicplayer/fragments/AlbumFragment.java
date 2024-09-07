package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.albums;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.adapters.AlbumAdapter;

import java.util.Random;


public class AlbumFragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView recyclerView;
    TextView noAlbumsFound;
    FloatingActionButton albumShuffleBtn;
    int mainPosition = 0;
    public static AlbumAdapter albumAdapter;

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_album, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        recyclerView = view.findViewById(R.id.recyclerView);
        noAlbumsFound = view.findViewById(R.id.no_albums_found);
        albumShuffleBtn = view.findViewById(R.id.albumShuffleBtn);


        albumAdapter = new AlbumAdapter(getContext(), albums);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(albumAdapter);
        recyclerView.setHasFixedSize(true);


        albumShuffleBtn.setOnClickListener(view1 -> playAllSongs());



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

//                            Snackbar.make(swipeRefreshLayout, albums.size()+" Albums Found", Snackbar.LENGTH_SHORT)
//                                    .setAction("OK", view -> {}).setActionTextColor(Color.parseColor("#00B0FF")).show();

                            Toast.makeText(getContext(), albums.size() +  " Albums Found", Toast.LENGTH_SHORT).show();


                        }

                    };
                    count.start();
                } catch (Exception e){
                    e.printStackTrace();
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });


        if (albums != null  &&  albums.size() >= 1)
        {
            noAlbumsFound.setVisibility(View.GONE);
            int cacheSize = albums.size();
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(cacheSize);
//        recyclerView.setHorizontalScrollBarEnabled(true);
            recyclerView.setVerticalScrollBarEnabled(true);
            recyclerView.setScrollbarFadingEnabled(true);
            recyclerView.setScrollContainer(true);

        }
        else {
//            playMainBtn.setVisibility(View.INVISIBLE);
            noAlbumsFound.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void playAllSongs() {
        isShuffle = true;
        if (!isLoop) {
            mainPosition = getRandom(albums.size() - 1);
        }
        if (!(albums.size() < 1)){
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
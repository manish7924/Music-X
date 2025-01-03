package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.albums;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validAlbums;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.adapters.AlbumAdapter;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.Random;


public class AlbumFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private TextView noAlbumsFound;
    private FloatingActionButton shuffleExtendedFab, fabUp;

    private boolean isFabVisible = true; // to track the main FAB visibility
    private boolean isFabUpVisible = false; // to
//    private LinearLayoutManager layoutManager;

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

        shuffleExtendedFab = view.findViewById(R.id.shuffleFab);
        fabUp = view.findViewById(R.id.UpFab);

        // Validate Albums
        validAlbums = Music.checkAndSetValidSongs(albums);

        albumAdapter = new AlbumAdapter(getContext(), validAlbums);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(albumAdapter);
        recyclerView.setHasFixedSize(true);

        // Initial setup for songs
        setUpAlbums();

        // Swipe-to-refresh setup
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);

        shuffleExtendedFab.setOnClickListener(view1 -> playAllSongs());


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                validAlbums = Music.checkAndSetValidSongs(albums);

                swipeRefreshLayout.setRefreshing(false);
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Hide the main FAB when scrolling down, show it when scrolling up
                if (dy > 0) {
                    hideFab();

                } else if (dy < 0) {
                    showFab();
                }

                // Show the "Up Arrow" FAB when scrolled beyond a certain point
//                if (layoutManager.findLastCompletelyVisibleItemPosition() > 7) {
//                    if (!isFabUpVisible) {
//                        showFabUp();
//                    }
//                } else {
//                    if (isFabUpVisible) {
//                        hideFabUp();
//                    }
//                }
            }
        });




        // Set onClickListener for the "Up Arrow" FAB to scroll to the top
//        fabUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerView.smoothScrollToPosition(0); // Scroll to the top
//            }
//        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUpAlbums() {
        validAlbums = Music.checkAndSetValidSongs(albums);

        if (validAlbums != null  && !validAlbums.isEmpty()) {
            noAlbumsFound.setVisibility(View.GONE);
            shuffleExtendedFab.setVisibility(View.VISIBLE);

            // Update adapter and RecyclerView
            albumAdapter = new AlbumAdapter(getContext(), validAlbums);
            recyclerView.setAdapter(albumAdapter);
            albumAdapter.notifyDataSetChanged();

            int cacheSize = validAlbums.size();
            recyclerView.setItemViewCacheSize(cacheSize);

        } else {
            shuffleExtendedFab.setVisibility(View.INVISIBLE);
            noAlbumsFound.setVisibility(View.VISIBLE);
        }

    }


    private void playAllSongs() {
        isShuffle = true;
        if (!isLoop) {
            mainPosition = getRandom(albums.size() - 1);
        }
        if (!(albums.isEmpty())){
            Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
            intent.putExtra("position", mainPosition);
            intent.putExtra("mainPlayIntent", "MainPlayIntent");
            startActivity(intent);
        }
        else {
            Toast.makeText(getContext(), "You hava not any song", Toast.LENGTH_SHORT).show();
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
//        fabUp.setVisibility(View.GONE); // Hide "Up Arrow" FAB
//    }
//
//    private void showFabUp() {
//        isFabUpVisible = true;
//        fabUp.setVisibility(View.VISIBLE); // Show "Up Arrow" FAB
//    }



    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView == null){
            setUpAlbums();
        }
    }

}
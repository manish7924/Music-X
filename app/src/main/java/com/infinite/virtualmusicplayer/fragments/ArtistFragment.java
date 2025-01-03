package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.artists;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validArtists;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.adapters.AlbumAdapter;
import com.infinite.virtualmusicplayer.adapters.ArtistAdapter;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.Random;

public class ArtistFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;
    private TextView noArtistsFound;


    private FloatingActionButton shuffleExtendedFab, fabUp;

    private boolean isFabVisible = true; // to track the main FAB visibility
    private boolean isFabUpVisible = false; // to

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
        noArtistsFound = view.findViewById(R.id.no_artists_found);

        shuffleExtendedFab = view.findViewById(R.id.shuffleFab);
        fabUp = view.findViewById(R.id.UpFab);
        recyclerView = view.findViewById(R.id.recyclerView);


        validArtists = Music.checkAndSetValidSongs(artists);


        artistAdapter = new ArtistAdapter(getContext(), validArtists);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(artistAdapter);
        recyclerView.setHasFixedSize(true);

        setUpArtists();


        // set color
        swipeRefreshLayout.setColorSchemeColors(Color.BLACK);


        shuffleExtendedFab.setOnClickListener(view1 -> playAllSongs());


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                validArtists = Music.checkAndSetValidSongs(artists);

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
//                if (GridLayoutManager > 7) {
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
    public void setUpArtists() {
        validArtists = Music.checkAndSetValidSongs(artists);

        if (validArtists != null  && !validArtists.isEmpty())
        {
            noArtistsFound.setVisibility(View.GONE);
            shuffleExtendedFab.setVisibility(View.VISIBLE);

            artistAdapter = new ArtistAdapter(getContext(), validSongs);
            recyclerView.setAdapter(artistAdapter);
            artistAdapter.notifyDataSetChanged();

            int cacheSize = artists.size();
            recyclerView.setItemViewCacheSize(cacheSize);

        }
        else {
            shuffleExtendedFab.setVisibility(View.INVISIBLE);
            noArtistsFound.setVisibility(View.VISIBLE);
        }

    }


    private void playAllSongs() {
        isShuffle = true;
        if (!isLoop) {
            mainPosition = getRandom(artists.size() - 1);
        }
        if (!(artists.isEmpty())){
            Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
            intent.putExtra("position", mainPosition);
            intent.putExtra("mainPlayIntent", "MainPlayIntent");
            startActivity(intent);


        }else {
            Toast.makeText(getContext(), "You hava not any song", Toast.LENGTH_SHORT).show();
        }

    }


    private void hideFab() {
        ObjectAnimator.ofFloat(shuffleExtendedFab, "translationX", 0).start();
    }

    private void showFab() {
        ObjectAnimator.ofFloat(shuffleExtendedFab, "translationY", 0).start();
    }

    private void hideFabUp() {
        isFabUpVisible = false;
        fabUp.setVisibility(View.GONE); // Hide "Up Arrow" FAB
    }

    private void showFabUp() {
        isFabUpVisible = true;
        fabUp.setVisibility(View.VISIBLE); // Show "Up Arrow" FAB
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView == null){
            setUpArtists();
        }
    }


}

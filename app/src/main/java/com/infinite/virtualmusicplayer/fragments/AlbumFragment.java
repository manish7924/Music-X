package com.infinite.virtualmusicplayer.fragments;

import static com.infinite.virtualmusicplayer.activities.MainActivity.albums;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.infinite.virtualmusicplayer.adapters.AlbumAdapter;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.R;

import java.util.Random;


public class AlbumFragment extends Fragment {

    RecyclerView recyclerView;
    TextView noAlbumsFound;
    ImageView albumShuffleBtn, albumGridBtn;
    int mainPosition = 0;
    public static AlbumAdapter albumAdapter;

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        noAlbumsFound = view.findViewById(R.id.no_albums_found);
        albumShuffleBtn = view.findViewById(R.id.album_shuffleBtn);
        albumGridBtn = view.findViewById(R.id.album_gridBtn);


        albumAdapter = new AlbumAdapter(getContext(), albums);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(albumAdapter);
        recyclerView.setHasFixedSize(true);


        albumShuffleBtn.setOnClickListener(view1 -> playAllSongs());

//        albumGridBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                albumAdapter = new AlbumAdapter(getContext(), albums);
////                recyclerView.setAdapter(albumAdapter);
////                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//
//            }
//        });


        //            ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(albumAdapter);
        //            scaleInAnimationAdapter.setDuration(40);
//            scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());
//            scaleInAnimationAdapter.setFirstOnly(true);
//            recyclerView.setAdapter(scaleInAnimationAdapter);


        if (!(albums.size() < 1))
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
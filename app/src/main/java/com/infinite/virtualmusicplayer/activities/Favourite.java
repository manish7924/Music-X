package com.infinite.virtualmusicplayer.activities;

import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.adapters.FavouriteAdapter;
import com.infinite.virtualmusicplayer.model.Music;
import com.infinite.virtualmusicplayer.R;

import java.util.ArrayList;
import java.util.Random;

public class Favourite extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noFavSongsFound;
    int favPosition = 0;
    FloatingActionButton shuffleBtnFav;
    @SuppressLint("StaticFieldLeak")
    static FavouriteAdapter favouriteAdapter;

    public static ArrayList<Music> favouriteSongs = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        recyclerView = findViewById(R.id.favouriteRv);
        noFavSongsFound = findViewById(R.id.no_fav_songs_found);
        shuffleBtnFav = findViewById(R.id.shuffleBtnFav);


        shuffleBtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playFavSongs();
            }
        });



        if (!(favouriteSongs.size() < 1))
        {
            favouriteAdapter = new FavouriteAdapter(this, favouriteSongs);
            recyclerView.setAdapter(favouriteAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(16);
            recyclerView.setHorizontalScrollBarEnabled(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            noFavSongsFound.setVisibility(View.INVISIBLE);
            shuffleBtnFav.setVisibility(View.VISIBLE);

//            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }else {
            shuffleBtnFav.setVisibility(View.INVISIBLE);
            noFavSongsFound.setVisibility(View.VISIBLE);
        }

    }

    private void playFavSongs() {
        isShuffle = true;
        if (!isLoop) {
            favPosition = getRandom(favouriteSongs.size() - 1);
        }
        if (!(favouriteSongs.size() < 1)){
            Intent intent = new Intent(Favourite.this, MusicPlayerActivity.class);
            intent.putExtra("position", favPosition);
            intent.putExtra("favShuffle", "FavShuffle");
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "You have not favourite songs yet", Toast.LENGTH_SHORT).show();
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void favBackBtn(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        super.onBackPressed();
        finish();
    }
}
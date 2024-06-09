package com.infinite.virtualmusicplayer.activities;

import static com.infinite.virtualmusicplayer.activities.MainActivity.musicFiles;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.adapters.ArtistDetailsAdapter;
import com.infinite.virtualmusicplayer.model.Music;
import com.infinite.virtualmusicplayer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ArtistDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView artistPhoto;
    ImageView moreOptions;
    FloatingActionButton artistPlayBtn;
    int mainPosition = 0;
    String artistName;
    TextView artistTitle, artistNoOfSongs;
    ArrayList<Music> artistSongs = new ArrayList<>();
    ArtistDetailsAdapter artistDetailsAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_artist_details);


        recyclerView = findViewById(R.id.recyclerView);
        artistPhoto = findViewById(R.id.artistPhoto);
        artistPlayBtn = findViewById(R.id.artistPlayBtn);
        moreOptions = findViewById(R.id.artist_menu);
        artistTitle = findViewById(R.id.artistTitle);
        artistNoOfSongs = findViewById(R.id.no_of_songs_and_duration);

//        getWindow().setStatusBarColor(getResources().getColor(R.color.wb));

        artistTitle.setText(getIntent().getStringExtra("artistTitle"));

        artistName = getIntent().getStringExtra("artistName");

        int j = 0;
        for (int i = 0; i < musicFiles.size(); i++)
        {
            if (artistName.equals(musicFiles.get(i).getArtist()))
            {
                artistSongs.add(j, musicFiles.get(i));
                j++;

                String NoOfSong = String.valueOf(artistSongs.size());
                artistNoOfSongs.setText(NoOfSong + " Songs");
            }
        }
        byte[] image = getAlbumArt(artistSongs.get(0).getPath());
        if (image != null)
        {
            Glide.with(this)
                    .load(image)
                    .into(artistPhoto);
        }
        else {
            Glide.with(this)
                    .load(R.drawable.artist_art)
                    .into(artistPhoto);
        }


        moreOptions.setOnClickListener(view -> {
//                Toast.makeText(ArtistDetails.this, "Menu", Toast.LENGTH_SHORT).show();
            PopupMenu popupMenu = new PopupMenu(ArtistDetails.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.album_menu_more, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener((item) -> {
                if (item.getItemId() == R.id.add_to_playlist) {
                    Toast.makeText(ArtistDetails.this, "Coming soon", Toast.LENGTH_SHORT).show();
                }

                else if (item.getItemId() == R.id.playNext) {
                    Toast.makeText(ArtistDetails.this, "Coming soon", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.albumShuffleBtn) {
                    playShuffledSong();
                }

                else if (item.getItemId() == R.id.share) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("audio/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(artistSongs.get(mainPosition).getPath()));
                    startActivity(Intent.createChooser(shareIntent, "Sharing Music File via"));
                }
                return true;
            });
        });


        artistPlayBtn.setOnClickListener(view -> playAllSongs());

    }

    private void playShuffledSong() {
        isShuffle = true;
        if (!isLoop) {
            mainPosition = getRandom(artistSongs.size() - 1);
        }
        if (!(artistSongs.size() < 1)){
            Intent intent = new Intent(ArtistDetails.this, MusicPlayerActivity.class);
            intent.putExtra("artistSender", "artistDetails");
            intent.putExtra("position", mainPosition);
            this.startActivity(intent);

        }else {
            Toast.makeText(ArtistDetails.this, "You hava not any song", Toast.LENGTH_SHORT).show();
        }

    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }


    private void playAllSongs() {
        if (!(artistSongs.size() < 1)){
            if (isLoop || isShuffle){
                if (isLoop){
                    isLoop = false;
                }
                if (isShuffle){
                    isShuffle = false;
                }
            }
            try {
                Intent intent = new Intent(this, MusicPlayerActivity.class);
                intent.putExtra("artistSender", "artistDetails");
                intent.putExtra("position", mainPosition);
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void setFullScreen() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return art;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!(artistSongs.size() < 1))
        {
            artistDetailsAdapter = new ArtistDetailsAdapter(this, artistSongs);
            recyclerView.setAdapter(artistDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }

    public void backToHome(View view) {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
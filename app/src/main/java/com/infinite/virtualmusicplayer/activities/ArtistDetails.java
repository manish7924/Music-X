package com.infinite.virtualmusicplayer.activities;

import static com.infinite.virtualmusicplayer.activities.MainActivity.albums;
import static com.infinite.virtualmusicplayer.activities.MainActivity.artists;
import static com.infinite.virtualmusicplayer.activities.MainActivity.musicFiles;
import static com.infinite.virtualmusicplayer.activities.MainActivity.tracks;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validAlbums;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validArtists;
import static com.infinite.virtualmusicplayer.activities.MainActivity.validSongs;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isLoop;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isShuffle;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.adapters.ArtistDetailsAdapter;
import com.infinite.virtualmusicplayer.model.Music;

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
//        setFullScreen();
//        EdgeToEdge.enable(this);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_artist_details);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mArtistContainer), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

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
            assert artistName != null;
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
                    .override(500,500)
                    .into(artistPhoto);
        }
        else {
            Glide.with(this)
                    .load(R.drawable.artist_art)
                    .override(200,200)
                    .into(artistPhoto);
        }

        validSongs = Music.checkAndSetValidSongs(tracks);
        validAlbums = Music.checkAndSetValidSongs(albums);
        validArtists = Music.checkAndSetValidSongs(artists);


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
        if (!(artistSongs.isEmpty())){
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
        if (!(artistSongs.isEmpty())){
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

//    private void setFullScreen() {
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        byte[] art = null;
        try {
            retriever.setDataSource(uri);
            art = retriever.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return art;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!(artistSongs.isEmpty()))
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
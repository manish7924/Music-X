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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.adapters.AlbumDetailsAdapter;
import com.infinite.virtualmusicplayer.model.Music;

import java.util.ArrayList;
import java.util.Random;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    ImageView moreOptions;
    FloatingActionButton albumPlayBtn;
    int mainPosition = 0;
    String albumName;
    TextView noOfAlbumSong;
    TextView albumTitle, albumYear;
//    private Palette mAlbumPalette;
    ArrayList<Music> albumSongs = new ArrayList<>();

    AlbumDetailsAdapter albumDetailsAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_album_details);

        recyclerView = findViewById(R.id.recyclerView);
        albumPhoto = findViewById(R.id.albumPhoto);
        albumPlayBtn = findViewById(R.id.albumPlayBtn);
        moreOptions = findViewById(R.id.album_menu);
        albumTitle = findViewById(R.id.albumTitle);
        albumYear = findViewById(R.id.album_year);
        noOfAlbumSong = findViewById(R.id.no_of_album_songs);

//        getWindow().setStatusBarColor(getResources().getColor(R.color.bw));


        albumTitle.setText(getIntent().getStringExtra("albumTitle"));
        albumYear.setText(getIntent().getStringExtra("albumYear"));

        albumName = getIntent().getStringExtra("albumName");


        int j = 0;

        for (int i = 0; i < musicFiles.size(); i++)
        {
            assert albumName != null;
            if (albumName.equals(musicFiles.get(i).getAlbum()))
            {
                albumSongs.add(j, musicFiles.get(i));
                j++;

                String NoOfSong = String.valueOf(albumSongs.size());
                noOfAlbumSong.setText("("+ NoOfSong +")");
            }

        }



        byte[] image = getAlbumArt(albumSongs.get(0).getPath());
        if (image != null)
        {
            Glide.with(this)
                    .load(image)
                    .override(500,500)
                    .placeholder(R.drawable.album_art)
                    .into(albumPhoto);
        }
        else
        {
            Glide.with(this)
                    .load(R.drawable.album_art)
                    .override(200,200)
                    .into(albumPhoto);
        }


        validSongs = Music.checkAndSetValidSongs(tracks);
        validAlbums = Music.checkAndSetValidSongs(albums);
        validArtists = Music.checkAndSetValidSongs(artists);


        moreOptions.setOnClickListener(view -> {
//                Toast.makeText(AlbumDetails.this, "Menu", Toast.LENGTH_SHORT).show();
            PopupMenu popupMenu = new PopupMenu(AlbumDetails.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.album_menu_more, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener((item) -> {
                if (item.getItemId() == R.id.add_to_playlist) {
                    Toast.makeText(AlbumDetails.this, "Coming soon", Toast.LENGTH_SHORT).show();
                }

                else if (item.getItemId() == R.id.playNext) {
                    Toast.makeText(AlbumDetails.this, "Coming soon", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.albumShuffleBtn) {
                    playShuffledSong();
                }

                else if (item.getItemId() == R.id.share) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("audio/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(albumSongs.get(mainPosition).getPath()));
                    startActivity(Intent.createChooser(shareIntent, "Sharing Music File via"));
                }
                return true;
            });
        });


        albumPlayBtn.setOnClickListener(view -> playAllSongs());


    }

    private void playShuffledSong() {
        isShuffle = true;
        if (!isLoop) {
            mainPosition = getRandom(albumSongs.size() - 1);
        }
        if (!(albumSongs.isEmpty())){
            Intent intent = new Intent(AlbumDetails.this, MusicPlayerActivity.class);
            intent.putExtra("albumSender","albumDetails");
            intent.putExtra("position", mainPosition);
            this.startActivity(intent);

        }else {
            Toast.makeText(AlbumDetails.this, "You hava not any song", Toast.LENGTH_SHORT).show();
        }

    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void playAllSongs() {
        if (!(albumSongs.size() < 1)){
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
                intent.putExtra("albumSender","albumDetails");
                intent.putExtra("position", mainPosition);
                this.startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();

        if (!(albumSongs.isEmpty())){
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }


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

    public void backToHome(View view) {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
package com.infinite.virtualmusicplayer.activities;

import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.lightVibrantColor;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.mPalette;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.musicService;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.thumb;
import static com.infinite.virtualmusicplayer.fragments.AlbumFragment.albumAdapter;
import static com.infinite.virtualmusicplayer.fragments.ArtistFragment.artistAdapter;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.albumArt;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.artistName;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.mini_player;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.mini_player_progressBar;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.playPauseBtn;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.songName;
import static com.infinite.virtualmusicplayer.fragments.SongsFragment.musicAdapter;
import static com.infinite.virtualmusicplayer.fragments.SongsFragment.progressBar;
import static com.infinite.virtualmusicplayer.services.MusicService.MUSIC_FILE;
import static com.infinite.virtualmusicplayer.services.MusicService.MUSIC_LAST_PLAYED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.fragments.AlbumFragment;
import com.infinite.virtualmusicplayer.fragments.ArtistFragment;
import com.infinite.virtualmusicplayer.fragments.HomeFragment;
import com.infinite.virtualmusicplayer.fragments.NowPlayingFragment;
import com.infinite.virtualmusicplayer.fragments.PlaylistFragment;
import com.infinite.virtualmusicplayer.fragments.SongsFragment;
import com.infinite.virtualmusicplayer.model.Music;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final int REQUEST_CODE = 1;
    int id;

    private MaterialToolbar toolbar;
    private SearchView searchView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

//    NavigationViewHeader and text and image
    static RelativeLayout headerBackground;

    static TextView headerSongTitle;
    static TextView headerSongArtist;
    static ImageView headerImage;

//    allSongs
    public static ArrayList<Music> musicFiles = new ArrayList<>();
//    tempSongs
    private ArrayList<Music> tempSongList = new ArrayList<>();
//    allValidSongs
    private ArrayList<Music> validSongs = new ArrayList<>();
//    allArtists
    public static ArrayList<Music> artists = new ArrayList<>();
//    allAlbums
    public static ArrayList<Music> albums = new ArrayList<>();

    public static ArrayList<Music> playlists = new ArrayList<>();




    static SharedPreferences preferences;
    //    themes
    private SharedPreferences sharedPreferences;

    //    private SharedPreferences myFavPref;
    private final String[] themes = {"      System", "      Dark", "      Light"};

    public static boolean SHOW_MINI_PLAYER = false;
    static boolean isSystem = false;
    static boolean isNight = false;
    static boolean isLight = false;

    private static final String MY_SORT_PREF = "SortOrder";
    public static String PATH_TO_FRAG = null;
    public static String ARTIST_TO_FRAG = null;
    public static String SONG_NAME_TO_FRAG = null;

    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";





    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFullScreen();
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayout);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

//        Action bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        Navigation bar
        View headerView = navigationView.getHeaderView(0);

        headerBackground = headerView.findViewById(R.id.header_background);
        headerSongTitle = headerView.findViewById(R.id.header_songTitle);
        headerSongArtist = headerView.findViewById(R.id.header_songArtist);
        headerImage = headerView.findViewById(R.id.header_imageView);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                // Navigation drawer item click listener
                int id = item.getItemId();

                if (id == R.id.ac_home) {
                    replaceFragment(new HomeFragment());
                    bottomNavigationView.setSelectedItemId(R.id.ac_home);

                }
                else if (id == R.id.ac_tracks) {
                    replaceFragment(new SongsFragment());
                    bottomNavigationView.setSelectedItemId(R.id.ac_tracks);

                }
                else if (id == R.id.ac_albums) {
                    replaceFragment(new AlbumFragment());
                    bottomNavigationView.setSelectedItemId(R.id.ac_albums);

                }
                else if (id == R.id.ac_artists) {
                    replaceFragment(new ArtistFragment());
                    bottomNavigationView.setSelectedItemId(R.id.ac_artists);

                }
                else if (id == R.id.ac_playlist) {
                    replaceFragment(new PlaylistFragment());
                    bottomNavigationView.setSelectedItemId(R.id.ac_playlist);

                }
                else if (id == R.id.ac_favourite) {
                    Intent intent = new Intent(MainActivity.this, Favourite.class);
                    startActivity(intent);
                }

                else if (id == R.id.ac_equalizer) {
                    if (musicService != null) {
                        try {
                            Intent eqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                            eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService.mediaPlayer.getAudioSessionId());
                            eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getBaseContext().getPackageName());
                            eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                            startActivityForResult(eqIntent, 7);
                        }
                        catch (Exception e){
                            Toast.makeText(MainActivity.this, "Play any Song for use Equalizer", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Play any song for using Equalizer", Toast.LENGTH_SHORT).show();
                    }

                }

                else if (id == R.id.instagram) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/mr._stark7?igsh=YzljYTk1ODg3Zg=="));
                    startActivity(intent);
                }
                else if (id == R.id.linkedin) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/manish-chidar-393b1525b?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app"));
                    startActivity(intent);
                }
                else if (id == R.id.github) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/manish7924/Music-X"));
                    startActivity(intent);
                }
                else if (id == R.id.music_community) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com/channel/0029VaQCaJZCnA7zDT0OjE34"));
                    startActivity(intent);
                }
                else if (id == R.id.share) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share song with");
                    String string = "Try this Music player app Music X with amazing features " +
                            "\n\nhttps://github.com/manish7924/Music-X/releases";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, string);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
                else if (id == R.id.rate) {
                    Toast.makeText(MainActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                }


                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        try {
            applyTheme(getSelectedTheme());
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }



        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) item -> {

            id = item.getItemId();

            if (id == R.id.ac_home){
                replaceFragment(new HomeFragment());

            } else if (id == R.id.ac_tracks) {
                replaceFragment(new SongsFragment());

            } else if (id == R.id.ac_albums) {
                replaceFragment(new AlbumFragment());

            } else if (id == R.id.ac_artists) {
                replaceFragment(new ArtistFragment());

            } else if (id == R.id.ac_playlist) {
                replaceFragment(new PlaylistFragment());
            }

            return true;
        });


        bottomNavigationView.setOnItemReselectedListener((BottomNavigationView.OnNavigationItemReselectedListener) item -> id = item.getItemId());


//        Request Permission
        permission();

//        Showing Mini player
        showOrHideMiniPlayer();

    }




    private void permission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.BLUETOOTH_CONNECT
                    ).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {

                            setUpWithSongList();

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();

                        }
                    }).check();
        } else {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
                            Manifest.permission.READ_EXTERNAL_STORAGE

                    ).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {

                            setUpWithSongList();

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();

                        }
                    }).check();
        }

    }

    private void setUpWithSongList() {
        try {
            musicFiles = getAllAudio(MainActivity.this);
            progressBar.setVisibility(View.VISIBLE);

            new Thread(() -> {
                validSongs = Music.checkAndSetValidSongs(tempSongList);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.INVISIBLE);

                });
            }).start();

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void showOrHideMiniPlayer() {

        if (musicService != null){
            mini_player.setVisibility(View.VISIBLE);
        }
        else {
            mini_player.setVisibility(View.INVISIBLE);
        }

        try {
            showMiniPlayer();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }





    private void replaceFragment(Fragment fragment){
//        Inline method
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();

    }



    private ArrayList<Music> getAllAudio(Context context) {
        preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByName");

        ArrayList<String> duplicate = new ArrayList<>();
        ArrayList<String> artist_duplicate = new ArrayList<>();


        try {
            albums.clear();
            artists.clear();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

        String order = null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        if (sortOrder.equals("sortByName")){
            try {
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                setDefaultFragment();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        else if (sortOrder.equals("sortByDate")){
            try {
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                setDefaultFragment();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        else if (sortOrder.equals("sortBySize")){
            try {
                order = MediaStore.MediaColumns.SIZE + " DESC";
                setDefaultFragment();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }



        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.SIZE

        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, order);

        if (cursor != null){
            while (cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);
                String year = cursor.getString(6);
                String size = cursor.getString(7);

                Music music = new Music(path, title, artist, album, duration, id, year, size);
//                Log.e("path " + path, "Album " + album);

                try {
                    tempSongList.add(music);

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }

                if (!duplicate.contains(album)){
                    try {
                        albums.add(music);
                        duplicate.add(album);
//                        validSongs = Music.checkAndSetValidSongs(albums);

                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }
//                    playlists.add(music);
                }

                if (!artist_duplicate.contains(artist)){
                    try {
                        artists.add(music);
                        artist_duplicate.add(artist);
//                        validSongs = Music.checkAndSetValidSongs(artists);

                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
            cursor.close();
        }


        return tempSongList;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.search);

        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Music (Tracks, Albums, Artists)");
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<Music> myFiles = new ArrayList<>();
        for (Music song: musicFiles)
        {
            if (song.getTitle().toLowerCase().contains(userInput)  ||  song.getArtist().toLowerCase().contains(userInput)  ||  song.getAlbum().toLowerCase().contains(userInput))
            {
                try {
                    myFiles.add(song);
                    if (musicAdapter != null){
                        musicAdapter.updateList(myFiles);
                    }
                    if (albumAdapter != null){
                        albumAdapter.updateList(myFiles);
                    }
                    if (artistAdapter != null){
                        artistAdapter.updateList(myFiles);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();

        int id = item.getItemId();

        if (id == R.id.by_name) {
            if (musicService != null){
                if (musicService.isPlaying()){
                    musicService.pause();

                    try {
                        editor.putString("sorting", "sortByName");
                        editor.apply();
                        this.recreate();
                        setDefaultFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    try {
                        editor.putString("sorting", "sortByName");
                        editor.apply();
                        this.recreate();
                        setDefaultFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                try {
                    editor.putString("sorting", "sortByName");
                    editor.apply();
                    this.recreate();
                    setDefaultFragment();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (id == R.id.by_date) {
            if (musicService != null){
                if (musicService.isPlaying()){
                    musicService.pause();

                    try {
                        editor.putString("sorting", "sortByDate");
                        editor.apply();
                        this.recreate();
                        setDefaultFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        editor.putString("sorting", "sortByDate");
                        editor.apply();
                        this.recreate();
                        setDefaultFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                try {
                    editor.putString("sorting", "sortByDate");
                    editor.apply();
                    this.recreate();
                    setDefaultFragment();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }


        } else if (id == R.id.by_size) {
            if (musicService != null){
                if (musicService.isPlaying()){
                    musicService.pause();
                    try {
                        editor.putString("sorting", "sortBySize");
                        editor.apply();
                        this.recreate();
                        setDefaultFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        editor.putString("sorting", "sortBySize");
                        editor.apply();
                        this.recreate();
                        setDefaultFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
            else {
                try {
                    editor.putString("sorting", "sortBySize");
                    editor.apply();
                    this.recreate();
                    setDefaultFragment();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        }
        else if (id == R.id.ac_theme) {
            showThemeSelectionDialog();
        }

//        else if(id == R.id.ac_refresh) {
//            // Implement your desired functionality here
//
//            return true;
//        }

        else if (id == R.id.ac_about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
//            startActivity(new Intent(this,About.class));
            return true;
        }
        else if (id == R.id.ac_whats_new) {
            showWhatsNewDialog();
            return true;

        }
//        else if (id == R.id.ac_share) {
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share song with");
//            String string = "Try this amazing Music player app Music X a simple music player with amazing features";
//            shareIntent.putExtra(Intent.EXTRA_TEXT, string);
//            startActivity(Intent.createChooser(shareIntent, "Share via"));
//            return true;
//
//        }
        else if (id == R.id.exit_btn) {
            showExitApplicationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showExitApplicationDialog() {
        try {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Exit")
                    .setIcon(R.drawable.exit)
                    .setMessage("Do you want to close this playing session ?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Exit the app
                        exitApplication();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Exit the app
                        dialog.dismiss();
                    })
                    .show();

            //     new AlertDialog.Builder(this)
//                    .setMessage("Do you want to exit the app?")
//                    .setPositiveButton("Yes", (dialog, which) -> {
//                        // Exit the app
//                        finish();
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showWhatsNewDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("What's New!")
                .setIcon(R.drawable.icon)
                .setMessage(R.string.whats_new_description)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private void applyTheme(String theme) {
        switch (theme) {
            case "      System":
                try {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    setDefaultFragment();
                    isSystem = true;
                    isNight = false;
                    isLight = false;
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case "      Dark":
                try {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    setDefaultFragment();
                    isNight = true;
                    isLight = false;
                    isSystem = false;
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case "      Light":
                try {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    setDefaultFragment();
                    isLight = true;
                    isNight = false;
                    isSystem = false;
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setDefaultFragment() {
//        replaceFragment(new SongsFragment());
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SongsFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.ac_tracks);
//        frameLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }


    private void showThemeSelectionDialog() {
        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, themes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Theme")
                .setIcon(R.drawable.night_mode1)
                .setMessage("Choose your default theme for app")
                .setView(spinner)
                .setPositiveButton("OK", (dialog, which) -> {
                    String selectedTheme = themes[spinner.getSelectedItemPosition()];
                    try {
                        // Save the selected theme
                        saveSelectedTheme(selectedTheme);

                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString() , Toast.LENGTH_SHORT).show();
                    }
                    try {
                        // Apply the selected theme
                        applyTheme(selectedTheme);
                        Toast.makeText(this, selectedTheme + " Applied Successfully. ", Toast.LENGTH_SHORT).show();
                    } catch (Exception e1){
                        e1.printStackTrace();
                        Toast.makeText(this, e1.toString() , Toast.LENGTH_SHORT).show();
                    }
                    setDefaultFragment();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Exit the app
                    dialog.dismiss();
                })
                .show();

    }



    private String getSelectedTheme() {
        // Retrieve the selected theme from SharedPreferences
        return sharedPreferences.getString("theme", "System Theme");
    }

    private void saveSelectedTheme(String theme) {
        // Save the selected theme to SharedPreferences
        sharedPreferences.edit().putString("theme", theme).apply();
    }


    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        try {
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return art;
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        showOrHideMiniPlayer();

        new Thread(() -> {
            validSongs = Music.checkAndSetValidSongs(tempSongList);

            runOnUiThread(() -> {

            });
        }).start();

    }

    @SuppressLint("ResourceAsColor")
    private void showMiniPlayer() {
        SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
        String path = preferences.getString(MUSIC_FILE, null);
        String artist = preferences.getString(ARTIST_NAME, null);
        String song_name = preferences.getString(SONG_NAME, null);

        if (musicService != null){
            if (musicService.isPlaying()){
                playPauseBtn.setImageResource(R.drawable.pause_bottom);
            }
            else {
                playPauseBtn.setImageResource(R.drawable.play_bottom);
            }
        }

        if (path != null){
            SHOW_MINI_PLAYER = true;
            PATH_TO_FRAG = path;
            ARTIST_TO_FRAG = artist;
            SONG_NAME_TO_FRAG = song_name;
        }
        else {
            SHOW_MINI_PLAYER = false;
            PATH_TO_FRAG = null;
            ARTIST_TO_FRAG = null;
            SONG_NAME_TO_FRAG = null;
        }

        if (SHOW_MINI_PLAYER){
            byte[] art = getAlbumArt(PATH_TO_FRAG);
            if (art != null){
                Glide.with(this).load(art).placeholder(R.drawable.music_note).into(albumArt);
            }
            else {
                Glide.with(this).load(R.drawable.music_note).into(albumArt);
            }
            songName.setText(SONG_NAME_TO_FRAG);
            artistName.setText(ARTIST_TO_FRAG);

            if (musicService != null){
                mini_player_progressBar.setProgress(musicService.getCurrentPosition(), false);

                headerSongTitle.setTextColor(Color.WHITE);
                headerSongTitle.setText(SONG_NAME_TO_FRAG);
                headerSongArtist.setText(ARTIST_TO_FRAG);


                if (thumb != null){
                    headerImage.setImageBitmap(thumb);
                    Palette.from(thumb).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            mPalette = palette;
                            createGradientBackground();
                        }

                        private void createGradientBackground() {
                            if (mPalette != null) {

                                // Extract dominant color
                                int dominantColor = mPalette.getDominantColor(Color.WHITE);
                                lightVibrantColor = manipulateColor(mPalette.getLightVibrantColor(Color.WHITE), 1f);

                                int lightColor1 = manipulateColor(dominantColor, 0.8f);
                                int lightColor2 = manipulateColor(dominantColor, 0.9f);
                                int lightColor3 = manipulateColor(dominantColor, 0.95f);
                                int lightColor4 = manipulateColor(dominantColor, 1f);

//                        status bar color
//                        getWindow().setStatusBarColor(lightColor4);


                                GradientDrawable gradientDrawableMiniPlayer = new GradientDrawable(
                                        GradientDrawable.Orientation.RIGHT_LEFT,
                                        new int[]{lightColor1, lightColor2, lightColor3, lightColor4});

                                gradientDrawableMiniPlayer.setCornerRadius(17);

                                NowPlayingFragment.mini_player.setBackground(gradientDrawableMiniPlayer);

                                GradientDrawable gradientDrawableHeader = new GradientDrawable(
                                        GradientDrawable.Orientation.RIGHT_LEFT,
                                        new int[]{lightColor1, lightColor4});

                                headerBackground.setBackground(gradientDrawableHeader);

                                // Manipulate colors for gradient (darker at bottom, lighter at top)

                                mini_player_progressBar.setIndicatorColor(lightVibrantColor);

                                if (thumb != null){
                                    songName.setTextColor(lightVibrantColor);
                                    artistName.setTextColor(manipulateColor(lightVibrantColor, 1f));
                                    playPauseBtn.setColorFilter(lightVibrantColor);
                                    NowPlayingFragment.nextBtn.setColorFilter(lightVibrantColor);
                                    NowPlayingFragment.prevBtn.setColorFilter(lightVibrantColor);
                                    mini_player_progressBar.setIndicatorColor(lightVibrantColor);

                                }
                                else {
                                    songName.setTextColor(Color.WHITE);
                                    artistName.setTextColor(Color.WHITE);
                                    playPauseBtn.setColorFilter(Color.WHITE);
                                    NowPlayingFragment.nextBtn.setColorFilter(Color.WHITE);
                                    NowPlayingFragment.prevBtn.setColorFilter(Color.WHITE);
                                    mini_player_progressBar.setIndicatorColor(Color.WHITE);

                                }


                            }
                        }

                        private int manipulateColor(int color, float factor) {
                            int alpha = Color.alpha(color);
                            int red = Color.red(color);
                            int green = Color.green(color);
                            int blue = Color.blue(color);

                            red = Math.round(red * factor);
                            green = Math.round(green * factor);
                            blue = Math.round(blue * factor);

                            return Color.argb(alpha, red, green, blue);
                        }

                    });
                }
            }
            else {
                headerSongTitle.setText("Title");
                headerSongArtist.setText("Artist");

                if (isNight) {
                    headerSongTitle.setTextColor(Color.WHITE);
                }
                else if (isLight) {
                    headerSongTitle.setTextColor(Color.BLACK);
                }
                else {
                    headerSongTitle.setTextColor(R.color.ar_gray);
                }
            }


        }
    }




    @Override
    protected void onDestroy() {
        if (musicService != null){
            if (musicService.isPlaying()){
                try {
                    super.onDestroy();
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            else {
                try {
                    musicService.stop();
                    musicService.release();
                    musicService.stopForeground(true);
                    musicService = null;
                    if (getBaseContext() != null){
                        super.onDestroy();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }


        }
        else {
            try {
                super.onDestroy();
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void exitApplication() {
        if (musicService != null){
            if (musicService.isPlaying()){
                try {
//                    audioManager.abandonAudioFocus(musicService);
                    musicService.stop();
                    musicService.release();
                    musicService.stopForeground(true);
                    musicService = null;
                    if (getBaseContext() != null){
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
//                    audioManager.abandonAudioFocus(musicService);
                    musicService.stop();
                    musicService.release();
                    musicService.stopForeground(true);
                    musicService = null;
                    if (getBaseContext() != null){
                        finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }


        }
        else {
            try {
                if (getBaseContext() != null){
                    finish();
                }
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onBackPressed() {
        // Handle back press event
        if (!searchView.isIconified()) {
            // If the SearchView is open, close it and restore the original song list
            try {
                searchView.setIconified(true);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }



}

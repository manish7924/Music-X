package com.infinite.virtualmusicplayer.activities;

import static com.infinite.virtualmusicplayer.fragments.AlbumFragment.albumAdapter;
import static com.infinite.virtualmusicplayer.fragments.ArtistFragment.artistAdapter;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.musicService;
import static com.infinite.virtualmusicplayer.services.MusicService.MUSIC_FILE;
import static com.infinite.virtualmusicplayer.services.MusicService.MUSIC_LAST_PLAYED;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.albumArt;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.artistName;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.mini_player;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.playPauseBtn;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.songName;
import static com.infinite.virtualmusicplayer.fragments.SongsFragment.musicAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.infinite.virtualmusicplayer.fragments.AlbumFragment;
import com.infinite.virtualmusicplayer.fragments.ArtistFragment;
import com.infinite.virtualmusicplayer.fragments.HomeFragment;
import com.infinite.virtualmusicplayer.model.Music;
import com.infinite.virtualmusicplayer.fragments.PlaylistFragment;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.fragments.SongsFragment;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

//    For Binding Views

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    private static final int REQUEST_CODE = 1;

//    MusicService musicService;
    public static ArrayList<Music> musicFiles;
    public static ArrayList<Music> albums = new ArrayList<>();
    SearchView searchView;
    int id;
    public static ArrayList<Music> artists = new ArrayList<>();
    public static ArrayList<Music> playlists = new ArrayList<>();
//    Boolean state;

//    private ViewPager viewPager;
//    Music music;


    private final String MY_SORT_PREF = "SortOrder";
    public static boolean SHOW_MINI_PLAYER = false;
    public static String PATH_TO_FRAG = null;
    public static String ARTIST_TO_FRAG = null;
    public static String SONG_NAME_TO_FRAG = null;

    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";


//    themes
    private SharedPreferences sharedPreferences;
//    private SharedPreferences myFavPref;
    private final String[] themes = {"System Theme", "Dark Theme", "Light Theme"};

    static boolean isSystem = false;
    static boolean isNight = false;
    static boolean isLight = false;

    //        fragment
//        View bottomSheet = findViewById(R.id.design_bottom_sheet);
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//        state = true;
//        viewPager = findViewById(R.id.viewpager);
//        bottomSheetBehavior = BottomSheetBehavior.from(frag_mini_player);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFullScreen();
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);

//        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbarLayout);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayout);

        replaceFragment(new SongsFragment());
        bottomNavigationView.setSelectedItemId(R.id.ac_tracks);

//        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SongsFragment()).commit();


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

//            assert selectedFragment != null;
//            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, selectedFragment).commit();

            return true;
        });



        try {
            applyTheme(getSelectedTheme());
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


//        Request Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                permission();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }else {
            try {
                legacyPermission();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        showOrHideMiniPlayer();


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
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void legacyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            try {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        , REQUEST_CODE);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            try {
                musicFiles = getAllAudio(this);
                replaceFragment(new SongsFragment());
//                frameLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
//                replaceFragment(new SongsFragment());
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

        }
    }



    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            try {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}
                        , REQUEST_CODE);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
        else {
            try {
                musicFiles = getAllAudio(this);
                replaceFragment(new SongsFragment());
//                frameLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
//            Toast.makeText(this, musicFiles.size() + " Songs Found", Toast.LENGTH_SHORT).show();

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    musicFiles = getAllAudio(this);
//                    Important for first time launch
                    replaceFragment(new SongsFragment());
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}
                            , REQUEST_CODE);
                }
                else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            , REQUEST_CODE);
                }
            }
        }
    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }


//    private void setFullScreen() {
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    }


//    private void initViewPager() {
//        ViewPager viewPager = findViewById(R.id.viewpager);
//        TabLayout tabLayout = findViewById(R.id.tab_layout);
//
//        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//
//        viewPagerAdapter.addFragments(new HomeFragment(), "Home");
//        viewPagerAdapter.addFragments(new SongsFragment(), "Tracks");
//        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
//        viewPagerAdapter.addFragments(new ArtistFragment(), "Artists");
////        viewPagerAdapter.addFragments(new PlaylistFragment(), "Playlists");
////        viewPagerAdapter.addFragments(new GenresFragment(), "Genres");
//
//        viewPager.setAdapter(viewPagerAdapter);
////        for opening tracks directly
//        viewPager.setCurrentItem((int) viewPagerAdapter.getItemId(1), true);
//
//        tabLayout.setupWithViewPager(viewPager);
//
//
//    }
//
//
//
//    public static class ViewPagerAdapter extends FragmentPagerAdapter {
//
//        private final ArrayList<Fragment> fragments;
//        private final ArrayList<String> titles;
//
//        public ViewPagerAdapter(@NonNull FragmentManager fm) {
//            super(fm);
//            this.fragments = new ArrayList<>();
//            this.titles = new ArrayList<>();
//        }
//
//
//        void addFragments(Fragment fragment, String title)
//        {
//            fragments.add(fragment);
//            titles.add(title);
//        }
//
//        @NonNull
//        @Override
//        public Fragment getItem(int position) {
//            return fragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//
//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titles.get(position);
//        }
//    }

    private ArrayList<Music> getAllAudio(Context context) {
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByName");


        ArrayList<String> duplicate = new ArrayList<>();
        ArrayList<String> artist_duplicate = new ArrayList<>();
        ArrayList<Music> tempAudioList = new ArrayList<>();

        try {
            albums.clear();
            artists.clear();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        String order = null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch (sortOrder){
            case "sortByName":
                try {
                    order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                    setDefaultFragment();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;

            case "sortByDate":
                try {
                    order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                    setDefaultFragment();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;

            case "sortBySize":
                try {
                    order = MediaStore.MediaColumns.SIZE + " DESC";
                    setDefaultFragment();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
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
                    tempAudioList.add(music);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }

                if (!duplicate.contains(album)){
                    try {
                        albums.add(music);
                        duplicate.add(album);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
//                    playlists.add(music);
                }

                if (!artist_duplicate.contains(artist)){
                    try {
                        artists.add(music);
                        artist_duplicate.add(artist);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
            cursor.close();
        }
        return tempAudioList;

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
        else if(id == R.id.ac_refresh) {
            // Implement your desired functionality here
            try {
                CountDownTimer count = new CountDownTimer(0, 100) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
//                        replaceFragment(new SongsFragment());
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new SongsFragment()).commit();
                        bottomNavigationView.setSelectedItemId(R.id.ac_tracks);
                        Toast.makeText(MainActivity.this, musicFiles.size()+" Songs Found", Toast.LENGTH_SHORT).show();
//                        frameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_refresh));

                    }

                };
                count.start();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        else if (id == R.id.ac_about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
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
                .setTitle("Whats New!")
                .setIcon(R.drawable.music_icon)
                .setMessage(R.string.whats_new_description)
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private void applyTheme(String theme) {
        switch (theme) {
            case "System Theme":
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
            case "Dark Theme":
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
            case "Light Theme":
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
                .setPositiveButton("Ok", (dialog, which) -> {
                    String selectedTheme = themes[spinner.getSelectedItemPosition()];
                    try {
                        saveSelectedTheme(selectedTheme); // Save the selected theme

                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString() , Toast.LENGTH_SHORT).show();
                    }
                    try {
                        applyTheme(selectedTheme); // Apply the selected theme
                        Toast.makeText(this, selectedTheme + " Applied Successfully. ", Toast.LENGTH_SHORT).show();
                    } catch (Exception e1){
                        e1.printStackTrace();
                        Toast.makeText(this, e1.toString() , Toast.LENGTH_SHORT).show();
                    }
//                    replaceFragment(new SongsFragment());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return art;
    }

    @Override
    protected void onResume() {
        super.onResume();

        showOrHideMiniPlayer();

    }

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
                Glide.with(this).load(art).into(albumArt);
            }
            else {
                Glide.with(this).load(R.drawable.music_note).into(albumArt);
            }
            songName.setText(SONG_NAME_TO_FRAG);
            artistName.setText(ARTIST_TO_FRAG);
        }
    }


    @Override
    protected void onDestroy() {
        if (musicService != null){
            if (musicService.isPlaying()){
                try {
                    super.onDestroy();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    super.onDestroy();
                    if (musicService != null){
//                        audioManager.abandonAudioFocus(musicService);
                        musicService.stop();
                        musicService.release();
                        musicService.stopForeground(true);
                        musicService = null;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }


        }
        else {
            try {
                super.onDestroy();
            }catch (Exception e){
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
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
            showExitApplicationDialog();
        }

    }


}

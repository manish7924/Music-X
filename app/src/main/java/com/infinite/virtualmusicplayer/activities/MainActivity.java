package com.infinite.virtualmusicplayer.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.lightVibrantColor;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.mPalette;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.musicService;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.position;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.thumb;
import static com.infinite.virtualmusicplayer.fragments.SongsFragment.musicAdapter;
import static com.infinite.virtualmusicplayer.fragments.AlbumFragment.albumAdapter;
import static com.infinite.virtualmusicplayer.fragments.ArtistFragment.artistAdapter;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.miniPlayerCoverArt;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.artistName;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.mini_player;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.mini_player_progressBar;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.playPauseBtn;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.songName;
import static com.infinite.virtualmusicplayer.services.MusicService.MUSIC_FILE;
import static com.infinite.virtualmusicplayer.services.MusicService.MUSIC_LAST_PLAYED;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.palette.graphics.Palette;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.fragments.AlbumFragment;
import com.infinite.virtualmusicplayer.fragments.ArtistFragment;
import com.infinite.virtualmusicplayer.fragments.HomeFragment;
import com.infinite.virtualmusicplayer.fragments.MusicPlayerBottomSheetFragment;
import com.infinite.virtualmusicplayer.fragments.NowPlayingFragment;
import com.infinite.virtualmusicplayer.fragments.PlaylistFragment;
import com.infinite.virtualmusicplayer.fragments.SongsFragment;
import com.infinite.virtualmusicplayer.model.Music;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final int REQUEST_CODE_PERMISSIONS = 1;
    private boolean permissionGranted = false;

    private SearchView searchView;
    private DrawerLayout drawerLayout;

    protected ViewPager2 viewPager;
    protected BottomNavigationView bottomNavigationView;
//    private FrameLayout frameLayout;

//    NavigationViewHeader and text and image
    @SuppressLint("StaticFieldLeak")
    static RelativeLayout headerBackground;

    @SuppressLint("StaticFieldLeak")
    static TextView headerSongTitle;
    @SuppressLint("StaticFieldLeak")
    static TextView headerSongArtist;
    @SuppressLint("StaticFieldLeak")
    static ImageView headerImage;

//    allSongs
    public static ArrayList<Music> musicFiles = new ArrayList<>();
//    tempSongs
    public static ArrayList<Music> tracks = new ArrayList<>();
    //    allValidSongs
    public static ArrayList<Music> validSongs = new ArrayList<>();

//    allAlbums
    public static ArrayList<Music> albums = new ArrayList<>();
    //    all valid Albums
    public static ArrayList<Music> validAlbums = new ArrayList<>();

//    allArtists
    public static ArrayList<Music> artists = new ArrayList<>();
//    all valid Artists
    public static ArrayList<Music> validArtists = new ArrayList<>();

    public static ArrayList<Music> playlists = new ArrayList<>();

    SongsFragment songsFragment;
    AlbumFragment albumFragment;
    ArtistFragment artistFragment;

    private Boolean isGranted = false;


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



    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFullScreen();
        setContentView(R.layout.activity_main);
//        setTheme(com.google.android.material.R.style.Theme_Material3_DynamicColors_DayNight_NoActionBar);

//        getWindow().setStatusBarColor(Color.TRANSPARENT);

        sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        frameLayout = findViewById(R.id.frameLayout);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView.setSelectedItemId(R.id.ac_home);
        NavigationView navigationView = findViewById(R.id.navigation_view);

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

//        getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new HomeFragment()).commit();

        viewPager.setCurrentItem(1);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new SongsFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.ac_tracks);
        updateIcons(bottomNavigationView.getId());



        navigationView.setNavigationItemSelectedListener(item -> {

            // Navigation drawer item click listener
            int id = item.getItemId();

            if (id == R.id.ac_home) {
                viewPager.setCurrentItem(0);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new HomeFragment()).commit();
                bottomNavigationView.setSelectedItemId(R.id.ac_home);
                updateIcons(id);
            }
            else if (id == R.id.ac_tracks) {
                viewPager.setCurrentItem(1);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new SongsFragment()).commit();
                bottomNavigationView.setSelectedItemId(R.id.ac_tracks);
                updateIcons(id);
            }
            else if (id == R.id.ac_albums) {
                viewPager.setCurrentItem(2);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new AlbumFragment()).commit();
                bottomNavigationView.setSelectedItemId(R.id.ac_albums);
                updateIcons(id);
            }
            else if (id == R.id.ac_artists) {
                viewPager.setCurrentItem(3);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new ArtistFragment()).commit();
                bottomNavigationView.setSelectedItemId(R.id.ac_artists);
                updateIcons(id);
            }
            else if (id == R.id.ac_playlist) {
                viewPager.setCurrentItem(4);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new PlaylistFragment()).commit();
                bottomNavigationView.setSelectedItemId(R.id.ac_playlist);
                updateIcons(id);
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

            else if (id == R.id.whatsapp_community) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://whatsapp.com/channel/0029VaQCaJZCnA7zDT0OjE34"));
                startActivity(intent);
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/manish7924"));
                startActivity(intent);
            }
            else if (id == R.id.twitter) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://x.com/Manishx792"));
                startActivity(intent);
            }
            else if (id == R.id.source_code) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/manish7924/Music-X"));
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
                MusicPlayerBottomSheetFragment musicPlayerBottomSheet = MusicPlayerBottomSheetFragment.newInstance();
                musicPlayerBottomSheet.show(getSupportFragmentManager(), musicPlayerBottomSheet.getTag());
            }


            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });



        try {
            applyTheme(getSelectedTheme());

            Favourite.favouriteSongs = new ArrayList<>();
            SharedPreferences sharedPreferences = getSharedPreferences("FAVOURITES", MODE_PRIVATE);
            String jsonString = sharedPreferences.getString("FavouriteSongs", null);

            if (jsonString != null) {
                Type typeToken = new TypeToken<ArrayList<Music>>() {}.getType();
                ArrayList<Music> data = new GsonBuilder().create().fromJson(jsonString, typeToken);
                Favourite.favouriteSongs.addAll(data);
            }

        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


        viewPager.setAdapter(new ScreenSlidePagerAdapter(this));
        viewPager.setOffscreenPageLimit(1);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                updateIcons(bottomNavigationView.getMenu().getItem(position).getItemId());
            }
        });

//        viewPager.unregisterOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            }
//        });

        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) item -> {

            int id = item.getItemId();

            if (id == R.id.ac_home){
                viewPager.setCurrentItem(0);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new HomeFragment()).commit();
                updateIcons(id);

            } else if (id == R.id.ac_tracks) {
                viewPager.setCurrentItem(1);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new SongsFragment()).commit();
                updateIcons(id);

            } else if (id == R.id.ac_albums) {
                viewPager.setCurrentItem(2);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new AlbumFragment()).commit();
                updateIcons(id);

            } else if (id == R.id.ac_artists) {
                viewPager.setCurrentItem(3);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new ArtistFragment()).commit();
                updateIcons(id);

            } else if (id == R.id.ac_playlist) {
                viewPager.setCurrentItem(4);
//                getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new PlaylistFragment()).commit();
                updateIcons(id);
            }

            return true;
        });



        bottomNavigationView.setOnItemReselectedListener((BottomNavigationView.OnNavigationItemReselectedListener) item -> {
            int id = item.getItemId();
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            setupShortcuts();
        }

//        Request Permission
        RequestPermissions();

//        Showing Mini player
        showOrHideMiniPlayer();

    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateIcons(int selectedItemId) {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem item = bottomNavigationView.getMenu().getItem(i);
            if (item.getItemId() == selectedItemId) {
                item.setIcon(getResources().getDrawable(getFilledIcon(item.getItemId()), getTheme()));
            } else {
                item.setIcon(getResources().getDrawable(getOutlinedIcon(item.getItemId()), getTheme()));
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    private int getFilledIcon(int itemId) {
        int id = itemId;

        if (id == R.id.ac_home){
            return R.drawable.ic_home_filled;

        } else if (id == R.id.ac_tracks) {
            return R.drawable.ic_tracks_filled;

        } else if (id == R.id.ac_albums) {
            return R.drawable.ic_album_filled;

        } else if (id == R.id.ac_artists) {
            return R.drawable.ic_artist_filled;

        } else if (id == R.id.ac_playlist) {
            return R.drawable.ic_playlist_filled;

        }
        return itemId;
    }

    private int getOutlinedIcon(int itemId) {
        int id = itemId;

        if (id == R.id.ac_home){
            return R.drawable.ic_home_outlined;

        } else if (id == R.id.ac_tracks) {
            return R.drawable.ic_tracks_outlined;

        } else if (id == R.id.ac_albums) {
            return R.drawable.ic_album_outlined;

        } else if (id == R.id.ac_artists) {
            return R.drawable.ic_artist_outlined;

        } else if (id == R.id.ac_playlist) {
            return R.drawable.ic_playlist_outlined;

        }
        return itemId;
    }


    private void RequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // Check for READ_MEDIA_AUDIO or READ_EXTERNAL_STORAGE based on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        // Check for POST_NOTIFICATIONS for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // Check for Bluetooth permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                            != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH);
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN);
            }
        }

        // Request all necessary permissions if not granted
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
        }
        else {
            permissionGranted = true;
            loadMusicFiles();
        }

        // Request Manage All Files permission
        showManageAllFilesPermissionDialog();
    }

    private void showManageAllFilesPermissionDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Manage All Files Access")
                    .setIcon(R.drawable.manage_file)
                    .setMessage("This permission is required to access and manage all files on your device. Do you want to proceed?"+"\n\n"+"Note: Without this Rename & Delete features doesn't work")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // User clicked OK, request Manage All Files permission
                        requestManageAllFilesPermission();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // User clicked Cancel, do nothing
                        Toast.makeText(MainActivity.this, "Permission denied to manage all files.", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        } else {
            // If Manage All Files permission is already granted, load music files
            permissionGranted = true;
            loadMusicFiles();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestManageAllFilesPermission() {
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private void loadMusicFiles() {
        musicFiles = getAllAudio(this);
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



    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void setupShortcuts() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        // Shortcut for "Songs"
        ShortcutInfo songsShortcut = new ShortcutInfo.Builder(this, "shortcut_songs")
                .setShortLabel("Songs")
                .setLongLabel("Open Songs")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_tracks_sc))  // Set icon for shortcut
                .setIntent(new Intent(this, MainActivity.class)  // Set your Songs activity here
                        .setAction(Intent.ACTION_VIEW)
                        .putExtra("navigateTo", "songs"))
                .build();

        // Shortcut for "Albums"
        ShortcutInfo albumsShortcut = new ShortcutInfo.Builder(this, "shortcut_albums")
                .setShortLabel("Albums")
                .setLongLabel("Open Albums")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_album_sc))  // Set icon for shortcut
                .setIntent(new Intent(this, MainActivity.class)  // Set your Albums activity here
                        .setAction(Intent.ACTION_VIEW)
                        .putExtra("navigateTo", "albums"))
                .build();

        // Shortcut for "Artists"
        ShortcutInfo artistsShortcut = new ShortcutInfo.Builder(this, "shortcut_artists")
                .setShortLabel("Artists")
                .setLongLabel("Open Artists")
                .setIcon(Icon.createWithResource(this, R.drawable.ic_artist_sc))  // Set icon for shortcut
                .setIntent(new Intent(this, MainActivity.class)  // Set your Artists activity here
                        .setAction(Intent.ACTION_VIEW)
                        .putExtra("navigateTo", "artists"))
                .build();

        // Add all shortcuts to ShortcutManager
        if (shortcutManager != null) {
            shortcutManager.setDynamicShortcuts(Arrays.asList(songsShortcut, albumsShortcut, artistsShortcut));
        }
    }


    private void openSongsView() {
        viewPager.setCurrentItem(1);
//        getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new SongsFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.ac_tracks);
        updateIcons(bottomNavigationView.getId());
    }

    private void openAlbumsView() {
        viewPager.setCurrentItem(2);
//        getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new AlbumFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.ac_albums);
        updateIcons(bottomNavigationView.getId());
    }

    private void openArtistsView() {
        viewPager.setCurrentItem(3);
//        getSupportFragmentManager().beginTransaction().replace(R.id.viewPager, new ArtistFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.ac_artists);
        updateIcons(bottomNavigationView.getId());
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            permissionGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = false;
                    break;
                }
            }
            if (permissionGranted) {
                loadMusicFiles();
            } else {
                Toast.makeText(this, "Permissions are required to access media files.", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private ArrayList<Music> getAllAudio(Context context) {
        preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByName");

        tracks.clear();
        albums.clear();
        artists.clear();


        String order = null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch (sortOrder) {
            case "sortByName":
                try {
                    order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
//                setDefaultFragment();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case "sortByDate":
                try {
                    order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
//                setDefaultFragment();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case "sortBySize":
                try {
                    order = MediaStore.MediaColumns.SIZE + " DESC";
//                setDefaultFragment();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }


        ArrayList<String> track_duplicate = new ArrayList<>();
        ArrayList<String> album_duplicate = new ArrayList<>();
        ArrayList<String> artist_duplicate = new ArrayList<>();


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
//                    adding songs on trackslist
                    if (!track_duplicate.contains(title)){
                        tracks.add(music);
                        track_duplicate.add(title);
                    }

                    if (!album_duplicate.contains(album)){
                        albums.add(music);
                        album_duplicate.add(album);
                    }

                    if (!artist_duplicate.contains(artist)){
                        artists.add(music);
                        artist_duplicate.add(artist);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
            cursor.close();
        }

        return tracks;

    }



//    private ArrayList<Music> getAllAudio(Context context) {
//        preferences = context.getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
//        String sortOrder = preferences.getString("sorting", "sortByName");
//
//        tracks.clear();
//        albums.clear();
//        artists.clear();
//
//        String order = null;
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//
//        // Determine sort order
//        switch (sortOrder) {
//            case "sortByName":
//                order = MediaStore.Audio.Media.TITLE + " ASC";
//                break;
//            case "sortByDate":
//                order = MediaStore.Audio.Media.DATE_ADDED + " DESC";
//                break;
//            case "sortBySize":
//                order = MediaStore.Audio.Media.SIZE + " DESC";
//                break;
//            default:
//                order = MediaStore.Audio.Media.TITLE + " ASC";
//        }
//
//        // Avoiding duplication
//        ArrayList<String> trackDuplicate = new ArrayList<>();
//        ArrayList<String> albumDuplicate = new ArrayList<>();
//        ArrayList<String> artistDuplicate = new ArrayList<>();
//
//        String[] projection = {
//                MediaStore.Audio.Media.ALBUM,
//                MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.DURATION,
//                MediaStore.Audio.Media.DATA,
//                MediaStore.Audio.Media.ARTIST,
//                MediaStore.Audio.Media._ID,
//                MediaStore.Audio.Media.YEAR,
//                MediaStore.Audio.Media.SIZE
//        };
//
//        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, order)) {
//            if (cursor != null && cursor.moveToFirst()) {
//                do {
//                    String album = cursor.getString(0);
//                    String title = cursor.getString(1);
//                    String duration = cursor.getString(2);
//                    String path = cursor.getString(3);
//                    String artist = cursor.getString(4);
//                    String id = cursor.getString(5);
//                    String year = cursor.getString(6);
//                    String size = cursor.getString(7);
//
//                    // Validate data
//                    if (path == null || title == null || duration == null) continue;
//
//                    Music music = new Music(path, title, artist, album, duration, id, year, size);
//
//                    // Add to tracks, albums, and artists without duplication
//                    if (!trackDuplicate.contains(title)) {
//                        tracks.add(music);
//                        trackDuplicate.add(title);
//                    }
//
//                    if (!albumDuplicate.contains(album)) {
//                        albums.add(music);
//                        albumDuplicate.add(album);
//                    }
//
//                    if (!artistDuplicate.contains(artist)) {
//                        artists.add(music);
//                        artistDuplicate.add(artist);
//                    }
//                } while (cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(context, "Error fetching audio files: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//        return musicFiles;
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.search);

        searchView = (SearchView) menuItem.getActionView();
        if (searchView == null) throw new AssertionError();
        searchView.setQueryHint("Search Music (Tracks, Albums, Artists)");
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String userInput = query.toLowerCase();
        ArrayList<Music> myFiles = new ArrayList<>();
        for (Music song: musicFiles) {
            if (bottomNavigationView.getSelectedItemId() == R.id.ac_tracks) {
                if (musicAdapter != null) {
                    if (song.getTitle().toLowerCase().contains(userInput)){
                        myFiles.add(song);
                        musicAdapter.updateList(myFiles);
                    }
                }
                else {
                    Toast.makeText(this, "Result Not found", Toast.LENGTH_SHORT).show();
                }
            }
            else if (bottomNavigationView.getSelectedItemId() == R.id.ac_albums) {
                if (albumAdapter != null) {
                    if (song.getAlbum().toLowerCase().contains(userInput)){
                        myFiles.add(song);
                        albumAdapter.updateList(myFiles);
                    }
                }
            }
            else if (bottomNavigationView.getSelectedItemId() == R.id.ac_artists) {
                if (artistAdapter != null) {
                    if (song.getArtist().toLowerCase().contains(userInput)){
                        myFiles.add(song);
                        artistAdapter.updateList(myFiles);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        String userInput = query.toLowerCase();
        ArrayList<Music> myFiles = new ArrayList<>();
        for (Music song: musicFiles) {
            if (bottomNavigationView.getSelectedItemId() == R.id.ac_tracks) {
                if (musicAdapter != null) {
                    if (song.getTitle().toLowerCase().contains(userInput)){
                        myFiles.add(song);
                        musicAdapter.updateList(myFiles);
                    }
                }
            }
            else if (bottomNavigationView.getSelectedItemId() == R.id.ac_albums) {
                if (albumAdapter != null) {
                    if (song.getAlbum().toLowerCase().contains(userInput)){
                        myFiles.add(song);
                        albumAdapter.updateList(myFiles);
                    }
                }
            }
            else if (bottomNavigationView.getSelectedItemId() == R.id.ac_artists) {
                if (artistAdapter != null) {
                    if (song.getArtist().toLowerCase().contains(userInput)){
                        myFiles.add(song);
                        artistAdapter.updateList(myFiles);
                    }
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
//                        setDefaultFragment();
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
//                        setDefaultFragment();
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
//                    setDefaultFragment();
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
//                        setDefaultFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        editor.putString("sorting", "sortByDate");
                        editor.apply();
                        this.recreate();
//                        setDefaultFragment();
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
//                    setDefaultFragment();
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
//                        setDefaultFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        editor.putString("sorting", "sortBySize");
                        editor.apply();
                        this.recreate();
//                        setDefaultFragment();
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
//                    setDefaultFragment();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        }
        else if (id == R.id.ac_theme) {
            showThemeSelectionDialog();
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
                    .setIcon(R.drawable.exit)
                    .setMessage("Are you want to sure to Exit app ?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Exit the app
                        exitApplication();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Exit the app
                        dialog.dismiss();
                    })
                    .show();

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
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void applyTheme(String theme) {
        switch (theme) {
            case "      System":
                try {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
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



    private void showThemeSelectionDialog() {
        // LinearLayout to wrap the RadioGroup
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int paddingHorizontal = (int) getResources().getDimension(R.dimen.dialog_horizontal_padding);
        int paddingVertical = (int) getResources().getDimension(R.dimen.dialog_vertical_padding);
        layout.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

        // RadioGroup
        RadioGroup radioGroup = new RadioGroup(this);

        // Dynamically created radio buttons based on the themes array
        for (String theme : themes) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(theme);
            radioGroup.addView(radioButton);
        }

        // Add the RadioGroup to the layout
        layout.addView(radioGroup);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Theme")
                .setIcon(R.drawable.night_mode1)
                .setMessage("Choose your default theme for the app")
                .setView(layout)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Find the selected radio button
                    int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                    if (selectedRadioButtonId != -1) {
                        RadioButton selectedRadioButton = radioGroup.findViewById(selectedRadioButtonId);
                        String selectedTheme = selectedRadioButton.getText().toString();
                        try {
                            // Save the selected theme
                            saveSelectedTheme(selectedTheme);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        try {
                            // Apply the selected theme
                            applyTheme(selectedTheme);
                            Toast.makeText(this, selectedTheme + " Applied Successfully.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            Toast.makeText(this, e1.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No theme selected!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
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
    protected void onStart() {
        super.onStart();

    }




    @Override
    protected void onResume() {
        super.onResume();


        Intent intent = getIntent();
        if (intent != null && Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_VIEW)) {
            String navigateTo = intent.getStringExtra("navigateTo");

            if ("songs".equals(navigateTo)) {
                // Open Songs view
                openSongsView();
            } else if ("albums".equals(navigateTo)) {
                // Open Albums view
                openAlbumsView();
            } else if ("artists".equals(navigateTo)) {
                // Open Artists view
                openArtistsView();
            }
            else {
                try {
                    if (musicService != null  &&  SHOW_MINI_PLAYER){
                        try {
                            Intent openPlayer = new Intent(getBaseContext(), MusicPlayerActivity.class);
                            openPlayer.putExtra("position", position);
                            openPlayer.putExtra("nowPlaying","NowPlaying");
                            this.startActivity(openPlayer);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getBaseContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getBaseContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();

                        }
                    }
                    else {
                        try {
                            Toast.makeText(getBaseContext(), "No Composition Found ", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getBaseContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Toast.makeText(getBaseContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getBaseContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "No Composition Found " + e, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(), "Please Play any song from Track list" , Toast.LENGTH_SHORT).show();

                }
            }
        }
        else {
            if (intent == null){
                intent = new Intent(getBaseContext(), MainActivity.class);
                this.startActivity(intent);
            }

        }


        // For storing favourites data using SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit();
        String jsonString = new GsonBuilder().create().toJson(Favourite.favouriteSongs);
        editor.putString("FavouriteSongs", jsonString);
//        String jsonStringPlaylist = new GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist);
//        editor.putString("MusicPlaylist", jsonStringPlaylist);
        editor.apply();


        showOrHideMiniPlayer();

        new Thread(() -> {
            validSongs = Music.checkAndSetValidSongs(tracks);
            validAlbums = Music.checkAndSetValidSongs(albums);
            validArtists = Music.checkAndSetValidSongs(artists);

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
                Glide.with(this)
                        .load(art)
                        .override(200,200)
                        .placeholder(R.drawable.music_note)
                        .into(miniPlayerCoverArt);
            }
            else {
                Glide.with(this)
                        .load(R.drawable.music_note)
                        .override(200,200)
                        .into(miniPlayerCoverArt);
            }
            songName.setText(SONG_NAME_TO_FRAG);
            artistName.setText(ARTIST_TO_FRAG);
            headerSongTitle.setText(SONG_NAME_TO_FRAG);
            headerSongArtist.setText(ARTIST_TO_FRAG);


            if (musicService != null){
                mini_player_progressBar.setProgress(musicService.getCurrentPosition(), false);

                if (thumb != null){
                    Glide.with(this)
                            .load(thumb)
                            .override(200,200)
                            .circleCrop()
                            .placeholder(R.drawable.music_note)
                            .into(headerImage);

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

                                mini_player.setBackground(gradientDrawableMiniPlayer);

                                GradientDrawable gradientDrawableHeader = new GradientDrawable(
                                        GradientDrawable.Orientation.RIGHT_LEFT,
                                        new int[]{lightColor1, lightColor4});

                                headerBackground.setBackground(gradientDrawableHeader);

                                // Manipulate colors for gradient (darker at bottom, lighter at top)

                                mini_player_progressBar.setIndicatorColor(lightVibrantColor);

                                if (thumb != null){
                                    songName.setTextColor(lightVibrantColor);
                                    artistName.setTextColor(manipulateColor(lightVibrantColor, 1f));
                                    headerSongTitle.setTextColor(lightVibrantColor);
                                    headerSongArtist.setTextColor(manipulateColor(lightVibrantColor, 1f));
                                    playPauseBtn.setColorFilter(lightVibrantColor);
                                    NowPlayingFragment.nextBtn.setColorFilter(lightVibrantColor);
                                    NowPlayingFragment.prevBtn.setColorFilter(lightVibrantColor);
                                    mini_player_progressBar.setIndicatorColor(lightVibrantColor);

                                }
                                else {
                                    songName.setTextColor(Color.WHITE);
                                    artistName.setTextColor(Color.WHITE);
                                    headerSongTitle.setTextColor(Color.WHITE);
                                    headerSongArtist.setTextColor(Color.WHITE);
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
                else {
                    Glide.with(this)
                            .load(R.drawable.music_note)
                            .placeholder(R.drawable.music_note)
                            .into(headerImage);
                }
            }
            else {
                headerSongTitle.setText("Title");
                headerSongArtist.setText("Artist");

                if (isNight) {
                    headerSongTitle.setTextColor(Color.WHITE);
                    headerSongArtist.setTextColor(Color.WHITE);
                }
                else if (isLight) {
                    headerSongTitle.setTextColor(Color.BLACK);
                    headerSongArtist.setTextColor(Color.BLACK);
                }
                else {
                    headerSongTitle.setTextColor(R.color.ar_gray);
                    headerSongArtist.setTextColor(R.color.ar_gray);
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

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(MainActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            if (position == 1) {
                return new SongsFragment();
            } else if (position == 2) {
                return new AlbumFragment();
            } else if (position == 3) {
                return new ArtistFragment();
            } else if (position == 4) {
                return new PlaylistFragment();
            }
            else {
                return new HomeFragment();
            }


        }

        @Override
        public int getItemCount() {
            return 5; // Number of fragments
        }
    }

}

package com.infinite.virtualmusicplayer.activities;

import static com.infinite.virtualmusicplayer.activities.MainActivity.headerBackground;
import static com.infinite.virtualmusicplayer.activities.MainActivity.headerImage;
import static com.infinite.virtualmusicplayer.activities.MainActivity.headerSongArtist;
import static com.infinite.virtualmusicplayer.activities.MainActivity.headerSongTitle;
import static com.infinite.virtualmusicplayer.activities.MainActivity.isLight;
import static com.infinite.virtualmusicplayer.activities.MainActivity.isNight;
import static com.infinite.virtualmusicplayer.adapters.AlbumDetailsAdapter.albumSongFiles;
import static com.infinite.virtualmusicplayer.adapters.ArtistDetailsAdapter.artistSongFiles;
import static com.infinite.virtualmusicplayer.adapters.FavouriteAdapter.favouriteFiles;
import static com.infinite.virtualmusicplayer.adapters.MusicAdapter.mFiles;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.albumArt;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.artistName;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.mini_player_progressBar;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.playPauseBtn;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.songName;
import static com.infinite.virtualmusicplayer.services.MusicService.currentSong;
import static com.infinite.virtualmusicplayer.services.MusicService.currentSongIndex;
import static com.infinite.virtualmusicplayer.services.MusicService.musicServiceFiles;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.fragments.NowPlayingFragment;
import com.infinite.virtualmusicplayer.model.Music;
import com.infinite.virtualmusicplayer.receivers.ActionPlaying;
import com.infinite.virtualmusicplayer.receivers.LyricsFinder;
import com.infinite.virtualmusicplayer.receivers.SwipeGestureListener;
import com.infinite.virtualmusicplayer.services.MusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class MusicPlayerActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {

    TextView songTitleTv, artistTv, lyricsTitle, lyricsText, currentTimeTv, totalTimeTv;
    SeekBar seekBar, seekVol;
    static ImageView play;

    ConnectivityManager connectivityManager;
    AudioManager audioManager;
    ImageView nextBtn, previousBtn, musicCover, loop, shuffle, shape, rotateAnim, equalizerBtn, songInfo, copyBtn, lyricsInfoBtn, lyricsBtn, lyrics_gradient, fontSizeBtn, searchLyricsBtn, volumeBtn;
    ImageView favBtn, backBtn, moreOptionBtn;
//    Thread playThread, prevThread, nextThread;
    CardView cardView;

    GradientDrawable gradientDrawable;
    ConstraintLayout mContainer, buttonPanel;
    ScrollView scrollView;
    LinearLayout linearBtnLayout;
    public static ArrayList<Music> listSongs = new ArrayList<>();
//    static ArrayList<Music> favouriteSongs = new ArrayList<>();
    static Uri uri;
    public static MusicService musicService;
    ProgressBar lyricsProg;

    public static Bitmap thumb;

    private final Handler handler = new Handler();


    Vibrator myVib;

    static Palette mPalette;

    public static int position = -1;
//    String url;
    public static int lightVibrantColor;
    int maxVol;
    int curVol;
    public static Boolean isFav = false;
    static int fIndex = -1;

    int currentDuration = 0;
    public static long finalSeekProgress = 0;
//    static String nowPlayingId = "";
    int totalDuration = 0;
    public static boolean isPlaying = false;
    private boolean isPrev = false;
    private boolean isNext = false;
    public static boolean isShuffle = false;
    public static boolean isLoop = false;

//    private boolean isPlay = false;
    private boolean isLyrics = false;
    private boolean isVol = false;
    static boolean isAnim = false;
    private static final int SEEK_DURATION = 10000;   // 10 sec

    //    private static PowerManager.WakeLock wakeLock;


    private GestureDetector gestureDetector;


    @SuppressLint({"ClickableViewAccessibility", "InvalidWakeLockTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        for full screen
//        setFullScreen();
        setContentView(R.layout.activity_music_player);
        getWindow().setStatusBarColor(Color.parseColor("#141B1B"));


        initViews();

        getIntentMethod();


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());


        // Volume Bar
        seekVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                seekVol.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress);
                    seekBar.setProgress(progress);

                    //  most imp not change this position it is only in from user
                    showNotificationWithPreference();
                }

                mini_player_progressBar.setProgress(progress, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null) {

                    currentDuration = musicService.getCurrentPosition();
                    totalDuration = musicService.getDuration();
                    seekBar.setProgress(currentDuration);
                    seekBar.setMax(totalDuration);
                    mini_player_progressBar.setMax(totalDuration);


                    curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    seekVol.setProgress(curVol);

                    currentTimeTv.setText(millisecondsToTime(currentDuration));
                    totalTimeTv.setText(millisecondsToTime(totalDuration));


                }
                handler.postDelayed(this, 100);
            }
        });


        play.setOnClickListener(view -> {
            playPauseBtnClicked();

            if (musicService != null && musicService.isPlaying()) {
                play.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_once_play));
            } else {
                play.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_once_pause));
            }

        });


        nextBtn.setOnClickListener(view -> {
            nextBtnClicked();
            nextBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_next_prev_btn));

        });

        previousBtn.setOnClickListener(view -> {
            prevBtnClicked();
            previousBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_next_prev_btn));

        });


        shuffle.setOnClickListener(view -> {
            if (isShuffle){
                shuffle.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.square_shape_anim));
            } else {
                shuffle.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_half_loop));
            }

            if (isShuffle) {
                isShuffle = false;
//                    Toast.makeText(MusicPlayerActivity.this, "Shuffle off", Toast.LENGTH_SHORT).show();
                shuffle.setColorFilter(R.color.btn_off);
                shuffle.setImageResource(R.drawable.shuffle_off);
            } else {
                if (isLoop){
                    isShuffle = false;
                    Toast.makeText(MusicPlayerActivity.this, "Shuffle feature is not supported with loop once", Toast.LENGTH_SHORT).show();
                    shuffle.setColorFilter(R.color.btn_off);
                    shuffle.setImageResource(R.drawable.shuffle_off);
                }
                else {
                    isShuffle = true;
//                        Toast.makeText(MusicPlayerActivity.this, "Shuffle on", Toast.LENGTH_SHORT).show();
                    shuffle.setColorFilter(lightVibrantColor);
                    shuffle.setImageResource(R.drawable.shuffle_on);
                }
            }
        });

        loop.setOnClickListener(view -> {
            if (isLoop){
                loop.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.square_shape_anim));
            }
            else {
                loop.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_half_loop));
            }

            if (isLoop) {
                isLoop = false;
                loop.setColorFilter(R.color.btn_off);
                loop.setImageResource(R.drawable.repeat_off);
            } else {
                if (isShuffle){
                    isShuffle = false;
                    Toast.makeText(MusicPlayerActivity.this, "Shuffle feature is not supported with loop once", Toast.LENGTH_SHORT).show();
                    shuffle.setColorFilter(R.color.btn_off);
                    shuffle.setImageResource(R.drawable.shuffle_off);
                }

                isLoop = true;
                loop.setColorFilter(lightVibrantColor);
                loop.setImageResource(R.drawable.repeat_on);

            }
        });


        previousBtn.setOnLongClickListener(view -> {
            myVib.vibrate(32);
            previousBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));
            backwardTenSecond();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                previousBtn.setTooltipText("Backward 10 Second");
            }
            return true;
        });


        nextBtn.setOnLongClickListener(view -> {
            myVib.vibrate(32);
            nextBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));
            forwardTenSecond();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nextBtn.setTooltipText("Forward 10 Second");
            }
            return true;
        });


        backBtn.setOnClickListener(view -> {
            backBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));
            onBackPressed();
//                finish();
        });


        songInfo.setOnClickListener(view -> {
//            myVib.vibrate(30);
            songInfo.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));

            showSongDetails();

        });

        fontSizeBtn.setOnClickListener(view -> increaseTextSize());

        moreOptionBtn.setOnClickListener(v -> {
            myVib.vibrate(30);
            moreOptionBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));
//                moreOptionBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotete90));
            PopupMenu moreMenu2 = new PopupMenu(MusicPlayerActivity.this, moreOptionBtn);
            moreMenu2.getMenuInflater().inflate(R.menu.menu_more_player, moreMenu2.getMenu());

            moreMenu2.show();

            // PopUp Menu click listener
            //                    @SuppressLint({"NonConstantResourceId", "RestrictedApi"})
            moreMenu2.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.add_to_playlist) {
                    Toast.makeText(MusicPlayerActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (id == R.id.go_to_album) {
                    goToAlbum();
                    return true;
                }
                if (id == R.id.go_to_artist) {
                    goToArtist();
                    return true;
                }
                if (id == R.id.go_to_favourite) {
                    Intent intent = new Intent(MusicPlayerActivity.this, Favourite.class);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.details){
                    showSongDetails();
                    return true;
                }
                if (id == R.id.share){
                    shareIntent();
                    return true;
                }
//                        shape menu
                if (id == R.id.very_small) {
                    cardView.setRadius(12);
                    shape.setImageResource(R.drawable.square);
//                            rotateAnim.setVisibility(View.INVISIBLE);
                    isAnim = false;
                    animStarterForSquare();
                    rotateAnim.setImageResource(R.drawable.anim_off);
                    return true;
                } else if (id == R.id.small) {
                    cardView.setRadius(30);
                    shape.setImageResource(R.drawable.rounded_square);
//                            rotateAnim.setVisibility(View.INVISIBLE);
                    isAnim = false;
                    animStarterForSquare();
                    rotateAnim.setImageResource(R.drawable.anim_off);
                    return true;
                } else if (id == R.id.medium) {
                    cardView.setRadius(600);
//                            cardViewUn.setRadius(600);
                    isAnim = false;
                    //                            toggleAnim();
                    shape.setImageResource(R.drawable.circle);
                    rotateAnim.setVisibility(View.VISIBLE);
                    seekVol.setVisibility(View.INVISIBLE);
                    animStarterForSquare();
                    rotateAnim.setImageResource(R.drawable.anim_off);
                    return true;
                } else if (id == R.id.large) {
                    cardView.setRadius(600);
                    shape.setImageResource(R.drawable.anim_circle);
                    isAnim = true;
                    if (musicService.isPlaying() && isAnim) {
                        rotateAnim.setImageResource(R.drawable.anim_on);
                        toggleAnim();
                    } else {
                        rotateAnim.setImageResource(R.drawable.anim_off);
                        circleCard();
                    }
                    rotateAnim.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_once));
                    rotateAnim.setVisibility(View.VISIBLE);
                    seekVol.setVisibility(View.INVISIBLE);

                    return true;
                }
                if (id == R.id.delete_from_device) {
                    deleteMusicFile(position, MusicPlayerActivity.this);
                    return true;
                }

                if (id == R.id.equalizer) {

                    equalizerActivityLaunch();

                    return true;
                }
                return true;
            });

        });

        cardView.setOnClickListener(view -> {
            showLyrics();
            searchLyrics();
        });


        searchLyricsBtn.setOnClickListener(view -> {
            myVib.vibrate(30);
            searchLyrics();
        });


        lyricsBtn.setOnClickListener(v -> {
            myVib.vibrate(30);
            showLyrics();
            lyricsBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));
        });

        volumeBtn.setOnClickListener(view -> {
            myVib.vibrate(30);
            volumeBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));

            isVol = !isVol;
            if (isVol) {
                volumeBtn.setImageResource(R.drawable.volume_on);
                seekVol.setVisibility(View.VISIBLE);
            } else {
                volumeBtn.setImageResource(R.drawable.volume_off);
                seekVol.setVisibility(View.INVISIBLE);
            }
        });


        rotateAnim.setOnClickListener(view -> {
            myVib.vibrate(30);

            if (isAnim){
                rotateAnim.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));

            } else {
                rotateAnim.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_once));
            }
//            rotateAnim.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));

            isAnim = !isAnim;
            if (musicService.isPlaying()  && isAnim) {
                rotateAnim.setImageResource(R.drawable.anim_on);
                toggleAnim();
            } else {
                isAnim = false;
                rotateAnim.setImageResource(R.drawable.anim_off);
                circleCard();
            }

        });

        rotateAnim.setOnLongClickListener(view -> {
            myVib.vibrate(32);
            rotateAnim.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));

            isAnim = false;
            cardView.setRadius(30);
            animStarterForSquare();
            shape.setImageResource(R.drawable.shape);
            rotateAnim.setImageResource(R.drawable.anim_off);
            return true;
        });


        copyBtn.setOnClickListener(v -> {
            myVib.vibrate(30);
            copyToClipboard();
        });


        lyricsInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myVib.vibrate(30);
                showLyricsInfoDialog();
            }
        });


        favBtn.setOnClickListener(view -> {
            myVib.vibrate(30);
            favBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_smooth));

            if (isFav) {
                isFav = false;
                favBtn.setImageResource(R.drawable.favourite_off);
                Favourite.favouriteSongs.remove(listSongs.get(position));
//                    Toast.makeText(MusicPlayerActivity.this, "Removed to favourite", Toast.LENGTH_SHORT).show();

            } else {
                isFav = true;
                favBtn.setImageResource(R.drawable.favourite_on);
                Favourite.favouriteSongs.add(listSongs.get(position));
//                    Toast.makeText(MusicPlayerActivity.this, "Added to favourite", Toast.LENGTH_SHORT).show();

            }
        });


        equalizerBtn.setOnClickListener(view -> {
//            myVib.vibrate(30);
            equalizerBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));

            equalizerActivityLaunch();
        });


        shape.setOnClickListener(v -> {
//            myVib.vibrate(30);
            shape.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));

            showShapePopupMenu();

        });


        SwipeGestureListener swipeGestureListener = new SwipeGestureListener(this, new SwipeGestureListener.OnSwipeListener() {

            @Override
            public void onSwipeLeft() {
                nextBtnClicked();
//                nextBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));

                if (musicService!= null  && musicService.isPlaying()){
                    playPauseBtn.setImageResource(R.drawable.pause_bottom);
                }
                else {
                    playPauseBtn.setImageResource(R.drawable.play_bottom);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nextBtn.setTooltipText("Next");
                }

                if (musicService.isPlaying() && isAnim) {
                    rotateAnim.setImageResource(R.drawable.anim_on);
                    toggleAnim();
                } else {
                    rotateAnim.setImageResource(R.drawable.anim_off);
                    circleCard();
                }
//                cardView.setTooltipText("Next");
            }


            @Override
            public void onSwipeRight() {
                prevBtnClicked();
                if (musicService!= null  && musicService.isPlaying()){
                    playPauseBtn.setImageResource(R.drawable.pause_bottom);
                }
                else {
                    playPauseBtn.setImageResource(R.drawable.play_bottom);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    previousBtn.setTooltipText("Previous");
                }

                if (musicService.isPlaying() && isAnim) {
                    rotateAnim.setImageResource(R.drawable.anim_on);
                    toggleAnim();
                } else {
                    rotateAnim.setImageResource(R.drawable.anim_off);
                    circleCard();
                }
//                cardView.setTooltipText("Previous");
            }
        });

        gestureDetector = new GestureDetector(this, swipeGestureListener);

        cardView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        musicCover.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        mContainer.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        play.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        previousBtn.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        nextBtn.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        buttonPanel.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));



        NowPlayingFragment.playPauseBtn.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        NowPlayingFragment.prevBtn.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        NowPlayingFragment.nextBtn.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));


        NowPlayingFragment.mini_player.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));


    }



    private void showNotificationWithPreference() {
        if (musicService != null) {
            if (musicService.isPlaying()){
                isPlaying = true;
                musicService.showNotification(R.drawable.pause_noti, 1F);
            } else {
                isPlaying = false;
                musicService.showNotification(R.drawable.play_noti, 0F);
            }
        }
    }

    private void equalizerActivityLaunch() {
        try {
            Intent eqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService.mediaPlayer.getAudioSessionId());
            eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getBaseContext().getPackageName());
            eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
            startActivityForResult(eqIntent, 13);
        }
        catch (Exception e){
            Toast.makeText(MusicPlayerActivity.this, "Equalizer Not Found", Toast.LENGTH_SHORT).show();
        }
    }


    private void increaseTextSize() {

        PopupMenu fontMenu = new PopupMenu(MusicPlayerActivity.this, fontSizeBtn);
        fontMenu.getMenuInflater().inflate(R.menu.font_size_menu, fontMenu.getMenu());

        fontMenu.show();

        // PopUp Menu click listener
        fontMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.very_small) {
                lyricsText.setTextSize(15);
                return true;
            } else if (id == R.id.small) {
                lyricsText.setTextSize(17);
                return true;
            } else if (id == R.id.medium) {
                lyricsText.setTextSize(19);
                return true;
            } else if (id == R.id.large) {
                lyricsText.setTextSize(21);
                return true;
            } else if (id == R.id.very_large) {
                lyricsText.setTextSize(23);
                return true;
            }
            return true;
        });
    }

    private void showShapePopupMenu() {
        PopupMenu shapeMenu = new PopupMenu(MusicPlayerActivity.this, shape);
        shapeMenu.getMenuInflater().inflate(R.menu.shape_menu, shapeMenu.getMenu());

        shapeMenu.show();

        // PopUp Menu click listener
        shapeMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.very_small) {
                    cardView.setRadius(12);
                    shape.setImageResource(R.drawable.square);
//                            rotateAnim.setVisibility(View.INVISIBLE);
                    isAnim = false;
                    animStarterForSquare();
                    rotateAnim.setImageResource(R.drawable.anim_off);
                    return true;
                } else if (id == R.id.small) {
                    cardView.setRadius(30);
                    shape.setImageResource(R.drawable.rounded_square);
//                            rotateAnim.setVisibility(View.INVISIBLE);
                    isAnim = false;
                    animStarterForSquare();
                    rotateAnim.setImageResource(R.drawable.anim_off);
                    return true;
                } else if (id == R.id.medium) {
                    cardView.setRadius(600);
//                            cardViewUn.setRadius(600);
                    isAnim = false;
                    //                            toggleAnim();
                    shape.setImageResource(R.drawable.circle);
                    rotateAnim.setVisibility(View.VISIBLE);
                    seekVol.setVisibility(View.INVISIBLE);
                    animStarterForSquare();
                    rotateAnim.setImageResource(R.drawable.anim_off);
                    return true;
                } else if (id == R.id.large) {
                    cardView.setRadius(600);
                    shape.setImageResource(R.drawable.anim_circle);
                    isAnim = true;
                    if (musicService.isPlaying() && isAnim) {
                        rotateAnim.setImageResource(R.drawable.anim_on);
                        toggleAnim();
                    } else {
                        rotateAnim.setImageResource(R.drawable.anim_off);
                        circleCard();
                    }
                    rotateAnim.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_once));
                    rotateAnim.setVisibility(View.VISIBLE);
                    seekVol.setVisibility(View.INVISIBLE);

                    return true;
                }
                return true;
            }
        });
    }

    private void goToArtist() {
        String artistTitle = currentSong.getArtist();
        Intent intent = new Intent(this, ArtistDetails.class);
        intent.putExtra("artistName", artistTitle);
        intent.putExtra("artistTitle", artistTitle);
        this.startActivity(intent);
    }

    private void goToAlbum() {
        String albumTitle = currentSong.getAlbum();
        String albumYear = currentSong.getYear();
        Intent intent = new Intent(this, AlbumDetails.class);
        intent.putExtra("albumName", albumTitle);
        intent.putExtra("albumTitle", albumTitle);
        intent.putExtra("albumYear", albumYear);
        this.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void showLyrics() {

        if (isVol) {
            seekVol.setVisibility(View.VISIBLE);
        } else {
            seekVol.setVisibility(View.INVISIBLE);
        }

        isLyrics = !isLyrics;
        if (isLyrics) {
            flipCard();
//            lyricsText.setText(R.string.no_lyrics_found);
            lyricsTitle.setText("Lyrics of '"+ currentSong.getTitle() + "'");
            lyricsBtn.setImageResource(R.drawable.lyrics_on);
//            musicCover.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off_ly));
        } else {
            flipLyrics();
            lyricsBtn.setImageResource(R.drawable.lyrics_off);
        }

        if (musicService.isPlaying() && isAnim) {
            toggleAnim();
            if (isAnim) {
                circleCard();
                animStarter();
            } else {
                animStarterForSquare();
                circleCard();
            }
        } else {
            animStarterForSquare();
            circleCard();

        }
    }

    @SuppressLint("SetTextI18n")
    private void searchLyrics() {
        String SongTitle = currentSong.getTitle(); // Get song title from your UI
        String SongArtist = currentSong.getArtist(); // Get song title from your UI
        String title = SongTitle.toLowerCase();
        String artist = SongArtist.toLowerCase();

        lyricsTitle.setText("Lyrics of '" + SongTitle + "'");

        lyricsText.setText(R.string.finding_lyrics);
        lyricsProg.setVisibility(View.VISIBLE);


        new Thread(() -> {
            String lyrics = LyricsFinder.find(title, artist);

            runOnUiThread(() -> {

                lyricsTitle.setText("Lyrics of '" + SongTitle + "'");

                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED  ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                    if (lyrics == null){
                        lyricsText.setText(R.string.lyrics_not_found);
                    }else {
                        lyricsText.setText(lyrics + "\n\n\n");
                    }

                }
                else {
                    lyricsText.setText(R.string.check_internet_connection);
//                    Snackbar.make(findViewById(R.id.lyricsLayout), "Not Connected to Internet", Snackbar.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(R.id.lyricsLayout), "No Internet", Snackbar.LENGTH_LONG)
                            .setAction("Go to Settings", view -> {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }).setActionTextColor(Color.parseColor("#00B0FF")).show();

                }

                lyricsProg.setVisibility(View.INVISIBLE);

            });
        }).start();


    }


    private void showLyricsInfoDialog() {
        new MaterialAlertDialogBuilder(MusicPlayerActivity.this)
                .setTitle("Info")
                .setIcon(R.drawable.ic_lyrics)
                .setMessage(R.string.lyrics_info)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }


    private void shareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(currentSong.getPath()));
        this.startActivity(Intent.createChooser(shareIntent, "Sharing Music File via"));
    }

//    private void setFullScreen() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    }



    private void initViews() {
        songTitleTv = findViewById(R.id.songTitle);
        artistTv = findViewById(R.id.artistTitle);
        currentTimeTv = findViewById(R.id.currentDuration);
        totalTimeTv = findViewById(R.id.totalDuration);
        seekBar = findViewById(R.id.seekBar);
        seekVol = findViewById(R.id.seekVol);
        play = findViewById(R.id.playPauseBtn);
        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);
        musicCover = findViewById(R.id.cover_art);
        loop = findViewById(R.id.loopBtn);
        shuffle = findViewById(R.id.shuffleBtn);
        seekBar = findViewById(R.id.seekBar);
        backBtn = findViewById(R.id.back);
        shape = findViewById(R.id.shapeBtn);
        songInfo = findViewById(R.id.information);
        fontSizeBtn = findViewById(R.id.fontSize);
        copyBtn = findViewById(R.id.copy);
        lyricsInfoBtn = findViewById(R.id.info_dialog);
        equalizerBtn = findViewById(R.id.equalizerBtn);
        rotateAnim = findViewById(R.id.animBtn);

        lyricsBtn = findViewById(R.id.lyrics);
        searchLyricsBtn = findViewById(R.id.searchLyrics);
        volumeBtn = findViewById(R.id.volume);
        favBtn = findViewById(R.id.favourite);
        moreOptionBtn = findViewById(R.id.more_op_tooltip);

        cardView = findViewById(R.id.cardView);
        mContainer = findViewById(R.id.mContainer);
        buttonPanel = findViewById(R.id.buttonPanel);
        scrollView = findViewById(R.id.lyricsLayout);
        linearBtnLayout = findViewById(R.id.linearBtnLayout);
        lyricsTitle = findViewById(R.id.lyricsTitle);
        lyricsText = findViewById(R.id.lyricsText);
        lyricsProg = findViewById(R.id.lyricsProgressBar);
        lyrics_gradient = findViewById(R.id.lyrics_gradient);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekVol.setMax(maxVol);

        curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekVol.setProgress(curVol);
//        recyclerView = findViewById(R.id.nowPlayingRV);

    }


    private void getIntentMethod() {
//        url = getIntent().getStringExtra("url");
        position = getIntent().getIntExtra("position", -1);
        String albumSender = getIntent().getStringExtra("albumSender");
        String artistSender = getIntent().getStringExtra("artistSender");
        String nowPlaying = getIntent().getStringExtra("nowPlaying");
        String favIntent = getIntent().getStringExtra("favIntent");
        String mainPlayIntent = getIntent().getStringExtra("mainPlayIntent");
        String favShuffleIntent = getIntent().getStringExtra("favShuffle");



        if (albumSender != null && albumSender.equals("albumDetails")) {
            listSongs = albumSongFiles;
            startPlaying();
        }

        else if (artistSender != null && artistSender.equals("artistDetails")) {
            listSongs = artistSongFiles;
            startPlaying();
        }

        else if (nowPlaying != null && nowPlaying.equals("NowPlaying")) {
//            Not anything
            playingSetUp();
        }

        else if (favIntent != null && favIntent.equals("FavIntent")) {
            listSongs = favouriteFiles;
            startPlaying();
        }

        else if (mainPlayIntent != null && mainPlayIntent.equals("MainPlayIntent")) {
            listSongs = mFiles;
            startPlaying();
        }

        else if (favShuffleIntent != null && favShuffleIntent.equals("FavShuffle")) {
            listSongs = favouriteFiles;
            startPlaying();
        }

        else {
            listSongs = mFiles;
            startPlaying();
        }


    }

    private void playingSetUp() {
        if (musicService != null  &&  musicService.isPlaying()){
            play.setImageResource(R.drawable.pause);
        }else {
            play.setImageResource(R.drawable.play);
        }

        if (isVol){
            seekVol.setVisibility(View.VISIBLE);
        }else {
            seekVol.setVisibility(View.INVISIBLE);
        }

        if (isLoop || isShuffle){
            if (isLoop){
                loop.setImageResource(R.drawable.repeat_on);
            }else {
                loop.setImageResource(R.drawable.repeat_off);
            }

            if (isShuffle){
                shuffle.setImageResource(R.drawable.shuffle_on);
            }else {
                shuffle.setImageResource(R.drawable.shuffle_off);
            }
        }

        if (musicService != null){
            if (musicService.isPlaying() && isAnim) {
                rotateAnim.setImageResource(R.drawable.anim_on);
                toggleAnim();
            } else {
                rotateAnim.setImageResource(R.drawable.anim_off);
                circleCard();
            }
        }
    }


    private void startPlaying(){
        if (listSongs != null) {
            play.setImageResource(R.drawable.pause);
            playPauseBtn.setImageResource(R.drawable.pause_bottom);
            uri = Uri.parse(listSongs.get(position).getPath());

            if (isShuffle){
                shuffle.setImageResource(R.drawable.shuffle_on);
            }else {
                shuffle.setImageResource(R.drawable.shuffle_off);
            }

            try {
                //  start playing
                Intent intent = new Intent(this, MusicService.class);
                intent.putExtra("servicePosition", position);
                startService(intent);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString() , Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void animStarterForSquare() {
        musicCover.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.square_shape_anim));

    }

    private void animStarter() {
        //        Animation fade in and rotate clockwise
        if (musicService != null && musicService.isPlaying()) {
            musicCover.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise));
        } else {
            musicCover.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.square_shape_anim));
        }
    }


    private void forwardTenSecond() {
        if (musicService != null) {
            int currentPosition = musicService.getCurrentPosition();
            int newPosition;

            if (currentPosition + SEEK_DURATION < musicService.getDuration()) {
                newPosition = currentPosition + SEEK_DURATION;

            } else {
                newPosition = musicService.getDuration();
                Toast.makeText(MusicPlayerActivity.this, "End of the song", Toast.LENGTH_SHORT).show();
            }


            if (musicService != null) {
                musicService.seekTo(newPosition);
                showNotificationWithPreference();
            }
        }
    }

    private void backwardTenSecond() {
        if (musicService != null) {
            int currentPosition = musicService.getCurrentPosition();
            int newPosition;

            if (currentPosition - SEEK_DURATION > 0) {
                newPosition = currentPosition - SEEK_DURATION;
            } else {
                newPosition = 0;
                Toast.makeText(MusicPlayerActivity.this, "Start of the song", Toast.LENGTH_SHORT).show();

            }
            if (musicService != null) {
                musicService.seekTo(newPosition);
                showNotificationWithPreference();
            }
        }
    }



    private void copyToClipboard() {
        String text = lyricsText.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("TextView", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Lyrics copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }




    private void flipCard() {
        lyricsBtn.setImageResource(R.drawable.lyrics_on);
        AnimatorSet setFlipIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_in);

        setFlipIn.setTarget(cardView);

        setFlipIn.start();

        cardView.setVisibility(View.INVISIBLE);
        musicCover.setVisibility(View.INVISIBLE);

        lyricsTitle.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
        lyricsText.setVisibility(View.VISIBLE);
        fontSizeBtn.setVisibility(View.VISIBLE);
        searchLyricsBtn.setVisibility(View.VISIBLE);
        copyBtn.setVisibility(View.VISIBLE);
        lyricsInfoBtn.setVisibility(View.VISIBLE);

//        lyrics_gradient.setVisibility(View.VISIBLE);
//        lyrics_gradient.setBackground(gradientDrawable);
    }

    private void flipLyrics() {
        lyricsBtn.setImageResource(R.drawable.lyrics_off);
        AnimatorSet setFlipOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.flip_out);

        setFlipOut.setTarget(cardView);

        setFlipOut.start();

        setImageArt();

        lyricsTitle.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        lyricsText.setVisibility(View.INVISIBLE);
        lyricsProg.setVisibility(View.INVISIBLE);
        fontSizeBtn.setVisibility(View.INVISIBLE);
        searchLyricsBtn.setVisibility(View.INVISIBLE);
        copyBtn.setVisibility(View.INVISIBLE);
        lyricsInfoBtn.setVisibility(View.INVISIBLE);

        cardView.setVisibility(View.VISIBLE);
        musicCover.setVisibility(View.VISIBLE);

//        lyrics_gradient.setVisibility(View.INVISIBLE);


    }


    private void circleCard() {
        if (cardView.getRadius() == 600) {
            cardView.setRadius(600);
            animStarterForSquare();
            if (isAnim) {
                rotateAnim.setImageResource(R.drawable.anim_on);
            } else {
                rotateAnim.setImageResource(R.drawable.anim_off);
            }
        } else if (cardView.getRadius() == 30) {
            cardView.setRadius(30);
            animStarterForSquare();
            if (isAnim) {
                rotateAnim.setImageResource(R.drawable.anim_on);
            } else {
                rotateAnim.setImageResource(R.drawable.anim_off);
            }
        }
    }

    public void toggleAnim() {
        if (musicService.isPlaying() && isAnim) {
            rotateAnim.setImageResource(R.drawable.anim_on);
            cardView.setRadius(600);
            shape.setImageResource(R.drawable.anim_circle);
            animStarter();
        } else {
            circleCard();
            rotateAnim.setImageResource(R.drawable.anim_off);
            shape.setImageResource(R.drawable.shape);
        }
    }


    @SuppressLint("ResourceAsColor")
    private void initializePlayerMetaData() {

        songTitleTv.setText(listSongs.get(position).getTitle());
        artistTv.setText(listSongs.get(position).getArtist());
//        frag_mini_player.setVisibility(View.VISIBLE);
        NowPlayingFragment.songName.setText(listSongs.get(position).getTitle());
        NowPlayingFragment.artistName.setText(listSongs.get(position).getArtist());
        seekBar.setMax(totalDuration);
        headerSongTitle.setTextColor(Color.WHITE);
        mini_player_progressBar.setMax(totalDuration);
        totalTimeTv.setText(millisecondsToTime(totalDuration));
        NowPlayingFragment.mini_player_progressBar.setTrackCornerRadius(12);
//        totalDuration = musicService.getDuration();

        if (thumb != null) {

            musicCover.setPadding(0, 0, 0, 0);

            musicCover.setImageBitmap(thumb);
            //        settingImageArt on Mini player
            albumArt.setImageBitmap(thumb);
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

//                        int myTintColor = manipulateColor(lightVibrantColor, 4f);

//                        int lightVibrantColor = mPalette.getLightVibrantColor(Color.WHITE);
//                        int darkVibrantColor = mPalette.getDarkVibrantColor(Color.BLACK);
//                        int VibrantColor = mPalette.getVibrantColor(Color.WHITE);
//                        int darkVibrantColor = mPalette.getDarkVibrantColor(Color.GRAY);
//                        int darkVibrantColor2 = mPalette.getLightMutedColor(Color.GRAY);
//                        int lightStatusColor = mPalette.getLightVibrantColor(Color.TRANSPARENT);

//                        int lightColor = manipulateColor(dominantColor, 0.7f);
                        int lightColor1 = manipulateColor(dominantColor, 0.8f);
                        int lightColor2 = manipulateColor(dominantColor, 0.9f);
                        int lightColor3 = manipulateColor(dominantColor, 0.95f);
                        int lightColor4 = manipulateColor(dominantColor, 1f);
//                        int darkColor = mPalette.getDarkVibrantColor(Color.WHITE);

//                        status bar color
                        getWindow().setStatusBarColor(lightColor4);

                        // Create gradient drawable
                        gradientDrawable = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{lightColor1, lightColor2, lightColor3, lightColor4, lightColor4});
                        mContainer.setBackground(gradientDrawable);

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


                        songTitleTv.setTextColor(lightVibrantColor);
                        artistTv.setTextColor(lightVibrantColor);
                        lyricsTitle.setTextColor(lightVibrantColor);
                        lyricsText.setTextColor(lightVibrantColor);
                        totalTimeTv.setTextColor(lightVibrantColor);
                        currentTimeTv.setTextColor(lightVibrantColor);

                        backBtn.setColorFilter(lightVibrantColor);
                        equalizerBtn.setColorFilter(lightVibrantColor);
                        shape.setColorFilter(lightVibrantColor);
                        songInfo.setColorFilter(lightVibrantColor);

                        play.setColorFilter(lightVibrantColor);
                        previousBtn.setColorFilter(lightVibrantColor);
                        nextBtn.setColorFilter(lightVibrantColor);
                        volumeBtn.setColorFilter(lightVibrantColor);
                        lyricsBtn.setColorFilter(lightVibrantColor);
                        favBtn.setColorFilter(lightVibrantColor);
                        rotateAnim.setColorFilter(lightVibrantColor);
                        moreOptionBtn.setColorFilter(lightVibrantColor);

                        copyBtn.setColorFilter(lightVibrantColor);
                        lyricsInfoBtn.setColorFilter(lightVibrantColor);
                        searchLyricsBtn.setColorFilter(lightVibrantColor);
                        fontSizeBtn.setColorFilter(lightVibrantColor);

                        seekBar.setThumbTintList(ColorStateList.valueOf(lightVibrantColor));
                        seekVol.setThumbTintList(ColorStateList.valueOf(lightVibrantColor));
                        seekBar.setProgressTintList(ColorStateList.valueOf(lightVibrantColor));
                        seekVol.setProgressTintList(ColorStateList.valueOf(lightVibrantColor));
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
//                            playPauseBtn.setBackgroundColor(Color.parseColor("#E8DEF8"));

                        }
                        if (isLoop){
                            loop.setColorFilter(lightVibrantColor);
                        }else {
                            loop.setColorFilter(R.color.btn_off);
                        }
                        if (isShuffle){
                            shuffle.setColorFilter(lightVibrantColor);
                        }else {
                            shuffle.setColorFilter(R.color.btn_off);
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
            musicCover.setPadding(70, 60, 70, 60);

//            getWindow().setStatusBarColor(Color.BLACK);

            musicCover.setImageResource(R.drawable.music_note_player);
            albumArt.setImageResource(R.drawable.music_note);
            headerImage.setImageResource(R.drawable.music_note);

            if (isNight) {

//                GradientDrawable gradientDrawable = new GradientDrawable(
//                        GradientDrawable.Orientation.BOTTOM_TOP,
//                        new int[]{Color.parseColor("#141B1B"), Color.parseColor("#141B1B"), Color.parseColor("#141B1B"), Color.parseColor("#141B1B")}
//                );

                musicCover.setImageResource(R.drawable.music_note_player);
                albumArt.setImageResource(R.drawable.music_note);
                headerImage.setImageResource(R.drawable.music_note);

                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{Color.BLACK, Color.BLACK, Color.BLACK, Color.parseColor("#141218"), Color.parseColor("#242B2E"), Color.parseColor("#414F55"), Color.parseColor("#61757E"), Color.parseColor("#7D96A2"), Color.parseColor("#8DABB8")} // You can modify this array to create different gradients
                );

                // Set the gradient drawable as the background of your music player view
                mContainer.setBackground(gradientDrawable);
//                statusBarColor
                getWindow().setStatusBarColor(Color.parseColor("#8DABB8"));


//              new int[]{Color.BLACK, Color.BLACK, Color.parseColor("#141218"), Color.parseColor("#242B2E"), Color.parseColor("#414F55"), Color.parseColor("#61757E"), Color.parseColor("#7D96A2"), Color.parseColor("#8DABB8")});

                GradientDrawable gradientDrawableMiniPlayer = new GradientDrawable(
                        GradientDrawable.Orientation.RIGHT_LEFT,
                        new int[]{Color.BLACK, Color.BLACK, Color.BLACK, Color.parseColor("#141218"), Color.parseColor("#242B2E"), Color.parseColor("#414F55"), Color.parseColor("#61757E"), Color.parseColor("#7D96A2"), Color.parseColor("#8DABB8")} // You can modify this array to create different gradients
                );

                gradientDrawableMiniPlayer.setCornerRadius(17);

                NowPlayingFragment.mini_player.setBackground(gradientDrawableMiniPlayer);
                headerBackground.setBackground(gradientDrawableMiniPlayer);


                songTitleTv.setTextColor(Color.WHITE);
                artistTv.setTextColor(Color.WHITE);
                lyricsTitle.setTextColor(Color.WHITE);
                lyricsText.setTextColor(Color.WHITE);
                currentTimeTv.setTextColor(Color.WHITE);
                totalTimeTv.setTextColor(Color.WHITE);

                backBtn.setColorFilter(Color.WHITE);
                equalizerBtn.setColorFilter(Color.WHITE);
                shape.setColorFilter(Color.WHITE);
                songInfo.setColorFilter(Color.WHITE);

                play.setColorFilter(Color.WHITE);
                previousBtn.setColorFilter(Color.WHITE);
                nextBtn.setColorFilter(Color.WHITE);
                volumeBtn.setColorFilter(Color.WHITE);
                lyricsBtn.setColorFilter(Color.WHITE);
                favBtn.setColorFilter(Color.WHITE);
                rotateAnim.setColorFilter(Color.WHITE);
                moreOptionBtn.setColorFilter(Color.WHITE);

                copyBtn.setColorFilter(Color.WHITE);
                lyricsInfoBtn.setColorFilter(Color.WHITE);
                searchLyricsBtn.setColorFilter(Color.WHITE);
                fontSizeBtn.setColorFilter(Color.WHITE);

                seekBar.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
                seekBar.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
                seekVol.setThumbTintList(ColorStateList.valueOf(Color.WHITE));
                seekVol.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
                NowPlayingFragment.mini_player_progressBar.setIndicatorColor(Color.WHITE);

                NowPlayingFragment.songName.setTextColor(Color.WHITE);
                NowPlayingFragment.artistName.setTextColor(Color.WHITE);
                NowPlayingFragment.playPauseBtn.setColorFilter(Color.WHITE);
                NowPlayingFragment.nextBtn.setColorFilter(Color.WHITE);
                NowPlayingFragment.prevBtn.setColorFilter(Color.WHITE);
//                NowPlayingFragment.playPauseBtn.setBackgroundColor(Color.parseColor("#4A4458"));

                if (isLoop){
                    loop.setColorFilter(Color.WHITE);
                }else {
                    loop.setColorFilter(R.color.btn_off);
                }

                if (isShuffle){
                    shuffle.setColorFilter(Color.WHITE);
                }else {
                    shuffle.setColorFilter(R.color.btn_off);
                }

//                NowPlayingFragment.mini_player.setBackgroundColor(R.color.colorPrimaryDark);

            } else if (isLight) {

                musicCover.setImageResource(R.drawable.music_note);
                albumArt.setImageResource(R.drawable.music_note);
                headerImage.setImageResource(R.drawable.music_note);

                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{Color.parseColor("#D0F4FF"), Color.parseColor("#B9EFFF"), Color.parseColor("#A5EBFF")}
                );

                // Set the gradient drawable as the background of your music player view
                mContainer.setBackground(gradientDrawable);
                //                statusBarColor
                getWindow().setStatusBarColor(Color.parseColor("#8DABB8"));

//                mini_player background
                GradientDrawable gradientDrawableMiniPlayer = new GradientDrawable(
                        GradientDrawable.Orientation.RIGHT_LEFT,
                        new int[]{Color.parseColor("#E9EDFA"), Color.parseColor("#E0E0FF"), Color.parseColor("#DDDDFF")}
                );

                gradientDrawableMiniPlayer.setCornerRadius(17);

                NowPlayingFragment.mini_player.setBackground(gradientDrawableMiniPlayer);
                headerBackground.setBackground(gradientDrawableMiniPlayer);


                songTitleTv.setTextColor(Color.BLACK);
                artistTv.setTextColor(Color.BLACK);
                lyricsTitle.setTextColor(Color.BLACK);
                lyricsText.setTextColor(Color.BLACK);
                currentTimeTv.setTextColor(Color.BLACK);
                totalTimeTv.setTextColor(Color.BLACK);

                backBtn.setColorFilter(Color.BLACK);
                equalizerBtn.setColorFilter(Color.BLACK);
                shape.setColorFilter(Color.BLACK);
                songInfo.setColorFilter(Color.BLACK);

                play.setColorFilter(Color.BLACK);
                previousBtn.setColorFilter(Color.BLACK);
                nextBtn.setColorFilter(Color.BLACK);
                volumeBtn.setColorFilter(Color.BLACK);
                lyricsBtn.setColorFilter(Color.BLACK);
                favBtn.setColorFilter(Color.BLACK);
                rotateAnim.setColorFilter(Color.BLACK);
                moreOptionBtn.setColorFilter(Color.BLACK);

                copyBtn.setColorFilter(Color.BLACK);
                searchLyricsBtn.setColorFilter(Color.BLACK);
                fontSizeBtn.setColorFilter(Color.BLACK);

                seekBar.setThumbTintList(ColorStateList.valueOf(Color.BLACK));
                seekBar.setProgressTintList(ColorStateList.valueOf(Color.BLACK));
                seekVol.setThumbTintList(ColorStateList.valueOf(Color.BLACK));
                seekVol.setProgressTintList(ColorStateList.valueOf(Color.BLACK));
                NowPlayingFragment.mini_player_progressBar.setIndicatorColor(R.color.black);

                NowPlayingFragment.songName.setTextColor(R.color.black);
                NowPlayingFragment.artistName.setTextColor(R.color.black);
                NowPlayingFragment.playPauseBtn.setColorFilter(R.color.black);
                NowPlayingFragment.nextBtn.setColorFilter(R.color.black);
                NowPlayingFragment.prevBtn.setColorFilter(R.color.black);
//                NowPlayingFragment.playPauseBtn.setBackgroundColor(Color.parseColor("#E8DEF8"));

                if (isLoop){
                    loop.setColorFilter(Color.BLACK);
                }else {
                    loop.setColorFilter(R.color.btn_off);
                }

                if (isShuffle){
                    shuffle.setColorFilter(Color.BLACK);
                }else {
                    shuffle.setColorFilter(R.color.btn_off);
                }

//                NowPlayingFragment.mini_player.setBackgroundColor(R.color.colorPrimaryDark);

            }
        }

    }


//    private void nextThreadBtn() {
//        nextThread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                nextBtn.setOnClickListener(view -> {
//                    nextBtnClicked();
//                    nextBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_next_prev_btn));
//
//                });
//            }
//        };
//        nextThread.start();
//    }

    @SuppressLint("SetTextI18n")
    public void nextBtnClicked() {
        if (musicService != null) {
            musicService.stop();
            musicService.release();


            if (isShuffle && !isLoop) {
                try {
                    if (listSongs != null) {
                        position = getRandom(listSongs.size() - 1);
                    } else {
                        Toast.makeText(this, "No Composition Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No next Composition because" + e, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
            } else if (!isShuffle && !isLoop) {
                try {
                    if (listSongs != null) {
                        position = ((position + 1) % listSongs.size());
                    } else {
                        Toast.makeText(this, "No Composition Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No next Composition because" + e, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
            }
            if (listSongs != null) {
                uri = Uri.parse(listSongs.get(position).getPath());
                musicService.createMediaPlayer(position);
                initializeLayout();
                seekBar.setMax(totalDuration);
                setNotificationArt();
                mini_player_progressBar.setMax(totalDuration);
            }


            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        currentDuration = musicService.getCurrentPosition();
                        totalDuration = musicService.getDuration();

                        seekBar.setProgress(currentDuration);
                        seekBar.setMax(totalDuration);
                        mini_player_progressBar.setMax(totalDuration);

                        currentTimeTv.setText(millisecondsToTime(currentDuration));
                        totalTimeTv.setText(millisecondsToTime(totalDuration));

                    }
                    handler.postDelayed(this, 100);
                }
            });

            musicService.onCompleted();
            isNext = true;
            isPlaying = true;
            musicService.start();
            musicService.showNotification(R.drawable.pause_noti, 1F);
            playPauseBtn.setImageResource(R.drawable.pause_bottom);
            play.setImageResource(R.drawable.pause);
            setImageArt();
            //            setting Music Cover art

        }
        else {

            if (isShuffle && !isLoop) {
                try {
                    if (listSongs != null) {
                        position = getRandom(listSongs.size() - 1);
                    } else {
                        Toast.makeText(this, "No Composition Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No next Composition because" + e, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
            } else if (!isShuffle && !isLoop) {
                try {
                    if (listSongs != null) {
                        position = ((position + 1) % listSongs.size());
                    } else {
                        Toast.makeText(this, "No Composition Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "No next Composition because" + e, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
            }
            if (listSongs != null) {
                uri = Uri.parse(listSongs.get(position).getPath());
                musicService.createMediaPlayer(position);
                initializeLayout();
                seekBar.setMax(totalDuration);
                setNotificationArt();
                mini_player_progressBar.setMax(totalDuration);
            }

            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        currentDuration = musicService.getCurrentPosition();
                        totalDuration = musicService.getDuration();

                        seekBar.setProgress(currentDuration);
                        seekBar.setMax(totalDuration);
                        mini_player_progressBar.setMax(totalDuration);

                        currentTimeTv.setText(millisecondsToTime(currentDuration));
                        totalTimeTv.setText(millisecondsToTime(totalDuration));

                    }
                    handler.postDelayed(this, 100);
                }
            });

            musicService.onCompleted();
            isNext = true;
            musicService.showNotification(R.drawable.play_noti, 1F);
            play.setImageResource(R.drawable.play);
            playPauseBtn.setImageResource(R.drawable.play_bottom);
            // setting Mini Music Cover art
            setImageArt();

        }

        if (musicService != null){
            if (musicService.isPlaying() && isAnim) {
                rotateAnim.setImageResource(R.drawable.anim_on);
                toggleAnim();
            } else {
                rotateAnim.setImageResource(R.drawable.anim_off);
                circleCard();
            }
        }

        if (isLyrics){
            lyricsText.setText("Embedded Lyrics not found");
            searchLyrics();
        }


    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

//    private void prevThreadBtn() {
//        prevThread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                previousBtn.setOnClickListener(view -> {
//                    prevBtnClicked();
//                    previousBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_next_prev_btn));
//
//                });
//            }
//        };
//        prevThread.start();
//    }

    public void prevBtnClicked() {

        if (musicService != null) {
            musicService.stop();
            musicService.release();

            if (isShuffle && !isLoop) {
                try {
                    if (listSongs != null) {
                        position = getRandom(listSongs.size() - 1);
                    }
                    else {
                        Toast.makeText(this, "No Composition Found" , Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "No previous Composition because"+ e, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }

            } else if (!isShuffle && !isLoop) {
                try {
                    if (listSongs != null) {
                        position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
                    }
                    else {
                        Toast.makeText(this, "No Composition Found" , Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "No previous Composition because"+ e, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
            }

            if (listSongs != null) {
                uri = Uri.parse(listSongs.get(position).getPath());
                musicService.createMediaPlayer(position);
                initializeLayout();
                seekBar.setMax(totalDuration);
                setNotificationArt();
                mini_player_progressBar.setMax(totalDuration);
            }

            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        currentDuration = musicService.getCurrentPosition();
                        totalDuration = musicService.getDuration();

                        seekBar.setProgress(currentDuration);
                        seekBar.setMax(totalDuration);
                        mini_player_progressBar.setMax(totalDuration);

                        currentTimeTv.setText(millisecondsToTime(currentDuration));
                        totalTimeTv.setText(millisecondsToTime(totalDuration));

                    }
                    handler.postDelayed(this, 100);
                }
            });

            musicService.onCompleted();
            isPrev = true;
            isPlaying = true;
            musicService.start();
            musicService.showNotification(R.drawable.pause_noti, 1F);
            play.setImageResource(R.drawable.pause);
            playPauseBtn.setImageResource(R.drawable.pause_bottom);
//            setting Music Cover art
            setImageArt();

        } else {

            if (isShuffle && !isLoop) {
                try {
                    if (listSongs != null) {
                        position = getRandom(listSongs.size() - 1);
                    }
                    else {
                        Toast.makeText(this, "No Composition Found" , Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "No previous Composition because"+ e, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
            } else if (!isShuffle && !isLoop) {
                try {
                    if (listSongs != null) {
                        position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
                    }
                    else {
                        Toast.makeText(this, "No Composition Found" , Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "No previous Composition because"+ e, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    this.startActivity(intent);
                }
            }

            if (listSongs != null) {
                uri = Uri.parse(listSongs.get(position).getPath());
                musicService.createMediaPlayer(position);
                initializeLayout();
                seekBar.setMax(totalDuration);
                setNotificationArt();
                mini_player_progressBar.setMax(totalDuration);
            }

            MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        currentDuration = musicService.getCurrentPosition();
                        totalDuration = musicService.getDuration();

                        seekBar.setProgress(currentDuration);
                        seekBar.setMax(totalDuration);
                        mini_player_progressBar.setMax(totalDuration);

                        currentTimeTv.setText(millisecondsToTime(currentDuration));
                        totalTimeTv.setText(millisecondsToTime(totalDuration));

                    }
                    handler.postDelayed(this, 100);
                }
            });

            musicService.onCompleted();
            isPrev = true;
            isPlaying = true;
            musicService.start();
            musicService.showNotification(R.drawable.play_noti, 1F);
            play.setImageResource(R.drawable.play);
            playPauseBtn.setImageResource(R.drawable.play_bottom);
//            setting MusicCover art
            setImageArt();

        }

        if (musicService != null){
            if (musicService.isPlaying() && isAnim) {
                rotateAnim.setImageResource(R.drawable.anim_on);
                toggleAnim();
            } else {
                rotateAnim.setImageResource(R.drawable.anim_off);
                circleCard();
            }
        }

        if (isLyrics){
            lyricsText.setText("Embedded Lyrics not found");
            searchLyrics();
        }


    }

    private void setImageArt() {
        if (thumb != null){
            Glide.with(getBaseContext()).load(thumb).into(albumArt);
        }
        else {
            Glide.with(getBaseContext()).load(R.drawable.music_note_player).into(albumArt);
        }
    }


//    private void playThreadBtn() {
//        playThread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                play.setOnClickListener(view -> {
//                    playPauseBtnClicked();
//
//                    if (musicService.isPlaying()){
////                    pause
//                        play.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_once_pause));
//                    } else {
////                    play
//                        play.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_once_play));
//                    }
//
//                });
//            }
//        };
//        playThread.start();
//    }

    public void playPauseBtnClicked() {

        if (musicService != null) {
//            play.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_once_play));
            if (musicService.isPlaying()) {
                musicService.pause();
                play.setImageResource(R.drawable.play);
                isPlaying = false;
                musicService.showNotification(R.drawable.play_noti, 0F);
                playPauseBtn.setImageResource(R.drawable.play_bottom);


                MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (musicService != null) {
                            currentDuration = musicService.getCurrentPosition();
                            totalDuration = musicService.getDuration();

                            seekBar.setProgress(currentDuration);
                            seekBar.setMax(totalDuration);
                            mini_player_progressBar.setMax(totalDuration);

                            currentTimeTv.setText(millisecondsToTime(currentDuration));
                            totalTimeTv.setText(millisecondsToTime(totalDuration));

                        }
                        handler.postDelayed(this, 100);
                    }
                });

            }
            else {
//            play.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_once_pause));

                musicService.start();
                play.setImageResource(R.drawable.pause);
                isPlaying = true;
                musicService.showNotification(R.drawable.pause_noti, 1F);
                playPauseBtn.setImageResource(R.drawable.pause_bottom);


                MusicPlayerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (musicService != null) {
                            currentDuration = musicService.getCurrentPosition();
                            totalDuration = musicService.getDuration();

                            seekBar.setProgress(currentDuration);
                            seekBar.setMax(totalDuration);
                            mini_player_progressBar.setMax(totalDuration);

                            currentTimeTv.setText(millisecondsToTime(currentDuration));
                            totalTimeTv.setText(millisecondsToTime(totalDuration));

                        }
                        handler.postDelayed(this, 100);
                    }
                });
            }


            if (musicService.isPlaying() && isAnim) {
                rotateAnim.setImageResource(R.drawable.anim_on);
                toggleAnim();
            } else {
                rotateAnim.setImageResource(R.drawable.anim_off);
                circleCard();
            }
        }

    }

//    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
//        Animation animOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
//        Animation animIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
//
//        animOut.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                Glide.with(context).load(bitmap).into(imageView);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                Glide.with(context).load(bitmap).into(imageView);
//
//                animIn.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });
//                imageView.startAnimation(animIn);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        imageView.startAnimation(animOut);
//    }




    @SuppressLint("DefaultLocale")
    public static String millisecondsToTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        int hours = milliseconds / (1000 * 60 * 60);
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }



    @SuppressLint("DefaultLocale")
    public static String formatMB(int bytes) {
        double kb = bytes / 1024.0;
        double mb = kb / 1024.0;
        double gb = mb / 1024.0;

        if (gb >= 1) {
            return String.format("%.2f GB", gb);
        } else if (mb >= 1) {
            return String.format("%.2f MB", mb);
        } else {
            return String.format("%.2f kB", kb);
        }
    }

//    @SuppressLint("DefaultLocale")
//    public static String formatBitrate(int bytes) {
//        int kb = (bytes / 1024) % 1024;
//
//        return String.format("%02d"+ " kb/s", kb);
//
//    }


//    @SuppressLint("DefaultLocale")
//    public static String convertToMMSS(String duration) {
//        long millis = Long.parseLong(duration);
//        return String.format("%02d:%02d",
//                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
//                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
//    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        super.onResume();

//        playThreadBtn();
//        prevThreadBtn();
//        nextThreadBtn();


    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13  ||  requestCode == RESULT_OK){}
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();

        musicService.setCallBack(this);

        initializeLayout();

        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        playingSetUp();

        musicService.onCompleted();

//        requestAudioFocus();

//        audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//        audioManager.requestAudioFocus(musicService, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
//        audioManager.requestAudioFocus(musicService, AudioManager.STREAM_NOTIFICATION, AudioManager.AUDIOFOCUS_GAIN);
//        audioManager.requestAudioFocus(musicService, AudioManager.STREAM_RING, AudioManager.AUDIOFOCUS_GAIN);
//        audioManager.requestAudioFocus(musicService, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN);


    }

//    public void requestAudioFocus() {
//        // Request audio focus
//        int result = audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//
//        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//            // Start playback
//            play.setImageResource(R.drawable.pause);
//            musicService.start();
//            isPlaying = true;
////                Toast.makeText(this, "play called", Toast.LENGTH_SHORT).show();
//            musicService.showNotification(R.drawable.pause_noti, 1F);
//            playPauseBtn.setImageResource(R.drawable.pause_bottom);
//        }
//        else {
//            play.setImageResource(R.drawable.play);
//            musicService.pause();
//            isPlaying = false;
////                Toast.makeText(this, "play called", Toast.LENGTH_SHORT).show();
//            musicService.showNotification(R.drawable.play_noti, 0F);
//            playPauseBtn.setImageResource(R.drawable.play_bottom);
//        }
//    }

    @SuppressLint("SetTextI18n")
    private void initializeLayout() {
        fIndex = Music.favouriteChecker(listSongs.get(position).getId());
        setNotificationArt();
        initializePlayerMetaData();

        if (isNext  ||  isPrev) {
            isPlaying = true;
            musicService.showNotification(R.drawable.pause_noti, 1F);
        }
        else {
            showNotificationWithPreference();
        }

        totalDuration = musicService.getDuration();
        currentDuration = musicService.getCurrentPosition();

        lyricsTitle.setSelected(true);
        songTitleTv.setText(listSongs.get(position).getTitle());
        songTitleTv.setSelected(true);
        artistTv.setText(listSongs.get(position).getArtist());
        artistTv.setSelected(true);
        headerSongTitle.setText(listSongs.get(position).getTitle());
        headerSongArtist.setText(listSongs.get(position).getArtist());


        if (isFav){
            favBtn.setImageResource(R.drawable.favourite_on);
        }else {
            favBtn.setImageResource(R.drawable.favourite_off);
        }

    }

    private void setNotificationArt() {
        byte[] picture;
        picture = getAlbumArt(musicServiceFiles.get(currentSongIndex).getPath());
        if (picture != null) {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        } else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.music_note_player);
        }


//        views.setTextViewText(R.id.widgetTextTitle, currentSong.getTitle());
//        views.setTextViewText(R.id.widgetTextArtist, currentSong.getArtist());
//        views.setImageViewBitmap(R.id.widgetImageCover, thumb);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;

    }


    @SuppressLint("SetTextI18n")
    private void showSongDetails() {
        final Dialog dialog = new Dialog(MusicPlayerActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.song_details_popup);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.song_details_popup);

//        TextView TitleView = dialog.findViewById(R.id.titleView);
        TextView titleTextView = dialog.findViewById(R.id.titleTextView);
        TextView artistTextView = dialog.findViewById(R.id.artistTextView);
        TextView albumTextView = dialog.findViewById(R.id.albumTextView);
        TextView yearTextView = dialog.findViewById(R.id.yearTextView);
        TextView durationTextView = dialog.findViewById(R.id.durationTextView);
        TextView sizeTextView = dialog.findViewById(R.id.sizeTextView);
        TextView pathTitleTextView = dialog.findViewById(R.id.pathTitleTextView);
        TextView pathTextView = dialog.findViewById(R.id.pathTextView);

        titleTextView.setText("Title : " + currentSong.getTitle());
        artistTextView.setText("Artist : " + currentSong.getArtist());
        albumTextView.setText("Album : " + currentSong.getAlbum());
        yearTextView.setText("Year : "+ currentSong.getYear());
        durationTextView.setText("Duration : " + millisecondsToTime(Integer.parseInt(currentSong.getDuration())));
        sizeTextView.setText("Size : "+ formatMB(Integer.parseInt(currentSong.getSize())));
        pathTitleTextView.setText("File path: ");
        pathTextView.setText(currentSong.getPath());


        // Close button
        Button okBtn = dialog.findViewById(R.id.closeButton);
        okBtn.setOnClickListener(v -> {
            okBtn.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_btn));
            dialog.dismiss();

        });

        ImageView songImg = dialog.findViewById(R.id.coverImg);


        byte[] nowPlayingImage = getAlbumArt(listSongs.get(position).getPath());
        if (nowPlayingImage != null){
            Glide.with(this)
                    .load(nowPlayingImage).placeholder(R.drawable.music_note)
                    .into(songImg);
            songImg.setPadding(0, 0, 0, 0);
        }
        else {
            songImg.setPadding(12, 12, 12, 12);
            Glide.with(this)
                    .load(R.drawable.music_note).centerInside()
                    .into(songImg);
        }
//
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void deleteMusicFile(int position, Context context){

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));
        File file = new File(mFiles.get(position).getPath());
        boolean delete = file.delete();

        if (file.exists()){
            delete = !delete;
            if (delete) {
                try {
                    context.getContentResolver().delete(contentUri, null, null);
                    mFiles.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, mFiles.size());
                    Toast.makeText(this, "File Deleted From device", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
//                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();


                }
            }
            else {
                Toast.makeText(this, "File Can't be Deleted", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "File doesn't exist", Toast.LENGTH_SHORT).show();
        }
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
    public void onBackPressed() {
        super.onBackPressed();
    }

}
package com.infinite.virtualmusicplayer.services;

import static com.infinite.virtualmusicplayer.activities.MainActivity.ARTIST_TO_FRAG;
import static com.infinite.virtualmusicplayer.activities.MainActivity.PATH_TO_FRAG;
import static com.infinite.virtualmusicplayer.activities.MainActivity.SHOW_MINI_PLAYER;
import static com.infinite.virtualmusicplayer.activities.MainActivity.SONG_NAME_TO_FRAG;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.finalSeekProgress;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.isPlaying;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.listSongs;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.musicService;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.thumb;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.miniPlayerCoverArt;
import static com.infinite.virtualmusicplayer.fragments.NowPlayingFragment.songName;
import static com.infinite.virtualmusicplayer.receivers.ApplicationClass.ACTION_NEXT;
import static com.infinite.virtualmusicplayer.receivers.ApplicationClass.ACTION_PLAY;
import static com.infinite.virtualmusicplayer.receivers.ApplicationClass.ACTION_PREVIOUS;
import static com.infinite.virtualmusicplayer.receivers.ApplicationClass.CHANNEL_ID_2;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.bumptech.glide.Glide;
import com.infinite.virtualmusicplayer.R;
import com.infinite.virtualmusicplayer.activities.MainActivity;
import com.infinite.virtualmusicplayer.activities.MusicPlayerActivity;
import com.infinite.virtualmusicplayer.fragments.NowPlayingFragment;
import com.infinite.virtualmusicplayer.model.Music;
import com.infinite.virtualmusicplayer.receivers.ActionPlaying;
import com.infinite.virtualmusicplayer.receivers.NotificationReceiver;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

//    , AudioManager.OnAudioFocusChangeListener
    IBinder mBinder = new MyBinder();
    public MediaPlayer mediaPlayer = new MediaPlayer();
    MusicPlayerActivity musicPlayerActivity;
    public static ArrayList<Music> musicServiceFiles = new ArrayList<>();
    public static Music currentSong;
    static Uri musicServiceUri;
    public static int currentSongIndex = -1;
    ActionPlaying actionPlaying;
    static MediaSessionCompat mediaSession;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        Log.e("Bind", "Method");
        mediaSession = new MediaSessionCompat(getBaseContext(), "My Music");
        return mBinder;
    }

//    @Override
//    public void onAudioFocusChange(int focusChange) {
//        if (focusChange != 0){
//            //            pause
//            play.setImageResource(R.drawable.play);
//            pause();
//            isPlaying = false;
////                Toast.makeText(this, "pause called", Toast.LENGTH_SHORT).show();
//            showNotification(R.drawable.play_bottom, 0F);
//            playPauseBtn.setImageResource(R.drawable.play_bottom);
//
//        }
//        else {
////            play
//            play.setImageResource(R.drawable.pause);
//            start();
//            isPlaying = true;
////                Toast.makeText(this, "play called", Toast.LENGTH_SHORT).show();
//            showNotification(R.drawable.pause_bottom, 1F);
//            playPauseBtn.setImageResource(R.drawable.pause_bottom);
//        }
//        switch (focusChange) {
//            case AudioManager.AUDIOFOCUS_GAIN:
//                // Resume playback or start playback if stopped
//                playSetup();
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS:
//                // Stop playback and release resources//
//                pauseSetup();
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                // Pause playback
//                stopSetup();
//                break;
////            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
////                // Lower the volume
////                break;
//        }
//
//    }

//    private void pauseSetup() {
//        //            //            pause
//            play.setImageResource(R.drawable.play);
//            pause();
//            isPlaying = false;
////                Toast.makeText(this, "pause called", Toast.LENGTH_SHORT).show();
//            showNotification(R.drawable.play_bottom, 0F);
//            playPauseBtn.setImageResource(R.drawable.play_bottom);
//    }
//
//    private void stopSetup() {
//        //            //            pause
//            play.setImageResource(R.drawable.play);
//            stop();
//            isPlaying = false;
////                Toast.makeText(this, "pause called", Toast.LENGTH_SHORT).show();
//            showNotification(R.drawable.play_bottom, 0F);
//            playPauseBtn.setImageResource(R.drawable.play_bottom);
//    }
//
//    private void playSetup() {
//        ////            play
//            play.setImageResource(R.drawable.pause);
//            start();
//            isPlaying = true;
////                Toast.makeText(this, "play called", Toast.LENGTH_SHORT).show();
//            showNotification(R.drawable.pause_bottom, 1F);
//            playPauseBtn.setImageResource(R.drawable.pause_bottom);
//    }


    public class MyBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);

        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
//        String url = intent.getStringExtra("url");

        if (myPosition != -1)
        {
            try {
                playMedia(myPosition);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if (actionName != null) {
            switch (actionName) {
                case "playPause":
                    playPauseBtnClicked();

                    break;
                case "next":
                    nextBtnClicked();

                    break;
                case "previous":
                    prevBtnClicked();

                    break;
                case "exit":
                    MusicPlayerActivity.musicService.stopForeground(true);
                    MusicPlayerActivity.musicService = null;
                    System.exit(0);
                    break;
            }
        }



        if (actionPlaying != null){
            SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE).edit();
            editor.putString(MUSIC_FILE, musicServiceFiles.get(currentSongIndex).getPath());
            editor.putString(ARTIST_NAME, musicServiceFiles.get(currentSongIndex).getArtist());
            editor.putString(SONG_NAME, musicServiceFiles.get(currentSongIndex).getTitle());
            editor.apply();

            SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
            String path = preferences.getString(MUSIC_FILE, null);
            String artistName = preferences.getString(ARTIST_NAME, null);
            String song_name = preferences.getString(SONG_NAME, null);
            if (path != null){
                SHOW_MINI_PLAYER = true;
                PATH_TO_FRAG = path;
                ARTIST_TO_FRAG = artistName;
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
                    Glide.with(this).load(art).into(miniPlayerCoverArt);
                }
                else {
                    Glide.with(this).load(R.drawable.music_note).into(miniPlayerCoverArt);
                }
                songName.setText(SONG_NAME_TO_FRAG);
                NowPlayingFragment.artistName.setText(ARTIST_TO_FRAG);
            }
        }

        return START_STICKY;
    }


    private void playMedia(int startPosition) {
        musicServiceFiles = listSongs;
        currentSongIndex = startPosition;
        if (mediaPlayer != null)
        {
           try {
               mediaPlayer.stop();
               mediaPlayer.release();

           }catch (Exception e){
               e.printStackTrace();
//               Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
           }
           try {
               if (musicServiceFiles != null)
               {
                   createMediaPlayer(currentSongIndex);
                   mediaPlayer.start();
               }
           }catch (Exception e) {
               e.printStackTrace();
               Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
           }

        }
        else {
            try {
                if (musicServiceFiles != null)
                {
                    createMediaPlayer(currentSongIndex);
                    mediaPlayer.start();
                }
            }catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public boolean isPlaying() {
       return mediaPlayer.isPlaying();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void release() {
        mediaPlayer.release();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    long getSeekBarCurrentPosition() {
        finalSeekProgress = (long) getCurrentPosition();
        return finalSeekProgress;
    }


    public void createMediaPlayer(int positionInner) {
        currentSongIndex = positionInner;
        currentSong = musicServiceFiles.get(currentSongIndex);
        musicServiceUri = Uri.parse(musicServiceFiles.get(currentSongIndex).getPath());
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE).edit();
        editor.putString(MUSIC_FILE, musicServiceUri.toString());
        editor.putString(ARTIST_NAME, musicServiceFiles.get(currentSongIndex).getArtist());
        editor.putString(SONG_NAME, musicServiceFiles.get(currentSongIndex).getTitle());
        editor.apply();

        mediaPlayer = MediaPlayer.create(getBaseContext(), musicServiceUri);

    }

    public void onCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (actionPlaying != null){
            if (musicService != null){
                nextBtnClicked();
            }

            if (actionPlaying != null){
                SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE).edit();
                editor.putString(MUSIC_FILE, musicServiceFiles.get(currentSongIndex).getPath());
                editor.putString(ARTIST_NAME, musicServiceFiles.get(currentSongIndex).getArtist());
                editor.putString(SONG_NAME, musicServiceFiles.get(currentSongIndex).getTitle());
                editor.apply();

                SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
                String path = preferences.getString(MUSIC_FILE, null);
                String artistName = preferences.getString(ARTIST_NAME, null);
                String song_name = preferences.getString(SONG_NAME, null);
                if (path != null){
                    SHOW_MINI_PLAYER = true;
                    PATH_TO_FRAG = path;
                    ARTIST_TO_FRAG = artistName;
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
                        Glide.with(this).load(art).into(miniPlayerCoverArt);
                    }
                    else {
                        Glide.with(this).load(R.drawable.music_note).into(miniPlayerCoverArt);
                    }
                    songName.setText(SONG_NAME_TO_FRAG);
                    NowPlayingFragment.artistName.setText(ARTIST_TO_FRAG);
                }
            }

        }


    }

    public void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }



    @SuppressLint("ForegroundServiceType")
    public void showNotification(int playPauseBtn, float playbackSpeed) {

//        createMediaSession();
//        imp for mini player
//        intent.putExtra("position", position);
////        intent.putExtra("position", MusicPlayerActivity.position);
//        intent.putExtra("nowPlaying","NowPlaying");

        Intent intent = new Intent(getBaseContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_MUTABLE);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_MUTABLE);

        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_MUTABLE);

//        Intent repeatIntent = new Intent(this, NotificationReceiver.class)
//                .setAction(ACTION_NEXT);
//        PendingIntent repeatPending = PendingIntent.getBroadcast(this, 0, repeatIntent, PendingIntent.FLAG_MUTABLE);
//
//        Intent shuffleIntent = new Intent(this, NotificationReceiver.class)
//                .setAction(ACTION_NEXT);
//        PendingIntent shufflePending = PendingIntent.getBroadcast(this, 0, shuffleIntent, PendingIntent.FLAG_MUTABLE);

//        byte[] picture = null;
//        picture = getAlbumArt(musicServiceFiles.get(currentSongIndex).getPath());
//        Bitmap thumb = null;
//        if (picture != null) {
//            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
//        } else {
//            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.music_note);
//
//        }


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(thumb)
                .setContentIntent(contentIntent)
                .setContentTitle(musicServiceFiles.get(currentSongIndex).getTitle())
                .setContentText(musicServiceFiles.get(currentSongIndex).getArtist())
                .addAction(R.drawable.skip_previous_noti, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.skip_next_noti, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT) // Fixed category

                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (long) getDuration())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE,  musicServiceFiles.get(currentSongIndex).getTitle())
                    .putText(MediaMetadataCompat.METADATA_KEY_ARTIST,  musicServiceFiles.get(currentSongIndex).getArtist())
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, thumb)
                    .build());

            if (isPlaying){
                mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, getSeekBarCurrentPosition(), playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE |
                                PlaybackStateCompat.ACTION_SEEK_TO)
                        .build());
            } else {
                mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED, getSeekBarCurrentPosition(), playbackSpeed)
                        .setActions(PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE |
                                PlaybackStateCompat.ACTION_SEEK_TO)
                        .build());
            }

            mediaSession.setSessionActivity(contentIntent);
            mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mediaSession.setActive(true);
//            mediaSession.setRepeatMode(R.drawable.repeat_on);
//            mediaSession.setShuffleMode(R.drawable.shuffle_on);

            mediaSession.setCallback(new MediaSessionCompat.Callback() {

                @Override
                public void onPlay() {
                    if (musicService != null){
                        playPauseBtnClicked();
                    }
                    super.onPlay();
                }
                //
                @Override
                public void onPause() {
                    if (musicService != null){
                        playPauseBtnClicked();
                    }
                    super.onPause();
                }


                @Override
                public void onSkipToNext() {
                    if (musicService != null){
                        nextBtnClicked();
                    }
                    super.onSkipToNext();
                }

                @Override
                public void onSkipToPrevious() {
                    if (musicService != null){
                        prevBtnClicked();
                    }
                    super.onSkipToPrevious();
                }

                @Override
                public void onSeekTo(long playbackPosition) {
                    seekTo((int) playbackPosition);
                    super.onSeekTo(playbackPosition);
                }

//            @Override
//            public void onSetRepeatMode(int repeatMode) {
//                super.onSetRepeatMode(repeatMode);
//            }
//
//            @Override
//            public void onSetShuffleMode(int shuffleMode) {
//                super.onSetShuffleMode(shuffleMode);
//            }

            });

        }

//        .addAction(R.drawable.repeat_on, "Repeat", repeatPending)
//                .addAction(R.drawable.shuffle_on, "Shuffle", shufflePending)

        startForeground(13, notification);

//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification);


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


    public void nextBtnClicked(){
        if (musicService != null){
            if (actionPlaying != null){
                actionPlaying.nextBtnClicked();
            }
            else if (getBaseContext() != null){
                musicPlayerActivity.nextBtnClicked();
            }
        }

    }

    public void playPauseBtnClicked() {
        if (musicService != null){
            if (actionPlaying != null){
                actionPlaying.playPauseBtnClicked();
            }
            else if (getBaseContext() != null){
                musicPlayerActivity.playPauseBtnClicked();
            }
        }
    }

    public void prevBtnClicked() {
        if (musicService != null){
            if (actionPlaying != null){
                actionPlaying.prevBtnClicked();
            }
            else if (getBaseContext() != null){
                musicPlayerActivity.prevBtnClicked();
            }
        }
    }


}

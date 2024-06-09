package com.infinite.virtualmusicplayer.activities;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.infinite.virtualmusicplayer.R;

public class NowPlayingWidget extends AppWidgetProvider {

    ImageView cover;

//    public static boolean SHOW_MINI_PLAYER = false;
//    public static String PATH_TO_FRAG = null;
//    public static String ARTIST_TO_FRAG = null;
//    public static String SONG_NAME_TO_FRAG = null;
//    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
//    public static final String MUSIC_FILE = "STORED_MUSIC";
//    public static final String ARTIST_NAME = "ARTIST NAME";
//    public static final String SONG_NAME = "SONG NAME";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.nowplaying_widget);


//        SharedPreferences.Editor editor = context.getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE).edit();
//        editor.putString(MUSIC_FILE, MusicService.musicServiceFiles.get(MusicService.currentSongIndex).getPath());
//        editor.putString(ARTIST_NAME, MusicService.musicServiceFiles.get(MusicService.currentSongIndex).getArtist());
//        editor.putString(SONG_NAME, MusicService.musicServiceFiles.get(MusicService.currentSongIndex).getTitle());
//        editor.apply();
//
//        SharedPreferences preferences = context.getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
//        String path = preferences.getString(MUSIC_FILE, null);
//        String artistName = preferences.getString(ARTIST_NAME, null);
//        String song_name = preferences.getString(SONG_NAME, null);
//
//        if (path != null) {
//            SHOW_MINI_PLAYER = true;
//            PATH_TO_FRAG = path;
//            ARTIST_TO_FRAG = artistName;
//            SONG_NAME_TO_FRAG = song_name;
//        } else {
//            SHOW_MINI_PLAYER = false;
//            PATH_TO_FRAG = null;
//            ARTIST_TO_FRAG = null;
//            SONG_NAME_TO_FRAG = null;
//        }
//
//
//        if (SHOW_MINI_PLAYER){
//            if (PATH_TO_FRAG != null){
//                views.setImageViewBitmap(R.id.widgetImageCover, thumb);
//                views.setTextViewText(R.id.widgetTextTitle, SONG_NAME_TO_FRAG);
//                views.setTextViewText(R.id.widgetTextArtist, ARTIST_TO_FRAG);
//
//
//            }
//        }



    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


}

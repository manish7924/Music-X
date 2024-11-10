package com.infinite.virtualmusicplayer.activities;

import static com.infinite.virtualmusicplayer.R.id.album_art;
import static com.infinite.virtualmusicplayer.R.id.artist_name;
import static com.infinite.virtualmusicplayer.R.id.song_title;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.listSongs;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.position;
import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.thumb;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.infinite.virtualmusicplayer.R;

public class NowPlayingWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.now_playing_widget);

        // Set song title and artist
        views.setTextViewText(song_title, listSongs.get(position).getTitle());  // Replace with actual song title
        views.setTextViewText(artist_name, listSongs.get(position).getArtist());  // Replace with actual artist name

        // Set album art
         views.setImageViewBitmap(album_art, thumb);

        // PendingIntent for previous, play/pause, and next buttons
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(context, NowPlayingWidgetProvider.class).setAction("ACTION_PREVIOUS"),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.prev_button, prevPendingIntent);

        PendingIntent playPendingIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(context, NowPlayingWidgetProvider.class).setAction("ACTION_PLAY_PAUSE"),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.play_button, playPendingIntent);

        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(context, NowPlayingWidgetProvider.class).setAction("ACTION_NEXT"),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.next_button, nextPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        // Handle button actions here (e.g., Previous, Play/Pause, Next)
        if ("ACTION_PREVIOUS".equals(action)) {
            // Handle previous action
        } else if ("ACTION_PLAY_PAUSE".equals(action)) {
            // Handle play/pause action
        } else if ("ACTION_NEXT".equals(action)) {
            // Handle next action
        }
    }
}

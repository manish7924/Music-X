// NotificationReceiver.java
package com.infinite.virtualmusicplayer.receivers;

import static com.infinite.virtualmusicplayer.activities.MusicPlayerActivity.musicService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.infinite.virtualmusicplayer.services.MusicService;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && musicService != null) {
            switch (action) {
                case Intent.ACTION_MEDIA_BUTTON:
                    KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        switch (keyEvent.getKeyCode()) {
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                                performAction(context, "playPause");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                performAction(context, "next");
                                break;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                performAction(context, "previous");
                                break;
                        }
                    }
                    break;
                case ApplicationClass.ACTION_PLAY:
                    performAction(context, "playPause");
                    break;
                case ApplicationClass.ACTION_NEXT:
                    performAction(context, "next");
                    break;
                case ApplicationClass.ACTION_PREVIOUS:
                    performAction(context, "previous");
                    break;
            }
        }
    }

    private void performAction(Context context, String actionName) {
        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.putExtra("ActionName", actionName);
        context.startService(serviceIntent);
    }
}
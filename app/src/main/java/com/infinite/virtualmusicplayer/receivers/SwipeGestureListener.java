package com.infinite.virtualmusicplayer.receivers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 10;
    private static final int SWIPE_VELOCITY_THRESHOLD = 10;

    private Context mContext;
    private OnSwipeListener mListener;

    public SwipeGestureListener(Context context, OnSwipeListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe right
                        mListener.onSwipeRight();
                    } else {
                        // Swipe left
                        mListener.onSwipeLeft();
                    }
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public interface OnSwipeListener {
        void onSwipeLeft();
        void onSwipeRight();
    }
}
package com.example.carbonfootprinttracker;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewSwipeListener extends RecyclerView.OnFlingListener {
    private static final String TAG = "SwipeListener";
    private static final int SWIPE_VELOCITY_THRESHOLD = 2000;

    boolean mIsScrollingVertically;

    // change swipe listener depending on whether we are scanning items horizontally or vertically
    public RecyclerViewSwipeListener(boolean vertical) {
        mIsScrollingVertically = vertical;
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        Log.d(TAG,  String.format("velocityX: %s \n velocityY: %s", String.valueOf(velocityX), String.valueOf(velocityY)));
        if (mIsScrollingVertically && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            if (velocityY < 0) {
                onSwipeDown();
            } else {
                onSwipeUp();
            }
            return true;
        } else if (!mIsScrollingVertically && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (velocityX < 0) {
                onSwipeLeft();
            } else {
                onSwipeRight();
            }
            return true;
        }
        return false;
    }


    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeUp() {
    }

    public void onSwipeDown() {
    }
}
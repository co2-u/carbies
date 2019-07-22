package com.example.carbonfootprinttracker;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbonfootprinttracker.fragments.CarbiesAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private static final String TAG = "SwipeToDeleteCall";

    private CarbiesAdapter mAdapter;

    public SwipeToDeleteCallback(CarbiesAdapter adapter) {
        super(0, ItemTouchHelper.RIGHT);
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Log.d(TAG, "swiped" + mAdapter.getItemId(position));
    }
}

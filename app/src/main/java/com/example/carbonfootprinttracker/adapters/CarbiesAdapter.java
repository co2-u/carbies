package com.example.carbonfootprinttracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.fragments.AddFavoriteFragment;
import com.example.carbonfootprinttracker.fragments.DetailsFragment;
import com.example.carbonfootprinttracker.fragments.PublicTransDialogFragment;
import com.example.carbonfootprinttracker.fragments.RouteFragment;
import com.example.carbonfootprinttracker.models.Carbie;
import com.example.carbonfootprinttracker.models.TransportationMode;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CarbiesAdapter extends RecyclerView.Adapter<CarbiesAdapter.ViewHolder> {
    private static final String TAG = "CarbiesAdapter";
    private static final Integer MAX_CARBON = 2000;

    private List<Carbie> carbies;
    private Context context;
    private FragmentManager fragmentManager;
    private Carbie mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private Activity mActivity;
    private boolean isDailyLog;

    public CarbiesAdapter (Context context, FragmentManager fragmentManager, List<Carbie> carbies, Activity activity,
                           Boolean isDailyLog) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.carbies = carbies;
        this.mActivity = activity;
        this.isDailyLog = isDailyLog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carbie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Carbie carbie = carbies.get(position);
        final ParseUser author = ParseUser.getCurrentUser();
        final Integer score = carbie.getScore();
        Boolean fav = carbie.getIsFavorited();

        holder.tvTitle.setText(carbie.getTitle());
        holder.tvStartAddress.setText(carbie.getStartLocation());
        holder.tvEndAddress.setText(carbie.getEndLocation());
        holder.tvScore.setText(score.toString());

        if (fav) {
            holder.ivLike.setImageResource(R.drawable.filled_heart);
        } else {
            holder.ivLike.setImageResource(R.drawable.heart_outline);
        }

        if (score > MAX_CARBON * 1.1) {
            holder.ivCircle.setBackground(context.getResources().getDrawable(R.drawable.red_circle));
        } else if (score > MAX_CARBON && score <= MAX_CARBON * 1.1) {
            holder.ivCircle.setBackground(context.getResources().getDrawable(R.drawable.yellow_circle));
        } else {
            holder.ivCircle.setBackground(context.getResources().getDrawable(R.drawable.green_circle));
        }
    }

    @Override
    public int getItemCount() {
        return carbies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCircle) ImageView ivCircle;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvStartAddress) TextView tvStartAddress;
        @BindView(R.id.tvEndAddress) TextView tvEndAddress;
        @BindView(R.id.tvScore) TextView tvScore;
        @BindView(R.id.ivLike) ImageView ivLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Carbie carbie = carbies.get(position);
                        if (isDailyLog) {
                            Log.e(TAG, "isDailyLog");
                            String message = "";
                            String title = carbie.getTitle();
                            if (carbie.getIsFavorited()) {
                                message = " removed from favorites";
                            } else {
                                message = " added to favorites!";
                            }
                            Toast.makeText(mActivity, title + message, Toast.LENGTH_SHORT).show();
                            carbie.setIsFavorited(!(carbie.getIsFavorited()));
                            notifyItemChanged(getAdapterPosition());

                            carbie.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.d(TAG, "Error while saving");
                                        e.printStackTrace();
                                        return;
                                    }
                                    Log.d(TAG, "Success!");
                                }
                            });
                        } else {
                            Log.e(TAG, "isFavorites");
                            if (carbie.getIsFavorited()) {
                                unfavoriteItem(position);
                                carbie.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Log.d(TAG, "Error while saving");
                                            e.printStackTrace();
                                            return;
                                        }
                                        Log.d(TAG, "Success!");
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = carbies.get(position);
        mRecentlyDeletedItemPosition = position;
        mRecentlyDeletedItem.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                carbies.remove(position);
                notifyItemRemoved(position);
                Log.d(TAG, "Successfully deleted item " + mRecentlyDeletedItem.getObjectId());
                Carbie carbie = new Carbie();
                showUndoSnackbar(true, carbie);
            }
        });

    }

    public void unfavoriteItem(int position) {
        Carbie carbie = carbies.get(position);
        carbie.setIsFavorited(false);
        carbies.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar(false, carbie);
        Log.d(TAG, "Successfully unfavorited item");
    }

    private void showUndoSnackbar(boolean isDeleting, Carbie carbie) {
        View view = mActivity.findViewById(R.id.rvCarbies);
        if (isDeleting) {
            Snackbar snackbar = Snackbar.make(view, "Deleted 1 carbie", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", v -> undoDelete());
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(view, "Unfavorited one carbie", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", v -> undoUnfavorite(carbie));
            snackbar.show();
        }
    }

    private void undoUnfavorite(Carbie carbie) {
        carbie.setIsFavorited(true);
        carbies.add(carbie);
        notifyItemInserted(carbies.indexOf(carbie));
        carbie.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error while saving");
                    e.printStackTrace();
                    return;
                }
                Log.d(TAG, "Success!");
            }
        });
    }

    private void undoDelete() {
        //must make copy of recently deleted item before saving it back to Parse
        Carbie copied = mRecentlyDeletedItem.copy();
        copied.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    carbies.add(mRecentlyDeletedItemPosition, copied);
                    notifyItemInserted(mRecentlyDeletedItemPosition);
                    Log.d(TAG, "Successfully undid deleting of item " + mRecentlyDeletedItem.getObjectId());
                } else {
                    Log.d(TAG, "Failed to undo deleting of item " + mRecentlyDeletedItem.getObjectId());
                    e.printStackTrace();
                }
            }
        });
    }
}

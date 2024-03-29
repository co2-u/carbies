package com.example.carbonfootprinttracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.fragments.DetailsFragment;
import com.example.carbonfootprinttracker.models.Carbie;
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

    public CarbiesAdapter(Context context, FragmentManager fragmentManager, List<Carbie> carbies, Activity activity,
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
        @BindView(R.id.ivCircle)
        ImageView ivCircle;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvStartAddress)
        TextView tvStartAddress;
        @BindView(R.id.tvEndAddress)
        TextView tvEndAddress;
        @BindView(R.id.tvScore)
        TextView tvScore;
        @BindView(R.id.ivLogMore)
        ImageView ivLogMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (isDailyLog) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
                        Fragment fragment = new DetailsFragment();
                        Bundle args = new Bundle();
                        args.putParcelable("carbie", carbies.get(getAdapterPosition()));
                        args.putInt("itemPosition", getAdapterPosition());
                        fragment.setArguments(args);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentPlaceholder, fragment).addToBackStack(null).commit();
                    }
                });
            }

            ivLogMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDailyLog) {
                        PopupMenu popup = new PopupMenu(context, ivLogMore);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater()
                                .inflate(R.menu.daily_log_popup, popup.getMenu());
                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getTitle().toString()) {
                                    case "Add to Frequents":
                                        Toast.makeText(context, "Added to Frequents", Toast.LENGTH_SHORT).show();
                                        carbies.get(getAdapterPosition()).setIsFavorited(true);
                                        carbies.get(getAdapterPosition()).saveInBackground(new SaveCallback() {
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
                                        notifyDataSetChanged();

                                        break;
                                    case "Share":
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_TEXT, "My CO2&U score is " + MainActivity.score);
                                        sendIntent.setType("text/plain");
                                        mActivity.startActivity(sendIntent);
                                        break;
                                    case "Delete":
                                        deleteItem(getAdapterPosition());
                                        break;
                                }
                                return true;
                            }
                        });
                        popup.show(); //showing popup menu
                    } else {
                        PopupMenu popup = new PopupMenu(context, ivLogMore);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater()
                                .inflate(R.menu.frequents_popup, popup.getMenu());
                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getTitle().toString()) {
                                    case "Add to Daily Log":
                                        Carbie carbie = new Carbie();
                                        try {
                                            carbie = carbies.get(getAdapterPosition());
                                        } catch (NullPointerException e) {
                                            Log.e(TAG, "Carbie was not passed into dialogFragment");
                                            e.printStackTrace();
                                        }
                                        Carbie newCarbie = carbie.copy();
                                        newCarbie.setIsFavorited(false);
                                        newCarbie.setIsDeleted(false);
                                        newCarbie.saveInBackground(new SaveCallback() {
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
                                        //TODO maybe go to daily log

                                        break;
                                    case "Share":
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_TEXT, "My CO2&U score is " + MainActivity.score);
                                        sendIntent.setType("text/plain");
                                        mActivity.startActivity(sendIntent);
                                        break;
                                    case "Delete":
                                        deleteItem(getAdapterPosition());
                                        break;
                                }
                                return true;
                            }
                        });
                        popup.show(); //showing popup menu
                    }
                }

            });
        }
    }

        public void deleteItem(int position) {
            if (carbies.get(position).getIsFavorited() && isDailyLog) {
                carbies.get(position).setIsDeleted(true);
                carbies.get(position).saveInBackground(new SaveCallback() {
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
                mRecentlyDeletedItem = carbies.get(position);
                mRecentlyDeletedItemPosition = position;
                carbies.remove(position);
                notifyItemRemoved(position);
                showUndoSnackbar(mRecentlyDeletedItem);
            } else {
                mRecentlyDeletedItem = carbies.get(position);
                mRecentlyDeletedItemPosition = position;
                mRecentlyDeletedItem.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        carbies.remove(position);
                        notifyItemRemoved(position);
                        Log.d(TAG, "Successfully deleted item " + mRecentlyDeletedItem.getObjectId());
                        showUndoSnackbar(mRecentlyDeletedItem);
                    }
                });
            }
        }

        private void showUndoSnackbar(Carbie carbie) {
            View view = mActivity.findViewById(R.id.rvCarbies);
            Snackbar snackbar = Snackbar.make(view, "Deleted 1 carbie", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", v -> undoDelete(carbie));
            snackbar.show();

        }

        private void undoDelete(Carbie carbie) {
            if (carbie.getIsFavorited() && isDailyLog) {
                carbie.setIsDeleted(false);
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
                notifyDataSetChanged();
            } else {
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
}

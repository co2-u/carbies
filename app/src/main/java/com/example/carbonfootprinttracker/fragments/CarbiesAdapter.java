package com.example.carbonfootprinttracker.fragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CarbiesAdapter extends RecyclerView.Adapter<CarbiesAdapter.ViewHolder> {
    private static final String TAG = "CarbiesAdapter";
    private static final Integer MAX_CARBON = 8000;

    private List<Carbie> carbies;
    private Context context;
    private FragmentManager fragmentManager;
    private Carbie mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;

    public CarbiesAdapter (Context context, FragmentManager fragmentManager, List<Carbie> carbies) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.carbies = carbies;
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCircle) ImageView ivCircle;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvStartAddress) TextView tvStartAddress;
        @BindView(R.id.tvEndAddress) TextView tvEndAddress;
        @BindView(R.id.tvScore) TextView tvScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
            }
        });
    }
}

package com.example.carbonfootprinttracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CommunityCarbiesAdapter extends RecyclerView.Adapter<CommunityCarbiesAdapter.ViewHolder> {
    private static final String TAG = "CommunityCarbieAdapter";
    private static final Integer MAX_CARBON = 2000;

    private List<Carbie> carbies;
    private Context context;

    public CommunityCarbiesAdapter(Context context, List<Carbie> carbies) {
        this.context = context;
        this.carbies = carbies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community_carbie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Carbie carbie = carbies.get(position);
        final String username = carbie.getUser().getUsername();
        final Integer score = carbie.getScore();
        final String title = carbie.getTitle();

        holder.tvTitle.setText(title);
        holder.tvScore.setText(score.toString());
        holder.tvUsername.setText(username);

        if (score > MAX_CARBON * 1.1) {
            holder.tvScore.setTextColor(context.getResources().getColor(R.color.colorRed));
        } else if (score > MAX_CARBON && score <= MAX_CARBON * 1.1) {
            holder.tvScore.setTextColor(context.getResources().getColor(R.color.colorYellow));
        } else {
            holder.tvScore.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
    }

    @Override
    public int getItemCount() {
        return carbies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvScore) TextView tvScore;
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

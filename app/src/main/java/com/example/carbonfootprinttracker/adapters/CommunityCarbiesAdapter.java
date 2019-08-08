package com.example.carbonfootprinttracker.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.fragments.ProfileFragment;
import com.example.carbonfootprinttracker.models.Carbie;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CommunityCarbiesAdapter extends RecyclerView.Adapter<CommunityCarbiesAdapter.ViewHolder> {
    private static final String TAG = "CommunityCarbieAdapter";
    private static final Integer MAX_CARBON = 2000;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private List<Carbie> carbies;
    private Context context;
    private FragmentManager fragmentManager;

    public CommunityCarbiesAdapter(Context context, List<Carbie> carbies, FragmentManager fragmentManager) {
        this.carbies = carbies;
        this.context = context;
        this.fragmentManager = fragmentManager;
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
        final ParseUser user = carbie.getUser();
        final String username = user.getUsername();
        final Integer score = carbie.getScore();
        final String title = carbie.getTitle();
        final Date date = carbie.getCreatedAt();

        holder.tvTitle.setText(title);
        holder.tvScore.setText(score.toString());
        holder.tvUsername.setText(username);
        holder.tvDate.setText(getRelativeTimeAgo(date.toString()));

        ParseFile photoFile = user.getParseFile("profileImage");
        if (photoFile != null) {
            String preUrl = photoFile.getUrl();
            String completeURL = preUrl.substring(0, 4) + "s" + preUrl.substring(4, preUrl.length());
            Glide.with(context)
                    .load(completeURL)
                    .into(holder.ivProfileImage);
        } else {
            holder.ivProfileImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_account_circle));
        }

        String transport = "";
        switch (carbie.getTransportation()) {
            case "SmallCar":
                transport = " drove ";
                break;
            case "MediumCar":
                transport = " drove ";
                break;
            case "LargeCar":
                transport = " drove ";
                break;
            case "Hybrid":
                transport = " drove ";
                break;
            case "Electric":
                transport = " drove ";
                break;
            case "Bus":
                transport = " took a bus for ";
                break;
            case "Rail":
                transport = " went by rail for ";
                break;
            case "Bike":
                transport = " biked ";
                break;
            case "Walk":
                transport = " walked ";
                break;
            case "Rideshare":
                transport = " carpooled ";
                break;
        }

        holder.tvMoved.setText(transport);
        holder.tvDistance.setText(df.format(carbie.getDistance()) + " mi.");

        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(carbies.get(position).getUser());
            }
        });

        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile(carbies.get(position).getUser());
            }
        });

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
        @BindView(R.id.tvDailyTitle) TextView tvDate;
        @BindView(R.id.tvMoved) TextView tvMoved;
        @BindView(R.id.tvDistance) TextView tvDistance;
        @BindView(R.id.ivCircleProfile) ImageView ivProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private String getRelativeTimeAgo(String rawJsonDate) {
        String instagramFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(instagramFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    private void goToProfile(ParseUser user) {
        Fragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        profileFragment.setArguments(args);
        fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, profileFragment).addToBackStack("CommunityFragment").commit();
    }
}

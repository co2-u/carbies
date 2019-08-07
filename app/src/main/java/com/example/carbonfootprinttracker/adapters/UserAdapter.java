package com.example.carbonfootprinttracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbonfootprinttracker.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";

    private List<ParseUser> users;
    private Context context;

    public UserAdapter (Context context, List<ParseUser> users) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        ParseUser user = users.get(position);

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

        holder.tvUsername.setText(user.getUsername());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivCircleProfile)
        ImageView ivProfileImage;
        @BindView(R.id.tvUsername)
        TextView tvUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

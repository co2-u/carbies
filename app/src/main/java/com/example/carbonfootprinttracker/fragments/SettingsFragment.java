package com.example.carbonfootprinttracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.LoginActivity;
import com.example.carbonfootprinttracker.R;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    @BindView(R.id.btLogout) public Button btLogout;
    @BindView(R.id.btChangeUsername) public Button btChangeUsername;
    @BindView(R.id.btChangeEmail) public Button btChangeEmail;
    @BindView(R.id.btChangePassword) public Button btChangePassword;
    @BindView(R.id.tvUsername) public TextView tvUsername;
    @BindView(R.id.ivProfileImage) public ImageView ivProfileImage;

    private FragmentManager fragmentManager;
    private ParseUser user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        fragmentManager = getFragmentManager();
        user = ParseUser.getCurrentUser();

        tvUsername.setText(user.getUsername());

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Log.d(TAG, "Logged out successfully");
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //TODO - implement change username
            }
        });

        btChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - implement change email
            }
        });

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - implement change passowrd
            }
        });


//    public void sendNotification(View view) {
//        createNotificationChannel();
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "channel1")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("title")
//                .setContentText("content");
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
//        notificationManager.notify(50, builder.build());
//    }
//
//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //CharSequence name = getString(R.string.channel_name);
//            //String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("channel1","CO2nU", importance);
//            channel.setDescription("carbiessss");
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
    }

}

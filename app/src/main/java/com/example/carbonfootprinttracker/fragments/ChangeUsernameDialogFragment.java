package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.carbonfootprinttracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeUsernameDialogFragment extends AppCompatDialogFragment {
    private static final String TAG = "ChangeUsernameDialog";

    @BindView(R.id.etNewUsername) EditText etNewUsername;
    @BindView(R.id.btAccept) Button btAccept;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_username_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = etNewUsername.getText().toString();
                if (newUsername.isEmpty()) {
                    Toast.makeText(getContext(), "Missing new username", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO - change username
                }
            }
        });
    }
}

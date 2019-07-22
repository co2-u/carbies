package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublicTransDialogFragment extends AppCompatDialogFragment {

    private static final String TAG = "PTransportDialFragment";

    private Carbie carbie;

    @BindView(R.id.btnRail) Button btnRail;
    @BindView(R.id.btnBus) Button btnBus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publictrans_dialog, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        try {
            carbie = getArguments().getParcelable("carbie");
        } catch (NullPointerException e) {
            Log.e(TAG, "Carbie was not passed into dialogFragment");
            e.printStackTrace();
        }
        btnRail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                carbie.setTransportation("Rail");
                goRoute();
                //// Close the dialog and return back to the parent activity
                dismiss();
            }
        });

        btnBus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                carbie.setTransportation("Bus");
                goRoute();
                //// Close the dialog and return back to the parent activity
                dismiss();
            }
        });
    }

    private void goRoute() {
        Fragment fragment = new RouteFragment();
        Bundle args = new Bundle();
        args.putParcelable("carbie", carbie);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, fragment)
                .commit();
    }
}

package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class ElectricCarDialogFragment extends AppCompatDialogFragment {

    @BindView(R.id.btnHybrid) Button btnHybrid;
    @BindView(R.id.btnFullE) Button btnFullE;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_electric_car_dialog, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        btnHybrid.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goRoute();
                //// Close the dialog and return back to the parent activity
                dismiss();
            }
        });

        btnFullE.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goRoute();
                //// Close the dialog and return back to the parent activity
                dismiss();
            }
        });
    }

    private void goRoute() {
        Fragment fragment = new RouteFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, fragment)
                .commit();
    }
}

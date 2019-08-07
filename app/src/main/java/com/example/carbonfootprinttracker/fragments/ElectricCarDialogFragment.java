package com.example.carbonfootprinttracker.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ElectricCarDialogFragment extends AppCompatDialogFragment {

    private static final String TAG = "ECarDialogFragment";

    private Carbie carbie;
    @BindView(R.id.btnHybrid) Button btnHybrid;
    @BindView(R.id.btnElectric) Button btnElectric;
    @BindView(R.id.tvFuelSource)
    TextView tvEnergySource;
    @BindView(R.id.btnFossilFuel)
    Button btnFossilFuel;
    @BindView(R.id.btnRenewable)
    Button btnRenewable;

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
        tvEnergySource.setVisibility(View.GONE);
        btnFossilFuel.setVisibility(View.GONE);
        btnRenewable.setVisibility(View.GONE);

        try {
            carbie = getArguments().getParcelable("carbie");
        } catch (NullPointerException e) {
            Log.e(TAG, "Carbie was not passed into dialogFragment");
            e.printStackTrace();
        }

        btnHybrid.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                carbie.setTransportation("Hybrid");
                goLiveRoute();
                //// Close the dialog and return back to the parent activity
                dismiss();
            }
        });

        btnElectric.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tvEnergySource.setVisibility(View.VISIBLE);
                btnFossilFuel.setVisibility(View.VISIBLE);
                btnRenewable.setVisibility(View.VISIBLE);
                btnElectric.setBackground(getContext().getResources().getDrawable(R.drawable.grey_clicked_button));
            }
        });

        btnFossilFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carbie.setTransportation("FossilFuel");
                goLiveRoute();
                //// Close the dialog and return back to the parent activity
                dismiss();
            }
        });

        btnRenewable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carbie.setTransportation("Renewable");
                goLiveRoute();
                //// Close the dialog and return back to the parent activity
                dismiss();
            }
        });
    }

    private void goLiveRoute() {
        Fragment fragment = new LiveRouteFragment();
        Bundle args = new Bundle();
        args.putParcelable("carbie", carbie);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, fragment)
                .addToBackStack("ComposeFragment")
                .commit();
    }
}

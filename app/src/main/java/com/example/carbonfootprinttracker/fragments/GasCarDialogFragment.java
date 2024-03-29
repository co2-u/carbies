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

public class GasCarDialogFragment extends AppCompatDialogFragment {

    private static final String TAG = "GasCarDialogFragment";

    private Carbie carbie;
    @BindView(R.id.btnSmallGas)
    Button btnSmallGas;
    @BindView(R.id.btnMediumGas) Button btnMediumGas;
    @BindView(R.id.btnLargeGas) Button btnLargeGas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type_of_gas_car, container, false);

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
        btnSmallGas.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v) {
                carbie.setTransportation("SmallCar");
                goLiveRoute();
                //// Close the dialog and return back to the parent activity
                dismiss();
            }
        });

        btnMediumGas.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                carbie.setTransportation("MediumCar");
                goLiveRoute();
                //// Close the dialog and return back to the parent activity
                dismiss();
            }
        });

        btnLargeGas.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                carbie.setTransportation("LargeCar");
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



package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.example.carbonfootprinttracker.models.TransportationMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeFragment extends Fragment {



    @BindView(R.id.btnBike) Button btnBike;
    @BindView(R.id.btnElectricCar) Button btnElectricCar;
    @BindView(R.id.btnGasCar) Button btnGasCar;
    @BindView(R.id.btnPublicTransportation) Button btnPublicTransportation;
    @BindView(R.id.btnWalk) Button btnWalk;
    // @BindView(R.id.btnBack) Button btnBack;

    //this is the button that will have the dialog
    @BindView(R.id.btnCarpool) Button btnCarpool;

    private FragmentManager fm;


    @Nullable
    @Override
    //this is creating the view
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode_of_transportation, container, false);
        return view;
    }

    @Override
    // after the view is created, this thing happens where you link up the buttons to go to the route_view
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        fm = getFragmentManager();

        btnBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRoute(TransportationMode.Bike);
            }
        });

        btnCarpool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCarpoolDialog();
            }
        });

        btnElectricCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRoute(TransportationMode.eCar);
            }
        });

        btnGasCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRoute(TransportationMode.Car);
            }
        });

        btnPublicTransportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRoute(TransportationMode.PublicTransportation);
            }
        });

        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goRoute(TransportationMode.Walk);
            }
        });

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ComposeFragment.this, CurrentScoreFragment.this));
//            }
//        });
    }

    //this is a helper method -- it tells the buttons to go to the RouteFragment
    //once you are in the RouteFragment, you inflate the view and the route xml file shows up
    private void goRoute(TransportationMode mode) {
        Fragment fragment = new RouteFragment();

        Bundle args = new Bundle();
        Carbie carbie = new Carbie();
        carbie.setTransportation(mode.toString());
        args.putParcelable("carbie", carbie);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, fragment)
                .commit();
    }

    public void showCarpoolDialog(){
        RideshareDialogFragment RideShareDialog = new RideshareDialogFragment();
        RideShareDialog.show(fm, "compose_fragment");


    }
}

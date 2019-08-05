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
                goLiveRoute(TransportationMode.Bike);
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
                showElectricCarDialog();
            }
        });

        btnGasCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGasCarDialog();
            }
        });

        btnPublicTransportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPublicTransDialog();
            }
        });

        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLiveRoute(TransportationMode.Walk);
            }
        });
    }

    //this is a helper method -- it tells the buttons to go to the RouteFragment
    //once you are in the RouteFragment, you inflate the view and the route xml file shows up
    private void goLiveRoute(TransportationMode mode) {
        Fragment fragment = new LiveRouteFragment();

        Bundle args = new Bundle();
        Carbie carbie = new Carbie();
        carbie.setTransportation(mode.toString());
        args.putParcelable("carbie", carbie);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentPlaceholder, fragment)
                .addToBackStack("ComposeFragment")
                .commit();
    }

    public void showCarpoolDialog(){
        RideshareDialogFragment RideShareDialog = new RideshareDialogFragment();

        Bundle args = new Bundle();
        Carbie carbie = new Carbie();
        carbie.setTransportation(TransportationMode.Rideshare.toString());
        args.putParcelable("carbie", carbie);
        RideShareDialog.setArguments(args);

        RideShareDialog.show(fm, "compose_fragment");
    }

    public void showElectricCarDialog(){
        ElectricCarDialogFragment ElectricCarDialog = new ElectricCarDialogFragment();
        Bundle args = new Bundle();
        Carbie carbie = new Carbie();
        carbie.setTransportation(TransportationMode.eCar.toString());
        args.putParcelable("carbie", carbie);
        ElectricCarDialog.setArguments(args);

        ElectricCarDialog.show(fm, "compose_fragment");
    }

    public void showGasCarDialog(){
        GasCarDialogFragment GasCarDialog = new GasCarDialogFragment();
        Bundle args = new Bundle();
        Carbie carbie = new Carbie();
        carbie.setTransportation(TransportationMode.eCar.toString());
        args.putParcelable("carbie", carbie);
        GasCarDialog.setArguments(args);

        GasCarDialog.show(fm, "compose_fragment");
    }

    public void showPublicTransDialog(){
        PublicTransDialogFragment PublicTransDialog = new PublicTransDialogFragment();
        Bundle args = new Bundle();
        Carbie carbie = new Carbie();
        carbie.setTransportation(TransportationMode.eCar.toString());
        args.putParcelable("carbie", carbie);
        PublicTransDialog.setArguments(args);

        PublicTransDialog.show(fm, "compose_fragment");
    }
}

package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveRouteFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "LiveRouteFragment";

    private GoogleMap mGoogleMap;
    private GeoApiContext mGeoApiContext = null;
    private FragmentManager fragmentManager;

    @BindView(R.id.btStart) Button btStart;
    @BindView(R.id.btStop) Button btStop;
    @BindView(R.id.mapView2) MapView mapView;
    @BindView(R.id.progressBar2) ProgressBar pbLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        fragmentManager = getFragmentManager();

        mapView.onCreate(savedInstanceState);
        showProgressBar();
        mapView.getMapAsync(this);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        hideProgressBar();
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void showProgressBar() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressBar() {
        pbLoading.setVisibility(ProgressBar.INVISIBLE);
    }
}

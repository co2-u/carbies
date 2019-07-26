package com.example.carbonfootprinttracker.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.Manifest;
import com.example.carbonfootprinttracker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class LiveRouteFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "LiveRouteFragment";
    private final static String KEY_LOCATION = "location";
    private static final long UPDATE_INTERVAL = 2 * 1000;  /* 2 secs */
    private static final long FASTEST_INTERVAL = 1000; /* 1 sec */

    private GoogleMap mGoogleMap;
    private GeoApiContext mGeoApiContext = null;
    private FragmentManager fragmentManager;
    private Context context;
    private Location mCurrentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private List<Location> mLocations;
    private boolean isTracking = false;

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
        context = getContext();
        mLocations = new ArrayList<>();

        fusedLocationProviderClient = getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        
        mapView.onCreate(savedInstanceState);
        showProgressBar();
        mapView.getMapAsync(this);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTracking) {
                    isTracking = true;
                    if (mCurrentLocation != null) {
                        mLocations.add(mCurrentLocation);
                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
                    } else {
                        Log.d(TAG, "mCurrentLocation is null");
                    }
                } else {
                    Toast.makeText(context, "Route in progress", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTracking = false;
                float distanceInMeters = getTotalDistance();
                Toast.makeText(context, "Total distance: " + distanceInMeters, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        hideProgressBar();
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        LiveRouteFragmentPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
        LiveRouteFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void getMyLocation() {
        mGoogleMap.setMyLocationEnabled(true);

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            updateCurrentLocation(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission({android.Manifest.permission.ACCESS_FINE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        Log.d(TAG, "start location updates");
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stop location updates");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }

        // only add to mLocations if isTracking is true
        // but should I always updateCurrentLocation

//        if (mLocations.size() == 0) {
//            mLocations.add(location);
//            updateCurrentLocation(location);
//            Log.d(TAG, mLocations.toString());
//        } else {
//            final float distanceInMeters = mCurrentLocation.distanceTo(location);
//            if (distanceInMeters >= 1) {
//                mLocations.add(location);
//                updateCurrentLocation(location);
//                Log.d(TAG, mLocations.toString());
//                Log.d(TAG, "Distance: " + distanceInMeters);
//            }
//        }

        final float distanceInMeters = mCurrentLocation.distanceTo(location);
        if (distanceInMeters > 1) {
            Log.d(TAG, "Distance: " + distanceInMeters);
            if (isTracking) {
                mLocations.add(location);
                drawPolyline(mCurrentLocation, location);
                Log.d(TAG, mLocations.toString());
            }
            updateCurrentLocation(location);
        }
    }

    private void updateCurrentLocation (Location location) {
        mCurrentLocation = location;
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void drawPolyline(Location from, Location to) {
        mGoogleMap.addPolyline(
                new PolylineOptions()
                        .add(new LatLng(from.getLatitude(), from.getLongitude()))
                        .add(new LatLng(to.getLatitude(), to.getLongitude()))
        );
    }

    private float getTotalDistance() {
        float distance = 0;
        for (int i = 0; i < mLocations.size() - 1; i++) {
            Location from = mLocations.get(i);
            Location to = mLocations.get(i+1);
            distance += from.distanceTo(to);
        }
        return distance;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LiveRouteFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // Annotate a method which is invoked if the user doesn't grant the permissions
    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForLocation() {
        Toast.makeText(context, "Location access was denied.", Toast.LENGTH_SHORT).show();
    }

    // Annotates a method which is invoked if the user
    // chose to have the device "never ask again" about a permission
    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForLocation() {
        Toast.makeText(context, "Location access was denied and can't ask.", Toast.LENGTH_SHORT).show();
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
        stopLocationUpdates();
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

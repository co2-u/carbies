package com.example.carbonfootprinttracker.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.Manifest;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.GeoApiContext;

import java.io.ByteArrayOutputStream;
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
    private Carbie carbie;

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

        try {
            carbie = getArguments().getParcelable("carbie");
        } catch (NullPointerException e) {
            Log.e(TAG, "Carbie was not passed into LiveRouteFragment");
            e.printStackTrace();
        }

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
                    Toast.makeText(context, "Tracking Route in Progress", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocations.size() == 0) {
                    Toast.makeText(context, "You haven't started a route!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressBar();
                mLocations.add(mCurrentLocation);
                mGoogleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        ));
                isTracking = false;

                carbie.setDistance(toMiles(getTotalDistance()));
                carbie.setStartLocation("Live Start");
                carbie.setEndLocation("Live End");

                final Fragment confirmationFragment = new ConfirmationFragment();
                final Bundle args = new Bundle();
                args.putParcelable("carbie", carbie);

                prepMapForSnapshot();
                // Called after map has loaded the camera update
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mGoogleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(Bitmap bitmap) {
                                hideProgressBar();
                                Log.d(TAG, "Snapshot taken");
                                // Convert bitmap to byte[] to put into args bundle
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();

                                args.putByteArray("snapshot", byteArray);
                                confirmationFragment.setArguments(args);
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragmentPlaceholder, confirmationFragment)
                                        .addToBackStack("LiveRouteFragment")
                                        .commit();
                            }
                        });

                    }
                });
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
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
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
        // Only add locations that are at least 1 meter away from mCurrentLocation
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

    private void prepMapForSnapshot() {
        final LatLngBounds routeBounds = findBounds();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(routeBounds, 120), 600, null);
    }

    private float getTotalDistance() {
        float distance = 0;
        if (mLocations.size() > 1) {
            for (int i = 0; i < mLocations.size() - 1; i++) {
                Location from = mLocations.get(i);
                Location to = mLocations.get(i + 1);
                distance += from.distanceTo(to);
            }
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

//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.findViewById(R.id.ivGreentfoot).setVisibility(ImageView.GONE);
        mainActivity.findViewById(R.id.settingsTab).setVisibility(View.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mapView.onDestroy();
        stopLocationUpdates();
    }

    private void showProgressBar() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressBar() {
        pbLoading.setVisibility(ProgressBar.INVISIBLE);
    }

    private double toMiles(float meters) {
        return (meters * 0.00062137);
    }

    private LatLngBounds findBounds() {
        Location preNE = mLocations.get(0);
        Location preSW = mLocations.get(0);
        for (Location location: mLocations) {
            if (location.getLatitude() > preNE.getLatitude()) {
                preNE = location;
            } else if (location.getLatitude() < preSW.getLatitude()) {
                preSW = location;
            }

        }
        LatLng northeast = new LatLng(preNE.getLatitude(), preNE.getLongitude());
        LatLng southwest = new LatLng(preSW.getLatitude(), preSW.getLongitude());
        return new LatLngBounds(southwest, northeast);
    }
}

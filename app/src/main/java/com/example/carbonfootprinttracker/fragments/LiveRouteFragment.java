package com.example.carbonfootprinttracker.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.MainActivity;
import com.example.carbonfootprinttracker.Manifest;
import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final int ZOOM = 17;

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
    private AutocompleteSupportFragment autocompleteFragment;
    private TravelMode travelMode;
    private Marker endMarker;
    private Location endLocation;

    @BindView(R.id.btStart) Button btStart;
    @BindView(R.id.btStop) Button btStop;
    @BindView(R.id.mapView2) MapView mapView;
    @BindView(R.id.progressBar2) ProgressBar pbLoading;
    @BindView(R.id.chronometer) Chronometer chronometer;
    TextView tvEnterData;

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
        endLocation = new Location("");

        try {
            carbie = getArguments().getParcelable("carbie");
            setTravelMode();
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(carbie.getTransportation());
            String transportation = carbie.getTransportation();
            String title = "";
            if (transportation.equals("FossilFuel") || transportation.equals("Renewable")) {
                title = "Full Electric";
            } else if (transportation.equals("SmallCar") || transportation.equals("MediumCar") || transportation.equals("LargeCar")) {
                title = "Gasoline Car";
            } else {
                title = transportation;
            }
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);
        } catch (NullPointerException e) {
            Log.e(TAG, "Carbie was not passed into LiveRouteFragment");
            e.printStackTrace();
        }

        fusedLocationProviderClient = getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult != null) {
                    onLocationChanged(locationResult.getLastLocation());
                }
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

        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d(TAG, "Place: " + place.getName() + ", " + place.getId());
                String start = "" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
                String destination = "place_id:" + place.getId();
                calculateDirections(start, destination);
            }
            @Override
            public void onError(Status status) {
                Toast.makeText(context, "Error while selecting place.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "An error occurred: " + status);
            }
        });

        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mGoogleMap != null) { mGoogleMap.clear(); }
                        autocompleteFragment.setText("");
                    }
                });
        autocompleteFragment.getView().setBackground(context.getResources().getDrawable(R.drawable.background_button_rectangle));

        // Move location button below search bar
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.setMargins(0, 180, 0, 0);

        tvEnterData = ((MainActivity) getActivity()).findViewById(R.id.tvEnterData);
        tvEnterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment routeFragment = new RouteFragment();
                Bundle args = new Bundle();
                args.putParcelable("carbie", carbie);
                routeFragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentPlaceholder, routeFragment)
                        .addToBackStack("LiveRouteFragment")
                        .commit();
            }
        });

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTracking) {
                    isTracking = true;
                    if (mCurrentLocation != null) {
                        autocompleteFragment.getView().setVisibility(View.GONE); // Hide searchbar
                        rlp.setMargins(0, 20, 0, 0); // Move location button to top right
                        chronometer.setVisibility(View.VISIBLE);
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();
                        mLocations.add(mCurrentLocation);
                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), ZOOM));
                        if (endMarker != null) {
                            endLocation.setLatitude(endMarker.getPosition().latitude);
                            endLocation.setLongitude(endMarker.getPosition().longitude);
                        }
                        btStart.setVisibility(View.INVISIBLE);
                        btStop.setVisibility(View.VISIBLE);
                        checkHasArrived();
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
                if (endMarker != null) {
                    endMarker.remove();
                }

                showProgressBar();
                chronometer.stop();
                long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;

                mLocations.add(mCurrentLocation);
                mGoogleMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        ));
                isTracking = false;
                stopLocationUpdates();

                carbie.setDistance(toMiles(getTotalDistance()));
                carbie.setStartLocation("Live Start");
                carbie.setEndLocation("Live End");
                carbie.setTripLength(elapsedSeconds);

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
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM));
                            //Prefer place search results that are nearby
                            LatLngBounds latLngBounds = new LatLngBounds.Builder().include(new LatLng(location.getLatitude(), location.getLongitude())).build();
                            autocompleteFragment.setLocationBias(RectangularBounds.newInstance(latLngBounds));
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

    @SuppressLint("MissingPermission")
    private void stopLocationUpdates() {
        Log.d(TAG, "stop location updates");
        mGoogleMap.setMyLocationEnabled(false);
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
        // Only update location if >3 meters away from current location.
        final float distanceInMeters = mCurrentLocation.distanceTo(location);
        if (distanceInMeters > 3) {
            Log.d(TAG, "Distance: " + distanceInMeters);
            if (isTracking) {
                mLocations.add(location);
                drawPolyline(mCurrentLocation, location);
            }
            updateCurrentLocation(location);
        }

        if (isTracking) {
            checkHasArrived();
        }

    }

    private void checkHasArrived() {
        // Check if within 40 meters of destination.
        final float distanceToDestination = mCurrentLocation.distanceTo(endLocation);
        if (distanceToDestination < 40) {
            Log.d(TAG, "auto click stop");
            btStop.performClick();
        }
        Log.d(TAG, "Distance to Destination: " + distanceToDestination);
    }

    private void updateCurrentLocation (Location location) {
        mCurrentLocation = location;
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Log.d(TAG, msg);
    }

    private void drawPolyline(Location from, Location to) {
        mGoogleMap.addPolyline(
                new PolylineOptions()
                        .add(new LatLng(from.getLatitude(), from.getLongitude()))
                        .add(new LatLng(to.getLatitude(), to.getLongitude()))
        );
    }

    @SuppressLint("MissingPermission")
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

    private void showProgressBar() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressBar() {
        pbLoading.setVisibility(ProgressBar.INVISIBLE);
    }

    private double toMiles(float meters) {
        return (meters * 0.00062137);
    }

    private void setTravelMode() {
        // Set TravelMode for Directions API request from our TransportationMode
        switch (carbie.getTransportation()) {
            case "SmallCar":
                travelMode = TravelMode.DRIVING;
                break;
            case "MediumCar":
                travelMode = TravelMode.DRIVING;
                break;
            case "LargeCar":
                travelMode = TravelMode.DRIVING;
                break;
            case "Bike":
                travelMode = TravelMode.BICYCLING;
                break;
            case "Hybrid":
                travelMode = TravelMode.DRIVING;
                break;
            case "FossilFuel":
                travelMode = TravelMode.DRIVING;
                break;
            case "Renewable":
                travelMode = TravelMode.DRIVING;
                break;
            case "Bus":
                travelMode = TravelMode.TRANSIT;
                break;
            case "Rail":
                travelMode = TravelMode.TRANSIT;
                break;
            case "Walk":
                travelMode = TravelMode.WALKING;
                break;
            case "Rideshare":
                travelMode = TravelMode.DRIVING;
                break;
        }
    }

    private LatLngBounds findBounds() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Location location: mLocations) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        return bounds;
    }

    private void calculateDirections(String startLocation, String endLocation){
        showProgressBar();

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                startLocation
        );

        Log.d(TAG, "calculateDirections: destination: " + endLocation);
        directions.destination(endLocation)
                .mode(travelMode)
                .setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(DirectionsResult result) {
                        hideProgressBar();
                        // Start and End LatLng coordinates and user-friendly addresses
                        final com.google.maps.model.LatLng startLatLng = result.routes[0].legs[0].startLocation;
                        final com.google.maps.model.LatLng endLatLng = result.routes[0].legs[0].endLocation;
                        final String startAddress = result.routes[0].legs[0].startAddress;
                        final String endAddress= result.routes[0].legs[0].endAddress;

                        Log.d(TAG, "startAddress: " + startAddress);
                        Log.d(TAG, "endAddress: " + endAddress);

//                        addMarker(startLatLng.lat, startLatLng.lng, startAddress, BitmapDescriptorFactory.HUE_RED, true);
                        addMarker(endLatLng.lat, endLatLng.lng, endAddress, BitmapDescriptorFactory.HUE_ORANGE);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(endLatLng.lat, endLatLng.lng), ZOOM));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Failed to get directions!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        hideProgressBar();
                        Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );
                    }
                });
    }

    private void addMarker(double lat, double lng, String name, float color) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final LatLng markerLocation = new LatLng(lat,lng);
                endMarker = mGoogleMap.addMarker(
                        new MarkerOptions().position(markerLocation)
                                .title(name)
                                .icon(BitmapDescriptorFactory.defaultMarker(color)));
            }
        });
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
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.findViewById(R.id.bottomNavigation).setVisibility(TextView.GONE);
        mainActivity.findViewById(R.id.tvEnterData).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.findViewById(R.id.tvEnterData).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.findViewById(R.id.bottomNavigation).setVisibility(TextView.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocationUpdates();
    }
}

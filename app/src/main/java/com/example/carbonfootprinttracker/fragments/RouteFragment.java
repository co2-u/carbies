package com.example.carbonfootprinttracker.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Carbie;
import com.example.carbonfootprinttracker.models.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;
import com.google.maps.model.TravelMode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RouteFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {
    private static final String TAG = "RouteFragment";
    private static final Integer ANIMATION_DURATION_MS = 600;
    private static final Integer ROUTE_PADDING = 120;

    private GoogleMap mGoogleMap;
    private GeoApiContext mGeoApiContext = null;
    private ArrayList<Route> mRoutes = new ArrayList<>();
    private Marker startMarker;
    private Marker endMarker;
    private Route selectedRoute;
    private FragmentManager fragmentManager;
    private Carbie carbie;
    private TravelMode travelMode;
    private LatLngBounds routeBounds;


    @BindView(R.id.mapView) MapView mMapView;
    @BindView(R.id.btSeeRoutes) Button btSeeRoutes;
    @BindView(R.id.btAcceptRoute) Button btAcceptRoute;
    @BindView(R.id.etStart) EditText etStart;
    @BindView(R.id.etEnd) EditText etEnd;
    @BindView(R.id.progressBar) ProgressBar pbLoading;
    @BindView(R.id.btLive) Button btLive;
    @BindView(R.id.etCarbieName) EditText etCarbieName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        fragmentManager = getFragmentManager();

        try {
            carbie = getArguments().getParcelable("carbie");
        } catch (NullPointerException e) {
            Log.e(TAG, "Carbie was not passed into RouteFragment");
            e.printStackTrace();
        }

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
            case "Electric":
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

        mMapView.onCreate(savedInstanceState);
        showProgressBar();
        mMapView.getMapAsync(this);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }

        btLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etCarbieName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a title!", Toast.LENGTH_LONG).show();
                } else {
                    carbie.setTitle(etCarbieName.getText().toString());

                    Fragment liveRouteFragment = new LiveRouteFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("carbie", carbie);
                    liveRouteFragment.setArguments(args);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentPlaceholder, liveRouteFragment)
                            .addToBackStack("RouteFragment")
                            .commit();
                }
            }
        });

        btSeeRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String start = etStart.getText().toString();
                final String end = etEnd.getText().toString();

                if (start.isEmpty() || end.isEmpty()) {
                    Toast.makeText(getContext(), "Missing start/end location.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "start:" + start);
                } else {
                    // clear map and mRoutes before adding new routes
                    mGoogleMap.clear();
                    mRoutes.clear();

                    calculateDirections(start, end);
                }
            }
        });

        btAcceptRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRoute == null) {
                    Toast.makeText(getContext(), "Need to select a route!", Toast.LENGTH_SHORT).show();
                } else if(etCarbieName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a title!", Toast.LENGTH_LONG).show();
                } else {
                    showProgressBar();
                    // Add information about selectedRoute and title to carbie
                    carbie.setTitle(etCarbieName.getText().toString());
                    carbie.setDistance(toMiles(selectedRoute.getDistance().inMeters));
                    carbie.setStartLocation(selectedRoute.getStartAddress());
                    carbie.setEndLocation(selectedRoute.getEndAddress());
                    // Create confirmationFragment and arguments bundle
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
                                            .addToBackStack("RouteFragment")
                                            .commit();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        hideProgressBar();
        mGoogleMap = googleMap;
        mGoogleMap.setOnPolylineClickListener(this);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng googleplex = new LatLng(37.422133, -122.084042);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(googleplex));
    }

    private void calculateDirections(String startLocation, String endLocation){
        showProgressBar();
        Log.d(TAG, "calculateDirections: calculating directions.");

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
                        Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                        Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                        Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);

                        // Start and End LatLng coordinates and user-friendly addresses
                        final com.google.maps.model.LatLng startLatLng = result.routes[0].legs[0].startLocation;
                        final com.google.maps.model.LatLng endLatLng = result.routes[0].legs[0].endLocation;
                        final String startAddress = result.routes[0].legs[0].startAddress;
                        final String endAddress= result.routes[0].legs[0].endAddress;

                        // Get northeast and southwest LatLngBounds to use for centering camera
                        final com.google.maps.model.LatLng preNE = result.routes[0].bounds.northeast;
                        final com.google.maps.model.LatLng preSW = result.routes[0].bounds.southwest;
                        final LatLng northeast = new LatLng(preNE.lat, preNE.lng);
                        final LatLng southwest = new LatLng(preSW.lat, preSW.lng);
                        routeBounds = new LatLngBounds(southwest, northeast);

                        hideProgressBar();
                        addMarker(startLatLng.lat, startLatLng.lng, startAddress, BitmapDescriptorFactory.HUE_RED, true);
                        addMarker(endLatLng.lat, endLatLng.lng, endAddress, BitmapDescriptorFactory.HUE_ORANGE, false);
                        addPolylinesToMap(result);
                        centerCameraOn(routeBounds);
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

    private void addPolylinesToMap(final DirectionsResult result){
        // Must run on main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                Distance minDistance = null;
                // Iterate through the available routes
                for (DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    // This loops through all com.google.maps.model.LatLng coordinates of ONE polyline
                    // and makes them LatLng coordinates
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();
                    for (com.google.maps.model.LatLng latLng: decodedPath){
                        newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
                    }
                    // Add polyline to the map
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorGrey));
                    polyline.setClickable(true);
                    mRoutes.add(new Route(polyline, route.legs[0]));
                    // Highlight shortest route
                    if (minDistance == null || route.legs[0].distance.inMeters < minDistance.inMeters) {
                        minDistance = route.legs[0].distance;
                        onPolylineClick(polyline);
                    }
                }
            }
        });
    }

    private void centerCameraOn(LatLngBounds bounds) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, ROUTE_PADDING), ANIMATION_DURATION_MS, null);
            }
        });
    }

    private void addMarker(double lat, double lng, String name, float color, boolean isStart) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (isStart) {
                    final LatLng markerLocation = new LatLng(lat,lng);
                    startMarker = mGoogleMap.addMarker(
                            new MarkerOptions().position(markerLocation)
                                    .title(name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
                } else {
                    final LatLng markerLocation = new LatLng(lat,lng);
                    endMarker = mGoogleMap.addMarker(
                            new MarkerOptions().position(markerLocation)
                                    .title(name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.GONE);
        mainActivity.findViewById(R.id.ivShare).setVisibility(TextView.GONE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!etStart.getText().toString().isEmpty() && !etEnd.getText().toString().isEmpty()) {
            btSeeRoutes.performClick();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.findViewById(R.id.tvName).setVisibility(TextView.VISIBLE);
        mainActivity.findViewById(R.id.ivShare).setVisibility(TextView.VISIBLE);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        for (Route route: mRoutes) {
            if (!route.getPolyline().getId().equals(polyline.getId())) {
                route.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.colorGrey));
                route.getPolyline().setZIndex(0); // sends polyline to back
            } else {
                selectedRoute = route;
                route.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.colorSkyBlue));
                route.getPolyline().setZIndex(1); // brings polyline to front
                endMarker.setSnippet("Distance: " + route.getDistance());
                endMarker.showInfoWindow();
            }
        }
    }

    private void prepMapForSnapshot() {
        centerCameraOn(routeBounds);
        endMarker.hideInfoWindow();
        Polyline polyline = selectedRoute.getPolyline();
        for (Route route: mRoutes) {
            if (!route.getPolyline().getId().equals(polyline.getId())) {
                route.getPolyline().setVisible(false);
            }
        }
    }

    private double toMiles(long meters) {
        return (meters * 0.00062137);
    }

    private void showProgressBar() {
        pbLoading.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressBar() {
        pbLoading.setVisibility(ProgressBar.INVISIBLE);
    }
}

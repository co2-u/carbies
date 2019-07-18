package com.example.carbonfootprinttracker.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.carbonfootprinttracker.R;
import com.example.carbonfootprinttracker.models.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RouteFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {
    private static final String TAG = "RouteFragment";
    private static final Integer ZOOM_LEVEL = 12;

    private GoogleMap mGoogleMap;
    private GeoApiContext mGeoApiContext = null;
    private ArrayList<Route> mRoutes = new ArrayList<>();

    @BindView(R.id.mapView) MapView mMapView;
    @BindView(R.id.btSetRoute) Button btSetRoute;
    @BindView(R.id.etStart) EditText etStart;
    @BindView(R.id.etEnd) EditText etEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }

        btSetRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String start = etStart.getText().toString();
                final String end = etEnd.getText().toString();

                // clear map and mRoutes before adding new routes
                mGoogleMap.clear();
                mRoutes.clear();

                calculateDirections(start, end);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnPolylineClickListener(this);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void calculateDirections(String startLocation, String endLocation){
        Log.d(TAG, "calculateDirections: calculating directions.");

        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                startLocation
        );

        Log.d(TAG, "calculateDirections: destination: " + endLocation);
        directions.destination(endLocation).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

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
                final LatLngBounds route = new LatLngBounds(southwest, northeast);

                addPolylinesToMap(result);
                centerCameraOn(route);
                addMarker(startLatLng.lat, startLatLng.lng, startAddress, BitmapDescriptorFactory.HUE_RED);
                addMarker(endLatLng.lat, endLatLng.lng, endAddress, BitmapDescriptorFactory.HUE_GREEN);
            }

            @Override
            public void onFailure(Throwable e) {
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
                }
            }
        });
    }

    private void centerCameraOn(LatLngBounds bounds) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), ZOOM_LEVEL));
            }
        });
    }

    private void addMarker(double lat, double lng, String name, float color) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final LatLng markerLocation = new LatLng(lat,lng);
                mGoogleMap.addMarker(new MarkerOptions().position(markerLocation)
                                                        .title(name)
                                                        .icon(BitmapDescriptorFactory.defaultMarker(color)));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        polyline.setColor(ContextCompat.getColor(getActivity(), R.color.colorSkyBlue));
        polyline.setZIndex(1); // brings polyline to front
        for (Route route: mRoutes) {
            if (!route.getPolyline().getId().equals(polyline.getId())) {
                route.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.colorGrey));
                route.getPolyline().setZIndex(0); // brings polyline to front
            }
        }
    }
}

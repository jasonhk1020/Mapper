package com.thinkful.mapper;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

;


public class MapsActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<LatLng> latLngList = new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(Bundle connectionHint) {

        // get last location
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //move the camera to the current location and then log coordinates. (maybe incorporate in here the path line)
        onLocationChanged(mCurrentLocation);

        //update with the new location every 5-10 seconds with highest accuracy
        startLocationUpdates(mGoogleApiClient);

    }
    protected void showLocation(Location mCurrentLocation) {
        if (mCurrentLocation != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()),16));
        }
    }

    @Override
    public void onLocationChanged(Location mCurrentLocation) {

        //add new current location to the end of list
        latLngList.add(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()));

        //send List to the polyline and set parameters
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions
                .addAll(latLngList)
                .width(10)
                .color(Color.GREEN);

        //add the polyline to the map.
        mMap.addPolyline(polylineOptions);

        //center over location
        showLocation(mCurrentLocation);

        //log where i am
        Log.i("Where am I?", "Latitude: " + mCurrentLocation.getLatitude() + ", Longitude:" + mCurrentLocation.getLongitude());

    }
    protected void startLocationUpdates(GoogleApiClient mGoogleApiClient) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            setUpMapIfNeeded();    // <-from previous tutorial
            startLocationUpdates(mGoogleApiClient);
        }
    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.setMyLocationEnabled(true);


        /*
        Marker thinkfulMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.72493, -73.996599))
                .title("Thinkful Headquarters")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.thinkful))
                .snippet("On a mission to reinvent education")
                .visible(true));
        thinkfulMarker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(40.72493, -73.996599), 10));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(19),2000,null);
            }
        }, 2000);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);*/

    }
}

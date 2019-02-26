package com.sistec.redspot;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 101;
    private static final int MAP_UPDATE_TIME = 10000; //10sec
    private static final long MAP_UPDATE_DISTANCE = 50; //50 meters
    private boolean IS_LOCATION_PERMISSION_ENABLED = false;

    TextView tvDengerCount;
    //TextView tvPlaceDetails;


    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationProviderClient;
    LocationCallback locationCallback;
    Marker marker;
    MarkerOptions markerOptions;
    Location userCurrentLocation = null;

    FirebaseDatabase database;
    DatabaseReference addressRef;
    Query fetchAddQuery;

    ArrayList<AddressStructure> addressStructuresArrayList = new ArrayList<AddressStructure>();
    AddressStructureAdapter adapter;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            setContentView(R.layout.activity_main);
            //tvPlaceDetails = findViewById(R.id.place_details);
            tvDengerCount = findViewById(R.id.denger_count);
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            database = FirebaseDatabase.getInstance();
            addressRef = database.getReference();
            adapter = new AddressStructureAdapter(this, addressStructuresArrayList);
            listView.setAdapter(adapter);
            checkLocationPermission();
            if (!IS_LOCATION_PERMISSION_ENABLED){
                getLocationPermission();
            }
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        Toast.makeText(MainActivity.this, "Cant get location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    userCurrentLocation = locationResult.getLastLocation();
                    LatLng ll = new LatLng(userCurrentLocation.getLatitude(), userCurrentLocation.getLongitude());
                    Geocoder gc = new Geocoder(MainActivity.this);
                    try {
                        List<Address> list = gc.getFromLocation(ll.latitude, ll.longitude,1);
                        setMarker(list.get(0), new LatLng(ll.latitude,ll.longitude));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15.0f);
                    mGoogleMap.animateCamera(update);
                };
            };
            initMap();
        } else {
            // No Google Maps Layout
            setContentView(R.layout.activity_main_error);
        }
    }

    private void setMarker(Address address, LatLng ll){
        if (address.getSubLocality()==null) {
            address.setSubLocality(address.getLocality());
        }
        markerOptions = new MarkerOptions()
                .title(address.getSubLocality())
                .draggable(true)
                .snippet("You are here")
                .position(ll);
        if (marker != null){
            marker.remove();
        }
        fetchAddQuery = addressRef.orderByChild("sub_locality").startAt(address.getSubLocality());
        fetchAddQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot result : dataSnapshot.getChildren()){
                        AddressStructure tempHolder = result.getValue(AddressStructure.class);
                        tempHolder.setCurr_lat_lng(userCurrentLocation);
                        addressStructuresArrayList.add(tempHolder);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*StringBuilder sb = new StringBuilder();
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            sb.append(address.getAddressLine(i)).append("\n");
        }
        sb.append(address.getLocality()).append("\n");
        sb.append(address.getPostalCode()).append("\n");
        sb.append(address.getCountryName());
        tvPlaceDetails.setText(sb.toString());*/
        marker = mGoogleMap.addMarker(markerOptions);
    }
    private void updateMarker(Address address){
        if (address.getSubLocality()==null)
            address.setSubLocality(address.getLocality());

        /*StringBuilder sb = new StringBuilder();
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            sb.append(address.getAddressLine(i)).append("\n");
        }
        sb.append(address.getLocality()).append("\n");
        sb.append(address.getPostalCode()).append("\n");
        sb.append(address.getCountryName());
        tvPlaceDetails.setText(sb.toString());*/
        marker.setSnippet("You are not here");
        marker.setTitle(address.getSubLocality());
        marker.showInfoWindow();
    }
    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mGoogleMap != null){

            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc = new Geocoder(MainActivity.this);
                    LatLng ll = marker.getPosition();
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(ll.latitude, ll.longitude,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = list.get(0);
                    updateMarker(address);
                }
            });

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.marker_discription, null);

                    TextView tvLocality = v.findViewById(R.id.tv_locality);
                    TextView tvLatitude = v.findViewById(R.id.tv_lat);
                    TextView tvLongitude = v.findViewById(R.id.tv_lng);
                    TextView tvSnippet = v.findViewById(R.id.tv_snippet);

                    LatLng ll = marker.getPosition();

                    tvLocality.setText(marker.getTitle());
                    tvLatitude.setText("Latitude: " + ll.latitude);
                    tvLongitude.setText("Longitude: " + ll.longitude);
                    tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        mLocationRequest = LocationRequest.create();
                        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        mLocationRequest.setInterval(MAP_UPDATE_TIME);
                        mLocationRequest.setSmallestDisplacement(MAP_UPDATE_DISTANCE);
                        try{
                            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback,null);
                        } catch (SecurityException ex){
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
        mGoogleApiClient.connect();
        //goToLocationZoom(23.3036179,77.3375503, 15.0f);
        /*
        if (checkLocationPermission())
            mGoogleMap.setMyLocationEnabled(true);
        else
            getLocationPermission();
         */

    }

    private void checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();

            IS_LOCATION_PERMISSION_ENABLED = false;   //if permission is not granted
        } else
            IS_LOCATION_PERMISSION_ENABLED = true;    //if permission is already granted
    }

    private void getLocationPermission(){
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IS_LOCATION_PERMISSION_ENABLED = true;

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                   IS_LOCATION_PERMISSION_ENABLED = false;
                   MainActivity.this.finish();
                }
            }
        }
    }

    private void goToLocationZoom(double lat, double lng, float zoom){
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }
}

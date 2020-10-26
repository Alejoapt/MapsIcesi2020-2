package com.example.mapsicesi2020_2.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsicesi2020_2.R;
import com.example.mapsicesi2020_2.communication.HoleWorker;
import com.example.mapsicesi2020_2.communication.LocationWorker;
import com.example.mapsicesi2020_2.communication.TrackHolesWorker;
import com.example.mapsicesi2020_2.communication.TrackUsersWorker;
import com.example.mapsicesi2020_2.model.Hole;
import com.example.mapsicesi2020_2.model.Position;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String user;
    private LocationManager manager;
    private Marker userMarker;
    private ArrayList<Marker> markers;
    private ArrayList<String> userNames;
    private Button addHoleBtn;
    private TextView holeDistancetxt;
    private LocationWorker locationWorker;
    private Position currentPosition;
    private TrackUsersWorker trackUsersWorker;
    private TrackHolesWorker trackHolesWorker;
    private HoleWorker holesWorker;
    private Marker hole;
    private Hole holeClass;
    private ArrayList<Marker> holes;
    private Button confirmBtn;
    private double distanceToHole;

    public ArrayList<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(ArrayList<String> userNames) {
        this.userNames = userNames;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        addHoleBtn = findViewById(R.id.addHoleBtn);
        user = getIntent().getExtras().getString("user");
        markers = new ArrayList<>();
        holes = new ArrayList<>();
        userNames = new ArrayList<>();
        holeDistancetxt = findViewById(R.id.holeDistancetxt);
        computeDistances();
        holeDistancetxt.setText("Hole a " + computeDistances() + " meters");
        holeDistancetxt.setGravity(Gravity.CENTER);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        confirmBtn = findViewById(R.id.confirmBtn);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
        setInitialPos();
        //mMap.setOnMapClickListener(this);
        //mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        holesWorker = new HoleWorker(this);
        addHoleBtn.setOnClickListener(
                (v) ->{

                    AlertDialog.Builder confirmMessage = new AlertDialog.Builder(this);
                    confirmMessage.setMessage("Coordinates:" + "\n" + currentPosition.getLat() + ", " + currentPosition.getLng() + "\n" + "Address:" + "\n"  + getCompleteAddress(currentPosition.getLat(), currentPosition.getLng()
                    ))
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    holeClass = new Hole(UUID.randomUUID().toString());
                                    holesWorker.execute();

                                }
                            });
                    AlertDialog tittle = confirmMessage.create();
                    tittle.setTitle("Add a hole");
                    tittle.show();

                    //LatLng latLng = new LatLng(currentPosition.getLat(),currentPosition.getLng());
                    //Map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 100));

                }
        );
        confirmBtn.setOnClickListener(
                (v)->{
                    for (int i = 0; i < holes.size(); i++){
                        holes.get(i).setTag(true);
                    }
                    Toast.makeText(this,"Hole confirmed", Toast.LENGTH_SHORT).show();
                    confirmBtn.setVisibility(View.INVISIBLE);
                }
        );

        locationWorker = new LocationWorker(this);
        locationWorker.execute();

        trackUsersWorker = new TrackUsersWorker(this);
        trackUsersWorker.start();


        trackHolesWorker = new TrackHolesWorker(this);
        trackHolesWorker.start();
    }

    @Override
    protected void onDestroy() {
        locationWorker.finish();
        trackUsersWorker.finish();
        holesWorker.finish();
        trackHolesWorker.finish();
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    public void setInitialPos(){
         Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         if(location != null) {
             updateMyLocation(location);
         }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateMyLocation(location);
    }

    public void updateMyLocation(Location location){

        LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 6));
        currentPosition = new Position(location.getLatitude(), location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        for(int i = 0; i < holes.size(); i++) {
            if (marker.getTag().equals(false)) {
                confirmBtn.setVisibility(View.VISIBLE);
            }
        }
        marker.showInfoWindow();
        return true;
    }

    public double computeDistances(){
        distanceToHole = 1000000000;
        runOnUiThread(
                ()->{
                    if(holes != null) {
                        for (int i = 0; i < holes.size(); i++) {
                            Marker marker = holes.get(i);
                            LatLng holeLoc = marker.getPosition();
                            LatLng meLoc = new LatLng(currentPosition.getLat(), currentPosition.getLng());

                            distanceToHole = (Math.rint((Math.min((SphericalUtil.computeDistanceBetween(holeLoc, meLoc)), distanceToHole) * 10) / 10));

                        }
                    }

                }
        );
        return distanceToHole;
    }

    public String getCompleteAddress(double latitude, double longitude){
        String address = "";
        Geocoder geocoder = new Geocoder(MapsActivity.this , Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude, 1);
            if (address != null){
                Address returnAddress = addresses.get(0);
                StringBuilder stringBuilderReturnAddress = new StringBuilder();
                for (int i = 0; i <= returnAddress.getMaxAddressLineIndex(); i++){
                    stringBuilderReturnAddress.append(returnAddress.getAddressLine(i)).append("\n");
                }
                address = stringBuilderReturnAddress.toString();
            }else{
                Toast.makeText(this,"Address not found", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }
    public Position getCurrentPosition(){
        return currentPosition;
    }

    public String getUser(){
        return user;
    }

    public Hole getHoleClass() {
        return holeClass;
    }

    public void updateMarkers(ArrayList<Position> positions){
            LatLng markerPos = new LatLng(currentPosition.getLat(), currentPosition.getLng());
            runOnUiThread(
                    ()->{

                        for (int i = 0; i < markers.size(); i++){
                            Marker m = markers.get(i);
                            m.remove();
                        }
                        markers.clear();

                            for (int i = 0; i < positions.size(); i++) {
                                for (int j = 0; j < this.userNames.size(); j++) {
                                    String id = this.userNames.get(i);
                                    Position pos = positions.get(i);
                                    LatLng latLng = new LatLng(pos.getLat(), pos.getLng());
                                    userMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                                    userMarker.setTag(i);
                                    if (userMarker.getTag().equals(1)) {
                                        userMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                    } else {
                                        userMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                        userMarker.setTitle(id);
                                    }


                                    markers.add(userMarker);
                                }

                            }



                    }
            );
    }

    public void updateHoleMarkers(ArrayList<Position> positions) {

        runOnUiThread(
                ()->{

                    for (int i = 0; i < holes.size(); i++){
                        Marker m = holes.get(i);
                        m.remove();
                    }
                    holes.clear();

                    for (int i = 0; i < positions.size(); i++){
                        Position pos = positions.get(i);
                        LatLng latLng = new LatLng(pos.getLat(), pos.getLng());
                        Marker m = mMap.addMarker(new MarkerOptions().position(latLng));
                        m.setTag(false);
                        if (m.getTag().equals(false)) {
                            m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.holeredmarker));
                        }else{
                            m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.holegraymarker));
                        }
                        holes.add(m);
                    }

                }
        );

    }

}


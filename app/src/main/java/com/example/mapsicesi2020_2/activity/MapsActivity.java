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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsicesi2020_2.R;
import com.example.mapsicesi2020_2.communication.LocationWorker;
import com.example.mapsicesi2020_2.communication.TrackUsersWorker;
import com.example.mapsicesi2020_2.model.Position;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private String user;
    private LocationManager manager;
    //private Marker me;
    private ArrayList<Marker> markers;
    private Button addHoleBtn;
    private TextView holeDistancetxt;
    private TextView txtData;
    private LocationWorker locationWorker;
    private Position currentPosition;
    private TrackUsersWorker trackUsersWorker;
    private Marker hole;
    private ArrayList<Marker> holes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        addHoleBtn = findViewById(R.id.addHoleBtn);
        user = getIntent().getExtras().getString("user");
        markers = new ArrayList<>();
        txtData = findViewById(R.id.txtData);
        holeDistancetxt = findViewById(R.id.holeDistancetxt);
        holeDistancetxt.setText("Hole a " + computeDistances() + " meters");
        holeDistancetxt.setGravity(Gravity.CENTER);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, this);
        setInitialPos();
        //mMap.setOnMapClickListener(this);
        //mMap.setOnMapLongClickListener(this);
        //mMap.setOnMarkerClickListener(this);

        addHoleBtn.setOnClickListener(
                (v) ->{
                    AlertDialog.Builder confirmMessage = new AlertDialog.Builder(this);
                    //txtData.setText("\"Coordinates:\" + \"\\n\" + me.getPosition().latitude + \", \" + me.getPosition().longitude + \"\\n\" + \"Address:\" + \"\\n\"");
                   // txtData.showContextMenu();
                    //String address = locationAddress.getAddressFromLocation(me.getPosition().latitude, me.getPosition().longitude, getApplicationContext(), new Geocoder());

                    confirmMessage.setMessage("Coordinates:" + "\n" + currentPosition.getLat() + ", " + currentPosition.getLng() + "\n" + "Address:" + "\n"  + getCompleteAddress(currentPosition.getLat(), currentPosition.getLng()
                    ))
                    .setCancelable(true)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            LatLng myPos = new LatLng(currentPosition.getLng(), currentPosition.getLng());
                            hole = mMap.addMarker(new MarkerOptions().position(myPos).title("Hole"));
                            hole.setIcon(BitmapDescriptorFactory.defaultMarker(90));
                            hole.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.holegraymarker));
                        }
                    });
                    AlertDialog tittle = confirmMessage.create();
                    tittle.setTitle("Add a hole");
                    tittle.show();

                    LatLng latLng = new LatLng(currentPosition.getLat(),currentPosition.getLng());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                }
        );
        locationWorker = new LocationWorker(this);
        locationWorker.start();

        trackUsersWorker = new TrackUsersWorker(this);
        trackUsersWorker.start();
    }

    @Override
    protected void onDestroy() {
        locationWorker.finish();
        trackUsersWorker.finish();
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
        /*
        if (me == null) {
            me = mMap.addMarker(new MarkerOptions().position(myPos).title("Yo"));
        }else{
            me.setPosition(myPos);
        }
         */
        mMap.animateCamera(CameraUpdateFactory.newLatLng(myPos));
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
        //Marker m = mMap.addMarker(new MarkerOptions().position(latLng).title("Marcador"));
        //snipet subtittle
        //markers.add(m);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(this, marker.getPosition().latitude + ", " + marker.getPosition().longitude, Toast.LENGTH_LONG).show();
        marker.showInfoWindow();
        return true;
    }

    public double computeDistances(){
        double meters = 0;
        if(holes != null) {
            for (int i = 0; i < holes.size(); i++) {
                Marker marker = holes.get(i);
                LatLng holeLoc = marker.getPosition();
                LatLng meLoc = new LatLng(currentPosition.getLat(), currentPosition.getLng());

                meters = SphericalUtil.computeDistanceBetween(holeLoc, meLoc);
            }
        }
        return meters;
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
    public Position getMyMarker(){
        return currentPosition;
    }

    public String getUser(){
        return user;
    }

    public void updateMarkers(ArrayList<Position> positions){
            runOnUiThread(
                    ()->{

                        for (int i = 0; i < markers.size(); i++){
                            Marker m = markers.get(i);
                            m.remove();
                        }

                        for (int i = 0; i < positions.size(); i++){
                            Position pos = positions.get(i);
                            LatLng latLng = new LatLng(pos.getLat(), pos.getLng());
                            Marker m = mMap.addMarker(new MarkerOptions().position(latLng));
                            markers.add(m);
                        }
                    }
            );
    }
}


package com.example.redrestaurantapp.Views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.ServiceLayer.UserManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditPinsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private final String TAG = "EditPinsActivity";

    LinearLayout btnBack;

    Button btnDone;

    ConstraintLayout spinnerContainer;
    ConstraintLayout contentContainer;

    GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pins);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        btnBack = findViewById(R.id.btnBackEditPin);
        btnBack.setOnClickListener(this::onBackClick);

        btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(this::onDoneClick);

        spinnerContainer = findViewById(R.id.spinnerContainerEditPin);
        contentContainer = findViewById(R.id.contentContainerEditPin);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setMyLocationEnabled();
        getCurrentLocation();

        setLoading(false);
    }

    private void onBackClick(View v) {
        finish();
    }

    private void onDoneClick(View v) {
        if(mMap == null) return;

        saveSelectedLocation();
    }

    private void setMyLocationEnabled() {
        if(mMap == null) return;

        checkLocationPermission();
        mMap.setMyLocationEnabled(true);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            setMyLocationEnabled();
            return;
        }

        Log.d(TAG, "permission denied!");
    }

    private void getCurrentLocation() {
        checkLocationPermission();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location == null) return;
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        if(mMap == null) return;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        mMap.animateCamera(cameraUpdate);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void setMarker(LatLng position){
        if(position == null) return;

        Bitmap bmpOg = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        Bitmap bmp = Bitmap.createScaledBitmap(bmpOg, 79, 171, true);

        mMap.addMarker(new MarkerOptions()
                .title("CurrentLocation")
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
    }

    private void saveSelectedLocation() {
        if(mMap == null) return;

        setLoading(true);

        LatLng selectedLocation = mMap.getCameraPosition().target;
        String newAddress = getAddress(selectedLocation);

        UserManager.getAddress(this, new UserManager.OnAddressReadCompleted() {
            @Override
            public void onSuccess(com.example.redrestaurantapp.Models.Address address) {
                address.setLatitude(selectedLocation.latitude);
                address.setLongitude(selectedLocation.longitude);
                address.setAddress(newAddress);

                UserManager.setAddress(getApplicationContext(), address, new UserManager.OnAddressWriteCompleted() {
                    @Override
                    public void onSuccess(boolean success) {
                        Log.d(TAG, "new location saved!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EditPinsActivity.this, "New location has been saved.", Toast.LENGTH_SHORT).show();
                                        finish();
                                        setLoading(false);
                                    }
                                }, 1000);
                            }
                        });
                    }

                    @Override
                    public void onFailed(Exception ex) {
                        ex.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLoading(false);
                                Toast.makeText(EditPinsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailed(Exception ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoading(false);
                        Toast.makeText(EditPinsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String getAddress(LatLng latLng){
        if(latLng == null) return null;

        try{
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if(addressList == null || addressList.isEmpty()) return null;
            Address fetchedAddresses = addressList.get(0);
            StringBuilder addressBuilder = new StringBuilder();
            for(int i = 0; i <= fetchedAddresses.getMaxAddressLineIndex(); i++)
                addressBuilder.append(fetchedAddresses.getAddressLine(i)).append("\n");

            return addressBuilder.toString();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    private void setLoading(boolean state){
        if(state){
            spinnerContainer.setVisibility(View.VISIBLE);
            contentContainer.setVisibility(View.GONE);
            return;
        }

        spinnerContainer.setVisibility(View.GONE);
        contentContainer.setVisibility(View.VISIBLE);
    }
}
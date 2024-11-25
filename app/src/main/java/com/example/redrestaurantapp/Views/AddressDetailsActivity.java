package com.example.redrestaurantapp.Views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.redrestaurantapp.Models.Address;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.ServiceLayer.UserManager;
import com.example.redrestaurantapp.Utils.AlertBox;
import com.example.redrestaurantapp.Views.BottomSheetDialogs.BuildingTypesBottomSheet;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.snapshot.StringNode;

import org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Locale;

public class AddressDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final String TAG = "AddressDetailsActivity";
    private final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    ImageButton btnBack;
    ImageButton btnNotifications;
    ImageButton btnCart;

    TextView txtAppBarTitle;
    TextView txtCurrentAddress;

    EditText txtBuildingType;
    EditText txtAptFlatFloor;
    EditText txtBuildingName;
    EditText txtLandmark;
    EditText txtDeliveryInstructions;
    EditText txtAddressLabel;

    Button btnSaveAndContinue;

    ConstraintLayout contentContainer;
    ConstraintLayout spinnerContainer;

    LinearLayout btnEditPin;

    Address mAddress;
    GoogleMap mMap;

    FusedLocationProviderClient mFusedLocationProviderClient;

    public AddressDetailsActivity() {
        mAddress = new Address();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtAppBarTitle = findViewById(R.id.txtAppbarTitle);
        txtAppBarTitle.setText("Address Details");

        txtCurrentAddress = findViewById(R.id.txtCurrentLocation);
        txtCurrentAddress.setText("Address not found.");

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this::onBackClick);

        btnNotifications = findViewById(R.id.btnNotifications);
        btnNotifications.setVisibility(View.GONE);

        btnCart = findViewById(R.id.btnCart);
        btnCart.setVisibility(View.GONE);

        txtBuildingType = findViewById(R.id.txtBuildingType);
        txtBuildingType.setOnClickListener(this::onBuildingTypeClick);

        txtAptFlatFloor = findViewById(R.id.txtAptFlatFloor);
        txtBuildingName = findViewById(R.id.txtBuildingName);
        txtLandmark = findViewById(R.id.txtLandmark);
        txtDeliveryInstructions = findViewById(R.id.txtDeliveryInstructions);
        txtAddressLabel = findViewById(R.id.txtAddressLabel);

        btnSaveAndContinue = findViewById(R.id.btnSaveAndContinue);
        btnSaveAndContinue.setOnClickListener(this::onSaveAndContinueClick);

        contentContainer = findViewById(R.id.contentContainer);
        spinnerContainer = findViewById(R.id.spinnerContainer);

        btnEditPin = findViewById(R.id.btnEditPin);
        btnEditPin.setOnClickListener(this::onEditPinClick);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getCurrentLocation();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getCurrentLocation();
        setLoading(false);
    }

    private void onBackClick(View v){
        finish();
    }

    private void onSaveAndContinueClick(View v){
        setLoading(true);

        mAddress.setBuildingType(txtBuildingType.getText().toString());
        mAddress.setAptFlatFloor(txtAptFlatFloor.getText().toString());
        mAddress.setBuildingName(txtBuildingName.getText().toString());
        mAddress.setLandmark(txtLandmark.getText().toString());
        mAddress.setDeliveryInstructions(txtDeliveryInstructions.getText().toString());
        mAddress.setAddressLabel(txtAddressLabel.getText().toString());

        UserManager.setAddress(this, mAddress, new UserManager.OnAddressWriteCompleted() {
            @Override
            public void onSuccess(boolean success) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddressDetailsActivity.this, "Saved successfully!", Toast.LENGTH_SHORT).show();
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
                setLoading(false);
                AlertBox alertBox = new AlertBox(ex.getMessage(), AlertBox.Type.ERROR);
                alertBox.show(getSupportFragmentManager(), TAG);
            }
        });
    }

    private void onBuildingTypeClick(View v){
        BuildingTypesBottomSheet bottomSheet = new BuildingTypesBottomSheet();
        bottomSheet.show(getSupportFragmentManager(), TAG);
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

    private void configureMap(){
        if(mMap == null) return;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void setMyLocationButton(){
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
            setMyLocationButton();
            return;
        }

        Log.d(TAG, "permission denied!");
    }

    private void getCurrentLocation() {
        if(mMap == null) return;
        UserManager.getAddress(this, new UserManager.OnAddressReadCompleted() {
            @Override
            public void onSuccess(Address address) {
                Log.d(TAG, "Success read");
                Log.d(TAG, address.toString());
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mAddress = address;

                Log.d(TAG, String.valueOf(mMap == null));

                updateFields(address);
                setMarker(latLng);
                updateCurrentAddressLabel(latLng);
                updateCamera(latLng);
            }

            @Override
            public void onFailed(Exception ex) {
                checkLocationPermission();
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                mFusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location == null) return;
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                                if(mMap == null) return;
                                setMarker(latLng);
                                updateCurrentAddressLabel(latLng);
                                updateCamera(latLng);
                                mAddress.setLatitude(latLng.latitude);
                                mAddress.setLongitude(latLng.longitude);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
            }
        });
    }

    private void updateCamera(LatLng position){
        if(position == null) return;

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 15);
        mMap.animateCamera(cameraUpdate);
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

    private void updateCurrentAddressLabel(LatLng position) {
        if(position == null) return;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try{
            List<android.location.Address> addressList = geocoder.getFromLocation(position.latitude, position.longitude, 1);

            if(addressList == null || addressList.isEmpty()) return;

            android.location.Address fetchedAddress = addressList.get(0);
            StringBuilder addressBuilder = new StringBuilder();

            for(int i = 0; i <= fetchedAddress.getMaxAddressLineIndex(); i++)
                addressBuilder.append(fetchedAddress.getAddressLine(i)).append("\n");

            txtCurrentAddress.setText(addressBuilder.toString());
            mAddress.setAddress(addressBuilder.toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void updateFields(Address address){
        if(address == null) return;

        Log.d(TAG + "[updateFields]:", address.toString());

        if(!address.getBuildingType().isEmpty())
            txtBuildingType.setText(address.getBuildingType());

        if(!address.getAptFlatFloor().isEmpty())
            txtAptFlatFloor.setText(address.getAptFlatFloor());

        if(!address.getBuildingName().isEmpty())
            txtBuildingName.setText(address.getBuildingName());

        if(!address.getLandmark().isEmpty())
            txtLandmark.setText(address.getLandmark());

        if(!address.getDeliveryInstructions().isEmpty())
            txtDeliveryInstructions.setText(address.getDeliveryInstructions());

        if(!address.getAddressLabel().isEmpty())
            txtAddressLabel.setText(address.getAddressLabel());
    }

    private void onEditPinClick(View v) {
        Intent editPinActivity = new Intent(this, EditPinsActivity.class);
        startActivity(editPinActivity);
    }
}
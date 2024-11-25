package com.example.redrestaurantapp.Views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.redrestaurantapp.Interfaces.TaskCallback;
import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.AlertBox;
import com.example.redrestaurantapp.Utils.StoragePermissionModule;
import com.example.redrestaurantapp.ServiceLayer.UserManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = "SplashActivity";
    private final int POST_NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    ImageView mImgLogo;
    StoragePermissionModule mStoragePermissionModule;
    UserManager mUser;

    TaskCallback notificationPermissionCallback;

    public SplashActivity() {
        mUser = new UserManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mImgLogo = findViewById(R.id.imgLogo);
        mStoragePermissionModule = new StoragePermissionModule(this);

        Log.d(TAG, "currentUserState: " + mUser.isValidUser());

        checkAllPermissions();
    }

    private void checkAllPermissions() {
        checkStoragePermissions(() -> checkNotificationPermission(this::checkUserValidity));
    }

    private void checkStoragePermissions(TaskCallback callback) {
        if(mStoragePermissionModule.checkPermission() < 1) {
            Log.d(TAG, "Storage permission has not granted!");

            AlertBox alertBox = new AlertBox("Storage permission is required!", AlertBox.Type.WARNING);
            alertBox.show(
                    getSupportFragmentManager(),
                    "Warning",
                    new AlertBox.Action() {
                        @Override
                        public void onClick() {
                            Log.d(TAG, "positive clicked!");
                            mStoragePermissionModule.requestPermission(
                                    new StoragePermissionModule.ResultCallback() {
                                        @Override
                                        public void onCallback(ActivityResult result) {
                                            if(result.getResultCode() == Activity.RESULT_OK) {
                                                Log.d(TAG, "Permission granted.");
                                                callback.onComplete();
                                            }else{
                                                checkAllPermissions();
                                            }
                                        }
                                    }
                            );
                        }
                    },
                    new AlertBox.Action() {
                        @Override
                        public void onClick() {
                            Log.d(TAG,"negative clicked!");
                            System.exit(0);
                        }
                    }
            );
        }else{
            Toast.makeText(this, "Storage permission granted.", Toast.LENGTH_SHORT).show();
            callback.onComplete();
        }
    }

    private void checkNotificationPermission(TaskCallback callback) {
        int granted =
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                );
        if(granted != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{ Manifest.permission.POST_NOTIFICATIONS }, POST_NOTIFICATION_PERMISSION_REQUEST_CODE);
            notificationPermissionCallback = callback;
        }else{
            Log.d(TAG, "Notification permission has granted!");
            callback.onComplete();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == POST_NOTIFICATION_PERMISSION_REQUEST_CODE){
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if(notificationPermissionCallback == null) return;
            if(!granted) {
                checkNotificationPermission(this::checkUserValidity);
                return;
            }
            notificationPermissionCallback.onComplete();
            notificationPermissionCallback = null;
        }
    }

    private void checkUserValidity(){
        if(!mUser.isValidUser()) {
            navigateToSignIn();
        }else{
            startLogoAnimation();
            navigateToMain();
        }
    }

    private void startLogoAnimation() {
        mImgLogo.animate()
                .alpha(1)
                .setDuration(5000)
                .setStartDelay(100);
    }

    private void navigateToMain() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        }, 6000);
    }

    private void navigateToSignIn() {
        Intent signInActivity = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signInActivity);
        finish();
    }
}
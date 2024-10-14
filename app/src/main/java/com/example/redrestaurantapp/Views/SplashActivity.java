package com.example.redrestaurantapp.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Utils.AlertBox;
import com.example.redrestaurantapp.Utils.StoragePermissionModule;
import com.example.redrestaurantapp.ServiceLayer.UserManager;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = "SplashActivity";

    ImageView mImgLogo;
    StoragePermissionModule mStoragePermissionModule;
    UserManager mUser;

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

        if(!mUser.isValidUser()) {
            navigateToSignIn();
            return;
        }

        checkStoragePermissions();
    }

    private void checkStoragePermissions() {
        if(mStoragePermissionModule.checkPermission() < 1) {
            Toast.makeText(this, "Storage permission has not granted.", Toast.LENGTH_SHORT).show();

            AlertBox alertBox = new AlertBox("Storage permission is required!", AlertBox.Type.WARNING);
            alertBox.show(
                    getSupportFragmentManager(),
                    "Warning",
                    new AlertBox.Action() {
                        @Override
                        public void onClick() {
                            Toast.makeText(SplashActivity.this, "positive clicked!", Toast.LENGTH_SHORT).show();
                            mStoragePermissionModule.requestPermission(
                                    new StoragePermissionModule.ResultCallback() {
                                        @Override
                                        public void onCallback(ActivityResult result) {
                                            if(result.getResultCode() == Activity.RESULT_OK) {
                                                Toast.makeText(SplashActivity.this, "Permission granted.", Toast.LENGTH_SHORT).show();

                                                startLogoAnimation();
                                                navigateToMain();
                                            }else{
                                                checkStoragePermissions();
                                            }
                                        }
                                    }
                            );
                        }
                    },
                    new AlertBox.Action() {
                        @Override
                        public void onClick() {
                            Toast.makeText(SplashActivity.this, "negative clicked!", Toast.LENGTH_SHORT).show();
                            System.exit(0);
                        }
                    }
            );
        }else{
            Toast.makeText(this, "Storage permission granted.", Toast.LENGTH_SHORT).show();
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
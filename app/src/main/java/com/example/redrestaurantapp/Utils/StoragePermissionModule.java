package com.example.redrestaurantapp.Utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class StoragePermissionModule {
    private final AppCompatActivity mActivity;
    private ResultCallback mResultCallback;
    private ActivityResultLauncher<Intent> mActivityResultLauncher;

    final int PERMISSION_GRANTED = 1;
    final int PERMISSION_DENIED = 0;
    final int LESS_THAN_API_LEVEL_31 = -1;
    final int LEGACY_PERMISSION_REQUEST_CODE = 100;
    final String TAG = "StoragePermissionModule";

    public StoragePermissionModule(AppCompatActivity activity) {
        mActivity = activity;

        registerActivityResultLauncher();
    }

    public int checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if(Environment.isExternalStorageManager()) {
                return PERMISSION_GRANTED;
            }else{
                return PERMISSION_DENIED;
            }
        }
        return  PERMISSION_DENIED;
    }

    private void registerActivityResultLauncher() {
        if(mActivity == null) return;

        // register activity result launcher
        mActivityResultLauncher = mActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d(TAG, "" + result.getResultCode());
                        if(mResultCallback == null) return;

                        mResultCallback.onCallback(result);
                    }
                }
        );
    }

    public void requestPermission(ResultCallback resultCallback) {
        mResultCallback = resultCallback;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            try{
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                mActivityResultLauncher.launch(intent);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            ActivityCompat.requestPermissions(mActivity, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, LEGACY_PERMISSION_REQUEST_CODE);
        }
    }

    public interface ResultCallback {
        void onCallback(ActivityResult result);
    }
}

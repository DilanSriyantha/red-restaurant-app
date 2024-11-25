package com.example.redrestaurantapp.ServiceLayer;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.redrestaurantapp.Models.Address;
import com.example.redrestaurantapp.Utils.ThreadPoolManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class UserManager {
    private static final String TAG = "UserManager";

    private static FirebaseAuth mAuth;
    private static FirebaseUser mUser;
    private static Address mAddress;

    public UserManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        if(mUser == null)
            mUser = mAuth.getCurrentUser();

        return mUser;
    }

    public boolean isValidUser() {
        return getCurrentUser() != null;
    }

    public static void setAddress(Context ctx, Address address, OnAddressWriteCompleted callback){
        mAddress = address;
        saveAddressToCache(ctx, callback);
    }

    public static void getAddress(Context ctx, OnAddressReadCompleted callback){
        if(mAddress != null) {
            callback.onSuccess(mAddress);
            return;
        }
        readAddressFromCache(ctx, callback);
    }

    private static void saveAddressToCache(Context ctx, OnAddressWriteCompleted callback) {
        if(ctx == null) return;
        if(mAddress == null) return;

        ThreadPoolManager.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {
                File cacheDir = ctx.getCacheDir();
                File file = new File(cacheDir, "AddressInfo");

                try(FileOutputStream fos = new FileOutputStream(file)){
                    try(ObjectOutputStream oos = new ObjectOutputStream(fos)){
                        oos.writeObject(mAddress);
                        callback.onSuccess(true);
                    }catch (Exception ex){
                        callback.onFailed(ex);
                    }
                }catch (Exception ex){
                    callback.onFailed(ex);
                }
            }
        });
    }

    private static void readAddressFromCache(Context ctx, OnAddressReadCompleted callback){
        if(ctx == null) return;

        ThreadPoolManager.getInstance().submitTask(new Runnable() {
            @Override
            public void run() {
                File cacheDir = ctx.getCacheDir();
                File file = new File(cacheDir, "AddressInfo");

                if(!file.exists()){
                    callback.onFailed(new Exception("AddressInfo file does not exist"));
                    return;
                }

                try(FileInputStream fis = new FileInputStream(file)){
                    try(ObjectInputStream ois = new ObjectInputStream(fis)){
                        Object obj = ois.readObject();
                        if(obj instanceof Address){
                            mAddress = (Address) obj;
                            callback.onSuccess(mAddress);
                            return;
                        }
                        callback.onFailed(new Exception("Object was not an instance of Address"));
                    }catch (Exception ex){
                        callback.onFailed(ex);
                    }
                }catch (Exception ex){
                    callback.onFailed(ex);
                }
            }
        });
    }

    public interface OnAddressReadCompleted {
        void onSuccess(Address address);
        void onFailed(Exception ex);
    }

    public interface OnAddressWriteCompleted {
        void onSuccess(boolean success);
        void onFailed(Exception ex);
    }
}
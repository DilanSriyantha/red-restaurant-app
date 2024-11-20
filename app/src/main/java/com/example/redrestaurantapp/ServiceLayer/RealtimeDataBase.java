package com.example.redrestaurantapp.ServiceLayer;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RealtimeDataBase {
    private final String TAG = "RealTimeDataBase";
    private static RealtimeDataBase mInstance;
    private static DatabaseReference mDbRef;

    public RealtimeDataBase(String databaseReference) {
        init(databaseReference);
    }

    public static RealtimeDataBase getInstance(String databaseReference) {
        if(mInstance == null)
            mInstance = new RealtimeDataBase(databaseReference);

        return mInstance;
    }

    private void init(String databaseReference) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDbRef = database.getReference(databaseReference);
    }

    public <T> void fetch(Class<T> model, OnFetchCompleted<T> callback) {
        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                handleValueChange(snapshot, model, callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public <T> void fetch(String valueEqualTo, Class<T> model, OnFetchCompleted<T> callback){
        mDbRef.orderByChild("to").equalTo(valueEqualTo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                handleValueChange(snapshot, model, callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public <T> void startListeningToChildEvents(Class<T> model, OnRealTimeDataChangedCallback<T> callback) {
        mDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                handleChildChange(snapshot, model, callback);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                handleChildChange(snapshot, model, callback);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public <T> void startListeningToChildEvents(String orderBy, String equalTo, Class<T> model, OnRealTimeDataChangedCallback<T> callback){
        mDbRef.orderByChild(orderBy).equalTo(equalTo).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                handleChildChange(snapshot, model, callback);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                handleChildChange(snapshot, model, callback);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private <T> void handleChildChange(DataSnapshot snapshot, Class<T> model, OnRealTimeDataChangedCallback<T> callback){
        try{
            T modelInstance = model.getDeclaredConstructor().newInstance();
            Field[] fields = model.getDeclaredFields();

            for(DataSnapshot snap : snapshot.getChildren()){
                for(Field field : fields){
                    if(field.getName().equals(snap.getKey())){
                        field.setAccessible(true);
                        field.set(modelInstance, snap.getValue());
                    }
                }
            }

            callback.onChange(modelInstance);
        }catch (Exception ex){
            callback.onError(ex);
        }
    }

    private <T> void handleValueChange(DataSnapshot snapshot, Class<T> model, OnFetchCompleted<T> callback){
        try{
            List<T> children = new ArrayList<>();

            for(DataSnapshot outerSnap : snapshot.getChildren()){
                T modelInstance = model.getDeclaredConstructor().newInstance();
                Field[] fields = model.getDeclaredFields();
                for(DataSnapshot innerSnap : outerSnap.getChildren()){
                    for(Field field : fields){
                        if(field.getName().equals(innerSnap.getKey())){
                            field.setAccessible(true);
                            field.set(modelInstance, innerSnap.getValue());
                        }
                    }
                }
                children.add(modelInstance);
            }

            callback.onSuccessful(children);
        }catch(Exception ex){
            callback.onFailure(ex);
        }
    }

    public interface OnRealTimeDataChangedCallback <T> {
        void onChange(T latestChild);
        void onError(Exception ex);
    }

    public interface OnFetchCompleted<T> {
        void onSuccessful(List<T> children);
        void onFailure(Exception ex);
    }
}

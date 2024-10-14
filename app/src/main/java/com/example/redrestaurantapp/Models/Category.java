package com.example.redrestaurantapp.Models;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.nfc.Tag;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Category {
    private final String TAG = "Category";

    private long id;
    private String name;
    private String imageUrl;

    public Category() {}

    public Category(long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Category(HashMap<String, Object> dataMap){
        String[] keys = dataMap.keySet().toArray(new String[0]);
        Field[] fields = this.getClass().getDeclaredFields();

        for(String key : keys){
            for(Field field : fields){
                if(field.getName().equals(key)){
                    field.setAccessible(true);

                    try{
                        field.set(this, dataMap.get(key));
                    }catch (Exception ex){
                        Log.d(TAG, "Constructor error: " + ex.getLocalizedMessage());
                    }
                    break;
                }
            }
        }
    }

    public Category(Map<String, Object> dataMap){
        String[] keys = dataMap.keySet().toArray(new String[0]);
        Field[] fields = this.getClass().getDeclaredFields();

        for(String key : keys){
            for(Field field : fields){
                if(field.getName().equals(key)){
                    field.setAccessible(true);

                    try{
                        field.set(this, dataMap.get(key));
                    }catch (Exception ex){
                        Log.d(TAG, "Constructor error: " + ex.getLocalizedMessage());
                    }
                    break;
                }
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

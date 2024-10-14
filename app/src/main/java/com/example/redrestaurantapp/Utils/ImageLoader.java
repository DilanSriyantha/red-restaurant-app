package com.example.redrestaurantapp.Utils;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.example.redrestaurantapp.R;
import com.example.redrestaurantapp.Views.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static String TAG = "ImageLoader";
    private static ImageLoader mInstance;
    private static Map<String, Bitmap> mInMemCache = new HashMap<>();
    private final Context mCtx;

    public ImageLoader(Context ctx) {
        mCtx = ctx;
    }

    public static ImageLoader getInstance(Context ctx) {
        if(mInstance == null)
            mInstance = new ImageLoader(ctx);

        return mInstance;
    }

    public void loadImage(ImageView imageView, String url){
        // declaring executor to parse the url
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // once the executor parses the url
        // and receives the image, handler will load it
        // in the image view
        Handler handler = new Handler(Looper.getMainLooper());

        // initializing the image
        Bitmap image = null;

        // set image if in memory cache available
        if(isStored(url)){
            imageView.setImageBitmap(mInMemCache.get(url));
            return;
        }

        // set image placeholder temporarily
        imageView.setImageResource(R.drawable.image_placeholder);

        // only for background process (can take time depending on the internet efficiency
        executorService.execute(new Runnable() {
            String _url = url;
            Bitmap _image = image;
            @Override
            public void run() {
                try{
                    InputStream is = new URL(_url).openStream();
                    _image = BitmapFactory.decodeStream(is);

                    if(!isStored(_url)) {
                        saveToImMemoryCache(_url, _image);
                        saveToLocalCache(_url, _image);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(_image);
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage());
                }
            }
        });
    }

    private boolean isStored(String url) {
        return mInMemCache.containsKey(url) || isStoredLocally(url);
    }

    private boolean isStoredLocally(String key){
        if(key == null) return false;

        File cacheDir = mCtx.getCacheDir();

        File cacheFile = new File(cacheDir, key.replace('/',  '_') + ".png");
        if(cacheFile.exists()) {
            mInMemCache.put(key, BitmapFactory.decodeFile(cacheFile.getAbsolutePath()));
            return true;
        }else{
            return false;
        }
    }

    private void saveToImMemoryCache(String key, Bitmap bmp){
        mInMemCache.put(key, bmp);
    }

    private void saveToLocalCache(String key, Bitmap bmp){
        File cacheDir = mCtx.getCacheDir();

        File file = new File(cacheDir, key.replace('/', '_') + ".png");

        try(FileOutputStream fos = new FileOutputStream(file)){
            bmp.compress(Bitmap.CompressFormat.PNG, 60, fos);
            fos.flush();
            Log.d(TAG, key + " saved to localCacheDir");
        }catch (Exception ex){
            Log.e(TAG, ex.getLocalizedMessage());
        }
    }

    public void loadImage(ImageView imageView, Uri uri){
        try{
            URL url = new URL(uri.toString());
            loadImage(imageView, url.toString());
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}

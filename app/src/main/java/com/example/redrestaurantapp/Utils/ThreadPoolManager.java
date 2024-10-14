package com.example.redrestaurantapp.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
    private final String TAG = "ThreadPool";

    private static ThreadPoolManager mInstance;
    private final ExecutorService mExecutorService;

    public ThreadPoolManager() {
        mExecutorService = Executors.newFixedThreadPool(4);
    }

    public static synchronized ThreadPoolManager getInstance() {
        if(mInstance == null)
            mInstance = new ThreadPoolManager();

        return mInstance;
    }

    public void submitTask(Runnable task){
        mExecutorService.submit(task);
    }

    public void shutdown() {
        if(mExecutorService.isShutdown()) return;

        mExecutorService.shutdown();
    }
}

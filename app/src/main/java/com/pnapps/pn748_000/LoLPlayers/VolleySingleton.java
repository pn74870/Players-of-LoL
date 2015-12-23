package com.pnapps.pn748_000.LoLPlayers;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by pn748_000 on 8/2/2015.
 */
public class VolleySingleton {
    private static VolleySingleton sInstance = null;
    private final ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;

    public static VolleySingleton getInstance() {
        if(sInstance==null)sInstance=new VolleySingleton();
        return sInstance;
    }

    private VolleySingleton() {
        mRequestQueue=Volley.newRequestQueue(MyApplication.getAppContext());
        mImageLoader=new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

       private LruCache<String, Bitmap> cache=new LruCache<>((int)(Runtime.getRuntime().maxMemory()/1024)/8);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

    }
    public RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }
    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

}

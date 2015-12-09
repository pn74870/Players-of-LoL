package com.example.pn748_000.lolinfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.lang.ref.WeakReference;

import static com.example.pn748_000.lolinfo.Keys.PNG;
import static com.example.pn748_000.lolinfo.Keys.PROFILE_ICON;

/**
 * Created by pn748_000 on 11/21/2015.
 */
public class LoadBitmapTask extends AsyncTask<Void,Void,Bitmap> {

    int id;
    WeakReference<ImageView> imageView;
    Context context;
    public LoadBitmapTask(int id,ImageView imageView,Context context){
        this.id=id;
        this.imageView=new WeakReference<>(imageView);
        this.context=context;
    }
    @Override
    protected Bitmap doInBackground(Void... Voids) {
        return Utilities.readBitmapFromSD(PROFILE_ICON+id+PNG,context,48);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {


        if(imageView!=null){
            if(bitmap!=null ){
                imageView.get().setImageBitmap(bitmap);
                Utilities.showLog("loaded image from sd");
            }
            else Utilities.getImage(Utilities.getProfilerIconUrl(id), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap=response.getBitmap();
                    if(bitmap!=null){
                        Utilities.showLog("getting image from net");
                        imageView.get().setImageBitmap(bitmap);
                        SaveBitmapTask task=new SaveBitmapTask(PROFILE_ICON+id+PNG,bitmap,context);
                        task.execute();
                    }}

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
        }}

}


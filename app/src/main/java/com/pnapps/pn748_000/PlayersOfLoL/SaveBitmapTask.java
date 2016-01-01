package com.pnapps.pn748_000.PlayersOfLoL;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;


public class SaveBitmapTask extends AsyncTask<Void,Void,Void> {
    String name;
    Bitmap bitmap;
    Context context;
    public SaveBitmapTask(String name, Bitmap bitmap,Context context){
        this.name=name;
        this.context=context;
        this.bitmap=bitmap;
    }



    @Override
    protected Void doInBackground(Void... voids) {
        Utilities.saveBitmapToSD(bitmap,context,name);
        return null;
    }
}
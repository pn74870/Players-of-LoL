package com.pnapps.pn748_000.PlayersOfLoL;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;


public class SaveBitmapTask extends AsyncTask<Void,Void,Void> {
    String name;
    Bitmap bitmap;
    Context context;
    boolean isExtAvailable;
    public SaveBitmapTask(String name, Bitmap bitmap,Context context){
        this.name=name;
        this.context=context;
        this.bitmap=bitmap;
    }



    @Override
    protected Void doInBackground(Void... voids) {
        isExtAvailable=Utilities.saveBitmapToSD(bitmap,context,name);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(!isExtAvailable) Utilities.showToast("External storage was not found.", context);
    }
}
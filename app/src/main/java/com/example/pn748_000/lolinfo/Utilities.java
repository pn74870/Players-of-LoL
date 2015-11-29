package com.example.pn748_000.lolinfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import static com.example.pn748_000.lolinfo.Keys.API_KEY;
import static com.example.pn748_000.lolinfo.Keys.API_KEY_AND;
import static com.example.pn748_000.lolinfo.Keys.ARG_SUMMONER_OBJECT;
import static com.example.pn748_000.lolinfo.Keys.DDRAGON;
import static com.example.pn748_000.lolinfo.Keys.DDRAGON_SPELL_IMG;
import static com.example.pn748_000.lolinfo.Keys.DDRAGON_SUMMONER_SPELLS;
import static com.example.pn748_000.lolinfo.Keys.ENTRY;
import static com.example.pn748_000.lolinfo.Keys.HTTP;
import static com.example.pn748_000.lolinfo.Keys.ID;
import static com.example.pn748_000.lolinfo.Keys.LEVEL;
import static com.example.pn748_000.lolinfo.Keys.NAME;
import static com.example.pn748_000.lolinfo.Keys.PNG;
import static com.example.pn748_000.lolinfo.Keys.PROFILE_ICON;
import static com.example.pn748_000.lolinfo.Keys.PROFILE_ICON_ID;
import static com.example.pn748_000.lolinfo.Keys.RANKED;
import static com.example.pn748_000.lolinfo.Keys.RECENT;

import static com.example.pn748_000.lolinfo.Keys.URL_BY_NAME;
import static com.example.pn748_000.lolinfo.Keys.URL_CHAMPION;
import static com.example.pn748_000.lolinfo.Keys.URL_CHAMP_ICON;
import static com.example.pn748_000.lolinfo.Keys.URL_ITEMS;
import static com.example.pn748_000.lolinfo.Keys.URL_LEAGUE_BY_ID;
import static com.example.pn748_000.lolinfo.Keys.URL_MATCH_HISTORY;
import static com.example.pn748_000.lolinfo.Keys.URL_START;
import static com.example.pn748_000.lolinfo.Keys.URL_START_GLOBAL;
import static com.example.pn748_000.lolinfo.Keys.URL_SUMMONER_ICON;
import static com.example.pn748_000.lolinfo.Keys.URL_SUMMONER_STATS;
import static com.example.pn748_000.lolinfo.Keys.URL_VERSION;
import static com.example.pn748_000.lolinfo.Keys.URL_VERSION_LIST;

/**
 * Created by pn748_000 on 10/17/2015.
 */
public abstract class Utilities {
   private CustomListener listener;
   ImgLoader imgLoader;
    public Utilities( int id,  ImageView imageView,JSONArray versions){
        imgLoader=new ImgLoader(imageView,id,versions);
        listener=new CustomListener(versions,id,imageView);
    }
    public Utilities(){

    }
    public static double calculateAverage(int quantity,int numberOfGames){
        return (double)(Math.round((double) quantity*10 / numberOfGames))/10;
    }
    public static String formatStringOneAfterDec( double number){
        return String.format(Locale.US,"%.1f",number);
    }
    public static String getRegion(String reg) {
        switch (reg) {
            case "euw":
                return "Europe West";

            case "na":
                return "North America";

            case "eune":
                return "Europe Nordic and East";

            case "lan":
                return "Latin America North";

            case "las":
                return "Latin America South";

            case "oce":
                return "Oceania";

            case "ru":
                return "Russian";

            case "tr":
                return "Turkey";

            default:
                return "";

        }
    }
    public static String getRegionSymbol(String reg) {
        switch (reg) {
            case "Europe West":
                return "euw";

            case "North America":
                return "na";

            case "Europe Nordic and East":
                return "eune";

            case "Latin America North":
                return "lan";

            case "Latin America South":
                return "las";

            case "Oceania":
                return "oce";

            case "Russian":
                return "ru";

            case "Turkey":
                return "tr";

            default:
                return "";

        }
    }
    public static String getProfilerIconUrl(int id){
      return   DDRAGON + MainActivity.version + URL_SUMMONER_ICON + id + PNG;
    }
    public  static String getPlatformID(String region){

        switch (region) {

            case "eune":
                return "EUN1/";

            case "lan":
                return "LA1/";

            case "las":
                return "LA2/";

            case "oce":
                return "OC1/";

            case "ru":
                return "RU/";

            case "kr":
                return "KR/";

            default:
                return region.toUpperCase()+1+"/";


        }
    }

    public static String matchType(String type,String subtype, String mode, int queueConfigId){
        if(subtype.equals("")) {
            switch (queueConfigId){
                case 0: return "CUSTOM";
                case 8: return "NORMAL 3x3";
                case 2: return "NORMAL 5x5 BLIND";
                case 14: return "NORMAL 5x5 DRAFT";
                case 4: return "RANKED SOLO 5x5";
                case 6: return "RANKED PREMADE 5x5*";
                case 9: return "RANKED PREMADE 3x3*";
                case 41: return "RANKED TEAM 3x3";
                case 42: return "RANKED TEAM 5x5";
                case 16: return "ODIN 5x5 BLIND";
                case 17: return "ODIN 5x5 DRAFT";
                case 7: return "BOT 5x5*";
                case 25: return "BOT ODIN 5x5";
                case 31: return "BOT 5x5 INTRO";
                case 32: return "BOT 5x5 BEGINNER";
                case 33: return "BOT 5x5 INTERMEDIATE";
                case 52: return "BOT TT 3x3";
                case 61: return "GROUP FINDER 5x5";
                case 65: return "ARAM 5x5";
                case 70: return "ONEFORALL 5x5";
                case 72: return "FIRSTBLOOD 1x1";
                case 73: return "FIRSTBLOOD 2x2";
                case 75: return "SR 6x6";
                case 76: return "URF 5x5";
                case 83: return "BOT URF 5x5";
                case 91: return "NIGHTMARE BOT 5x5 RANK1";
                case 92: return "NIGHTMARE BOT 5x5 RANK2";
                case 93: return "NIGHTMARE BOT 5x5 RANK5";
                case 96: return "ASCENSION 5x5";
                case 98: return "HEXAKILL";
                case 100: return "BILGEWATER ARAM 5x5";
                case 300: return "KING PORO 5x5";
                case 310: return "COUNTER PICK";
                case 313: return "BILGEWATER 5x5";


            }
        }
        if(type.equals("CUSTOM_GAME")) return "CUSTOM";

        if(mode.equals("CLASSIC")){

        switch (subtype){
            case "CAP_5x5": return "TEAM BUILDER";
            case "ARAM_UNRANKED_5x5": return "ARAM";
            case "ONEFORALL_5x5":return "ONE FOR ALL";


        }
        return subtype.replace('_',' ');}
        return mode;
    }

    public static int stat(JSONObject object,String text)  {

        if(object.has(text) && !object.isNull(text)) try {
            return object.getInt(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
public static void startSummonerActivity(Summoner summoner,Context context) {
    Intent intent = new Intent(context, SummonerActivity.class);
    intent.putExtra(ARG_SUMMONER_OBJECT, summoner);
    context.startActivity(intent);
}


     public void champNameFromId(final int index, Context context, final int champId, String region){
         final SharedPreferences champIdPrefs=context.getSharedPreferences(context.getResources().getString(R.string.champIdPreferences), Context.MODE_PRIVATE);
         String champName = champIdPrefs.getString(champId + "", "");
        showLog("getting champ "+champId);

         if(champName.equals("")) {
             JsonObjectRequest champRequest=new JsonObjectRequest(Request.Method.GET, getChampNameRequestUrl(champId, region), (String) null, new Response.Listener<JSONObject>() {
                 @Override
                 public void onResponse(JSONObject response) {
                     try {

                         final String champion=response.getString("key");
                         champIdPrefs.edit().putString(champId+"",champion).apply();



                        onResponseReceived(index,champion);
                         Log.e("asd", "champion " + champion + " was cached");



                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                   error.printStackTrace();
                 }
             });
             VolleySingleton.getInstance().getmRequestQueue().add(champRequest);
         }
         else  onResponseReceived(index,champName);



     }

    public  abstract void onResponseReceived(int index, String champName);
    public  abstract void onResponseReceived(int index,JSONArray array);
    public static String getChampNameRequestUrl(int id, String region) {
        return HTTP  + URL_START_GLOBAL + region + URL_CHAMPION + id + API_KEY;


    }
    public static String getLeagueRequestUrl(int id, String region) {
        return HTTP + region + URL_START + region + URL_LEAGUE_BY_ID + id + API_KEY;


    }

    public static String getSummonerUrl(String region, String name){
        return HTTP + region + URL_START + region + URL_BY_NAME + name + API_KEY;
    }

    public static void requestJsonObject(String url,Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        VolleySingleton.getInstance().getmRequestQueue().add(new JsonObjectRequest(Request.Method.GET, url, (String) null, responseListener, errorListener));
    }
    public static void requestJsonArray(String url,Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener){
        VolleySingleton.getInstance().getmRequestQueue().add(new JsonArrayRequest(Request.Method.GET, url, (String) null, responseListener, errorListener));
    }
    public static void showLog(String text){
        Log.e("asd", text);
    }
   public static int getIntFromJson(JSONObject object,String name){

       try {
           if(object!=null && object.has(name) && !object.isNull(name))
           return object.getInt(name);

       } catch (JSONException e) {
           e.printStackTrace();
       }
       showLog("value is not found");
       return -1;
   }
    public static String getStringFromJson(JSONObject object,String name){

        try {
            if(object!=null && object.has(name) && !object.isNull(name))
            return object.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean getBooleanFromJson(JSONObject object,String name){

        try {
            if(object!=null && object.has(name) && !object.isNull(name))
            return object.getBoolean(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static JSONObject getJsonObjectFromJson(JSONObject object, String name){
        try {
            if(object!=null && object.has(name) && !object.isNull(name))
            return object.getJSONObject(name);
            else showLog("Couldnt find "+name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONArray getJsonArrayFromJson(JSONObject object, String name){
        try {
            if(object!=null && object.has(name) && !object.isNull(name))
            return object.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
   public  void getLeagueEntry(final int[] ids,String region){

       requestJsonObject(HTTP + region + URL_START+region + URL_LEAGUE_BY_ID + Arrays.toString(ids).replace("[","").replace("]","").replace(" ","") + ENTRY + API_KEY, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               for(int i=0; i<ids.length;i++){
               JSONArray array=getJsonArrayFromJson(response,ids[i]+"");
               onResponseReceived(i,array);}
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               error.printStackTrace();
           }
       });

   }

    public static int getTierImage(String tier, String division) {
        int[] tiers = {R.drawable.bronze_v, R.drawable.bronze_iv, R.drawable.bronze_iii, R.drawable.bronze_ii, R.drawable.bronze_i,
                R.drawable.silver_v, R.drawable.silver_iv, R.drawable.silver_iii, R.drawable.silver_ii, R.drawable.silver_i,
                R.drawable.gold_v, R.drawable.gold_iv, R.drawable.gold_iii, R.drawable.gold_ii, R.drawable.gold_i,
                R.drawable.platinum_v, R.drawable.platinum_iv, R.drawable.platinum_iii, R.drawable.platinum_ii, R.drawable.platinum_i,
                R.drawable.diamond_v, R.drawable.diamond_iv, R.drawable.diamond_iii, R.drawable.diamond_ii, R.drawable.diamond_i,
                R.drawable.master, R.drawable.challenger};

        return tiers[getRankScore(tier,division)];
    }
    public static Summoner createSummonerObject(JSONObject object,String region){

        return new Summoner(getStringFromJson(object,NAME),getIntFromJson(object, ID),getIntFromJson(object,PROFILE_ICON_ID),getIntFromJson(object,LEVEL),region);

    }
    public static int getRankScore(String tier, String division) {

        int rankNumb = 0;
        boolean pro=false;
        switch (tier) {
            case "SILVER":
                rankNumb = 5;
                break;
            case "GOLD":
                rankNumb = 10;
                break;
            case "PLATINUM":
                rankNumb = 15;
                break;
            case "DIAMOND":
                rankNumb = 20;
                break;
            case "MASTER":
                pro=true;
                rankNumb = 25;
                break;
            case "CHALLENGER":
                pro=true;
                rankNumb = 26;
                break;
        }
        if(!pro){
            switch (division) {

                case "IV":
                    rankNumb += 1;
                    break;
                case "III":
                    rankNumb += 2;
                    break;
                case "II":
                    rankNumb += 3;
                    break;
                case "I":
                    rankNumb += 4;
                    break;
            } }
        return rankNumb;
    }

    public static String getChampImg(String name){
        return DDRAGON + MainActivity.version + URL_CHAMP_ICON + name + PNG;
    }

   public static String getStatsUrl(int id,String region){
       return HTTP+region+URL_START+region+URL_SUMMONER_STATS+id+RANKED+API_KEY;
   }
 public static void getImage(String url,ImageLoader.ImageListener imageListener){
     VolleySingleton.getInstance().getImageLoader().get(url, imageListener);
 }

    public  void setItemImage( int id, ImageView imageView, String version,boolean isItem){
        String type;
        if(isItem) type="item";
        else type="summonerSpell";
        Bitmap bitmap=MainActivity.mMemoryCache.get(type+id);
        if(bitmap!=null) imageView.setImageBitmap(bitmap);
        else {
            if(isItem)getImage(getItemImgUrl(id, version), imgLoader);
            else requestJsonObject(DDRAGON + version + DDRAGON_SUMMONER_SPELLS, listener, listener);
        }}
 public void findSummSpells(int id,String version,ImageView imageView){
     setItemImage(id, imageView, version, false);
     showLog("trying find summ spell in ver "+version);
 }

    public static String getItemImgUrl(int id, String version){
        return DDRAGON+version+URL_ITEMS+id+PNG;
    }
    public static String getVersionUrl(String region){
        return HTTP+URL_START_GLOBAL+region+URL_VERSION+API_KEY;
    }
    public static void cacheImage(String key,Bitmap bitmap){
        if (MainActivity.mMemoryCache.get(key) == null && key!=null && bitmap!=null) {

            MainActivity.mMemoryCache.put(key, bitmap);
        }
    }
    public static void showToast(String text, Context context){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    public static void saveBitmapToSD(Bitmap bitmap,Context context,String name){
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File folder=context.getExternalCacheDir();
            File file=new File(folder,name);
            FileOutputStream outputStream=null;
            try {
                outputStream=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                if(outputStream!=null) try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else showToast("External storage was not found.",context);
    }
    public static Bitmap readBitmapFromSD(String name,Context context){
        Bitmap bitmap=null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            File file=new File(context.getExternalCacheDir(),name);
            bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
            showLog("getting bmp from "+file.getAbsolutePath());
        }
        else showLog("cant access SD");
        return bitmap;
    }

    class ImgLoader implements ImageLoader.ImageListener {
        ImageView imageView;
        int id, versionIndex=-4;
        JSONArray versions;


        public ImgLoader(ImageView imgView, int idd,JSONArray vers){
            imageView=imgView;
            id=idd;
            versions=vers;
        }


        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            Bitmap bitmap=response.getBitmap();
            imageView.setImageBitmap(bitmap);
            cacheImage("item" + id, bitmap);


        }

        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse=error.networkResponse;
            if(networkResponse!=null && networkResponse.data!=null){
                if(networkResponse.statusCode==404 &&versions!=null&& versionIndex+4<versions.length()){

                    versionIndex+=4;
                    try {
                      setItemImage(id, imageView, versions.getString(versionIndex),true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showLog("resolvig error of id "+id+" "+versionIndex+ " nth");
                }
            }
            error.printStackTrace();

        }



}
    class CustomListener implements Response.Listener<JSONObject>,Response.ErrorListener{
        JSONArray versions;
        int id,versionIndex=-4;
        ImageView imageView;
        String name;

        CustomListener(JSONArray versions, int id,ImageView imageView){
            this.versions=versions;
            this.id=id;
            this.imageView=imageView;

        }

        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }

        @Override
        public void onResponse(JSONObject response) {
            JSONObject spells=getJsonObjectFromJson(response,"data");
            if(spells!=null) {
                Iterator<String> keys=spells.keys();
                while (keys.hasNext()){
                    JSONObject spell=getJsonObjectFromJson(spells,keys.next());
                    if(getStringFromJson(spell,"key").equals(id+""))name=getStringFromJson(spell,"id");

                }
                if(name==null && versionIndex+4<versions.length()) {
                    versionIndex+=4;
                    try {

                        findSummSpells(id,versions.getString(versionIndex),imageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        getImage(DDRAGON + versions.getString(versionIndex) + DDRAGON_SPELL_IMG + name + PNG, new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                Bitmap bitmap = response.getBitmap();
                                imageView.setImageBitmap(bitmap);
                                cacheImage("summonerSpell" + id, bitmap);
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

}


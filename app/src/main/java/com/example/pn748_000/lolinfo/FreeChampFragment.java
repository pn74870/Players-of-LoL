package com.example.pn748_000.lolinfo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.pn748_000.lolinfo.Keys.API_KEY;

import static com.example.pn748_000.lolinfo.Keys.DDRAGON;
import static com.example.pn748_000.lolinfo.Keys.HTTP;
import static com.example.pn748_000.lolinfo.Keys.JPG0;
import static com.example.pn748_000.lolinfo.Keys.URL_CHAMPION;
import static com.example.pn748_000.lolinfo.Keys.URL_CHAMP_ICON;
import static com.example.pn748_000.lolinfo.Keys.URL_CHAMP_LOADING;
import  static com.example.pn748_000.lolinfo.Keys.URL_FREE_CHAMPS;
import static com.example.pn748_000.lolinfo.Keys.URL_START_GLOBAL;
//import static com.example.pn748_000.lolinfo.Keys.VERSION;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link FreeChampFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FreeChampFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
   ImageView[] champions;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ImageLoader imageLoader;
    private RequestQueue requestQueue;
    VolleySingleton volleySingleton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FreeChampFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FreeChampFragment newInstance(String param1, String param2) {
        FreeChampFragment fragment = new FreeChampFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FreeChampFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("asd","creating freechamp fragment");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        volleySingleton=VolleySingleton.getInstance();
        requestQueue = volleySingleton.getmRequestQueue();
        imageLoader=volleySingleton.getImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_free_champ, container, false);
       champions=new ImageView[10];
        champions[0]= (ImageView) view.findViewById(R.id.champ1);
        champions[1]= (ImageView) view.findViewById(R.id.champ2);
        champions[2]= (ImageView) view.findViewById(R.id.champ3);
        champions[3]= (ImageView) view.findViewById(R.id.champ4);
        champions[4]= (ImageView) view.findViewById(R.id.champ5);
        champions[5]= (ImageView) view.findViewById(R.id.champ6);
        champions[6]= (ImageView) view.findViewById(R.id.champ7);
        champions[7]= (ImageView) view.findViewById(R.id.champ8);
        champions[8]= (ImageView) view.findViewById(R.id.champ9);
        champions[9]= (ImageView) view.findViewById(R.id.champ10);
        getFreeChampions();

        // Inflate the layout for this fragment
        return view;
    }







 public void getFreeChampions(){
     JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, URL_FREE_CHAMPS, (String) null, new Response.Listener<JSONObject>() {
         @Override
         public void onResponse(JSONObject response) {
             try {
                 JSONArray champArray=response.getJSONArray("champions");
                 for(int i=0; i<champArray.length(); i++) {
                     final int numb=i;
                     JSONObject champ=champArray.getJSONObject(i);
                     final int id=champ.getInt("id");
                     final SharedPreferences champIdPreferences= getActivity().getSharedPreferences(getString(R.string.champIdPreferences), Context.MODE_PRIVATE);
                     String champName=champIdPreferences.getString(id + "", "");
                     if(champName.equals("")){

JsonObjectRequest champRequest=new JsonObjectRequest(Request.Method.GET, HTTP + URL_START_GLOBAL + "euw" + URL_CHAMPION + id + API_KEY, (String) null, new Response.Listener<JSONObject>() {
    @Override
    public void onResponse(JSONObject response) {
        try {
             String name=response.getString("key");
             champIdPreferences.edit().putString(id+"",name).apply();
            Log.e("asd", "champion " + name + " was cached");
             loadImage(name,numb);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
    }
});
                     requestQueue.add(champRequest);

                 }
                 else loadImage(champName,numb);}
             } catch (JSONException e) {
                 e.printStackTrace();
             }
         }
     }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {
        Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
         }
     });
     requestQueue.add(request);
 }

    void loadImage(String name, int n){
        final int numb=n;
        imageLoader.get(URL_CHAMP_LOADING + name + JPG0, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                champions[numb].setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MyApplication.getAppContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

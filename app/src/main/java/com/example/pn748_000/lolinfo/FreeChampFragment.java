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
import android.widget.TextView;
import android.widget.Toast;

import static com.example.pn748_000.lolinfo.Keys.API_KEY;

import static com.example.pn748_000.lolinfo.Keys.ARG_REGION;
import static com.example.pn748_000.lolinfo.Keys.DDRAGON;
import static com.example.pn748_000.lolinfo.Keys.HTTP;
import static com.example.pn748_000.lolinfo.Keys.JPG0;
import static com.example.pn748_000.lolinfo.Keys.URL_CHAMPION;
import static com.example.pn748_000.lolinfo.Keys.URL_CHAMP_ICON;
import static com.example.pn748_000.lolinfo.Keys.URL_CHAMP_LOADING;
import static com.example.pn748_000.lolinfo.Keys.URL_FREE_CHAMPS;
import static com.example.pn748_000.lolinfo.Keys.URL_START_GLOBAL;
//import static com.example.pn748_000.lolinfo.Keys.VERSION;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.pn748_000.lolinfo.Utilities.getImage;
import static com.example.pn748_000.lolinfo.Utilities.requestJsonObject;
import static com.example.pn748_000.lolinfo.Utilities.showToast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p/>
 * to handle interaction events.
 * Use the {@link FreeChampFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FreeChampFragment extends Fragment {
    //private TextView champsTxt;
    private ImageView[] champions;
    private String region;
   // private StringBuffer buffer=new StringBuffer();
    private FreeChampClickListener listener;
    interface FreeChampClickListener{
        void onFreeChampClicked();
    }
    public static FreeChampFragment newInstance(String region) {
        FreeChampFragment fragment = new FreeChampFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REGION, region);
        fragment.setArguments(args);
        return fragment;
    }

    public FreeChampFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("asd", "creating freechamp fragment");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            region = getArguments().getString(ARG_REGION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_free_champ, container, false);
        champions = new ImageView[10];
        champions[0] = (ImageView) view.findViewById(R.id.champ1);
        champions[1] = (ImageView) view.findViewById(R.id.champ2);
        champions[2] = (ImageView) view.findViewById(R.id.champ3);
        champions[3] = (ImageView) view.findViewById(R.id.champ4);
        champions[4] = (ImageView) view.findViewById(R.id.champ5);
        champions[5] = (ImageView) view.findViewById(R.id.champ6);
        champions[6] = (ImageView) view.findViewById(R.id.champ7);
        champions[7] = (ImageView) view.findViewById(R.id.champ8);
        champions[8] = (ImageView) view.findViewById(R.id.champ9);
        champions[9] = (ImageView) view.findViewById(R.id.champ10);
     //   champsTxt = (TextView) view.findViewById(R.id.champsTxt);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFreeChampClicked();
            }
        });
        getFreeChampions();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener= (FreeChampClickListener) context;
        }
        catch (ClassCastException e){
           throw new ClassCastException(context.toString()+" must implement FreeChampClickListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }
    public void getFreeChampions() {
        requestJsonObject(URL_FREE_CHAMPS, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray champArray = response.getJSONArray("champions");
                    for (int i = 0; i < champArray.length(); i++) {
                        final int numb = i;
                        JSONObject champ = champArray.getJSONObject(i);
                        final int id = champ.getInt("id");
                        final SharedPreferences champIdPreferences = getActivity().getSharedPreferences(getString(R.string.champIdPreferences), Context.MODE_PRIVATE);
                        String champName = champIdPreferences.getString(id + "", "");
                        if (champName.equals("")) {
                            requestJsonObject(HTTP + URL_START_GLOBAL + region + URL_CHAMPION + id + API_KEY, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String name = response.getString("key");
                                        champIdPreferences.edit().putString(id + "", name).apply();
                                        Log.e("asd", "champion " + name + " was cached");
                                        loadImage(name, numb);

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


                        } else loadImage(champName, numb);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(error instanceof NetworkError){
                    showToast("No internet connection", getActivity());
                    listener.onFreeChampClicked();
            }}
        });

    }

    void loadImage(String name, int n) {
       // buffer.append(name+"\n");
        final int numb = n;
        getImage(URL_CHAMP_LOADING + name + JPG0, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                champions[numb].setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
      //  if(n==9) champsTxt.setText(buffer);

    }
}

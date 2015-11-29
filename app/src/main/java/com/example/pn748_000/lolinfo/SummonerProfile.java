package com.example.pn748_000.lolinfo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import static com.example.pn748_000.lolinfo.Keys.PROFILE_ICON;
import static com.example.pn748_000.lolinfo.Keys.PNG;
//import static com.example.pn748_000.lolinfo.Keys.VERSION;


public class SummonerProfile extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REGION = "region";
    private static final String ARG_SUMMONER= "summoner";


    // TODO: Rename and change types of parameters

    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    private OnSummonerProfileInteractionListener mListener;
    TextView nameTextView, levelTxt,regionTxt;
    ImageView profileIconImageView;
    LinearLayout leagueLayout;
    TextView[] ranks,lpTxts;
    ImageView[] tierIcons;
    private Summoner summoner;


    public static SummonerProfile newInstance(Summoner summoner) {
        SummonerProfile fragment = new SummonerProfile();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SUMMONER,summoner);
        fragment.setArguments(args);
        return fragment;
    }


    public SummonerProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.showLog("onCreate summ prof");
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getmRequestQueue();
        if(getArguments()!=null)
        summoner=getArguments().getParcelable(ARG_SUMMONER);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.summoner_profile, container, false);

        ranks=new TextView[3];
        ranks[0]= (TextView) view.findViewById(R.id.rank_text_3v3);
        ranks[1]= (TextView) view.findViewById(R.id.rank_text);
        ranks[2]= (TextView) view.findViewById(R.id.rank_text_5v5_t);
        lpTxts=new TextView[3];
        lpTxts[0]= (TextView) view.findViewById(R.id.lp_3v3);
        lpTxts[1]= (TextView) view.findViewById(R.id.lp_5v5);
        lpTxts[2]= (TextView) view.findViewById(R.id.lp_5v5_t);
        tierIcons=new ImageView[3];
        tierIcons[0]= (ImageView) view.findViewById(R.id.img_3v3);
        tierIcons[1]= (ImageView) view.findViewById(R.id.img_5v5);
        tierIcons[2]= (ImageView) view.findViewById(R.id.img_5v5_t);
        profileIconImageView = (ImageView) view.findViewById(R.id.icon_container);
        leagueLayout= (LinearLayout) view.findViewById(R.id.leaguesLayout);
  /*      editText = (EditText) view.findViewById(R.id.text_field);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_SEARCH){
                    getSummonerProfile();
                    return true;
                }
                return false;
            }
        });
     //   button = (ImageButton) view.findViewById(R.id.search_btn);
     //
        /*
        region = "euw";
        regionButton= (Button) view.findViewById(R.id.region_button);
        regionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(getActivity(),view);
                popupMenu.setOnMenuItemClickListener(summonerProfile);
                MenuInflater inflater=popupMenu.getMenuInflater();
                inflater.inflate(R.menu.region_menu,popupMenu.getMenu());
                popupMenu.show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             getSummonerProfile();
            }
        }); */
        regionTxt = (TextView) view.findViewById(R.id.region_text);
        levelTxt = (TextView) view.findViewById(R.id.level_text);
        nameTextView = (TextView) view.findViewById(R.id.name_text);
        getSummonerProfile();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void sendSummonerNameToActivity(String name) {
        if (mListener != null) {
            mListener.onSummonerFound(name);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSummonerProfileInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    public interface OnSummonerProfileInteractionListener {
        // TODO: Update argument type and nameTextView
         void onSummonerFound(String name);
    }



    public void getSummonerProfile(){
        int level=summoner.level;
        int id=summoner.id;
        final int iconId=summoner.iconId;
        final String region=summoner.region;
        final String name=summoner.name;
  /*      if (editText.getText().toString().length() > 2) {
            final View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            summoner = editText.getText().toString().replace(" ","");

            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getRequestUrl(summoner.replace(" ","%20"), region, 0), (String) null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("asd", "successful request");
                    Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();

                   try {
                        JSONObject jsonObject = response.getJSONObject(summoner);
              */        nameTextView.setText(name);
                        levelTxt.setText("Level: " + level);
               //         onIdReceived(id,region,version);
                        regionTxt.setText(Utilities.getRegion(region));
                        leagueLayout.setAlpha(1);
        for(int i=0;i<3;i++){
            tierIcons[i].setImageResource(R.drawable.provisional);
            ranks[i].setText("Unranked");
            lpTxts[i].setText("");
        }
                        Utilities utilities=new Utilities() {
                            @Override
                            public void onResponseReceived(int arguments, String champName) {

                            }

                            @Override
                            public void onResponseReceived(int in,JSONArray array) {
                                showLog("league received");
                                int[] scores = {0, 0, 0};
                                int[] bestIndexes = {-1, -1, -1};
                                String[] queues = {"RANKED_TEAM_3x3", "RANKED_SOLO_5x5", "RANKED_TEAM_5x5"};

                                try {
                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject entry = array.getJSONObject(i);
                                        int type = -1;
                                        for (int j = 0; j < 3; j++){
                                            if (entry.getString("queue").equals(queues[j]))
                                                type = j;}
                                          int score=Utilities.getRankScore(entry.getString("tier"),
                                                  entry.getJSONArray("entries").getJSONObject(0).getString("division"));
                                        if (score > scores[type]){
                                            bestIndexes[type] = i;
                                            scores[type]=score;
                                        }


                                    }
                                    for (int i = 0; i < 3; i++) {

                                        if(bestIndexes[i]>-1) {
                                        String tier = array.getJSONObject(bestIndexes[i]).getString("tier"), division = array.getJSONObject(bestIndexes[i]).getJSONArray("entries").getJSONObject(0).getString("division");

                                        showLog(tier+" "+division);

                                        ranks[i].setText(tier + " " + division);
                                        tierIcons[i].setImageResource(Utilities.getTierImage(tier, division));
                                        lpTxts[i].setText(array.getJSONObject(bestIndexes[i]).getJSONArray("entries").getJSONObject(0).getInt("leaguePoints")
                                                + " League Points\n"+array.getJSONObject(bestIndexes[i]).getJSONArray("entries").getJSONObject(0).getInt("wins")+" wins "
                                                +array.getJSONObject(bestIndexes[i]).getJSONArray("entries").getJSONObject(0).getInt("losses")+" losses ");
                                    }}
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        };
                        int[] ids={id};
                        utilities.getLeagueEntry(ids, region);
                        LoadBitmapTask loadBitmapTask=new LoadBitmapTask(iconId,profileIconImageView,getActivity());
                        loadBitmapTask.execute();


                 /*   } catch (JSONException e) {
                        Log.e("asd", e.toString());
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("asd","critical error");
                    if (error.toString().equals("com.android.volley.ServerError"))
                        Toast.makeText(getActivity(), "Summoner was not found", Toast.LENGTH_SHORT);
                    else
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();

                }
            });
            requestQueue.add(request);
        }*/
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }



}

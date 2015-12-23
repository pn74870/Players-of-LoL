package com.pnapps.pn748_000.LoLPlayers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import static com.example.pn748_000.lolinfo.Keys.VERSION;


public class SummonerProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REGION = "region";
    private static final String ARG_SUMMONER = "summoner";


    // TODO: Rename and change types of parameters


    private OnSummonerProfileInteractionListener mListener;
    private TextView nameTextView, levelTxt, regionTxt;
    private ImageView profileIconImageView;
    private LinearLayout leagueLayout;
    private TextView[] ranks, lpTxts, wlTexts;
    private ImageView[] tierIcons;
    private ImageView[][] promos=new ImageView[3][];
    private Summoner summoner;
    private LinearLayout[] favChamps;

    public static SummonerProfile newInstance(Summoner summoner) {
        SummonerProfile fragment = new SummonerProfile();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SUMMONER, summoner);
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

        if (getArguments() != null)
            summoner = getArguments().getParcelable(ARG_SUMMONER);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.summoner_profile, container, false);
        favChamps=new LinearLayout[]{
                (LinearLayout) view.findViewById(R.id.favouriteChamp1),
                (LinearLayout) view.findViewById(R.id.favouriteChamp2),
                (LinearLayout) view.findViewById(R.id.favouriteChamp3),
                (LinearLayout) view.findViewById(R.id.favouriteChamp4)};
        ranks = new TextView[]{
                (TextView) view.findViewById(R.id.rank_text_3v3),
                (TextView) view.findViewById(R.id.rank_text),
                (TextView) view.findViewById(R.id.rank_text_5v5_t)};
        lpTxts = new TextView[]{
                (TextView) view.findViewById(R.id.lp_3v3),
                (TextView) view.findViewById(R.id.lp_5v5),
                (TextView) view.findViewById(R.id.lp_5v5_t)};
        tierIcons = new ImageView[]{
                (ImageView) view.findViewById(R.id.img_3v3),
                (ImageView) view.findViewById(R.id.img_5v5),
                (ImageView) view.findViewById(R.id.img_5v5_t)};
        wlTexts = new TextView[]{
                (TextView) view.findViewById(R.id.wins_loses_3v3),
                (TextView) view.findViewById(R.id.wins_loses_5v5),
                (TextView) view.findViewById(R.id.wins_loses_5v5_t)};
        View[] promosViews=new View[]{view.findViewById(R.id.promos_3v3),view.findViewById(R.id.promos_5v5),view.findViewById(R.id.promos_5v5_t)};
        for(int i=0;i<3;i++) promos[i]=new ImageView[]{
                (ImageView) promosViews[i].findViewById(R.id.promo1),
                (ImageView) promosViews[i].findViewById(R.id.promo2),
                (ImageView) promosViews[i].findViewById(R.id.promo3),
                (ImageView) promosViews[i].findViewById(R.id.promo4),
                (ImageView) promosViews[i].findViewById(R.id.promo5)};
        profileIconImageView = (ImageView) view.findViewById(R.id.icon_container);
        leagueLayout = (LinearLayout) view.findViewById(R.id.leaguesLayout);

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
    protected void setFavouriteChamp(int champNumber, Bitmap champImg, int games){
        ImageView imgView= (ImageView) favChamps[champNumber].findViewById(R.id.favouriteChampImg);
        imgView.setImageBitmap(champImg);
        TextView textView= (TextView) favChamps[champNumber].findViewById(R.id.favouriteChampTxt);
        textView.setText(String.format("%s games",games));
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void sendSummonerNameToActivity(String name) {
        if (mListener != null) {
            mListener.onSummonerFound(name);
        }
    }

    @Override
    public void onAttach(Context activity) {
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


    public void getSummonerProfile() {
        int level = summoner.level;
        int id = summoner.id;
        final int iconId = summoner.iconId;
        final String region = summoner.region;
        final String name = summoner.name;
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
              */
        nameTextView.setText(name);
        levelTxt.setText("Level: " + level);
        //         onIdReceived(id,region,version);
        regionTxt.setText(Utilities.getRegion(region));
        leagueLayout.setAlpha(1);
        for (int i = 0; i < 3; i++) {
            tierIcons[i].setImageResource(R.drawable.provisional);
            ranks[i].setText("Unranked");
            lpTxts[i].setText("");
        }
        Utilities utilities = new Utilities() {
            @Override
            public void onResponseReceived(int arguments, String champName) {

            }

            @Override
            public void onResponseReceived(int in, JSONArray array) {
                showLog("league received");
                int[] scores = {0, 0, 0};
                int[] bestIndexes = {-1, -1, -1};
                String[] queues = {"RANKED_TEAM_3x3", "RANKED_SOLO_5x5", "RANKED_TEAM_5x5"};
                if(array!=null){
                try {
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject entry = array.getJSONObject(i);
                        int type = -1;
                        for (int j = 0; j < 3; j++) {
                            if (entry.getString("queue").equals(queues[j]))
                                type = j;
                        }
                        int score = Utilities.getRankScore(entry.getString("tier"),
                                entry.getJSONArray("entries").getJSONObject(0).getString("division"));
                        if (score > scores[type]) {
                            bestIndexes[type] = i;
                            scores[type] = score;
                        }


                    }
                    for (int i = 0; i < 3; i++) {

                        if (bestIndexes[i] > -1) {
                            String tier = array.getJSONObject(bestIndexes[i]).getString("tier"), division = array.getJSONObject(bestIndexes[i]).getJSONArray("entries").getJSONObject(0).getString("division");

                            showLog(tier + " " + division);

                            ranks[i].setText(tier + " " + division);
                            tierIcons[i].setImageResource(Utilities.getTierImage(tier, division));
                            JSONObject entry=array.getJSONObject(bestIndexes[i]).getJSONArray("entries").getJSONObject(0);
                            lpTxts[i].setText(entry.getInt("leaguePoints")
                                    + " League Points");
                            wlTexts[i].setText(entry.getInt("wins")+" wins " + entry.getInt("losses")+" losses");
                            if(entry.has("miniSeries")){
                               JSONObject series =getJsonObjectFromJson(entry,"miniSeries");
                               if(series!=null){
                                   String progress=series.getString("progress");
                                   char[] chars=progress.toCharArray();
                                   for(int j=0;j<chars.length;j++){
                                       switch (chars[j]){
                                           case 'L':
                                               promos[i][j].setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.red_cross_18dp));
                                               break;
                                           case 'W':
                                               promos[i][j].setImageDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.green_tick_18dp));
                                               break;
                                       }
                                   }
                               }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }}
            }


        };
        int[] ids = {id};
        utilities.getLeagueEntry(ids, region);
        LoadBitmapTask loadBitmapTask = new LoadBitmapTask(iconId, profileIconImageView, getActivity());
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




}

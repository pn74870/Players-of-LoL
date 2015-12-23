package com.pnapps.pn748_000.LoLPlayers;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pnapps.pn748_000.LoLPlayers.Keys.PROFILE_ICON_ID;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.getChampImg;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.getImage;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.getJsonArrayFromJson;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.showLog;

/**
 * Created by pn748_000 on 12/3/2015.
 */
public class ActiveMatchScrollFragment extends Fragment {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_REGION = "regionpar";
        private static final String ARG_PARTICIPANTS = "partcpar";
        private Summoner summoner;
        private String[] bansB=new String[3],bansP=new String[3];
        private ImageView[] bansChampBlue=new ImageView[3];
        private ImageView[] bansChampPurple=new ImageView[3];
        private int numbB=0;
        private int numbP=0;
        // TODO: Rename and change types of parameters
        private String region, platformID;

        private TextView type,gameStart;

        LinearLayout blueLayout,purpleLayout;
        Utilities utilities,utilitiesForBans;
        ArrayList<Player> playersList;

        int[] ids;
        JSONObject activeMatchJson;
        private ArrayList<Integer> idsList=new ArrayList<>();
    private boolean champsReceived=false, bansReceivedB=false,bansReceivedP=false,settingData=false;
    // TODO: Rename and change types and number of parameters
        public static ActiveMatchScrollFragment newInstance(String region, String participants) {
            ActiveMatchScrollFragment fragment = new ActiveMatchScrollFragment();
            Bundle args = new Bundle();
            args.putString(ARG_REGION, region);
            args.putString(ARG_PARTICIPANTS, participants);
            fragment.setArguments(args);
            return fragment;
        }

        public ActiveMatchScrollFragment() {
            // Required empty public constructor
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                region = getArguments().getString(ARG_REGION);
                platformID = Utilities.getPlatformID(region);
                try {
                    activeMatchJson = new JSONObject(getArguments().getString(ARG_PARTICIPANTS));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            playersList = new ArrayList<>();
            utilitiesForBans=new Utilities() {
                @Override
                public void onResponseReceived(final int index, final String champName) {
                    showLog("getting image from " + getChampImg(champName));
                    if(index==100){
                        if(numbB<bansB.length){
                            bansB[numbB]=champName;
                            numbB++;}
                        if(numbB==bansB.length) {
                            bansReceivedB=true;
                            if(!settingData&&bansReceivedP&&champsReceived) setData();
                        }
                    }
                    else if(index==200){
                        if(numbP<bansP.length){
                        bansP[numbP]=champName;
                        numbP++;}
                        if(numbP==bansP.length) {
                            bansReceivedP=true;
                            if(!settingData&&bansReceivedB&&champsReceived) setData();
                        }
                    }
                }

                @Override
                public void onResponseReceived(int index, JSONArray array) {
                    showLog("this should never happen");
                }
            };





            utilities = new Utilities() {
                @Override
                public void onResponseReceived(int index, String champName) {
                    showLog("response received 1");
                    champNameReceived(champName, index);

                }

                @Override
                public void onResponseReceived(int index, JSONArray array) {
                    showLog("response received 2");
                    try {
                        onArrayReceived(array, index);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.active_match_scroll, container, false);
            type = (TextView) view.findViewById(R.id.matchType);
            gameStart= (TextView) view.findViewById(R.id.gameStart);

            purpleLayout= (LinearLayout) view.findViewById(R.id.purpleContainer);
            blueLayout= (LinearLayout) view.findViewById(R.id.blueContainer);
            int[] bannedChampBlue={R.id.bannedChamp1,R.id.bannedChamp2,R.id.bannedChamp3};
            int[] bannedChampPurple={R.id.bannedChamp4,R.id.bannedChamp5,R.id.bannedChamp6};
            for(int i=0;i<3;i++){
                bansChampBlue[i]= (ImageView) view.findViewById(bannedChampBlue[i]);
                bansChampPurple[i]= (ImageView) view.findViewById(bannedChampPurple[i]);
            }
            return view;
        }

      protected void getActiveMatch(JSONObject activeMatchObject, String region) {

            showLog("getActiveMatch was called");
            this.region = region;
            JSONArray participants = getJsonArrayFromJson(activeMatchObject, "participants");


            JSONArray bannedChamps=getJsonArrayFromJson(activeMatchObject,"bannedChampions");
            if(bannedChamps!=null)
                if(bannedChamps.length()>0)
            for(int i=0;i<bannedChamps.length();i++){
                try {
                    JSONObject object=bannedChamps.getJSONObject(i);
                    showLog("team id for banned champ " + object.getInt("teamId"));
                   utilitiesForBans.champNameFromId(object.getInt("teamId"), getActivity(),object.getInt("championId"),region);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
                else {
                    bansReceivedB=true;
                    bansReceivedP=true;
                }
            if (participants != null) {

                Log.e("asd", "participants: " + participants.length());
                type.setTextSize(20);
                type.setText(Utilities.matchType("", "", "", Utilities.stat(activeMatchObject, "gameQueueConfigId")));
                try {
                    Date date=new Date(activeMatchObject.getLong("gameStartTime"));
                    gameStart.setText( DateFormat.getTimeInstance().format(date));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ids = new int[participants.length()];

                for (int i = 0; i < participants.length(); i++) {
                    showLog("participants loop "+i);
                    try {


                        JSONObject player = participants.getJSONObject(i);


                        final Player playerObj = new Player();
                        ids[i] = player.getInt("summonerId");
                        playerObj.name = player.getString("summonerName");
                        final int champID = player.getInt("championId");
                        playerObj.blueTeam = player.getInt("teamId") == 100;
                        playerObj.profileId = player.getInt(PROFILE_ICON_ID);
                        playerObj.id = ids[i];
                        //  idsList.add(new Integer(ids[i]));
       /*                         requestJsonObject(getStatsUrl(ids[i], region), new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONArray champions = response.getJSONArray("champions");
                                            for (int i = 0; i < champions.length(); i++) {
                                                JSONObject champion = champions.getJSONObject(i);
                                                if (champion.getInt("id") == champID) {
                                                    JSONObject stats = champion.getJSONObject("stats");
                                                    int games = stats.getInt("totalSessionsPlayed");
                                                    playerObj.games = games;
                                                    playerObj.k = Math.round((float) stats.getInt("totalChampionKills") / games * 10) / 10;
                                                    playerObj.d = Math.round((float) stats.getInt("totalDeathsPerSession") / games * 10) / 10;
                                                    playerObj.a = Math.round((float) stats.getInt("totalAssists") / games * 10) / 10;

                                                }
                                            }
                                            playersList.set(in, playerObj);
                                            utilities.champNameFromId(in, getActivity(), champID, region);
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
*/
                        playersList.add(playerObj);
                        utilities.champNameFromId(i, MyApplication.getAppContext(), champID, region);

                        //TODO runes and masteries

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        }

        void champNameReceived(final String name, final int index) {
            showLog("champ name received");
            playersList.get(index).champ = name;
            if (index == ids.length - 1) utilities.getLeagueEntry(ids, region);

        }

        void onArrayReceived(JSONArray jsonArray, int in) throws JSONException {
            showLog("array received");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject entry = jsonArray.getJSONObject(i);
                    if (entry.getString("queue").equals("RANKED_SOLO_5x5")) {
                        Player player = playersList.get(in);
                        player.unranked = false;
                        player.tier = entry.getString("tier");
                        JSONObject stats = entry.getJSONArray("entries").getJSONObject(0);
                        if (player.tier.equals("MASTER") || player.tier.equals("CHALLENGER"))
                            player.division = "";
                        else player.division = stats.getString("division");
                        player.wins = stats.getInt("wins");
                        player.loses = stats.getInt("losses");
                        player.lp = stats.getInt("leaguePoints");
                        break;
                    }
                }
            }
            showLog("the length of the list " + playersList.size());

            if (in+1==playersList.size()){
                champsReceived=true;

                setData();
            }
        }
    private void setData(){
        showLog("set data was called champsreceived "+champsReceived+" bans received "+bansReceivedP+" "+ bansReceivedB);
        if(champsReceived&&bansReceivedB&&bansReceivedP){
        settingData=true;

        for(Player player:playersList){
            View v;
            if(player.blueTeam) {
                v=LayoutInflater.from(getActivity()).inflate(R.layout.player_row_purple,blueLayout,false);
                blueLayout.addView(v);
                }
            else {
                v=LayoutInflater.from(getActivity()).inflate(R.layout.player_row_purple,purpleLayout,false);
                purpleLayout.addView(v);
            }
            final LinearLayout buttonPanel= (LinearLayout) v.findViewById(R.id.buttonsPanel);
            RelativeLayout background = (RelativeLayout) v.findViewById(R.id.playerParentLayout);
            background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonPanel.setVisibility(buttonPanel.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
                }
            });
            TextView  name = (TextView) v.findViewById(R.id.summonerName);
            TextView  champName = (TextView) v.findViewById(R.id.champName);
            TextView  numberPlayed = (TextView) v.findViewById(R.id.numberPlayed);
            TextView  winsLoses = (TextView) v.findViewById(R.id.wins_loses);
            TextView   kda = (TextView) v.findViewById(R.id.kda);
            TextView rank = (TextView) v.findViewById(R.id.rank);
            TextView  lp = (TextView) v.findViewById(R.id.lp);
            final ImageView  champIcon = (ImageView) v.findViewById(R.id.champIcon);
            ImageView  tierIcon = (ImageView) v.findViewById(R.id.tierIcon);
            champName.setText(player.champ);
            name.setText(player.name);
            if (player.blueTeam) {
                if (Build.VERSION.SDK_INT > 15)
                    background.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.blue_ripple));
                else
                    background.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.blue_ripple));
            }
            if (player.unranked) {
                lp.setVisibility(View.GONE);}
            else {




                lp.setText(player.lp + "LP");
                rank.setText(player.tier + " " + player.division);

                tierIcon.setImageResource(Utilities.getTierImage(player.tier, player.division));}
            winsLoses.setText(Html.fromHtml("<font color=#01DF01>" + player.wins + "</font> <font color=#000000>" + "/" + "</font> <font color=#FF0000>" + player.loses + "</font>"));

            //    holder.numbPlayed.setText(player.games);
            getImage(Utilities.getChampImg(player.champ), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    champIcon.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });


        }

        for(int i=0; i<bansB.length+bansP.length; i++) {
            final int finalI = i;
            getImage(getChampImg(i<bansB.length?bansB[i]:bansP[i-bansB.length]), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    if(finalI< bansB.length) bansChampBlue[finalI].setImageBitmap(response.getBitmap());
                    else bansChampPurple[finalI-bansB.length].setImageBitmap(response.getBitmap());

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
        }}
    }



    class Player {
            String name, champ, tier, division;
            boolean blueTeam, unranked = true;
            int wins, loses, numbPlayed, lp, profileId, id;
            double k, d, a;



        }


}

package com.example.pn748_000.lolinfo;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.pn748_000.lolinfo.Keys.API_KEY;
import static com.example.pn748_000.lolinfo.Keys.ARG_SUMMONER_OBJECT;
import static com.example.pn748_000.lolinfo.Keys.DDRAGON;
import static com.example.pn748_000.lolinfo.Keys.HTTP;
import static com.example.pn748_000.lolinfo.Keys.ID;
import static com.example.pn748_000.lolinfo.Keys.PNG;
import static com.example.pn748_000.lolinfo.Keys.PROFILE_ICON_ID;
import static com.example.pn748_000.lolinfo.Keys.URL_ACTIVE_MATCH;
import static com.example.pn748_000.lolinfo.Keys.URL_CHAMP_ICON;
import static com.example.pn748_000.lolinfo.Keys.URL_ITEMS;
import static com.example.pn748_000.lolinfo.Utilities.createSummonerObject;
import static com.example.pn748_000.lolinfo.Utilities.getIntFromJson;
import static com.example.pn748_000.lolinfo.Utilities.getJsonArrayFromJson;
import static com.example.pn748_000.lolinfo.Utilities.getJsonObjectFromJson;
import static com.example.pn748_000.lolinfo.Utilities.getLeagueRequestUrl;

import static com.example.pn748_000.lolinfo.Utilities.getSummonerUrl;
import static com.example.pn748_000.lolinfo.Utilities.requestJsonObject;
import static com.example.pn748_000.lolinfo.Utilities.showLog;
import static com.example.pn748_000.lolinfo.Utilities.startSummonerActivity;
import static com.example.pn748_000.lolinfo.Utilities.stat;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActiveMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveMatchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_REGION = "regionpar";
    private static final String ARG_PARTICIPANTS = "partcpar";
    private Summoner summoner;

    // TODO: Rename and change types of parameters
    private String region, platformID;

    TextView type;
    LinearLayout parentLayout;
    Utilities utilities;
    ArrayList<Player> playersList;
    RecyclerView recyclerView;
    int[] ids;
    ActiveMatchAdapter adapter;
    JSONObject activeMatchJson;

    // TODO: Rename and change types and number of parameters
    public static ActiveMatchFragment newInstance(String region, String participants) {
        ActiveMatchFragment fragment = new ActiveMatchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REGION, region);
        args.putString(ARG_PARTICIPANTS, participants);
        fragment.setArguments(args);
        return fragment;
    }

    public ActiveMatchFragment() {
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
        utilities = new Utilities() {
            @Override
            public void onResponseReceived(int index, String champName) {
                champNameReceived(champName, index);

            }

            @Override
            public void onResponseReceived(int index, JSONArray array) {

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
        View view = inflater.inflate(R.layout.fragment_active_match, container, false);
        type = (TextView) view.findViewById(R.id.matchType);
        parentLayout = (LinearLayout) view.findViewById(R.id.parentLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.activeMatchRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ActiveMatchAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    void getActiveMatch(JSONObject activeMatchObject, String region) {

        showLog("getActiveMatch was called");
        this.region = region;
        if (playersList != null) playersList.clear(); //TODO notify adapter
        adapter.notifyDataSetChanged();
        JSONArray participants = getJsonArrayFromJson(activeMatchObject, "participants");
        if (participants != null) {
            for (int i = 0; i < participants.length(); i++) playersList.add(null);
            Log.e("asd", "participants: " + participants.length());
            type.setTextSize(20);
            type.setText(Utilities.matchType("", "", "", Utilities.stat(activeMatchObject, "gameQueueConfigId")));

            ids = new int[participants.length()];

            for (int i = 0; i < participants.length(); i++) {

                try {


                    JSONObject player = participants.getJSONObject(i);


                    final Player playerObj = new Player();
                    ids[i] = player.getInt("summonerId");
                    playerObj.name = player.getString("summonerName");
                    final int champID = player.getInt("championId");
                    playerObj.blueTeam = player.getInt("teamId") == 100;
                    playerObj.profileId = player.getInt(PROFILE_ICON_ID);
                    playerObj.id = ids[i];
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
                    playersList.set(i, playerObj);
                    utilities.champNameFromId(i, MyApplication.getAppContext(), champID, region);

                    //TODO runes and masteries

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }
    }

    void champNameReceived(final String name, final int index) {
        playersList.get(index).champ = name;
        if (index == ids.length - 1) utilities.getLeagueEntry(ids, region);

    }

    void onArrayReceived(JSONArray jsonArray, int in) throws JSONException {
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
        boolean noNulls = true;
        for (Player player : playersList)
            if (player == null) noNulls = false;
        if (noNulls) adapter.setData(playersList);


    }

    class Player {
        String name, champ, tier, division;
        boolean blueTeam, unranked = true;
        int wins, loses, numbPlayed, lp, profileId, id;
        double k, d, a;

    }

    class ActiveMatchAdapter extends RecyclerView.Adapter<ActiveMatchAdapter.ActiveMatchViewHolder> {

        ArrayList<Player> data = new ArrayList<>();
        private final LayoutInflater inflater;
        private final ImageLoader imageLoader;

        public ActiveMatchAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            VolleySingleton volleySingleton = VolleySingleton.getInstance();
            imageLoader = volleySingleton.getImageLoader();
        }

        @Override
        public ActiveMatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ActiveMatchViewHolder(inflater.inflate(R.layout.player_row, parent, false));
        }

        @Override
        public void onBindViewHolder(final ActiveMatchViewHolder holder, int position) {
            Player player = data.get(position);
            if (player != null) {
                holder.iconId = player.profileId;
                holder.champName.setText(player.champ);
                holder.id = player.id;
                holder.name.setText(player.name);
                if (player.blueTeam)
                    holder.background.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.active_match_blue));

                if (player.unranked) {
                    holder.lp.setVisibility(View.GONE);}
                else {
                    holder.unranked = false;



                    holder.lp.setText(player.lp + "LP");
                    holder.rank.setText(player.tier + " " + player.division);

                    holder.tierIcon.setImageResource(Utilities.getTierImage(player.tier, player.division));

                }
                //   holder.kda.setText(player.k+"/"+player.d+"/"+player.a);

                holder.winsLoses.setText(Html.fromHtml("<font color=#01DF01>" + player.wins + "</font> <font color=#000000>" + "/" + "</font> <font color=#FF0000>" + player.loses + "</font>"));

                //    holder.numbPlayed.setText(player.games);
                imageLoader.get(Utilities.getChampImg(player.champ), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.champIcon.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

            } else showLog("player is null @ position " + position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ActiveMatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name, champName, numberPlayed, winsLoses, kda, rank, lp;
            ImageView champIcon, tierIcon;
            FrameLayout background;
            boolean unranked = true;
            int id, iconId;

            public ActiveMatchViewHolder(View v) {
                super(v);
                background = (FrameLayout) v.findViewById(R.id.playerParentLayout);
                name = (TextView) v.findViewById(R.id.summonerName);
                champName = (TextView) v.findViewById(R.id.champName);
                numberPlayed = (TextView) v.findViewById(R.id.numberPlayed);
                winsLoses = (TextView) v.findViewById(R.id.wins_loses);
                kda = (TextView) v.findViewById(R.id.kda);
                rank = (TextView) v.findViewById(R.id.rank);
                lp = (TextView) v.findViewById(R.id.lp);
                champIcon = (ImageView) v.findViewById(R.id.champIcon);
                tierIcon = (ImageView) v.findViewById(R.id.tierIcon);
                v.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                final String nameTxt = name.getText().toString();
                if (unranked) {
                    requestJsonObject(getSummonerUrl(region, nameTxt.toLowerCase()), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            startSummonerActivity(createSummonerObject(getJsonObjectFromJson(response,nameTxt.toLowerCase()), region), getActivity());

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                } else
                    startSummonerActivity(new Summoner(nameTxt, id, iconId, 30, region), getActivity());

            }
        }

        public void setData(ArrayList<Player> players) {
            showLog("setting data");
            data = players;
            notifyItemRangeChanged(0, players.size());
            notifyDataSetChanged();
        }
    }


}

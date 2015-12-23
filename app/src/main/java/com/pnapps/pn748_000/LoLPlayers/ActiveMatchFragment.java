package com.pnapps.pn748_000.LoLPlayers;


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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.pnapps.pn748_000.LoLPlayers.Keys.PROFILE_ICON_ID;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.getJsonArrayFromJson;

import static com.pnapps.pn748_000.LoLPlayers.Utilities.showLog;


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
    private ArrayList<Integer> idsList=new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_active_match, container, false);
        type = (TextView) view.findViewById(R.id.matchType);
        idsList.add(1);
        parentLayout = (LinearLayout) view.findViewById(R.id.parentLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.activeMatchRecycler);

        return view;
    }

    void getActiveMatch(JSONObject activeMatchObject, String region) {

        showLog("getActiveMatch was called");
        this.region = region;
        JSONArray participants = getJsonArrayFromJson(activeMatchObject, "participants");
        if (participants != null) {

            Log.e("asd", "participants: " + participants.length());
            type.setTextSize(20);
            type.setText(Utilities.matchType("", "", "", Utilities.stat(activeMatchObject, "gameQueueConfigId")));

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

        if (in+1==playersList.size()) {
            showLog("notifying adapter " + playersList.size());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new ActiveMatchAdapter(playersList);
            recyclerView.setAdapter(adapter);



            //  adapter.setData(playersList);
        }


    }

    class Player implements ParentListItem{
        String name, champ, tier, division;
        boolean blueTeam, unranked = true;
        int wins, loses, numbPlayed, lp, profileId, id;
        double k, d, a;

        @Override
        public List<Integer> getChildItemList() {
            return idsList;
        }

        @Override
        public boolean isInitiallyExpanded() {
            return false;
        }


    }
  private class ActiveMatchAdapter extends ExpandableRecyclerAdapter<ActiveMatchAdapter.ActiveMatchViewHolderP,ActiveMatchAdapter.ActiveMatchViewHolderC>{ // extends RecyclerView.Adapter<ActiveMatchAdapter.ActiveMatchViewHolderP> {

        ArrayList<Player> data = new ArrayList<>();
        private final LayoutInflater inflater;
        private final ImageLoader imageLoader;

     /*   public ActiveMatchAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            VolleySingleton volleySingleton = VolleySingleton.getInstance();
            imageLoader = volleySingleton.getImageLoader();
        }

      /**
       * Primary constructor. Sets up {@link #mParentItemList} and {@link #mItemList}.
       * <p/>
       * Changes to {@link #mParentItemList} should be made through add/remove methods in
       * {@link ExpandableRecyclerAdapter}
       *
       * @param parentItemList List of all {@link ParentListItem} objects to be
       *                       displayed in the RecyclerView that this
       *                       adapter is linked to
       */
      public ActiveMatchAdapter(List<? extends ParentListItem> parentItemList) {
          super(parentItemList);
          inflater = LayoutInflater.from(getActivity());
          VolleySingleton volleySingleton = VolleySingleton.getInstance();
          imageLoader = volleySingleton.getImageLoader();
      }



      @Override
      public ActiveMatchViewHolderP onCreateParentViewHolder(ViewGroup parent) {
          return new ActiveMatchViewHolderP(inflater.inflate(R.layout.player_row_purple, parent, false));
      }

      @Override
      public ActiveMatchViewHolderC onCreateChildViewHolder(ViewGroup childViewGroup) {
          return new ActiveMatchViewHolderC(inflater.inflate(R.layout.player_second_row, childViewGroup, false));
      }

      @Override
      public void onBindChildViewHolder(ActiveMatchViewHolderC childViewHolder, int position, Object childListItem) {

      }

      @Override
      public void onBindParentViewHolder(final ActiveMatchViewHolderP holder, int position, ParentListItem parentListItem) {

          Player player= (Player) parentListItem;
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

      /*  @Override
        public int getItemCount() {
            showLog("getting item count");
            return playersList.size();
        }*/
        public void setData(ArrayList<Player> players) {
            showLog("setting data");
            data = players;
            notifyItemRangeChanged(0, players.size());
            notifyDataSetChanged();
        }
       class ActiveMatchViewHolderP extends ParentViewHolder{ //RecyclerView.ViewHolder implements View.OnClickListener {
            TextView name, champName, numberPlayed, winsLoses, kda, rank, lp;
            ImageView champIcon, tierIcon;
            RelativeLayout background;
            boolean unranked = true;
            int id, iconId;

            public ActiveMatchViewHolderP(View v) {
                super(v);
                showLog("parent constructor was called");
                background = (RelativeLayout) v.findViewById(R.id.playerParentLayout);
                name = (TextView) v.findViewById(R.id.summonerName);
                champName = (TextView) v.findViewById(R.id.champName);
                numberPlayed = (TextView) v.findViewById(R.id.numberPlayed);
                winsLoses = (TextView) v.findViewById(R.id.wins_loses);
                kda = (TextView) v.findViewById(R.id.kda);
                rank = (TextView) v.findViewById(R.id.rank);
                lp = (TextView) v.findViewById(R.id.lp);
                champIcon = (ImageView) v.findViewById(R.id.champIcon);
                tierIcon = (ImageView) v.findViewById(R.id.tierIcon);
               // v.setOnClickListener(this);

            }

         /*   @Override
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

            }*/
        }

     class ActiveMatchViewHolderC extends ChildViewHolder{
        int id;
         TextView idTest;
         public ActiveMatchViewHolderC(View itemView) {
             super(itemView);
             showLog("child constructor was called");
           //  idTest= (TextView) itemView.findViewById(R.id.id_test);
         }
     }

    }


}

package com.pnapps.pn748_000.PlayersOfLoL;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static com.pnapps.pn748_000.PlayersOfLoL.Keys.ApiKeys.API_KEY;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.ApiKeys.API_KEY_AND;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.ARG_PARTICIPANTS;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.ARG_REGION;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.HTTP;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.MASTERY_TREE;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.PROFILE_ICON_ID;

import static com.pnapps.pn748_000.PlayersOfLoL.Keys.URL_MASTERY;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.URL_RUNE;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.URL_START_GLOBAL;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.calculateAverage;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.createSummonerObject;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.formatStringOneAfterDec;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getChampImg;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getImage;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getJsonArrayFromJson;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getJsonObjectFromJson;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getKDA;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getStatsUrl;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getSummonerUrl;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.requestJsonObject;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.startSummonerActivity;

/**
 * Created by pn748_000 on 11/27/2015.
 */
public class ActiveMatchActivity extends AppCompatActivity {

    private static final int BLUE_TEAM_ID = -100;
    private static final int PURPLE_TEAM_ID = -200;
    private static final String PLAYER_LIST_STATE = "playersState";
    private static final String BANS_B_STATE = "bansBState";
    private static final String BANS_P_STATE = "bansPState";
    private static final String REGION_STATE = "regionState";
    private static final String MATCH_STATE = "activeMatchState";
    private static final String BANS_RECEIVED_STATE = "bansRecState"; //delete probably
    private static final String BANS_NUMB_STATE = "numbOfBansState";
    private Summoner summoner;
    private ArrayList<String> bansB = new ArrayList<>(), bansP = new ArrayList<>();
    private ImageView[] bansChampBlue = new ImageView[3];
    private ImageView[] bansChampPurple = new ImageView[3];


    private String region, platformID;
    private ProgressBar loadingBar;
    private TextView gameStart;
    private View panel;
    private LinearLayout blueLayout, purpleLayout, tButtonPanel;
    private Utilities utilities;
    private ArrayList<Player> playersList;
    private ScrollView parentLayout;
    private int[] ids;
    private JSONObject activeMatchJson;
    private boolean champsReceived = false, bansReceived = false, settingData = false;
    private int  numbOfBans;
    private int lengthOfList=-1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_match_scroll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            region = bundle.getString(ARG_REGION);
            platformID = Utilities.getPlatformID(region);
            try {
                activeMatchJson = new JSONObject(bundle.getString(ARG_PARTICIPANTS));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else finish();
        playersList = new ArrayList<>();
        utilities = new Utilities() {
            private int numberOfChampsReceived=0, numberOfEntriesReceived=0;
            @Override
            public void onResponseReceived(int index, String champName) {
                if (index == BLUE_TEAM_ID) {
                    bansB.add(champName);

                    if (numbOfBans == bansB.size() + bansP.size()) {
                        bansReceived = true;
                        if (!settingData && champsReceived) setData();
                    }
                } else if (index == PURPLE_TEAM_ID) {

                    bansP.add(champName);

                    if (numbOfBans == bansB.size() + bansP.size()) {
                        bansReceived = true;
                        if (!settingData && champsReceived) setData();
                    }
                } else {
                    numberOfChampsReceived++;
                    champsReceived=lengthOfList==numberOfChampsReceived&&lengthOfList==numberOfEntriesReceived;
                    champNameReceived(champName, index);
                }

            }

            @Override
            public void onResponseReceived(int index, JSONArray array) {

                try {
                    numberOfEntriesReceived++;
                    champsReceived=lengthOfList==numberOfChampsReceived&&lengthOfList==numberOfEntriesReceived;
                    onArrayReceived(array, index);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        loadingBar= (ProgressBar) findViewById(R.id.progressSpinner);
        parentLayout= (ScrollView) findViewById(R.id.scrollView);
        gameStart = (TextView) findViewById(R.id.gameStart);
        // scrollView= (ScrollView) findViewById(R.id.scrollView);
        purpleLayout = (LinearLayout) findViewById(R.id.purpleContainer);
        blueLayout = (LinearLayout) findViewById(R.id.blueContainer);
        int[] bannedChampBlue = {R.id.bannedChamp1, R.id.bannedChamp2, R.id.bannedChamp3};
        int[] bannedChampPurple = {R.id.bannedChamp4, R.id.bannedChamp5, R.id.bannedChamp6};
        for (int i = 0; i < 3; i++) {
            bansChampBlue[i] = (ImageView) findViewById(bannedChampBlue[i]);
            bansChampPurple[i] = (ImageView) findViewById(bannedChampPurple[i]);
        }
       if(savedInstanceState==null) getActiveMatch(activeMatchJson, region);
        else {
           region=savedInstanceState.getString(REGION_STATE);
           playersList=savedInstanceState.getParcelableArrayList(PLAYER_LIST_STATE);
           bansB=savedInstanceState.getStringArrayList(BANS_B_STATE);
           bansP=savedInstanceState.getStringArrayList(BANS_P_STATE);
           try {
               activeMatchJson=new JSONObject(savedInstanceState.getString(MATCH_STATE));
           } catch (JSONException e) {
               e.printStackTrace();
           }
           if(activeMatchJson!=null) {
               champsReceived=true;
               bansReceived=true;
               numbOfBans=savedInstanceState.getInt(BANS_NUMB_STATE);
               setData();
               for (int i = 0; i < numbOfBans; i++) {
                   final int finalI = i;
                   getImage(getChampImg(i < bansB.size() ? bansB.get(i) : bansP.get(i - bansB.size())), new ImageLoader.ImageListener() {
                       @Override
                       public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                           if (finalI < bansB.size())
                               bansChampBlue[finalI].setImageBitmap(response.getBitmap());
                           else
                               bansChampPurple[finalI - bansB.size()].setImageBitmap(response.getBitmap());

                       }

                       @Override
                       public void onErrorResponse(VolleyError error) {
                           error.printStackTrace();
                       }
                   });
               }
           }
       }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(PLAYER_LIST_STATE, playersList);
        outState.putStringArrayList(BANS_B_STATE, bansB);
        outState.putStringArrayList(BANS_P_STATE, bansP);
        outState.putString(REGION_STATE, region);
        outState.putString(MATCH_STATE,activeMatchJson.toString());
        outState.putBoolean(BANS_RECEIVED_STATE, bansReceived);
        outState.putInt(BANS_NUMB_STATE,numbOfBans);
    }

    private void getActiveMatch(JSONObject activeMatchObject, String region) {


        JSONArray participants = getJsonArrayFromJson(activeMatchObject, "participants");
        JSONArray bannedChamps = getJsonArrayFromJson(activeMatchObject, "bannedChampions");
        if (bannedChamps != null) {
            numbOfBans = bannedChamps.length();
            if (numbOfBans > 0)
                for (int i = 0; i < bannedChamps.length(); i++) {
                    try {
                        JSONObject object = bannedChamps.getJSONObject(i);
                        utilities.champNameFromId(-object.getInt("teamId"), this, object.getInt("championId"), region);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            else {
                bansReceived = true;
            }
        }
        if (participants != null) {


            ActionBar bar = getSupportActionBar();
            if (bar != null)
                bar.setTitle(Utilities.matchType("", "", "", Utilities.stat(activeMatchObject, "gameQueueConfigId")));
            try {
                if(activeMatchObject.getLong("gameStartTime")>0){
                Date date = new Date(activeMatchObject.getLong("gameStartTime"));
                gameStart.setText(DateFormat.getTimeInstance().format(date));}
                else gameStart.setText(R.string.not_started_game);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ids = new int[participants.length()];
            lengthOfList=participants.length();
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
                    playerObj.champId=champID;
                    if (player.has("runes")) playerObj.runes = player.getJSONArray("runes");
                    if (player.has("masteries"))
                        playerObj.masteries = player.getJSONArray("masteries");
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

    class KeyAndValue {
        String key;
        double value;

        KeyAndValue(String key, double value) {
            this.value = value;
            this.key = key;
        }
    }

    private KeyAndValue[] splitRuneStats(String stats) {
        boolean positive = stats.contains("+");
        String[] values = stats.split("\\+|\\-");
        KeyAndValue[] array = new KeyAndValue[values.length];

        for (int i = 0; i < values.length; i++) {
            if (!values[i].isEmpty()) {
                String[] tempArray = values[i].split(" ", 2);

                double value;
                String key;
                if (tempArray[0].contains("%")) {
                    key = "% " + tempArray[1];
                    value = Double.parseDouble(tempArray[0].split("%")[0]);
                } else {
                    key = tempArray[1];
                    value = Double.parseDouble(tempArray[0]);
                }
                array[i] = new KeyAndValue(key.toLowerCase(), positive ? value : -value);
            }
        }
        return array;
    }

    private AlertDialog createDialog(LinkedHashMap<String, Double> map, String title) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title).setPositiveButton("OK", null);
        StringBuilder buffer = new StringBuilder();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String sign = entry.getValue() > 0 ? "+" : "";

            if(entry.getKey().contains("("))
                buffer.append(String.format(Locale.UK, "%s %.2f %s %.2f at champion level 18)\n",sign, entry.getValue(),entry.getKey(),entry.getValue()*18));
            else buffer.append(String.format(Locale.UK, "%s %.2f %s \n",sign, entry.getValue(), entry.getKey()));

        }
        return dialogBuilder.setMessage(buffer.toString()).create();
    }

    private void showMasteriesDialog(final Player player) throws JSONException {
        final JSONArray masteries=player.masteries;
        final int[] trees={0,0,0};
        for(int i=0;i<masteries.length();i++){

            final JSONObject mastery=masteries.getJSONObject(i);
            int masteryId=mastery.getInt("masteryId");
            requestJsonObject(HTTP + URL_START_GLOBAL + region+URL_MASTERY + masteryId + MASTERY_TREE + API_KEY_AND, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                       String tree= response.getString("masteryTree");
                        switch (tree){
                            case "Ferocity": trees[0]+=mastery.getInt("rank");
                                break;
                            case "Cunning": trees[1]+=mastery.getInt("rank");
                                break;
                            case "Resolve": trees[2]+=mastery.getInt("rank");
                                break;
                        }
                        player.numbOfCompletedMast++;
                        if(player.numbOfCompletedMast==masteries.length()) {
                            AlertDialog.Builder builder=new AlertDialog.Builder(ActiveMatchActivity.this);
                            player.masteryDialog=builder.setTitle("Masteries").setPositiveButton("OK",null).setMessage("Ferocity/"+trees[0]+"\n"+
                                    "Cunning/"+trees[1]+"\n"+"Resolve/"+trees[2]+"\n").create();
                            player.masteryDialog.show();
                        }
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
        }
    }
    private void showRunesDialog(final Player player) throws JSONException {
        final JSONArray runes = player.runes;

        final LinkedHashMap<String, Double> runeMap = new LinkedHashMap<>();

        for (int i = 0; i < runes.length(); i++) {

            final JSONObject rune = runes.getJSONObject(i);

            requestJsonObject(HTTP + URL_START_GLOBAL + region + URL_RUNE + rune.getInt("runeId") + API_KEY, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        KeyAndValue[] valuesAndKeys = splitRuneStats(response.getString("description"));
                        int count = rune.getInt("count");
                        for (KeyAndValue current : valuesAndKeys) {

                            if (current != null) {
                                if (!current.key.contains(")")) {
                                    if (runeMap.containsKey(current.key)) {
                                        runeMap.put(current.key, runeMap.get(current.key) + current.value * count);
                                    } else runeMap.put(current.key, current.value * count);

                                }
                            }
                        }
                        player.numbOfCompletedRunes++;
                        if (player.numbOfCompletedRunes == runes.length()) {
                            player.runeDialog = createDialog(runeMap, "Runes");
                            player.runeDialog.show();
                        }
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
        }
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

        if (champsReceived && !settingData) {
            setData();
        }
    }

    private void setData() {
        if (champsReceived && bansReceived) {
            settingData = true;

            for (final Player player : playersList) {
                View v;
                if (player.blueTeam) {
                    v = LayoutInflater.from(this).inflate(R.layout.player_row_blue, blueLayout, false);
                    blueLayout.addView(v);
                } else {
                    v = LayoutInflater.from(this).inflate(R.layout.player_row_purple, purpleLayout, false);
                    purpleLayout.addView(v);
                }

                final LinearLayout buttonPanel = (LinearLayout) v.findViewById(R.id.buttons);
                RelativeLayout background = (RelativeLayout) v.findViewById(R.id.playerParentLayout);
                background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tButtonPanel != null) tButtonPanel.setVisibility(View.GONE);
                        if (tButtonPanel != buttonPanel) {
                            buttonPanel.setVisibility(View.VISIBLE);
                            tButtonPanel = buttonPanel;
                        } else tButtonPanel = null;

                    }
                });
                TextView name = (TextView) v.findViewById(R.id.summonerName);
                TextView champName = (TextView) v.findViewById(R.id.champName);
                final TextView numberPlayed = (TextView) v.findViewById(R.id.numberPlayed);
                TextView winsLoses = (TextView) v.findViewById(R.id.wins_loses);
                final TextView kda = (TextView) v.findViewById(R.id.kda);
                TextView rank = (TextView) v.findViewById(R.id.rank);
                TextView lp = (TextView) v.findViewById(R.id.lp);
                Button profileBtn = (Button) buttonPanel.findViewById(R.id.profileBtn);
                Button runesBtn = (Button) buttonPanel.findViewById(R.id.runesBtn);
                Button masteriesBtn = (Button) buttonPanel.findViewById(R.id.masteriesBtn);
                profileBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (player.unranked) {
                            requestJsonObject(getSummonerUrl(region, StringEscapeUtils.escapeHtml4(player.name.toLowerCase())), new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    startSummonerActivity(createSummonerObject(getJsonObjectFromJson(response, player.name.toLowerCase()), region), ActiveMatchActivity.this);

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });
                        } else
                            startSummonerActivity(new Summoner(player.name, player.id, player.profileId, 30, region), ActiveMatchActivity.this);

                    }
                });
                runesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (player.runeDialog == null)
                                showRunesDialog(player);
                            else player.runeDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                masteriesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if(player.masteryDialog==null)
                                showMasteriesDialog(player);
                            else player.masteryDialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                final ImageView champIcon = (ImageView) v.findViewById(R.id.champIcon);
                ImageView tierIcon = (ImageView) v.findViewById(R.id.tierIcon);
                champName.setText(player.champ);
                name.setText(player.name);
             /*   if (player.blueTeam) {
                    if (Build.VERSION.SDK_INT > 15)
                        background.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_ripple));
                    else
                        background.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.blue_ripple));
                }
*/
                if (player.unranked) {
                    lp.setVisibility(View.GONE);
                } else {


                    lp.setText(String.format("%d LP",player.lp ));
                    rank.setText(String.format("%s %s",player.tier, player.division));

                    tierIcon.setImageResource(Utilities.getTierImage(player.tier, player.division));
                }
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
                requestJsonObject(getStatsUrl(player.id, region), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray champions=getJsonArrayFromJson(response,"champions");
                        if(champions!=null){
                            for(int i=0; i<champions.length();i++){
                                try {
                                    JSONObject champ=champions.getJSONObject(i);
                                    if(champ.getInt("id")==player.champId){
                                        JSONObject stats=champ.getJSONObject("stats");
                                        numberPlayed.setText(String.format("(%d)", stats.getInt("totalSessionsPlayed")));
                                        kda.setText(getKDA(stats));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

            }

            for (int i = 0; i < numbOfBans; i++) {
                final int finalI = i;
                getImage(getChampImg(i < bansB.size() ? bansB.get(i) : bansP.get(i - bansB.size())), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                        if (finalI < bansB.size())
                            bansChampBlue[finalI].setImageBitmap(response.getBitmap());
                        else
                            bansChampPurple[finalI - bansB.size()].setImageBitmap(response.getBitmap());

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
            }


        loadingBar.setVisibility(View.GONE);
        parentLayout.setVisibility(View.VISIBLE);
    }}


    static class Player implements Parcelable{
        String name, champ, tier, division;
        boolean blueTeam, unranked = true;
        int wins, loses, numbPlayed, lp, profileId, id,champId,numbOfCompletedMast=0,numbOfCompletedRunes=0;
        double k, d, a;
        JSONArray runes, masteries;
        AlertDialog runeDialog, masteryDialog;


        protected Player(){}

        protected Player(Parcel in) {
            name = in.readString();
            champ = in.readString();
            tier = in.readString();
            division = in.readString();
            blueTeam = in.readByte() != 0;
            unranked = in.readByte() != 0;
            wins = in.readInt();
            loses = in.readInt();
            numbPlayed = in.readInt();
            lp = in.readInt();
            profileId = in.readInt();
            id = in.readInt();
            k = in.readDouble();
            d = in.readDouble();
            a = in.readDouble();
            try {
                runes=new JSONArray(in.readString());
                masteries=new JSONArray((in.readString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static final Creator<Player> CREATOR = new Creator<Player>() {
            @Override
            public Player createFromParcel(Parcel in) {
                return new Player(in);
            }

            @Override
            public Player[] newArray(int size) {
                return new Player[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(name);
            parcel.writeString(champ);
            parcel.writeString(tier);
            parcel.writeString(division);
            parcel.writeByte((byte) (blueTeam ? 1 : 0));
            parcel.writeByte((byte) (unranked ? 1 : 0));
            parcel.writeInt(wins);
            parcel.writeInt(loses);
            parcel.writeInt(numbPlayed);
            parcel.writeInt(lp);
            parcel.writeInt(profileId);
            parcel.writeInt(id);
            parcel.writeDouble(k);
            parcel.writeDouble(d);
            parcel.writeDouble(a);
            parcel.writeString(runes.toString());
            parcel.writeString(masteries.toString());
        }
    }


}

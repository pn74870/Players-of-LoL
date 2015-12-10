package com.example.pn748_000.lolinfo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.pn748_000.lolinfo.Keys.API_KEY;
import static com.example.pn748_000.lolinfo.Keys.API_KEY_AND;
import static com.example.pn748_000.lolinfo.Keys.ARG_PARTICIPANTS;
import static com.example.pn748_000.lolinfo.Keys.ARG_REGION;
import static com.example.pn748_000.lolinfo.Keys.HTTP;
import static com.example.pn748_000.lolinfo.Keys.MASTERY_TREE;
import static com.example.pn748_000.lolinfo.Keys.PROFILE_ICON_ID;

import static com.example.pn748_000.lolinfo.Keys.URL_MASTERY;
import static com.example.pn748_000.lolinfo.Keys.URL_RUNE;
import static com.example.pn748_000.lolinfo.Keys.URL_START_GLOBAL;
import static com.example.pn748_000.lolinfo.Utilities.createSummonerObject;
import static com.example.pn748_000.lolinfo.Utilities.getChampImg;
import static com.example.pn748_000.lolinfo.Utilities.getImage;
import static com.example.pn748_000.lolinfo.Utilities.getJsonArrayFromJson;
import static com.example.pn748_000.lolinfo.Utilities.getJsonObjectFromJson;
import static com.example.pn748_000.lolinfo.Utilities.getSummonerUrl;
import static com.example.pn748_000.lolinfo.Utilities.requestJsonObject;
import static com.example.pn748_000.lolinfo.Utilities.showLog;
import static com.example.pn748_000.lolinfo.Utilities.startSummonerActivity;

/**
 * Created by pn748_000 on 11/27/2015.
 */
public class ActiveMatchActivity extends AppCompatActivity {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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

    // TODO: Rename and change types of parameters
    private String region, platformID;

    private TextView gameStart;
    private View panel;
    private LinearLayout blueLayout, purpleLayout, tButtonPanel;
    private Utilities utilities;
    private ArrayList<Player> playersList;
    // private  ScrollView scrollView;
    private int[] ids;
    private JSONObject activeMatchJson;
    private boolean champsReceived = false, bansReceived = false, settingData = false;
    private int numbOfCompletedRunes, numbOfBans, numbOfCompletedMasteries;


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
                } else champNameReceived(champName, index);

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

        showLog("getActiveMatch was called");

        JSONArray participants = getJsonArrayFromJson(activeMatchObject, "participants");


        JSONArray bannedChamps = getJsonArrayFromJson(activeMatchObject, "bannedChampions");
        if (bannedChamps != null) {
            numbOfBans = bannedChamps.length();
            if (numbOfBans > 0)
                for (int i = 0; i < bannedChamps.length(); i++) {
                    try {
                        JSONObject object = bannedChamps.getJSONObject(i);
                        showLog("team id for banned champ " + object.getInt("teamId"));
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

            Log.e("asd", "participants: " + participants.length());

            ActionBar bar = getSupportActionBar();
            if (bar != null)
                bar.setTitle(Utilities.matchType("", "", "", Utilities.stat(activeMatchObject, "gameQueueConfigId")));
            try {
                Date date = new Date(activeMatchObject.getLong("gameStartTime"));
                showLog(DateFormat.getDateTimeInstance().format(date)); //TODO might need rework
                gameStart.setText(DateFormat.getTimeInstance().format(date));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ids = new int[participants.length()];

            for (int i = 0; i < participants.length(); i++) {
                showLog("participants loop " + i);
                try {


                    JSONObject player = participants.getJSONObject(i);


                    final Player playerObj = new Player();
                    ids[i] = player.getInt("summonerId");
                    playerObj.name = player.getString("summonerName");
                    final int champID = player.getInt("championId");
                    playerObj.blueTeam = player.getInt("teamId") == 100;
                    playerObj.profileId = player.getInt(PROFILE_ICON_ID);
                    playerObj.id = ids[i];
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
            showLog("new string " + values[i]);
            if (!values[i].isEmpty()) {
                String[] tempArray = values[i].split(" ", 2);
                showLog("splitted string " + tempArray);
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
        dialogBuilder.setTitle(title).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO try null listener
            }
        });
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String sign = entry.getValue() > 0 ? "+" : "";

            if(entry.getKey().contains("("))
                buffer.append(sign + String.format(Locale.UK, "%.2f", entry.getValue()) + " " + entry.getKey()
                        +String.format(Locale.UK,"%.2f",entry.getValue()*18)+" at champion level 18)\n");
            else buffer.append(sign + String.format(Locale.UK, "%.2f", entry.getValue()) + " " + entry.getKey()+"\n");
            showLog(buffer.toString());
        }
        return dialogBuilder.setMessage(buffer.toString()).create();
    }

    private void showMasteriesDialog(final Player player) throws JSONException {
        final JSONArray masteries=player.masteries;
        final int[] trees={0,0,0};
        numbOfCompletedMasteries =0;
        for(int i=0;i<masteries.length();i++){

            final JSONObject mastery=masteries.getJSONObject(i);
            int masteryId=mastery.getInt("masteryId");
            requestJsonObject(HTTP + URL_START_GLOBAL + region+URL_MASTERY + masteryId + MASTERY_TREE + API_KEY_AND, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                       String tree= response.getString("masteryTree");
                        showLog("tree is "+tree);
                        switch (tree){
                            case "Ferocity": trees[0]+=mastery.getInt("rank");
                                break;
                            case "Cunning": trees[1]+=mastery.getInt("rank");
                                break;
                            case "Resolve": trees[2]+=mastery.getInt("rank");
                                break;
                        }
                        numbOfCompletedMasteries++;
                        if(numbOfCompletedMasteries==masteries.length()) {
                            showLog("creating mastery dialog");
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
    private void showRunesDialog(final Player player) throws JSONException { //TODO FIX!! DOES NOT SHOW THE LAST LINE
        final JSONArray runes = player.runes;
        numbOfCompletedRunes = 0;
        final LinkedHashMap<String, Double> runeMap = new LinkedHashMap<>();

        for (int i = 0; i < runes.length(); i++) {

            final JSONObject rune = runes.getJSONObject(i);

            showLog(HTTP + URL_START_GLOBAL + region + URL_RUNE + rune.getInt("runeId") + API_KEY);
            requestJsonObject(HTTP + URL_START_GLOBAL + region + URL_RUNE + rune.getInt("runeId") + API_KEY, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        showLog("got description of rune " + response.getString("description"));
                        KeyAndValue[] valuesAndKeys = splitRuneStats(response.getString("description"));
                        int count = rune.getInt("count");
                        for (KeyAndValue current : valuesAndKeys) {

                            if (current != null) {
                                if (!current.key.contains(")")) {
                                    if (runeMap.containsKey(current.key)) {
                                        runeMap.put(current.key, runeMap.get(current.key) + current.value * count);
                                    } else runeMap.put(current.key, current.value * count);
                                    showLog("value & key & count: " + current.value + " " + current.key + " " + count);
                                }
                            }
                        }
                        numbOfCompletedRunes++;
                        if (numbOfCompletedRunes == runes.length()) {
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

        if (in + 1 == playersList.size()) {
            champsReceived = true;

            setData();
        }
    }

    private void setData() {
        showLog("set data was called champsreceived " + champsReceived + " bans received " + bansReceived);
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
                TextView numberPlayed = (TextView) v.findViewById(R.id.numberPlayed);
                TextView winsLoses = (TextView) v.findViewById(R.id.wins_loses);
                TextView kda = (TextView) v.findViewById(R.id.kda);
                TextView rank = (TextView) v.findViewById(R.id.rank);
                TextView lp = (TextView) v.findViewById(R.id.lp);
                Button profileBtn = (Button) buttonPanel.findViewById(R.id.profileBtn);
                Button runesBtn = (Button) buttonPanel.findViewById(R.id.runesBtn);
                Button masteriesBtn = (Button) buttonPanel.findViewById(R.id.masteriesBtn);
                profileBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (player.unranked) {
                            requestJsonObject(getSummonerUrl(region, player.name.toLowerCase()), new Response.Listener<JSONObject>() {
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
                        showLog("rune btn was clicked");
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
                        showLog("masteries btn was clicked");
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


                    lp.setText(player.lp + "LP");
                    rank.setText(player.tier + " " + player.division);

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
        }
    }


    class Player implements Parcelable{
        String name, champ, tier, division;
        boolean blueTeam, unranked = true;
        int wins, loses, numbPlayed, lp, profileId, id;
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

        public final Creator<Player> CREATOR = new Creator<Player>() {
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

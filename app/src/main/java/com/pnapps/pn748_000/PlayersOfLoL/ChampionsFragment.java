package com.pnapps.pn748_000.PlayersOfLoL;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.calculateAverage;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.formatDouble;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.formatStringOneAfterDec;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getChampImg;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getImage;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getJsonArrayFromJson;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getJsonObjectFromJson;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getStatsUrl;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.requestJsonObject;


/**
 * Created by pn748_000 on 11/25/2015.
 */
public class ChampionsFragment extends Fragment {
    private static final String ARG_ID = "argumentID";
    private static final String ARG_REGION = "argumentRegion";
    private static final String STATE_LIST = "stateOfList";
    private int id;
    private String region;
    private Utilities utilities;
    private ArrayList<ChampionStats> statsList = new ArrayList<>();
    private int length;
    private ChampionListAdapter adapter;
    private TextView noRankedText;
    private StatsListener listener;
    private ProgressBar loadingBar;

    interface StatsListener {
        void onStatsReceived(JSONObject stats);
        void noRankedGamesFound();
        void onListCompleted(int index, Bitmap bitmap, int games);
    }

    public static ChampionsFragment newInstance(int id, String region) {
        ChampionsFragment championsFragment = new ChampionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_REGION, region);
        championsFragment.setArguments(args);
        return championsFragment;
    }

    public ChampionsFragment() {
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            listener = (StatsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement Stats Listener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            id = args.getInt(ARG_ID);
            region = args.getString(ARG_REGION);
        }
        utilities = new Utilities() {
            int numberOfFinished = 0;

            @Override
            public void onResponseReceived(int index, String champName) {
                numberOfFinished++;
                statsList.get(index).champ = champName;
                if (numberOfFinished == length - 1) {
                    adapter.setData(statsList);
                }
            }

            @Override
            public void onResponseReceived(int index, JSONArray array) {

            }
        };
        adapter = new ChampionListAdapter();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.champions_fragment, container, false);
        noRankedText = (TextView) view.findViewById(R.id.noRankedTxt);
        loadingBar = (ProgressBar) view.findViewById(R.id.progressSpinner);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.championsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getAppContext()));
        recyclerView.setAdapter(adapter);
        if (savedInstanceState != null)
            statsList = savedInstanceState.getParcelableArrayList(STATE_LIST);
        else
            getChampsList();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (statsList != null) outState.putParcelableArrayList(STATE_LIST, statsList);
    }

    public void getChampsList() {

        requestJsonObject(getStatsUrl(id, region), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray champions = getJsonArrayFromJson(response, "champions");
                if (champions != null) {
                    length = champions.length();

                    for (int i = 0; i < length; i++) {
                        try {

                            JSONObject champ = champions.getJSONObject(i);
                            if (champ != null) {
                                int champId = champ.getInt("id");
                                if (champId != 0) {//zero id is used for general stats by rito
                                    JSONObject champStats = getJsonObjectFromJson(champ, "stats");
                                    if (champStats != null)
                                        statsList.add(new ChampionStats(champStats.getInt("totalChampionKills"),
                                                champStats.getInt("totalDeathsPerSession"), champStats.getInt("totalAssists"), champStats.getInt("totalMinionKills"),
                                                champStats.getInt("totalSessionsLost"), champStats.getInt("totalSessionsWon")));
                                    final int index = statsList.size() - 1;
                                    utilities.champNameFromId(index, MyApplication.getAppContext(), champId, region);

                                } else listener.onStatsReceived(champ);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else noRankedText.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                listener.noRankedGamesFound();
                noRankedText.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.GONE);
            }
        });

    }

    static class ChampionStats implements Comparable<ChampionStats>, Parcelable {
        int kills, deaths, assists, cs, loses, wins, games;
        String champ;

        ChampionStats(int kills, int deaths, int assists, int cs, int loses, int wins) {
            this.assists = assists;
            this.cs = cs;
            this.deaths = deaths;
            this.kills = kills;
            this.loses = loses;
            this.wins = wins;
            games = wins + loses;
        }

        protected ChampionStats(Parcel in) {
            kills = in.readInt();
            deaths = in.readInt();
            assists = in.readInt();
            cs = in.readInt();
            loses = in.readInt();
            wins = in.readInt();
            games = in.readInt();
            champ = in.readString();
        }

        public final static Creator<ChampionStats> CREATOR = new Creator<ChampionStats>() {
            @Override
            public ChampionStats createFromParcel(Parcel in) {
                return new ChampionStats(in);
            }

            @Override
            public ChampionStats[] newArray(int size) {
                return new ChampionStats[size];
            }
        };

        @Override
        public int compareTo(ChampionStats championStats) {
            return championStats.games - games;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(kills);
            parcel.writeInt(deaths);
            parcel.writeInt(assists);
            parcel.writeInt(cs);
            parcel.writeInt(loses);
            parcel.writeInt(wins);
            parcel.writeInt(games);
            parcel.writeString(champ);
        }
    }

    class ChampionListViewHolder extends RecyclerView.ViewHolder {
        ImageView champIcon;
        TextView kda, winsLoses, ratio, creeps;
        String name;

        public ChampionListViewHolder(View itemView) {
            super(itemView);
            champIcon = (ImageView) itemView.findViewById(R.id.championIcon);
            kda = (TextView) itemView.findViewById(R.id.kda_number);
            winsLoses = (TextView) itemView.findViewById(R.id.wl_number);
            ratio = (TextView) itemView.findViewById(R.id.ratio_number);
            creeps = (TextView) itemView.findViewById(R.id.cs_number);
        }
    }

    class ChampionListAdapter extends RecyclerView.Adapter<ChampionListViewHolder> {
        ArrayList<ChampionStats> list = new ArrayList<>();

        public void setData(ArrayList<ChampionStats> list) {
            this.list = list;
            Collections.sort(this.list, new Comparator<ChampionStats>() {
                @Override
                public int compare(ChampionStats championStats, ChampionStats t1) {
                    return championStats.compareTo(t1);
                }
            });
            notifyDataSetChanged();
            loadingBar.setVisibility(View.GONE);
        }

        @Override
        public ChampionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChampionListViewHolder(LayoutInflater.from(MyApplication.getAppContext()).inflate(R.layout.champion_row, parent, false));
        }

        @Override
        public void onBindViewHolder(final ChampionListViewHolder holder, final int position) {

            final ChampionStats stats = list.get(position);
            holder.creeps.setText(formatDouble(calculateAverage(stats.cs, stats.games), 0));
            holder.ratio.setText(formatStringOneAfterDec(calculateAverage(stats.wins * 100, stats.games)) + "%");
            holder.winsLoses.setText(Html.fromHtml("<font color=#01DF01>" + stats.wins + "</font> <font color=#000000>" + "/" + "</font> <font color=#FF0000>" + stats.loses + "</font>"));
            holder.kda.setText(String.format("%s/%s/%s", formatStringOneAfterDec(calculateAverage(stats.kills, stats.games)),
                    formatStringOneAfterDec(calculateAverage(stats.deaths, stats.games)),
                    formatStringOneAfterDec(calculateAverage(stats.assists, stats.games))));
            holder.name = stats.champ;
            getImage(getChampImg(stats.champ), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    holder.champIcon.setImageBitmap(bitmap);

                    if (position < 4)
                        if (listener != null)
                            listener.onListCompleted(position, bitmap, stats.games);
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

}

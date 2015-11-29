package com.example.pn748_000.lolinfo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import static com.example.pn748_000.lolinfo.Utilities.calculateAverage;
import static com.example.pn748_000.lolinfo.Utilities.formatStringOneAfterDec;
import static com.example.pn748_000.lolinfo.Utilities.getChampImg;
import static com.example.pn748_000.lolinfo.Utilities.getChampNameRequestUrl;
import static com.example.pn748_000.lolinfo.Utilities.getImage;
import static com.example.pn748_000.lolinfo.Utilities.getJsonArrayFromJson;
import static com.example.pn748_000.lolinfo.Utilities.getJsonObjectFromJson;
import static com.example.pn748_000.lolinfo.Utilities.getStatsUrl;
import static com.example.pn748_000.lolinfo.Utilities.requestJsonObject;
import static com.example.pn748_000.lolinfo.Utilities.showLog;
import static com.example.pn748_000.lolinfo.Utilities.stat;

/**
 * Created by pn748_000 on 11/25/2015.
 */
public class ChampionsFragment extends Fragment {
    private static final String ARG_ID="argumentID";
    private static final String ARG_REGION="argumentRegion";
    private int id;
    private RecyclerView recyclerView;
    private String region;
    private Utilities utilities;
    private ArrayList<ChampionStats> statsList=new ArrayList<>();
    private int length;
    private ChampionListAdapter adapter;
    private TextView noRankedText;
    private StatsListener listener;
interface StatsListener{
    void onStatsReceived(JSONObject stats);
}
    public static ChampionsFragment newInstance(int id, String region){
        ChampionsFragment championsFragment=new ChampionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_REGION,region);
        championsFragment.setArguments(args);
        return championsFragment;
    }
    public ChampionsFragment(){}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener= (StatsListener) activity;}
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement Stats Listener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        if(args!=null){
            id=args.getInt(ARG_ID);
            region=args.getString(ARG_REGION);
        }
         utilities=new Utilities() {
            @Override
            public void onResponseReceived(int index, String champName) {
                statsList.get(index).champ=champName;
                showLog("getting champ name "+index);
                if(index+2==length) {
                    adapter.setData(statsList);
                    noRankedText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponseReceived(int index, JSONArray array) {

            }
        };
        adapter=new ChampionListAdapter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.champions_fragment, container, false);
        noRankedText= (TextView) view.findViewById(R.id.noRankedTxt);
        recyclerView= (RecyclerView) view.findViewById(R.id.championsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyApplication.getAppContext()));
        recyclerView.setAdapter(adapter);
        getChampsList();
        return view;
    }
    public void getChampsList(){
        showLog("getChampsList was called");
        requestJsonObject(getStatsUrl(id, region), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray champions = getJsonArrayFromJson(response, "champions");

                if (champions != null) {
                    length=champions.length();
                    showLog("the length of list is "+length);
                    for (int i = 0; i < length; i++) {
                        try {

                            JSONObject champ = champions.getJSONObject(i);
                            if (champ != null ) {

                                if(champ.getInt("id")!=0){
                            JSONObject champStats = getJsonObjectFromJson(champ, "stats");
                            if(champStats!=null)
                                statsList.add(new ChampionStats( champStats.getInt("totalChampionKills"), champStats.getInt("totalDeathsPerSession"), champStats.getInt("totalAssists"), champStats.getInt("totalMinionKills"),
                                        champStats.getInt("totalSessionsLost"), champStats.getInt("totalSessionsWon")));
                                utilities.champNameFromId(statsList.size()-1, MyApplication.getAppContext(), champ.getInt("id"), region);

                            }
                            else listener.onStatsReceived(champ);}

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

    }
    class ChampionStats{
        int kills,deaths,assists,cs,loses,wins,games;
        String champ;
        ChampionStats(int kills,int deaths,int assists,int cs,int loses,int wins){
            this.assists=assists;
            this.cs=cs;
            this.deaths=deaths;
            this.kills=kills;

            this.loses=loses;
            this.wins=wins;
            games=wins+loses;
        }}
        class ChampionListViewHolder extends RecyclerView.ViewHolder{
            ImageView champIcon;
            TextView kda,winsLoses,ratio,creeps;
            public ChampionListViewHolder(View itemView) {
                super(itemView);
                champIcon= (ImageView) itemView.findViewById(R.id.championIcon);
                kda= (TextView) itemView.findViewById(R.id.kda_number);
                winsLoses= (TextView) itemView.findViewById(R.id.wl_number);
                ratio= (TextView) itemView.findViewById(R.id.ratio_number);
                creeps= (TextView) itemView.findViewById(R.id.cs_number);
            }
        }
        class ChampionListAdapter extends RecyclerView.Adapter<ChampionListViewHolder>{
            ArrayList<ChampionStats> list=new ArrayList<>();
            public void setData(ArrayList<ChampionStats> list){
                this.list=list;
                notifyDataSetChanged();
            }
            @Override
            public ChampionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ChampionListViewHolder(LayoutInflater.from(MyApplication.getAppContext()).inflate(R.layout.champion_row,parent,false));
            }

            @Override
            public void onBindViewHolder(final ChampionListViewHolder holder, int position) {
                ChampionStats stats=list.get(position);
                holder.creeps.setText(formatStringOneAfterDec(calculateAverage(stats.cs, stats.games)));
                holder.ratio.setText(formatStringOneAfterDec(calculateAverage(stats.wins*100,stats.games))+"%");
                holder.winsLoses.setText(Html.fromHtml("<font color=#01DF01>"+stats.wins+"</font> <font color=#000000>"+"/"+"</font> <font color=#FF0000>"+stats.loses+"</font>"));
                holder.kda.setText(formatStringOneAfterDec(calculateAverage(stats.kills, stats.games)) + "/"
                        + formatStringOneAfterDec(calculateAverage(stats.deaths, stats.games))
                        + "/" + formatStringOneAfterDec(calculateAverage(stats.assists, stats.games)));
                getImage(getChampImg(stats.champ), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.champIcon.setImageBitmap(response.getBitmap());
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

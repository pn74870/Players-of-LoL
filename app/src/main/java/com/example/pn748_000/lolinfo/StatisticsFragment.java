package com.example.pn748_000.lolinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by pn748_000 on 8/14/2015.
 */
public class StatisticsFragment extends Fragment {
    private static final String ARG_STATS = "statsJson";//TODO might need to delete
    private TableLayout table;
    private TextView pentas,quadras,triples,doubles,kills,killingSprees,mostKills,largestSpree,assists,gold,turrets,title;

    public StatisticsFragment() {
    }

    public static StatisticsFragment newInstance() {
       /*  StatisticsFragment fragment = new StatisticsFragment();
       Bundle args = new Bundle();
        args.putString(ARG_STATS, stats);
        fragment.setArguments(args);*/
        return new StatisticsFragment();
    }
    public void showStats(JSONObject stats){
        JSONObject statsObject=Utilities.getJsonObjectFromJson(stats,"stats");
        if (statsObject != null) {


            pentas.setText(statsObject.optInt("totalPentaKills") + "");
            quadras.setText(statsObject.optInt("totalQuadraKills") + "");
            triples.setText(statsObject.optInt("totalTripleKills") + "");
            doubles.setText(statsObject.optInt("totalDoubleKills") + "");
            kills.setText(statsObject.optInt("totalChampionKills") + "");
            mostKills.setText(statsObject.optInt("maxChampionsKilled") + "");
            assists.setText(statsObject.optInt("totalAssists") + "");
            gold.setText(NumberFormat.getInstance(Locale.US).format(statsObject.optInt("totalGoldEarned")));
            turrets.setText(statsObject.optInt("totalTurretsKilled") + "");
            largestSpree.setText(statsObject.optInt("maxLargestKillingSpree")+"");
            killingSprees.setText(statsObject.optInt("killingSpree")+"");
            table.setVisibility(View.VISIBLE);
            title.setText(getString(R.string.ranked_stats));
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.summoner_stats, container, false);
     /*   JSONObject statsObject = null;
        if (getArguments() != null) {
            try {
                statsObject = new JSONObject(getArguments().getString(ARG_STATS));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
         pentas = (TextView) view.findViewById(R.id.penta_number);
         quadras = (TextView) view.findViewById(R.id.quadra_number);
         triples = (TextView) view.findViewById(R.id.triple_number);
         doubles = (TextView) view.findViewById(R.id.double_number);
         kills = (TextView) view.findViewById(R.id.kills_number);
         assists = (TextView) view.findViewById(R.id.assists_number);
         mostKills = (TextView) view.findViewById(R.id.most_kills_number);
         gold = (TextView) view.findViewById(R.id.gold_number);
         turrets = (TextView) view.findViewById(R.id.turrets_number);
         killingSprees = (TextView) view.findViewById(R.id.killing_sprees_number);
         largestSpree = (TextView) view.findViewById(R.id.largest_killing_spree_number);
         table= (TableLayout) view.findViewById(R.id.stats_table);
         title= (TextView) view.findViewById(R.id.stats_title);
        return view;
        }
    }

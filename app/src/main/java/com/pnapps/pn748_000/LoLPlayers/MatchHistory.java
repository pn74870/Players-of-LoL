package com.pnapps.pn748_000.LoLPlayers;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pnapps.pn748_000.LoLPlayers.Keys.API_KEY;

import static com.pnapps.pn748_000.LoLPlayers.Keys.API_KEY_AND;

import static com.pnapps.pn748_000.LoLPlayers.Keys.DDRAGON;
import static com.pnapps.pn748_000.LoLPlayers.Keys.DDRAGON_SPELL_IMG;
import static com.pnapps.pn748_000.LoLPlayers.Keys.HTTP;
import static com.pnapps.pn748_000.LoLPlayers.Keys.PNG;
import static com.pnapps.pn748_000.LoLPlayers.Keys.RECENT;
import static com.pnapps.pn748_000.LoLPlayers.Keys.URL_CHAMPION;
import static com.pnapps.pn748_000.LoLPlayers.Keys.URL_CHAMP_ICON;
import static com.pnapps.pn748_000.LoLPlayers.Keys.URL_MATCH_HISTORY;
import static com.pnapps.pn748_000.LoLPlayers.Keys.URL_START;
import static com.pnapps.pn748_000.LoLPlayers.Keys.URL_START_GLOBAL;
import static com.pnapps.pn748_000.LoLPlayers.Keys.URL_SUMMONER_SPELLS;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.getBooleanFromJson;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.getChampImg;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.getIntFromJson;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.getJsonObjectFromJson;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.getStringFromJson;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.matchType;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.showLog;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.stat;
import static com.pnapps.pn748_000.LoLPlayers.Utilities.requestJsonObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MatchHistory.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MatchHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchHistory extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ID = "id";
    private static final String ARG_REGION = "region";
    private static final String STATE_LIST = "list";

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private ArrayList<MatchInfo> list;
    // TODO: Rename and change types of parameters


    private OnFragmentInteractionListener mListener;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private Utilities utilities;
    private int id;
    private String region;
    private int lengthOfList;

    // TODO: Rename and change types and number of parameters
    public static MatchHistory newInstance(int id, String region) {
        MatchHistory fragment = new MatchHistory();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_REGION, region);

        fragment.setArguments(args);

        return fragment;
    }

    public MatchHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_ID);
            region = getArguments().getString(ARG_REGION);

        }
        Log.e("asd", "onCreate match history");
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getmRequestQueue();
        adapter = new MyAdapter(getActivity());

        utilities = new Utilities() {
            int numberOfFinished=0;
            @Override
            public void onResponseReceived(int index, String champName) {
                showLog("onResponseReceived " + champName);
                numberOfFinished++;
                list.get(index).champIcon = getChampImg(champName);
                if (numberOfFinished==lengthOfList) {

                    adapter.setData(list);
                }

            }


            @Override
            public void onResponseReceived(int i, JSONArray array) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("asd", "onCreateView");
        View view = inflater.inflate(R.layout.match_history, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.matchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(adapter);
        if (savedInstanceState != null) {
            list = savedInstanceState.getParcelableArrayList(STATE_LIST);
            region = savedInstanceState.getString(ARG_REGION);
            adapter.setData(list);
        } else {
            list = new ArrayList<>();
            refreshList();
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and nameTextView
        void onFragmentInteraction(Uri uri);
    }


    public String getRequestUrl(int id, String region, int type, String champ) {
        switch (type) {
            case 1:
                return HTTP + URL_START_GLOBAL + region + URL_CHAMPION + id + API_KEY;
            case 2:
                return DDRAGON + MainActivity.version + URL_CHAMP_ICON + champ + PNG;
        }
        return HTTP + region + URL_START + region + URL_MATCH_HISTORY + id + RECENT + API_KEY;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, list);
        outState.putString(ARG_REGION, region);
    }

    public void refreshList() {

        if (list != null) {
            list.clear();
            adapter.notifyDataSetChanged();
        }
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getRequestUrl(id, region, 0, ""), (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final JSONArray array = response.getJSONArray("games");
                    lengthOfList=array.length();
                    for (int i = 0; i < array.length(); i++) list.add(null);
                    for (int i = 0; i < array.length(); i++) {
                        final int in = i;

                        final JSONObject stats = array.getJSONObject(i).getJSONObject("stats");
                        final int champ = array.getJSONObject(i).getInt("championId");
                        final String subtype = array.getJSONObject(i).getString("subType");
                        final String type = array.getJSONObject(i).getString("gameType");
                        final String mode = array.getJSONObject(i).getString("gameMode");
                        final int spell1 = array.getJSONObject(i).getInt("spell1");
                        final int spell2 = array.getJSONObject(i).getInt("spell2");
                        final long date = array.getJSONObject(i).getLong("createDate");
                        Log.e("asd", "date is " + date);

                        requestJsonObject(HTTP + URL_START_GLOBAL + URL_SUMMONER_SPELLS + API_KEY_AND, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                showLog("summoner spells were received");
                                JSONObject spells = getJsonObjectFromJson(response, "data");
                                String spellName1 = "" + spell1;
                                String spellName2 = "" + spell2;

                                if (spells != null) {
                                    if (spells.has(spell1 + ""))
                                        spellName1 = getStringFromJson(getJsonObjectFromJson(spells, spell1 + ""), "key");
                                    if (spells.has(spell2 + ""))
                                        spellName2 = getStringFromJson(getJsonObjectFromJson(spells, spell2 + ""), "key");
                                }
                                int[] items = {0, 0, 0, 0, 0, 0};
                                for (int i = 0; i < 6; i++) {

                                    if (stats.has("item" + i))
                                        items[i] = getIntFromJson(stats, "item" + i);

                                }
                                MatchInfo matchInfo = new MatchInfo(stat(stats, "championsKilled"), stat(stats, "assists"), stat(stats, "numDeaths"),
                                        stat(stats, "minionsKilled") + stat(stats, "neutralMinionsKilled"), getIntFromJson(stats, "goldEarned"),
                                        "", champ, getBooleanFromJson(stats, "win"),
                                        matchType(type, subtype, mode, -1), spellName1,
                                        spellName2, items, date, getIntFromJson(stats, "timePlayed"), in);
                                list.set(in, matchInfo);
                                utilities.champNameFromId(in, MyApplication.getAppContext(), champ, region);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });

                        //creating arguments
                        //final int spell1,final int spell2,final JSONObject stats,final int in, final String champion, final int champ, final String type,final String subtype,final String mode, final long date


/*
                        final SharedPreferences champIdPrefs=getActivity().getSharedPreferences(getString(R.string.champIdPreferences), Context.MODE_PRIVATE);
                        String champName = champIdPrefs.getString(champ + "", "");
                        if(champName.equals("")){
                        JsonObjectRequest champRequest=new JsonObjectRequest(Request.Method.GET, getRequestUrl(champ, region, 1, ""), (String) null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                   final String champion=response.getString("key");
                                    champIdPrefs.edit().putString(champ+"",champion).apply();
                                    processMatchData(spell1, spell2, stats, in, champion, champ, type,subtype,mode, date);
                                    Log.e("asd","champion "+champion+" was cached");



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        requestQueue.add(champRequest);

                    }
                    else processMatchData(spell1,spell2,stats,in,champName,champ,type,subtype,mode,date); }*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);


    }


    void processMatchData(int in, String champName) {
        showLog("processMatchData");

        list.get(in).champIcon = getChampImg(champName);


        boolean noNull = true;
        for (int j = 0; j < list.size(); j++) {
            if (list.get(j) == null) noNull = false;
        }
        if (noNull) {

            adapter.setData(list);
        }


    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<MatchInfo> data = new ArrayList<>();
        LayoutInflater inflater;
        ImageLoader imageLoader;


        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);

            VolleySingleton volleySingleton = VolleySingleton.getInstance();
            imageLoader = volleySingleton.getImageLoader();


        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.match_row, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            MatchInfo current = data.get(position);
            if (current != null) {
                holder.creeps.setText(current.creeps + "");
                holder.gold.setText(Math.round(((double) current.gold) / 1000) + "k");

                holder.kda.setText(current.kills + "/" + current.deaths + "/" + current.assists);
                holder.type.setText(current.type);
                if (current.win)
                    holder.bg.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.greenColor));
                else
                    holder.bg.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.redColor));

                Date date = new Date(current.date);
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(MyApplication.getAppContext());
                holder.date.setText(dateFormat.format(date));
                holder.duration.setText(Math.round(((double) current.duration) / 60) + "min");


                String imgUrl = current.champIcon;

                if (imgUrl != null) imageLoader.get(imgUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.champImg.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyApplication.getAppContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                getSpellImg(current.spell1, holder.spell1Img);
                getSpellImg(current.spell2, holder.spell2Img);


                int[] items = current.items;
                for (int i = 0; i < items.length; i++) {

                    if (items[i] > 0) {
                        final Utilities utilities = new Utilities(items[i], holder.items[i], MainActivity.versions) {//TODO object initialization for each onBindView
                            @Override
                            public void onResponseReceived(int index, String champName) {

                            }

                            @Override
                            public void onResponseReceived(int index, JSONArray array) {

                            }
                        };
                        utilities.setItemImage(items[i], holder.items[i], MainActivity.version, true);
                        //  Picasso.with(getActivity()).load(getItemImgUrl(items[i],MainActivity.version)).into(holder.items[i]);
                    } else {
                        holder.items[i].setImageDrawable(null);
                        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
                            holder.items[i].setBackgroundDrawable(MyApplication.getAppContext().getResources().getDrawable(R.drawable.border));
                        else
                            holder.items[i].setBackground(MyApplication.getAppContext().getDrawable(R.drawable.border));
                    }
                }
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView champImg, spell1Img, spell2Img;
            ImageView[] items;
            TextView kda, gold, creeps, type, date, duration;
            LinearLayout bg;

            public MyViewHolder(View itemView) {
                super(itemView);
                champImg = (ImageView) itemView.findViewById(R.id.champImg);
                kda = (TextView) itemView.findViewById(R.id.kdaTxt);
                type = (TextView) itemView.findViewById(R.id.typeTxt);
                gold = (TextView) itemView.findViewById(R.id.goldTxt);
                creeps = (TextView) itemView.findViewById(R.id.creepsTxt);
                spell1Img = (ImageView) itemView.findViewById(R.id.summSpell1);
                spell2Img = (ImageView) itemView.findViewById(R.id.summSpell2);
                bg = (LinearLayout) itemView.findViewById(R.id.matchBg);
                date = (TextView) itemView.findViewById(R.id.dateTxt);
                duration = (TextView) itemView.findViewById(R.id.durationTxt);
                items = new ImageView[6];
                items[0] = (ImageView) itemView.findViewById(R.id.item1);
                items[1] = (ImageView) itemView.findViewById(R.id.item2);
                items[2] = (ImageView) itemView.findViewById(R.id.item3);
                items[3] = (ImageView) itemView.findViewById(R.id.item4);
                items[4] = (ImageView) itemView.findViewById(R.id.item5);
                items[5] = (ImageView) itemView.findViewById(R.id.item6);

            }
        }

        public void setData(ArrayList<MatchInfo> list) {
            data = list;
            notifyItemRangeChanged(0, list.size());
            notifyDataSetChanged();
        }

        private void getSpellImg(String spell, final ImageView imageView) {
            if (spell != null) {
                if (!NumberUtils.isNumber(spell))
                    // Picasso.with(getActivity()).load(DDRAGON+MainActivity.version+DDRAGON_SPELL_IMG+spell+PNG).into(imageView);
                    imageLoader.get(DDRAGON + MainActivity.version + DDRAGON_SPELL_IMG + spell + PNG, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            imageView.setImageBitmap(response.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                else {
                    Utilities utilities = new Utilities(Integer.valueOf(spell), imageView, MainActivity.versions) {
                        @Override
                        public void onResponseReceived(int index, String champName) {

                        }

                        @Override
                        public void onResponseReceived(int index, JSONArray array) {

                        }
                    };
                    try {
                        utilities.findSummSpells(Integer.valueOf(spell), MainActivity.versions.getString(3), imageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }


}

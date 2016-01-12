package com.pnapps.pn748_000.PlayersOfLoL;


import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;

import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.pnapps.pn748_000.PlayersOfLoL.Keys.ApiKeys.API_KEY;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.ARG_PARTICIPANTS;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.ARG_REGION;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.HTTP;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.ID;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.PROFILE_ICON_ID;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.URL_ACTIVE_MATCH;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.URL_START_GLOBAL;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.URL_VERSION;
import static com.pnapps.pn748_000.PlayersOfLoL.Keys.URL_VERSION_LIST;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getIntFromJson;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getJsonObjectFromJson;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getPlatformID;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.getStringFromJson;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.requestJsonArray;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.requestJsonObject;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.showToast;
import static com.pnapps.pn748_000.PlayersOfLoL.Utilities.startSummonerActivity;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener, FreeChampFragment.FreeChampClickListener {
    private final static String ID_STATE = "id";
    private final static String REGION_STATE = "regionState";
    private final static String MODE_STATE = "modeState";
    private final static String FREE_CHAMP_STATE = "freeChampFragState";
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedId;
    private ImageView searchImg;
    private String region;
    private String summonerName;
    private boolean typingName = false;
    protected static String version;
    private FreeChampFragment freeChampFragment;
    protected static JSONObject summonerJsonObject;
    //   SearchView searchView;
    private String quer = "";
    protected static JSONArray versions;
    protected static LruCache<String, Bitmap> mMemoryCache;
    private DBAdapter dbAdapter;
    private RecyclerView recyclerView;
    private EditText editText;
    private SummonerListAdapter adapter;
    private boolean profileMode, isSummonerSelected = false;
    protected static int screenDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.main_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        recyclerView = (RecyclerView) findViewById(R.id.summoners_recycler_view);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        adapter = new SummonerListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        searchImg = (ImageView) findViewById(R.id.search_btn);
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typingName) {
                    editText.setText("");
                    quer = "";
                    searchImg.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_search_black_24dp));
                    typingName = false;
                }
            }
        });
        //   recyclerView.addItemDecoration(new RecyclerViewDivider(this));
        region = PreferenceManager.getDefaultSharedPreferences(this).getString(REGION_STATE, "euw");
        profileMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(MODE_STATE, true);
        versionCheck();
        dbAdapter = new DBAdapter(this);


        adapter.setData("");
        final View view = findViewById(R.id.app_bar_extension);
        editText = (EditText) view.findViewById(R.id.text_field);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!typingName) {
                    searchImg.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_close_black_24dp));
                    typingName = true;
                }
                if (editText.getText().toString().equals("")) {
                    searchImg.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_search_black_24dp));
                    typingName = false;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                quer = editable.toString();
                adapter.setData(quer);
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH && quer != null) {
                    searchSummoner(textView.getText().toString(), region);
                    return true;
                }
                return false;
            }
        });

        ImageButton submitBtn = (ImageButton) view.findViewById(R.id.sumbmitButton);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quer != null)
                    searchSummoner(quer, region);
            }
        });

        final int cacheSize = (int) (1.5 * 1024 * 1024);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                SummonerListAdapter.SummonerListViewHolder vh = (SummonerListAdapter.SummonerListViewHolder) viewHolder;
                dbAdapter.deleteRow(vh.summonerId);
                adapter.setData(quer);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getByteCount() / 1024;
            }

        };
        screenDensity = getResources().getDisplayMetrics().densityDpi;

    }


    protected void searchSummoner(final String query, final String region) {
        if (!isSummonerSelected) {
            isSummonerSelected = true;
            if (!(version==null)&&!version.isEmpty()) {
                summonerName = query.replace(" ", "").toLowerCase();
                Utilities.requestJsonObject(Utilities.getSummonerUrl(region, StringEscapeUtils.escapeHtml4(summonerName)), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        quer = "";
                        typingName = false;
                        summonerJsonObject = getJsonObjectFromJson(response, summonerName);
                        String summonerName = getStringFromJson(summonerJsonObject, "name");

                        if (!summonerName.equals(""))
                            dbAdapter.insertData(summonerName, Utilities.getRegion(region),
                                    Utilities.getIntFromJson(summonerJsonObject, PROFILE_ICON_ID), getIntFromJson(summonerJsonObject, ID));

                        invalidateOptionsMenu();
                        if (profileMode)
                            startSummonerActivity(Utilities.createSummonerObject(summonerJsonObject, region), MainActivity.this);
                        else getActiveMatch(getIntFromJson(summonerJsonObject, ID), region);
                        editText.setText(quer);
                        isSummonerSelected = false;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isSummonerSelected = false;
                        error.printStackTrace();
                        if (error instanceof NetworkError)
                            showToast("No internet connection", getApplicationContext());
                        else if (error.networkResponse != null && error.networkResponse.statusCode == 404)
                            showToast("Summoner was not found", getApplicationContext());
                        else if (error.networkResponse != null && error.networkResponse.statusCode == 429)
                            showToast("Rate limit exceeded. Please wait " + error.networkResponse.headers.get("Retry-After"), MainActivity.this);

                    }
                });
            } else {
                showToast("No internet connection", getApplicationContext());
                isSummonerSelected=false;
                versionCheck();
            }
        }
    }

/*    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // Handle the normal search query case
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchSummoner(query,region);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            String data = intent.getDataString();
            if (data != null) {
                String[] summoner = dbAdapter.getSummoner(Integer.parseInt(data));
                searchSummoner(summoner[0],Utilities.getRegionSymbol(summoner[1]));

            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.region_button).setTitle(region);
        String title;
        if (profileMode) title = "Profile";
        else title = "Match";
        menu.findItem(R.id.match_or_profile_button).setTitle(title);
     /*   SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typingName = true;

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                quer = newText;
                adapter.setData(quer);
                typingName = true;
                return false;
            }
        });
        if (typingName) {
            showLog("recovering query");
            searchView.setIconified(false);
            searchView.setQuery(quer, false);
        }*/

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.region_button) {
            PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.region_button));
            popupMenu.setOnMenuItemClickListener(this);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.region_menu, popupMenu.getMenu());

            popupMenu.show();
            return true;
        }
        if (id == R.id.match_or_profile_button) {
            PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.match_or_profile_button));
            popupMenu.setOnMenuItemClickListener(this);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.profile_or_match_menu, popupMenu.getMenu());

            popupMenu.show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mSelectedId = menuItem.getItemId();

        Log.e("asd", mSelectedId + "");
        navigate(mSelectedId);
        return true;

    }

    private void navigate(int mSelectedId) {

        switch (mSelectedId) {
            case R.id.navigation_item_1:
                toolbar.setTitle(R.string.app_name);
                if (freeChampFragment != null)
                    getSupportFragmentManager().beginTransaction().remove(freeChampFragment).commit();
                profileMode = true;
                invalidateOptionsMenu();

                break;
            case R.id.navigation_item_2:
                toolbar.setTitle(R.string.navigation_item_2);
                if (freeChampFragment == null)
                    freeChampFragment = FreeChampFragment.newInstance(region);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fab_in, R.anim.fab_out)
                        .replace(R.id.fragment_container, freeChampFragment).commit();


                break;
            case R.id.navigation_item_3:
                toolbar.setTitle(R.string.app_name);
                if (freeChampFragment != null)
                    getSupportFragmentManager().beginTransaction().remove(freeChampFragment).commit();
                profileMode = false;
                invalidateOptionsMenu();
               /* if (summonerJsonObject != null) {

                startActivity(new Intent(this,ActiveMatchActivity.class));
                } else {
                    Toast.makeText(this, "Enter the summoner name", Toast.LENGTH_SHORT).show();
                    mDrawerLayout.closeDrawers();
                    //    searchView.setIconified(false);
                }*/
                break;


        }
        mDrawerLayout.closeDrawers();
    }

    private void getActiveMatch(int id, final String region) {

        requestJsonObject(HTTP + region + URL_ACTIVE_MATCH + getPlatformID(region) + id + API_KEY, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
/*
                    if (activeMatchFragment == null)
                        activeMatchFragment = ActiveMatchFragment.newInstance(region,response.toString());
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.fragment_container, activeMatchFragment).commit();
                    manager.executePendingTransactions();
                    if (activeMatchFragment.activeMatchJson != response && Utilities.getIntFromJson(summonerJsonObject, "id") > 0)
                        activeMatchFragment.getActiveMatch(response,region);*/
                Intent intent = new Intent(MainActivity.this, ActiveMatchActivity.class);
                intent.putExtra(ARG_REGION, region).putExtra(ARG_PARTICIPANTS, response.toString());
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError)
                    showToast("No internet connection", getApplicationContext());
                if (error.networkResponse.statusCode == 404)
                    showToast("Active match was not found", getApplicationContext());
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (freeChampFragment != null && freeChampFragment.isAdded())
            getSupportFragmentManager().putFragment(outState, FREE_CHAMP_STATE, freeChampFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        freeChampFragment = (FreeChampFragment) getSupportFragmentManager().getFragment(savedInstanceState, FREE_CHAMP_STATE);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.euw_item:
                region = "euw";
                //  regionButton.setText("EUW");
                break;
            case R.id.eune_item:
                region = "eune";
                //  regionButton.setText("EUNE");
                break;
            case R.id.na_item:
                region = "na";
                //    regionButton.setText("NA");
                break;
            case R.id.lan_item:
                region = "lan";
                //   regionButton.setText("LAN");
                break;
            case R.id.las_item:
                region = "las";
                //     regionButton.setText("LAS");
                break;
            case R.id.oce_item:
                region = "oce";
                //    regionButton.setText("OCE");
                break;
            case R.id.ru_item:
                region = "ru";
                //    regionButton.setText("RU");
                break;
            case R.id.tr_item:
                region = "tr";
                //    regionButton.setText("TR");
                break;
            case R.id.profile_item:
                profileMode = true;
                break;
            case R.id.match_item:
                profileMode = false;
                break;

        }
        invalidateOptionsMenu();

     /* if(typingName){
            searchView.setQuery("",false);
        }*/
        return true;
    }

    public void versionCheck() {
        requestJsonArray(URL_VERSION_LIST, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                versions = response;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestJsonObject(HTTP + URL_START_GLOBAL + region + URL_VERSION + API_KEY, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    version = response.getString("dd");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error instanceof NetworkError)
                    showToast("No internet connection", getApplicationContext());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(REGION_STATE, region).putBoolean(MODE_STATE, profileMode).apply();

    }

    @Override
    public void onFreeChampClicked() {
        if (freeChampFragment != null)
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fab_out).remove(freeChampFragment).commit();
        setTitle(R.string.app_name);
    }

    class SummonerListAdapter extends RecyclerView.Adapter<SummonerListAdapter.SummonerListViewHolder> {
        ArrayList<Summoner> list;

        void setData(String query) {
            list = dbAdapter.getDataForList(query);

            notifyDataSetChanged();
        }

        @Override
        public SummonerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SummonerListViewHolder(getLayoutInflater().inflate(R.layout.summoner_row, parent, false));

        }

        @Override
        public void onBindViewHolder(SummonerListViewHolder holder, int position) {
            Summoner summoner = list.get(position);
            LoadBitmapTask loadBitmapTask = new LoadBitmapTask(summoner.iconId, holder.icon, MainActivity.this);
            loadBitmapTask.execute();
            holder.nameTxt.setText(summoner.name);
            holder.regionTxt.setText(summoner.region);
            holder.name = summoner.name;
            holder.region = summoner.region;
            holder.summonerId = summoner.id;

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class SummonerListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView icon;
            TextView nameTxt, regionTxt;
            String name, region;
            int summonerId;

            public SummonerListViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.icon);
                nameTxt = (TextView) itemView.findViewById(R.id.name);
                regionTxt = (TextView) itemView.findViewById(R.id.region);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                searchSummoner(name, Utilities.getRegionSymbol(region));
            }
        }
    }


}

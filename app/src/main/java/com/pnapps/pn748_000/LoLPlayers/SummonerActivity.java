package com.pnapps.pn748_000.LoLPlayers;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONObject;

import static com.pnapps.pn748_000.LoLPlayers.Keys.ARG_SUMMONER_OBJECT;

/**
 * Created by pn748_000 on 11/15/2015.
 */
public class SummonerActivity extends AppCompatActivity implements MatchHistory.OnFragmentInteractionListener,SummonerProfile.OnSummonerProfileInteractionListener,ChampionsFragment.StatsListener {
    final static String MATCH_HISTORY_STATE = "match history";
    final static String SUMMONER_PROFILE_STATE = "summoner profile";
    final static String SUMMONER_OBJECT_STATE = "summObject";
    final static String CHAMPIONS_FRAGMENT_STATE="champsFragment";
    private String[] tabNames = { "History","Profile", "Champions", "Ranked Stats"};
    private MatchHistory matchHistory;
    private SummonerProfile summonerProfile;
    private DBAdapter dbAdapter;
    private ChampionsFragment championsFragment;
    //TODO might neeed to initialise these from intent
    private Summoner summoner;
    private StatisticsFragment statisticsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.summoner_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);}
        summoner=getIntent().getExtras().getParcelable(ARG_SUMMONER_OBJECT);
        MyPagerAdapter myPagerAdapter=new MyPagerAdapter(getSupportFragmentManager());
        ViewPager  viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(myPagerAdapter);
        TabLayout tabLayout= (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabsFromPagerAdapter(myPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(1);
        dbAdapter=new DBAdapter(this);
        getSupportActionBar().setTitle(summoner.name);


    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.summoner_activity_menu,menu);

        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) finish();

     //   if(item.getItemId()==R.id.favourite_button) dbAdapter;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SUMMONER_OBJECT_STATE, summoner);
        FragmentManager fm = getSupportFragmentManager();
        if (summonerProfile != null)
            fm.putFragment(outState, SUMMONER_PROFILE_STATE, summonerProfile);
        if (matchHistory != null) fm.putFragment(outState, MATCH_HISTORY_STATE, matchHistory);
        if (championsFragment!=null) fm.putFragment(outState,CHAMPIONS_FRAGMENT_STATE,championsFragment);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        summoner = savedInstanceState.getParcelable(SUMMONER_OBJECT_STATE);
        FragmentManager fm = getSupportFragmentManager();
        summonerProfile = (SummonerProfile) fm.getFragment(savedInstanceState, SUMMONER_PROFILE_STATE);
        matchHistory = (MatchHistory) fm.getFragment(savedInstanceState, MATCH_HISTORY_STATE);
        championsFragment= (ChampionsFragment) fm.getFragment(savedInstanceState,CHAMPIONS_FRAGMENT_STATE);
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSummonerFound(String name) {

    }

    @Override
    public void onStatsReceived(JSONObject stats) {
        Utilities.showLog("onStatsReceived");
        if(statisticsFragment!=null)statisticsFragment.showStats(stats);
    }

    @Override
    public void onListCompleted(int index, Bitmap bitmap,int games) {
            if(summonerProfile!=null)summonerProfile.setFavouriteChamp(index,bitmap,games);
    }


    class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 1:
                    if (summonerProfile == null) summonerProfile = SummonerProfile.newInstance(summoner);
                    return summonerProfile;

                case 0:
                    if (matchHistory == null) matchHistory = MatchHistory.newInstance(summoner.id,summoner.region);
                    return matchHistory;
                case 2:
                    if(championsFragment==null) championsFragment=ChampionsFragment.newInstance(summoner.id,summoner.region);
                    return championsFragment;
                case 3:
                    if(statisticsFragment==null) statisticsFragment=StatisticsFragment.newInstance();
                    return statisticsFragment;
            }

            return new Fragment();
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames[position];
        }
    }
    }

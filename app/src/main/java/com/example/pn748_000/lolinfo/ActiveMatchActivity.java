package com.example.pn748_000.lolinfo;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONObject;

import static com.example.pn748_000.lolinfo.Keys.ARG_SUMMONER_OBJECT;

/**
 * Created by pn748_000 on 11/27/2015.
 */
public class ActiveMatchActivity extends AppCompatActivity {
    final static String MATCH_HISTORY_STATE = "match history";
    final static String SUMMONER_PROFILE_STATE = "summoner profile";
    final static String SUMMONER_OBJECT_STATE = "summObject";
    final static String CHAMPIONS_FRAGMENT_STATE="champsFragment";
    private String[] tabNames = {"Profile", "History", "Champions", "Ranked Stats"};
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


        getSupportActionBar().setTitle(summoner.name);


    }



}

package com.pnapps.pn748_000.LoLPlayers;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import android.text.TextUtils;

/**
 * Created by pn748_000 on 11/6/2015.
 */
public class SuggestionsContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.pn748_000.lolinfo.SuggestionsContentProvider";
    private static final int SUGGESTIONS_SUMMONER = 1;
    private static final int SEARCH_SUMMONER=2;
    private UriMatcher uriMatcher=buildUriMatcher();
    private DBAdapter dbAdapter;
    private UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Suggestion items of Search Dialog is provided by this uri
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,SUGGESTIONS_SUMMONER);
        uriMatcher.addURI(AUTHORITY,"suggestionTable",SEARCH_SUMMONER);



        return uriMatcher;}



    @Override
    public boolean onCreate() {
        dbAdapter=new DBAdapter(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Utilities.showLog("provider query is called "+uri+" "+selection);
        String order=sortOrder;
        switch (uriMatcher.match(uri)){
            case 1:
                if (TextUtils.isEmpty(order)) order = "_ID DESC";
                break;
            default:
                break;
        }

        return dbAdapter.getData(selection,selectionArgs,order);
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}

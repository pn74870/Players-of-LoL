package com.pnapps.pn748_000.PlayersOfLoL;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pn748_000 on 8/23/2015.
 */
public class DBAdapter {
    private  static final int MAX_NUMBER_OF_ROWS =10;
    private static final HashMap<String,String> map=projectionMap();
    DBHelper dbHelper;

    public DBAdapter(Context context){
        dbHelper=new DBHelper(context);
    }
    public long insertData(String name,String region,int iconId,int id){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
       if(DatabaseUtils.queryNumEntries(db,DBHelper.TABLE_NAME)>= MAX_NUMBER_OF_ROWS){
            deleteTheLastRow();
       }
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBHelper.NAME, name);
        contentValues.put(DBHelper.REGION, region);
        contentValues.put(DBHelper.ICON_RES, iconId);
        contentValues.put(DBHelper.SUMMONER_ID, id);

        return db.insertWithOnConflict(DBHelper.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public Cursor getData(String selection,String[] selectionArgs, String sortOrder){
        SQLiteQueryBuilder builder=new SQLiteQueryBuilder();
        builder.setTables(DBHelper.TABLE_NAME);
        builder.setProjectionMap(map);
        if(selectionArgs!=null) selectionArgs[0] = "%"+selectionArgs[0] + "%";
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String[] projection={DBHelper._ID,SearchManager.SUGGEST_COLUMN_TEXT_1,SearchManager.SUGGEST_COLUMN_TEXT_2,SearchManager.SUGGEST_COLUMN_INTENT_DATA,SearchManager.SUGGEST_COLUMN_ICON_1};
        return builder.query(db, projection, DBHelper.NAME + selection, selectionArgs, null, null, sortOrder);

    }
    public ArrayList<Summoner> getDataForList(String query){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        ArrayList<Summoner> summoners=new ArrayList<>();
        Cursor cursor= db.query(DBHelper.TABLE_NAME, new String[]{DBHelper.SUMMONER_ID, DBHelper.NAME, DBHelper.REGION, DBHelper.ICON_RES}, DBHelper.NAME + " LIKE ? ", new String[]{query+"%"}, null, null, DBHelper._ID+" DESC");
       // Cursor cursor=db.query(DBHelper.TABLE_NAME, new String[]{DBHelper.SUMMONER_ID, DBHelper.NAME, DBHelper.REGION, DBHelper.ICON_RES}, null,null, null, null, DBHelper._ID + " DESC");
        int nameColumn=cursor.getColumnIndex(DBHelper.NAME);
        int idColumn=cursor.getColumnIndex(DBHelper.SUMMONER_ID);
        int iconColumn=cursor.getColumnIndex(DBHelper.ICON_RES);
        int regionColumn=cursor.getColumnIndex(DBHelper.REGION);
        while (cursor.moveToNext()){
            summoners.add(new Summoner(cursor.getString(nameColumn),cursor.getInt(idColumn),cursor.getInt(iconColumn),0,cursor.getString(regionColumn)));
        }
        cursor.close();
        return summoners;
    }
    private void deleteTheLastRow(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        Cursor cursor=db.query(DBHelper.TABLE_NAME, new String[]{DBHelper._ID}, null, null, null, null, DBHelper._ID, " 1");
        cursor.moveToFirst();
        int lastRowId=cursor.getInt(cursor.getColumnIndex(DBHelper._ID));
        db.delete(DBHelper.TABLE_NAME, DBHelper._ID + " = ?", new String[]{String.valueOf(lastRowId)});
        cursor.close();
    }
    public void deleteRow(int summonerId){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_NAME,DBHelper.SUMMONER_ID+ " = ?",new String[]{summonerId+""});
    }
    public String[] getSummoner(int id){
        String[] args= {String.valueOf(id)};
        String[] summoner=new String[2];
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_NAME, new String[]{DBHelper.NAME, DBHelper.REGION}, DBHelper._ID + "= ?", args, null,null,DBHelper._ID+" DECS");
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            summoner[0]=cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
            summoner[1]=cursor.getString(cursor.getColumnIndex(DBHelper.REGION));
        }
        cursor.close();
        return summoner;
    }
   private static HashMap<String,String> projectionMap(){
        HashMap<String,String> map=new HashMap<>();
        map.put(DBHelper._ID,DBHelper._ID+" as "+DBHelper._ID);
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_1,DBHelper.NAME+" as "+SearchManager.SUGGEST_COLUMN_TEXT_1);
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_2,DBHelper.REGION+" as "+SearchManager.SUGGEST_COLUMN_TEXT_2);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA,DBHelper._ID+" as "+SearchManager.SUGGEST_COLUMN_INTENT_DATA);
        map.put(SearchManager.SUGGEST_COLUMN_ICON_1, DBHelper.ICON_RES + " as " + SearchManager.SUGGEST_COLUMN_ICON_1);
        return map;
    }

  static class DBHelper  extends SQLiteOpenHelper implements BaseColumns {
      private static final String DATABASE_NAME="suggestionDatabase";
      private static final String TABLE_NAME="suggestionTable";
      private static final int DATABASE_VERSION=15;
      private static final String NAME = "name";
      private static final String ICON_RES ="iconResource";
      private static final String SUMMONER_ID="summonerID";
      private static final String REGION ="region";
      private static final String VARCHAR=" VARCHAR(255)";
      private static final String COMMA=",";
      private static final String UNIQUE=" UNIQUE";
      private static final String INT=" INTEGER, ";
      private static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
              +SUMMONER_ID+INT+ NAME +VARCHAR+COMMA+ REGION +VARCHAR+COMMA+ ICON_RES +INT+UNIQUE+"("+ SUMMONER_ID +")"+")";
      private static final String SQL_DELETE_ENTRIES ="DROP TABLE IF EXISTS " +TABLE_NAME;


      public DBHelper(Context context) {
          super(context, DATABASE_NAME,null, DATABASE_VERSION);
       }


       @Override
       public void onCreate(SQLiteDatabase sqLiteDatabase) {
           try {
               sqLiteDatabase.execSQL(CREATE_TABLE);

           } catch (SQLException e){
               e.printStackTrace();
           }
       }
       @Override
       public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
           try {
               sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
               onCreate(sqLiteDatabase);

           } catch (SQLException e){
              e.printStackTrace();
           }
       }
   }
}

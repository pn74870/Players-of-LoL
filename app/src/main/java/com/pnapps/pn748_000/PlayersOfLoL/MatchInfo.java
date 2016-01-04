package com.pnapps.pn748_000.PlayersOfLoL;

import android.os.Parcel;
import android.os.Parcelable;

public class MatchInfo implements Parcelable {
    int kills,assists,deaths,creeps,gold,id,matchIndexInList; //id might be unused
    long date,duration;
    int [] items;
    boolean win;
    String champIcon,type,spell1,spell2;


    public MatchInfo(int k, int a, int d, int c, int g, String icon, int i, boolean w,String typ,String spel1,String spel2, int[] itemss,long datee, int dur, int in){
        kills=k;
        assists=a;
        deaths=d;
        creeps=c;
        gold=g;
        champIcon=icon;
        id=i;
        win=w;
        type=typ;
        spell1=spel1;
        spell2=spel2;
        items=itemss;
        date=datee;
        duration=dur;
        matchIndexInList=in;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
       parcel.writeInt(kills);
       parcel.writeInt(assists);
        parcel.writeInt(deaths);
        parcel.writeInt(creeps);
        parcel.writeInt(gold);
        parcel.writeInt(id);
        parcel.writeString(champIcon);
        parcel.writeString(type);
        if(win) parcel.writeInt(1);
        else parcel.writeInt(0);

    }
    public static final Parcelable.Creator<MatchInfo> CREATOR
            = new Parcelable.Creator<MatchInfo>() {
        public MatchInfo createFromParcel(Parcel in) {
            return new MatchInfo(in);
        }

        public MatchInfo[] newArray(int size) {
            return new MatchInfo[size];
        }
    };

    private MatchInfo(Parcel in) {
        kills=in.readInt();
        assists=in.readInt();
        deaths=in.readInt();
        creeps=in.readInt();
        gold=in.readInt();
        id=in.readInt();
        champIcon=in.readString();
        win=in.readInt()==1;
        type=in.readString();

    }
}

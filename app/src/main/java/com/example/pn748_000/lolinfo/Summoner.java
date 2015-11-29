package com.example.pn748_000.lolinfo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pn748_000 on 11/20/2015.
 */
public class Summoner implements Parcelable {
    String name,region;
    int id,iconId,level;
public Summoner(String name,int id,int iconId,int level,String region){
    this.name=name;
    this.id=id;
    this.iconId=iconId;
    this.level=level;
    this.region=region;
}
    private Summoner(Parcel in) {
        name = in.readString();
        id = in.readInt();
        iconId = in.readInt();
        level = in.readInt();
        region=in.readString();
    }

    public static final Creator<Summoner> CREATOR = new Creator<Summoner>() {
        @Override
        public Summoner createFromParcel(Parcel in) {
            return new Summoner(in);
        }

        @Override
        public Summoner[] newArray(int size) {
            return new Summoner[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
        parcel.writeInt(iconId);
        parcel.writeInt(level);
        parcel.writeString(region);
    }
}

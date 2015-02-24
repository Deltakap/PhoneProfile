package com.example.student.phoneprofile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileDB extends SQLiteOpenHelper{

    private static final String name = "profile.sqlite3";
    private static final int version = 7;

    public ProfileDB(Context ctx){
        super(ctx,name,null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE profile ("+
                "_id integer primary key autoincrement,"+
                "pname text not null,"+
                "wifi integer not null,"+
                "data integer not null,"+
                "bluetooth integer not null,"+
                "sound integer not null,"+
                "ringVol integer not null,"+
                "mediaVol integer not null,"+
                "brightness integer not null,"+
                "fromTime text,"+
                "toTime text);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS profile";
        db.execSQL(sql);
        this.onCreate(db);
    }
}

package com.example.student.phoneprofile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;

import java.util.Calendar;

public class applyManager extends AsyncTask<String,Void,Boolean> {

    private int automaticPro = 0;
    private int manPro = 0;
    ProfileDB helper;
    String line;
    StringBuilder buffer = new StringBuilder();
    private boolean res;
    private Context context;

    public applyManager(Context context){
        this.context = context;
        helper = new ProfileDB(context);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        automaticChk();
        return true;
    }

    public void setAuto(int p){
        automaticPro = p;
    }

    public void setMan(int p){
        manPro =p;
    }

    public long getAuto(){
        return automaticPro;
    }

    public long getMan(){
        return manPro;
    }

    private void automaticChk(){
        Calendar now = Calendar.getInstance();
        int nowhour = now.get(Calendar.HOUR_OF_DAY);
        int nowminute = now.get(Calendar.MINUTE);

        String nowhs = String.format("%02d",nowhour);
        String nowms = String.format("%02d",nowminute);

        String nowtime = nowhs+":"+nowms;

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profile WHERE datetime(fromTime) = " +
                "datetime('2015-02-24 "+nowtime+"');",null);
        c.moveToFirst();
        if(c.getCount() == 1){
            automaticPro = c.getInt(0);
        }
        else
            automaticPro = 0;
    }

    private void systemChk(){
        SQLiteDatabase db = helper.getReadableDatabase();
    }
}

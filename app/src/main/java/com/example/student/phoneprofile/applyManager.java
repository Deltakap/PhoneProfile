package com.example.student.phoneprofile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.Calendar;

public class applyManager extends AsyncTask<String,Void,Boolean> {

    private int automaticPro = 0;
    private int manPro = 0;
    ProfileDB helper;
    String line;
    StringBuilder buffer = new StringBuilder();
    private boolean res;
    private Context context;
    long applyID;

    int wifi;
    int data;
    int bt;
    int soundmode;
    int ringVol;
    int mediaVol;
    int brightness;

    private boolean isChange = false;

    public applyManager(Context context){
        this.context = context;
        helper = new ProfileDB(context);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        applyID = Long.parseLong(params[0]);
        wifi = Integer.parseInt(params[1]);
        data = Integer.parseInt(params[2]);
        bt = Integer.parseInt(params[3]);
        soundmode = Integer.parseInt(params[4]);
        ringVol = Integer.parseInt(params[5]);
        mediaVol = Integer.parseInt(params[6]);
        brightness = Integer.parseInt(params[7]);

        automaticChk();
        systemChk();
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

    public boolean getIsChange(){
        return isChange;
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
        Cursor c = db.rawQuery("SELECT * FROM profile WHERE _id = "+applyID,null);
        c.moveToFirst();
        if(c.getCount() == 1) {
            if (c.getInt(2) != 0 && c.getInt(2) != wifi) {
                Log.d("user","wifi");
                isChange = true;
                return;
            } else if (c.getInt(3) != 0 && c.getInt(3) != data) {
                Log.d("user","data");
                isChange = true;
                return;
            } else if (c.getInt(4) != 0 && c.getInt(4) != bt) {
                Log.d("user","bt");
                isChange = true;
                return;
            } else if (c.getInt(5) != 0 && c.getInt(5) != soundmode) {
                Log.d("user","soundmode");
                isChange = true;
                return;
            } else if (c.getInt(6) != -1 && c.getInt(6) != ringVol) {
                Log.d("user","ringVol");
                isChange = true;
                return;
            } else if (c.getInt(7) != -1 && c.getInt(7) != mediaVol) {
                Log.d("user","mediaVol");
                isChange = true;
                return;
            } else if (c.getInt(8) != -2 && c.getInt(8) != brightness) {
                Log.d("user","brightness");
                isChange = true;
                return;
            } else
                isChange = false;
        }
    }
}

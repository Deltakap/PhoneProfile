package com.example.student.phoneprofile;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, ActionMode.Callback {

    ProfileDB helper;
    SimpleCursorAdapter adapter;
    long selectedId;
    ActionMode actionMode;
    long applyId;
    int userId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        StayLoginTask st = new StayLoginTask();
        try {
            st.execute(android_id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        userId = st.getUser_id();

        showList();
    }

    private void showList(){
        helper = new ProfileDB(this.getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id,pname FROM profile ORDER BY _id;",null);

        adapter= new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,cursor,
                new String[] {"pname"}, new int[] {android.R.id.text1});

        ListView lv = (ListView)findViewById(R.id.profilelist);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);
        db.close();
    }

    private void applyCheck(){
        Calendar now = Calendar.getInstance();
        int nowhour = now.get(Calendar.HOUR_OF_DAY);
        int nowminute = now.get(Calendar.MINUTE);

        String nowhs = String.format("%02d",nowhour);
        String nowms = String.format("%02d",nowminute);

        String nowtime = nowhs+":"+nowms;

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profile WHERE datetime('2015-02-24 "+nowtime+"')" +
                " BETWEEN datetime(fromTime) AND datetime(toTime) OR datetime('2015-02-25 " +
                nowtime+"') BETWEEN datetime(fromTime) AND datetime(toTime);",null);
        c.moveToFirst();

        if(c.getCount() == 1 && applyId != c.getInt(0)){

            String pname = c.getString(1);
            applyId = c.getInt(0);

            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Profile Suggestion");
            alertDialog.setMessage("Would you like to switch to profile "+pname+"?");

            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE
                    , "Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        Apply();
                        alertDialog.dismiss();
                    }
                }
            });

            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    "No",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

            alertDialog.show();
        }
    }

    protected void onResume(){
        super.onResume();
        showList();
        applyCheck();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(userId != 0){
            menu.clear();
            menu.add(1,R.id.bcloud,100,"Backup to Cloud");
            menu.add(1,R.id.rcloud,100,"Restore from Cloud");
            menu.add(1,R.id.action_logout,100,"Sign Out");
        }
        else{
            menu.clear();
            menu.add(1,R.id.action_login,100,"Sign In");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent i = new Intent(this,Login.class);
            startActivityForResult(i, 25);
            return true;
        }

        if(id == R.id.action_logout){
            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            SignOutTask s = new SignOutTask();
            s.execute(android_id);

            userId = 0;
            Toast.makeText(getApplicationContext(),
                    "Sign out successfully",Toast.LENGTH_SHORT).show();
        }

        if(id == R.id.bcloud){
            BackupCloudTask b = new BackupCloudTask(getApplicationContext());
            b.execute(Integer.toString(userId));
        }

        if(id == R.id.rcloud){
            RestoreCloudTask r = new RestoreCloudTask(getApplicationContext());
            try {
                r.execute(Integer.toString(userId)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(r.getRes()){
                helper = new ProfileDB(this.getApplicationContext());
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT _id,pname FROM profile ORDER BY _id;",null);
                adapter.changeCursor(cursor);
                db.close();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_actionmode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                deleteClicked();
                mode.finish();
                break;
            case R.id.menu_edit:
                editClicked();
                mode.finish();
                break;
            default:
                return false;
        }
        return true;
    }

    private void editClicked(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profile WHERE _id="+Long.toString(selectedId)+";"
                ,null);
        if(c.getCount() == 1){
            c.moveToFirst();
            String pname = c.getString(1);
            int wifi = c.getInt(2);
            int sound = c.getInt(5);
            int bt = c.getInt(4);
            int data = c.getInt(3);
            int ringVol = c.getInt(6);
            int mediaVol = c.getInt(7);
            int brightness = c.getInt(8);
            String fromTime = c.getString(9);
            String toTime = c.getString(10);

            Intent i = new Intent(this,AddProfile.class);
            i.putExtra("pname",pname);
            i.putExtra("wifi",wifi);
            i.putExtra("data",data);
            i.putExtra("bt",bt);
            i.putExtra("sound",sound);
            i.putExtra("brightness",brightness);
            i.putExtra("ringVol", ringVol);
            i.putExtra("mediaVol",mediaVol);
            i.putExtra("fromTime",fromTime);
            i.putExtra("toTime",toTime);

            startActivityForResult(i,12);
        }
    }

    private void deleteClicked(){
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowCount = db.delete("profile", "_id = ?",
                new String[]{Long.toString(selectedId)});
        if (rowCount == 1) {
            Toast t = Toast.makeText(this.getApplicationContext(),
                    "Profile Deleted", Toast.LENGTH_SHORT);
            t.show();
        }
        Cursor cursor = db.rawQuery("SELECT _id,pname FROM profile ORDER BY _id;",null);
        adapter.changeCursor(cursor);
        db.close();
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
    }

    public void buttonClicked(View v){
        int id = v.getId();
        Intent i;

        switch(id){
            case R.id.addbt:
                i = new Intent(this,AddProfile.class);
                startActivityForResult(i,10);
                break;

            default: break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if(requestCode == 10 || requestCode == 12){
            if(resultCode == RESULT_OK) {
                String pname = i.getStringExtra("pname");
                int wifi = i.getIntExtra("wifi", -2);
                int data = i.getIntExtra("data", -2);
                int bt = i.getIntExtra("bt", -2);
                int sound = i.getIntExtra("sound", 0);
                int ringVol = i.getIntExtra("ringVol",-1);
                int mediaVol = i.getIntExtra("mediaVol",-1);
                int brightness = i.getIntExtra("brightness",-2);
                String fromTime = i.getStringExtra("fromTime");
                String toTime = i.getStringExtra("toTime");

                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues c = new ContentValues();
                c.put("pname", pname);
                c.put("wifi", wifi);
                c.put("data", data);
                c.put("bluetooth", bt);
                c.put("sound", sound);
                c.put("mediaVol",mediaVol);
                c.put("ringVol",ringVol);
                c.put("brightness",brightness);
                c.put("fromTime",fromTime);
                c.put("toTime",toTime);

                if(requestCode == 10) {
                    long testID = db.insert("profile", null, c);
                    db.close();

                    if (testID != -1) {
                        Toast t = Toast.makeText(this.getApplicationContext(), pname +
                                " profile is added",
                                Toast.LENGTH_SHORT);
                        t.show();
                    }
                }

                if(requestCode == 12) {
                    long testID = db.update("profile",c,"_id=?",
                            new String[]{Long.toString(selectedId)});
                    db.close();

                    if (testID != -1) {
                        Toast t = Toast.makeText(this.getApplicationContext(), pname +
                                        " profile is updated",
                                Toast.LENGTH_SHORT);
                        t.show();
                    }
                }
            }
        }
        if(requestCode == 25){
            if(resultCode == RESULT_OK){
                Toast.makeText(getApplicationContext(),
                        "Login Successfully",Toast.LENGTH_SHORT).show();
                userId = i.getIntExtra("user_id",-1);
            }
        }
    }

    private void toggleWifi(int status){
        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        if(status == 1){
            wifiManager.setWifiEnabled(true);
        }
        else if(status == 2){
            wifiManager.setWifiEnabled(false);
        }
    }

    private void setRing(int status){
        AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        switch(status){
            case 1: audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    break;
            case 2: audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
            case 3: audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    break;
            default:break;
        }
    }

    private void toggleBT(int status){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(status == 1)
            bluetoothAdapter.enable();
        else
            bluetoothAdapter.disable();
    }

    private void setRingVol(int status){
        AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getRingerMode();
        if(mode == audioManager.RINGER_MODE_NORMAL) {
            audioManager.setStreamVolume(audioManager.STREAM_RING, status, 0);
            audioManager.setStreamVolume(audioManager.STREAM_NOTIFICATION, status, 0);
        }
    }

    private void setMediaVol(int status){
        AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(audioManager.STREAM_MUSIC,status, 0);
    }

    private void setBrightness(int status){
        if(status != -1){
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,status);
        }
        else
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    private void toggleData(int status){
        ConnectivityManager dataManager;
        dataManager  = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        Method dataMtd = null;
        try {
            dataMtd = ConnectivityManager.class.getDeclaredMethod
                    ("setMobileDataEnabled", boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if(dataMtd != null) {
            dataMtd.setAccessible(true);
            if (status == 1) {
                try {
                    dataMtd.invoke(dataManager, true);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    dataMtd.invoke(dataManager, false);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setSelected(true);
        applyId = id;
        //Log.d("user","SELECT * FROM profile WHERE _id="+Long.toString(selectedId));

        Apply();
    }

    public void Apply()
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profile WHERE _id="+Long.toString(applyId)+";"
                ,null);

        c.moveToFirst();
        String pname = c.getString(1);
        int wifi = c.getInt(2);
        int sound = c.getInt(5);
        int bt = c.getInt(4);
        int data = c.getInt(3);
        int ringVol = c.getInt(6);
        int mediaVol = c.getInt(7);
        int brightness = c.getInt(8);

        if(wifi != 0)
            toggleWifi(wifi);
        if(sound != 0)
            setRing(sound);
        if(bt != 0)
            toggleBT(bt);
        if(ringVol != -1)
            setRingVol(ringVol);
        if(mediaVol != -1)
            setMediaVol(mediaVol);
        if(brightness != -2)
            setBrightness(brightness);
        if(data != 0)
            toggleData(data);

        Toast.makeText(this.getApplicationContext(),pname+" profile is applied",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        view.setSelected(true);
        selectedId = id;
        actionMode = this.startActionMode(this);
        return true;
    }
}

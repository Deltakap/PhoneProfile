package com.example.student.phoneprofile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ProfileDB helper;
    SimpleCursorAdapter adapter;
    long selectedId;
    ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showList();
    }

    private void showList(){
        helper = new ProfileDB(this.getApplicationContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id,pname FROM profile ORDER BY _id",null);

        adapter= new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,cursor,
                new String[] {"pname"}, new int[] {android.R.id.text1});

        ListView lv = (ListView)findViewById(R.id.profilelist);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        db.close();
    }

    protected void onResume(){
        super.onResume();
        showList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        if(requestCode == 10){
            if(resultCode == RESULT_OK) {
                String pname = i.getStringExtra("pname");
                int wifi = i.getIntExtra("wifi", -2);
                int data = i.getIntExtra("data", -2);
                int bt = i.getIntExtra("bt", -2);
                int gps = i.getIntExtra("gps", -2);
                int sound = i.getIntExtra("sound", 0);

                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues c = new ContentValues();
                c.put("pname", pname);
                c.put("wifi", wifi);
                c.put("data", data);
                c.put("bluetooth", bt);
                c.put("gps", gps);
                c.put("sound", sound);

                long testID = db.insert("profile", null, c);
                db.close();

                if(testID != -1){
                    Toast t = Toast.makeText(this.getApplicationContext(),pname+" profile is added",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        }
    }

    private void toggleWifi(int status){
        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        if(status == 1 && !wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

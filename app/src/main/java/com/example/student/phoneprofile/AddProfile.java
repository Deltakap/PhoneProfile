package com.example.student.phoneprofile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class AddProfile extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private int wifi;
    private int data;
    private int bt;
    private int gps;
    private int sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this,R.array.optionarray,android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource
                (this,R.array.soundarray,android.R.layout.simple_spinner_item);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner wifisp = (Spinner)findViewById(R.id.wifispin);
        Spinner datasp = (Spinner)findViewById(R.id.dataspin);
        Spinner btsp = (Spinner)findViewById(R.id.bluetoothspin);
        Spinner gpssp = (Spinner)findViewById((R.id.gpsspin));
        Spinner soundsp = (Spinner)findViewById(R.id.soundspin);

        wifisp.setAdapter(adapter);
        datasp.setAdapter(adapter);
        btsp.setAdapter(adapter);
        gpssp.setAdapter(adapter);
        soundsp.setAdapter(adapter2);

        wifisp.setOnItemSelectedListener(this);
        datasp.setOnItemSelectedListener(this);
        btsp.setOnItemSelectedListener(this);
        gpssp.setOnItemSelectedListener(this);
        soundsp.setOnItemSelectedListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_profile, menu);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int selid = parent.getId();
        if(selid == R.id.wifispin){
            if(id == 0)
                wifi = -1;
            if(id == 1)
                wifi = 1;
            if(id == 2)
                wifi = 0;
        }
        if(selid == R.id.dataspin){
            if(id == 0)
                data = -1;
            if(id == 1)
                data = 1;
            if(id == 2)
                data = 0;
        }
        if(selid == R.id.bluetoothspin){
            if(id == 0)
                bt = -1;
            if(id == 1)
                bt = 1;
            if(id == 2)
                bt = 0;
        }
        if(selid == R.id.gpsspin){
            if(id == 0)
                gps = -1;
            if(id == 1)
                gps = 1;
            if(id == 2)
                gps = 0;
        }
        if(selid == R.id.soundspin){
            if(id == 0)
                sound = 1;
            if(id == 1)
                sound = 2;
            if(id == 2)
                sound = 3;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void SaveClicked(View v){
        TextView tv = (TextView)findViewById(R.id.pname);
        String pname = tv.getText().toString();

        Intent i = new Intent();
        i.putExtra("pname",pname);
        i.putExtra("wifi",wifi);
        i.putExtra("data",data);
        i.putExtra("bt",bt);
        i.putExtra("gps",gps);
        i.putExtra("sound",sound);

        this.setResult(RESULT_OK,i);
        this.finish();
    }
}

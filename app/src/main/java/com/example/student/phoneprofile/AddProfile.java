package com.example.student.phoneprofile;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class AddProfile extends ActionBarActivity implements AdapterView.OnItemSelectedListener,
        SeekBar.OnSeekBarChangeListener, View.OnClickListener, View.OnFocusChangeListener {

    private int wifi;
    private int data=-1;
    private int bt;
    private int sound;
    private int ringVol;
    private int mediaVol;
    private int brightness;
    private boolean ovrChk;
    private boolean ovrMedChk;
    private boolean briChk;
    private boolean autobright;
    private boolean timeDetect;
    private String fromTime;
    private String toTime;

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
        Spinner soundsp = (Spinner)findViewById(R.id.soundspin);

        wifisp.setAdapter(adapter);
        datasp.setAdapter(adapter);
        btsp.setAdapter(adapter);
        soundsp.setAdapter(adapter2);

        wifisp.setOnItemSelectedListener(this);
        datasp.setOnItemSelectedListener(this);
        btsp.setOnItemSelectedListener(this);
        soundsp.setOnItemSelectedListener(this);

        SeekBar sb1 = (SeekBar)findViewById(R.id.ringVol);
        SeekBar sb2 = (SeekBar)findViewById(R.id.mediaVol);
        SeekBar sb3 = (SeekBar)findViewById(R.id.brightBar);
        CheckBox cb1 = (CheckBox)findViewById(R.id.autobright);
        EditText fromTimeT = (EditText)findViewById(R.id.fromTime);
        EditText toTimeT = (EditText)findViewById(R.id.toTime);

        fromTimeT.setEnabled(false);
        toTimeT.setEnabled(false);
        fromTimeT.setOnClickListener(this);
        fromTimeT.setOnFocusChangeListener(this);
        toTimeT.setOnClickListener(this);
        toTimeT.setOnFocusChangeListener(this);
        fromTimeT.setInputType(0);
        toTimeT.setInputType(0);

        sb1.setOnSeekBarChangeListener(this);
        sb2.setOnSeekBarChangeListener(this);
        sb3.setOnSeekBarChangeListener(this);

        sb1.setEnabled(false);
        sb2.setEnabled(false);
        sb3.setEnabled(false);
        cb1.setEnabled(false);

        AudioManager audioManager = (AudioManager)this.getSystemService(AUDIO_SERVICE);
        sb1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        sb2.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        Intent i = this.getIntent();
        if(i.hasExtra("wifi")){
            setTitle("Edit Profile");

            String pname = i.getStringExtra("pname");
            wifi = i.getIntExtra("wifi", -2);
            data = i.getIntExtra("data", -2);
            bt = i.getIntExtra("bt", -2);
            sound = i.getIntExtra("sound", 0);
            ringVol = i.getIntExtra("ringVol",-1);
            mediaVol = i.getIntExtra("mediaVol",-1);
            brightness = i.getIntExtra("brightness",-2);
            fromTime = i.getStringExtra("fromTime");
            toTime = i.getStringExtra("toTime");

            EditText et = (EditText)findViewById(R.id.pname);
            et.setText(pname);
            wifisp.setSelection(wifi);
            datasp.setSelection(data);
            btsp.setSelection(bt);
            soundsp.setSelection(sound);
            if(mediaVol != -1){
                CheckBox mediaChk = (CheckBox)findViewById(R.id.mediaChk);
                mediaChk.setChecked(true);
                ovrMedChk = true;
                sb2.setEnabled(true);
                sb2.setProgress(mediaVol);
            }
            if(ringVol != -1){
                CheckBox ringChk = (CheckBox)findViewById(R.id.ovrVol);
                ringChk.setChecked(true);
                ovrChk = true;
                sb1.setEnabled(true);
                sb1.setProgress(ringVol);
            }
            if(brightness != -2){
                CheckBox ovrBri = (CheckBox)findViewById(R.id.ovrBri);
                ovrBri.setChecked(true);
                briChk = true;
                cb1.setEnabled(true);
                if(brightness == -1){
                    cb1.setChecked(true);
                    autobright = true;
                    sb3.setEnabled(false);
                }
                else{
                    sb3.setEnabled(true);
                    sb3.setProgress(brightness);
                }
            }
            if(!fromTime.equals("")){
                CheckBox timeChk = (CheckBox)findViewById(R.id.timeBox);
                timeChk.setChecked(true);
                timeDetect = true;
                fromTimeT.setEnabled(true);
                toTimeT.setEnabled(true);

                fromTimeT.setText(fromTime);
                toTimeT.setText(toTime);
            }
        }
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
            wifi = (int) id;
        }
        if(selid == R.id.dataspin){
            data = (int)id;
        }
        if(selid == R.id.bluetoothspin){
            bt = (int)id;
        }
        if(selid == R.id.soundspin){
           sound = (int)id;
           onOvrVolClicked(findViewById(R.id.ovrVol));
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
        i.putExtra("sound",sound);

        if(briChk){
            if(autobright)
                i.putExtra("brightness",-1);
            else
                i.putExtra("brightness",brightness);
        }

        if(ovrChk){
            if(sound == 0 || sound == 1) {
                i.putExtra("ringVol", ringVol);
            }
        }

        if(ovrMedChk)
            i.putExtra("mediaVol",mediaVol);

        if(timeDetect) {
            EditText eft = (EditText) findViewById(R.id.fromTime);
            EditText ett = (EditText) findViewById(R.id.toTime);

            fromTime = eft.getText().toString();
            toTime = ett.getText().toString();

            i.putExtra("fromTime", "2015-02-24 "+fromTime);
            if(fromTime.compareTo(toTime)>0)
                i.putExtra("toTime", "2015-02-25 "+toTime);
            else
                i.putExtra("toTime", "2015-02-24 "+toTime);
        }

        this.setResult(RESULT_OK,i);
        this.finish();
    }

    public void onOvrVolClicked(View v){
        int id = v.getId();
        SeekBar sb1 = (SeekBar)findViewById(R.id.ringVol);
        SeekBar sb2 = (SeekBar)findViewById(R.id.mediaVol);

        switch (id){
            case R.id.ovrVol: ovrChk = ((CheckBox)v).isChecked();
                              break;
            case R.id.mediaChk: ovrMedChk = ((CheckBox)v).isChecked();
                                break;
            default:break;
        }

        if(ovrChk){
            if(sound == 1 || sound == 0)
                sb1.setEnabled(true);
            else
                sb1.setEnabled(false);
        }
        else
            sb1.setEnabled(false);

        if(ovrMedChk)
            sb2.setEnabled(true);
        else
            sb2.setEnabled(false);
    }

    public void onBrightClicked(View v){
        int id = v.getId();
        SeekBar sb1 = (SeekBar)findViewById(R.id.brightBar);
        CheckBox cb1 = (CheckBox)findViewById(R.id.autobright);
        if(id == R.id.autobright) {
            autobright = ((CheckBox)v).isChecked();
            if (!autobright) {
                sb1.setEnabled(true);
            } else {
                sb1.setEnabled(false);
            }
        }
        if(id == R.id.ovrBri){
            briChk = ((CheckBox) v).isChecked();
            if(briChk){
                cb1.setEnabled(true);
                if(!autobright)
                    sb1.setEnabled(true);
            }
            else{
                sb1.setEnabled(false);
                cb1.setEnabled(false);
            }
        }
    }

    public void onTimeClicked(View v){
        EditText fromTime = (EditText)findViewById(R.id.fromTime);
        EditText toTime = (EditText)findViewById(R.id.toTime);
        timeDetect = ((CheckBox)v).isChecked();
        if(timeDetect){
            fromTime.setEnabled(true);
            toTime.setEnabled(true);
        }
        else{
            fromTime.setEnabled(false);
            toTime.setEnabled(false);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar == (SeekBar)findViewById(R.id.ringVol)){
            ringVol = progress;
        }
        if(seekBar == (SeekBar)findViewById(R.id.mediaVol)){
            mediaVol = progress;
        }
        if(seekBar == (SeekBar)findViewById(R.id.brightBar)){
            brightness = progress;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        final EditText editText = (EditText)findViewById(v.getId());
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddProfile.this,
                new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editText.setText(String.format("%02d",hourOfDay)+":"
                                +String.format("%02d",minute));
                    }
                },hour,minute,true);

        timePickerDialog.setTitle("Set Time");
        timePickerDialog.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            final EditText editText = (EditText)findViewById(v.getId());
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddProfile.this,
                    new TimePickerDialog.OnTimeSetListener(){
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            editText.setText(String.format("%02d",hourOfDay)+":"
                                    +String.format("%02d",minute));
                        }
                    },hour,minute,true);

            timePickerDialog.setTitle("Set Time");
            timePickerDialog.show();
        }
    }
}

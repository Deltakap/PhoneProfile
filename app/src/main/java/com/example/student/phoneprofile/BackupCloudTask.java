package com.example.student.phoneprofile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class BackupCloudTask  extends AsyncTask<String,Void,Boolean> {

    ProfileDB helper;
    String line;
    StringBuilder buffer = new StringBuilder();
    boolean res = true;
    private Context context;

    public BackupCloudTask(Context context){
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String user_id = params[0];
        HttpClient h = new DefaultHttpClient();
        HttpPost p = new HttpPost
                ("http://ict.siit.tu.ac.th/~u5522781962/PPandroid/backup.php");

        HttpPost p2 = new HttpPost
                ("http://ict.siit.tu.ac.th/~u5522781962/PPandroid/predel.php");
        List<NameValuePair> v = new ArrayList<NameValuePair>();
        v.add(new BasicNameValuePair("user_id",user_id));
        try {
            p2.setEntity(new UrlEncodedFormEntity(v));
            h.execute(p2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        helper = new ProfileDB(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM profile;",null);

        while (c.moveToNext()){
            List<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("user_id",user_id));
            values.add(new BasicNameValuePair("profile_id",Integer.toString(c.getInt(0))));
            values.add(new BasicNameValuePair("profile_name",c.getString(1)));
            values.add(new BasicNameValuePair("wifi",Integer.toString(c.getInt(2))));
            values.add(new BasicNameValuePair("data",Integer.toString(c.getInt(3))));
            values.add(new BasicNameValuePair("bt",Integer.toString(c.getInt(4))));
            values.add(new BasicNameValuePair("sound",Integer.toString(c.getInt(5))));
            values.add(new BasicNameValuePair("ringVol",Integer.toString(c.getInt(6))));
            values.add(new BasicNameValuePair("mediaVol",Integer.toString(c.getInt(7))));
            values.add(new BasicNameValuePair("brightness",Integer.toString(c.getInt(8))));
            values.add(new BasicNameValuePair("fromTime",c.getString(9)));
            values.add(new BasicNameValuePair("toTime",c.getString(10)));

            try{
                p.setEntity(new UrlEncodedFormEntity(values));
                HttpResponse response = h.execute(p);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                JSONObject json = new JSONObject(buffer.toString());
                res = res&json.getBoolean("response");
                Log.d("user",json.getString("errmsg"));
            }
            catch (UnsupportedEncodingException e) {
                Log.e("Error", "Invalid encoding");
            } catch (ClientProtocolException e) {
                Log.e("Error", "Error in posting a message");
            } catch (IOException e) {
                Log.e("Error", "I/O Exception");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();
        if(res)
            return true;
        else
            return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result)
            Toast.makeText(context,"Backup Successfully",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context,"Backup Failed",Toast.LENGTH_SHORT).show();
    }
}

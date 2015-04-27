package com.example.student.phoneprofile;

import android.content.ContentValues;
import android.content.Context;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class RestoreCloudTask extends AsyncTask<String,Void,Boolean> {

    ProfileDB helper;
    String line;
    StringBuilder buffer = new StringBuilder();
    private boolean res;
    private Context context;

    public RestoreCloudTask(Context context){
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String user_id = params[0];
        HttpClient h = new DefaultHttpClient();
        HttpPost p = new HttpPost
                ("http://ict.siit.tu.ac.th/~u5522781962/PPandroid/restore.php");

        helper = new ProfileDB(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowCount = db.delete("profile","",null);

        try{
            List<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("user_id",user_id));

            p.setEntity(new UrlEncodedFormEntity(values));
            HttpResponse response = h.execute(p);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }

            JSONObject json = new JSONObject(buffer.toString());
            res = json.getBoolean("response");
            if(res){
                JSONArray profiles = json.getJSONArray("profile");
                Log.d("user",""+profiles.length());
                for(int i = 0; i<profiles.length(); i++){
                    JSONObject apro = profiles.getJSONObject(i);
                    ContentValues c = new ContentValues();
                    c.put("_id",apro.getInt("profile_id"));
                    c.put("pname",apro.getString("profile_name"));
                    c.put("wifi",apro.getInt("wifi"));
                    c.put("data",apro.getInt("data"));
                    c.put("bluetooth", apro.getInt("bt"));
                    c.put("sound", apro.getInt("sound"));
                    c.put("mediaVol",apro.getInt("mediaVol"));
                    c.put("ringVol",apro.getInt("ringVol"));
                    c.put("brightness",apro.getInt("brightness"));

                    String ft = apro.getString("fromTime");
                    String tt = apro.getString("toTime");
                    if(ft.equals(""))
                        ft = null;
                    if(tt.equals(""))
                        tt = null;

                    c.put("fromTime",ft);
                    c.put("toTime",tt);

                    long testID = db.insert("profile", null, c);
                    if(testID != -1)
                        res = res&true;
                    else
                        res = res&false;
                }
                db.close();
                return true;
            }
            else {
                Log.d("user", json.getString("errmsg"));
                return false;
            }
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
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result)
            Toast.makeText(context, "Restore Successfully", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context,"Restore Failed",Toast.LENGTH_SHORT).show();
    }

    public boolean getRes(){
        return res;
    }
}

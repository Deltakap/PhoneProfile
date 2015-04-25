package com.example.student.phoneprofile;

import android.os.AsyncTask;
import android.util.Log;

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

public class SignOutTask extends AsyncTask<String,Void,Boolean> {

    String line;
    StringBuilder buffer = new StringBuilder();

    @Override
    protected Boolean doInBackground(String... params) {
        String android_id = params[0];

        HttpClient h = new DefaultHttpClient();
        HttpPost p = new HttpPost
                ("http://ict.siit.tu.ac.th/~u5522781962/PPandroid/logout.php");

        List<NameValuePair> values = new ArrayList<NameValuePair>();
        values.add(new BasicNameValuePair("android_id",android_id));

        try{
            p.setEntity(new UrlEncodedFormEntity(values));
            HttpResponse response = h.execute(p);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }

            JSONObject json = new JSONObject(buffer.toString());
            boolean res = json.getBoolean("response");
            String error = json.getString("errmsg");
            if(res)
                return true;
            else {
                Log.d("user", error);
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
}

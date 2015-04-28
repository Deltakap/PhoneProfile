package com.example.student.phoneprofile;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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


public class SignUp extends ActionBarActivity {

    String username;
    String password;
    String confirmpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    public void Register(View v){
        EditText un = (EditText)findViewById(R.id.usernamereg);
        EditText pwd = (EditText)findViewById(R.id.passwordreg);
        EditText con = (EditText)findViewById(R.id.confirmpass);

        username = un.getText().toString();
        password = pwd.getText().toString();
        confirmpass = con.getText().toString();

        boolean flag = fieldCheck();

        if(flag) {
            PostRegisTask post = new PostRegisTask();
            post.execute();
        }
    }

    private boolean fieldCheck() {
        if(username.length() > 20 || username.length() < 4){
            Toast t = Toast.makeText(SignUp.this.getApplicationContext(),
                    "Username length should be between 8 and 20",Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        else if(password.length() > 20 || password.length() < 8){
            Toast t = Toast.makeText(SignUp.this.getApplicationContext(),
                    "Password length should be between 8 and 20",Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        else if(!confirmpass.equals(password)){
            Toast t = Toast.makeText(SignUp.this.getApplicationContext(),
                    "Passwords do not match",Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        else
            return true;
    }

    class PostRegisTask extends AsyncTask<String,Void,Boolean>{

        String line;
        StringBuilder buffer = new StringBuilder();

        @Override
        protected Boolean doInBackground(String... params) {
            HttpClient h = new DefaultHttpClient();
            HttpPost p = new HttpPost
                    ("http://ict.siit.tu.ac.th/~u5522781962/PPandroid/regpost.php");

            List<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("username",username));
            values.add(new BasicNameValuePair("password",password));

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
                    Log.d("user",error);
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
            if(result){
                SignUp.this.setResult(RESULT_OK);
                SignUp.this.finish();
            }
            else{
                Toast.makeText(SignUp.this.getApplicationContext(),
                        "Sign up failed",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

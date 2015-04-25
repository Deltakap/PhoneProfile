package com.example.student.phoneprofile;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
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


public class Login extends ActionBarActivity {

    String username;
    String password;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
            case R.id.signup:
                i = new Intent(this,SignUp.class);
                startActivityForResult(i,10);
                break;
            case R.id.signin:
                signIn();

            default: break;
        }
    }

    private void signIn() {
        EditText un = (EditText)findViewById(R.id.username);
        EditText pwd = (EditText)findViewById(R.id.password);

        username = un.getText().toString();
        password = pwd.getText().toString();

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        LoginTask l = new LoginTask();
        l.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == 10){
           if(resultCode == RESULT_OK){
                Toast.makeText(this.getApplicationContext(),
                        "Sign up successfully",Toast.LENGTH_SHORT).show();
           }
       }
    }

    class LoginTask extends AsyncTask<String,Void,Boolean> {

        String line;
        StringBuilder buffer = new StringBuilder();
        int id;

        @Override
        protected Boolean doInBackground(String... params) {
            HttpClient h = new DefaultHttpClient();
            HttpPost p = new HttpPost
                    ("http://ict.siit.tu.ac.th/~u5522781962/PPandroid/logincheck.php");

            List<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("username",username));
            values.add(new BasicNameValuePair("password",password));
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
                id = json.getInt("user_id");
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

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Intent i = new Intent();
                i.putExtra("user_id",id);

                Login.this.setResult(RESULT_OK,i);
                Login.this.finish();
            }
            else{
                Toast.makeText(Login.this.getApplicationContext(),
                        "Sign in failed",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

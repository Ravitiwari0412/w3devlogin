package com.w3dev.w3devlogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends Activity {
    private static final String PROFILE_URL = "https://accounts.studentgiri.com/v1.0/me/";
    private static final String OAUTH_ACCESS_TOKEN_PARAM = "access_token";
    private static final String QUESTION_MARK = "?";
    private static final String EQUALS = "=";
    private TextView welcomeText;
    private ProgressDialog pd;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_profile);
        this.welcomeText = (TextView)this.findViewById(R.id.activity_profile_welcome_text);
        SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
        String accessToken = preferences.getString("accessToken", (String)null);
        Log.i(TAG, "onCreate:activity2 "+accessToken);
        if(accessToken != null) {
            String profileUrl = getProfileUrl(accessToken);
          //  (new ProfileActivity.GetProfileRequestAsyncTask((ProfileActivity.GetProfileRequestAsyncTask)null)).execute(new String[]{profileUrl});
                            new GetProfileRequestAsyncTask().execute(profileUrl);
        }

    }

    private static final String getProfileUrl(String accessToken) {
        Log.i(TAG, "getProfileUrl: "+accessToken);
        return  PROFILE_URL
                +QUESTION_MARK
                +OAUTH_ACCESS_TOKEN_PARAM+EQUALS+accessToken;
    }

    private class GetProfileRequestAsyncTask extends AsyncTask<String, Void, JSONObject> {
        private GetProfileRequestAsyncTask() {
        }

        protected void onPreExecute() {
            ProfileActivity.this.pd = ProgressDialog.show(ProfileActivity.this, "", ProfileActivity.this.getString(R.string.welcome_text), true);
        }

        protected JSONObject doInBackground(String... urls) {
            if(urls.length > 0) {
                String url = urls[0];
                Log.i(TAG, "doInBackground: activity2"+url);
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                httpget.setHeader("email", "email");

                try {
                    HttpResponse response = httpClient.execute(httpget);
                    Log.i("Activity2", "doInBackground: 2"+response);
                    if(response != null && response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity());
                        Log.i("Activity2", "doInBackground:3 "+result);
                        return new JSONObject(result);
                    }
                } catch (IOException var7) {
                    Log.e("Authorize", "Error Http response " + var7.getLocalizedMessage());
                } catch (JSONException var8) {
                    Log.e("Authorize", "Error Http response " + var8.getLocalizedMessage());
                }
            }

            return null;
        }

        protected void onPostExecute(JSONObject data) {
            if(ProfileActivity.this.pd != null && ProfileActivity.this.pd.isShowing()) {
                ProfileActivity.this.pd.dismiss();
            }

            if(data != null) {
                try {
                    String welcomeTextString = String.format(ProfileActivity.this.getString(R.string.welcome_text),

                            new Object[]{data.getString("id"), data.getString("name"), data.getString("email")});
                    ProfileActivity.this.welcomeText.setText(welcomeTextString);
                } catch (JSONException var3) {
                    Log.e("Authorize", "Error Parsing json " + var3.getLocalizedMessage());
                }
            }

        }
    }
}


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

public class ProfileActivity extends Activity {
    private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~";
    private static final String OAUTH_ACCESS_TOKEN_PARAM = "oauth2_access_token";
    private static final String QUESTION_MARK = "?";
    private static final String EQUALS = "=";
    private TextView welcomeText;
    private ProgressDialog pd;

    public ProfileActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_profile);
        this.welcomeText = (TextView)this.findViewById(R.id.activity_profile_welcome_text);
        SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
        String accessToken = preferences.getString("accessToken", (String)null);
        if(accessToken != null) {
            String profileUrl = getProfileUrl(accessToken);
          //  (new ProfileActivity.GetProfileRequestAsyncTask((ProfileActivity.GetProfileRequestAsyncTask)null)).execute(new String[]{profileUrl});
                            new GetProfileRequestAsyncTask().execute(profileUrl);
        }

    }

    private static final String getProfileUrl(String accessToken) {
        return "https://api.linkedin.com/v1/people/~?oauth2_access_token=" + accessToken;
    }

    private class GetProfileRequestAsyncTask extends AsyncTask<String, Void, JSONObject> {
        private GetProfileRequestAsyncTask() {
        }

        protected void onPreExecute() {
            ProfileActivity.this.pd = ProgressDialog.show(ProfileActivity.this, "", ProfileActivity.this.getString(2131034114), true);
        }

        protected JSONObject doInBackground(String... urls) {
            if(urls.length > 0) {
                String url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                httpget.setHeader("x-li-format", "json");

                try {
                    HttpResponse response = httpClient.execute(httpget);
                    if(response != null && response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity());
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
                    String welcomeTextString = String.format(ProfileActivity.this.getString(R.string.welcome_text), new Object[]{data.getString("firstName"), data.getString("lastName"), data.getString("headline")});
                    ProfileActivity.this.welcomeText.setText(welcomeTextString);
                } catch (JSONException var3) {
                    Log.e("Authorize", "Error Parsing json " + var3.getLocalizedMessage());
                }
            }

        }
    }
}


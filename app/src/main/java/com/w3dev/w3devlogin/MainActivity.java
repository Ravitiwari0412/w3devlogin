package com.w3dev.w3devlogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.IOException;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final String API_KEY = "1729786746660237";
    private static final String SECRET_KEY = "GXpqf31Rs8ofodRiQZqZX79QgJiF2Dyw47sj3RH1";
   // private static final String STATE = "E3ZYKC1T6H2yP4z";
    private static final String REDIRECT_URI = "http://localhost";
    private static final String AUTHORIZATION_URL = "https://accounts.studentgiri.com/authorize/";
    private static final String ACCESS_TOKEN_URL = "https://accounts.studentgiri.com/token/";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE = "code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private WebView webView;
    private ProgressDialog pd;

    public MainActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.webView = (WebView)this.findViewById(R.id.main_activity_web_view);
        this.webView.requestFocus(130);
        this.pd = ProgressDialog.show(this, "", this.getString(R.string.loading), true);
        this.webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if(MainActivity.this.pd != null && MainActivity.this.pd.isShowing()) {
                    MainActivity.this.pd.dismiss();
                }

            }

            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                if(authorizationUrl.startsWith(REDIRECT_URI)) {
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);
//                    Intent intent = new Intent(getApplicationContext(),RedirectActivity.class);
//                    startActivity(intent);
//                    intent.putExtra("url",uri);
                 //   return true;
//                    String stateToken = uri.getQueryParameter("state");
//                    if(stateToken == null || !stateToken.equals("E3ZYKC1T6H2yP4z")) {
//                        Log.e("Authorize", "State token doesn't match");
//                        return true;
//                    }

                    String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                    if(authorizationToken == null) {
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }

                    Log.i("Authorize", "Auth token received: " + authorizationToken);
                    String accessTokenUrl = MainActivity.getAccessTokenUrl(authorizationToken);
                  //  (MainActivity.this.new PostRequestAsyncTask((MainActivity.PostRequestAsyncTask)null)).execute(new String[]{accessTokenUrl});
                    new PostRequestAsyncTask().execute(accessTokenUrl);

                } else {
                    Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                    MainActivity.this.webView.loadUrl(authorizationUrl);
                }

                return true;
            }
        });
        String authUrl = getAuthorizationUrl();
        Log.i("Authorize", "Loading Auth Url: " + authUrl);
        this.webView.loadUrl(authUrl);

    }


    /**
     * Method that generates the url for get the access token from the Service
     * @return Url
     */
    private static String getAccessTokenUrl(String authorizationToken){
        return ACCESS_TOKEN_URL
                +QUESTION_MARK
                +GRANT_TYPE_PARAM+EQUALS+GRANT_TYPE
                +AMPERSAND
                +RESPONSE_TYPE_VALUE+EQUALS+authorizationToken
                +AMPERSAND
                +CLIENT_ID_PARAM+EQUALS+API_KEY
                +AMPERSAND
                +REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI
                +AMPERSAND
                +SECRET_KEY_PARAM+EQUALS+SECRET_KEY;
    }
    /**
     * Method that generates the url for get the authorization token from the Service
     * @return Url
     */
    private static String getAuthorizationUrl(){
        return AUTHORIZATION_URL
                +QUESTION_MARK+RESPONSE_TYPE_PARAM+EQUALS+RESPONSE_TYPE_VALUE
                +AMPERSAND+CLIENT_ID_PARAM+EQUALS+API_KEY

                +AMPERSAND+REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI;
    }

//    private static String getAccessTokenUrl(String authorizationToken) {
//        return "https://accounts.studentgiri.com/token/?grant_type=authorization_code&code=" + authorizationToken + "&" + "client_id" + "=" + "1729786746660237" + "&" + "redirect_uri" + "=" + "http://localhost" + "&" + "client_secret" + "=" + "GXpqf31Rs8ofodRiQZqZX79QgJiF2Dyw47sj3RH1";
//    }

//    private static String getAuthorizationUrl() {
//        return "https://accounts.studentgiri.com/authorize/?response_type=code&client_id=1729786746660237&redirect_uri=http://localhost";
//    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {
        private PostRequestAsyncTask() {
        }

        protected void onPreExecute() {
            MainActivity.this.pd = ProgressDialog.show(MainActivity.this, "", MainActivity.this.getString(R.string.loading), true);
        }

        protected Boolean doInBackground(String... urls) {
            if(urls.length > 0) {
                String url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpost = new HttpPost(url);

                try {
                    HttpResponse response = httpClient.execute(httpost);
                    if(response != null && response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity());
                        JSONObject resultJson = new JSONObject(result);
                        int expiresIn = resultJson.has("expires_in")?resultJson.getInt("expires_in"):0;
                        String accessToken = resultJson.has("access_token")?resultJson.getString("access_token"):null;
                        if(expiresIn > 0 && accessToken != null) {
                            Log.i("Authorize", "This is the access Token: " + accessToken + ". It will expires in " + expiresIn + " secs");
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.SECOND, expiresIn);
                            long expireDate = calendar.getTimeInMillis();
                            SharedPreferences preferences = MainActivity.this.getSharedPreferences("user_info", 0);
                            Editor editor = preferences.edit();
                            editor.putLong("expires", expireDate);
                            editor.putString("accessToken", accessToken);
                            editor.commit();
                            return Boolean.valueOf(true);
                        }
                    }
                } catch (IOException var15) {
                    Log.e("Authorize", "Error Http response " + var15.getLocalizedMessage());
                } catch (ParseException var16) {
                    Log.e("Authorize", "Error Parsing Http response " + var16.getLocalizedMessage());
                } catch (JSONException var17) {
                    Log.e("Authorize", "Error Parsing Http response " + var17.getLocalizedMessage());
                }
            }

            return Boolean.valueOf(false);
        }

        protected void onPostExecute(Boolean status) {
            if(MainActivity.this.pd != null && MainActivity.this.pd.isShowing()) {
                MainActivity.this.pd.dismiss();
            }

            if(status.booleanValue()) {
                Intent startProfileActivity = new Intent(MainActivity.this, ProfileActivity.class);
                MainActivity.this.startActivity(startProfileActivity);
            }

        }
    }
}

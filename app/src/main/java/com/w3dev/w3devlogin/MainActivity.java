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

//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;

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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    private static final String API_KEY = "1729786746660236";
    private static final String SECRET_KEY = "GXpqf31Rs8ofodRiQZqZX79QgJiF2Dyw44sj3RT1";
    private static final String EMAIL = "email";
    private static final String REDIRECT_URI = "w3dev://auth/";
    private static final String AUTHORIZATION_URL = "https://accounts.studentgiri.com/authorize/";
    private static final String ACCESS_TOKEN_URL = "https://accounts.studentgiri.com/token/";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE = "code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String SCOPE = "scope";
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
        String authUrl = getAuthorizationUrl();
        Log.i("Authorize", "Loading Auth Url: " + authUrl);
        this.webView.loadUrl(authUrl);
        this.webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "onPageFinished: 1"+url);
                if(MainActivity.this.pd != null && MainActivity.this.pd.isShowing()) {
                    MainActivity.this.pd.dismiss();
                }

            }

            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                if(authorizationUrl.startsWith(REDIRECT_URI)) {
                    Log.i("Authorize", "hello");
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
                    Log.i("Authorize", "accesstokenURL :"+accessTokenUrl);
                  //  (MainActivity.this.new PostRequestAsyncTask((MainActivity.PostRequestAsyncTask)null)).execute(new String[]{accessTokenUrl});
                    new PostRequestAsyncTask().execute(accessTokenUrl);

                } else {
                    Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                    MainActivity.this.webView.loadUrl(authorizationUrl);
                }

                return true;
            }
        });


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
      //  return "https://accounts.studentgiri.com/authorize/?client_id=1729786746660238&scope=email&redirect_uri=http%3A%2F%2Fapi.internboards.com%2Fcallback&response_type=code";
    }
    /**
     * Method that generates the url for get the authorization token from the Service
     * @return Url
     */
    private static String getAuthorizationUrl(){
        return AUTHORIZATION_URL
                +QUESTION_MARK+RESPONSE_TYPE_PARAM+EQUALS+RESPONSE_TYPE_VALUE
                +AMPERSAND+CLIENT_ID_PARAM+EQUALS+API_KEY

                +AMPERSAND+REDIRECT_URI_PARAM+EQUALS+REDIRECT_URI+AMPERSAND+SCOPE+EQUALS+EMAIL;
       // return "https://accounts.studentgiri.com/authorize/?client_id=1729786746660238&scope=email&redirect_uri=http%3A%2F%2Fapi.internboards.com%2Fcallback&response_type=code";
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
    protected String getAuthorizationToken(String authorizationUrl) {
        Uri uri = Uri.parse(authorizationUrl);
        String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);

        return authorizationToken;
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {
        private PostRequestAsyncTask() {
            Log.i("Authorize", "PostRequestAsyncTask: ");

        }

        protected void onPreExecute() {
            Log.i("Authorize", "onPreExecute: ");
            MainActivity.this.pd = ProgressDialog.show(MainActivity.this, "", MainActivity.this.getString(R.string.loading), true);
        }

        protected Boolean doInBackground(String... urls) {
            if(urls.length > 0) {
                try{

                String url = urls[0];
                    Log.i("Authorize", "doInBackground: "+url);
                    Log.i("Authorize", "doInBackground:1 "+url);

              //  HttpClient httpClient = new DefaultHttpClient();
            //    HttpPost httpost = new HttpPost(url);
             //   Log.i("Authorize", "doInBackground:1 "+httpost);
                    OkHttpClient client=new OkHttpClient();
                    FormBody.Builder formBodyBuilder=new FormBody.Builder()
                            .add(CLIENT_ID_PARAM,API_KEY)
                            .add(SECRET_KEY_PARAM,SECRET_KEY)
                            .add(RESPONSE_TYPE_VALUE,getAuthorizationToken(url))
                            .add(GRANT_TYPE_PARAM,GRANT_TYPE);
                    Log.i("Authorize", "doInBackground: 2"+getAuthorizationToken(url));
                    Request request=new Request.Builder()
                            .url(url)
                            .post(formBodyBuilder.build())
                            .build();

//
//                    OkHttpClient client = new OkHttpClient();
//                   // okhttp3.RequestBody formBody=new FormBody.Builder()
//                    FormBody.Builder formBodyBuilder = new FormBody.Builder()
//                            .add("GRANT_TYPE_PARAM","authorization_code")
//                            .build();
//                          //  .add("message", "Your message")
//                          //  .build();
//
//                           // .add(Constants.POST_REFRESH_TOKEN, token);
//                    okhttp3.Request request=new okhttp3.Request().newBuilder()
//                            .url()
//
//                    Request request = new Request.Builder()
//                            .url("https://accounts.studentgiri.com/token/?")
//                            .post(formBodyBuilder.build())
//                            .build();

//                    RequestBody formBody = new FormBody.Builder()
//                            .add("message", "Your message")
//                            .build();
//                    Request request = new Request.Builder()
//                            .url("https://accounts.studentgiri.com/token/?")
//                            .post(formBody)
//                            .build();


                    try {  Response responses = client.newBuilder()//authenticator(Utils.getAuthenticator(oAuth2Client, authState))
                            .build().newCall(request).execute();
                        //Response responses = client.newCall(request).execute();
                      //  responses = client.newCall(request).execute();
                       Log.i("Authorize", "doInBackground:3 "+responses.body());

                    String jsonData = responses.body().string();
                        Log.i(TAG, "doInBackground: 4"+jsonData);
                  //  JSONObject Jobject = new JSONObject(jsonData);


                //    HttpResponse response = httpClient.execute(httpost);
                  //  Log.i(TAG, "doInBackground:2 "+response);
                  //  if(response != null && response.getStatusLine().getStatusCode() == 200) {
                       // String result = EntityUtils.toString(response.getEntity());
                        JSONObject resultJson = new JSONObject(jsonData);
                        Log.i(TAG, "doInBackground: 5"+resultJson);
                        int expiresIn = resultJson.has("expires_in")?resultJson.getInt("expires_in"):0;

                        String accessToken = resultJson.has("access_token")?resultJson.getString("access_token"):null;
                        Log.i("Authorize", "This is the access Token: " + accessToken + ". It will expires in " + expiresIn + " secs");

                        if(expiresIn > 0 && accessToken != null) {
                            Log.i("Authorize", "This is the access Token: " + accessToken + ". It will expires in " + expiresIn + " secs");
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.SECOND, expiresIn);
                            long expireDate = calendar.getTimeInMillis();
                            SharedPreferences preferences = MainActivity.this.getSharedPreferences("user_info", 0);
                            Editor editor = preferences.edit();
                            editor.putLong("expires", expireDate);
                            editor.putString("accessToken", accessToken);
                            Log.i(TAG, "doInBackground: 6"+editor);
                            editor.commit();
                            Log.i(TAG, "doInBackground: 7"+editor.commit());
                            return Boolean.valueOf(true);
                        } } catch (IOException e) {
                        e.printStackTrace();
                    }

                // catch (IOException var15) {
                   // Log.e("Authorize", "Error Http response " + var15.getLocalizedMessage());
                } catch (ParseException var16) {
                    Log.e("Authorize", "Error Parsing Http response " + var16.getLocalizedMessage());
                } catch (JSONException var17) {
                    Log.e("Authorize", "Error Parsing Http response1 " + var17.getLocalizedMessage());
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

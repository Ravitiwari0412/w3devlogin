package com.w3dev.w3devlogin;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RedirectActivity extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);
        TextView tv=(TextView)findViewById(R.id.textView);
       Intent intent=getIntent();
//      ;
//        tv.setText(  intent.getStringExtra("url"));
        if(intent!=null && intent.getData()!=null){
            tv.setText(intent.getData().toString());
        }


}}

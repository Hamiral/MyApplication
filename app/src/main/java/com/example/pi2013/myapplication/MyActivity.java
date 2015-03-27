package com.example.pi2013.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MyActivity extends BaseActivity{
    public final static String EXTRA_MESSAGE = "com.example.pi2013.myapplication.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        update();

    }

    public void onClickButton(View view)
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if(appState.getLogged() && appState.getHotspot()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            startActivity(browserIntent);
        }
        else if (appState.getLogged() && !appState.getHotspot())
        {
            Intent findHotspotIntent = new Intent(this, MapActivity.class);
            startActivity(findHotspotIntent);
        }
        else
        {
            Intent SignInIntent = new Intent(this, ConnexionActivity.class);
            startActivity(SignInIntent);
        }
    }

    public void update()
    {
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        Button button_dynamic = (Button) findViewById(R.id.button_dynamic);
        TextView textView=(TextView) findViewById(R.id.main_state);
        if(appState.getLogged() && appState.getHotspot()) {
            button_dynamic.setText("Start Browsing");
            textView.setText("Hotspot found !");
            textView.setTextColor(Color.GREEN);
        }
        else if (appState.getLogged() && !appState.getHotspot())
        {
            button_dynamic.setText("Find a Hotspot");
            textView.setText("No Hotspot found.");
            textView.setTextColor(Color.RED);
        }
        else
        {
            button_dynamic.setText("Sign In");
            textView.setText("No Hotspot found.");
            textView.setTextColor(Color.RED);
        }

    }

    public void onRestart()
    {
        super.onRestart();
        update();
    }

}
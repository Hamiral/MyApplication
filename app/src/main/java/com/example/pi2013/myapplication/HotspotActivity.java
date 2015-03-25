package com.example.pi2013.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.pi2013.myapplication.R.id.changing_button;


public class HotspotActivity extends BaseActivity {
    public boolean hotspot_found =false;
    public boolean connecte = false;
    public LinearLayout layout = null;
    public TextView text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot);

        layout = (LinearLayout) LinearLayout.inflate(this, R.layout.activity_hotspot, null);
        text = (TextView) layout.findViewById(changing_button);
        if (connecte == false)
        {
            text.setText("Sign In");
        }
        else
        {
            if (hotspot_found == true)
            {
                text.setText("Start Browsing");
            }
            else
            {
                text.setText("Find a Hotspot");
            }
        }


    }

}

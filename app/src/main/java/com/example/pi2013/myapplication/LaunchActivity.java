package com.example.pi2013.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class LaunchActivity extends BaseActivity {
    public RelativeLayout layout = null;
    public ImageView image_client = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        layout = (RelativeLayout) RelativeLayout.inflate(this, R.layout.activity_launch, null);
        image_client = (ImageView) layout.findViewById(R.id.image_client);
        image_client.setImageResource(R.drawable.ic_launch_client_random);




    }


}

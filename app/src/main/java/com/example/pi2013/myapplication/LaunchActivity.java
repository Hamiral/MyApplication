package com.example.pi2013.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new LoadViewTask().execute();
    }


    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            setContentView(R.layout.activity_launch);
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {

            try {
                //Get the current thread's token
                synchronized (this) {
                    this.wait(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            Intent intent = new Intent(getApplicationContext(), MyActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
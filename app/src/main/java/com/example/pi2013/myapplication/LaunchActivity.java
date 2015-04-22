package com.example.pi2013.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 *
 *
 */
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
            language_select();
        }

        // Charge default language of the application (previously chosen by the user in the Settings)
        private void language_select()
        {
            SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
            String lang=settings.getString("language", Locale.getDefault().getLanguage() );
            Locale myLocale;
            myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            getBaseContext().getResources().updateConfiguration(conf,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {

            try {
                //Get the current thread's token
                synchronized (this) {
                    this.wait(3000); // timer in milliseconds
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
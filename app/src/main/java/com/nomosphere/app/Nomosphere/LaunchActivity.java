package com.nomosphere.app.Nomosphere;

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
 * Launch Activity
 */
public class LaunchActivity extends Activity {

    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up: create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadViewTask().execute();

        if(MyActivity.MyActivity!=null) {
            Intent intent = new Intent(getApplicationContext(), MyActivity.class);
            startActivity(intent);
            finish();
        }
    }


    /**
     * Asynchronous thread to request the default language of the application
     */
    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            setContentView(R.layout.activity_launch);
            language_select();
        }

        /**
         * Gets default language of the application (previously chosen by the user in the Settings)
         */
        private void language_select()
        {
            SharedPreferences settings = getSharedPreferences(GlobalVariable.PREFS_NAME, 0);
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

        /**
         * The code to be executed in a background thread.
         */
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
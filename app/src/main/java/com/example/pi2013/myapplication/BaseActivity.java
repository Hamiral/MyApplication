package com.example.pi2013.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class  BaseActivity extends ActionBarActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if(appState.getLogged())
        {
            inflater.inflate(R.menu.main_activity_actions_logged, menu);
        }
        else
        {
            inflater.inflate(R.menu.main_activity_actions_unlogged, menu);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_navigate:
                openNavigate();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openNavigate() {
        Intent intent = new Intent(this, MyActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public void onRestart()
    {
        super.onRestart();
        invalidateOptionsMenu();
    }


}




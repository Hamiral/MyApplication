package com.example.pi2013.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


public class BaseActivity extends ActionBarActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id)
        {
            case R.id.action_logged:

                openLogged();
                return true;
            case R.id.action_ID:
                openID();
                return true;
            case R.id.action_navigate:
                openNavigate();
                return true;
            case R.id.action_MAPS:
                openMAPS();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openLogged()
    {

    }

    private void openMAPS() {
        Toast.makeText(this, "MAPS", Toast.LENGTH_SHORT).show();
    }

    private void openID() {
        Toast.makeText(this, "ID", Toast.LENGTH_SHORT).show();
    }

    private void openNavigate() {
        Toast.makeText(this, "NAVIGATE", Toast.LENGTH_SHORT).show();
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


}

package com.example.pi2013.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Base Activity used to extend the ActionBar on different activities
 */
public class  BaseActivity extends ActionBarActivity {

    /**
     * Initialize the Actionbar Menu
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions_logged, menu);
        return true;

    }

    /**
     * Items selected in the ActionBar
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            /*case R.id.action_navigate:
                openNavigate();
                return true;*/
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*private void openNavigate() {
        Intent intent = new Intent(this, MyActivity.class);
        startActivity(intent);
    }*/

    /**
     * Opens the Settings View
     */
    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Called after your activity has been stopped, prior to it being started again.
     */
    public void onRestart()
    {
        super.onRestart();
        invalidateOptionsMenu();
    }

}




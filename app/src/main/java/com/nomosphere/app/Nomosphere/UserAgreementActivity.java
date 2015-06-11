package com.nomosphere.app.Nomosphere;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * User Agreement Activity
 */
public class UserAgreementActivity extends ActionBarActivity {
    public static UserAgreementActivity UserAgreementActivity=null;

    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up: create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        UserAgreementActivity=this;
        getActionBar().setTitle(getResources().getString(R.string.title_activity_user_agreement));
    }

    /**
     * Called when your activity is done and should be closed
     */
    public void finish()
    {
        super.finish();
        UserAgreementActivity=null;
    }
}

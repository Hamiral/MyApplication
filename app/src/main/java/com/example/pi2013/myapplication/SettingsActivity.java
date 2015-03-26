package com.example.pi2013.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    Spinner ChoixLangue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Spinner ChoixLangue = (Spinner) findViewById(R.id.language_choice);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ChoixLangue.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_choice, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ChoixLangue.setAdapter(adapter);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (pos==1)
        {
            Toast.makeText(parent.getContext(),
                    "You have selected English", Toast.LENGTH_SHORT)
                .show();
            setLocale("en");
        }
        if (pos==2)
        {
            Toast.makeText(parent.getContext(),
                    "You have selected French", Toast.LENGTH_SHORT)
                    .show();
            setLocale("fr");
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void setLocale(String lang) {
        Locale myLocale;
        myLocale = new Locale(lang);
        setLanguageChanged(true);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getBaseContext().getResources().updateConfiguration(conf,
        getBaseContext().getResources().getDisplayMetrics());
        Intent refresh = new Intent(this, SettingsActivity.class);
        setLanguageChanged(true);
        finish();
        startActivity(refresh);

    }

    public void start_user_agreement_activity(View view)
    {
        Intent intent = new Intent(this, UserAgreementActivity.class);
        startActivity(intent);
    }
    public void start_about_activity(View view)
    {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}

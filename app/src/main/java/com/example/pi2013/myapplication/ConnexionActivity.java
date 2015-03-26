package com.example.pi2013.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class ConnexionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

    }

    public void login(View view) {
        EditText username = (EditText)findViewById(R.id.editText1);
        EditText password = (EditText)findViewById(R.id.editText2);
        GlobalVariable appState = ((GlobalVariable)getApplicationContext());
        if (username.getText().toString().equals("admin") &&
                password.getText().toString().equals("admin")) {
            //correcct password
            appState.setLogged(true);
            Intent intent = new Intent(this, MyActivity.class);
            startActivity(intent);
        } else {
            //wrong password
            appState.setLogged(false);
        Toast.makeText(getApplicationContext(),
            "WrongPassword", Toast.LENGTH_SHORT)
            .show();
        }
    }
}

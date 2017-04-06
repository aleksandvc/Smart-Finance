package com.vladimircvetanov.smartfinance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //I've added buttons to currently extant activities for ease of navigation during development.
        //Add and remove buttons as needed.
        //                                      ~Simo

        Button toLogIn = (Button) findViewById(R.id.temp_to_login);
        Button toRegister = (Button) findViewById(R.id.temp_to_register);
        Button toTransaction = (Button) findViewById(R.id.temp_to_transaction);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.temp_to_login:
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        break;
                    case R.id.temp_to_register:
                        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                        break;
                    case R.id.temp_to_transaction:
                        startActivity(new Intent(MainActivity.this, TransactionActivity.class));
                        break;
                }
            }
        };

        toLogIn.setOnClickListener(onClickListener);
        toRegister.setOnClickListener(onClickListener);
        toTransaction.setOnClickListener(onClickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_currency:
                return true;
            case R.id.item_settings:
                return true;
            case R.id.item_logout:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}


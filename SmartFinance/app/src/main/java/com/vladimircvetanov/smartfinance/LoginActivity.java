package com.vladimircvetanov.smartfinance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vladimircvetanov.smartfinance.db.DBAdapter;

public class LoginActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPass;
    private Button logIn;
    private Button signUp;

    private DBAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = (EditText)findViewById(R.id.loggin_email_insert);
        userPass = (EditText)findViewById(R.id.loggin_pass_insert);
        logIn = (Button)findViewById(R.id.loggin_loggin_button);
        signUp = (Button)findViewById(R.id.loggin_signup_button);

        adapter = DBAdapter.getInstance(this);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.loggin_loggin_button:
                        logIn();
                        break;
                    case R.id.loggin_signup_button:
                        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                        break;
                }
            }
        };
        logIn.setOnClickListener(listener);
        signUp.setOnClickListener(listener);
    }


    private void logIn() {
        String email = userEmail.getText().toString();
        String pass = userPass.getText().toString();
        if(adapter.getUser(email,pass)){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            Toast.makeText(this, "Successful logged in.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Wrong email or password!", Toast.LENGTH_SHORT).show();
        }
    }
}

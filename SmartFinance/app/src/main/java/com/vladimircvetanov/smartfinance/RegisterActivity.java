package com.vladimircvetanov.smartfinance;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.message.Message;
import com.vladimircvetanov.smartfinance.model.CategoryExpense;
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail;
    private EditText userPass;
    private EditText confirmPass;
    private Button signUp;
    private Button cancel;
    private DBAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        adapter = DBAdapter.getInstance(this);

        userEmail = (EditText)findViewById(R.id.register_email_insert);
        userPass = (EditText)findViewById(R.id.register_pass_insert);
        confirmPass = (EditText)findViewById(R.id.register_confirm_insert);

        signUp = (Button)findViewById(R.id.register_reg_button);
        cancel = (Button)findViewById(R.id.register_cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signUp();
            }
        });
    }

    private void signUp() {
        final String username = userEmail.getText().toString();
        final String pass = userPass.getText().toString();
        final String confirm = confirmPass.getText().toString();
        final boolean[] flag = new boolean[1];
        if (username.isEmpty()) {
            userEmail.setError("Empty email");
            userEmail.requestFocus();
            return;
        }
        //if(!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()){
        if (!username.matches("^(.+)@(.+)$")) {
            userEmail.setError("enter a valid email address");
            userEmail.setText("");
            userEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            userPass.setError("Empty password");
            userPass.requestFocus();
            return;

        }
        if (!pass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,30}$")) {
            userPass.setError("Password should contain at least one digit," +
                    "one special symbol,one small letter,and should be between 8 and 30 symbols. ");
            return;
        }
        if (confirm.isEmpty()) {
            confirmPass.setError("Empty confirmation");
            confirmPass.requestFocus();
            return;

        }
        if (!pass.equals(confirm)) {

            confirmPass.setError("Different passwords");
            confirmPass.setText("");
            confirmPass.requestFocus();
            return;

        }
        if(adapter.existsUser(username)){
            Message.message(this,"User already exists");
            return;
        }

        flag[0] = true;
        new AsyncTask<Void,Void,Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                if(flag[0] = true) {
                    User u = new User(username, pass);
                    long id = adapter.insertData(u);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                    Message.message(RegisterActivity.this, "User registered!");
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();

            }
        }.execute();




    }

}

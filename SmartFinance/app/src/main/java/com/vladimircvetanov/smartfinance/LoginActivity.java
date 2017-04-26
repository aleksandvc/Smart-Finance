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
import com.vladimircvetanov.smartfinance.model.Manager;
import com.vladimircvetanov.smartfinance.model.User;


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
       final   String email = userEmail.getText().toString();
       final  String pass = userPass.getText().toString();
        final User u = new User(email, pass);
        final boolean[] flag = new boolean[1];
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                flag[0] = adapter.getUser(email, pass);
                if(flag[0]){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Manager.setLoggedUser(u);
                    Manager.getLoggedUser().setId(adapter.getId(u.getEmail()));
                    startActivity(intent);


                    adapter.loadAccounts();
                    adapter.loadIncomeCategories();
                    adapter.loadExpenseCategories();
                    adapter.loadFavouriteCategories();

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                if(flag[0]){
                    finish();
                    Message.message(LoginActivity.this,"Successful logged in." + Manager.getLoggedUser().getId());

                } else{
                    Message.message(LoginActivity.this,"Wrong email or password.");
                }
            }
        }.execute();
    }
}

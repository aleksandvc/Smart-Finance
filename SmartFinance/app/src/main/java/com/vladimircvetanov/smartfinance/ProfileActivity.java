package com.vladimircvetanov.smartfinance;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vladimircvetanov.smartfinance.db.DBAdapter;
import com.vladimircvetanov.smartfinance.model.User;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE = 12 ;
    private ImageView userPic;
    private Button changePic;
    private EditText changeEmail;
    private EditText changePass;
    private Button editChanges;
    DBAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userPic = (ImageView) findViewById(R.id.profile_user_pic);
        changePic = (Button) findViewById(R.id.profile_changepic_button);
        changeEmail = (EditText) findViewById(R.id.profile_email_change);
        changePass = (EditText) findViewById(R.id.profile_pass_change);
        editChanges = (Button) findViewById(R.id.profile_edit_button);

        adapter = DBAdapter.getInstance(this);



        final User u = (User) getIntent().getSerializableExtra("user");

        changeEmail.setText(u.getEmail());
        changePass.setText(u.getPassword());

        final String oldData = changeEmail.getText().toString();
        final String oldPass = changePass.getText().toString();

        View.OnClickListener btnChoosePhotoPressed = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
            }
        };

        changePic.setOnClickListener(btnChoosePhotoPressed);


        editChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String newEmail = changeEmail.getText().toString();
               String newPass = changePass.getText().toString();


                if (newEmail.isEmpty()) {
                    changeEmail.setError("Empty email");
                    changeEmail.requestFocus();
                  return ;
                }
                //if(!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                if (!newEmail.matches("^(.+)@(.+)$")) {
                    changeEmail.setError("enter a valid email address");
                    changeEmail.setText("");
                    changeEmail.requestFocus();
                   return;
                }
                if (newPass.isEmpty()) {
                    changePass.setError("Empty password");
                    changePass.requestFocus();
                    return;

                }
                if (!newPass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,30}$")) {
                    changePass.setError("Password should contain at least one digit," +
                            "one special symbol,one small letter,and should be between 8 and 30 symbols. ");
                    return;
                }

                adapter.updateUser(oldData,oldPass,newEmail,newPass);
                finish();

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (imageReturnedIntent != null) {
                    try {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageReturnedIntent.getData());

                        userPic.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}




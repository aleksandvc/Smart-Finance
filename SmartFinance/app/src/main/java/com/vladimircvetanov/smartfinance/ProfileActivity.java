package com.vladimircvetanov.smartfinance;

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

public class ProfileActivity extends AppCompatActivity {

    private ImageView userPic;
    private Button changePic;
    private EditText changeEmail;
    private EditText changePass;
    private Button editChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userPic = (ImageView)findViewById(R.id.profile_user_pic);
        changePic = (Button)findViewById(R.id.profile_changepic_button);
        changeEmail=(EditText)findViewById(R.id.profile_email_change);
        changePass = (EditText)findViewById(R.id.profile_pass_change);
        editChanges = (Button)findViewById(R.id.profile_edit_button);

        View.OnClickListener btnChoosePhotoPressed = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        };

        changePic.setOnClickListener(btnChoosePhotoPressed);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    userPic.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    userPic.setImageURI(selectedImage);
                }
                break;
        }
    }
};




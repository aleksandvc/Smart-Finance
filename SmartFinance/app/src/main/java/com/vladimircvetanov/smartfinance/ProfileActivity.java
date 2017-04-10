package com.vladimircvetanov.smartfinance;

import android.app.Activity;
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

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE = 12 ;
    private ImageView userPic;
    private Button changePic;
    private EditText changeEmail;
    private EditText changePass;
    private Button editChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userPic = (ImageView) findViewById(R.id.profile_user_pic);
        changePic = (Button) findViewById(R.id.profile_changepic_button);
        changeEmail = (EditText) findViewById(R.id.profile_email_change);
        changePass = (EditText) findViewById(R.id.profile_pass_change);
        editChanges = (Button) findViewById(R.id.profile_edit_button);

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




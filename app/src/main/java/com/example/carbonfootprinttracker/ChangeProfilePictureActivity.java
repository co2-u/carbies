package com.example.carbonfootprinttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeProfilePictureActivity extends AppCompatActivity {

    public final static int PICK_PHOTO_CODE = 1046;
    @BindView(R.id.btnSelectPhoto)
    Button btnSelectPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);
        ButterKnife.bind(this);
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);

            }
        });
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            File file = null;
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                file = new File(getRealPathFromURI(photoUri));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(file != null) {
                ParseFile parseFile = new ParseFile(file);
                parseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            ParseUser user = ParseUser.getCurrentUser();
                            user.put("profileImage", parseFile);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e != null){
                                        e.printStackTrace();
                                    }
                                    else{
                                        int i = 0;
                                    }
                                }
                            });
                        } else {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }
        Intent intent = new Intent(ChangeProfilePictureActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}

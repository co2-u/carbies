package com.example.carbonfootprinttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeProfilePictureActivity extends AppCompatActivity {

    public final static int PICK_PHOTO_CODE = 1046;
    @BindView(R.id.btnSelectPhoto)
    Button btnSelectPhoto;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);
        ButterKnife.bind(this);
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            int i = 0;
                        }

                        Intent intent = new Intent(ChangeProfilePictureActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void onPickPhoto(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

//    // Trigger gallery selection for a photo
//    public void onPickPhoto(View view) {
//        // Create intent for picking a photo from the gallery
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
//        // So as long as the result is not null, it's safe to use the intent.
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            // Bring up gallery to select a photo
//            startActivityForResult(intent, PICK_PHOTO_CODE);
//        }
//    }
//
//    @Override
//    //Starting another activity doesn't have to be one-way. You can also start another activity and receive a result back. To receive a result, call startActivityForResult()
//    //For example, your app can start a camera app and receive the captured photo as a result
//    //Of course, the activity that responds must be designed to return a result. When it does, it sends the result as another Intent object.
//    // Your activity receives it in the onActivityResult() callback.
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (data != null) {
//            File file;
//            Uri photoUri = data.getData();
//            // Do something with the photo based on Uri
//            //Bitmap selectedImage = null;
//            // selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
//            file = new File(getRealPathFromURI(photoUri));
//            if(file != null) {
//                ParseFile parseFile = new ParseFile(file);
//                parseFile.saveInBackground();
//                ParseUser user = ParseUser.getCurrentUser();
//                user.put("profileImage", parseFile);
//                user.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if(e != null){
//                            e.printStackTrace();
//                        }
//                        else{
//                            int i = 0;
//                        }
//                        Intent intent = new Intent(ChangeProfilePictureActivity.this, MainActivity.class);
//                        startActivity(intent);
//                    }
//                });
//            }
//        }
//    }
//
//    public String getRealPathFromURI(Uri contentUri) {
//        String res = null;
//        String[] proj = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
//        if(cursor.moveToFirst()){;
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            res = cursor.getString(column_index);
//        }
//        cursor.close();
//        return res;
//    }

    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case PICK_PHOTO_CODE:
                    //data.getData return the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    File file;
                    file = new File(imgDecodableString);
                    if (file != null) {
                        ParseFile parseFile = new ParseFile(file);
                        parseFile.saveInBackground();
                        imageView.setImageURI(selectedImage);
                        ParseUser user = ParseUser.getCurrentUser();
                        user.put("profileImage", parseFile);
                        break;
                    }
            }
    }


}

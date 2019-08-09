package com.example.carbonfootprinttracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ChangeProfilePictureActivity extends AppCompatActivity {
    private static final String TAG = "ProfilePicActivity";
    public final static int PICK_PHOTO_CODE = 1046;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    @BindView(R.id.btnSelectPhoto)
    Button btnSelectPhoto;
    @BindView(R.id.ivPreview)
    ImageView imageView;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;
    @BindView(R.id.btCapture)
    Button btCapture;
    @BindView(R.id.progressBar6)
    public ProgressBar pbLoading;
    public String photoFileName = "profilephoto.jpg";
    private File photoFile;
    private Integer screenWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_picture);
        ButterKnife.bind(this);
        ParseUser user = ParseUser.getCurrentUser();
        screenWidth = DeviceDimensionsHelper.getDisplayWidth(this);
        btnConfirm.setVisibility(View.GONE);

        ParseFile pfProfile = user.getParseFile("profileImage");
        if (pfProfile != null) {
            String preUrl = pfProfile.getUrl();
            String completeURL = preUrl.substring(0, 4) + "s" + preUrl.substring(4, preUrl.length());
            Glide.with(this)
                    .load(completeURL)
                    .into(imageView);
        } else {
            imageView.setBackground(this.getResources().getDrawable(R.drawable.ic_account_circle));
        }

        btCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeProfilePictureActivityPermissionsDispatcher.onPickPhotoWithPermissionCheck(ChangeProfilePictureActivity.this);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoFile != null) {
                    pbLoading.setVisibility(View.VISIBLE);
                    ParseFile parseFile = new ParseFile(photoFile);
                    parseFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d(TAG, "Error while saving image.");
                                e.printStackTrace();
                            } else {
                                ParseUser user = ParseUser.getCurrentUser();
                                user.put("profileImage", parseFile);
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e != null) {
                                            Log.d(TAG, "Error while saving image to profile.");
                                            e.printStackTrace();
                                        } else {
                                            Log.d(TAG, "Updated user's profile image.");
                                        }
                                        pbLoading.setVisibility(View.GONE);
                                        Intent intent = new Intent(ChangeProfilePictureActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Photofile is null");
                }
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(this, "com.example.carbonfootprinttracker.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void onPickPhoto() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public Bitmap rotateResizeBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        Bitmap resizedRotatedBitmap = BitmapScaler.scaleToFitHeight(rotatedBitmap, screenWidth);

        return resizedRotatedBitmap;
    }

    public static class BitmapScaler
    {
        // Scale and maintain aspect ratio given a desired width
        // BitmapScaler.scaleToFitWidth(bitmap, 100);
        public static Bitmap scaleToFitWidth(Bitmap b, int width)
        {
            float factor = width / (float) b.getWidth();
            return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
        }

        // Scale and maintain aspect ratio given a desired height
        // BitmapScaler.scaleToFitHeight(bitmap, 100);
        public static Bitmap scaleToFitHeight(Bitmap b, int height)
        {
            float factor = height / (float) b.getHeight();
            return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
        }

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
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
                    photoFile = new File(imgDecodableString);
                    imageView.setImageURI(selectedImage);

                    imageView.setVisibility(View.VISIBLE);
                    btnConfirm.setVisibility(View.VISIBLE);
                    break;

                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                    // by this point we have the camera photo on disk
                    Bitmap rotatedResizedImage = rotateResizeBitmapOrientation(photoFile.getAbsolutePath());

                    // Configure byte output stream
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    // Compress the image further
                    rotatedResizedImage.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                    File resizedFile = getPhotoFileUri(photoFileName + "_resized");
                    try {
                        resizedFile.createNewFile();
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(resizedFile);
                        // Write the bytes of the bitmap to file
                        fos.write(bytes.toByteArray());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    imageView.setVisibility(View.VISIBLE);
                    btnConfirm.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(rotatedResizedImage);
                    break;
            }
        } else {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ChangeProfilePictureActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // Annotate a method which is invoked if the user doesn't grant the permissions
    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showDeniedForStorage() {
        Toast.makeText(this, "Storage access was denied.", Toast.LENGTH_SHORT).show();
    }

    // Annotates a method which is invoked if the user
    // chose to have the device "never ask again" about a permission
    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showNeverAskForStorage() {
        Toast.makeText(this, "Storage access was revoked.", Toast.LENGTH_SHORT).show();
    }
}

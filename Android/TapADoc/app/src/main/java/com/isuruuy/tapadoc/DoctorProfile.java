package com.isuruuy.tapadoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DoctorProfile extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int GALLERY_REQUEST_CODE = 105;
    ImageView camera_image_view;
    Button camera_button, gallery_button;
    BottomNavigationView navigationView_profile;
    String currentPhotoPath, userID;
    StorageReference storageReference;
    TextView name,email, specialities,mobile;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        camera_image_view = findViewById(R.id.imageView_doctor);
        camera_button = findViewById(R.id.button_camera_doctor);
        gallery_button = findViewById(R.id.buttongallery_doctor);

        name = findViewById(R.id.doctor_profile_name);
        email = findViewById(R.id.doctor_profile_email);
        mobile = findViewById(R.id.doctor_profile_mobile);
        specialities = findViewById(R.id.doctor_profile_speciality);

        navigationView_profile = findViewById(R.id.bottom_navigation_doctor_profile);
        navigationView_profile.setSelectedItemId(R.id.nav_profile);

        navigationView_profile.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_logout:
                    firebaseAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                    return true;
                case R.id.nav_profile:
                    startActivity(new Intent(getApplicationContext(), DoctorProfile.class));
                    finish();
                    return true;
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), DoctorDashboard.class));
                    finish();
                    return true;
            }
            return false;
        });

        StorageReference profileRef = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid()+ "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(camera_image_view);
            }
        });

        camera_button.setOnClickListener(view -> askCameraPermission());

        gallery_button.setOnClickListener(view -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);
        });

        userID = firebaseAuth.getCurrentUser().getUid();
        documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            name.setText(documentSnapshot.getString("name"));
            email.setText(documentSnapshot.getString("email"));
            mobile.setText(documentSnapshot.getString("mobile"));
            specialities.setText(documentSnapshot.getString("specialities"));
        });

    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
        else{
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                File file = new File(currentPhotoPath);
                camera_image_view.setImageURI(Uri.fromFile(file));
                Log.d("TAG", "Absoulute url of the image is : " + Uri.fromFile(file));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DoctorProfile.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Do you want to upload this image?");
                builder.setIcon(R.drawable.ic_upload);
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    uploadImageToFirebase(file.getName(),contentUri);
                });
                builder.setNegativeButton("NO", (dialogInterface, i) -> System.out.println("Clicked NO"));
                builder.show();
               // uploadImageToFirebase(file.getName(),contentUri);
        }

        }

        if(requestCode == GALLERY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {

                Uri contentUri = data.getData();
                String timestamp = new SimpleDateFormat("yyyymmdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timestamp + "." + getFileExt(contentUri);
                Log.d("GALLERY", "Gallery image uri: "+ imageFileName);
                //camera_image_view.setImageURI(contentUri);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DoctorProfile.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Do you want to upload this image?");
                builder.setIcon(R.drawable.ic_upload);
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    uploadImageToFirebase(imageFileName, contentUri);
                });
                builder.setNegativeButton("NO", (dialogInterface, i) -> System.out.println("Clicked NO"));
                builder.show();
            }

        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        StorageReference image = storageReference.child("users/" + firebaseAuth.getCurrentUser().getUid()+ "/profile.jpg");
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(camera_image_view);
                        System.out.println("URL is : "+uri);
                        Map<String,Object> user = new HashMap<>();
                        user.put("profilePicture", uri.toString());
                        documentReference.update(user);
                    }

                });
                Toast.makeText(DoctorProfile.this, "Image is uploaded!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(DoctorProfile.this, "Upload failed!", Toast.LENGTH_SHORT).show());
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
       File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
       //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.isuruuy.android.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
}
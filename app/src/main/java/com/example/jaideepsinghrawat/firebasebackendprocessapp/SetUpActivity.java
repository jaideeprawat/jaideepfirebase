package com.example.jaideepsinghrawat.firebasebackendprocessapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.Placeholder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {
private CircleImageView imageView;
private EditText yourname;
private Button saveButton;
    private Uri resultUri=null;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private  StorageReference storageReference;
    private String name;
    private String userId;
    boolean isImageChange=false;
 private ProgressBar userAccountprogress;
    private static final int MY_PERMISSIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        imageView=findViewById(R.id.meal_image_order);
        saveButton=findViewById(R.id.save);
        yourname=findViewById(R.id.your_name);
        userAccountprogress=findViewById(R.id.useraccountprogress);
        firebaseAuth =FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userAccountprogress.setVisibility(View.VISIBLE);
        imageView.setEnabled(false);
       userId= firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("imageUri");
                        resultUri = Uri.parse(image);
                        yourname.setText(name);
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.ic_person);
                        Glide.with(SetUpActivity.this).setDefaultRequestOptions(requestOptions).load(image).into(imageView);
                    }else{
                        Toast.makeText(SetUpActivity.this,"data doesnt exist",Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(SetUpActivity.this,"firebase retrieve error",Toast.LENGTH_LONG).show();

                }
                userAccountprogress.setVisibility(View.INVISIBLE);
                imageView.setEnabled(true);

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = yourname.getText().toString();
                if (!TextUtils.isEmpty(name.trim()) && resultUri != null) {
                    userAccountprogress.setVisibility(View.VISIBLE);
                if(isImageChange) {
                   StorageReference refrence = storageReference.child("profile_picture").child(userId + ".jpg");
                   refrence.putFile(resultUri)
                           .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                   if (task.isSuccessful()) {
                                       saveInfirestore(task);
                                   } else {
                                       Toast.makeText(SetUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                       userAccountprogress.setVisibility(View.INVISIBLE);

                                   }

                               }
                           });

               } else {
                    saveInfirestore(null);

               }
           }else{
                    Toast.makeText(SetUpActivity.this, "image uri or name is blank", Toast.LENGTH_LONG).show();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if(checkAndRequestPermissions()){
                       setimagePicker();
                    }
                }else{
                    setimagePicker();
                }
            }
        });

    }

    private void saveInfirestore(Task<UploadTask.TaskSnapshot> task) {
        Uri imageuri;
        if(task!=null) {
            imageuri = task.getResult().getDownloadUrl();
        }else{
            imageuri=resultUri;
        }
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("imageUri", imageuri.toString());
        firebaseFirestore.collection("Users").document(userId).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetUpActivity.this, "value saved", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SetUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    userAccountprogress.setVisibility(View.INVISIBLE);


                } else {
                    Toast.makeText(SetUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    userAccountprogress.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    @Override    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setimagePicker();
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imageView.setImageURI(resultUri);
                isImageChange=true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private boolean checkAndRequestPermissions() {
        int permissionReadStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);


        int storagePermission = ContextCompat.checkSelfPermission(this,


                Manifest.permission.WRITE_EXTERNAL_STORAGE);



        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,


                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS);
            return false;
        }

        return true;
    }
    public void setimagePicker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetUpActivity.this);
    }

}

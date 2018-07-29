package com.example.jaideepsinghrawat.firebasebackendprocessapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView post_image;
    private EditText post_edittext;
    private Button post_button;
    private Uri resultUri;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private StorageReference reference;
    private FirebaseFirestore firebaseFirestore;
    private String currentUserId;
    private Bitmap compressedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar3) ;
        setSupportActionBar(toolbar);
        post_image=findViewById(R.id.post_image);
        post_edittext=findViewById(R.id.post_description);
        post_button=findViewById(R.id.post_button);
        progressBar=findViewById(R.id.progressBar2);
        post_image.setOnClickListener(this);
        post_button.setOnClickListener(this);
        firebaseFirestore=FirebaseFirestore.getInstance();
        reference= FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.post_image:
                selectImage();
                break;
            case R.id.post_button:
                sendPost();
                break;
                default:
                    break;
                
        }
    }

    private void sendPost() {
        final String description = post_edittext.getText().toString();
        if (!TextUtils.isEmpty(description.trim()) && resultUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            Long tsLong = System.currentTimeMillis()/1000;
            final String current_timestamp = tsLong.toString();
                final StorageReference postrefrence = reference.child("post_image").child(current_timestamp + ".jpg");
            postrefrence.putFile(resultUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                                final String downloaduri=task.getResult().getDownloadUrl().toString();
                                if (task.isSuccessful()) {
                                    File compressfile=new File(resultUri.getPath());

                                    try {
                                        compressedImageBitmap = new Compressor(PostActivity.this)

                                                .setMaxWidth(80)
                                                .setMaxHeight(80)
                                                .setQuality(2)
//                                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                                                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
//                                                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                                .compressToBitmap(compressfile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbdata = baos.toByteArray();

                                    UploadTask uploadTask = reference.child("thumbs").child(current_timestamp+".jpg").putBytes(thumbdata);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle unsuccessful uploads
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                            // ...
                                            String thumbdownloaduri=taskSnapshot.getDownloadUrl().toString();

                                            Map<String,Object> data=new HashMap<>();
                                            data.put("thumburi",thumbdownloaduri);
                                            data.put("imageUri",downloaduri);
                                            data.put("description",description);
                                            data.put("userId",currentUserId);
                                            data.put("timestamp",FieldValue.serverTimestamp());

                                            firebaseFirestore.collection("Post").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if(task.isSuccessful()){
                                                        Intent intent=new Intent(PostActivity.this,MainActivity.class);
                                                        startActivity(intent);
                                                    }else{
                                                        Toast.makeText(PostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                        }
                                    });

                                    progressBar.setVisibility(View.INVISIBLE);

                                } else {
                                    Toast.makeText(PostActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);

                                }

                            }
                        });

        }else{
            Toast.makeText(PostActivity.this, "image uri or descripttion is blank", Toast.LENGTH_LONG).show();
        }
    }

    private void selectImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512,512)
        .setAspectRatio(1,1)
                .start(PostActivity.this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                post_image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

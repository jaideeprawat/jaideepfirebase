package com.example.jaideepsinghrawat.firebasebackendprocessapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActicity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {
    private Button signup;
    private TextView changeatextview;
    ConstraintLayout wholescreen;
    ImageView img;
    ProgressBar progressBar;
    FirebaseAuth auth;
    private boolean signupActive=true;
    private EditText username,password;
    @Override
    protected void onStart() {
        super.onStart();
         FirebaseUser currentUser= auth.getCurrentUser();
        if(currentUser!=null){
           sendTomain();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acticity);
        signup=findViewById(R.id.sign_up);
        changeatextview=(TextView)findViewById(R.id.login) ;
        username=(EditText)findViewById(R.id.email_login);
        password=(EditText)findViewById(R.id.password_login);
        progressBar=(ProgressBar) findViewById(R.id.progress);

        wholescreen =(ConstraintLayout)findViewById(R.id.wholescreen);
        wholescreen.setOnKeyListener(this);
        changeatextview.setOnClickListener(this);
        signup.setOnClickListener(this);
        auth=FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.sign_up){
            signUp();
        }
        if(view.getId()==R.id.login){
            if(signupActive){
                signupActive=false;
                signup.setText("Login");
                changeatextview.setText("or SIGNUP");
            }else{
                signupActive=true;
                signup.setText("SIGNUP");
                changeatextview.setText("or Login");
            }

        }
    }


    public void signUp(){
        if(username.getText().toString().matches("") && password.getText().toString().matches("")){
            Toast.makeText(this,"A username and password required",Toast.LENGTH_LONG).show();
        }else if(!signupActive){
            progressBar.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(username.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                      sendTomain();
                    }else{
                      Toast.makeText(LoginActicity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }else{
            progressBar.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(username.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendTomain();
                    }else{
                      Toast.makeText(LoginActicity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    private void sendTomain() {
        Intent intent=new Intent(LoginActicity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_ENTER){
            signUp();
        }
        return false;    }
}

package com.example.jaideepsinghrawat.firebasebackendprocessapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        HomeFragment.OnFragmentInteractionListener,
        NotificationFragment.OnFragmentInteractionListener,
        AccountFragment.OnFragmentInteractionListener {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String currentUserId;
    private FloatingActionButton floatingActionButton;
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if(currentUser==null){
            sendtologin();
        }else{
            currentUserId=mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                       if(task.getResult().exists()){

                       }else{
                           Intent intent=new Intent(MainActivity.this,SetUpActivity.class);
                           startActivity(intent);
                           finish();
                       }
                    }else{
                        Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
//            Intent intent=new Intent(MainActivity.this,SetUpActivity.class);
//            startActivity(intent);
//            finish();
        }
//        updateUI(currentUser);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar3) ;
        setSupportActionBar(toolbar);
         floatingActionButton=findViewById(R.id.floatingActionButton3);
         floatingActionButton.setOnClickListener(this);
         BottomNavigationView bottomNavigationView=findViewById(R.id.bottomnavigation);
         bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                 switch (menuItem.getItemId()){
                     case R.id.account_menu:
                         fragmentTransaction(accountFragment);
                         return  true;
                     case R.id.notiification_menu:
                         fragmentTransaction(notificationFragment);

                         return true;
                     case R.id.home_mennu:
                         fragmentTransaction(homeFragment);

                         return true;

                 }
                 return false;
             }
         });
//         fragments
        homeFragment=new HomeFragment();
        notificationFragment=new NotificationFragment();
        accountFragment=new AccountFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_bar_search:
                return true;
            case R.id.logout:
                logout();
                return true;

            case R.id.action_setting:
                gotoEditProfile();
                return true;
                default:
                    return false;
        }
    }

    private void gotoEditProfile() {
        Intent intent=new Intent(MainActivity.this,SetUpActivity.class);
        startActivity(intent);
    }

    private void logout() {
        mAuth.signOut();
        sendtologin();
    }

    private void sendtologin() {
        Intent intent=new Intent(MainActivity.this,LoginActicity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.floatingActionButton3:
            Intent intent=new Intent(MainActivity.this,PostActivity.class);
            startActivity(intent);
            break;
            default:
                break;
        }
    }
    public void fragmentTransaction(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

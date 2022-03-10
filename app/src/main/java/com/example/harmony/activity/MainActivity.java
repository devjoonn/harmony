package com.example.harmony.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.harmony.R;
import com.example.harmony.fragment.ChatFragment;
import com.example.harmony.fragment.HomeFragment;
import com.example.harmony.fragment.PeopleFragment;
import com.example.harmony.fragment.PostFragment;
import com.example.harmony.fragment.UserInfoFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private LinearLayout ButtonLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Harmony");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                init();
                break;
        }
    }

    private void init(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            myStartActivity(LoginActivity.class);

        } else {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d("MainActivity", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("MainActivity", "No such document");
                                
                            }
                        }
                    } else {
                        Log.d("MainActivity", "get failed with ", task.getException());
                    }
                }
            });

            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainactivity_bottomnavigationview);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_home:
                            HomeFragment homeFragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainactivity_framelayout, homeFragment)
                                    .commit();
                            return true;
                        case R.id.action_post:
                            PostFragment postFragment = new PostFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainactivity_framelayout, postFragment)
                                    .commit();
                            return true;
                        case R.id.action_chat:
                            ChatFragment chatFragment = new ChatFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainactivity_framelayout, chatFragment)
                                    .commit();
                            return true;
                        case R.id.action_people:
                            PeopleFragment peopleFragment = new PeopleFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainactivity_framelayout, peopleFragment)
                                    .commit();
                            return true;
                        case R.id.account:
                            UserInfoFragment userInfoFragment = new UserInfoFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.mainactivity_framelayout, userInfoFragment)
                                    .commit();
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
}
package com.example.bpmapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class DrMainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    long backPressTime;
    Toast backToast;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID,userName,userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dr_main);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        userID = fUser.getUid();

        drawerLayout = findViewById(R.id.dr_drawer);
        navigationView = findViewById(R.id.dr_navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.heart:
                        startActivity(new Intent(getApplicationContext(), Heart.class));
                        break;
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext(), Chat.class));
                        break;
                    case R.id.fall:
                        startActivity(new Intent(getApplicationContext(), Fall.class));
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        break;
                }
                return true;
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();


        //User and Email

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null) {
                    userName = documentSnapshot.getString("full_name");
                    userEmail = documentSnapshot.getString("email");
                    View header = navigationView.getHeaderView(0);
                    ((TextView) header.findViewById(R.id.profileName)).setText(userName);
                    ((TextView) header.findViewById(R.id.profileEmail)).setText(userEmail);
                }
            }
        });

    }

    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            backToast = Toast.makeText(getBaseContext(), "Press again to close the app", Toast.LENGTH_SHORT);
            backToast.show();
        }else if(backPressTime + 2000 > System.currentTimeMillis()){
            //if I want to sign out I must add the Login Activity as MAIN in the manifest
            try{
                backToast.cancel();
            } catch (NullPointerException e){

            }
            super.onBackPressed();
            return;
        }
        backPressTime = System.currentTimeMillis();
    }
}

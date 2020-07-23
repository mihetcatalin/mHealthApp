package com.example.bpmapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
/*Users that already have an account can login here. The data introduced in these fields is checked against Firebase Authentication
*to see if the user is registered. Before submiting the information it checks for some basic criteria on the email and password */
public class Login extends AppCompatActivity {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String userID;
    String title;

    TextView mTitle, mBott;
    EditText mEmail, mPassword;
    Button mLoginBtn;
    ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTitle = findViewById(R.id.textLogin);
        mBott = findViewById(R.id.noAccount);
        mEmail = findViewById(R.id.profileEmail);
        mPassword = findViewById(R.id.editTextPass);
        mLoginBtn = findViewById(R.id.buttonLogin);
        mProgress = findViewById(R.id.progressLogin);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password Required");
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError("Minimum 6 characters");
                    return;
                }

                mProgress.setVisibility(View.VISIBLE);

                //authenticate
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //FirebaseUser fUser = fAuth.getCurrentUser();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference detectTitle = fStore.collection("users").document(userID);
                            detectTitle.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                                    if(documentSnapshot != null){
                                        title = documentSnapshot.getString("title");
                                        if(title.equals("doctor")){
                                            startActivity(new Intent(getApplicationContext(), DrMainActivity.class));
                                        }else if(title.equals("user")){
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                    }
                                }
                            });
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(Login.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                            mPassword.setText("");
                            mProgress.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });

        mBott.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}

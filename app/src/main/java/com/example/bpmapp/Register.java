package com.example.bpmapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*This class ensures the user's registration to Firebase. It takes the email and password as authentication method.
* The fields are made to take data specific to the given hint by using inputType.
* Once a few criteria are passed the user data is stored to Firebase Firestore and the authentication method and credentials
* to Firebase Authentication. Depending on the user type they are redirected to the corresponding main activity that holds the menu. */

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    EditText mFullName, mEmail, mPassword, mPhone, mDocPhone;
    TextView mTitle, mBott;
    CheckBox checkBox;
    Button mRegisterBtn;
    ProgressBar mProgress;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        checkBox = findViewById(R.id.checkBox);
        mFullName = findViewById(R.id.editTextName);
        mEmail = findViewById(R.id.profileEmail);
        mPhone = findViewById(R.id.editTextTel);
        mDocPhone = findViewById(R.id.editTextDoc);
        mPassword = findViewById(R.id.editTextPass);
        mRegisterBtn = findViewById(R.id.buttonRegister);
        mTitle = findViewById(R.id.textRegister);
        mBott = findViewById(R.id.alreadyReg);
        mProgress = findViewById(R.id.progressRegister);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullname = mFullName.getText().toString();
                final String phoneno = mPhone.getText().toString();
                final String docphoneno = mDocPhone.getText().toString();
                final boolean doc = checkBox.isChecked();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email Required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password Required");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Minimum 6 characters");
                    return;
                }

                if (doc == true){
                    mDocPhone.setError("Provide contact");
                    return;
                }

                mProgress.setVisibility(View.VISIBLE);

                //firebase registration
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            if(doc == true) {
                                final Map<String, Object> user = new HashMap<>();
                                user.put("title", "doctor");
                                user.put("full_name", "Dr. "+fullname);
                                user.put("email", email);
                                user.put("phone_number", phoneno);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "user profile created for" + userID);
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), DrMainActivity.class));
                            } else{
                                final Map<String, Object> user = new HashMap<>();
                                user.put("title", "user");
                                user.put("full_name", fullname);
                                user.put("email", email);
                                user.put("phone_number", phoneno);
                                user.put("physician", docphoneno);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "user profile created for" + userID);
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }

                        }
                        else {
                            Toast.makeText(Register.this, "An error has occurred. Please try again!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mProgress.setVisibility(View.GONE);
                        }
                    }
                });


            }
        });


        mBott.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}

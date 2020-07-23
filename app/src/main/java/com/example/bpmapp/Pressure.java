package com.example.bpmapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pressure extends AppCompatActivity {

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private PressureAdapter adapter;

    private TextView title;
    private EditText systolic;
    private TextView separator;
    private EditText diastolic;
    private Button post;
    private Button pdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);
        title = findViewById(R.id.value_name);
        systolic = findViewById(R.id.systolic_value);
        separator = findViewById(R.id.values_separator);
        diastolic = findViewById(R.id.diastolic_value);
        post = findViewById(R.id.post_button);
        pdf = findViewById(R.id.generate_button);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postValues();
            }
        });

        setUpRecyclerView();
    }

    private void postValues() {
        String user = fAuth.getCurrentUser().getUid();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        final String sys_value = systolic.getText().toString();
        String dias_value = diastolic.getText().toString();
        String bloodpressure = sys_value + "/" + dias_value;
        Map values = new HashMap();
        values.put("user", user);
        values.put("bloodpressure", bloodpressure);
        values.put("date", date);
        fStore.collection("medical_info").add(values).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Values Posted", Toast.LENGTH_SHORT).show();
                    systolic.setText("");
                    diastolic.setText("");
                }
            }
        });
    }

    private void setUpRecyclerView() {
        String user = fAuth.getCurrentUser().getUid();
        Query query = fStore.collection("medical_info")
                .whereEqualTo("user", user)
                .orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<PressureClass> options = new FirestoreRecyclerOptions.Builder<PressureClass>()
                .setQuery(query, PressureClass.class)
                .build();
        adapter = new PressureAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.pressure_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

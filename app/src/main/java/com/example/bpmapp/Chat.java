package com.example.bpmapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

/*This activity performs a query to display in the recycler view patients - if the authenticated user is a physician,
or the assigend medic - if the authenticated user is a patient*/

public class Chat extends AppCompatActivity {

    FloatingActionButton addButton;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    ChatAdapter adapter;
    String userID;
    String title;
    String medicNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        addButton = findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddChat.class));
                finish();
            }
        });

        chooseRecyclerView();
    }

    private void chooseRecyclerView() {
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference detectTitle = fStore.collection("users").document(userID);
        detectTitle.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    title = documentSnapshot.getString("title");
                    if (title.equals("user")) {
                        medicNo = documentSnapshot.getString("physician");
                        Query query = fStore.collection("users").whereEqualTo("phone_number", medicNo);
                        setUpRecyclerView(query);
                    } else {
                        medicNo = documentSnapshot.getString("phone_number");
                        Query query = fStore.collection("users").whereEqualTo("physician", medicNo);
                        setUpRecyclerView(query);
                    }

                }
            }
        });

    }



    public void setUpRecyclerView(Query query){
        FirestoreRecyclerOptions<ChatClass> options = new FirestoreRecyclerOptions.Builder<ChatClass>()
                .setQuery(query, ChatClass.class)
                .build();
        adapter = new ChatAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.chat_list_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String userID = documentSnapshot.getId();
                String userName = documentSnapshot.getString("full_name");
                Intent intent = new Intent(getApplicationContext(), Conversation.class);
                intent.putExtra("visited_userID", userID);
                intent.putExtra("visited_user_name", userName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

package com.example.bpmapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/*This activity performs a query to display in the recycler all the network connected to the application*/

public class AddChat extends AppCompatActivity{

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    AddChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = fStore.collection("users");
        FirestoreRecyclerOptions<AddChatClass> options = new FirestoreRecyclerOptions.Builder<AddChatClass>()
                .setQuery(query, AddChatClass.class)
                .build();
        adapter = new AddChatAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AddChatAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
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
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

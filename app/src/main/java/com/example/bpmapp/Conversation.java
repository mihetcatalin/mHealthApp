package com.example.bpmapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*This activity holds all the functionalities from the chat function. It takes text messages and stores them into Firebase Firestore.
* It allows user to search the phone's storage for PDF files and images. After the file format is chosen, the file is uploaded to Firebase Storage
* where it gets an unique URL. This URL is then sent like normal messages allowing the other participants to have access to that file.
* The key created to ensure that messages can be seen b only the two participants is the toFrom key. It can be senderID+receiverID or
* receiverID+senderID. depending on the messaege direction. It can't contain any other userID in it*/

public class Conversation extends AppCompatActivity {

    //for sending message
    private String receiverID;
    private String receiverName;
    private String senderID;
    private String toFrom;
    private String fromTo;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    //FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private String checker = null;
    private String myUri = "";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressBar progressBar;



    Toolbar conversationToolbar;
    private TextView toolbarName;
    private TextView testView;
    private ImageView toolbarPic;

    //from conversation layout
    private ImageButton sendButton;
    private ImageButton attachButton;
    private EditText messageText;

    private ConversationAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        FirebaseUser firebaseUser = fAuth.getCurrentUser();
        senderID = firebaseUser.getUid();


        //still to be added image and status
        final Intent intent = getIntent();
        receiverID = intent.getExtras().get("visited_userID").toString();
        receiverName = intent.getExtras().get("visited_user_name").toString();
        //Toast.makeText(Conversation.this, receiverID, Toast.LENGTH_SHORT).show();

        toFrom = senderID+receiverID;
        fromTo = receiverID+senderID;


        InitializeControllers();

        toolbarName.setText(receiverName);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] options = new CharSequence[]{
                        "Image",
                        "PDF"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Conversation.this);
                builder.setTitle("Select file type");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            checker = "image";
                            Intent imageIntent =  new Intent();
                            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                            imageIntent.setType("image/*");
                            startActivityForResult(imageIntent.createChooser(imageIntent,"Select image"),100);
                        }
                        if (which == 1){
                            checker = "pdf";
                            Intent pdfIntent = new Intent();
                            pdfIntent.setAction(Intent.ACTION_GET_CONTENT);
                            pdfIntent.setType("application/pdf");
                            startActivityForResult(pdfIntent.createChooser(pdfIntent, "Select PDF"), 100);
                        }
                    }
                });
                builder.show();
            }
        });

        setUpRecyclerView();
    }



    private void setUpRecyclerView() {

        Query query = fStore.collectionGroup("messages")
                .whereIn("toFrom", Arrays.asList(toFrom, fromTo))
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ConversationClass> options = new FirestoreRecyclerOptions.Builder<ConversationClass>()
                .setQuery(query, ConversationClass.class)
                .build();
        adapter = new ConversationAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.message_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null){

            fileUri = data.getData();

            if(checker.equals("pdf")){
                final StorageReference pdfStorageReference = FirebaseStorage.getInstance().getReference("documents").child(senderID + "." + "pdf");
                UploadTask uploadTask = pdfStorageReference.putFile(fileUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return pdfStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isComplete()){
                            Uri downloadUri = task.getResult();
                            myUri = downloadUri.toString();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String date = simpleDateFormat.format(new Date());

                            Map chat = new HashMap();
                            chat.put("message", myUri);
                            chat.put("type", checker);
                            chat.put("date", date);
                            chat.put("sender", senderID);
                            chat.put("toFrom", toFrom);
                            //chat.put("receiver", receiverID);
                            fStore.collection("chat").document(senderID).collection("messages").add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(Conversation.this, "Message sent", Toast.LENGTH_SHORT).show();
                                    messageText.setText("");
                                }
                            });
                        } else {
                            Toast.makeText(Conversation.this, "Not successful", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }else if(checker.equals("image")){
                final StorageReference imageStorageReference = FirebaseStorage.getInstance().getReference("images").child(senderID + "." + "jpg");
                uploadTask = imageStorageReference.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return imageStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isComplete()){
                            Uri downloadUri = task.getResult();
                            myUri = downloadUri.toString();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String date = simpleDateFormat.format(new Date());

                            Map chat = new HashMap();
                            chat.put("message", myUri);
                            chat.put("type", checker);
                            chat.put("date", date);
                            chat.put("sender", senderID);
                            chat.put("toFrom", toFrom);
                            //chat.put("receiver", receiverID);
                            fStore.collection("chat").document(senderID).collection("messages").add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(Conversation.this, "Message sent", Toast.LENGTH_SHORT).show();
                                    messageText.setText("");
                                }
                            });
                        } else {
                            Toast.makeText(Conversation.this, "Not successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }else{
                Toast.makeText(this, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        }
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

    private void SendMessage() {
        String textmessage = messageText.getText().toString().trim();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        Map chat = new HashMap();
        chat.put("message", textmessage);
        chat.put("date", date);
        chat.put("type", "text");
        chat.put("sender", senderID);
        chat.put("toFrom", toFrom);
        fStore.collection("chat").document(senderID).collection("messages").add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Conversation.this, "Message sent", Toast.LENGTH_SHORT).show();
                messageText.setText("");
            }
        });

        Map send = new HashMap();
        send.put("username", senderID);
        fStore.collection("chat").document(senderID).set(send);
    }

    private void InitializeControllers() {
        conversationToolbar = findViewById(R.id.conversation_toolbar);
        setSupportActionBar(conversationToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_conversation_toolbar, null);
        actionBar.setCustomView(view);

        toolbarName = findViewById(R.id.toolbar_name);
        toolbarPic = findViewById(R.id.toolbar_pic);
        sendButton = findViewById(R.id.send_button);
        attachButton = findViewById(R.id.attach);
        messageText = findViewById(R.id.message_box);
    }

}
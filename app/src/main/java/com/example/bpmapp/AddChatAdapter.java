package com.example.bpmapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

/*The adapter class is used in order to display data on the recyclerView. By usign FirestoreUI the data doesn't have to stored
 * in the form of a list, making it easier. The AddChatHolder is used to declare the data types from the inflated view*/

public class AddChatAdapter extends FirestoreRecyclerAdapter<AddChatClass, AddChatAdapter.AddChatHolder> {

    private OnItemClickListener listener;

    public AddChatAdapter(@NonNull FirestoreRecyclerOptions<AddChatClass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AddChatHolder holder, int position, @NonNull AddChatClass model) {
        holder.usernameView.setText(model.getFull_name());
        holder.statusView.setText(model.getStatus());

    }

    //default for any recyclerview
    @NonNull
    @Override
    public AddChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_chat_user, parent, false);
        return new AddChatHolder(v);
    }

    class AddChatHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView usernameView;
        TextView statusView;

        public AddChatHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.profileImage);
            usernameView = itemView.findViewById(R.id.username);
            statusView = itemView.findViewById(R.id.usermessage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.OnItemClick(getSnapshots().getSnapshot(position), position);
                    }

                }
            });
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(DocumentSnapshot documentSnapshot,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;

    }

}

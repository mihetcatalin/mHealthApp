package com.example.bpmapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

/*The adapter class is used in order to display data on the recyclerView. By usign FirestoreUI the data doesn't have to stored
 * in the form of a list, making it easier. The ChatHolder is used to declare the data types from the inflated view
This adapter is different because it has to make certain elements invisible, depending on the message direction.
* Each message type is treated differently in term of showing the information*/

public class ConversationAdapter extends FirestoreRecyclerAdapter<ConversationClass, ConversationAdapter.ConversationHolder> {

    private String sender;
    private String type;

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ConversationAdapter(@NonNull FirestoreRecyclerOptions<ConversationClass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ConversationHolder holder, final int position, @NonNull final ConversationClass model) {
        String currentUser = fAuth.getCurrentUser().getUid();
        sender = model.getSender().toString();
        type = model.getType();

        if(type.equals("text")){
            if(currentUser.equals(sender)){
                holder.senderView.setText(model.getMessage());
                holder.receiverView.setVisibility(View.INVISIBLE);

                holder.senderImage.setVisibility(View.INVISIBLE);
                holder.receiverImage.setVisibility(View.INVISIBLE);
            } else {
                holder.receiverView.setText(model.getMessage());
                holder.senderView.setVisibility(View.INVISIBLE);

                holder.senderImage.setVisibility(View.INVISIBLE);
                holder.receiverImage.setVisibility(View.INVISIBLE);
            }
        } else if(type.equals("image")){
            if(currentUser.equals(sender)){
                holder.senderView.setVisibility(View.INVISIBLE);
                holder.receiverView.setVisibility(View.INVISIBLE);

                Picasso.get().load(model.getMessage()).into(holder.senderImage);
                holder.receiverImage.setVisibility(View.INVISIBLE);
            } else {
                holder.receiverView.setVisibility(View.INVISIBLE);
                holder.senderView.setVisibility(View.INVISIBLE);

                holder.senderImage.setVisibility(View.INVISIBLE);
                Picasso.get().load(model.getMessage()).into(holder.receiverImage);
            }
        } else if(type.equals("pdf")){
            if(currentUser.equals(sender)){
                holder.senderView.setVisibility(View.INVISIBLE);
                holder.receiverView.setVisibility(View.INVISIBLE);

                Picasso.get().load(R.drawable.pdf_logo).resize(120,136).into(holder.senderImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
                holder.receiverImage.setVisibility(View.INVISIBLE);
            } else {
                holder.receiverView.setVisibility(View.INVISIBLE);
                holder.senderView.setVisibility(View.INVISIBLE);

                holder.senderImage.setVisibility(View.INVISIBLE);
                Picasso.get().load(R.drawable.pdf_logo).resize(120,136).into(holder.receiverImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }

    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubbles, parent, false);
        return new ConversationHolder(v);

    }

    class ConversationHolder extends RecyclerView.ViewHolder {
        TextView senderView;
        TextView receiverView;
        ImageView senderImage;
        ImageView receiverImage;

        public ConversationHolder(@NonNull View itemView) {
            super(itemView);
            senderView = itemView.findViewById(R.id.sender_message_text);
            receiverView = itemView.findViewById(R.id.receiver_message_text);
            senderImage = itemView.findViewById(R.id.sender_message_image);
            receiverImage = itemView.findViewById(R.id.receiver_message_image);

        }
    }
}

package com.example.bpmapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PressureAdapter extends FirestoreRecyclerAdapter<PressureClass, PressureAdapter.PressureHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PressureAdapter(@NonNull FirestoreRecyclerOptions<PressureClass> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PressureHolder holder, int position, @NonNull PressureClass model) {
        holder.bp_value.setText(model.getBloodpressure());
        holder.date_value.setText(model.getDate());

    }

    @NonNull
    @Override
    public PressureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_layout, parent, false);
        return new PressureHolder(v);
    }

    class PressureHolder extends RecyclerView.ViewHolder {
        TextView textView1;

        TextView textView3;
        TextView bp_value;
        TextView date_value;

        public PressureHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView3 = itemView.findViewById(R.id.textView3);
            bp_value = itemView.findViewById(R.id.bp_value);
            date_value = itemView.findViewById(R.id.date_value);
        }
    }

}

package com.travelbuddy.guideapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlanAdapter extends FirestoreRecyclerAdapter<Plan,PlanAdapter.plan_holder> {

    private FirebaseFirestore db;
   // private FirebaseAuth mAuth;
    private OnItemClickListener listener;
   // SharedPreferences shared;
    public PlanAdapter(FirestoreRecyclerOptions<Plan> options)
    {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final plan_holder holder, int position, @NonNull Plan model) {
        holder.plan_name123.setText(model.getPlan_name());
        holder.plan_places.setText(model.getPlan_places());
        holder.price.setText(model.getPrice().toString());
        holder.duration.setText(model.getDuration());
        //shared = getSharedPreferences("Travel_Data",Context.MODE_PRIVATE);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mAuth = FirebaseAuth.getInstance();
                //FirebaseUser user = mAuth.getCurrentUser();
                //String guide_id=user.getUid();
                //db=FirebaseFirestore.getInstance();
                //int position = getAdapterPosition();
                int position=holder.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener !=null)
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                //db.collection("Guides").document(guide_id).collection("Plans").document().delete();
                Log.d("EEEE","Delete button clicked");
            }
        });
    }

    @NonNull
    @Override
    public plan_holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_plan_details,viewGroup,false);
        return new plan_holder(v);
    }


    class plan_holder extends RecyclerView.ViewHolder
    {
        public TextView plan_name123;
        public TextView price;
        public TextView plan_places;
        public TextView duration;
        public Button delete;
        public plan_holder(View view) {
            super(view);
            plan_name123=view.findViewById(R.id.plan_name12);
            price=view.findViewById(R.id.plan_prices12);
            plan_places=view.findViewById(R.id.plan_places12);
            duration=view.findViewById(R.id.plan_description12);
            delete=view.findViewById(R.id.delete);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position=getAdapterPosition();
//                    if(position != RecyclerView.NO_POSITION && listener !=null)
//                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
//                    Log.d("MMMMMM","In view Holder");
//                }
//            });
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    if(position != RecyclerView.NO_POSITION && listener !=null)
//                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
//                }
//            });
        }

    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(PlanAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}

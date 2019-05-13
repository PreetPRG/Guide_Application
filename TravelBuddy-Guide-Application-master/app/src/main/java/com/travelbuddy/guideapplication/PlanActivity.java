package com.travelbuddy.guideapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PlanActivity extends AppCompatActivity {

    FirebaseFirestore db=FirebaseFirestore.getInstance();
    RecyclerView mRecyclerview2;
    String guide_id,guide_name;
    TextView guideData;
    SharedPreferences shared;
    private PlanAdapter adapter;
    Button addPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        shared = getSharedPreferences("Travel_Data",Context.MODE_PRIVATE);
        mRecyclerview2=findViewById(R.id.mRecyclerView2);
        mRecyclerview2.setLayoutManager(new LinearLayoutManager(this));
        //guide_id = getIntent().getStringExtra("guide_id");
        //guide_name = getIntent().getStringExtra("guide_name");
        addPlan = findViewById(R.id.addButton);
        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),addPlanActivity.class);
                startActivity(i);

            }
        });

        guide_id=shared.getString("guide_id","Error");
        guide_name=shared.getString("guide_name","Error");
        guideData =findViewById(R.id.guideData);
        guideData.setText("Plans of "+guide_name+":");

        FirebaseUser checkUser = FirebaseAuth.getInstance().getCurrentUser();
        if(checkUser == null){
            Intent SelectionPage = new Intent(getApplicationContext(),login_signUp.class);
            startActivity(SelectionPage);
            finish();
            return;
        }

        Query query=FirebaseFirestore.getInstance()
                .collection("Guides")
                .document(guide_id)
                .collection("Plans")
                .limit(50);

        FirestoreRecyclerOptions<Plan> options = new FirestoreRecyclerOptions.Builder<Plan>()
                .setQuery(query,Plan.class)
                .build();

        adapter=new PlanAdapter(options);
        mRecyclerview2.setAdapter(adapter);
        adapter.startListening();

        adapter.setOnItemClickListener(new PlanAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                //Plan plan=documentSnapshot.toObject(Plan.class);
                String id = documentSnapshot.getId();
                DocumentReference path = documentSnapshot.getReference();
                path.delete();

//                Toast.makeText(PlanActivity.this,
//                        "Position: " + position + "  ID:  " + id + "  \nPath " + path, Toast.LENGTH_SHORT).show();


                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                //i.putExtra("plan_id",id);
                //i.putExtra("city",value);
                //i.putExtra("Value2", "Simple Tutorial");
                // Set the request code to any code you like, you can identify the
                // callback via this code
                startActivity(i);
            }
        });

    }
}

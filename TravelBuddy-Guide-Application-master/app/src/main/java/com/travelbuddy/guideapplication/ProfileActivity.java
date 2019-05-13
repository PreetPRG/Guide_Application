package com.travelbuddy.guideapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private long male = 0;
    private long female = 1;
    private EditText name;
    private EditText description;
    private EditText experience;
    private EditText language;
    private Button submit;
    private Hashtable<String,String> map;
    private Hashtable<String,Long> mapGender;
    private Spinner genderSpinner,searchSpinner;
    private FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();
        List<String> genderList= new ArrayList<String>();
        genderList.clear();
        genderList.add("Male");
        genderList.add("Female");
        mapGender.put("Male",male);
        mapGender.put("Female",female);


        ArrayAdapter<String> gAdapter = new ArrayAdapter<String>
                (getApplicationContext(),
                        android.R.layout.simple_spinner_item, genderList);
        gAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(gAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Cities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> city = new ArrayList<String>();
                            city.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("EEEE", document.getId() + " => " + document.get("CityName"));
                                city.add((String) document.get("CityName"));
                                map.put(document.getString("CityName"),document.getId());
                            }
                            Collections.sort(city);
                            ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>
                                    (getApplicationContext(),
                                            android.R.layout.simple_spinner_item, city);
                            areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            searchSpinner.setAdapter(areasAdapter);
//                            submit.setEnabled(true);
//                            loading.setVisibility(View.INVISIBLE);

                        } else {
                            Log.d("EEEE", "Error getting documents: ", task.getException());
                        }
                    }
                });

        SharedPreferences shared = getSharedPreferences("Travel_Data", Context.MODE_PRIVATE);
        String guide_id = shared.getString("guide_id","Error");
        db.collection("Guides").document(guide_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot d = task.getResult();
                name.setText(d.getString("Guide_name"));
                description.setText(d.getString("Description"));
                language.setText(d.getString("Language"));
                experience.setText(d.getString("Experience"));
            }
        });


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        map = new Hashtable<>();
        mapGender = new Hashtable<>();
        submit=findViewById(R.id.profile_btn_update);
        genderSpinner = findViewById(R.id.spGender);
        searchSpinner=findViewById(R.id.spCity);
        experience=findViewById(R.id.profile_exp);
        description=findViewById(R.id.profile_desc);
        language=findViewById(R.id.profile_language);
        name=findViewById(R.id.profile_name);
        db = FirebaseFirestore.getInstance();
        searchSpinner = findViewById(R.id.spCity);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String genderString = genderSpinner.getSelectedItem().toString();
                long g = mapGender.get(genderString);

                String c_name = searchSpinner.getSelectedItem().toString();
                String c_key = map.get(c_name);
                SharedPreferences shared = getSharedPreferences("Travel_Data",Context.MODE_PRIVATE);
                String email=shared.getString("guide_email","Error");

                Map<String, Object> guideMap = new HashMap<>();
                guideMap.put("Current_city",c_key);
                //guideMap.put("Available", true);
                guideMap.put("Description",description.getText().toString().trim());
                guideMap.put("Experience",experience.getText().toString().trim());
                guideMap.put("Guide_email",email);
                guideMap.put("Gender",g);
                guideMap.put("Guide_name", name.getText().toString());
                guideMap.put("Language",language.getText().toString());
                //guideMap.put("Ratings", (long)3);
                //SharedPreferences shared = getSharedPreferences("Travel_Data", Context.MODE_PRIVATE);
                String guide_id = shared.getString("guide_id","Error");
                db.collection("Guides").document(guide_id)
                        .update(guideMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d(TAG, "DocumentSnapshot successfully written!");
                                showMessage("Profile Updated Successfully");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.w(TAG, "Error writing document", e);
                                showMessage("ERROR in Updating User Document");

                            }
                        });
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
                return;

            }
        });
    }

    private void showMessage(String message) {

        //TODO: Make generic toast message
        Toast.makeText(getApplicationContext(),message,Toast
                .LENGTH_LONG).show();
    }
}

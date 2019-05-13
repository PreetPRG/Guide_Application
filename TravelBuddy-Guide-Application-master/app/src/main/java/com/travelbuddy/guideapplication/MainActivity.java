package com.travelbuddy.guideapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Button UProfile;
    Button MPlans;
    Intent profile;
    TextView welcome;
    Intent plan;
    FirebaseUser user;
    private FirebaseAuth auth;
    Switch toggle;
    Button logout;
    SharedPreferences shared;
    TextView cityText;
    String cityName;
    String guide_id,uid,guideName;
    FirebaseFirestore db;
    DocumentReference docref,cityref;
    @Override
    protected void onStart() {
        super.onStart();

        docref = db.collection("Guides").document(guide_id);
        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        boolean b = document.getBoolean("Available");
                        String city_id = document.get("Current_city").toString();
                        cityref = db.collection("Cities").document(city_id);
                        cityref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    cityName = document.get("CityName").toString();
                                    cityText.setText("Current City :"+cityName);

                                }
                            }
                        });

                        if (b ^ toggle.isChecked()) {
                            toggle.toggle();
                        }
                    } else {
                        Log.d("TEST_MAIN", "No such document");
                        showMessage("Oops! Something Went Wrong!");
                        SharedPreferences.Editor editor = shared.edit();
                        editor.clear();
                        editor.commit();
                        auth.signOut();
                        Intent i = new Intent(getApplicationContext(),login_signUp.class);
                        startActivity(i);
                        finish();

                    }
                } else {
                    Log.d("TEST_MAIN", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profile=new Intent(MainActivity.this,ProfileActivity.class);
        plan =new Intent(MainActivity.this,PlanActivity.class);
        shared = getSharedPreferences("Travel_Data",Context.MODE_PRIVATE);
        MPlans=(Button) findViewById(R.id.button2);
        UProfile=(Button) findViewById(R.id.button);
        UProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(profile);
            }
        });
        MPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(plan);
            }
        });
        guide_id =  shared.getString("guide_id","ERROR");
        guideName = shared.getString("guide_name","");
        welcome = findViewById(R.id.welcomeText);
        welcome.setText("Welcome!, "+guideName);
        toggle = findViewById(R.id.availabilty);
        cityText = findViewById(R.id.cityName);



        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    db.collection("Guides").document(guide_id).update("Available",true);
                    showMessage("Availabilty set to ON");

                    //Toast.makeText(this,"Availabilty set to True",Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled
                    db.collection("Guides").document(guide_id).update("Available",false);
                    showMessage("Availability set to OFF");

                }
            }
        });



        logout = (Button) findViewById(R.id.button3);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = shared.edit();
                editor.clear();
                editor.commit();
                auth.signOut();
                Intent i = new Intent(getApplicationContext(),login_signUp.class);
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

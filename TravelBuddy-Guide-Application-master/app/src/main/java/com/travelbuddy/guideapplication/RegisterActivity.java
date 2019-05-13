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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RRRR";
    private TextView log;
    private EditText userEmail, userPassword, userPassword2, userName,description,experience,language;
    private ProgressBar loadingProgrss;
    private Spinner genderSpinner, searchSpinner;
    private Button regBtn;
    Intent home;
    private long male = 0;
    private long female = 1;
    Hashtable<String,String> map;
    Hashtable<String,Long> mapGender;
    TextView loading;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    SharedPreferences shared;

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
                            regBtn.setEnabled(true);
                            loading.setVisibility(View.INVISIBLE);

                        } else {
                            Log.d("EEEE", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        map = new Hashtable<>();
        mapGender = new Hashtable<>();
        description = findViewById(R.id.description);
        experience = findViewById(R.id.experience);
        language = findViewById(R.id.language);

        genderSpinner = findViewById(R.id.gender);


        searchSpinner = findViewById(R.id.citySelection);
        loading = findViewById(R.id.loadingText);
        userEmail =(EditText) findViewById(R.id.email_input_reg);
        userName = (EditText) findViewById(R.id.name);
        userPassword = (EditText) findViewById(R.id.pass_input_reg);
        userPassword2 = (EditText) findViewById(R.id.cpass_input_reg);
        loadingProgrss = (ProgressBar) findViewById(R.id.progressBar2);
        regBtn = (Button) findViewById(R.id.signup_btn);
        loadingProgrss.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        log = (TextView) findViewById(R.id.logLink);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(login);
                finish();
                return;
            }
        });
        home = new Intent(this,MainActivity.class);
        shared = getSharedPreferences("Travel_Data", Context.MODE_PRIVATE);


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                loadingProgrss.setVisibility(View.VISIBLE);

                final String email = userEmail.getText().toString().trim();
                final String password = userPassword.getText().toString().trim();
                final String password2 = userPassword2.getText().toString().trim();
                final String name = userName.getText().toString().trim();

                if (userEmail.getText().toString().trim().isEmpty()  ){
                    userEmail.setError("Please Enter Email");
                    userEmail.requestFocus();
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgrss.setVisibility(View.INVISIBLE);
                    return;

//

                }else if(userName.getText().toString().trim().isEmpty()){
                    userName.setError("Please Add Name!");
                    userName.requestFocus();
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgrss.setVisibility(View.INVISIBLE);
                    return;

                }else if(userPassword.getText().toString().trim().isEmpty()){
                    userPassword.setError("Please Add Proper Password!");
                    userPassword.requestFocus();
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgrss.setVisibility(View.INVISIBLE);
                    return;
                }else if(userPassword2.getText().toString().trim().isEmpty() || !password.equals(password2)){
                    userPassword2.setError("Please Add same confirm Password!");
                    userPassword2.requestFocus();
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgrss.setVisibility(View.INVISIBLE);
                    return;
                }

                else if(description.getText().toString().isEmpty()){
                    description.setError("Please Add Proper Description!");
                    description.requestFocus();
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgrss.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(experience.getText().toString().isEmpty()){
                    experience.setError("Please Add Proper Experience!");
                    experience.requestFocus();
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgrss.setVisibility(View.INVISIBLE);
                    return;
                }
                else if(language.getText().toString().isEmpty()){
                    language.setError("Please Add Proper Languages know to you!");
                    language.requestFocus();
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgrss.setVisibility(View.INVISIBLE);
                    return;
                }

                else{

                    //All Set GO FOR Authentication FiREBASE
                    CreateUserAccount(email,name,password);

                }
            }
        });
    }

    private void CreateUserAccount(final String email,final String name, final String password) {

        //this method create user account
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Account Created
                            showMessage("Account Created");

                            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                            String uid = currentFirebaseUser.getUid();


                            String desc = description.getText().toString();
                            String exp = experience.getText().toString();

                            String genderString = genderSpinner.getSelectedItem().toString();
                            long g = mapGender.get(genderString);

                            String c_name = searchSpinner.getSelectedItem().toString();
                            String c_key = map.get(c_name);

                            String lang = language.getText().toString();
                            SharedPreferences.Editor editor = shared.edit();
                            editor.putString("guide_email",email);
                            editor.putString("guide_id",uid);
                            editor.putString("guide_name",name);
//                            UserRegister u = new UserRegister(name,email,sha_password);
                            long  x = 0;
                            long y = 3;
//                            GuideRegister guide = new GuideRegister(name,"",x,"English",y,true,
//                                    "0 Years","");
                            String cityID = map.get(searchSpinner.getSelectedItem().toString());
                            editor.putString("city_id",cityID);
                            editor.putString("cityName",searchSpinner.getSelectedItem().toString());
                            editor.commit();

                            Map<String, Object> guideMap = new HashMap<>();
                            guideMap.put("Current_city", c_key);
                            guideMap.put("Available", true);
                            guideMap.put("Description", desc);
                            guideMap.put("Experience", exp);
                            guideMap.put("Gender",g);
                            guideMap.put("Guide_name", name);
                            guideMap.put("Guide_email",email);
                            guideMap.put("Language",lang );
                            guideMap.put("Ratings", (long)3);

                            db.collection("Guides").document(uid)
                                    .set(guideMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                            showMessage("Guide Registerd");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                            showMessage("ERROR in writing USer Document");
                                        }
                                    });



                            changeActivity();
                        }
                        else{
                            showMessage("Account Creation Failed :"+task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgrss.setVisibility(View.INVISIBLE);


                        }
                    }
                });


    }

    private void changeActivity() {
        startActivity(home);
        finish();
        return;
    }

    private void showMessage(String message) {

        //TODO: Make generic toast message
        Toast.makeText(getApplicationContext(),message,Toast
                .LENGTH_LONG).show();
    }

    public String getSHA(String input) {

        try {

            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown"
                    + " for incorrect algorithm: " + e);

            return null;
        }
    }


}

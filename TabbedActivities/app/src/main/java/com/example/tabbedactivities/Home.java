package com.example.tabbedactivities;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends Application {
    public void onCreate(){
        super.onCreate();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        if(firebaseUser!=null && firebaseUser.isEmailVerified()){
            firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if(task.getResult().get("role").equals("Student")){
                                    Intent intent = new Intent(Home.this, LoggedIn.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else  if(task.getResult().get("role").equals("Faculty")){
                                    Intent intent = new Intent(Home.this, LoggedInFaculty.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Home.this, "Your Role is not defined Contact Admin", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Home.this, "Error while fetching Role." + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }
    }
}

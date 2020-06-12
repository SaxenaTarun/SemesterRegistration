package com.example.tabbedactivities.ui.feedback;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tabbedactivities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class FeedbackFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private String userID;
    private EditText email, feedback;
    private Button submit_feedback;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);

        email = v.findViewById(R.id.email);
        feedback = v.findViewById(R.id.feedback);

        submit_feedback = v.findViewById(R.id.submit_feedback);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        submit_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String semail = email.getText().toString().trim();
                String sfeedback = feedback.getText().toString().trim();
                if(semail.length() < 1 &&sfeedback.length() < 1){
                    Toast.makeText(getActivity(), "Please Fill your Form", Toast.LENGTH_SHORT).show();
                    return;
                }

                userID = firebaseAuth.getCurrentUser().getUid();
                final DocumentReference documentReference = firebaseFirestore.collection("feedbacks").document();
                final Map<String, Object> feedbackObj = new HashMap<>();
                feedbackObj.put("email", semail);
                feedbackObj.put("feedback",sfeedback);
                feedbackObj.put("userId", userID);
                firebaseFirestore.collection("users").document(userID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    feedbackObj.put("name", task.getResult().get("name"));
                                } else {
                                    feedbackObj.put("name", null);
                                }
                                documentReference.set(feedbackObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("SUCCESS","SuccessFully submitted feedback for " + userID);
                                        Toast.makeText(getContext(), "SuccessFully Submitted the FeedBack", Toast.LENGTH_LONG).show();
                                        email.setText("");
                                        feedback.setText("");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("FAILURE","Something Occured and Error Happened");
                                        Toast.makeText(getContext(), "Some Error Occured", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
            }
        });
        return v;
    }
}

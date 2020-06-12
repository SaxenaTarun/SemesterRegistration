package com.example.tabbedactivities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends Fragment {

    public Register() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Button register_btn;
    private EditText email, pwd, cnfrmpwd, enrollmentNo, name;
    private Spinner role;

    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        register_btn = (Button) v.findViewById(R.id.button);
        email = v.findViewById(R.id.email);
        pwd = v.findViewById(R.id.pwd);
        cnfrmpwd = v.findViewById(R.id.cnfrmpwd);
        enrollmentNo = v.findViewById(R.id.enrollmentNo);
        name = v.findViewById(R.id.name);
        role = v.findViewById(R.id.role);

        final String[] srole = {""};

        progressBar = v.findViewById(R.id.progress);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(role.getSelectedItem().toString().equals("Student")){
                    setEnrollmentVisible();
                    srole[0] = "Student";
                } else {
                    setEnrollmentInvisible();
                    srole[0] = "Faculty";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               progressBar.setVisibility(View.VISIBLE);
               register_btn.setClickable(false);
               final String nm = name.getText().toString();
               final String en = enrollmentNo.getText().toString();
               final String em = email.getText().toString();
               String pw = pwd.getText().toString();
               String cnfpwd = cnfrmpwd.getText().toString();
               if(!pw.equals(cnfpwd)) {
                   progressBar.setVisibility(View.GONE);
                   register_btn.setClickable(true);
                   Toast.makeText(getActivity(), "Password doesn't match.", Toast.LENGTH_LONG).show();
                   return;
               }
               if(pw.length() < 8 ){
                   progressBar.setVisibility(View.GONE);
                   register_btn.setClickable(true);
                   Toast.makeText(getActivity(), "Password must be 8 character long.", Toast.LENGTH_LONG).show();
                   return;
               }
               firebaseAuth.createUserWithEmailAndPassword( em, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   //Create User in DB
                                   DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
                                   final Map<String, Object> user = new HashMap<>();
                                   user.put("name", nm);
                                   user.put("email", em);
                                   user.put("role", srole[0]);
                                   if(srole[0].equals("Student")){
                                       user.put("enrollment_no", en);
                                       user.put("enrolled", false);
                                   }
                                   documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           Toast.makeText(getContext(), "SuccessFully Created User", Toast.LENGTH_LONG).show();
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(getContext(), "Some Error Occured. Please try again.", Toast.LENGTH_LONG).show();
                                       }
                                   });
                                   if(task.isSuccessful()){
                                       register_btn.setClickable(true);
                                       Toast.makeText(getActivity(), "SuccessFully Registered. Please Verify your Email.\n Check your Registered Email", Toast.LENGTH_LONG).show();
                                   } else {
                                       Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                   }
                               }
                           });
                           resetInputForm();
                       } else {
                           Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                           resetInputForm();
                       }
                       progressBar.setVisibility(View.GONE);
                   }
               });
           }
       });
        return v;
    }
    private  void resetInputForm(){
         email.setText(""); pwd.setText(""); cnfrmpwd.setText("");
    }
    private  void setEnrollmentVisible(){
        enrollmentNo.setVisibility(View.VISIBLE);
    }
    private void setEnrollmentInvisible(){
        enrollmentNo.setVisibility(View.GONE);
    }
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.button:
//                Toast.makeText(getActivity(), "Register Clicked", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }
}

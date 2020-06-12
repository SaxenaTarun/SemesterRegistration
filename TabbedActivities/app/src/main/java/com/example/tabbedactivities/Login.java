package com.example.tabbedactivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Login extends Fragment {


    public Login() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    private EditText uname, pwd;
    private TextView forgot_pwd;
    private Button login_btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        login_btn = v.findViewById(R.id.login_btn);
        uname = v.findViewById(R.id.username);
        pwd = v.findViewById(R.id.password);
        forgot_pwd = v.findViewById(R.id.forgot_pwd);

        progressBar = v.findViewById(R.id.progress);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //b.setOnClickListener(this);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_btn.setClickable(false);
                progressBar.setVisibility(View.VISIBLE);
                String userName = uname.getText().toString();
                String password = pwd.getText().toString();
                if(checkEmailPassword(userName, password)){
                    firebaseAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            login_btn.setClickable(true);
                            if(task.isSuccessful()){
                                if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                    Toast.makeText(getActivity(), "Login SuccessFully", Toast.LENGTH_LONG).show();
                                    firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        if(task.getResult().get("role").equals("Student")){
                                                            Intent i = new Intent(getActivity(), LoggedIn.class);
                                                            startActivity(i);
                                                        } else  if(task.getResult().get("role").equals("Faculty")){
                                                            Intent i = new Intent(getActivity(), LoggedInFaculty.class);
                                                            startActivity(i);
                                                        } else {
                                                            Toast.makeText(getActivity(), "Your Role is not defined Contact Admin", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(getActivity(), "Error while fetching Role." + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(getActivity(), "Please verify your Email for Access", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Make sure to fill Form", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    login_btn.setClickable(true);
                }

            }
        });
        forgot_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForgotPassword.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        return v;
    }

    private boolean checkEmailPassword ( String username, String password){
        if(username.length() == 0){
            return false;
        }
        if(password.length() == 0){
            return false;
        }
        return true;
    }
}

package com.example.tabbedactivities.ui.edit_profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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

public class EditProfileFragment extends Fragment {

    private EditProfileViewModel editProfileViewModel;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    private String userID;
    private ImageView edit;
    private Button saveChanges;
    private EditText fname, mname, dob, cs, bg, eno, eb;
    private TextView tfname, tmname, tdob, tcs, tbg, teno, teb, tname, temail, userName, userEmail;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        initializerViews(root);
        edit = root.findViewById(R.id.editProfile);
        tname =  root.findViewById(R.id.userName);
        temail = root.findViewById(R.id.userEmail);

        saveChanges = root.findViewById(R.id.editSaveChanges);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges.setVisibility(View.VISIBLE);
                tfname.setVisibility(View.GONE);
                tmname.setVisibility(View.GONE);
                tdob.setVisibility(View.GONE);
                tcs.setVisibility(View.GONE);
                tbg.setVisibility(View.GONE);
                teno.setVisibility(View.GONE);
                teb.setVisibility(View.GONE);
                fname.setVisibility(View.VISIBLE);
                mname.setVisibility(View.VISIBLE);
                dob.setVisibility(View.VISIBLE);
                cs.setVisibility(View.VISIBLE);
                bg.setVisibility(View.VISIBLE);
                eno.setVisibility(View.VISIBLE);
                eb.setVisibility(View.VISIBLE);
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges.setVisibility(View.GONE);
                tfname.setVisibility(View.VISIBLE);
                tmname.setVisibility(View.VISIBLE);
                tdob.setVisibility(View.VISIBLE);
                tcs.setVisibility(View.VISIBLE);
                tbg.setVisibility(View.VISIBLE);
                teno.setVisibility(View.VISIBLE);
                teb.setVisibility(View.VISIBLE);
                fname.setVisibility(View.GONE);
                mname.setVisibility(View.GONE);
                dob.setVisibility(View.GONE);
                cs.setVisibility(View.GONE);
                bg.setVisibility(View.GONE);
                eno.setVisibility(View.GONE);
                eb.setVisibility(View.GONE);
                getDataAndUpdate();
            }
        });
        fetchDataAndUpdate();

        return root;
    }

    private void initializerViews(View v){
        fname = v.findViewById(R.id.euserFatherName);
        mname = v.findViewById(R.id.euserMotherName);
        dob= v.findViewById(R.id.euserDOB);
        cs= v.findViewById(R.id.euserSemester);
        bg= v.findViewById(R.id.euserBloodGroup);
        eno= v.findViewById(R.id.euserEnrollmentNo);
        eb= v.findViewById(R.id.euserBranch);
        tfname = v.findViewById(R.id.userFatherName);
        tmname = v.findViewById(R.id.userMotherName);
        tdob= v.findViewById(R.id.userDOB);
        tcs= v.findViewById(R.id.userSemester);
        tbg= v.findViewById(R.id.userBloodGroup);
        teno= v.findViewById(R.id.userEnrollmentNo);
        teb= v.findViewById(R.id.userBranch);
        userName = v.findViewById(R.id.userName);
        userEmail = v.findViewById(R.id.userEmail);
    }


    private void getDataAndUpdate(){
        String sfname = fname.getText().toString().trim();
        String smname = mname.getText().toString().trim();
        String sdob = dob.getText().toString().trim();
        String scs = cs.getText().toString().trim();
        String sbg = bg.getText().toString().trim();
        String seno = eno.getText().toString().trim();
        String seb = eb.getText().toString().trim();

        Map<String, Object> user = new HashMap<>();
        user.put("father_name",sfname);
        user.put("mother_name",smname);
        user.put("dob",sdob);
        user.put("current_semester", scs);
        user.put("blood_group", sbg);
        user.put("enrollment_no", seno);
        user.put("branch", seb);

        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SUCCESS","SuccessFully submitted "+userID);
                Toast.makeText(getContext(), "SuccessFully Submitted.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FAILURE","Something Occured and Error Happened");
                Toast.makeText(getContext(), "Some Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }
    //Fetch data from firebase and update profile Element
    private  void fetchDataAndUpdate(){
        DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Toast.makeText(getActivity(), "Fetched Document", Toast.LENGTH_LONG).show();
                    tfname.setText((String) document.get("father_name"));
                    tmname.setText((String) document.get("mother_name"));
                    tdob.setText((String) document.get("dob"));
                    tcs.setText((String) document.get("current_semester"));
                    tbg.setText((String) document.get("blood_group"));
                    teno.setText((String) document.get("enrollment_no"));
                    teb.setText((String) document.get("branch"));
                    fname.setText((String) document.get("father_name"));
                    mname.setText((String) document.get("mother_name"));
                    dob.setText((String) document.get("dob"));
                    cs.setText((String) document.get("current_semester"));
                    bg.setText((String) document.get("blood_group"));
                    eno.setText((String) document.get("enrollment_no"));
                    eb.setText((String) document.get("branch"));
                    userName.setText((String) document.get("name"));
                    userEmail.setText((String) document.get("email"));

                } else {
                    Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

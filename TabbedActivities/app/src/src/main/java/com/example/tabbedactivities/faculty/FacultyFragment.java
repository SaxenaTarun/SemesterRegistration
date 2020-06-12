package com.example.tabbedactivities.faculty;

import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tabbedactivities.ApplicantListAdapter;
import com.example.tabbedactivities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FacultyFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ListView pendingApplications, approvedApplications, rejectedApplications;
    ArrayList<String[]> pendingApplicants = new ArrayList<>();
    ArrayList<String[]> approvedApplicants = new ArrayList<>();
    ArrayList<String[]> rejectedApplicants = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_faculty, container, false);
        initializeViews(root);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("pendingSemesterForm")
                .whereEqualTo("approved", false)
                .whereEqualTo("rejected", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot document = task.getResult();
                            for(QueryDocumentSnapshot doc : document){
                                Map documentSnapshot = doc.getData();
                                String name = (String) documentSnapshot.get("name");
                                String enrollment_no = (String) documentSnapshot.get("enrollment_no");
                                String initials = "";
                                String[] arrOfName = name.split(" ");
                                for(String partName: arrOfName){
                                    initials += String.valueOf(partName.charAt(0));
                                }
                                String[] applicant = {name, enrollment_no, initials};
                                pendingApplicants.add(applicant);
                            }
                            for(String[] applicant: pendingApplicants){
                                Log.d("Faculty", applicant[0]);
                            }
                            renderList();

                        }
                    }
                });

        firebaseFirestore.collection("pendingSemesterForm").whereEqualTo("approved", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot document = task.getResult();
                            for(QueryDocumentSnapshot doc : document){
                                Map documentSnapshot = doc.getData();
                                String name = (String) documentSnapshot.get("name");
                                String enrollment_no = (String) documentSnapshot.get("enrollment_no");
                                String initials = "";
                                String[] arrOfName = name.split(" ");
                                for(String partName: arrOfName){
                                    initials += String.valueOf(partName.charAt(0));
                                }
                                String[] applicant = {name, enrollment_no, initials};
                                approvedApplicants.add(applicant);
                            }
                            for(String[] applicant: approvedApplicants){
                                Log.d("Faculty", applicant[0]);
                            }
                            renderListApproved();

                        }
                    }
                });
        firebaseFirestore.collection("pendingSemesterForm").whereEqualTo("rejected", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot document = task.getResult();
                            for(QueryDocumentSnapshot doc : document){
                                Map documentSnapshot = doc.getData();
                                String name = (String) documentSnapshot.get("name");
                                String enrollment_no = (String) documentSnapshot.get("enrollment_no");
                                String initials = "";
                                String[] arrOfName = name.split(" ");
                                for(String partName: arrOfName){
                                    initials += String.valueOf(partName.charAt(0));
                                }
                                String[] applicant = {name, enrollment_no, initials};
                                rejectedApplicants.add(applicant);
                            }
                            for(String[] applicant: rejectedApplicants){
                                Log.d("Faculty", applicant[0]);
                            }
                            renderListRejected();

                        }
                    }
                });


        pendingApplications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView en = view.findViewById(R.id.studentEnrollmentNo);
                Intent i = new Intent(getContext(), IndividualForm.class);
                i.putExtra("enrollment_no", en.getText());
                i.putExtra("collection", "pendingSemesterForm");
                startActivity(i);
                Log.d("FAC", String.valueOf(en.getText()) );
            }
        });
        approvedApplications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView en = view.findViewById(R.id.studentEnrollmentNo);
                Intent i = new Intent(getContext(), IndividualForm.class);
                i.putExtra("enrollment_no", en.getText());
                i.putExtra("collection", "pendingSemesterForm");
                startActivity(i);
                Log.d("FAC", String.valueOf(position) );
            }
        });
        rejectedApplications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView en = view.findViewById(R.id.studentEnrollmentNo);
                Intent i = new Intent(getContext(), IndividualForm.class);
                i.putExtra("enrollment_no", en.getText());
                i.putExtra("collection", "pendingSemesterForm");
                startActivity(i);
                Log.d("FAC", String.valueOf(position) );
            }
        });
        return root;
    }

    private  void initializeViews(View v){
        pendingApplications = v.findViewById(R.id.pendingApplications);
        approvedApplications = v.findViewById(R.id.approvedApplications);
        rejectedApplications = v.findViewById(R.id.rejectedApplications);
    }

    private void renderList(){
        ApplicantListAdapter adapter = new ApplicantListAdapter(getActivity(), R.layout.list_item, pendingApplicants);
        pendingApplications.setAdapter(adapter);
    }
    private void renderListApproved(){
        ApplicantListAdapter adapter = new ApplicantListAdapter(getActivity(), R.layout.list_item, approvedApplicants);
        approvedApplications.setAdapter(adapter);
    }
    private void renderListRejected(){
        ApplicantListAdapter adapter = new ApplicantListAdapter(getActivity(), R.layout.list_item, rejectedApplicants);
        rejectedApplications.setAdapter(adapter);
    }
}

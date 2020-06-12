package com.example.tabbedactivities.faculty;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tabbedactivities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class IndividualForm extends AppCompatActivity {
    private static final String TAG = "Individual Form";
    Button success, reject;
    TextView enrollmentNo, formId, bankName, utrNo, paymentMode, name, semester, backlog, elective;
    ListView core;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String sformId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_form);

        initializeViews();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        final Bundle extras = getIntent().getExtras();
        if(extras != null){
            setTitle(extras.get("enrollment_no").toString());
            enrollmentNo.setText( extras.get("enrollment_no").toString());
            firebaseFirestore.collection(extras.get("collection").toString()).whereEqualTo("enrollment_no", extras.get("enrollment_no").toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot documents = task.getResult();
                                for(DocumentSnapshot doc : documents) {
                                    enrollmentNo.setText(doc.get("enrollment_no").toString());
                                    formId.setText(doc.getId().toString());
                                    sformId = doc.getId().toString();
                                    bankName.setText(doc.get("bank").toString());
                                    utrNo.setText(doc.get("unique_transaction_no").toString());
                                    paymentMode.setText(doc.get("payment_mode").toString());
                                    name.setText(doc.get("name").toString());
                                    semester.setText(doc.get("semester").toString());
                                    backlog.setText(doc.get("backlog").toString());
                                    elective.setText(doc.get("elective_subject").toString());
                                    Map<String, String> core_subjects = (Map<String, String>) doc.get("core_subjects");
                                    String[] subjects = new String[core_subjects.size()];
                                    int i = 0;
                                    for(Map.Entry<String, String > core_subject : core_subjects.entrySet()){
                                        subjects[i] = (core_subject.getValue());
                                        i++;
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, subjects);
                                    core.setAdapter(adapter);
                                    if((Boolean) doc.get("approved") == true || (Boolean) doc.get("rejected") == true){
                                        success.setVisibility(View.GONE);
                                        reject.setVisibility(View.GONE);
                                    };
                                }
                            }
                        }
                    });
            success.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sformId != null){
                        firebaseFirestore.collection(extras.get("collection").toString()).document(sformId)
                                .update("approved", true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Approved Application SuccessFully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sformId != null){
                        firebaseFirestore.collection(extras.get("collection").toString()).document(sformId)
                                .update("rejected", true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Rejected Application SuccessFully", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

    }

    private void initializeViews(){
        success = findViewById(R.id.approveForm);
        reject = findViewById(R.id.rejectForm);
        formId = findViewById(R.id.individualFormId);
        bankName = findViewById(R.id.individualBankName);
        paymentMode = findViewById(R.id.individualPaymentMode);
        backlog = findViewById(R.id.individualBacklog);
        name = findViewById(R.id.individualName);
        semester = findViewById(R.id.individualSemester);
        utrNo = findViewById(R.id.individualUTRNo);
        enrollmentNo = findViewById(R.id.enrollmentNo);
        core = findViewById(R.id.individualCoreSubjects);
        elective = findViewById(R.id.individualElectiveSubjects);
    }
}

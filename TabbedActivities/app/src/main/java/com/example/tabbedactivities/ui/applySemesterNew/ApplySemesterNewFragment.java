package com.example.tabbedactivities.ui.applySemesterNew;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tabbedactivities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ApplySemesterNewFragment extends Fragment {

    private static final String TAG = "Apply for Semester";
    private ApplySemesterNewViewModel applySemesterNewViewModel;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    Spinner bank, payment, semester;
    EditText unique_no;
    RadioGroup backlog_group, acknowledgement;
    String formId;
    Button submit;
    LinearLayout core_subjects;
    RadioGroup elective_subjects;
    Integer[] core_subject_id = new Integer[6];
    Integer[] elective_subject_id = new Integer[4];
    int core_subject_size, elective_subject_size;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        applySemesterNewViewModel =
                ViewModelProviders.of(this).get(ApplySemesterNewViewModel.class);
        final View root = inflater.inflate(R.layout.apply_semester_new, container, false);
        setViews(root);
        final TextView textView = root.findViewById(R.id.form_id);

        applySemesterNewViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Search for existing Form if there
        firebaseFirestore.collection("pendingSemesterForm")
                .whereEqualTo("userId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                final String docId = firebaseFirestore.collection("pendingSemesterForm").document().getId();
                                final DocumentReference documentReference = firebaseFirestore.collection("pendingSemesterForm").document(docId);
                                final Map<String, Object> form = new HashMap<String, Object>();
                                form.put("approved", false);
                                form.put("rejected", false);
                                form.put("userId", firebaseAuth.getCurrentUser().getUid());
                                firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    form.put("name", task.getResult().get("name"));
                                                    form.put("enrollment_no", task.getResult().get("enrollment_no"));
                                                    documentReference.set(form).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                textView.append(docId);
                                                                formId = docId;
                                                            } else {
                                                                textView.append("Some Error Occured Try Again Later");
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    textView.append("Some Error Occured Try Again Later");
                                                }
                                            }
                                        });

                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    textView.append(document.getId());
                                    formId = document.getId();
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                message(String.valueOf(position));
                firebaseFirestore.collection("subject_list").document(String.valueOf(position+1))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                        Map<String, String> electives = (Map) task.getResult().get("elective_subjects");
                                        Map<String, String> map = (Map) task.getResult().get("core_subjects");
                                        if (map != null) {
                                            core_subject_size = map.size();
                                            int i = 0;
                                            core_subjects.removeAllViews();
                                            TextView heading = new TextView(getActivity());
                                            heading.setText("Core Subjects");
                                            heading.setTextSize(20);
                                            heading.setTextColor(Color.BLACK);
                                            core_subjects.addView(heading);
                                            for( Map.Entry<String, String> subject: map.entrySet()){
                                                Random rand = new Random();
                                                core_subject_id[i] = rand.nextInt(1000000);
                                                CheckBox sub = new CheckBox(getActivity());
                                                sub.setId(core_subject_id[i]);
                                                sub.setText(subject.getValue());
                                                core_subjects.addView(sub);
                                                i++;
                                            }
                                        } else { elective_subjects.removeAllViews(); }
                                        if( electives != null){
                                            elective_subject_size = electives.size();
                                            int i = 0;
                                            elective_subjects.removeAllViews();
                                            for( Map.Entry<String, String> subject: electives.entrySet()){
                                                Random rand = new Random();
                                                elective_subject_id[i] = rand.nextInt(1000000);
                                                RadioButton sub = new RadioButton(getActivity());
                                                sub.setText(subject.getValue());
                                                sub.setId(elective_subject_id[i]);
                                                elective_subjects.addView(sub);
                                                i++;
                                            }
                                        } else { elective_subjects.removeAllViews(); }
                                    }
                                else {
                                    message(task.getException().toString());
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                message("Please Select Semester");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( acknowledgement.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getActivity(), "Please Accept the acknowledgement", Toast.LENGTH_SHORT).show();
                } else {
                    if(acknowledgement.getCheckedRadioButtonId() == R.id.deny){
                        Toast.makeText(getActivity(), "Please Accept the acknowledgement", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> form = new HashMap<String, Object>();
                        form.put("bank",bank.getSelectedItem().toString());
                        form.put("payment_mode", payment.getSelectedItem().toString());
                        if(unique_no.getText().toString().length() < 1){
                            message("Please Fill in the UTR No.");
                            return;
                        }
                        form.put("unique_transaction_no", unique_no.getText().toString());
                        //Check for Backlog
                        if(backlog_group.getCheckedRadioButtonId() != -1){
                            if(backlog_group.getCheckedRadioButtonId() == R.id.form_backlog){
                                form.put("backlog", true);
                            } else {
                                form.put("backlog", false);
                            }
                        } else {
                            message("Please Select for Backlog");
                            return;
                        }
                        form.put("semester", semester.getSelectedItem().toString());
                        //Get Core Subject from Form
                        int checked_subjects = 0;
                        Map<String, String> core_subjects = new HashMap<String, String>();
                        for(int i=0 ; i<core_subject_size; ++i){
                             CheckBox c = root.findViewById(core_subject_id[i]);
                             if(c.isChecked()){
                                 core_subjects.put(String.valueOf(checked_subjects), c.getText().toString());
                                 checked_subjects++;
                             }
                        }
                        if(checked_subjects < 5){
                            message("Please Select At least 5 Core Subject");
                            return;
                        }
                        form.put("core_subjects", core_subjects);
                        //Get Elected Subject from the form
                        boolean elected_subject = false;
                        for(int i=0; i<elective_subject_size; ++i){
                            RadioButton r = root.findViewById(elective_subject_id[i]);
                            int selectedId = elective_subjects.getCheckedRadioButtonId();
                            if(selectedId == elective_subject_id[i]){
                                elected_subject = true;
                                form.put("elective_subject", r.getText().toString());
                                break;
                            }
                        }
                        if(elected_subject == false){
                            message("One Elective Subject is Mandatory!");
                            return;
                        }

                        firebaseFirestore.collection("pendingSemesterForm").document(formId)
                                .update(form)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            message("Submitted Form Successfully");

                                        } else {
                                            message("Some Error Occured. Please try Again Later");
                                        }
                                    }
                                });
                    }
                }
            }
        });
        return root;
    }

    private void setViews(View v){
        bank = v.findViewById(R.id.banks_array);
        payment = v.findViewById(R.id.payment_mode);
        backlog_group = v.findViewById(R.id.backlog_group);
        acknowledgement = v.findViewById(R.id.acknowledgemwnt);
        unique_no = v.findViewById(R.id.payment_id);
        submit = v.findViewById(R.id.submit_form);
        semester = v.findViewById(R.id.semester_array);
        core_subjects = v.findViewById(R.id.core_subjects);
        elective_subjects = v.findViewById(R.id.elective_subject);
    }
    private void message(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}

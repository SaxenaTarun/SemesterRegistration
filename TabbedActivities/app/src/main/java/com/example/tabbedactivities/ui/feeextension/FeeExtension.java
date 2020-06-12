package com.example.tabbedactivities.ui.feeextension;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tabbedactivities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FeeExtension extends Fragment {

    private static final String TAG = "FEE EXTENSION";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    Spinner reason;
    EditText text_reason;
    TextView first_message;
    Button request;
    String extensionFormId;
    private Map<String, Object> extensionForm  = new HashMap<String, Object>();
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fee_extension, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        initializeViews(root);

        //Search for existing Extension Form of current User if there
        firebaseFirestore.collection("pendingExtensionForm")
                .whereEqualTo("userId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                final String docId = firebaseFirestore.collection("pendingExtensionForm").document().getId();
                                DocumentReference documentReference = firebaseFirestore.collection("pendingExtensionForm").document(docId);
                                Map<String, Object> form = new HashMap<String, Object>();
                                form.put("approved", false);
                                form.put("rejected", false);
                                form.put("userId", firebaseAuth.getCurrentUser().getUid());
                                documentReference.set(form).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            extensionFormId = docId;
                                        } else {
                                            Toast.makeText(getActivity(), "Some Error Occured Try Again Later", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    extensionFormId = document.getId();
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                                DocumentSnapshot doc = task.getResult();
                                String msg = "Respected Sir, \n I "+ doc.get("name").toString() + " enrolled in Institute with my Enrollment no. "
                                        + doc.get("enrollment_no").toString() + " requests for bank extension due to ,";
                                extensionForm.put("name", doc.get("name").toString());
                                extensionForm.put("enrollment_no", doc.get("enrollment_no").toString());
                                extensionForm.put("userId", firebaseAuth.getCurrentUser().getUid());
                                first_message.setText(msg);
                        } else {
                            Toast.makeText(getActivity(), "Error Fetching Data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extensionForm.put("reason", reason.getSelectedItem().toString());
                if(text_reason.getText().toString().length() < 20){
                    Toast.makeText(getActivity(), "Make sure to state reason properly.", Toast.LENGTH_SHORT).show();
                    return;
                }
                extensionForm.put("text_reason", text_reason.getText().toString());
                firebaseFirestore.collection("pendingExtensionForm").document(extensionFormId)
                        .update(extensionForm)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "Successfully Submitted Your Form.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Some Error in Submitting Your Form. Try Again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return root;
    }

    private void initializeViews(View v){
        reason = v.findViewById(R.id.reason);
        text_reason = v.findViewById(R.id.text_reason);
        first_message = v.findViewById(R.id.first_message);
        request = v.findViewById(R.id.request_extension);
    }
}

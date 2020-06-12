package com.example.tabbedactivities.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tabbedactivities.PopUpClass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tabbedactivities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static androidx.core.content.ContextCompat.getSystemService;

public class HomeFragment extends Fragment  {

    private static final String TAG = "HOME FRAGMENT";
    private HomeViewModel homeViewModel;
    private Context mContext;
    private Button view_Course;
    private CheckBox studentSubmitted, approvedByFac;
    private TextView infoView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        mContext = getActivity();
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initializeView(root);

        final TextView textView = root.findViewById(R.id.text_home);

        final LinearLayout current_status = root.findViewById(R.id.current_status);

        firebaseFirestore.collection("pendingSemesterForm").whereEqualTo("userId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot query  = task.getResult();
                            if(query.getDocuments().size() > 0){
                                studentSubmitted.setChecked(true);
                            }
                            for(QueryDocumentSnapshot doc : query){
                                Log.d(TAG, doc.toString());
                                if((Boolean) doc.getData().get("approved") ==  true){
                                    approvedByFac.setChecked(true);
                                    infoView.setText("Form is Approved by Your Faculty Advisor. You are Enrolled Successfully. Keep your Form Id for Future Reference :\n" + doc.getId().toString());
                                } else {
                                    infoView.setText("Form is Submitted by You. Not yet Approved by Your Faculty Advisor");

                                }
                            }
                        }
                    }
                });

        view_Course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "View Courses Now", Toast.LENGTH_SHORT).show();
                PopUpClass p = new PopUpClass();
                p.showPopupWindow(v);
            }
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
    private void initializeView(View v){
        studentSubmitted = v.findViewById(R.id.studentSubmittedForm);
        approvedByFac = v.findViewById(R.id.studentApprovedByFaculty);
        infoView = v.findViewById(R.id.informationText);
        view_Course = v.findViewById(R.id.view_course_current_status);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

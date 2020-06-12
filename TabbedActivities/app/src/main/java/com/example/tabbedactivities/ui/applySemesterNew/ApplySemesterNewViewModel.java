package com.example.tabbedactivities.ui.applySemesterNew;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ApplySemesterNewViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    public ApplySemesterNewViewModel() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mText = new MutableLiveData<>();

        mText.setValue("FormId\n");

    }

    public LiveData<String> getText() {
        return mText;
    }
}
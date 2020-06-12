package com.example.tabbedactivities;

import android.content.Context;
import android.view.View;
import android.view.*;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ApplicantListAdapter extends ArrayAdapter<String[]> {
    private Context mContext;
    int mResource;

    public ApplicantListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String[]> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position)[0];
        String enrollment_no = getItem(position)[1];
        String initials = getItem(position)[2];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView mName = convertView.findViewById(R.id.studentName);
        TextView mEnrollment = convertView.findViewById(R.id.studentEnrollmentNo);
        TextView mInitials = convertView.findViewById(R.id.studentInitials);
        mName.setText(name);
        mEnrollment.setText(enrollment_no);
        mInitials.setText(initials);

        return convertView;
    }
}

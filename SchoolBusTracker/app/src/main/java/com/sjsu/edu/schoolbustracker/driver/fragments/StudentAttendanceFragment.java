package com.sjsu.edu.schoolbustracker.driver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjsu.edu.schoolbustracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentAttendanceFragment extends Fragment {

    private Toolbar mToolbar;
    public StudentAttendanceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_attendance, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.student_attendance_fragment);
        mToolbar.setTitle(R.string.title_child_attendance);
        mToolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(),R.color.black, null));
        return view;
    }

}

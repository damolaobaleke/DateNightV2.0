package com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.dates;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.dates.pending.PendingFragment;
import com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.dates.scheduled.ScheduledFragment;
import com.immersia_ltd_datenight.views.datehub_navigation.ui_tab_experience.PageAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.immersia_ltd_datenight.utils.DownloadImageTask;
import com.immersia_ltd_datenight.views.datehub_navigation.ExperienceType;
import com.immersia_ltd_datenight.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFragment extends Fragment {
    FloatingActionButton createDate, joinDate;
    FirebaseAuth mAuth;
    ViewPager viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dates, container, false);

        createDate = view.findViewById(R.id.fabCreateDate);

        ViewPager viewPager = view.findViewById(R.id.pager_dates);

        PageAdapter pageAdapter = new PageAdapter(getChildFragmentManager(),1);
        pageAdapter.addFragment("Pending", new PendingFragment());
        pageAdapter.addFragment("Scheduled", new ScheduledFragment());

        viewPager.setAdapter(pageAdapter);

        TabLayout tabs = view.findViewById(R.id.date_tabs);
        tabs.setupWithViewPager(viewPager);

        openExperience();

        return view;
    }

    public void openExperience() {
        createDate.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ExperienceType.class);
            startActivity(intent);
        });
    }

    public String dateTime() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time is => " + c);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm a", Locale.ENGLISH);
        String formattedTime = df.format(c);
        System.out.println(formattedTime);

        return formattedTime;
    }

    public String dateDay() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time is => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd -MMM", Locale.ENGLISH);
        String formattedDate = df.format(c);
        System.out.println(formattedDate);

        return formattedDate;
    }

    private Bitmap getImageBitmap() {
        ArrayList<Bitmap> avatarBitmap = null;
        try {
            DownloadImageTask task = new DownloadImageTask();

            //execute() method calls the do in background method
            avatarBitmap = task.execute("https://res.cloudinary.com/dayvbcxai/image/upload/v1597180001/DateNight/DateNight-Logo-Icon-Transparetn_ms1kfn.png").get();

            DownloadImageTask task2 = new DownloadImageTask();

        } catch (Exception e) {
            Log.i("getting Images", "Error \n" + e);
        }

        assert avatarBitmap != null;
        return avatarBitmap.get(0); //gets image in position 0 based on url }
    }

}

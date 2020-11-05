package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.pending;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.model.DateDataModel;
import com.datenight_immersia_ltd.utils.DownloadImageTask;
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.CustomDatesAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PendingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_pending, container, false);

        ListView lv = view.findViewById(R.id.dateList);

        ArrayList<String> dates = new ArrayList<>();
        dates.add(0, "Date 1");
        dates.add(1, "Date 2");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext().getApplicationContext(), android.R.layout.simple_list_item_1, dates);
        lv.setAdapter(arrayAdapter);

        //==============================================================//
        List<DateDataModel> listOfDates = new ArrayList<>();

//        try {
//            //getdateTitle() from FBdatabase snapshot
//            listOfDates.add(new DateDataModel("Paris date night with sharon", getImageBitmap(), dateDay(), dateTime()));
//            listOfDates.add(new DateDataModel("Paris date night with Emeka", getImageBitmap(), dateDay(), dateTime()));
//
//
//            CustomDatesAdapter customDatesAdapter = new CustomDatesAdapter(requireContext(), R.layout.custom_dates_list_view, listOfDates);
//            lv.setAdapter(customDatesAdapter);
//        } catch (Exception e) {
//            //When trying to get Image from Server if no internet
//            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//        }


        return view;
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

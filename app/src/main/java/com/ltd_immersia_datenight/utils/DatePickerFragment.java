package com.ltd_immersia_datenight.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;


/*Already in native Android Library*/
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    Calendar c;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        System.out.print(view.getDayOfMonth() + view.getMonth() + view.getYear());

        //ageInput.setText(String.format(Locale.US, "%d-%d-%d", day, month, year));
        Log.i("Current Date", c.toString());

    }

    public static String dateformatter(DatePicker view){
        if (view == null) return "";
        return String.format(Locale.US, "%02d-%02d-%d.", view.getDayOfMonth(), view.getMonth(), view.getYear());
    }
}

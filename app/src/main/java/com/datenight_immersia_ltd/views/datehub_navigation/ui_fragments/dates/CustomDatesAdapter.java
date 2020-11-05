package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.model.DateDataModel;

import java.util.List;

public class CustomDatesAdapter extends ArrayAdapter {

    private int layoutResource;
    private Context context;
    private List<DateDataModel> itemsInList;
    ImageView avatar;

    public CustomDatesAdapter(@NonNull Context context, int layoutResource, List<DateDataModel> itemsInList) {
        super(context, layoutResource, itemsInList);
        this.layoutResource = layoutResource;
        this.context = context;
        this.itemsInList = itemsInList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutResource, null, false);
        TextView dateTitle = view.findViewById(R.id.dateTitle);
        TextView dateDate = view.findViewById(R.id.dateDate);
        TextView dateTime = view.findViewById(R.id.dateTime);
        avatar = view.findViewById(R.id.dateImage);


        //get data model specific position
        DateDataModel model = itemsInList.get(position);

        //add values to list item
        dateTitle.setText(model.getDateTitle());
        dateDate.setText(model.getDateDate());
        avatar.setImageBitmap(model.getDateImage());
        dateTime.setText(model.getDateTime());

        return view;

    }

}

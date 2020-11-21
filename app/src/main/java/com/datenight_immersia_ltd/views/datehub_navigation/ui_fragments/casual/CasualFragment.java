package com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.casual;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.views.date_schedule.DateScheduleActivity;
import com.datenight_immersia_ltd.views.unity.UnityEnvironmentLoad;

public class CasualFragment extends Fragment {

    private CasualViewModel galleryViewModel;
    CardView paris, morocco, dubai;
    Button createDate, joinDate;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_casual, container, false);
        paris = view.findViewById(R.id.paris);

        paris.setOnClickListener(v->{
           startParis();
        });


        return view;
    }

    public void startParis(){
        Intent intent = new Intent(getContext(), DateScheduleActivity.class);
        startActivity(intent);
    }

    public void showProgress(){

    }

//    public void parisAmbience(){
//        paris.setOnClickListener(v->{
//            boolean logo = false;
//            if(logo) {
//                paris.setBackground(getActivity().getDrawable(R.drawable.ic_menu_camera));
//                logo = true;
//            }else{
//                logo = true;
//                paris.setBackground(getActivity().getDrawable(R.drawable.datenight_logo));
//                logo =false;
//            }
//
//
//        });
//    }

//    public void moroccoAmbience(){
//
//    }


}
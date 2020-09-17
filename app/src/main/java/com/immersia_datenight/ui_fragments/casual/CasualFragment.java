package com.immersia_datenight.ui_fragments.casual;

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

import com.immersia_datenight.CreateDate;
import com.immersia_datenight.DateHubNavigation;
import com.immersia_datenight.JoinDate;
import com.immersia_datenight.R;
import com.immersia_datenight.views.unity.UnityEnvironmentLoad;

public class CasualFragment extends Fragment {

    private CasualViewModel galleryViewModel;
    CardView paris, morocco, dubai;
    Button createDate, joinDate;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_casual, container, false);
        paris = view.findViewById(R.id.paris);

        paris.setOnClickListener(v->{
            Toast.makeText(getContext(),"Paris", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), UnityEnvironmentLoad.class);
            startActivity(intent);
        });


        return view;
    }

    public void createDate() {
        createDate.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateDate.class);
            startActivity(intent);
        });
    }

    public void joinDate() {
        joinDate.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), JoinDate.class);
            startActivity(intent);
        });
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
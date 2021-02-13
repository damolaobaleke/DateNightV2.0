/*
 * Copyright 2021 Damola Obaleke. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.immersia_ltd_datenight.views.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.immersia_ltd_datenight.utils.constants.DatabaseConstants;
import com.immersia_ltd_datenight.R;
import com.immersia_ltd_datenight.modelfirestore.User.UserModel;
import com.immersia_ltd_datenight.utils.stripe.config.DateNight;
import com.immersia_ltd_datenight.views.datehub_navigation.DateHubNavigation;

public class UserOnBoarding extends AppCompatActivity {

    static int NO_FRAGS_ONBOARDING = 3;
    ScreenSlidePageAdapter pagerAdapter;
    FirebaseAuth mAuth;
    CollectionReference userCollRef;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_on_boarding);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        /** Note: Works fine, commented out inner UpdateUI method due to commenting out checkIsBoarded method in MainActivity has to flow*/
        //if there's no current user do check based on all users.
        userCollRef = db.collection(DatabaseConstants.USER_DATA_NODE);
        userCollRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    UserModel user = document.toObject(UserModel.class);

                    if (user.isOnBoarded() && mAuth.getCurrentUser() != null) {
                        UpdateUI(mAuth.getCurrentUser());
                    }else if(user.isOnBoarded() && mAuth.getCurrentUser() == null){
                        //boarding screen
                    }
                }
            }
        });


        ViewPager viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

    }

    public void UpdateUI(FirebaseUser user) {
        if (user != null) {

            DateNight appState = ((DateNight)this.getApplication());
            if (appState.getAppData(mAuth.getUid()) == null) {
                // Fetch required launch data and then launch DateHubNavigation class
                appState.initializeAppData(mAuth.getUid(), this);

            } else {
                Intent intent = new Intent(this, DateHubNavigation.class);
                startActivity(intent);
            }
        }
    }

    private static class ScreenSlidePageAdapter extends FragmentStatePagerAdapter {


        public ScreenSlidePageAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new OnBoardingFrag1();
                case 1:
                    return new OnBoardingFrag2();
                case 2:
                    return new OnBoardingFrag3();
                default:
                    return new OnBoardingFrag1(); //last page
            }
        }

        @Override
        public int getCount() {
            return NO_FRAGS_ONBOARDING;
        }
    }
}
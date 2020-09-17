package com.immersia_datenight.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PageAdapter extends FragmentPagerAdapter {
    ArrayList<String> fragmentTitleList ;
    ArrayList<Fragment> fragmentList ;


    public PageAdapter(FragmentManager fm, int behavior) {
        super(fm, behavior);

        fragmentList = new ArrayList<>();
        fragmentTitleList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(String title, Fragment fm){
        fragmentTitleList.add(title);
        fragmentList.add(fm);
    }

    //androidX Precaution==

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}

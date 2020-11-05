package com.datenight_immersia_ltd.views.datehub_navigation;

import android.os.Bundle;

import com.datenight_immersia_ltd.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.datenight_immersia_ltd.views.datehub_navigation.ui_tab_experience.PageAdapter;
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.casual.CasualFragment;
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.premium.PremiumFragment;

public class ExperienceType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_type);

        //SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), 1);
        pageAdapter.addFragment("Casual", new CasualFragment());
        pageAdapter.addFragment("Premium", new PremiumFragment());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pageAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }
}
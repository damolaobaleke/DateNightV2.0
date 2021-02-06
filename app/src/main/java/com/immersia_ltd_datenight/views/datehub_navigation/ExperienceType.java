package com.immersia_ltd_datenight.views.datehub_navigation;

import android.os.Bundle;

import com.immersia_ltd_datenight.R;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.immersia_ltd_datenight.views.datehub_navigation.ui_tab_experience.PageAdapter;
import com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.casual.CasualFragment;
import com.immersia_ltd_datenight.views.datehub_navigation.ui_fragments.premium.PremiumFragment;

public class ExperienceType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_type);
        setSupportActionBar(findViewById(R.id.experienceTypeFragmentToolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), 1);
        pageAdapter.addFragment("Casual", new CasualFragment());
        pageAdapter.addFragment("Premium", new PremiumFragment());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pageAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
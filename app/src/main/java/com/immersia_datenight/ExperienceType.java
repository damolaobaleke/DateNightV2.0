package com.immersia_datenight;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.immersia_datenight.ui.main.PageAdapter;
import com.immersia_datenight.ui.main.SectionsPagerAdapter;
import com.immersia_datenight.ui_fragments.casual.CasualFragment;
import com.immersia_datenight.ui_fragments.premium.PremiumFragment;

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
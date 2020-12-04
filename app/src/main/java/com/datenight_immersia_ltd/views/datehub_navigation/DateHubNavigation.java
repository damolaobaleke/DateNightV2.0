package com.datenight_immersia_ltd.views.datehub_navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.datenight_immersia_ltd.R;
import com.datenight_immersia_ltd.views.authentication.LoginActivity;
import com.datenight_immersia_ltd.views.datehub_navigation.ui_fragments.dates.pending.PendingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DateHubNavigation extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    FirebaseAuth mAuth;
    TextView emailDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datehub_navigation);
        setTitle("Datehub");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        emailDisplay = findViewById(R.id.emailDisplay);
        mAuth = FirebaseAuth.getInstance();
        //emailDisplay.setText(email.getText().toString());

        //Nav Views --Side and Bottom
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_accounts, R.id.nav_buy_dtc, R.id.nav_help, R.id.nav_my_dates, R.id.nav_date_hub, R.id.nav_inbox)
                .setOpenableLayout(drawer) //Drawer layout implements an openable interface
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //bottom nav view set up with navcontroller
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


//    @Override
//    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();   //closes activity as usual
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_date, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                signOut();
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        updateUI(null);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            Log.i("Name", name);

            Intent intent = new Intent(this, DateHubNavigation.class);
            startActivity(intent);

            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_my_dates:
                Fragment pendingFragment = new PendingFragment();
                Bundle arguments = new Bundle();

                arguments.putString("dateId", getIntent().getStringExtra("dateID"));
                pendingFragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, pendingFragment).commit();
                break;
        }

    }
}
package com.ltd_immersia_datenight.views.datehub_navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ltd_immersia_datenight.R;
import com.ltd_immersia_datenight.modelfirestore.User.UserModel;
import com.ltd_immersia_datenight.utils.constants.DatabaseConstants;
import com.ltd_immersia_datenight.utils.constants.IntentConstants;
import com.ltd_immersia_datenight.utils.DateNight;
import com.ltd_immersia_datenight.views.landing_screen.BoardingScreen;

public class DateHubNavigation extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    FirebaseAuth mAuth;
    TextView emailDisplay;
    DocumentReference userdocRef;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datehub_navigation);
        setTitle("Datehub");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mAuth =FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        //Nav Views --Side and Bottom
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        //username display
        View header = navigationView.getHeaderView(0);
        emailDisplay = header.findViewById(R.id.emailDisplay);

        mAuth = FirebaseAuth.getInstance();
        userdocRef = db.collection(DatabaseConstants.USER_DATA_NODE).document(mAuth.getCurrentUser().getUid());
        userdocRef.get().addOnSuccessListener(documentSnapshot -> {
            UserModel user = documentSnapshot.toObject(UserModel.class);
            if(user != null) {
                emailDisplay.setText(user.getUsername());
            }
        });

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

        //Navigate to specific fragment
        String fragmentToLaunch = getIntent().getStringExtra(IntentConstants.FRAGMENT_TO_LOAD);
        if (fragmentToLaunch != null){
            switch (fragmentToLaunch){
                case IntentConstants.DATE_HUB_FRAGMENT:
                    navController.navigate(R.id.action_nav_my_dates_to_nav_date_hub);
                    break;

                case IntentConstants.INBOX_FRAGMENT:
                    navController.navigate(R.id.action_nav_my_dates_to_nav_inbox);
                    break;
                case IntentConstants.BUY_DTC_FRAGMENT:
                    navController.navigate(R.id.action_nav_hub_to_nav_dtc);
                    break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_date, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void signOut(MenuItem item) {
        DateNight appState = ((DateNight)this.getApplication());
        appState.clearAppData();

        mAuth.signOut();
        updateUI(null);
        Intent intent = new Intent(this, BoardingScreen.class);
        startActivity(intent);
        finish();
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            Log.i("Name",name);

            Intent intent = new Intent(this, DateHubNavigation.class);
            startActivity(intent);

            Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
        }
    }

}
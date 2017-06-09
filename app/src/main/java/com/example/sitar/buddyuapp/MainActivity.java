package com.example.sitar.buddyuapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements HomeFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        CalendarFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        YourBuddiesFragment.OnFragmentInteractionListener,
        ChatFragment.OnFragmentInteractionListener,
        ChatGroupsFragment.OnFragmentInteractionListener,
        ClassesFragment.OnFragmentInteractionListener,
        AboutBuddyU.OnFragmentInteractionListener,
        TermsAndConditions.OnFragmentInteractionListener,
        BuddyRequests.OnFragmentInteractionListener,
        ReportProblem.OnFragmentInteractionListener,
        ScheduleFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth=FirebaseAuth.getInstance();
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // This will be the default fragment loaded into the main activity
        getFragmentManager().beginTransaction().replace(R.id.content_main, new HomeFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       // if (id == R.id.action_settings) {
          //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new HomeFragment()).commit();
        }
/*
        else if (id == R.id.nav_chat)
        {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new ChatGroupsFragment()).commit();
        }
        */
        else if(id==R.id.nav_classes)
        {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new ClassesFragment()).commit();
        }
        else if (id == R.id.nav_schedule)
        {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new ScheduleFragment()).commit();

        } else if (id == R.id.nav_profile)
        {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new ProfileFragment()).commit();
        }
        /*
        else if(id==R.id.nav_settings)
        {
            getFragmentManager().beginTransaction().replace(R.id.content_main, new SettingsFragment()).commit();
        }
        */
        else if(id==R.id.nav_logout)
        {
            finish();
            firebaseAuth.signOut();
            Intent intent = new Intent(this,com.example.sitar.buddyuapp.registration.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // you can leave it empty
    }
}

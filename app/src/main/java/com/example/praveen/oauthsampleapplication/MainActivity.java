package com.example.praveen.oauthsampleapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.praveen.oauthsampleapplication.utils.AppCommon;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WebViewFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_facebook) {
            WebViewFragment fragment = WebViewFragment.newInstance(getAuthenticationUrl(),"");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.containerFrame,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_linkedIn) {

        } else if (id == R.id.nav_twitter) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public  String getAuthenticationUrl() {
         String authRequestRedirect =
                 AppCommon.FB_APP_OAUTH_BASEURL+AppCommon.FB_APP_OAUTH_URL
                + "?client_id="+AppCommon.FB_APP_ID
                + "&response_type=code"
                + "&display=touch"
                + "&scope=public_profile"
                + "&redirect_uri="+AppCommon.FB_APP_REDIRECT_URL
                + "&"
                +AppCommon.STATE_PARAM + "="
                +AppCommon.STATE
        ;
        return authRequestRedirect;
    }

    @Override
    public void onFragmentInteraction(String accessToken) {
            ProfileFragment fragment = ProfileFragment.newInstance(getFbProfileUrl(accessToken),"");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.containerFrame,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
    }

    private String getFbProfileUrl(String accessToken) {
        String profileUrl = AppCommon.FB_ACCESS_TOKEN_URL
                + "/me"
                + AppCommon.QUESTION_MARK
                + "fields="
                + "name,email,birthday,cover"
                + AppCommon.AMPERSAND_PARAM
                + "access_token="
                + accessToken
                ;
        return profileUrl;
    }
}

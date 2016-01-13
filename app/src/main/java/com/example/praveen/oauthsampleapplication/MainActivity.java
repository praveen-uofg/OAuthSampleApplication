package com.example.praveen.oauthsampleapplication;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.praveen.oauthsampleapplication.utils.AppCommon;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WebViewFragment.OnFragmentInteractionListener,
        ProfileFragment.OnProfileFragmentInteractionListener{

    private ImageButton mFBButton;
    private ImageButton mLinkedInButton;
    private LinearLayout mMainFrame;
    private FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();
    }

    private void initView() {
        mFBButton = (ImageButton)findViewById(R.id.fbButton);
        mLinkedInButton = (ImageButton)findViewById(R.id.linkedinButton);
        mMainFrame = (LinearLayout) findViewById(R.id.mainFrame);
        mContainer = (FrameLayout)findViewById(R.id.containerFrame);
        setClickListeners();
    }


    private void setClickListeners() {
        mFBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileFragment(AppCommon.FB_NETWORK);
            }
        });
        mLinkedInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfileFragment(AppCommon.LINKEDIN_NETWORK);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
                mContainer.setVisibility(View.GONE);
                mMainFrame.setVisibility(View.VISIBLE);
                super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_facebook) {
            startProfileFragment(AppCommon.FB_NETWORK);
        } else if (id == R.id.nav_linkedIn) {
            startProfileFragment(AppCommon.LINKEDIN_NETWORK);
        } else if (id == R.id.nav_twitter) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startProfileFragment(String network) {
        mMainFrame.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
        ProfileFragment fragment = ProfileFragment.newInstance(null,network);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerFrame,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void invokeWebview(String network) {
        mMainFrame.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
        WebViewFragment fragment = WebViewFragment.newInstance(network);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.containerFrame,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(String accessToken, String network) {
            ProfileFragment fragment = ProfileFragment.newInstance(accessToken,network);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.containerFrame,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
    }

    @Override
    public void onConnectButtonPressed(String network) {
        invokeWebview(network);
    }
}

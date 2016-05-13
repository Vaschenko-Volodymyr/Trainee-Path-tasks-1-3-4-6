/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.traineepath.volodymyrvashchenko.traineepath.application.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.traineepath.volodymyrvashchenko.traineepath.R;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.channelsdata.ChannelsData;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.packetdata.PacketData;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.state.IsLogged;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.state.IsLoggedByPacket;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.state.SavedState;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.userdata.UserData;
import com.traineepath.volodymyrvashchenko.traineepath.application.exceptions.InvalidDataException;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments.CabinetFragment;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments.ChannelsFragment;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments.RetrievedDataFragment;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.NetworkState;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.SearchToolbarUi;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a main screen of an application.
 * Will be shown if user complete successfully log in procedure or
 * was logged before.
 */
public class MainScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Trainee Path App. ";

    public static Context sApplicationContext;
    public static MainScreenActivity sInstance;
    public static NavigationView sNavigationView;
    public static SweetAlertDialog sDialog;

    public static Toolbar sToolbar;
    public static EditText sSearch;
    public static ImageView sSearchImage;
    public static TextView sToolbarText;

    public static TextView sEmail;
    public static TextView sName;
    public static TextView sBalance;
    public static TextView sBonus;

    public static String sPacketId;
    public static String sPacketTitle;

    public static boolean sJustEntered = false;

    private static final int CHANNELS_ID = 0;
    private static final int CABINET_ID = 1;
    private static final int RETRIEVED_ID = 2;

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private NetworkState mState;
    private String mUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        sInstance = this;
        sApplicationContext = getApplicationContext();

        // Toolbar widgets
        sToolbar = (Toolbar) findViewById(R.id.app_bar_toolbar);
        sToolbarText = (TextView) findViewById(R.id.toolbar_text);
        sSearch = (EditText) findViewById(R.id.toolbar_search);
        sSearchImage = (ImageView) findViewById(R.id.toolbar_search_img);

        // Drawer widgets
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, sToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        sNavigationView = (NavigationView) findViewById(R.id.nav_view);

        //Navigation header widgets
        View header = sNavigationView.getHeaderView(0);
        sEmail = (TextView) header.findViewById(R.id.nav_header_user_email);
        sEmail.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        sName = (TextView) header.findViewById(R.id.nav_header_user_name);
        sBalance = (TextView) header.findViewById(R.id.nav_header_user_balance);
        sBonus = (TextView) header.findViewById(R.id.nav_header_user_bonus);

        // Screen widgets
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Handling widgets
        setSupportActionBar(sToolbar);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        sNavigationView.setNavigationItemSelectedListener(this);

        // Other inits and actions
        UserData.getInstance().initUserData(this);
        PacketData.getInstance().initPacketData(this, getResources());
        ChannelsData.getInstance().initChannelsData(this, getResources());
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        sDialog = new SweetAlertDialog(MainScreenActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        mState = new NetworkState(this);

        // Displaying progress dialog
        sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sDialog.setTitleText("Loading");

        //Retrieve user email from login activity by Intent instead of singleton and DB
        Intent intent = getIntent();
        mUserEmail = intent.getStringExtra(LoginActivity.USER_EMAIL);
    }

    public void onResume() {
        super.onResume();
        if (mState.networkIsAvailable()) {
            UserData.getInstance().initLoggedState();
            UserData.getInstance().loadUserData();
            if (SavedState.getInstance().isFirstTimeEntered()) {
                if (IsLogged.getInstance().isLogged()) {
                    Log.w(TAG, "Inside on Resume: Brunch " + IsLogged.getInstance().isLogged());
                    UserData.getInstance().initLoginTokens();
                    UserData.getInstance().initUserInfo();
                    PacketData.getInstance().initLoggedByPacketState();
                    if (IsLoggedByPacket.getInstance().isLogged()) {
                        PacketData.getInstance().initPacketTokens();
                        try {
                            sPacketId = PacketData.getInstance().getPacketId();
                            sNavigationView.getMenu().getItem(0).setChecked(true);
                            onNavigationItemSelected(sNavigationView.getMenu().getItem(0));
                        } catch (InvalidDataException e) {
                            sNavigationView.getMenu().getItem(1).setChecked(true);
                            onNavigationItemSelected(sNavigationView.getMenu().getItem(1));
                        }
                    } else {
                        sNavigationView.getMenu().getItem(1).setChecked(true);
                        onNavigationItemSelected(sNavigationView.getMenu().getItem(1));
                        Log.w(TAG, "IS LOGGED BY PACKET: " + String.valueOf(IsLoggedByPacket.getInstance().isLogged()));
                    }
                } else {
                    Log.w(TAG, "Inside on Resume: Brunch " + IsLogged.getInstance().isLogged());
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
            } else {
                setupViewPager(mViewPager, SavedState.getInstance().getParagraph());
            }

        } else {
            showNetworkStateError();
        }
    }

    // Inner class to work with ViewPager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void clearAll() {
            FragmentManager fragMan = getSupportFragmentManager();
            for (int i = 0; i < mFragmentList.size(); i++)
                fragMan.beginTransaction().remove(mFragmentList.get(i)).commit();
            mFragmentList.clear();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (ChannelsFragment.sSearchIsVisible) {
            ChannelsFragment.sSearchIsVisible = SearchToolbarUi.changeSearchToolbarUI(this, ChannelsFragment.sSearchIsVisible);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_current_packet) {
            if (sPacketTitle == null) {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.toolbar_text_shacking);
                sToolbarText.setText("Выберите пакет");
                sToolbarText.startAnimation(animation);
                mAdapter.clearAll();
                setupViewPager(mViewPager, CABINET_ID);
            } else {
                sToolbarText.setText("Пакет : " + sPacketTitle);
                mAdapter.clearAll();
                setupViewPager(mViewPager, CHANNELS_ID);
            }
        } else if (id == R.id.nav_cabinet) {
            sToolbarText.setText("Кабинет");
            mAdapter.clearAll();
            setupViewPager(mViewPager, CABINET_ID);
        } else if (id == R.id.nav_retrieved_data) {
            sToolbarText.setText("Задание номер 3");
            mAdapter.clearAll();
            setupViewPager(mViewPager, RETRIEVED_ID);
        } else if (id == R.id.nav_quit) {
            UserData.getInstance().resetUserData();
            PacketData.getInstance().resetPacketData();
            ChannelsData.getInstance().resetChannelsData();
            Intent toLoginScreen = new Intent(this, LoginActivity.class);
            startActivity(toLoginScreen);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager, int id) {
        SavedState.getInstance().setParagraph(id);
        mAdapter.clearAll();
        viewPager.setAdapter(null);
        Fragment fragment;
        switch (id) {
            case 0:
                Bundle bundle = new Bundle();
                bundle.putString("packetId", sPacketId);
                fragment = new ChannelsFragment();
                fragment.setArguments(bundle);
                break;
            case 1:
                fragment = new CabinetFragment();
                break;
            case 2:
                fragment = new RetrievedDataFragment();
                Bundle passingUserEmail = new Bundle();
                passingUserEmail.putString(LoginActivity.USER_EMAIL, mUserEmail);
                fragment.setArguments(passingUserEmail);
                break;
            default:
                fragment = new CabinetFragment();
                break;
        }
        mAdapter.addFragment(fragment, "FRAGMENT");
        viewPager.setAdapter(mAdapter);
    }

    private void showNetworkStateError() {
        new SweetAlertDialog(MainScreenActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Невозможно")
                .setContentText("Необходимо подключение к интернету")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .show();

    }

    public static Context getContext() {
        return sApplicationContext;
    }

    public static String AppTag() {
        return TAG;
    }

}
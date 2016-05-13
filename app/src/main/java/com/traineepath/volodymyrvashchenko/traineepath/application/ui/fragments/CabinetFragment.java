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

package com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.activities.MainScreenActivity;
import com.traineepath.volodymyrvashchenko.traineepath.R;
import com.traineepath.volodymyrvashchenko.traineepath.application.API;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.db.DBHelper;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.packetdata.PacketData;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.state.IsLogged;
import com.traineepath.volodymyrvashchenko.traineepath.application.http.RequestWithLoginToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.adaptors.PacketAdaptor;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.models.PacketListModel;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.SearchToolbarUi;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a Cabinet fragment. Displays all packets that user had bought
 * Names like CabinetFragment instead of PacketFragment because originally
 * on site this part is called "Cabinet"
 */
public class CabinetFragment extends Fragment {

    private static final String TAG = CabinetFragment.class.getSimpleName();

    public static Activity sPacketListViewActivity = null;
    public static ArrayList<PacketListModel> sPacketListArray;
    public static PacketAdaptor sAdapter;
    public static ListView sPackets;
    public static PullToRefreshView sCabinetPullToRefreshView;
    public static CabinetFragment sInstance;

    private RequestWithLoginToken mRequest;
    private String mPacketPassword;
    private String mPacketId;

    public CabinetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        sPacketListViewActivity = getActivity();
        sAdapter = new PacketAdaptor(this, sPacketListArray, getResources());
        sAdapter = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cabinet_content, container, false);

        SearchToolbarUi.resetToolbar(getActivity());

        mRequest = new RequestWithLoginToken();

        sPacketListArray = new ArrayList<>();
        sPackets = (ListView) v.findViewById(R.id.packets);
        sCabinetPullToRefreshView = (PullToRefreshView) v.findViewById(R.id.cabinet_refresh_layout);

        sCabinetPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sCabinetPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PacketData.getInstance().resetPacketData();
                        requestPacketData();
                        Log.d(TAG, "End of refreshing");
                    }
                }, 1000);
            }
        });

        if ((!IsLogged.getInstance().isLogged() || MainScreenActivity.sJustEntered)) {
            MainScreenActivity.sJustEntered = false;
            MainScreenActivity.sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            MainScreenActivity.sDialog.setTitleText("Loading");
            MainScreenActivity.sDialog.show();
            PacketData.getInstance().resetPacketData();
            requestPacketData();
        } else {
            PacketData.getInstance().loadPacketsFromDB();
        }

        return v;
    }

    /**
     * This method is invoked by PacketAdaptor when user click on packet list item
     *
     * @param mPosition - id on element in list.
     */
    public void onPacketsItemClick(int mPosition) {
        PacketListModel tempValues = CabinetFragment.sPacketListArray.get(mPosition);
        String chosenPacketTitle = tempValues.getName();
        MainScreenActivity.sPacketId = mPacketId = PacketData.getInstance().getPacketData(
                chosenPacketTitle, DBHelper.PACKET_ID);
        mPacketPassword = PacketData.getInstance().getPacketData(
                chosenPacketTitle, DBHelper.PACKET_PASSWORD);
        MainScreenActivity.sPacketTitle = PacketData.getInstance().getPacketData(
                chosenPacketTitle, DBHelper.PACKET_NAME);
        loginInPacket();
    }

    private void requestPacketData() {
        sPacketListArray.clear();
        mRequest.get(RequestWithLoginToken.PACKETS_INFO_REQUEST);
    }

    private void loginInPacket() {
        final String contentType = "Content-Type";
        final String contentTypeParameter = "application/x-www-form-urlencoded";
        final String type = "grant_type";
        final String password = "password";
        final String keyUsername = "username";
        final String keyPassword = "password";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.LOG_IN_PACKET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, "loginInPacket(): Server is reachable. " + response);
                        if (response.contains("ERROR")) {
                            Log.e(TAG, "loginInPacket(): Access denied. See details: " + response);
                        } else if (response.contains("access_token")) {
                            Log.d(TAG, "loginInPacket(): Access accepted. See details: " + response);
                            setPacketTokensAndShowChannels(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(contentType, contentTypeParameter);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(type, password);
                params.put(keyUsername, mPacketId);
                params.put(keyPassword, mPacketPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void setPacketTokensAndShowChannels(String response) {
        PacketData.getInstance().setPacketTokens(response);
        PacketData.getInstance().setLoggedByPacket();
        MainScreenActivity.sNavigationView.getMenu().getItem(0).setChecked(true);
        MainScreenActivity.sInstance.onNavigationItemSelected(
                MainScreenActivity.sNavigationView.getMenu().getItem(0));
    }

    public void onPacketItemLongClick(int position) {
        PacketListModel values = CabinetFragment.sPacketListArray.get(position);
        Vibrator v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
        new SweetAlertDialog(MainScreenActivity.sInstance)
                .setTitleText(values.getName())
                .setContentText("Пакет доступен до\n" + values.getDate() + "\n\n"
                        + "Для доступа к пакету используйте \n"
                        + "Id пакета - " + values.getId() + "\n"
                        + "Пароль пакета - " + values.getPassword())
                .show();
    }
}
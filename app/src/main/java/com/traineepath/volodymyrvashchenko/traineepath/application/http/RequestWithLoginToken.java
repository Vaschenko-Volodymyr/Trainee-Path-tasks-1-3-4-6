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

package com.traineepath.volodymyrvashchenko.traineepath.application.http;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.activities.MainScreenActivity;
import com.traineepath.volodymyrvashchenko.traineepath.application.API;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.packetdata.PacketData;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.userdata.UserData;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.userdata.UserEmail;
import com.traineepath.volodymyrvashchenko.traineepath.application.exceptions.InvalidDataException;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments.CabinetFragment;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.Parser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Depends on what kind of a request is needed the request will be done.
 * To switch between requests use <em>public static final</em> constants.
 *
 * @see #USER_INFO_REQUEST
 * @see #PACKETS_INFO_REQUEST
 * @see #get(int)
 */
public class RequestWithLoginToken {

    public static final int USER_INFO_REQUEST = 1;
    public static final int PACKETS_INFO_REQUEST = 2;

    private static final String TAG = RequestWithLoginToken.class.getSimpleName();

    private int mRequestId;
    private int mTrials = 0;
    private RequestQueue mRequestQueue;

    public RequestWithLoginToken() {
        mRequestQueue = Volley.newRequestQueue(MainScreenActivity.sInstance);
    }

    /**
     * Do a GET request depends on passed parameter.
     *
     * @param id - indicates what exact request is needed.
     * @see #USER_INFO_REQUEST for user data request.
     * @see #PACKETS_INFO_REQUEST for packets info request.
     */
    public void get(int id) {
        Log.d(TAG + ", method - get", "");
        mRequestId = id;
        String url;
        switch (mRequestId) {
            case USER_INFO_REQUEST:
                url = API.USER_INFO;
                break;
            case PACKETS_INFO_REQUEST:
                url = API.PACKETS_SUMMARY;
                break;
            default:
                url = API.API;
                break;
        }

        final JsonObjectRequest req = new GetWithLoginToken(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG + ", method - get", "response = " + response);
                        try {
                            if (response.getString("status").equals("ERROR")) {
                                Log.e(TAG + ", method - get", "ERROR");
                                refreshTokens();
                            } else {
                                mTrials = 0;
                                switch (mRequestId) {
                                    case USER_INFO_REQUEST:
                                        fillUserInfo(response);
                                        break;
                                    case PACKETS_INFO_REQUEST:
                                        fillPacketInfo(response);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG + ", method - get", "fillPacketInfo exception, " + e);
                        } catch (InvalidDataException e) {
                            Log.e(TAG + ", method - get", "fillUserInfo exception, " + e);
                        }
                        Log.w(TAG + ", method - get", "Response user info : " + response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG + ", method - get", "Volley error, " + error);
                        VolleyLog.e("Authorization failed: ", error.getMessage());
                        MainScreenActivity.sDialog.dismiss();
                        refreshTokens();
                    }
                });
        mRequestQueue.add(req);
    }

    private void fillUserInfo(JSONObject response) throws InvalidDataException {
        Parser parser = new Parser();
        MainScreenActivity.sEmail.setText(UserEmail.getInstance().getUserEmail());
        String result;
        try {
            result = "{" + parser.parse(response.toString(), "results") + "}";
            UserData.getInstance().setUserData(result);
        } catch (NullPointerException e) {
            throw new InvalidDataException();
        }
    }

    private void fillPacketInfo(JSONObject response) throws JSONException {
        if (response.toString().contains("results")) {
            PacketData.getInstance().savePacketInfo(response);
            PacketData.getInstance().loadPacketsFromDB();
            CabinetFragment.sCabinetPullToRefreshView.setRefreshing(false);
            CabinetFragment.sCabinetPullToRefreshView.clearDisappearingChildren();
            MainScreenActivity.sDialog.dismiss();
        } else {
            Log.d(TAG, response.toString());
        }
    }

    private void refreshTokens() {
        mTrials++;
        if (mTrials < 3) {
            StringRequest stringRequest = new RefreshLoginTokenRequest(Request.Method.POST, API.AUTH,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            UserData.getInstance().setLoginTokens(response);
                            get(mRequestId);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG + ", method - refreshToken", "Error... " + error);
                        }
                    }) {
            };
            mRequestQueue.add(stringRequest);
        } else {
            Log.e(TAG + ", method - refreshToken", "Too much trials");
        }
    }
}

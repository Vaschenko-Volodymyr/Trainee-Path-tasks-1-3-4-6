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

package com.traineepath.volodymyrvashchenko.traineepath.application.data.channelsdata;

import android.content.Context;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.traineepath.volodymyrvashchenko.traineepath.application.ui.activities.MainScreenActivity;
import com.traineepath.volodymyrvashchenko.traineepath.application.API;
import com.traineepath.volodymyrvashchenko.traineepath.application.http.GetWithPacketToken;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class do a request to get data about user channels.
 * Also pass data to be saved in local database and
 * delegate displaying of channels to ChannelsData class.
 *
 * @see ChannelsData
 */
public class ChannelsDataRequest {

    public static final  String TAG = "ChannelsDataRequest";

    private String mPacketId;
    private Context mContext;

    public ChannelsDataRequest(Context context, String packetId) {
        mContext = context;
        mPacketId = packetId;
    }

    /**
     * Do a GET request, in case of success obtain data and display it.
     */
    public void fillChannels() {
        requestPacketTvChannels();
    }

    private void requestPacketTvChannels() {
        RequestQueue rq = Volley.newRequestQueue(mContext);
        JsonObjectRequest req = new GetWithPacketToken(Request.Method.GET, API.PACKETS_SUMMARY + "/" + mPacketId + API.TV_CHANNELS, null,
            new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ChannelsData.getInstance().saveChannelsToDB(response, mPacketId);
                    ChannelsData.getInstance().loadChannelsFromDB();
                    MainScreenActivity.sDialog.dismiss();
                } catch (JSONException e) {
                    Log.w(TAG, "Wrong parsing in fillPackets");
                }
            }
        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Packet request error: " + error.toString());
                VolleyLog.e(TAG, "Error... " + error.getMessage());
                MainScreenActivity.sDialog.dismiss();
                }
            }
        );
        rq.add(req);
    }
}

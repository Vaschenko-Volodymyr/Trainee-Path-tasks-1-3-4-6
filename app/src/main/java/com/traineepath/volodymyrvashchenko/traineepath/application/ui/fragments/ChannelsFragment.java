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
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.squareup.picasso.Picasso;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.activities.MainScreenActivity;
import com.traineepath.volodymyrvashchenko.traineepath.R;
import com.traineepath.volodymyrvashchenko.traineepath.application.API;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.channelsdata.ChannelsData;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.channelsdata.ChannelsDataRequest;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.db.DBHelper;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.state.IsLoggedByPacket;
import com.traineepath.volodymyrvashchenko.traineepath.application.http.GetWithPacketToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.adaptors.TvChannelsAdaptor;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.models.TvChannelListModel;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.Parser;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.SearchToolbarUi;
import com.traineepath.volodymyrvashchenko.traineepath.application.widgets.Fab;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is Channels fragment. Displays channels that user had bought.
 * Also has some functional to search exact channel, mark channel as favorite and
 * search group of channels.
 */
public class ChannelsFragment extends Fragment {
    private static final String                 TAG = ChannelsFragment.class.getSimpleName();

    public static TvChannelsAdaptor sAdapter;
    public static Activity sChannelsListViewActivity = null;
    public static ArrayList<TvChannelListModel> sChannelsListArray;
    public static ListView sChannels;
    public static PullToRefreshView sChannelsPullToRefreshView;
    public static Fab sFab;
    public static ImageView sNotFoundImg;
    public static TextView sNotFoundText;
    public static ChannelsFragment sInstance;

    public static boolean sSearchIsVisible = false;

    private MaterialSheetFab mMaterialSheetFab;
    private String mPacketId;
    private ChannelsDataRequest mChannelsData;

    public ChannelsFragment() {
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sInstance = this;
        sChannelsListViewActivity = getActivity();
        sAdapter = new TvChannelsAdaptor(this, sChannelsListArray, getResources());
        sAdapter = null;
    }

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mPacketId = getArguments().getString("packetId");
        View v = inflater.inflate(R.layout.fragment_channels_activity, container, false);

        sFab = (Fab) v.findViewById(R.id.fab);
        View sheetView = v.findViewById(R.id.fab_sheet);
        View overlay = v.findViewById(R.id.overlay);
        sNotFoundImg = (ImageView) v.findViewById(R.id.channels_not_found_img);
        sNotFoundText = (TextView) v.findViewById(R.id.channels_not_found_text);

        int sheetColor = getResources().getColor(R.color.colorPrimary);
        int fabColor = getResources().getColor(R.color.colorPrimaryDark);
        mMaterialSheetFab = new MaterialSheetFab<>(sFab, sheetView, overlay, sheetColor, fabColor);
        mChannelsData = new ChannelsDataRequest(getContext(), mPacketId);
        sChannelsListArray = new ArrayList<>();
        sChannels = (ListView) v.findViewById(R.id.tv_channels);
        sChannelsPullToRefreshView = (PullToRefreshView) v.findViewById(R.id.channels_refresh_layout);

        SearchToolbarUi.resetToolbar(getActivity());
        MainScreenActivity.sSearchImage.setVisibility(View.VISIBLE);

        TextView favorite = (TextView) v.findViewById(R.id.fab_sheet_item_favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelsData.getInstance().loadChannelsFromDB(DBHelper.CHANNEL_FAVORITE, "true");
                mMaterialSheetFab.hideSheet();
            }
        });

        TextView all = (TextView) v.findViewById(R.id.fab_sheet_item_all);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelsData.getInstance().loadChannelsFromDB();
                mMaterialSheetFab.hideSheet();
            }
        });

        TextView available = (TextView) v.findViewById(R.id.fab_sheet_item_available);
        available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelsData.getInstance().loadChannelsFromDB(
                        DBHelper.CHANNEL_MONITORING_STATUS, "true");
                mMaterialSheetFab.hideSheet();
            }
        });

        TextView censored = (TextView) v.findViewById(R.id.fab_sheet_item_censored);
        censored.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelsData.getInstance().loadChannelsFromDB(DBHelper.CHANNEL_CENSORED, "true");
                mMaterialSheetFab.hideSheet();
            }
        });

        MainScreenActivity.sSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(
                        getContext(), R.anim.search_image_animation);
                MainScreenActivity.sSearchImage.setAnimation(animation);

                sSearchIsVisible = SearchToolbarUi.changeSearchToolbarUI(
                        getActivity(), sSearchIsVisible);
            }
        });

        MainScreenActivity.sSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChannelsData.getInstance().loadChannelsFromDB(DBHelper.CHANNEL_NAME, s);
            }
        });

        // Initialize material sheet FAB
        mMaterialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Called when the material sheet's "show" animation starts.
            }

            @Override
            public void onSheetShown() {
                // Called when the material sheet's "show" animation ends.
            }

            @Override
            public void onHideSheet() {
                // Called when the material sheet's "hide" animation starts.
            }

            public void onSheetHidden() {
                // Called when the material sheet's "hide" animation ends.
            }
        });

        Log.w(TAG, "packetID = " + mPacketId);

        sChannelsPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sChannelsPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ChannelsData.getInstance().resetChannelsData();
                        requestChannelsData();
                        sChannelsPullToRefreshView.setRefreshing(false);
                        sChannelsPullToRefreshView.clearDisappearingChildren();
                    }
                }, 100);
            }
        });

        if (!IsLoggedByPacket.getInstance().isLogged() || MainScreenActivity.sJustEntered) {
            MainScreenActivity.sJustEntered = false;
            MainScreenActivity.sDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            MainScreenActivity.sDialog.setTitleText("Loading");
            MainScreenActivity.sDialog.show();
            ChannelsData.getInstance().resetChannelsData();
            requestChannelsData();
        } else {
            ChannelsData.getInstance().loadChannelsFromDB();
        }

        return v;
    }

    /**
     * This method is invoked by ChannelsAdaptor when user click on packet list item
     *
     * @param position - id on element in list.
     */
    public void onChannelsItemClick(int position) {
        TvChannelListModel values = ChannelsFragment.sChannelsListArray.get(position);
        String url = API.PACKETS_SUMMARY + "/" + mPacketId + API.TV_CHANNELS +
                "/" + values.getId() + API.LINK;
        if (values.getUrl().equals("false")) {
            requestUrl(url);
        } else {
            goToVLCPlayer(values.getUrl());
        }
    }

    /**
     * This method is invoked by ChannelsAdaptor when user long click on packet list item
     *
     * @param position id on element in list.
     */
    public void onChannelsItemLongClick(int position) {
        TvChannelListModel value = ChannelsFragment.sChannelsListArray.get(position);
        Vibrator v = (Vibrator) this.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
        SweetAlertDialog dialog = new SweetAlertDialog(
                this.getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        String title = value.getTitle();
        if (value.isFavorite()) {
            title = title + "(Ваш любимый)";
        }
        dialog.setTitleText(title);
        String message = "Канал номер " + value.getNumber() + ".\n"
                + "Жанр канала - " + value.getGenre() + ".\n";

        if (value.isCensored()) {
            message = message + "Канал для взрослых.\n";
        } else {
            message = message + "Канал без возрастных ограничений.\n";
        }

        if ((value.isArchivable())) {
            message = message + "Канал архивируется.\n";
        } else {
            message = message + "Канал не архивируется.\n";
        }

        if (value.isAvailable()) {
            message = message + "Канал доступен.\n";
        } else {
            message = message + "Канал временно недоступен.\n";
        }
        dialog.setContentText(message);
        dialog.setCustomImage(getResources().getDrawable(R.drawable.toolbar_back_arrow));
        dialog.show();
        TextView tv = (TextView) dialog.findViewById(cn.pedant.SweetAlert.R.id.content_text);
        tv.setGravity(Gravity.START);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        ImageView img = (ImageView) dialog.findViewById(cn.pedant.SweetAlert.R.id.custom_image);
        img.setMinimumWidth(50);
        img.setMinimumHeight(50);
        Picasso.with(this.getActivity()).load(value.getLogo()).into(img);
    }

    /**
     * Saves clicked channel as a favorite.
     * <b><em>NOTE:</em></b> this won't save channel as a favorite on server, just on device,
     * so if you refresh data - all locally assigned to favorite channels will be marked as
     * <b>NOT</b> favorite again.
     *
     * To set channel as a favorite please use TV adapter.
     *
     * @param position
     */
    public void onChannelsFavoriteIconClick(int position) {
        TvChannelListModel values = ChannelsFragment.sChannelsListArray.get(position);
        RelativeLayout rl = (RelativeLayout) getViewByPosition(position, sChannels);
        ImageView favorite = (ImageView) rl.findViewById(R.id.channel_favorite);
        if (values.isFavorite()) {
            values.setFavorite(false);
            ChannelsData.getInstance().setChannelFavorite(values.getNumber(), false);
            favorite.setImageDrawable(getResources().getDrawable(R.drawable.channel_is_not_favorite));
        } else {
            values.setFavorite(true);
            ChannelsData.getInstance().setChannelFavorite(values.getNumber(), true);
            favorite.setImageDrawable(getResources().getDrawable(R.drawable.channel_is_favorite));
        }
    }

    private void requestChannelsData() {
        sChannelsListArray.clear();
        mChannelsData.fillChannels();
    }

    private void requestUrl(String url) {
        Log.d(TAG, "Request URL = " + url);
        RequestQueue rq = Volley.newRequestQueue(getContext());
        JsonObjectRequest req = new GetWithPacketToken(Request.Method.GET, url , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            handleUrl(response);
                        } catch (JSONException e) {
                            Log.w("JSONException", "wrong parsing in fillPackets");
                        }
                        Log.w("TV LINK", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Packet request error", error.toString());
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        rq.add(req);
    }

    private void handleUrl(JSONObject response) throws JSONException {
        Parser parser = new Parser();
        String result = response.getString("results");
        String url = parser.parse(result, "url");
        Log.w("url", url);
        goToVLCPlayer(url);
    }

    private void goToVLCPlayer(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("org.videolan.vlc");
        intent.setDataAndType(Uri.parse(url), "video/*");
        startActivity(intent);
    }

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}

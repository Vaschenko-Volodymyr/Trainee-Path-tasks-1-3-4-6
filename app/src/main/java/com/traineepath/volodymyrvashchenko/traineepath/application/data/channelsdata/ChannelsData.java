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
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.traineepath.volodymyrvashchenko.traineepath.application.data.db.DBHelper;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.adaptors.TvChannelsAdaptor;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments.ChannelsFragment;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.models.TvChannelListModel;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.ListViewConverter;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.Translit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Contains <b>useful methods</b> to initialize, save, load, set or reset channel's data</p>
 * <p><b><em>NOTE:</em></b> you should call initChannelsData(Context, Resources) method before
 * start working with it</p>
 * <p>Method to initialize Channel's data: initChannelsData(Context, Resources).</p>
 * <p>Method to save Channel's data to the local database: saveChannelsToDB(JSONObject, String).</p>
 * <p>Methods to load Channel's data: loadChannelsFromDB(String, CharSequence)
 * and its overloaded version loadChannelsFromDB(String, CharSequence) for more detailed load.</p>
 * <p>Method to set specific channel af favorite: setChannelFavorite(String, boolean).</p>
 * <p>Method to reset all channel's data: resetChannelsData()</p>
 * @see #saveChannelsToDB(JSONObject, String)
 * @see #loadChannelsFromDB()
 * @see #loadChannelsFromDB(String, CharSequence)
 * @see #setChannelFavorite(String, boolean)
 * @see #resetChannelsData()
 */
public class ChannelsData {

    private static final String TAG = ChannelsData.class.toString();

    private static ChannelsData sInstance = new ChannelsData();

    private DBHelper mDBHelper;
    private Resources mResources;
    private ListViewConverter mConverter;

    private ChannelsData() {
    }

    /**
     * Method to get singleton instance of the ChannelsData class.
     * @return ChannelsData instance.
     */
    public static ChannelsData getInstance() {
        return sInstance;
    }

    public void initChannelsData(Context context, Resources resources) {
        this.mResources = resources;
        mDBHelper = new DBHelper(context);
        mConverter = new ListViewConverter();
    }

    /**
     * Saves passed data to local database
     *
     * @param response - JSON response, obtained from server.
     * @param packetId - id of packet which user had picked
     */
    public void saveChannelsToDB(
            JSONObject response, String packetId) throws JSONException {
        JSONArray array = response.getJSONArray("results");
        Log.v(TAG, "Method: saveChannelsToDB -> Packets JSON array: " + array.toString());

        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        for (int i = 0; i < array.length(); i++) {
            JSONObject channel = array.getJSONObject(i);
            String query = "INSERT INTO " + DBHelper.CHANNELS_TABLE + " VALUES ( " +
                    "\"" + channel.getString("id") + "\", " +
                    "\"" + channel.getString("name") + "\", " +
                    "\"" + channel.getString("genre_id") + "\", " +
                    "\"" + channel.getString("number") + "\", " +
                    "\"" + channel.getString("url") + "\", ";

            if (channel.getString("archive").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }
            query = query + "\"" + channel.getString("archive_range") + "\", ";
            if (channel.getString("pvr").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }
            if (channel.getString("censored").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }
            if (channel.getString("favorite").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }

            query = query + "\"" + channel.getString("logo").replace("\\", "") + "\", ";
            if (channel.getString("monitoring_status").equals("0")) {
                query = query + "\"false\", ";
            } else {
                query = query + "\"true\", ";
            }
            query = query + "\"" + packetId + "\", ";
            query = query + "\"" + Translit.translit(channel.getString("name")) + "\") ";
            database.execSQL(query);
            Log.d(TAG, "Method: saveChannelsToDB -> " + query);
        }
        database.close();
    }

    /**
     * Overloaded method.
     * @see #loadChannelsFromDB(String, CharSequence) for more details.
     */
    public void loadChannelsFromDB() {
        loadChannelsFromDB("", "");
    }

    /**
     * Loads channels data from the local database and attach data
     * to the fragment layout to be visible.
     *
     * If caused without parameters, method will return all data which is stored in the database.
     *
     * For more specific data to be returned you should pass
     * @param column - name of database column in CHANNELS table
     * @param condition - the condition, by which the search will be done
     */
    public void loadChannelsFromDB(String column, CharSequence condition) {
        Map<Integer, Map<String, String>> channel = new HashMap<>();
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Cursor cursor;
        String query = "SELECT * FROM " + DBHelper.CHANNELS_TABLE + " ";
        if (!column.equals("")) {
            if (column.equals(DBHelper.CHANNEL_NAME)) {
                condition = Translit.translit(condition.toString());
                column = DBHelper.CHANNEL_NAME_TRANSLIT;
                String advancedQuery;
                advancedQuery = query + "WHERE " + column + " LIKE \"%" + condition + "\" " +
                                           "OR " + column + " LIKE \"%" + condition + "%\" " +
                                           "OR " + column + " LIKE \""  + condition + "%\" " +
                                           "OR " + column + " LIKE \""  + condition + "\" ";
                cursor = database.rawQuery(advancedQuery, null);
                Log.w(TAG, "loadChannelsFromDB: SQL LIKE query -> " + advancedQuery);
            }
            else {
                // TODO: other select with LIKE conditions
                String advancedQuery = "SELECT * FROM " + DBHelper.CHANNELS_TABLE + " " +
                        "WHERE " + column + " = \"" + condition + "\"";
                cursor = database.rawQuery(advancedQuery, null);

            }
            putInMap(channel, cursor);
        } else {
            cursor = database.rawQuery(query, null);
            Log.w(TAG, "loadChannelsFromDB: SQL LIKE query -> " + query);
            putInMap(channel, cursor);
        }
        attachChannelDataToRow(channel);
        cursor.close();
        database.close();
    }

    /**
     * Sets selected channel as favorite.
     * NOTE: this won't set channel as favorite at server,
     * so after refresh all locally set to favorite channels
     * will be refreshed to not favorite to
     */
    public void setChannelFavorite(String number, boolean isFavorite) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        String query = "UPDATE " + DBHelper.CHANNELS_TABLE + " " +
                "SET " + DBHelper.CHANNEL_FAVORITE + " = \'" + String.valueOf(isFavorite) + "\' " +
                "WHERE " + DBHelper.CHANNEL_NUMBER + " = " + number + " ";
        database.execSQL(query);
        Log.d(TAG, ": setChannelFavorite -> query = " + query);
    }

    /**
     * Will delete all channel's data from the database.
     * Is useful only if you want to log out.
     */
    public void resetChannelsData() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.execSQL("DELETE FROM " + DBHelper.CHANNELS_TABLE + " ;");
        database.execSQL("INSERT INTO " + DBHelper.CHANNELS_TABLE + " VALUES (" +
                "\"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\", " +
                "\"null\", \"null\", \"null\", \"null\", \"null\", \"null\", \"null\" " +
                ")");
        database.close();
    }

    private void putInMap(Map<Integer, Map<String, String>> channel, Cursor cursor) {
        int i = channel.size();
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> info = new HashMap<>();

                info.put("id", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_ID)));
                info.put("name", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_NAME)));
                info.put("genre_id", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_GENRE_ID)));
                info.put("number", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_NUMBER)));
                info.put("url", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_URL)));
                info.put("archive", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_ARCHIVE)));
                info.put("archive_range", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_ARCHIVE_RANGE)));
                info.put("pvr", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_PVR)));
                info.put("censored", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_CENSORED)));
                info.put("favorite", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_FAVORITE)));
                info.put("logo", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_LOGO)));
                info.put("monitoring_status", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_MONITORING_STATUS)));
                info.put("name_translit", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_NAME_TRANSLIT)));
                info.put("packetId", cursor.getString(
                        cursor.getColumnIndex(DBHelper.CHANNEL_PACKET_ID)));

                channel.put(i++, info);
            } while (cursor.moveToNext());
        }
    }

    private void attachChannelDataToRow(Map<Integer, Map<String, String>> channel) {
        ChannelsFragment.sChannelsListArray.clear();
        if (channel.size() >= 1) {
            ChannelsFragment.sNotFoundText.setVisibility(View.INVISIBLE);
            ChannelsFragment.sNotFoundImg.setVisibility(View.INVISIBLE);
            ChannelsFragment.sChannels.setVisibility(View.VISIBLE);
            for (int i = 0; i < channel.size(); i++) {
                Map<String, String> info = channel.get(i);
                final TvChannelListModel model = new TvChannelListModel();
                Log.d(TAG, "attachChannelDataToRow: Channel = " + info.toString());
                if (!info.get("id").equals("null")) {
                    model.setId(info.get("id"));
                    model.setTitle(info.get("name"));
                    model.setGenre(info.get("genre_id"));
                    model.setNumber(info.get("number"));
                    model.setUrl(info.get("url"));

                    if (info.get("archive").equals("false")) {
                        model.setArchive(false);
                    } else {
                        model.setArchive(true);
                    }

                    model.setArchiveRange(info.get("archive_range"));

                    if (info.get("pvr").equals("false")) {
                        model.setPvr(false);
                    } else {
                        model.setPvr(true);
                    }

                    if (info.get("censored").equals("false")) {
                        model.setCensored(false);
                    } else {
                        model.setCensored(true);
                    }

                    if (info.get("favorite").equals("false")) {
                        model.setFavorite(false);
                    } else {
                        model.setFavorite(true);
                    }

                    model.setLogo(info.get("logo").replace("\\", ""));

                    if (info.get("monitoring_status").equals("false")) {
                        model.setAvailable(false);
                    } else {
                        model.setAvailable(true);
                    }
                    model.setPacketId(info.get("packerId"));
                    ChannelsFragment.sChannelsListArray.add(model);
                }
            }
        } else {
            ChannelsFragment.sNotFoundText.setVisibility(View.VISIBLE);
            ChannelsFragment.sNotFoundImg.setVisibility(View.VISIBLE);
            ChannelsFragment.sChannels.setVisibility(View.INVISIBLE);
        }
        ChannelsFragment.sAdapter = new TvChannelsAdaptor(
                ChannelsFragment.sInstance, ChannelsFragment.sChannelsListArray, mResources);
        ChannelsFragment.sAdapter.updateResults(ChannelsFragment.sChannelsListArray);
        ChannelsFragment.sChannels.setAdapter(ChannelsFragment.sAdapter);
        mConverter.setListViewHeightBasedOnChildren(ChannelsFragment.sChannels);
    }
}

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

package com.traineepath.volodymyrvashchenko.traineepath.application.data.packetdata;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traineepath.volodymyrvashchenko.traineepath.application.data.db.DBHelper;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.state.IsLogged;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.state.IsLoggedByPacket;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.LoginAccessToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.LoginRefreshToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.PacketAccessToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.PacketRefreshToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.exceptions.InvalidDataException;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.adaptors.PacketAdaptor;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments.CabinetFragment;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.models.PacketListModel;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.DateConverter;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.ListViewConverter;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.Parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * <p>Contains <b>useful methods</b> to initialize, get, set, save, load or reset packet's data.</p>
 * <p><b><em>NOTE:</em></b> you should call initPacketData(Context, Resources) method before start working with it</p>
 * <p>Initialization methods: initPacketData(Context, Resources), initPacketTokens(), initLoggedByPacketState(). </p>
 * <p>Methods to set what you want to set: setPacketTokens(String), setLoggedByPacket(), setPacketIdAndPassword(String, String) - deprecated.</p>
 * <p>Methods to get data: getPacketId(), getPacketPassword() - deprecated, getPacketData(String, String).</p>
 * <p>Method to save data: savePacketInfo(JSONObject).</p>
 * <p>Method to load data: loadPacketsFromDB().</p>
 * <p>Method to reset data: resetPacketData().</p>
 * @see #initPacketData(Context, Resources)
 * @see #initLoggedByPacketState()
 * @see #setPacketTokens(String)
 * @see #setLoggedByPacket()
 * @see #setPacketIdAndPassword(String, String) - depreacted.
 * @see #getPacketId()
 * @see #getPacketPassword()
 * @see #getPacketData(String, String)
 * @see #savePacketInfo(JSONObject)
 * @see #loadPacketsFromDB()
 * @see #resetPacketData()
 */
public class PacketData {

    private static final String TAG = PacketData.class.toString();

    private static PacketData sInstance = new PacketData();

    private DBHelper mDBHelper;
    private Parser mParser;
    private ListViewConverter mConverter;
    private Resources mResources;
    private String mPacketId;
    private String mPacketPassword;

    private PacketData() {
    }

    /**
     * Method to get singleton instance of the PacketData class.
     * @return PacketData instance
     */
    public static PacketData getInstance() {
        return sInstance;
    }

    public void initPacketData(Context context, Resources resources) {
        this.mResources = resources;
        mDBHelper = new DBHelper(context);
        mParser = new Parser();
        mConverter = new ListViewConverter();
    }

    public void initPacketTokens() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TOKENS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int accessTokenIndex = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN);
            PacketAccessToken.getInstance().setPacketAccessToken(cursor.getString(accessTokenIndex));
            int refreshTokenIndex = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN);
            PacketRefreshToken.getInstance().setPacketRefreshToken(cursor.getString(refreshTokenIndex));
        }
        cursor.close();
        Log.w(TAG, "Packet access token is loaded as " + PacketAccessToken.getInstance().getPacketAccessToken() + ", and packet refresh token is saved as " + PacketRefreshToken.getInstance().getPacketRefreshToken());

        cursor = database.query(DBHelper.PACKET_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int packetId = cursor.getColumnIndex(DBHelper.PACKET_ID);
            this.mPacketId = cursor.getString(packetId);
            int packetPassword = cursor.getColumnIndex(DBHelper.PACKET_PASSWORD);
            this.mPacketPassword = cursor.getString(packetPassword);
        }
        database.close();
    }

    public void initLoggedByPacketState() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.LOGGED_DETAILS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int state = cursor.getColumnIndex(DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET);
            IsLoggedByPacket.getInstance().saveLoggedState(cursor.getString(state));
        }
        cursor.close();
        Log.w(TAG, "Logged by packet state : logged - " + String.valueOf(IsLogged.getInstance().isLogged()));
        database.close();
    }

    public void setPacketTokens(String response) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        String accessToken = mParser.parse(response, "access_token");
        String refreshToken = mParser.parse(response, "refresh_token");
        database.execSQL("UPDATE " + DBHelper.TOKENS_TABLE + " SET " +
                DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN + " = \"" + LoginAccessToken.getInstance().getAccessToken() + "\", " +
                DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN + " = \"" + LoginRefreshToken.getInstance().getRefreshToken() + "\", " +
                DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN + " = \"" + accessToken + "\", " +
                DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN + " = \"" + refreshToken + "\" ");
        PacketAccessToken.getInstance().setPacketAccessToken(accessToken);
        PacketRefreshToken.getInstance().setPacketRefreshToken(refreshToken);

        Cursor cursor = database.query(DBHelper.TOKENS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int login = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN);
            int refresh = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN);
            int access = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN);
            int accessRefresh = cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN);
            Log.w(TAG, "Packet and login tokens are saved as : Access - " + cursor.getString(login) + ", Refresh - " + cursor.getString(refresh) +
                    ", Packet Access - " + cursor.getString(access) + ", Packet Refresh - " + cursor.getString(accessRefresh));
        }
        cursor.close();
        database.close();
    }

    public void setLoggedByPacket() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.LOGGED_DETAILS_TABLE + " SET " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED + " = \"" + String.valueOf(IsLogged.getInstance().isLogged()) + "\", " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET + " = \"true\" ");
        database.close();
    }

    /**
     *
     * @param packetId
     * @param packetPassword
     *
     * @deprecated as for it is no use in storing user data
     */
    @Deprecated
    public void setPacketIdAndPassword(String packetId, String packetPassword) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.PACKET_TABLE + " SET " +
                DBHelper.PACKET_ID + " = \"" + packetId + "\", " +
                DBHelper.PACKET_PASSWORD + " = \"" + packetPassword + "\" ");
        database.close();
    }

    public String getPacketId() throws InvalidDataException {
        if (mPacketId != null) {
            return mPacketId;
        } else throw new InvalidDataException();
    }

    /**
     *
     * @return
     * @throws InvalidDataException
     *
     * @deprecated
     */
    @Deprecated
    public String getPacketPassword() throws InvalidDataException {
        if (mPacketPassword != null) {
            return mPacketPassword;
        } else throw new InvalidDataException();
    }

    public String getPacketData(String name, String from) {
        String result = null;
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        String query = "SELECT * FROM " + DBHelper.PACKET_TABLE + " WHERE " + DBHelper.PACKET_NAME + " = \"" + name + "\"";
        Log.d(TAG, "getPacketId: query = " + query);
        Cursor cursor = database != null ? database.rawQuery(query, null) : null;
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        while (true) {
            result = cursor.getString(cursor.getColumnIndex(from));
            if (cursor.isLast()) break;
            cursor.moveToNext();
        }
        cursor.close();
        Log.d(TAG, "getPacketId: result = " + result);
        return result;
    }

    public void savePacketInfo(JSONObject response) throws JSONException {
        JSONArray array = response.getJSONArray("results");
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Log.w(TAG, "Packets JSON array: " + array.toString());
        Cursor cursor = database.query(DBHelper.USER_INFO_TABLE, null, null, null, null, null, null);
        String userEmail = null;
        if (cursor.moveToLast()) {
            int userIdIndex = cursor.getColumnIndex(DBHelper.USER_INFO_EMAIL);
            userEmail = cursor.getString(userIdIndex);
        }

        for (int i = 0; i < array.length(); i++) {
            JSONObject packet = array.getJSONObject(i);
            String id = packet.getString("id");
            String name = packet.getString("title");
            String password = packet.getString("pass");
            String dateEnd = packet.getString("date_end");
            String status = packet.getString("status");

            String query = "INSERT INTO " + DBHelper.PACKET_TABLE + " VALUES ( " +
                    "\"" + id + "\", " +
                    "\"" + name + "\", " +
                    "\"" + password + "\", " +
                    "\"" + dateEnd + "\", " +
                    "\"" + status + "\", " +
                    "\"" + userEmail + "\" " +
                    ")"
                    ;
            database.execSQL(query);
        }
        cursor.close();
        database.close();
    }

    public void loadPacketsFromDB() {
        Log.d(TAG, "Packet info is loaded from DB");
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.PACKET_TABLE, null, null, null, null, null, null);
        int i = 1;
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                final PacketListModel model = new PacketListModel();
                int id = cursor.getColumnIndex(DBHelper.PACKET_ID);
                int name = cursor.getColumnIndex(DBHelper.PACKET_NAME);
                int password = cursor.getColumnIndex(DBHelper.PACKET_PASSWORD);
                int dateEnd = cursor.getColumnIndex(DBHelper.PACKET_DATE_END);
                int status = cursor.getColumnIndex(DBHelper.PACKET_STATUS);

                model.setId(cursor.getString(id));
                model.setName(cursor.getString(name));
                model.setDate(DateConverter.timestampToDate(new Date(Long.parseLong(cursor.getString(dateEnd)) * 1000).toString()));
                model.setPassword(cursor.getString(password));
                model.setStatus(cursor.getColumnName(status));
                CabinetFragment.sPacketListArray.add(model);
                i++;
            }
            CabinetFragment.sAdapter = new PacketAdaptor(CabinetFragment.sInstance, CabinetFragment.sPacketListArray, mResources);
            CabinetFragment.sPackets.setAdapter(CabinetFragment.sAdapter);
            mConverter.setListViewHeightBasedOnChildren(CabinetFragment.sPackets);
        }

        cursor.close();
        database.close();
    }

    public void resetPacketData() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.execSQL("DELETE FROM " + DBHelper.PACKET_TABLE + " ;");
        database.execSQL("INSERT INTO " + DBHelper.PACKET_TABLE + " VALUES (\"null\", \"null\", \"null\", \"null\", \"null\", \"null\")");
        database.close();
    }
}

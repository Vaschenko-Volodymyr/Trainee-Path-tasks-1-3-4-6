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

package com.traineepath.volodymyrvashchenko.traineepath.application.data.userdata;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.traineepath.volodymyrvashchenko.traineepath.application.ui.activities.MainScreenActivity;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.db.DBHelper;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.state.IsLogged;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.LoginAccessToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.LoginRefreshToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.PacketAccessToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.PacketRefreshToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.Parser;

/**
 * <p>Contains <b>useful methods</b> to initialize, set, load or reset user's data.</p>
 * <p><b><em>NOTE:</em></b> you should call initUserData(Context) method before start working with it</p>
 * <p>Initialization methods: initLoggedState(), initLoginTokens(), initUserInfo().</p>
 *
 * <p>Methods to set what you want to set: setPacketTokens(String), setLoggedByPacket(),
 * setPacketIdAndPassword(String, String) - deprecated.</p>
 *
 * <p>Methods to set data: setLoginTokens(String), setLoggedByLogin(),
 * setUserData(String), setUserEmail(String).</p>
 *
 * <p>Method to load data: loadUserData() .</p>
 * <p>Method to reset data: resetUserData().</p>
 * @see #initLoggedState()
 * @see #initLoginTokens()
 * @see #initUserInfo()
 * @see #setLoginTokens(String)
 * @see #setUserEmail(String)
 * @see #setUserData(String)
 * @see #setLoggedByLogin()
 * @see #loadUserData()
 * @see #resetUserData()
 */
public class UserData {

    private static final String TAG = "UserData";

    private static UserData sInstance = new UserData();

    private DBHelper mDBHelper;
    private Parser mParser;

    private UserData() {
    }

    public void initUserData(Context context) {
        mDBHelper = new DBHelper(context);
        mParser = new Parser();
    }

    public static UserData getInstance() {
        return sInstance;
    }

    /**
     * Obtain logged state from local database
     */
    public void initLoggedState() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Cursor cursor = database.query(
                DBHelper.LOGGED_DETAILS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int state = cursor.getColumnIndex(DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED);
            IsLogged.getInstance().saveLoggedState(cursor.getString(state));
        }
        cursor.close();
        database.close();
    }

    /**
     * Obtain login access and refresh tokens and save values to appropriate singleton classes
     * @see LoginAccessToken
     * @see LoginRefreshToken
     */
    public void initLoginTokens() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TOKENS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            LoginAccessToken.getInstance().setAccessToken(cursor.getString(
                    cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN)));

            LoginRefreshToken.getInstance().setRefreshToken(cursor.getString(
                    cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN)));
        }
        cursor.close();
        database.close();
    }

    /**
     * Retrieve used data from the local database.
     */
    public void initUserInfo() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.USER_INFO_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            MainScreenActivity.sEmail.setText(cursor.getString(
                    cursor.getColumnIndex(DBHelper.USER_INFO_EMAIL)));
            MainScreenActivity.sBalance.setText(cursor.getString(
                    cursor.getColumnIndex(DBHelper.USER_INFO_BALANCE)));
            MainScreenActivity.sBonus.setText(cursor.getString(
                    cursor.getColumnIndex(DBHelper.USER_INFO_BONUS)));
            MainScreenActivity.sName.setText(cursor.getString(
                    cursor.getColumnIndex(DBHelper.USER_INFO_NAME)));
        }
        cursor.close();
        database.close();
    }

    /**
     * Retrieve login access and refresh tokens from server's response and
     * save it to the local database.
     *
     * @param response - String response from server.
     */
    public void setLoginTokens(String response) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.TOKENS_TABLE + " SET " +
                DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN + " = \""
                + mParser.parse(response, "login_token") + "\", " +

                DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN + " = \""
                + mParser.parse(response, "refresh_token") + "\", " +

                DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN + " = \""
                + PacketAccessToken.getInstance().getPacketAccessToken()+ "\", " +

                DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN + " = \""
                + PacketRefreshToken.getInstance().getPacketRefreshToken() + "\"");

        Cursor cursor = database.query(DBHelper.TOKENS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            LoginAccessToken.getInstance().setAccessToken(cursor.getString(
                    cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN)));

            LoginRefreshToken.getInstance().setRefreshToken(cursor.getString(
                    cursor.getColumnIndex(DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN)));
        }
        cursor.close();
        database.close();
    }

    /**
     * Saves inputted by user email to the local database.
     *
     * @param email - String value of email.
     */
    public void setUserEmail(String email) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.USER_INFO_TABLE + " SET " +
                DBHelper.USER_INFO_ID + " = \"null\", " +
                DBHelper.USER_INFO_EMAIL + " = \"" + email + "\", " +
                DBHelper.USER_INFO_NAME + " = \"null\", " +
                DBHelper.USER_INFO_BALANCE + " = \"null\", " +
                DBHelper.USER_INFO_BONUS + " = \"null\" "
        );
        database.close();
    }

    /**
     * Retrieve used data from server's response and saves it to the local database.
     *
     * @param response - String server's response.
     */
    public void setUserData(String response) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.USER_INFO_TABLE, null, null, null, null, null, null);
        String email = null;
        if (cursor.moveToLast()) {
            email = cursor.getString(cursor.getColumnIndex(DBHelper.USER_INFO_EMAIL));
        }

        database.execSQL("UPDATE " + DBHelper.USER_INFO_TABLE + " SET " +
                 DBHelper.USER_INFO_ID + " = \"" + mParser.parse(response, "id") + "\", " +
                 DBHelper.USER_INFO_EMAIL + " = \"" + email + "\", " +
                 DBHelper.USER_INFO_NAME + " = \"" + mParser.parse(response, "name") + "\", " +
                 DBHelper.USER_INFO_BALANCE + " = \"" + mParser.parse(response, "balance") + "\", " +
                 DBHelper.USER_INFO_BONUS + " = \"" + mParser.parse(response, "bonus") + "\" "
        );
        loadUserData();
        cursor.close();
        database.close();
    }

    /**
     * Retrieves logged state from the local database and
     * sets state to the appropriate singleton class.
     *
     * @see IsLogged
     */
    public void setLoggedByLogin() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.LOGGED_DETAILS_TABLE + " SET " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED + " = \"true\", " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET + " = \"false\" ");

        Cursor cursor = database.query(
                DBHelper.LOGGED_DETAILS_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            IsLogged.getInstance().saveLoggedState(cursor.getString(
                    cursor.getColumnIndex(DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED)));
        }
        cursor.close();
        database.close();
    }

    /**
     * Obtain user data from the local database and displays it on the MainScreen
     *
     * @see MainScreenActivity
     */
    public void loadUserData() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.USER_INFO_TABLE, null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            UserEmail.getInstance().setUserEmail(cursor.getString(
                    cursor.getColumnIndex(DBHelper.USER_INFO_EMAIL)));

            MainScreenActivity.sEmail.setText(cursor.getString(
                    cursor.getColumnIndex(DBHelper.USER_INFO_EMAIL)));
            MainScreenActivity.sName.setText(cursor.getString(
                    cursor.getColumnIndex(DBHelper.USER_INFO_NAME)));
            MainScreenActivity.sBalance.setText(cursor.getString(
                    cursor.getColumnIndex(DBHelper.USER_INFO_BALANCE)));
            MainScreenActivity.sBonus.setText(cursor.getString(
                    cursor.getColumnIndex(DBHelper.USER_INFO_BONUS)));

        }
        cursor.close();
        database.close();
    }

    /**
     * Reset used data. Is useful if user wants to log out and delete all the cashes.
     */
    public void resetUserData() {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        database.execSQL("UPDATE " + DBHelper.TOKENS_TABLE + " SET " +
                DBHelper.TOKEN_COLUMN_LOGIN_ACCESS_TOKEN + " = \"null\", " +
                DBHelper.TOKEN_COLUMN_LOGIN_REFRESH_TOKEN + " = \"null\", " +
                DBHelper.TOKEN_COLUMN_PACKET_ACCESS_TOKEN + " = \"null\", " +
                DBHelper.TOKEN_COLUMN_PACKET_REFRESH_TOKEN + " = \"null\" ");

        database.execSQL("UPDATE " + DBHelper.LOGGED_DETAILS_TABLE + " SET " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED + " = \"false\", " +
                DBHelper.LOGGED_STATE_COLUMN_IS_LOGGED_BY_PACKET + " = \"false\" ");

        database.execSQL("UPDATE " + DBHelper.USER_INFO_TABLE + " SET " +
                DBHelper.USER_INFO_ID + " = \"null\", " +
                DBHelper.USER_INFO_EMAIL + " = \"null\", " +
                DBHelper.USER_INFO_NAME + " = \"null\", " +
                DBHelper.USER_INFO_BALANCE + " = \"null\", " +
                DBHelper.USER_INFO_BONUS + " = \"null\" ");

        database.close();
    }
}

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

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.dd.processbutton.iml.ActionProcessButton;

import com.traineepath.volodymyrvashchenko.traineepath.R;
import com.traineepath.volodymyrvashchenko.traineepath.application.API;
import com.traineepath.volodymyrvashchenko.traineepath.application.data.userdata.UserData;
import com.traineepath.volodymyrvashchenko.traineepath.application.http.RequestWithLoginToken;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.NetworkState;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the class for login screen. Will be shown to user in case user is not logged in or
 * committed logout. In other cases the MainScreenActivity will be shown.
 * LoginActivity contains two edit text fields to input user data and
 * ProgressButton to perform log in.
 *
 * @see MainScreenActivity
 */
public class LoginActivity extends AppCompatActivity {

    public static final String USER_EMAIL = "user_email";

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    private static final String TYPE = "grant_type";
    private static final String PASSWORD = "password";

    private EditText mLoginField;
    private EditText mPasswordField;
    private TextView mCreateAccount;
    private TextView mForgotPassword;
    private ActionProcessButton mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mLoginField = (EditText) findViewById(R.id.login_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);

        mLogin = (ActionProcessButton) findViewById(R.id.btnProcess);
        mLogin.setMode(ActionProcessButton.Mode.PROGRESS);
        mLogin.setProgress(0);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLogin.setMode(ActionProcessButton.Mode.ENDLESS);
                mLogin.setProgress(1);
                if (!isNetworkConnected()) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Отсутствует подключение к интернету")
                            .setContentText("Проверьте включен ли wifi(рекомендуется) или передача мобильных данных и повторите попытку")
                            .show();
                    mLogin.setProgress(0);
                } else {
                    Log.d("Login", "Login button pressed");
                    loginUser();
                }
            }
        });

        mCreateAccount = (TextView) findViewById(R.id.create_account);
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(API.REGISTER_REDIRECT));
                startActivity(intent);
            }
        });

        mForgotPassword = (TextView) findViewById(R.id.remind_password);
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(API.REMIND_REDIRECT));
                startActivity(intent);
            }
        });

        UserData.getInstance().initUserData(this);
    }

    private void loginUser(){
        final String username = mLoginField.getText().toString().trim();
        final String password = mPasswordField.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.AUTH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("Server is reachable", response);
                        if (response.contains("error")) {
                            Log.d("Login", "Server request error: " + response);
                            mLogin.setError("Error!");
                            mLogin.setErrorText("Error!");
                            mLogin.setProgress(0);
                            showWrongDataMessage();
                        } else if (response.contains("login_token")) {
                            saveTokensToTheDB(response);
                            saveUserDataToTheDB();
                            setLogged();
                            RequestWithLoginToken request = new RequestWithLoginToken();
                            request.get(RequestWithLoginToken.USER_INFO_REQUEST);
                            mLogin.setProgress(100);
                            Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                            intent.putExtra(USER_EMAIL, "Passed from "
                                    + LoginActivity.class.getSimpleName() + "\n"
                                    + mLoginField.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showWrongDataMessage();
                        Log.d("Login", "Error... " + error);
                        mLogin.setProgress(0);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put(TYPE, PASSWORD);
                params.put(KEY_USERNAME, username);
                params.put(KEY_PASSWORD, password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private boolean isNetworkConnected() {
        NetworkState state = new NetworkState(this);
        return state.networkIsAvailable();
    }

    private void showWrongDataMessage() {
        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ошибка доступа")
                .setContentText("Проверьте корректность ваших данных.\n" +
                        "Если не помните свои данные - воспользуйтесь напоминанием пароля")
                .show();
    }

    private void saveTokensToTheDB(String response){
        UserData.getInstance().setLoginTokens(response);
    }

    private void saveUserDataToTheDB() {
        UserData.getInstance().setUserEmail(mLoginField.getText().toString().trim());
    }

    private void setLogged() {
        MainScreenActivity.sJustEntered = true;
        UserData.getInstance().setLoggedByLogin();
    }
}

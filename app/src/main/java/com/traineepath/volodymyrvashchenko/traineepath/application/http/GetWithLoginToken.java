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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.LoginAccessToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a GET request with headers by default according to the API and
 * with LoginAccessToken as a Bearer value.
 *
 * @see LoginAccessToken
 */
public class GetWithLoginToken extends JsonObjectRequest {
    public GetWithLoginToken(int method, String url, JSONObject j, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, j, listener, errorListener);

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Accept-Language", "ru-RU");
        String token = "Bearer " + LoginAccessToken.getInstance().getAccessToken();
        headers.put("Login", token);
        return headers;
    }

}

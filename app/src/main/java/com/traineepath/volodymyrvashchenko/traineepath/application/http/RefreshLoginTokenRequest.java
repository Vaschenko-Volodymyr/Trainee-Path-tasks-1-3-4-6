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

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.LoginRefreshToken;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the request to refresh LoginAccessToken with the headers and
 * parameters by default according to the API.
 *
 * @see com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens.LoginAccessToken
 */
public class RefreshLoginTokenRequest extends StringRequest {
    public RefreshLoginTokenRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }

    @Override
    protected Map<String,String> getParams(){
        Map<String,String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", LoginRefreshToken.getInstance().getRefreshToken());
        return params;
    }
}

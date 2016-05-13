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

package com.traineepath.volodymyrvashchenko.traineepath.application.data.tokens;

/**
 * This class stores packet refresh token to be accessed while application is running
 */
public class PacketRefreshToken {
    private static PacketRefreshToken sInstance = new PacketRefreshToken();
    private static String mRefreshToken;

    private PacketRefreshToken() {
    }

    public static PacketRefreshToken getInstance() {
        return sInstance;
    }

    public String getPacketRefreshToken(){
        return mRefreshToken;
    }

    public void setPacketRefreshToken(String token){
        mRefreshToken = token;
    }

}

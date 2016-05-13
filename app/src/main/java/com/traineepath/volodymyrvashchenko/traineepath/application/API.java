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

package com.traineepath.volodymyrvashchenko.traineepath.application;

/**
 * Contains all needed links to work with
 */
public class API {

    // non-official server: http://dev28.starcards.tv/register
    public static final String REGISTER_REDIRECT = "http://starcards.tv/register";
    // non-official server: http://dev28.starcards.tv/
    public static final String REMIND_REDIRECT = "http://starcards.tv/";

    // non-official server: http://api28.starcards.tv/user
    public static final String API = "https://api.starcards.tv/user";
    public static final String AUTH            = API + "/login";
    public static final String USER_INFO       = API + "/info";
    public static final String PACKETS_SUMMARY = API + "/packets";
    public static final String LOG_IN_PACKET   = API + "/token";

    public static final String LINK = "/link";
    public static final String TV_CHANNELS = "/tv-channels";

}

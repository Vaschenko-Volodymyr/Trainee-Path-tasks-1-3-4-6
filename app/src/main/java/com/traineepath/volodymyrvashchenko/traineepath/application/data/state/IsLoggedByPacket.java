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

package com.traineepath.volodymyrvashchenko.traineepath.application.data.state;

/**
 * The main use of this class if to store the logged by packet state and
 * to save logged by packet state no matter user just logged in packet or wants to log out.
 */
public class IsLoggedByPacket {
    public static IsLoggedByPacket sInstance = new IsLoggedByPacket();
    public boolean isLogged = false;

    private IsLoggedByPacket() {
    }

    /**
     * Method to get singleton instance of the IsLoggedByPacket class.
     *
     * @return IsLoggedByPacket instance
     */
    public static IsLoggedByPacket getInstance() {
        if (sInstance == null) {
            sInstance = new IsLoggedByPacket();
        }
        return sInstance;
    }

    /**
     * Method to save logged state.
     * @param state String logged state.
     */
    public void saveLoggedState(String state) {
        isLogged = Boolean.parseBoolean(state);
    }

    /**
     * Method to get logged state.
     * @return whether user is logged or not.
     */
    public boolean isLogged() {
        return isLogged;
    }
}

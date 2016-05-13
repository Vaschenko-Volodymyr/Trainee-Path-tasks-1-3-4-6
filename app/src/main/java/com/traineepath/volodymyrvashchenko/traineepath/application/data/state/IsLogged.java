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
 * The main use of this class if to store the logged state and
 * to save logged state no matter user just logged in or wants to log out.
 */
public class IsLogged {
    public static IsLogged sInstance = new IsLogged();
    public boolean isLogged;

    private IsLogged() {
    }

    /**
     * Method to get singleton instance of the IsLogged class.
     *
     * @return IsLogged instance
     */
    public static IsLogged getInstance() {
        if (sInstance == null) {
            sInstance = new IsLogged();
        }
        return sInstance;
    }

    /**
     * Method to save logged state.
     * @param state boolean logged state.
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

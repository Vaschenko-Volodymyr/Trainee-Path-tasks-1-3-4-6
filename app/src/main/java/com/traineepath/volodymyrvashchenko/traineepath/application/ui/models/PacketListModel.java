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

package com.traineepath.volodymyrvashchenko.traineepath.application.ui.models;

/**
 * Model for PacketAdapter, will be used in ArrayList<PacketListModel>
 */
public class PacketListModel {

    private String mId = "";
    private String mName ="";
    private String mDate ="";
    private String mPassword ="";
    private String mStatus ="";

    public void setId(String id) {
        mId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getDate() {
        return mDate;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getStatus() {
        return mStatus;
    }
}

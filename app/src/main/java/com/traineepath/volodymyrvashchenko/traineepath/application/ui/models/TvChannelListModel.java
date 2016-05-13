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
 * Model for ChannelsAdapter, will be used in ArrayList<TvChannelsListModel>
 */
public class TvChannelListModel {

    private String mId;
    private String mTitle;
    private String mGenre;
    private String mNumber;
    private String mUrl;
    private String mArchiveRange;
    private String mLogo;
    private String mPacketId;
    private boolean mArchive;
    private boolean mPvr;
    private boolean mCensored;
    private boolean mFavorite;
    private boolean mAvailable;

    public void setId(String id) {
        mId = id;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setGenre(String genre) {
        mGenre = genre;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setArchiveRange(String archiveRange) {
        mArchiveRange = archiveRange;
    }

    public void setLogo(String logo) {
        mLogo = logo;
    }

    public void setArchive(boolean archive) {
        mArchive = archive;
    }

    public void setPvr(boolean pvr) {
        mPvr = pvr;
    }

    public void setCensored(boolean censored) {
        mCensored = censored;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public void setAvailable(boolean available) {
        mAvailable = available;
    }

    public void setPacketId(String packetId) {
        mPacketId = packetId;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getGenre() {
        return mGenre;
    }

    public String getNumber() {
        return mNumber;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getArchiveRange() {
        return mArchiveRange;
    }

    public String getLogo() {
        return mLogo;
    }

    public boolean isArchivable() {
        return mArchive;
    }

    public boolean isPvr() {
        return mPvr;
    }

    public boolean isCensored() {
        return mCensored;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public boolean isAvailable() {
        return mAvailable;
    }

    public String getPacketId() {
        return mPacketId;
    }
}

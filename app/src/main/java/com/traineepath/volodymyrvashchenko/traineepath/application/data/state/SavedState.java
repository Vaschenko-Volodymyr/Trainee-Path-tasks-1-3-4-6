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

import android.content.Context;
import android.content.SharedPreferences;

import com.traineepath.volodymyrvashchenko.traineepath.application.ui.activities.MainScreenActivity;

/**
 * This class saves and loads correct fragment which user had visited last time;
 */
public class SavedState {
    private static final String TAG = SavedState.class.getSimpleName();
    private static final String PARAGRAPH = "paragraph";

    private static SavedState sInstance = new SavedState();

    private boolean mFirstTimeEntered = true;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private SavedState() {
        mSharedPreferences = MainScreenActivity.getContext().getSharedPreferences(
                MainScreenActivity.AppTag(), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static SavedState getInstance() {
        return sInstance;
    }

    public void setParagraph(int id) {
        mEditor.putInt(PARAGRAPH, id);
        mEditor.apply();
    }

    public int getParagraph() {
        return mSharedPreferences.getInt(PARAGRAPH, 0);
    }

    public boolean isFirstTimeEntered() {
        if (mFirstTimeEntered) {
            mFirstTimeEntered =!mFirstTimeEntered;
            return !mFirstTimeEntered;
        } else {
            return mFirstTimeEntered;
        }
    }
}

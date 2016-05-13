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

package com.traineepath.volodymyrvashchenko.traineepath.application.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.traineepath.volodymyrvashchenko.traineepath.application.ui.activities.MainScreenActivity;
import com.traineepath.volodymyrvashchenko.traineepath.R;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments.ChannelsFragment;

/**
 * Utility to control search toolbar appearance
 */
public class SearchToolbarUi {

    /**
     * Resets search ToolBar to default
     *
     * @param activity - Activity which caused reset.
     */
    public static void resetToolbar(Activity activity) {
        MainScreenActivity.sSearch.setVisibility(View.INVISIBLE);
        MainScreenActivity.sSearchImage.setVisibility(View.INVISIBLE);
        changeSearchToolbarUI(activity, true);
    }

    /**
     * Change search ToolBar appearance depends on should it be visible on not.
     *
     * @param activity - Activity which caused changes.
     * @param searchIsVisible - desired state, pass <em>true</em> to hide search ToolBar
     *
     * @return reversed search Toolbar state
     */
    public static boolean changeSearchToolbarUI(Activity activity, boolean searchIsVisible) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (searchIsVisible) {
            imm.hideSoftInputFromWindow(MainScreenActivity.sSearch.getWindowToken(), 0);
            MainScreenActivity.sSearch.setVisibility(View.INVISIBLE);
            MainScreenActivity.sToolbarText.setVisibility(View.VISIBLE);
            MainScreenActivity.sToolbar.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            try {
                ChannelsFragment.sFab.setVisibility(View.VISIBLE);
            } catch (NullPointerException e) {
                // Should not do any complex work
            }
        } else {
            MainScreenActivity.sSearch.setVisibility(View.VISIBLE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            MainScreenActivity.sToolbarText.setVisibility(View.INVISIBLE);
            MainScreenActivity.sToolbar.setBackgroundColor(activity.getResources().getColor(R.color.lightColorPrimary));
            MainScreenActivity.sSearch.requestFocus();
            try {
                ChannelsFragment.sFab.setVisibility(View.INVISIBLE);
            } catch (NullPointerException e) {
                // Should not do any complex work
            }
        }
        return !searchIsVisible;
    }
}

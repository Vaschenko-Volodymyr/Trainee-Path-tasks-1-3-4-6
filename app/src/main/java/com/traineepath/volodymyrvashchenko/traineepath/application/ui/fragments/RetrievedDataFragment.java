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

package com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.traineepath.volodymyrvashchenko.traineepath.R;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.activities.LoginActivity;
import com.traineepath.volodymyrvashchenko.traineepath.application.util.SearchToolbarUi;

/**
 * Just to show that i can pass dana through Bundle
 */
public class RetrievedDataFragment extends Fragment{

    private String mUserEmail;

    public RetrievedDataFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        mUserEmail = b.getString(LoginActivity.USER_EMAIL);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_retrieved_data, container, false);

        SearchToolbarUi.resetToolbar(getActivity());

        TextView userEmail = (TextView) v.findViewById(R.id.retrieved_text);
        userEmail.setText(mUserEmail);

        return v;
    }
}

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Simple parser
 */
public class Parser {

    public String parse(String json, String key) {
        JsonParser parser = new JsonParser();
        JsonObject mainObject = parser.parse(json).getAsJsonObject();
        String result = mainObject.get(key).toString();
        return result.substring(1, result.length() - 1);
    }
}

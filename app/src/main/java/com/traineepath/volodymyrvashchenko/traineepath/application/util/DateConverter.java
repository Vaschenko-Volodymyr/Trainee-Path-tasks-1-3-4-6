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

/**
 * Converts english date appearance to russian date
 */
public class DateConverter {

    /**
     * @param date - date in english
     * @return - String date in russian
     */
    public static String timestampToDate(String date) {
        String result = "";
        result = result + date.substring(8, 10) + " ";

        switch (date.substring(4, 7)) {
            case "Jan":
                result = result + "января ";
                break;
            case "Feb":
                result = result + "февраля ";
                break;
            case "Mar":
                result = result + "марта ";
                break;
            case "Apr":
                result = result + "апреля ";
                break;
            case "May":
                result = result + "мая ";
                break;
            case "Jun":
                result = result + "июня ";
                break;
            case "Jul":
                result = result + "июля ";
                break;
            case "Aug":
                result = result + "августа ";
                break;
            case "Sep":
                result = result + "сентября ";
                break;
            case "Oct":
                result = result + "октября ";
                break;
            case "Nov":
                result = result + "ноября ";
                break;
            case "Dec":
                result = result + "декабря ";
                break;
            default:
                break;
        }
        result = result + date.substring(date.length() - 4, date.length());
        result = result + ", ";
        result = result + date.substring(11, 16);
        return result;
    }
}

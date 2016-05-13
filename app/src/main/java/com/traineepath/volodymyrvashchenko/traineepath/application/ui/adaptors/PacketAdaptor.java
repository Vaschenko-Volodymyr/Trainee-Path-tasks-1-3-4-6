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

package com.traineepath.volodymyrvashchenko.traineepath.application.ui.adaptors;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.traineepath.volodymyrvashchenko.traineepath.R;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments.CabinetFragment;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.models.PacketListModel;

import java.util.ArrayList;

/**
 * Adaptor for packet's list on a CabinetFragment.
 */
public class PacketAdaptor extends BaseAdapter implements View.OnClickListener{

    public Resources resources;

    private static LayoutInflater inflater = null;

    private Fragment mFragment;
    private ArrayList mData;
    private PacketListModel mModel = null;

    public PacketAdaptor(Fragment fragment, ArrayList data, Resources resources){
        mFragment = fragment;
        mData = data;
        this.resources = resources;
        inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if(mData.size() <= 0) return 1;
        return mData.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView id;
        public TextView name;
        public TextView date;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        ViewHolder holder;

        if(convertView==null) {
            view = inflater.inflate(R.layout.list_packets_layout, null);
            holder = new ViewHolder();
            holder.id = (TextView) view.findViewById(R.id.packet_id);
            holder.name=(TextView) view.findViewById(R.id.packet_name);
            holder.date=(TextView) view.findViewById(R.id.packet_date);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (mData.size()<=0){
            holder.name.setText("У вас нет пакетов");
        } else {
            mModel = null;
            mModel = (PacketListModel) mData.get(position);
            holder.id.setText(mModel.getId());
            holder.name.setText(mModel.getName());
            holder.date.setText(mModel.getDate());

            view.setOnClickListener(new OnItemClickListener(position));
            view.setOnLongClickListener(new OnLongItemClickListener(position));
        }
        return view;
    }

    public void onClick(View view) {

    }

    private class OnItemClickListener  implements View.OnClickListener {
        private int position;

        OnItemClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View arg0) {
            CabinetFragment container = (CabinetFragment) mFragment;
            container.onPacketsItemClick(position);
        }
    }

    private class OnLongItemClickListener implements View.OnLongClickListener {
        private int position;

        OnLongItemClickListener(int position) {this.position = position; }

        @Override
        public boolean onLongClick(View v) {
            CabinetFragment container = (CabinetFragment) mFragment;
            container.onPacketItemLongClick(position);
            return false;
        }
    }
}

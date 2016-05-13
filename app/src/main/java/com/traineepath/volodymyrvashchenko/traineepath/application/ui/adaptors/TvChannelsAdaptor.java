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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.traineepath.volodymyrvashchenko.traineepath.R;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.fragments.ChannelsFragment;
import com.traineepath.volodymyrvashchenko.traineepath.application.ui.models.TvChannelListModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adaptor for channel's list on a ChannelsFragment.
 */
public class TvChannelsAdaptor extends BaseAdapter implements View.OnClickListener {

    public Resources resources;

    private static LayoutInflater inflater = null;

    private ChannelsFragment mFragment;
    private ArrayList mData;

    public TvChannelsAdaptor(ChannelsFragment fragment, ArrayList data, Resources resources){
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
        public TextView title;
        public TextView genre;
        public TextView number;
        public ImageView img;
        public CircleImageView available;
        public TextView info;
        public ImageView favorite;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        ViewHolder holder;
        if(convertView==null) {
            view = inflater.inflate(R.layout.list_channels_layout, null);
            holder = new ViewHolder();

            holder.title = (TextView) view.findViewById(R.id.channel_title);
            holder.genre = (TextView) view.findViewById(R.id.channel_genre);
            holder.number = (TextView) view.findViewById(R.id.channel_number);
            holder.img = (ImageView) view.findViewById(R.id.channel_logo);
            holder.available = (CircleImageView) view.findViewById(R.id.channel_is_available);
            holder.info = (TextView) view.findViewById(R.id.channel_info);
            holder.favorite = (ImageView) view.findViewById(R.id.channel_favorite);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (mData.size()<=0){
            holder.title.setText("У вас нет пакетов");
        } else {
            TvChannelListModel mModel = (TvChannelListModel) mData.get(position);
            holder.title.setText(mModel.getTitle());
            holder.genre.setText(mModel.getGenre());
            holder.number.setText(mModel.getNumber());

            if(!mModel.isAvailable()) {
                holder.available.setVisibility(View.VISIBLE);
            }

            if (mModel.isFavorite()) {
                holder.favorite.setImageDrawable(resources.getDrawable(R.drawable.channel_is_favorite));
            } else {
                holder.favorite.setImageDrawable(resources.getDrawable(R.drawable.channel_is_not_favorite));
            }

            String channelInfo = "";
            if (mModel.isArchivable()) {
                channelInfo = resources.getString(R.string.isArchivable);
            }

            if (mModel.isCensored()) {
                if (channelInfo.equals("")) {
                    channelInfo = resources.getString(R.string.isCensoredFirst);
                } else {
                    channelInfo = channelInfo + resources.getString(R.string.isCensoredContinuous);
                }
            }

            if (channelInfo.equals("")) {
                channelInfo = "Обычный канал";
            }
            holder.info.setText(channelInfo);

            holder.favorite.setOnClickListener(new OnFavoriteIconClickListener(position));
            Picasso.with(mFragment.getActivity().getApplicationContext()).load(mModel.getLogo()).into(holder.img);

            view.setOnLongClickListener(new OnLongItemClickListener(position));
            view.setOnClickListener(new OnShortItemClick(position));

        }
        return view;
    }

    public void updateResults(ArrayList results) {
        mData = results;
        //Triggers the list update
        notifyDataSetChanged();
    }

    public void onClick(View view) {

    }

    private class OnShortItemClick implements View.OnClickListener {
        private int position;

        OnShortItemClick(int position){
            this.position = position;
        }

        @Override
        public void onClick(View arg0) {
            ChannelsFragment container = mFragment;
            container.onChannelsItemClick(position);
        }
    }

    private class OnLongItemClickListener implements View.OnLongClickListener {
        private int position;

        OnLongItemClickListener(int position){
            this.position = position;
        }

        @Override
        public boolean onLongClick(View v) {
            ChannelsFragment container = mFragment;
            container.onChannelsItemLongClick(position);
            return false;
        }
    }

    private class OnFavoriteIconClickListener implements View.OnClickListener {
        private int position;

        OnFavoriteIconClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            ChannelsFragment container = mFragment;
            container.onChannelsFavoriteIconClick(position);
        }
    }

}

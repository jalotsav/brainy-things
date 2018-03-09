/*
 * Copyright (c) 2018 Jalotsav
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jalotsav.brainythings.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jalotsav.brainythings.R;

/**
 * Created by Jalotsav on 2/23/2018.
 */

public class DevicesViewHolder extends RecyclerView.ViewHolder {

    View mItemView, mVwOnOffIndicator;
    ImageView mImgvwOnOffImage;
    TextView mTvDeviceName;

    public DevicesViewHolder(View itemView) {
        super(itemView);
        this.mItemView = itemView;
        mVwOnOffIndicator = itemView.findViewById(R.id.vw_recylrvw_item_devices_onoffindicator);
        mImgvwOnOffImage = itemView.findViewById(R.id.imgvw_recylrvw_item_devices_image);
        mTvDeviceName = itemView.findViewById(R.id.tv_recylrvw_item_devices_name);
    }

    public void setDeviceName (String deviceName) {

        mTvDeviceName.setText(deviceName);
    }

    public void setTraitsOnOff (boolean isOn) {

        mVwOnOffIndicator.setSelected(isOn);
        mImgvwOnOffImage.setSelected(isOn);
    }
}

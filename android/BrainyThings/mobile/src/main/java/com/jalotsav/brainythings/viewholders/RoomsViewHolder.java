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

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jalotsav.brainythings.R;
import com.jalotsav.brainythings.common.AppConstants;

/**
 * Created by Jalotsav on 2/22/2018.
 */

public class RoomsViewHolder extends RecyclerView.ViewHolder {

    View mItemView;
    ImageView mImgvwRoomImage;
    TextView mTvRoomName;

    public RoomsViewHolder(View itemView) {
        super(itemView);
        this.mItemView = itemView;
        mImgvwRoomImage = itemView.findViewById(R.id.imgvw_recylrvw_item_rooms_image);
        mTvRoomName = itemView.findViewById(R.id.tv_recylrvw_item_rooms_name);
    }

    public void setRoomName(String roomName) {

        mTvRoomName.setText(roomName);
    }

    public void setRoomImage(Context context, String roomType) {

        switch (roomType){
            case AppConstants.ROOM_TYPE_LIVING:
                mImgvwRoomImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_flat_room_living));
                break;
            case AppConstants.ROOM_TYPE_BED:
                mImgvwRoomImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_flat_room_bed));
                break;
            default:
                mImgvwRoomImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_flat_room_living));
                break;
        }
    }
}

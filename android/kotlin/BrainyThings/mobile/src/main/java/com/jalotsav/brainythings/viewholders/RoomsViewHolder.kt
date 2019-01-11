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

package com.jalotsav.brainythings.viewholders

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.jalotsav.brainythings.R
import com.jalotsav.brainythings.common.*

/**
 * Created by Manish Karena on 10/27/2018.
 */

class RoomsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var mImgvwRoomImage: ImageView = itemView.findViewById(R.id.imgvw_recylrvw_item_rooms_image)
    private var mTvRoomName: TextView = itemView.findViewById(R.id.tv_recylrvw_item_rooms_name)

    fun setRoomName(roomName: String) {

        mTvRoomName.text = roomName
    }

    fun setRoomImage(context: Context, roomType: String) {

        when (roomType) {
            ROOM_TYPE_LIVING -> mImgvwRoomImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_flat_room_living))
            ROOM_TYPE_BED -> mImgvwRoomImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_flat_room_bed))
            else -> mImgvwRoomImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_flat_room_living))
        }
    }
}
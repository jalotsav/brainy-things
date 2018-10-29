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

package com.jalotsav.brainythings

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jalotsav.brainythings.common.*
import com.jalotsav.brainythings.models.MdlDevices
import com.jalotsav.brainythings.models.MdlRooms
import com.jalotsav.brainythings.models.MdlTraits
import com.jalotsav.brainythings.viewholders.RoomsViewHolder
import kotlinx.android.synthetic.main.lo_actvty_main.*
import kotlinx.android.synthetic.main.lo_recyclremptyvw_appearhere.*
import java.util.*

/**
 * Created by Manish Karena on 10/27/2018.
 */

class ActvtyMain : AppCompatActivity(), View.OnClickListener {

    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mAdapter: FirebaseRecyclerAdapter<MdlRooms, RoomsViewHolder>
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mJalosavHomeRef: DatabaseReference
    private lateinit var mRoomRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lo_actvty_main)

        mLayoutManager = LinearLayoutManager(this@ActvtyMain)
        rcyclrvw_actvty_main_roomslst.setHasFixedSize(true)
        rcyclrvw_actvty_main_roomslst.layoutManager = mLayoutManager
        rcyclrvw_actvty_main_roomslst.setEmptyView(lnrlyot_recyclremptyvw_appearhere)

        tv_recyclremptyvw_appearhere.text = resources.getString(R.string.rooms_appear_here)

        mDatabase = FirebaseDatabase.getInstance()
        mJalosavHomeRef = mDatabase.reference.child(ROOT_NAME)
        mRoomRef = mJalosavHomeRef.child(CHILD_ROOOMS)

        setupFirebaseAdapter()

        fab_actvty_main_addroom.setOnClickListener(this)
    }

    // Setup Recycler Adapter using Firebase-UI with Realtime database support.
    private fun setupFirebaseAdapter() {

        prgrsbr_actvty_main.visibility = View.VISIBLE
        mAdapter = object : FirebaseRecyclerAdapter<MdlRooms, RoomsViewHolder>(
                MdlRooms::class.java,
                R.layout.lo_recyclritem_roomslst,
                RoomsViewHolder::class.java,
                mRoomRef
        ) {
            override fun onDataChanged() {
                super.onDataChanged()

                if (prgrsbr_actvty_main != null && prgrsbr_actvty_main.visibility == View.VISIBLE)
                    prgrsbr_actvty_main.visibility = View.GONE
                mAdapter.notifyDataSetChanged()
            }

            override fun populateViewHolder(viewHolder: RoomsViewHolder, model: MdlRooms, position: Int) {

                viewHolder.setRoomName(model.name.toUpperCase())
                viewHolder.setRoomImage(this@ActvtyMain, model.type)

                viewHolder.itemView.setOnClickListener {
                    startActivity(Intent(this@ActvtyMain, ActvtyDevices::class.java)
                            .putExtra(PUTEXTRA_DBREF_CHILD_KEY, mAdapter.getRef(position).key))
                }
            }
        }
        rcyclrvw_actvty_main_roomslst.adapter = mAdapter
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab_actvty_main_addroom -> {
                if (mRoomRef == null)
                    mRoomRef = mJalosavHomeRef.child(CHILD_ROOOMS)

                // Create Rooms insert object
                val newRoomsRef = mRoomRef.push()
                newRoomsRef.setValue(MdlRooms(newRoomsRef.key, ROOM_TYPE_LIVING, "My Living"))

                val arrylstMdlDevices = ArrayList<MdlDevices>()
                arrylstMdlDevices.add(MdlDevices(null, DEVICES_TYPE_LIGHT, "Main Light", PIN_NXPMX7D_GPIO6_IO12, MdlTraits(false, 0)))

                for (objMdlDevices in arrylstMdlDevices) {
                    // Create Devices object under Rooms
                    val newDevicesRef = newRoomsRef.child(CHILD_DEVICES).push()
                    objMdlDevices.id = newDevicesRef.key
                    newDevicesRef.setValue(objMdlDevices)
                }
            }
        }
    }
}
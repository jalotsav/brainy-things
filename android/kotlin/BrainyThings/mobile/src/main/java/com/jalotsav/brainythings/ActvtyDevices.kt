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

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jalotsav.brainythings.common.*
import com.jalotsav.brainythings.models.MdlDevices
import com.jalotsav.brainythings.viewholders.DevicesViewHolder
import kotlinx.android.synthetic.main.lo_actvty_devices.*
import kotlinx.android.synthetic.main.lo_recyclremptyvw_appearhere.*

/**
 * Created by Manish Karena on 10/27/2018.
 */

class ActvtyDevices : AppCompatActivity() {

    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mAdapter: FirebaseRecyclerAdapter<MdlDevices, DevicesViewHolder>
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mJalosavHomeRef: DatabaseReference
    private lateinit var mRoomRef: DatabaseReference
    private lateinit var mDevicesRef: DatabaseReference
    private lateinit var slctdRoomChildKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lo_actvty_devices)

        try {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        slctdRoomChildKey = intent.getStringExtra(PUTEXTRA_DBREF_CHILD_KEY)

        mLayoutManager = LinearLayoutManager(this)
        rcyclrvw_actvty_devices_deviceslst.setHasFixedSize(true)
        rcyclrvw_actvty_devices_deviceslst.layoutManager = mLayoutManager
        rcyclrvw_actvty_devices_deviceslst.setEmptyView(lnrlyot_recyclremptyvw_appearhere)

        tv_recyclremptyvw_appearhere.text = resources.getString(R.string.devices_appear_here)

        mDatabase = FirebaseDatabase.getInstance()
        mJalosavHomeRef = mDatabase.reference.child(ROOT_NAME)
        mRoomRef = mJalosavHomeRef.child(CHILD_ROOOMS)
        mDevicesRef = mRoomRef.ref.child(slctdRoomChildKey).child(CHILD_DEVICES)

        setupFirebaseAdapter()

    }

    // Setup Recycler Adapter using Firebase-UI with Realtime database support.
    private fun setupFirebaseAdapter() {

        prgrsbr_actvty_devices.visibility = View.VISIBLE
        mAdapter = object : FirebaseRecyclerAdapter<MdlDevices, DevicesViewHolder>(
                MdlDevices::class.java,
                R.layout.lo_recyclritem_deviceslst,
                DevicesViewHolder::class.java,
                mDevicesRef
        ) {
            override fun onDataChanged() {
                super.onDataChanged()

                if (prgrsbr_actvty_devices != null && prgrsbr_actvty_devices.visibility == View.VISIBLE)
                    prgrsbr_actvty_devices.visibility = View.GONE
                mAdapter.notifyDataSetChanged()
            }

            override fun populateViewHolder(viewHolder: DevicesViewHolder, model: MdlDevices, position: Int) {

                viewHolder.setDeviceName(model.name.toUpperCase())
                viewHolder.setTraitsOnOff(model.traits!!.on)

                viewHolder.itemView.setOnClickListener {
                    // set updated value(Reverse of current isOn value) into model class
                    model.traits!!.on = !model.traits!!.on

                    viewHolder.setTraitsOnOff(model.traits!!.on)

                    // Update Trait: isOn value in to database
                    mAdapter.getRef(position).child(CHILD_TRAITS).setValue(model.traits)
                }
            }
        }
        rcyclrvw_actvty_devices_deviceslst.adapter = mAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
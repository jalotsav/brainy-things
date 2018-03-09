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

package com.jalotsav.brainythings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jalotsav.brainythings.common.AppConstants;
import com.jalotsav.brainythings.common.RecyclerViewEmptySupport;
import com.jalotsav.brainythings.models.MdlDevices;
import com.jalotsav.brainythings.viewholders.DevicesViewHolder;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jalotsav on 2/23/2018.
 */

public class ActvtyDevices extends AppCompatActivity implements AppConstants {

    private static final String TAG = ActvtyDevices.class.getSimpleName();

    @BindView(R.id.cordntrlyot_actvty_devices) CoordinatorLayout mCrdntrlyot;
    @BindView(R.id.lnrlyot_recyclremptyvw_appearhere) LinearLayout mLnrlyotAppearHere;
    @BindView(R.id.tv_recyclremptyvw_appearhere) TextView mTvAppearHere;
    @BindView(R.id.rcyclrvw_actvty_devices_deviceslst) RecyclerViewEmptySupport mRecyclerView;
    @BindView(R.id.prgrsbr_actvty_devices) ProgressBar mPrgrsbr;

    @BindString(R.string.devices_appear_here) String mDevicesAppearHere;

    RecyclerView.LayoutManager mLayoutManager;
    FirebaseRecyclerAdapter<MdlDevices, DevicesViewHolder> mAdapter;
    FirebaseDatabase mDatabase;
    DatabaseReference mJalosavHomeRef, mRoomRef, mDevicesRef;
    String slctdRoomChildKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_actvty_devices);
        ButterKnife.bind(this);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        slctdRoomChildKey = getIntent().getStringExtra(PUTEXTRA_DBREF_CHILD_KEY);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setEmptyView(mLnrlyotAppearHere);

        mTvAppearHere.setText(mDevicesAppearHere);

        mDatabase = FirebaseDatabase.getInstance();
        mJalosavHomeRef = mDatabase.getReference().child(ROOT_NAME);
        mRoomRef = mJalosavHomeRef.child(CHILD_ROOOMS);
        mDevicesRef = mRoomRef.getRef().child(slctdRoomChildKey).child(CHILD_DEVICES);

        setupFirebaseAdapter();
    }

    // Setup Recycler Adapter using Firebase-UI with Realtime database support.
    private void setupFirebaseAdapter() {

        mPrgrsbr.setVisibility(View.VISIBLE);
        mAdapter = new FirebaseRecyclerAdapter<MdlDevices, DevicesViewHolder>(
                MdlDevices.class,
                R.layout.lo_recyclritem_deviceslst,
                DevicesViewHolder.class,
                mDevicesRef
        ) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if(mPrgrsbr != null && mPrgrsbr.getVisibility() == View.VISIBLE)
                    mPrgrsbr.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void populateViewHolder(final DevicesViewHolder viewHolder, final MdlDevices model, final int position) {

                viewHolder.setDeviceName(model.getName().toUpperCase());
                viewHolder.setTraitsOnOff(model.getTraits().isOn());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // set updated value(Reverse of current isOn value) into model class
                        model.getTraits().setOn(!model.getTraits().isOn());

                        viewHolder.setTraitsOnOff(model.getTraits().isOn());

                        // Update Trait: isOn value in to database
                        mAdapter.getRef(position).child(CHILD_TRAITS).setValue(model.getTraits());
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}

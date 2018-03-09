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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.jalotsav.brainythings.models.MdlRooms;
import com.jalotsav.brainythings.models.MdlTraits;
import com.jalotsav.brainythings.viewholders.RoomsViewHolder;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActvtyMain extends AppCompatActivity implements AppConstants {

    private static final String TAG = ActvtyMain.class.getSimpleName();

    @BindView(R.id.cordntrlyot_actvty_main) CoordinatorLayout mCrdntrlyot;
    @BindView(R.id.lnrlyot_recyclremptyvw_appearhere) LinearLayout mLnrlyotAppearHere;
    @BindView(R.id.tv_recyclremptyvw_appearhere) TextView mTvAppearHere;
    @BindView(R.id.rcyclrvw_actvty_main_roomslst) RecyclerViewEmptySupport mRecyclerView;
    @BindView(R.id.prgrsbr_actvty_main) ProgressBar mPrgrsbr;

    @BindString(R.string.rooms_appear_here) String mRoomsAppearHere;

    RecyclerView.LayoutManager mLayoutManager;
    FirebaseRecyclerAdapter<MdlRooms, RoomsViewHolder> mAdapter;
    FirebaseDatabase mDatabase;
    DatabaseReference mJalosavHomeRef, mRoomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_actvty_main);
        ButterKnife.bind(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setEmptyView(mLnrlyotAppearHere);

        mTvAppearHere.setText(mRoomsAppearHere);

        mDatabase = FirebaseDatabase.getInstance();
        mJalosavHomeRef = mDatabase.getReference().child(ROOT_NAME);
        mRoomRef = mJalosavHomeRef.child(CHILD_ROOOMS);

        setupFirebaseAdapter();
    }

    // Setup Recycler Adapter using Firebase-UI with Realtime database support.
    private void setupFirebaseAdapter() {

        mPrgrsbr.setVisibility(View.VISIBLE);
        mAdapter = new FirebaseRecyclerAdapter<MdlRooms, RoomsViewHolder>(
                MdlRooms.class,
                R.layout.lo_recyclritem_roomslst,
                RoomsViewHolder.class,
                mRoomRef
        ) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if(mPrgrsbr != null && mPrgrsbr.getVisibility() == View.VISIBLE)
                    mPrgrsbr.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void populateViewHolder(RoomsViewHolder viewHolder, MdlRooms model, final int position) {

                viewHolder.setRoomName(model.getName().toUpperCase());
                viewHolder.setRoomImage(ActvtyMain.this, model.getType());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(ActvtyMain.this, ActvtyDevices.class)
                                .putExtra(PUTEXTRA_DBREF_CHILD_KEY, mAdapter.getRef(position).getKey()));
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick({R.id.fab_actvty_main_addroom})
    public void onClickView(View view) {

        switch (view.getId()) {
            case R.id.fab_actvty_main_addroom:

                if(mRoomRef == null)
                    mRoomRef = mJalosavHomeRef.child(CHILD_ROOOMS);

                // Create Rooms insert object
                DatabaseReference newRoomsRef = mRoomRef.push();
                newRoomsRef.setValue(new MdlRooms(newRoomsRef.getKey(), ROOM_TYPE_LIVING, "My Living"));

                ArrayList<MdlDevices> arrylstMdlDevices = new ArrayList<>();
                arrylstMdlDevices.add(new MdlDevices(null, DEVICES_TYPE_LIGHT, "Main Light", PIN_NXPMX7D_GPIO6_IO12, new MdlTraits(false, 0)));

                for (MdlDevices objMdlDevices : arrylstMdlDevices) {
                    // Create Devices object under Rooms
                    DatabaseReference newDevicesRef = newRoomsRef.child(CHILD_DEVICES).push();
                    objMdlDevices.setId(newDevicesRef.getKey());
                    newDevicesRef.setValue(objMdlDevices);
                }
                break;
        }
    }
}

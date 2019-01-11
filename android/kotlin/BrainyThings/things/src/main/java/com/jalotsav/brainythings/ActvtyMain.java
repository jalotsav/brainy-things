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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jalotsav.brainythings.common.AppConstants;
import com.jalotsav.brainythings.models.MdlDevices;
import com.jalotsav.brainythings.models.MdlRooms;

import java.io.IOException;
import java.util.Map;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class ActvtyMain extends Activity implements AppConstants {

    private static final String TAG = ActvtyMain.class.getSimpleName();

    PeripheralManager mPeripheralManager;
    Gpio mGpio6IO13, mGpio6IO12, mGpio1IO10;
    FirebaseDatabase mDatabase;
    DatabaseReference mJalosavHomeRef, mRoomRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPeripheralManager = PeripheralManager.getInstance();
        //Check for the available GPIOs
        if (mPeripheralManager.getGpioList().isEmpty()) {
            Log.e(TAG, "No GPIO port available on this device.");
            finish();
            return;
        }

        // Open Gpio for all devices
        openGpioAll();

        // Setup Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        mJalosavHomeRef = mDatabase.getReference().child(ROOT_NAME);
        mRoomRef = mJalosavHomeRef.child(CHILD_ROOOMS);
        setFirebaseEventListener();
    }

    private void openGpioAll() {

        try {

            mGpio6IO13 = mPeripheralManager.openGpio(PIN_NXPMX7D_GPIO6_IO13);
            mGpio6IO12 = mPeripheralManager.openGpio(PIN_NXPMX7D_GPIO6_IO12);
            mGpio1IO10 = mPeripheralManager.openGpio("GPIO1_IO10");

            mGpio6IO13.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mGpio6IO12.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mGpio1IO10.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mGpio1IO10.setValue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error on PeripheralIO API during openGpio: ", e);
            finish();
        }
    }

    private void setFirebaseEventListener() {

        mRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {

                    MdlRooms objMdlRoom = roomSnapshot.getValue(MdlRooms.class);
                    if(objMdlRoom.getDevices() != null) {
                        Map<String, MdlDevices> objMap = objMdlRoom.getDevices();
                        for (MdlDevices objMdlDevices : objMap.values()) {

                            switch (objMdlDevices.getGpio()) {
                                case PIN_NXPMX7D_GPIO6_IO12:

                                    if (mGpio6IO12 == null)
                                        continue;

                                    try {
                                        mGpio6IO12.setValue(objMdlDevices.getTraits().isOn());
                                        Log.i(TAG, PIN_NXPMX7D_GPIO6_IO12 + " isOn Traits change to " + objMdlDevices.getTraits().isOn());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "Error on " + PIN_NXPMX7D_GPIO6_IO12 + " PeripheralIO API: ", e);
                                    }
                                    break;
                                case PIN_NXPMX7D_GPIO6_IO13:

                                    if (mGpio6IO13 == null)
                                        continue;

                                    try {
                                        mGpio6IO13.setValue(objMdlDevices.getTraits().isOn());
                                        Log.i(TAG, PIN_NXPMX7D_GPIO6_IO13 + " isOn Traits change to " + objMdlDevices.getTraits().isOn());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "Error on " + PIN_NXPMX7D_GPIO6_IO13 + " PeripheralIO API: ", e);
                                    }
                                    break;
                                case PIN_NXPMX7D_GPIO1_IO10:

                                    if (mGpio1IO10 == null)
                                        continue;

                                    try {
                                        mGpio1IO10.setValue(objMdlDevices.getTraits().isOn());
                                        Log.i(TAG, PIN_NXPMX7D_GPIO1_IO10 + " isOn Traits change to " + objMdlDevices.getTraits().isOn());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.e(TAG, "Error on " + PIN_NXPMX7D_GPIO1_IO10 + " PeripheralIO API: ", e);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e(TAG, "setFirebaseEventListener - onCancelled: " + databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Close the Gpio pin.
        Log.i(TAG, "Closing All GPIO pins");
        try {
            mGpio6IO12.close();
            mGpio6IO13.close();
            mGpio1IO10.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API during closeGpio: ", e);
        } finally {
            mGpio6IO12 = null;
            mGpio6IO13 = null;
            mGpio1IO10 = null;
        }
    }
}

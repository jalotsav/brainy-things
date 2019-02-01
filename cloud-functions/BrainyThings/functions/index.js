'use strict';

const {
    dialogflow,
    BasicCard,
    Suggestions,
    Image,
} = require('actions-on-google');
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

var sprintf = require('sprintf-js').sprintf;
const strings = require('./strings') // Import strings.js for use constants value

const app = dialogflow({ debug: true });

// Firebase Realtime database references
const jalosavHomeRef = admin.database().ref(strings.general.fireDatabase.rootName);
const roomsRef = jalosavHomeRef.child(strings.general.fireDatabase.childRooms);

/**
 * @template T
 * @param {Array<T>} array The array to get a random value from
 */
const getRandomValue = array => array[Math.floor(Math.random() * array.length)];

var isRoomMatched = false;
var isDeviceMatched = false;

app.intent("things_execute", (conv, params) => {
    const paramThingsRooms = params[strings.dialogflowConsole.parameter.thingsRooms];
    const paramThingsDevices = params[strings.dialogflowConsole.parameter.thingsDevices];
    const paramThingsTraits = params[strings.dialogflowConsole.parameter.thingsTraits];
    let fulfillmentMsg = strings.general.unhandled;
    let deviceTraitsIsOn;

    // Get boolean value of Device Traits isOn by comparing on/off string
    if (paramThingsTraits === strings.general.onSml)
        deviceTraitsIsOn = true;
    else if (paramThingsTraits === strings.general.offSml)
        deviceTraitsIsOn = false;

    return roomsRef.once('value', snapshot => {
        snapshot.forEach(function (childSnapshot) {
            if (paramThingsRooms === childSnapshot.val().name.toLowerCase()) {
                isRoomMatched = true;
                console.log('Room name matched.');

                // Get all devices of matched room
                var devicesSnapshot = childSnapshot.child(strings.general.fireDatabase.childDevices);

                devicesSnapshot.forEach(function (childDeviceSnapshot) {
                    if (paramThingsDevices === childDeviceSnapshot.val().name.toLowerCase()) {
                        isDeviceMatched = true;
                        console.log('Device name matched');

                        let fireDBTraitsIsOn = childDeviceSnapshot.val().traits.isOn;
                        console.log('Device current traits isOn: ', fireDBTraitsIsOn);

                        if (fireDBTraitsIsOn !== deviceTraitsIsOn) { // Requested Traits isOn value is different than database value
                            const traitsIsOnRef = roomsRef.child(childSnapshot.key)
                                .child(strings.general.fireDatabase.childDevices)
                                .child(childDeviceSnapshot.key)
                                .child(strings.general.fireDatabase.childTraits);

                            // Update Traits isOn value and generate response and send back to assistant
                            return new Promise ((resolve, reject) => {
                                traitsIsOnRef.update({ isOn: deviceTraitsIsOn, on: deviceTraitsIsOn });

                                console.log('Device traits isOn udpated to: ', deviceTraitsIsOn);

                                fulfillmentMsg = sprintf(getRandomValue(strings.richResponse.fulfillmentTurning), paramThingsRooms, paramThingsDevices, paramThingsTraits);

                                /**
                                 * If Assistant device has screen,
                                 * Generate Rich response with BasicCard including Title, Text, Button, Image and Suggestion chips
                                 */
                                if (!conv.screen)
                                    conv.ask(fulfillmentMsg);
                                else {
                                    let [imagePath, imageName] = ["", ""];
                                    if (deviceTraitsIsOn)
                                        [imagePath, imageName] = strings.richResponse.images.lightOn;
                                    else
                                        [imagePath, imageName] = strings.richResponse.images.lightOff;

                                    conv.ask(fulfillmentMsg, new BasicCard({
                                        title: sprintf(strings.general.cardTitleTurning, paramThingsTraits),
                                        text: fulfillmentMsg,
                                        image: new Image({
                                            url: imagePath,
                                            alt: imageName,
                                        }),
                                    }));
                                    conv.ask(new Suggestions(deviceTraitsIsOn ? strings.richResponse.suggestions.turnOff : strings.richResponse.suggestions.turnOn));
                                }
                                resolve();
                            });
                        } else { // Device is already On/Off
                            console.log('Device traits isOn is already ', deviceTraitsIsOn);

                            fulfillmentMsg = sprintf(getRandomValue(strings.richResponse.fulfillmentTurnAlready), paramThingsRooms, paramThingsDevices, paramThingsTraits);
                            conv.ask(fulfillmentMsg);
                            if (conv.screen)
                                conv.ask(new Suggestions(deviceTraitsIsOn ? strings.richResponse.suggestions.turnOff : strings.richResponse.suggestions.turnOn));
                        }

                        return conv; // return for STOP the loop.
                    } else 
                        isDeviceMatched = false;
                });

                return conv; // return for STOP the loop.
            } else 
                isRoomMatched = false;
        });

        if (!isRoomMatched) {
            console.log('Room name does NOT matched.');
            conv.ask(`Oops! we didn't find this room in your home.`);
        } else {
            if (!isDeviceMatched) {
                console.log('Device name does NOT matched');
                conv.ask(`Oops! we didn't find this device in the room.`);
            }
        }
    });
});

exports.brainyThingsWebhook = functions.https.onRequest(app);

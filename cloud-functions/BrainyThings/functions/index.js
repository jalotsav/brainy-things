'use strict';

process.env.DEBUG = 'actions-on-google:*';

const functions = require('firebase-functions');
const { DialogflowApp } = require('actions-on-google');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
var sprintf = require('sprintf-js').sprintf;

const strings = require('./strings') // Import strings.js for use constants value

// Firebase Realtime database reference
const jalosavHomeRef = admin.database().ref(strings.general.fireDatabase.rootName);
const roomsRef = jalosavHomeRef.child(strings.general.fireDatabase.childRooms);

/**
 * @template T
 * @param {Array<T>} array The array to get a random value from
 */
const getRandomValue = array => array[Math.floor(Math.random() * array.length)];

exports.thingsExecute = functions.https.onRequest((request, response) => {

    const assistant = new DialogflowApp({ request: request, response: response });

    // The Entry point to all our actions
    const actionMap = new Map();
    actionMap.set(strings.dialogflowConsole.action.thingsExecute, executeHandler);
    assistant.handleRequest(actionMap);

    // Fulfill things execute action business logic
    function executeHandler(assistant) {

        const screenOutput = assistant.hasSurfaceCapability(assistant.SurfaceCapabilities.SCREEN_OUTPUT);
        const paramThingsRooms = assistant.getArgument(strings.dialogflowConsole.parameter.thingsRooms);
        const paramThingsDevices = assistant.getArgument(strings.dialogflowConsole.parameter.thingsDevices);
        const paramThingsTraits = assistant.getArgument(strings.dialogflowConsole.parameter.thingsTraits);
        let fulfillmentMsg = strings.general.unhandled;
        let deviceTraitsIsOn;

        // Get boolean value of Device Traits isOn by comparing on/off string
        if (paramThingsTraits === strings.general.onSml)
            deviceTraitsIsOn = true;
        else if (paramThingsTraits === strings.general.offSml)
            deviceTraitsIsOn = false;

        console.log('Room Name: ', paramThingsRooms);
        console.log('Device Name: ', paramThingsDevices);
        console.log('Traits: ', paramThingsTraits);

        // Get all Rooms from firebase database
        roomsRef.once('value', snapshot => {

            snapshot.forEach(function (childSnapshot) {

                if (paramThingsRooms === childSnapshot.val().name.toLowerCase()) {

                    console.log('Room name matched.');

                    // Get all devices of matched room
                    var devicesSnapshot = childSnapshot.child(strings.general.fireDatabase.childDevices);
                    devicesSnapshot.forEach(function (childDeviceSnapshot) {

                        if (paramThingsDevices === childDeviceSnapshot.val().name.toLowerCase()) {

                            console.log('Device name matched');

                            let fireDBTraitsIsOn = childDeviceSnapshot.val().traits.isOn;
                            console.log('Device current traits isOn: ', fireDBTraitsIsOn);

                            if (fireDBTraitsIsOn !== deviceTraitsIsOn) { // Requested Traits isOn value is different than database value

                                const traitsIsOnRef = roomsRef.child(childSnapshot.key)
                                    .child(strings.general.fireDatabase.childDevices)
                                    .child(childDeviceSnapshot.key)
                                    .child(strings.general.fireDatabase.childTraits);

                                // Update Traits isOn value and generate response and send back to assistant
                                traitsIsOnRef.update({ isOn: deviceTraitsIsOn, on: deviceTraitsIsOn }).then(function () {

                                    console.log('Device traits isOn change to ', deviceTraitsIsOn);
                                    fulfillmentMsg = sprintf(getRandomValue(strings.richResponse.fulfillmentTurning), paramThingsRooms, paramThingsDevices, paramThingsTraits);

                                    /**
                                     * If Assistant device has screen,
                                     * Generate RichResponse with Text, Card (with Image) and suggestion chips
                                     */
                                    if (screenOutput) {

                                        let [imagePath, imageName] = ["", ""];
                                        if (deviceTraitsIsOn)
                                            [imagePath, imageName] = strings.richResponse.images.lightOn;
                                        else
                                            [imagePath, imageName] = strings.richResponse.images.lightOff;

                                        const card = assistant.buildBasicCard(sprintf(strings.general.cardTitleTurning, paramThingsTraits))
                                            .setImage(imagePath, imageName);

                                        const richResponseBuild = assistant.buildRichResponse()
                                            .addSimpleResponse(fulfillmentMsg)
                                            .addBasicCard(card)
                                            .addSuggestions(deviceTraitsIsOn ? strings.richResponse.suggestions.turnOff : strings.richResponse.suggestions.turnOn);

                                        assistant.ask(richResponseBuild);
                                    } else
                                        assistant.ask(fulfillmentMsg);

                                    return traitsIsOnRef;
                                }).catch(function (error) {
                                    console.error(error);
                                });
                            } else { // Device is already On/Off

                                console.log('Device traits isOn is already ', deviceTraitsIsOn);
                                fulfillmentMsg = sprintf(getRandomValue(strings.richResponse.fulfillmentTurnAlready), paramThingsRooms, paramThingsDevices, paramThingsTraits);
                                
                                if(screenOutput) {

                                    const richResponseBuild = assistant.buildRichResponse()
                                        .addSimpleResponse(fulfillmentMsg)
                                        .addSuggestions(deviceTraitsIsOn ? strings.richResponse.suggestions.turnOff : strings.richResponse.suggestions.turnOn);
                
                                    assistant.ask(richResponseBuild);
                                } else 
                                    assistant.ask(fulfillmentMsg);
                            }
                        }
                    });
                }
            });
        });
    }
});

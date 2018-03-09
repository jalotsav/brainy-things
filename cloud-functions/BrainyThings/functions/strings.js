// eslint-disable-next-line quotes
const deepFreeze = require('deep-freeze');

const dialogflowConsole = {
    "action": {
        "thingsExecute":"things.execute"
    },
    "parameter": {
        "thingsRooms":"things-rooms",
        "thingsDevices":"things-devices",
        "thingsTraits":"things-traits"
    }
}

const richResponse = {
    // Used to generate RichResponse with image card 
    "images": {
        "lightOn": [
            "https://firebasestorage.googleapis.com/v0/b/brainy-things.appspot.com/o/images%2Flight_bulb_on.png?alt=media&token=b7b757d0-f486-4a2e-ba1c-916e4a70a7f4",
            "Light On"
        ],
        "lightOff": [
            "https://firebasestorage.googleapis.com/v0/b/brainy-things.appspot.com/o/images%2Flight_bulb_off.png?alt=media&token=aab7a696-ef64-4a03-bc2a-2c83331ce4e5",
            "Light Off"
        ]
    },
    // Used to give responses for: Turning the room light on/off
    "fulfillmentTurning" : [
        "Sure, Turning the %1$s room %2$s %3$s",
        "You got it, Turning the %1$s room %2$s %3$s",
        "Here you go, Turning the %1$s room %2$s %3$s",
        "Woohoo, %1$s room %2$s is now %3$s"
    ],
    // Used to give responses for: Already light is turn on/off
    "fulfillmentTurnAlready" : [
        "%1$s room %2$s is already %3$s",
        "Ohh, %1$s room %2$s is already %3$s. I'm smart but not more than you.",
        "Sorry, but i'm not able to do this because the %1$s room %2$s is already %3$s"
    ],
    "suggestions": {
        "turnOn":"Turn on",
        "turnOff":"Turn off"
    }
}

const general = {
    "unhandled" : "Sorry, I didn't understand. What you want to do? Like Turn on or off the living room light.",
    "cardTitleTurning" : "Turning %s",
    "onSml" : "on",
    "offSml" : "off",
    // Firebase database Root, Childs, Values
    "fireDatabase" : {
        "rootName" : "/jalotsavHome",
        "childRooms" : "rooms",
        "childDevices" : "devices",
        "childTraits" : "traits",
        "childIsOn" : "isOn",
        "roomType" : {
            "living" : "LIVING",
            "bed" : "BED"
        },
        "deviceType" : {
            "light" : "LIGHT"
        }
    }
}

// Use deepFreeze to make the constant objects immutable so they are not unintentionally modified
module.exports = deepFreeze({
    dialogflowConsole,
    richResponse,
    general
  });
/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Notify Me When
 *
 *  Author: SmartThings
 *  Date: 2013-03-20
 *
 * Change Log:
 *	1. Todd Wackford
 *	2014-10-03:	Added capability.button device picker and button.pushed event subscription. For Doorbell.
 */
definition(
    name: "Notify Me When",
    namespace: "okpowerman",
    author: "okpowerman",
    description: "Get a push notification or text message when any of a variety of SmartThings is activated.  Supports button push, motion, contact, acceleration, moisture and presence sensors as well as switches.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact@2x.png"
)

preferences {
	section("Choose one or more, when..."){
		input "button", "capability.button", title: "Button Pushed", required: false, multiple: true //tw
        input "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true
		input "contact", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true
		input "contactClosed", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true
		input "acceleration", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true
		input "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true
		input "mySwitchOff", "capability.switch", title: "Switch Turned Off", required: false, multiple: true
		input "arrivalPresence", "capability.presenceSensor", title: "Arrival Of", required: false, multiple: true
		input "departurePresence", "capability.presenceSensor", title: "Departure Of", required: false, multiple: true
		input "smoke", "capability.smokeDetector", title: "Smoke Detected", required: false, multiple: true
		input "water", "capability.waterSensor", title: "Water Sensor Wet", required: false, multiple: true
        input "flameSensor", "capability.switch",  title: "Flame sensor from arduino:", required: false, multiple: false
        input "flameSensorDebug", "capability.switch",  title: "(DEBUG) Flame sensor from arduino:", required: false, multiple: false
        input "esp8266error", "capability.switch", title: "PHANT data error messages", required: false, multiple: true
	}
	section("Send this message (optional, sends standard status message if not specified)"){
		input "messageText", "text", title: "Message Text", required: false
        input "messageFlame", "text", title: "Flame message", required: false
	}
	section("Via a push notification and/or an SMS message"){
        input("recipients", "contact", title: "Send notifications to") {
            input "phone", "phone", title: "Phone Number (for SMS, optional)", required: false
            input "pushAndPhone", "enum", title: "Both Push and SMS?", required: false, options: ["Yes", "No"]
        }
	}
	section("Minimum time between messages (optional, defaults to every message)") {
		input "frequency", "decimal", title: "Minutes", required: false
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribeToEvents()
}

def updated() {
	log.debug "Notify Me When: Updated with settings: ${settings}"
	unsubscribe()
	subscribeToEvents()
}

def subscribeToEvents() {
	subscribe(button, "button.pushed", eventHandler) //tw
    subscribe(contact, "contact.open", eventHandler)
	subscribe(contactClosed, "contact.closed", eventHandler)
	subscribe(acceleration, "acceleration.active", eventHandler)
	subscribe(motion, "motion.active", eventHandler)
	subscribe(mySwitch, "switch.on", eventHandler)
	subscribe(mySwitchOff, "switch.off", eventHandler)
	subscribe(arrivalPresence, "presence.present", eventHandler)
	subscribe(departurePresence, "presence.not present", eventHandler)
	subscribe(smoke, "smoke.detected", eventHandler)
	subscribe(smoke, "smoke.tested", eventHandler)
	subscribe(smoke, "carbonMonoxide.detected", eventHandler)
	subscribe(water, "water.wet", eventHandler)
    subscribe(flameSensor, "flameSensorState.off", eventHandlerFlame)
    subscribe(flameSensorDebug, "flameSensorState", eventHandlerFlame)
    subscribe(esp8266error, "switch.error", esp8266errorHandler)
}

def eventHandler(evt) {
	log.debug "Notify got evt ${evt}"
	if (frequency) {
		def lastTime = state[evt.deviceId]
		if (lastTime == null || now() - lastTime >= frequency * 60000) {
			sendMessage(evt)
		}
	}
	else {
		sendMessage(evt)
	}
}

private sendMessage(evt) {
	def msg = messageText ?: defaultText(evt)
	log.debug "$evt.name:$evt.value, pushAndPhone:$pushAndPhone, '$msg'"
    

    if (location.contactBookEnabled) {
        sendNotificationToContacts(msg, recipients)
    }
    else {

        if (!phone || pushAndPhone != "No") {
            log.debug "sending push"
            sendPush(msg)
        }
        if (phone) {
            log.debug "sending SMS"
            sendSms(phone, msg)
        }
    }
	if (frequency) {
		state[evt.deviceId] = now()
	}
}

private defaultText(evt) {
	if (evt.name == "presence") {
		if (evt.value == "present") {
			if (includeArticle) {
				"$evt.linkText has arrived at the $location.name"
			}
			else {
				"$evt.linkText has arrived at $location.name"
			}
		}
		else {
			if (includeArticle) {
				"$evt.linkText has left the $location.name"
			}
			else {
				"$evt.linkText has left $location.name"
			}
		}
	}
	else {
		evt.descriptionText
	}
}

private getIncludeArticle() {
	def name = location.name.toLowerCase()
	def segs = name.split(" ")
	!(["work","home"].contains(name) || (segs.size() > 1 && (["the","my","a","an"].contains(segs[0]) || segs[0].endsWith("'s"))))
}
def eventHandlerFlame(evt) {
	log.debug "Notify got flame evt ${evt}"
	if (frequency) {
		def lastTime = state[evt.deviceId]
		if (lastTime == null || now() - lastTime >= frequency * 60000) {
			sendMessageFlame(evt)
		}
	}
	else {
		sendMessageFlame(evt)
	}
}
def esp8266errorHandler(evt) {
	log.debug "Notify got ESP8266 evt ${evt}"
	if (frequency) {
		def lastTime = state[evt.deviceId]
		if (lastTime == null || now() - lastTime >= frequency * 60000) {
			sendMessageESP8266(evt)
		}
	}
	else {
		sendMessageESP8266(evt)
	}

}

private sendMessageFlame(evt) {
	def msg = messageFlame+". Pilot is: $evt.value. Sensor reads: ${flameSensor.currentValue("flameSensor")}" ?: defaultText(evt)
	//log.debug "$evt.name:$evt.value, pushAndPhone:$pushAndPhone, '$msg'"
    

    if (location.contactBookEnabled) {
        sendNotificationToContacts(msg, recipients)
    }
    else {

        if (!phone || pushAndPhone != "No") {
            log.debug "sending push"
            sendPush(msg)
        }
        if (phone) {
            log.debug "sending SMS"
            sendSms(phone, msg)
        }
    }
	if (frequency) {
		state[evt.deviceId] = now()
	}
}

private sendMessageESP8266(evt) {
	def msg = "ESP8266 id: ${evt.displayName} state is: $evt.value."
	//log.debug "$evt.name:$evt.value, pushAndPhone:$pushAndPhone, '$msg'"
    

    if (location.contactBookEnabled) {
        sendNotificationToContacts(msg, recipients)
    }
    else {

        if (!phone || pushAndPhone != "No") {
            log.debug "sending push"
            sendPush(msg)
        }
        if (phone) {
            log.debug "sending SMS"
            sendSms(phone, msg)
        }
    }
	if (frequency) {
		state[evt.deviceId] = now()
	}
}
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
 *  Author: SmartThings, Gecko, and Okpowerman
 *  Date: 2013-03-20
 *
 * Change Log:
 *	1. Todd Wackford
 *	2014-10-03:	Added capability.button device picker and button.pushed event subscription. For Doorbell.
 */
definition(
    name: "IFTTT Reschedule",
    namespace: "okpowerman",
    author: "okpowerman",
    description: "Reschedule Pollster when triggered by IFTTT",
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
	}
    /*
	section("Send this message (optional, sends standard status message if not specified)"){
		input "messageText", "text", title: "Message Text", required: false
	}
	section("Via a push notification and/or an SMS message"){
        input("recipients", "contact", title: "Send notifications to") {
            input "phone", "phone", title: "Phone Number (for SMS, optional)", required: false
            input "pushAndPhone", "enum", title: "Both Push and SMS?", required: false, options: ["Yes", "No"]
        }
	}
    */
	section("Minimum time between updates (optional, defaults to every message)") {
		input "frequency", "decimal", title: "Minutes", required: false
	}
    
    for (int n = 1; n <= 4; n++) {
        section("Polling Group ${n}") {
            input "group_${n}", "capability.polling", title:"Select devices to be polled", multiple:true, required:false
            input "refresh_${n}", "capability.refresh", title:"Select devices to be refreshed", multiple:true, required:false
            input "interval_${n}", "number", title:"Set polling interval (in minutes)", defaultValue:5
            input "interval_s${n}", "seconds", title:"Set polling interval (in seconds)", defaultValue:0
        }
    }
    
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribeToEvents()
    initialize()

}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
    unschedule()
	subscribeToEvents()
 	initialize()
}

def subscribeToEvents() {
	subscribe(mySwitch, "switch.on", eventHandler)
}

def eventHandler(evt) {
	log.debug "Notify got evt ${evt} with f: ${frequency}"
	if (frequency) {
		def lastTime = state[evt.deviceId]
		if (lastTime == null || now() - lastTime >= frequency * 60000) {
        	IFTTTupdate()
			//pollingTask1()
            //log.debug "IFTTT rescheduled F"
		}
	}
	else {
    	IFTTTupdate()
		//pollingTask1()
       // log.debug "IFTTT rescheduled no F"
	}
    mySwitch.off()
    
}
/*
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
*/
def pollingTask1() {
    log.trace "IFTTT Task1"

    if (settings.group_1) {
        settings.group_1*.poll()
    }

    if (settings.refresh_1) {
        settings.refresh_1*.refresh()
    }
}

def pollingTask2() {
    log.trace "IFTTT Task2"

    if (settings.group_2) {
        settings.group_2*.poll()
    }

    if (settings.refresh_2) {
        settings.refresh_2*.refresh()
    }
}

def pollingTask3() {
    log.trace "IFTTT Task3"

    if (settings.group_3) {
        settings.group_3*.poll()
    }

    if (settings.refresh_3) {
        settings.refresh_3*.refresh()
    }
}

def pollingTask4() {
    log.trace "IFTTT Task4"

    if (settings.group_4) {
        settings.group_4*.poll()
    }

    if (settings.refresh_4) {
        settings.refresh_4*.refresh()
    }
}

private def initialize() {
    log.debug "initialize() with settings: ${settings}"

    for (int n = 1; n <= 4; n++) {
    	def seconds = settings."interval_s${n}".toInteger()
        def minutes = settings."interval_${n}".toInteger()
        def size1 = settings["group_${n}"]?.size() ?: 0
        def size2 = settings["refresh_${n}"]?.size() ?: 0

        if (minutes > 0 && (size1 + size2) > 0) {
            def sched = "${seconds} 0/${minutes} * * * ?"
            switch (n) {
            case 1:
                schedule(sched, pollingTask1)
                break;
            case 2:
                schedule(sched, pollingTask2)
                break;
            case 3:
                schedule(sched, pollingTask3)
                break;
            case 4:
                schedule(sched, pollingTask4)
                break;
            }
        }
    }

}
def IFTTTupdate() {
	log.debug "IFTTT with settings: ${settings}"
    unschedule()
 	initialize()
}
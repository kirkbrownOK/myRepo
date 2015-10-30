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
 *  Turn It On For 5 Minutes
 *  Turn on a switch when a contact sensor opens and then turn it back off 5 minutes later.
 *
 *  Author: SmartThings
 */
definition(
    name: "Turn the Fan On For X Minutes",
    namespace: "okpowerman",
    author: "okpwerman", //Originally by smartthings
    description: "When a button tile is triggered by IFTTT turn on the fan for X minutes",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section("When it is triggered..."){
		input "switch1", "capability.switch"
	}
	section("Turn on a HVAC Fan for X minutes..."){
		input "thermostat1", "capability.thermostat"
	}
    section("For X minutes") {
    	input "duration", "number"
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe(switch1, "switch.on", switchOnHandler)
}

def updated(settings) {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	subscribe(switch1, "switch.on", switchOnHandler)
}

def switchOnHandler(evt) {
	log.debug "Switch on ${evt}"
	thermostat1.fanOn()
    log.debug "Fan turned on"
	def timeDelay = 60 * duration
	runIn(timeDelay, turnOffSwitch)
}

def turnOffSwitch() {
	log.debug "Duration passed"
	thermostat1.fanAuto()
    log.debug "auto Done"
}
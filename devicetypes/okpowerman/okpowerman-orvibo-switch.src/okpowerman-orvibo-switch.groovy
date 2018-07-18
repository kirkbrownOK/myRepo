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
 *  On/Off Button Tile
 *
 *  Author: SmartThings
 *
 *  Date: 2013-05-01
 */
metadata {
	definition (name: "Okpowerman Orvibo Switch", namespace: "okpowerman", author: "SmartThings") {
		capability "Actuator"
		capability "Switch"
		capability "Sensor"
        
        command "arduinoOn"
        command "arduinoOff"
        command "refreshArd"
        attribute "switchPsu", "string"
	}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles {
		standardTile("button", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "on"
			state "on", label: 'On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "off"
		}
        standardTile("turnOn", "device.switch", width: 1, height: 1, decoration: "flat") {
			state "default", label: "On", backgroundColor: "#ffffff", action: "on"
		} 
 		standardTile("turnOff", "device.switch", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Off", backgroundColor: "#ffffff", action: "off"
		}
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refreshArd", icon:"st.secondary.refresh"
        }
		main "button"
		details "button","turnOn","turnOff","refresh"
	}
}

def parse(String description) {
}

def on() {
	sendEvent(name: "switchPsu", value: "on")
    TRACE("Send Psu ON")
}

def off() {
	sendEvent(name: "switchPsu", value: "off")
    TRACE("Send Psu OFF")
}
def arduinoOn() {
	sendEvent(name: "switch", value: "on", descriptionText: "Arduino")
    TRACE("Send ON")
}

def arduinoOff() {
	sendEvent(name: "switch", value: "off",descriptionText: "Arduino")
    TRACE("Send OFF")
}
def refreshArd() {
	TRACE("Sending Refresh  ARD")
	sendEvent([name: "switchPsu", value: "refresh"])
}

private def TRACE(message) {
    log.debug message
}
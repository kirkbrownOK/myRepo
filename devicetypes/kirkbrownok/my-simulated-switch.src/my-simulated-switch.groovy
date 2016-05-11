/**
 *  Simulated Virtual Switch with refresh and error states
 *
 *  Copyright 2015 Kirk Brown
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
 */
 
import groovy.json.JsonSlurper
preferences {
    input("MinutesOfError","number",title: "Only send error event if in error condition for this many minutes: ", defaultValue:10, required: false)
    
}
metadata {
	definition (name: "My Simulated Switch", namespace:"kirkbrownOK", author:"Kirk Brown") {
		capability "Contact Sensor"
        capability "Switch"
        capability "Refresh"
        capability "Polling" 
        
        command "refreshDem"
        command "refreshRecord"
        command "reset"
        command "error"
        //command "on"
        

	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles { 
        standardTile("refresh", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refreshDem", icon:"st.secondary.refresh"
		}
        standardTile("switch", "device.switch", width: 2, height: 1, inactiveLabel:false) {
				state "on", label: 'ON', action: "off",icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
				state "off", label: 'OFF', action: "on", icon: "st.Appliances.appliances17", backgroundColor: "#FF0000"
                state "error", label: 'Error', action: "refreshDem",  icon: "st.Appliances.appliances17",backgroundColor: "#000000"
                state "refresh", label: 'Waiting for Data', action: "refreshDem", icon: "st.Appliances.appliances17",backgroundColor: "#ffff00"
                state "refresh2", label: 'Waiting for Data', action: "refreshDem", icon: "st.Appliances.appliances17",backgroundColor: "#ffff00"
			}
       	standardTile("switchOn", "device.switch", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'ON', action: "on", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchOff", "device.switch", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'OFF', action: "off", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
		}    
		// TODO: define your main and details tiles here
        main(["switch"])

        details(["switch","refresh","switchOn","switchOff"])
	}
}

// parse events into attributes
def parse(String description) {
    TRACE("parse(${description})")

}

// polling.poll 
def poll() {
    TRACE("poll()")
    return refresh()
}

// refresh.refresh
def refresh() {
    
	log.trace "Not automatically refreshing"
    return    
}
def refreshDem() {
	log.trace "RefreshDEM"
	sendEvent([name: "switch", value: "refresh"])
}
def refreshRecord() {
	log.trace "Arduino sent refreshing command"
    sendEvent([name: "switch", value: "refresh2"])
}
def on() {
	log.trace "ON cmd"
	sendEvent([name: "switch", value: "on"])
}

def off() {
	log.trace "off cmd"
	sendEvent([name: "switch", value: "off"])
}
def reset() {
	off()
    runIn(60,on)
}
def error() {
	log.trace "error cmd"
	sendEvent([name: "switch", value: "error"])
}

private def TRACE(message) {
    log.debug message
}
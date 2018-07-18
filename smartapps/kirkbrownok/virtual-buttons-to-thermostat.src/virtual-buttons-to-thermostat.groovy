/**
 *  Virtual Buttons to Thermostat
 *
 *  Copyright 2016 Kirk Brown
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
definition(
    name: "Virtual Buttons to Thermostat",
    namespace: "kirkbrownOK",
    author: "Kirk Brown",
    description: "This app will watch for virtual buttons and then perform Temp Up and Temp Down on the assigned thermostat",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Choose the Thermostat for Group 1 Modes") {
		input "thermostat1", "capability.thermostat", title: "Which Thermostat?"	
	}
    section("Choose Modes for Group 1 Thermostat") {
    	
		input "group1Modes", "mode", title: "Group1 Modes", multiple: true

    }
    section("Choose the Thermostat for Group 2 Modes") {
		input "thermostat2", "capability.thermostat", title: "Which Thermostat?"	
	}
    section("Choose Modes for Group 2 Thermostat") {
    	
		input "group2Modes", "mode", title: "Group2 Modes", multiple: true

    }
    
    section("Choose the virtual button for Temp Up") {
    	input "buttonUp", "capability.switch", title: "Which button for Temp UP"
    }
    section("Choose the virtual button for Temp Down") {
    	input "buttonDown", "capability.switch", title: "Which button for Temp Down"
    }    
    
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    subscribe(buttonUp, "switch.on", buttonUpHandler)
    subscribe(buttonDown, "switch.on", buttonDownHandler)
}

// TODO: implement event handlers
def buttonUpHandler(evt) {
	log.debug "Calling tempup"
    def devNo = whichGroup()
	
   if (devNo == 1) {
    	log.debug "T1.up"
    	thermostat1.tempUp()
    } else if (devNo == 2) {
        log.debug "T2.Up"
        thermostat2.tempUp()
    }
}

def buttonDownHandler(evt) {
	log.debug "Calling Temp Down"
    def devNo = whichGroup()
    if (devNo == 1) {
    	log.debug "T1.down"
    	thermostat1.tempDown()
    } else if (devNo == 2) {
        log.debug "T2.down"
        thermostat2.tempDown()
    }
}

def whichGroup() {
	log.debug "whichGroup, location.mode = $location.mode"
    log.debug "Group1: ${group1Modes}"
    log.debug "Group2: ${group2Modes}"
	def myMode = location.mode
    if(myMode in group1Modes) {
    	log.debug "In Group1"
        return 1
    }
    else if (myMode in group2Modes) {
    	log.debug "In Group2"
        return 2
    } else {
    	log.debug "Unknown Group"
        return 0
    }
    
}
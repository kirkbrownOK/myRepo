/**
 *  Copyright 2016 OKpowerman
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
 *  Control something when IF turns on a switch that is immediately turned off.
 *
 *  Author: OKpowerman
 *  Date: 2016-8-05
 *
 * Change Log:
 *	1. Todd Wackford
 *	
 */
definition(
    name: "Do something when IF button tile turns on",
    namespace: "okpowerman",
    author: "OKpowerman",
    description: "When IF turns on a simulated button do something",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact@2x.png"
)

preferences {
	section("Choose one or more, when..."){
		input "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true
				
	}
		section("Turn on/off light(s)..."){
		input "switches", "capability.switch", multiple: true
	}
    
    
    
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribeToEvents()

}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	subscribeToEvents()
 
}

def subscribeToEvents() {
	subscribe(mySwitch, "switch.on", eventHandler)
	state.averageState = 0
}

def eventHandler(evt) {
	for (it in (switches)) {
    	//TRACE("state sw: ${it.currentSwitch}")
        if(it.currentSwitch == "off") {
        	state.averageState = state.averageState - 1
        } else if (it.currentSwitch == "on") {
        	state.averageState = state.averageState + 1
        }
    }
    //TRACE("The lights sum: ${state.averageState}")
    if(state.averageState >= 0) {
    	switches.off()
    } else {
    	switches.on()
    }
    state.averageState = 0
    
}
private def TRACE(message) {
    log.debug message
}
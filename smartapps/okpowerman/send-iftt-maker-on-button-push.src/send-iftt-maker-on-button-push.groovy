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
 *  Send IFTTT Maker command when STEPS are updated
 *
 *  Author: Kirk Brown
 *  Date: 2016-01-09
 *
 */
definition(
    name: "Send IFTT Maker on Button Push",
    namespace: "okpowerman",
    author: "okpowerman",
    description: "Send IFTTT Maker update when Steps value changes for Jawbone",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact@2x.png"
)

preferences {
	section("Choose one or more, when..."){
		input "button", "capability.button", title: "Jawbone Step Counter", required: true
        
	}
	section("Send to this Maker event:"){
		input "makerEvent", "text", title: "Maker Event", required: true
        input "makerKey", "text", title: "Maker Key", required: true
	}
    section("Minutes between updates:"){
		input "makerDelay", "number", title: "Delay between messages", required: true, default: 1        
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribeToEvents()
}

def updated() {
	log.debug "IFTTT Send notification Updated with settings: ${settings}"
	unsubscribe()
	subscribeToEvents()
    state.lastSteps = 0
    state.lastTime = now()
    state.lastTime = state.lastTime - makerDelay*60*1000
    log.debug "Receved steps trigger. Last Steps: ${state.lastSteps} Current Steps: ${button.currentValue("steps")} Now: ${now()} State.lastTime: ${state.lastTime}"
}

def subscribeToEvents() {
	subscribe(button, "steps", eventHandler) //tw
    
}

def eventHandler(evt) {
	//log.debug "IFtt got Steps evt ${evt}"
    //Using this value of steps to determine if 0 should not be sent. Its annoying.
    if(button.currentValue("steps") > 0) {
        if (state.lastSteps != button.currentValue("steps")) {
            log.debug "Receved steps trigger. Last Steps: ${state.lastSteps} Current Steps: ${button.currentValue("steps")} Now: ${now()} State.lastTime: ${state.lastTime}"
            if (now() - state.lastTime > makerDelay*60*1000) {
                state.lastSteps = button.currentValue("steps")
                sendMessage(state.lastSteps)
                state.lastTime = now()
            } else{
                log.error "Received updates too close together: Previous steps: ${state.lastSteps} @ ${state.lastTime} Message: ${button.currentValue("steps")} @ ${now()}"
            }

        }
    }
}

private sendMessage(evt) {
		//log.debug "https://maker.ifttt.com/trigger/${makerEvent}/with/key/${makerKey}"
	def params = [
    	uri: "https://maker.ifttt.com",
        path: "/trigger/${makerEvent}/with/key/${makerKey}",
        query: ["value1":"${evt}"]    	
	]
        log.debug "${params}"

	try {
   	 httpGet(params) { 

        }
	} catch (e) {
    	log.error "something went wrong: $e"
	}
}
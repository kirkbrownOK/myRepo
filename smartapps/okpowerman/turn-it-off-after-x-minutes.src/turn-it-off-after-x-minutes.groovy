/**
 *  Copyright 2015 OKpowerman
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
 *  When a light gets turned on, turn it off after X minutes
 *  Turn on a switch when a contact sensor opens and then turn it back off 5 minutes later.
 *
 *  Author: SmartThings
 */
definition(
    name: "Turn It Off After X Minutes",
    namespace: "okpowerman",
    author: "Okpowerman",
    description: "After a light comes on, turn it off after X minutes",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	section("When it turns on..."){
		input "switch1", "capability.switch"
	}
	section("Turn off after..."){
		input "duration", "number"
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribe(switch1, "switch", switchHandler)
}

def updated() {
	unsubscribe()
    if(state.scheduled) { 
    	unschedule() 
        log.debug "Unscheduled in update"
    }
    log.debug "subscribing to: ${switch1}"
	subscribe(switch1, "switch", switchHandler)
}

def switchHandler(evt) {
        log.info "evt: ${evt.value}"
        if(evt.value == "on") {
        	runIn(duration*60, turnOffSwitch)
            state.scheduled = true
            log.info "Turn off ${switch1} in ${duration} minutes"
        } else if (evt.value == "off") {
             if(state.scheduled) {
                   unschedule();
                   state.scheduled = false
                   log.info "${switch1} turned off so schedule cancelled"
             }
        }
}

def turnOffSwitch() {
	switch1.off()
}
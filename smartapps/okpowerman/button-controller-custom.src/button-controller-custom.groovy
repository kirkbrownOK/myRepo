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
 *  Turn It On When It Opens
 *
 *  Author: SmartThings
 */
definition(
    name: "Button Controller Custom",
    namespace: "okpowerman",
    author: "okpowerman",
    description: "Turn something on when my remote button is pressed.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_contact-outlet@2x.png"
)

preferences {
	page (name: "configApp")

}
def configApp() {
	dynamicPage(name: "configApp", install: true, uninstall: true){
	section("When the remote is pressed..."){
		input "remote1", "capability.switch", title: "Which Remote Switches?"
	}
	section("Switch Group 1..."){
		input "switches1", "capability.switch", multiple: true
	}
    section("Switch Group 2..."){
		input "switches2", "capability.switch", multiple: true, required: false
	}
    	section("Switch Group 3..."){
		input "switches3", "capability.switch", multiple: true, required: false
	}
	section("Button 4 Set Mode to...") {
		input(name: "completionMode", type: "mode", title: "Change home Mode To", description: null, required: false)
		//input(name: "completionPhrase", type: "enum", title: "Execute The Phrase", description: null, required: false, multiple: false)
	} 
    def phrases = location.helloHome?.getPhrases()*.label
		if (phrases) {
        	phrases.sort()
			section("Hello Home Actions") {
				log.trace phrases
				input "button5A", "enum", title: "Toggle 1 for Button 5", required: true, options: phrases,  refreshAfterSelection:true
				input "button5B", "enum", title: "Toggle 2 for Button 5", required: true, options: phrases,  refreshAfterSelection:true
			}
        }
   }
}

def installed()
{
	subscribe(remote1, "switch.1", remoteHandler)
    subscribe(remote1, "switch.2", remoteHandler)
    subscribe(remote1, "switch.3", remoteHandler)
    subscribe(remote1, "switch.4", remoteHandler)
    subscribe(remote1, "switch.5", remoteHandler)
}

def updated()
{
	unsubscribe()
    subscribe(remote1, "switch", remoteHandler)
	subscribe(remote1, "switch.1", remoteHandler)
    subscribe(remote1, "switch.2", remoteHandler)
    subscribe(remote1, "switch.3", remoteHandler)
    subscribe(remote1, "switch.4", remoteHandler)
    subscribe(remote1, "switch.5", remoteHandler)
    
}

def remoteHandler(evt) {
	
	log.debug "The value is ${evt.description} ${evt.value}"
    if (evt.value == "0") {
    	log.debug "Do Nothing, not a remote event"
    } else if (evt.value == "1") {
    	for (it in switches1) {
			if (it.currentSwitch == "off") {
            	state.toggledOn = 1
            	switches1.on()
				break
			} else {
       	    	log.debug "Not on" 
                state.toggledOn = 0
            }
                        
		}
        if (state.toggledOn == 0) {
        	switches1.off()
        
        }
        
    } else if (evt.value == "2") {
    	for (it in switches2) {
			if (it.currentSwitch == "off") {
            	state.toggledOn = 1
            	switches2.on()
				break
			} else {
       	    	log.debug "Not on" 
                state.toggledOn = 0
            }
                        
		}
        if (state.toggledOn == 0) {
        	switches2.off()
        
        }
        
    } else if (evt.value == "3") {
    	for (it in switches3) {
			if (it.currentSwitch == "off") {
            	switches3.setLevel(99)
            	state.toggledOn = 1
            	switches3.on()
				break
			} else {
       	    	log.debug "Not on" 
                state.toggledOn = 0
            }
                        
		}
        if (state.toggledOn == 0) {
        	switches3.off()
        
        }
        
    } else if (evt.value == "4") {
        if (location.mode ==completionMode) {
        	log.debug "Waking up from Nap"
            setLocationMode("Home")
            sendNotificationEvent("Setting Home to Home because ${master} button 4 was pressed.")
        } else {
            setLocationMode(completionMode)
            sendNotificationEvent("Setting Home to ${completionMode} because button 4 was pressed.")
        }
        
        
    } else if (evt.value == "5") {
        if ((location.mode =="Night" )|| (location.mode== "Napping")) {
        	log.debug "Night Time already"
            location.helloHome.execute(button5A)
            sendNotificationEvent("${location} executing ${button5A} because button 5 was pressed.")
        } else if( location.mode =="Home") {
            location.helloHome.execute(button5B)
            sendNotificationEvent("${location} executing ${button5B} because button 5 was pressed.")
        }
        
        
    }
    
    
    
    else{
    	log.debug "Not detected"
    }
    //if switches.state() 
	//switches.on()
}


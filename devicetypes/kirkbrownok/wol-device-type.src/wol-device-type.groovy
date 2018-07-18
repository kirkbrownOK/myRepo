/**
 *  WOL Device Type
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
preferences{ 
	input("myMAC","string",title: "MAC Address", defaultValue:"NO : or -", required: true)

}
metadata {
	definition (name: "WOL Device Type", namespace: "kirkbrownOK", author: "Kirk Brown") {
		capability "Polling"
		capability "Refresh"
		capability "Switch"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
        standardTile("switch", "device.switch", width: 1, height: 1, inactiveLabel:false) {
			state "on", label:'${name}', action:"switch.on", icon:"st.switches.switch.on"
            }
         standardTile("switchOff", "device.switch", width: 1, height: 1, inactiveLabel:false) {
			state "off", label:'${name}', action:"switch.off", icon:"st.switches.switch.off"
            
		}
		// TODO: define your main and details tiles here
        main(["switch"])

        details(["switch","switchOff"])
        // TODO: define your main and details tiles here
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute

}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
    
	// TODO: handle 'poll' command
}

def refresh() {
	log.debug "Executing 'refresh'"
    poll()
	// TODO: handle 'refresh' command
}

def on() {
	log.debug "Executing 'on'"
    log.debug "wake on lan ${myMAC}"
    def result = new physicalgraph.device.HubAction (
        "wake on lan ${myMAC}",
        physicalgraph.device.Protocol.LAN,
        null,
        //[secureCode: "111122223333"]
    )
    return result
	// TODO: handle 'on' command
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}
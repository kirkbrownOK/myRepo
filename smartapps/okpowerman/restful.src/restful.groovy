/**
 *  Kirks RESTful App
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
definition(
    name: "RESTful",
    namespace: "okpowerman",
    author: "Kirk Brown",
    description: "Testing the SmartThings Web Services",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName: "OKpowermanTesting", displayLink: ""])

preferences {
  section("Allow External Service to Control These Things...") {
    input "switches", "capability.switch", title: "Which Switches?", multiple: true, required: false
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
    log.debug "switches: ${switches}"
    log.debug "app id: ${app.id}"
    log.debug "base url: ${getServerUrl()}"
    log.debug "Complete url: ${getCompleteUrl()}"
    
}

// TODO: implement event handlers

mappings {
  path("/switches") {
    action: [
      GET: "listSwitches",
      PUT: "updateSwitches",
      POST: "updateSwitches"
      
    ]
  }
  path("/switches/:device/:command") {
    action: [
      GET: "URIupdateSwitches"
    ]
  }

}

void updateSwitches() {
    // use the built-in request object to get the command parameter
    //def command = params.command
    try {
    	log.debug "request: ${request}"
        log.debug "params: ${params} ${params.JSON?.value}"
        log.debug "request json ${request.data}"
        def command = request.JSON?.value
        //def index = command.toFloat()
        log.debug "${location.mode} Switches: ${switches}"
        def counter = 0
        counter = 0
        log.debug "Message: ${command}"
        switches."$command"()
//        for (it in (switches)) {
//            if (command.toFloat() == counter) {
//               log.debug "command: $command counter: $counter on switch $it"
//                it.on()
//            }
            //log.debug "$counter $it in switches" 
//            counter ++
//         }
     } catch (e) {
     	log.debug e
     }
    //log.debug command
 /*   if (command) {

        // check that the switch supports the specified command
        // If not, return an error using httpError, providing a HTTP status code.
        switches.each {
            if (!it.hasCommand(command)) {
                httpError(501, "$command is not a valid command for all switches specified")
            }
        }

        // all switches have the comand
        // execute the command on all switches
        // (note we can do this on the array - the command will be invoked on every element
        switches."$command"()
    }
    */
    //listSwitches()
}
def URIupdateSwitches() {
    // use the built-in request object to get the command parameter
    log.debug "URI Params: ${params}"
    def command = params.command
    def device = params.device
    try {
    	log.debug "URI request: ${request}"
        log.debug "${location.mode} Switches: ${switches}"
        def counter = 0
        counter = 0
        log.debug "Message: ${command}"
//        switches."$command"()
        for (it in (switches)) {
           if (it.displayName == device) {
              log.debug "Device: $device and cmd is $command"
                it."$command"()
            }
            else{
            	log.debug "Device $device did not match. cmd is $command"
                
            }
            //log.debug "$counter $it in switches" 
//            counter ++
         }
     } catch (e) {
     	log.debug e
     }
    //log.debug command
 /*   if (command) {

        // check that the switch supports the specified command
        // If not, return an error using httpError, providing a HTTP status code.
        switches.each {
            if (!it.hasCommand(command)) {
                httpError(501, "$command is not a valid command for all switches specified")
            }
        }

        // all switches have the comand
        // execute the command on all switches
        // (note we can do this on the array - the command will be invoked on every element
        switches."$command"()
    }
    */
    return listSwitches()
}
// returns a list like
// [[name: "kitchen lamp", value: "off"], [name: "bathroom", value: "on"]]
def listSwitches() {
	log.debug "ListSwitches Today"
    def resp = []
    switches.each {
      resp << [name: it.displayName, value: it.currentValue("switch")]
    }
    return resp
}

def notification() {
	log.debug "notify"   
    def resp = ["ok"]
    return resp

}

def getServerUrl()           { return "https://graph.api.smartthings.com" }
def getCompleteUrl()	{ return "${getServerUrl()}/api/smartapps/installations/$app.id" }
private def TRACE(message) {
    log.debug message
}
/**
 *  OG&E GET PRICE from website
 *
 *  
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
include 'asynchttp_v1'
preferences {
	//Nothing to do here    
}
metadata {
	definition (name: "OG&E PRICE ASYNC Device", namespace:"kirkbrownOK", author:"Kirk Brown") {
        capability "Switch"
        capability "Refresh"
        capability "Polling" 
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles { 
        standardTile("refresh", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("switch", "device.switch", width: 3, height: 3, inactiveLabel:false) {
			state "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.off", backgroundColor:"#79b821", nextState:"turningOff"
            state "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.on", backgroundColor:"#ffffff", nextState:"turningOn"
            state "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.off", backgroundColor:"#ffff00", nextState:"error"
            state "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.on", backgroundColor:"#ff8000", nextState:"error"
            state "error", label:'${name}', icon:"st.switches.switch.off", action: "refresh" , backgroundColor:"#ff0000"
		}
        standardTile("switchOn", "device.switch", width: 1, height: 1, inactiveLabel:false) {
			state "on", label:'${name}', action:"switch.on", icon:"st.switches.switch.on"
            }
         standardTile("switchOff", "device.switch", width: 1, height: 1, inactiveLabel:false) {
			state "off", label:'${name}', action:"switch.off", icon:"st.switches.switch.off"
            
		}
		// TODO: define your main and details tiles here
        main(["switch"])

        details(["switch","switchOn","switchOff","refresh"])
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
	log.debug "Attempting Refresh"
    def params = [
        //uri: "216.99.20.72:49153",
        uri: "https://secure.oge.com",
        path: "/OK_PriceSignal/"
    ]
	log.debug "${params}"
    
    asynchttp_v1.get(processResponse,params)
    //return    
}
def processResponse(response, data) {
	TRACE("received Resp: $data")
    state.myResponse = response
    state.myData = data
    if (!response.hasError()) {
        try {
            def xml = response.xml
        } catch(e) {
            log.warn "could not parse body to XML"
        }
        TRACE("XML RESP: $xml")
    } else {
        log.error "response has error: ${response.getErrorMessage()}"

    }
}
def on() {
	log.debug "ON"
    sendEvent([name: "switch", value: "turningOn"])
    log.debug "Attempting to turn ON"
    def params = [
        //uri: "216.99.20.72:49153",
        uri: "${myIPaddress}:${state.port}",
        path: "/upnp/control/basicevent1",
        //uri: "http://requestb.in:41953",
        //path: "/1jpgmoq1",
        headers: [ "Content-Type": "text/xml; charset=\"utf-8\"", "Connection": "keep-alive","SOAPACTION":"\"urn:Belkin:service:basicevent:1#SetBinaryState\"","Host":"192.168.1.7"],
        body: "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:SetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\"><BinaryState>1</BinaryState></u:SetBinaryState></s:Body></s:Envelope>"
    ]
	//log.debug "${params}"
    try {
        httpPost(params) { resp ->
        	//log.debug "${resp}"
            //resp.headers.each {
            //	log.debug "${it.name} : ${it.value}"
        	//}
            log.debug "response data: ${resp.data}"
            if(resp.data == 1) sendEvent([name: "switch", value: "on"])
            if(resp.data == 0) sendEvent([name: "switch", value: "off"])
            if(resp.data == "Error") {
                sendEvent([name: "switch", value: "error"])
                TRACE("Refreshing")
				return refresh()
        	}
        }
    } catch (e) {
        log.error "something went wrong: $e"
    }
    
}

def off() {
	log.debug "OFF"
	sendEvent([name: "switch", value: "turningOff"])
    log.debug "Attempting to turn OFF"
    def params = [
        //uri: "216.99.20.72:49153",
        uri: "${myIPaddress}:${state.port}",
        path: "/upnp/control/basicevent1",
        //uri: "http://requestb.in:41953",
        //path: "/1jpgmoq1",
        headers: [ "Content-Type": "text/xml; charset=\"utf-8\"", "Connection": "keep-alive","SOAPACTION":"\"urn:Belkin:service:basicevent:1#SetBinaryState\"","Host":"192.168.1.7"],
        body: "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:SetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\"><BinaryState>0</BinaryState></u:SetBinaryState></s:Body></s:Envelope>"
    ]
	//log.debug "${params}"
    try {
        httpPost(params) { resp ->
			//log.debug "${resp}"
            //resp.headers.each {
            //	log.debug "${it.name} : ${it.value}"
        	//}
            if(resp.data == 1) sendEvent([name: "switch", value: "on"])
            if(resp.data == 0) sendEvent([name: "switch", value: "off"])
            if(resp.data == "Error") {
                sendEvent([name: "switch", value: "error"])
                TRACE("Refreshing")
				return refresh()
        	}
        }
    } catch (e) {
        log.error "something went wrong: $e"
    }
}
def reset() {
	log.debug "Cameras been reset"
    off()
    runIn(60, on)
}

private def TRACE(message) {
    log.debug message
}
private def checkPort() {
	//state.port uses the active port.
    //if the wemo doesn't answer on one port it will try the next
    if(state.lastMessage == "success") return
    
    if(EndPort > 0 ) {
    	TRACE("Port was: ${state.port}")
		if(state.port < StartPort) state.port = StartPort
    	else state.port = state.port +1
        if(state.port > EndPort) state.port = StartPort
        TRACE("New Port is: ${state.port}")
        runIn(60,refresh)
    } else {
    	state.port = StartPort
    }   
}
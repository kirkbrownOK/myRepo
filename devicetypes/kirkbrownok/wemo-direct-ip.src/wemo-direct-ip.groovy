/**
 *  Wemo Direct Access VIA IP Address and Port #
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
input("myIPaddress","string",title: "IP Address of the WEMO", defaultValue:"216.99.20.72", required: true)
input("StartPort","number",title: "Port Number:", defaultValue:49154, required: true)
input("EndPort","number",title: "If you enter a port number, the device will scroll the ports between start and end", defaultValue:49155, required: false)

    
}
metadata {
	definition (name: "WeMo Direct IP", namespace:"kirkbrownOK", author:"Kirk Brown") {
	capability "Contact Sensor"
        capability "Switch"
        capability "Refresh"
        capability "Polling" 
        
        command "reset"
		command "on"
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
        uri: "${myIPaddress}:${state.port}",
        path: "/upnp/control/basicevent1",
        //uri: "http://requestb.in:41953",
        //path: "/1jpgmoq1",
        headers: [ "Content-Type": "text/xml; charset=\"utf-8\"", "Connection": "keep-alive","SOAPACTION":"\"urn:Belkin:service:basicevent:1#GetBinaryState\"","Host":"192.168.1.7"],
        body: "<?xml version=\"1.0\" encoding=\"utf-8\"?><s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:GetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\"></u:GetBinaryState></s:Body></s:Envelope>"
    ]
	//log.debug "${params}"
    try {
        httpPost(params) { resp ->
        	//log.debug "${resp}"
            //resp.headers.each {
            //	log.debug "${it.name} : ${it.value}"
        	//}
        //log.debug "response contentType: ${resp.contentType}"
        log.debug "response data: ${resp.data}"
        if(resp.data == 1) sendEvent([name: "switch", value: "on"])
        if(resp.data == 0) sendEvent([name: "switch", value: "off"])
        }
    } catch (e) {
        log.error "something went wrong: $e"
        sendEvent([name: "switch", value: "error"])
        checkPort()
    }    
    return    
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
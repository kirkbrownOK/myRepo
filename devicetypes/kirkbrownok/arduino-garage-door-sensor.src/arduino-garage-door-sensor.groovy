/**
 *  Adapted Filtrete 3M-50 WiFi Thermostat to work with Arduino Ethernet 
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright (c) 2014 geko@statusbits.com
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  --------------------------------------------------------------------------
 *
 */

import groovy.json.JsonSlurper

preferences {
    input "confIpAddr", "string", title:"Arduino IP Address", defaultValue: "192.168.0.115",required:true, displayDuringSetup: true
    input "confTcpPort", "number", title:"TCP Port", defaultValue:"80", required:true, displayDuringSetup:true
	input "minutesBeforeTimeout", "number", title:"How many minutes before considering the delay an error?", defaultValue:"5", required:true, displayDuringSetup:true
    
}

metadata {
    definition (name:"Arduino Garage Door Sensor", namespace:"kirkbrownOK", author:"Kirk Brown") {
    	//Primarily based on author:"geko@statusbits.com" radiothermostat       
        capability "Sensor"
        capability "Refresh"
        capability "Polling"
        capability "Actuator"
		capability "Door Control"
		capability "Garage Door Control"
		capability "Contact Sensor"
        capability "Switch"
        capability "Relay Switch"

        // Custom attributes
		attribute "doorState", "string" // Door state. Values: "unkown", "closed", "open", "closing", "opening"
        attribute "lastReset", "number"
        attribute "failedMsg", "number"
        
        command "resetDevice"
    }

    tiles {
        standardTile("doorState", "device.doorState", width: 3, height: 2) {
			state("unkown", label:'${name}', icon:"st.doors.garage.garage-closed", action: "refresh", backgroundColor:"#ff0000")
            state("closed", label:'${name}', icon:"st.doors.garage.garage-closed", action: "open", backgroundColor:"#02ff1b", nextState:"opening")
			state("open", label:'${name}', icon:"st.doors.garage.garage-open", action: "close", backgroundColor:"#2dedff", nextState:"closing")
			state("opening", label:'${name}', icon:"st.doors.garage.garage-opening", backgroundColor:"#e0aaff")
			state("closing", label:'${name}', icon:"st.doors.garage.garage-closing", action: "refresh", backgroundColor:"#fbff23")         
			
		}
		standardTile("open", "device.doorState", inactiveLabel: false, decoration: "flat") {
			state "default", label:'open', action:"open", icon:"st.doors.garage.garage-opening"
		}
		standardTile("close", "device.doorState", inactiveLabel: false, decoration: "flat") {
			state "default", label:'close', action:"close", icon:"st.doors.garage.garage-closing"
		}
		standardTile("refresh", "device.doorState", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        valueTile("reset", "device.lastReset", inactiveLabel: false, decoration: "flat") {
			state "default", label:'reset: ${currentValue}', action:"resetDevice"
		}
        valueTile("failedMsg", "device.failedMsg", inactiveLabel: false, decoration: "flat") {
			state "default", label:'Failed Messages: ${currentValue}', action:"refresh.refresh"
		}


        main(["doorState"])

        details(["doorState", "refresh",  "open", "close", "contact", "reset", "failedMsg"])
    }

    simulator {
    /*
        status "Temperature 72.0":      "simulator:true, temp:72.00"
        status "Cooling Setpoint 76.0": "simulator:true, t_cool:76.00"
        status "Heating Setpoint 68.0": "simulator:true, t_cool:68.00"
        status "Thermostat Mode Off":   "simulator:true, tmode:0"
        status "Thermostat Mode Heat":  "simulator:true, tmode:1"
        status "Thermostat Mode Cool":  "simulator:true, tmode:2"
        status "Thermostat Mode Auto":  "simulator:true, tmode:3"
        status "Fan Mode Auto":         "simulator:true, fmode:0"
        status "Fan Mode Circulate":    "simulator:true, fmode:1"
        status "Fan Mode On":           "simulator:true, fmode:2"
        status "State Off":             "simulator:true, tstate:0"
        status "State Heat":            "simulator:true, tstate:1"
        status "State Cool":            "simulator:true, tstate:2"
        status "Fan State Off":         "simulator:true, fstate:0"
        status "Fan State On":          "simulator:true, fstate:1"
        status "Hold Disabled":         "simulator:true, hold:0"
        status "Hold Enabled":          "simulator:true, hold:1"
        */
    }
}

def parse(String message) {
    //TRACE("parse(${message})")

    def msg = stringToMap(message)

    if (msg.headers) {
        // parse HTTP response headers
        def headers = new String(msg.headers.decodeBase64())
        def parsedHeaders = parseHttpHeaders(headers)
        //TRACE("parsedHeaders: ${parsedHeaders}")
        if (parsedHeaders.status != 200) {
            log.error "Server error: ${parsedHeaders.reason}"
            return null
        }

        // parse HTTP response body
        if (!msg.body) {
            log.debug "HTTP response has no body"
            return null
        }

        def body = new String(msg.body.decodeBase64())
        def slurper = new JsonSlurper()
        def tstat = slurper.parseText(body)

        return parseTstatData(tstat)
    } else if (msg.containsKey("simulator")) {
        // simulator input
        return parseTstatData(msg)
    }

    return null
}


def on() {
	open()
}

def off() {
	close()
}

// device.open()
def open() {
    TRACE("open()")

    if (device.currentValue("doorState") == "open") {
        return null
    }
	TRACE("sending open()")
    sendEvent([name:"open()", value:"open"])
    return writeTstatValue('doorcmd', 4)
}
// device.close()
def close() {
    TRACE("close()")

    if (device.currentValue("doorState") == "closed") {
        return null
    }
	TRACE("sending Close()")
    sendEvent([name:"close()", value:"closed"])
    return writeTstatValue('doorcmd', 5)
}


// polling.poll 
def poll() {
    TRACE("poll()")
    return refresh()
}

// refresh.refresh
def refresh() {
	
    TRACE("Last Reset Time: ${state.lastTimeReceived} now: ${now()} elapsed: ${now()-state.lastTimeReceived}")
	if ((now() - state.lastTimeReceived)/1000 > (minutesBeforeTimeout * 65 )) {
    	state.failedMessageCount = state.failedMessageCount + 1
    	TRACE("Refresh failed to work for ${(now() - state.lastTimeReceived)/1000}")
    	sendEvent([name:"failedMsg", value: state.failedMessageCount ,descriptionText:"Error Via Timeout"])	
        state.lastTimeReceived = now() 
    }
    setNetworkId(confIpAddr, confTcpPort)
    return apiGet("/garage")
}

// Sets device Network ID in 'AAAAAAAA:PPPP' format
private String setNetworkId(ipaddr, port) { 
    //TRACE("setNetworkId(${ipaddr}, ${port})")

    def hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    def hexPort = String.format('%04X', port.toInteger())
    device.deviceNetworkId = "${hexIp}:${hexPort}"
    //log.debug "device.deviceNetworkId = ${device.deviceNetworkId}"
}

private apiGet(String path) {
    //TRACE("apiGet(${path})")

    def headers = [
        HOST:       "${confIpAddr}:${confTcpPort}",
        Accept:     "*/*"
    ]

    def httpRequest = [
        method:     'GET',
        path:       path,
        headers:    headers
    ]

    return new physicalgraph.device.HubAction(httpRequest)
}

private apiPost(String path, data) {
    //TRACE("apiPost(${path}, ${data})")

    def headers = [
        HOST:       "${confIpAddr}:${confTcpPort}",
        Accept:     "*/*"
    ]

    def httpRequest = [
        method:     'POST',
        path:       path,
        headers:    headers,
        body:       data
    ]

    return new physicalgraph.device.HubAction(httpRequest)
}

private def writeTstatValue(name, value) {
    //TRACE("writeTstatValue(${name}, ${value})")

    setNetworkId(confIpAddr, confTcpPort)

    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPost("/garage", json),
        delayHubAction(5000),        
        
        apiGet("/garage")
    ]

    return hubActions
}

private def delayHubAction(ms) {
    return new physicalgraph.device.HubAction("delay ${ms}")
}

private parseHttpHeaders(String headers) {
    def lines = headers.readLines()
    def status = lines.remove(0).split()

    def result = [
        protocol:   status[0],
        status:     status[1].toInteger(),
        reason:     status[2]
    ]

    return result
}

private def parseTstatData(Map tstat) {
    //TRACE("parseTstatData(${tstat})")

    def events = []
    if (tstat.containsKey("error_msg")) {
        log.error "error: ${tstat.error_msg}"
        return null
    }

    if (tstat.containsKey("success")) {
        // this is POST response - ignore
        return null
    }

	if (tstat.containsKey("doorState")) {
    	//TRACE("Contains doorState: ")
        //TRACE(tstat.doorState)
        def value = parseDoorState(tstat.doorState)
        def ev = [
                name:   "doorState",
                value:  value
            ]

            events << createEvent(ev)
            ev = [
                name:   "contact",
                value:  value
            ]
            events << createEvent(ev)
            if ( tstat.lastReset == device.currentValue("lastReset")) {
            	TRACE("lastReset received but same as last time")
                state.failedMessageCount = state.failedMessageCount + 1
            } else {
            	state.failedMessageCount = 0 //Valid message received
                state.lastTimeReceived = now()
            }
            ev = [ name: "failedMsg", value: state.failedMessageCount, descriptionText: "Determined from Message" ]
            events << createEvent(ev)
            ev = [ name: "lastReset", value: tstat.lastReset ]
            events << createEvent(ev)
        
        
    }
    

    TRACE("events: ${events}")
    return events
}

private def parseDoorState(val) {
    def values = [
        "open",      // 0
        "closed",        // 1
        "opening",	// 2
        "closing",	// 3
        "unknown"   // 4
    ]

    return values[val.toInteger()]
}
def resetDevice() {
	TRACE("RESET CALLED")
    return writeTstatValue('doorcmd', 7)

}


private def TRACE(message) {
    log.debug message
}

private def STATE() {
    log.debug "deviceNetworkId : ${device.deviceNetworkId}"
    log.debug "doorState : ${device.currentValue("doorState")}"
    log.debug "contact : ${device.currentValue("contact")}"
}
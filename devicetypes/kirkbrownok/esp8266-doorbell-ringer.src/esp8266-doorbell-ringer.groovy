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
    input "confIpAddr", "string", title:"Arduino IP Address", defaultValue: "192.168.86.53",required:true, displayDuringSetup: true
    input "confTcpPort", "number", title:"TCP Port", defaultValue:"80", required:true, displayDuringSetup:true
	input "minutesBeforeTimeout", "number", title:"How many minutes before considering the delay an error?", defaultValue:"20", required:true, displayDuringSetup:true
    
}

metadata {
    definition (name:"ESP8266 Doorbell Ringer", namespace:"kirkbrownOK", author:"Kirk Brown") {
        capability "Switch"
        capability "Contact Sensor"
        capability "Polling"
        capability "Refresh"
        capability "Button"
        
        attribute "doorbell", "string"
        attribute "millis", "number"
        attribute "mode", "number"
        
        command "senddoorbell"
        command "smsendmode"
        command "smsenddoorbell"
        command "bellIsNormal"
        command "bellIsSiren"
        command "bellIsStar"
        command "setRepeat"
        command "silence"
        command "soundsEmergency"
        command "soundsNormal"
        command "soundsOff"

        attribute "lastReset", "number"
        attribute "failedMsg", "number"
    }

   tiles {
    	standardTile("switch", "device.switch", width: 1, height: 1) {
			state("on", label:'${name}', icon:"st.switch.switch.on", backgroundColor:"#79b821", action: "off")
			state("off", label:'${name}', icon:"st.switch.switch.off", backgroundColor:"#ffa81e", action: "on")
		}
        valueTile("millis", "device.uptime") {
			state "default", label:'uptime ${currentValue}',action:"refresh"
		}
        standardTile("silent", "device.switch", width: 3, height: 2) {
			state("default", label:'SILENCE', icon:"st.switch.switch.off", backgroundColor:"#79b821", action: "silence")
		}
        standardTile("doorbell", "device.doorbell") {
			state "default", label:'Updating',action:"bellIsNormal", nextState: "normal"
            state "normal", label: 'Normal', action: "bellIsSiren", nextState: "siren"
            state "siren", label: 'Siren', action: "bellIsStar", nextState: "star"
            state "star", label: 'Fairy Tale', action: "bellIsNormal", nextState: "normal"
		}
        standardTile("mode", "device.mode") {
            state "1", label: 'Normal', action: "soundsEmergency", nextState: "2"
            state "2", label: 'Emergency Only', action: "soundsOff", nextState: "3"
            state "3", label: 'NO Sounds', action: "soundsNormal", nextState: "1"
		}
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main "silent"
        details (["mode","silent","doorbell","switch", "millis","refresh"])
    }
}

def parse(String message) {
    TRACE("parse(${message})")

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
        //TRACE("BODY: " + $body)
        def slurper = new JsonSlurper()
        def resp = slurper.parseText(body)

        return parseRespData(resp)
    } else if (msg.containsKey("simulator")) {
        // simulator input
        return parseRespData(msg)
    }

    return null
}
def bellIsNormal() {
	TRACE("BellIsNormal")
    state.bell = 1
    sendEvent([name: "doorbell", value: "normal"])
   	return setRepeat(2)
}
def bellIsSiren() {
	TRACE("BellisSiren")
    state.bell = 3
    state.repeat = 1
    sendEvent([name: "doorbell", value: "siren"])
    return setRepeat(1)
}
def soundsOff() {
	sendEvent([name: "mode", value: "3"])
	return smsendmode(3)
}
def soundsNormal() {
	sendEvent([name: "mode", value: "1"])
	return smsendmode(1)
}
def soundsEmergency() {
	sendEvent([name: "mode", value: "2"])
	return smsendmode(2)
}
def setRepeat(repeatTimes) {
	state.repeat = repeatTimes
    return writeValue("repeat",state.repeat)

}
def smsenddoorbell(rep,bell) {
	state.bell = bell
    def json = "${rep}&doorbell=${state.bell}"
	return writeValue("repeat",json) 

}
def smsendmode(_mode) {
	state.mode = _mode
    return writeValue("mode",_mode)  

}

def bellIsStar() {
	TRACE("BellisStar")
    state.bell = 4
    state.repeat = 1
    sendEvent([name: "doorbell", value: "star"])
    return setRepeat(1)
}
def silence() {
	return senddoorbell(999)
}
def on() {
	if(state.bell>0) {
    
    } else {
    	bellIsNormal()
    }    
	TRACE("SWITCH ON")
    sendEvent([name:"switch", value: "on" ])
    def result = senddoorbell(state.bell)
    off()
    return result
	
}

def off() {
	TRACE("SWITCH OFF")
	sendEvent([name:"switch", value: "off" ])
}


def senddoorbell(bell) {
    TRACE("senddoorbell ${bell}")

    return writeValue("doorbell", bell)
}
// polling.poll 
def poll() {
    TRACE("poll()")
    return refresh()
}

// refresh.refresh
def refresh() {
/*	
    TRACE("Last Reset Time: ${state.lastTimeReceived} now: ${now()} elapsed: ${now()-state.lastTimeReceived}")
	if ((now() - state.lastTimeReceived)/1000 > (minutesBeforeTimeout * 65 )) {
    	state.failedMessageCount = state.failedMessageCount + 1
    	TRACE("Refresh failed to work for ${(now() - state.lastTimeReceived)/1000}")
    	sendEvent([name:"failedMsg", value: state.failedMessageCount ,descriptionText:"Error Via Timeout"])	
        state.lastTimeReceived = now() 
    }*/
    setNetworkId(confIpAddr, confTcpPort)
    return apiGet("/status")
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
	path+="?$data"
    def httpRequest = [
        method:     'POST',
        path:       path,
        headers:    headers,
        
    ]

    return new physicalgraph.device.HubAction(httpRequest)
}

private def writeValue(name, value) {
    //TRACE("writeValue(${name}, ${value})")

    setNetworkId(confIpAddr, confTcpPort)

    def json = "${name}=${value}"
    def hubActions = [
        apiPost("/cmd", json),
        delayHubAction(5000),        
        
        apiGet("/status")
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

private def parseRespData(Map resp) {
    //TRACE("parserespData(${resp})")

    def events = []
    if (resp.containsKey("error_msg")) {
        log.error "error: ${resp.error_msg}"
        return null
    }

    if (resp.containsKey("success")) {
        // this is POST response - ignore
        log.info "This is a POST response- Ignore"
        return null
    }

	if (resp.containsKey("mode")) {
    	//TRACE("Contains doorState: ")
        //TRACE(tstat.doorState)
        
        def ev = [
                name:   "mode",
                value:  resp.mode
            ]

            events << createEvent(ev)
            if ( resp.uptime == device.currentValue("uptime")) {
            	TRACE("uptime received but same as last time")
                if( now() - resp.uptime > 50000) {
                	state.failedMessageCount = state.failedMessageCount + 1
                }
                else {
                	TRACE("Ignoring failed message because its within 50 seconds of the alst one")
                }    
            } else {
            	state.failedMessageCount = 0 //Valid message received
                state.lastTimeReceived = now()
            }
            ev = [ name: "failedMsg", value: state.failedMessageCount]
            events << createEvent(ev)
            ev = [ name: "uptime", value: resp.uptime ]
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


private def TRACE(message) {
    log.debug message
}

private def STATE() {
    log.debug "deviceNetworkId : ${device.deviceNetworkId}"
    log.debug "doorbell : ${device.currentValue("doorbell")}"
    log.debug "mode : ${device.currentValue("mode")}"
}
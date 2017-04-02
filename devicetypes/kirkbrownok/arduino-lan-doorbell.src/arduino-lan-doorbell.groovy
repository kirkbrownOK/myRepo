/**
 *  Arduino LAN DoorBell Device Type Matches Arduino Sketch from 4/1/2017
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

metadata {
    definition (name: "Arduino LAN DoorBell", namespace: "kirkbrownOK", author: "Kirk Brown") {
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
    }

    simulator {
    }
		
    tiles {
    	standardTile("switch", "device.switch", width: 1, height: 1) {
			state("on", label:'${name}', icon:"st.switch.switch.on", backgroundColor:"#79b821", action: "off")
			state("off", label:'${name}', icon:"st.switch.switch.off", backgroundColor:"#ffa81e", action: "on")
		}
        valueTile("millis", "device.millis") {
			state "default", label:'millis ${currentValue}',action:"refresh"
		}
        standardTile("silent", "device.switch", width: 3, height: 2) {
			state("default", label:'SILENCE', icon:"st.switch.switch.off", backgroundColor:"#79b821", action: "silence")
		}
        standardTile("doorbell", "device.doorbell") {
			state "default", label:'Updating',action:"bellIsNormal"
            state "normal", label: 'Normal', action: "bellIsSiren"
            state "siren", label: 'Siren', action: "bellIsStar"
            state "star", label: 'Star Wars', action: "bellIsNormal"
		}
        standardTile("mode", "device.mode") {
            state "1", label: 'Normal', action: "soundsEmergency"
            state "2", label: 'Emergency Only', action: "soundsOff"
            state "3", label: 'NO Sounds', action: "soundsNormal"
		}
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main "silent"
        details (["mode","silent","doorbell","switch", "millis","refresh"])
    }
}

// parse events into attributes
def parse(String description) {
	
    //def usn = getDataValue('ssdpUSN')
    TRACE( "Parsing Arduino DT ${device.deviceNetworkId} '${description}'")

    def parsedEvent = parseDiscoveryMessage(description)
	def events = []
    def ev = []
    if (parsedEvent['body'] != null) {
    	try{
            def xmlText = new String(parsedEvent.body.decodeBase64())
            TRACE("xmlText: ${xmlText}")
            def xmlTop = new XmlSlurper().parseText(xmlText)
            TRACE("xmlTop ${xmlTop}")
            def mode = xmlTop.mode[0]
            def millis = xmlTop.millis[0] //
			
            log.debug "Millis: $millis Mode: $mode"
            def targetUsn = xmlTop.usn[0].toString()
            def childUsn = getDataValue("ssdpUSN").toString()
            if (childUsn == targetUsn) {
            	TRACE( "childUSN Matches TargetUSN")
                try{
                	if(millis.toFloat() > 0 ) {
                    	TRACE( "Updating ${device.label} to ${millis.toFloat()}")
                        log.warn "Sending new millis"
                        sendEvent(name:"millis", value: millis)
                            	
                    }
                } catch(e) {
                        TRACE( "No millis in msg")
                }
                try{
                    if(mode.toFloat() > 0 ) {
                        TRACE( "Updating ${device.label} to ${mode}")
                        log.warn "Sending new mode"
                        sendEvent(name:"mode", value: mode)
                    }
                } catch(e) {
                    TRACE( "No mode in msg")
                }
                    

            } else {
                TRACE( "childUSN ${childUsn} not equal to Target USN ${targetUsn}")
            }
        } catch (e) {
            TRACE("NO XML: Probably a GET response: $e")
        }
    }
    log.debug "EVENTS: ${events}"
    return events
}

private Integer convertHexToInt(hex) {
    Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
    [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
    def ip = getDataValue("ip")
    def port = getDataValue("port")

    if (!ip || !port) {
        def parts = device.deviceNetworkId.split(":")
        if (parts.length == 2) {
            ip = parts[0]
            port = parts[1]
        } else {
            //log.warn "Can't figure out ip and port for device: ${device.id}"
        }
    }

    //convert IP/port
    ip = convertHexToIP(ip)
    port = convertHexToInt(port)
    log.debug "Using ip: ${ip} and port: ${port} for device: ${device.id}"
    return ip + ":" + port
}

def getRequest(path) {
    log.debug "Sending request for ${path} from ${device.deviceNetworkId}"

    def result = new physicalgraph.device.HubAction(
        'method': 'GET',
        'path': path,
        'headers': [
            'HOST': getHostAddress(),
        ], device.deviceNetworkId)
        TRACE("GET: ${result}")
        return result
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
    return writeValue('rpt',state.repeat)

}
def smsenddoorbell(rep,bell) {
	state.bell = bell
    def json = "{\"rpt\": ${rep},\"db\": ${state.bell}}"
    def hubActions = [
        apiPost(getDataValue("ssdpPath"), json)
    ]

    return hubActions   

}
def smsendmode(_mode) {
	state.mode = _mode
    def json = "{\"mode\": ${_mode}}"
    def hubActions = [
        apiPost(getDataValue("ssdpPath"), json)
    ]

    return hubActions   

}

def bellIsStar() {
	TRACE("BellisStar")
    state.bell = 4
    state.repeat = 1
    sendEvent([name: "doorbell", value: "star"])
    return setRepeat(1)
}
def postRequest(path,json) {
    log.debug "Sending request for ${path} message ${json} from ${device.deviceNetworkId}"

    new physicalgraph.device.HubAction(
        'method': 'POST',
        'path': path,
        'headers': [
            'HOST': getHostAddress(),
        ], device.deviceNetworkId)   
}
private apiPost(String path, data) {
    log.debug "apiPost(${path}, ${data})"

    def headers = [
        HOST:       getHostAddress(),
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
private def writeValue(name, value) {
    TRACE("writeValue(${name}, ${value})")

    //setNetworkId(confIpAddr, confTcpPort)

    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPost(getDataValue("ssdpPath"), json)
    ]

    return hubActions
}
def poll() {
    log.debug "Executing 'poll' from ${device.deviceNetworkId} "

    def path = getDataValue("ssdpPath")
    getRequest(path)
}

def refresh() {
    log.debug "Executing 'refresh'"

    def path = getDataValue("ssdpPath")
    getRequest(path)
}
def silence() {
	return senddoorbell(999)
}
def subscribe() {
	state.ssdpPath = getDataValue("ssdpPath")
    log.debug "Subscribe requested\r\n${state.ssdpPath}"
    subscribeAction(getDataValue("ssdpPath"))
}

private def parseDiscoveryMessage(String description) {
	TRACE("In PDM:${description}")
    def device = [:]
    def parts = description.split(',')
    parts.each { part ->
        part = part.trim()
        if (part.startsWith('devicetype:')) {
            def valueString = part.split(":")[1].trim()
            device.devicetype = valueString
        } else if (part.startsWith('mac:')) {
            def valueString = part.split(":")[1].trim()
            if (valueString) {
                device.mac = valueString
            }
        } else if (part.startsWith('networkAddress:')) {
        	
            def valueString = part.split(":")[1].trim()
            TRACE("NET Address ${valueString}")
            if (valueString) {
                device.ip = valueString
            }
        } else if (part.startsWith('deviceAddress:')) {
        	
            def valueString = part.split(":")[1].trim()
            TRACE("Port: ${valueString}")
            if (valueString) {
                device.port = valueString
            }
        } else if (part.startsWith('ssdpPath:')) {
            def valueString = part.split(":")[1].trim()
            if (valueString) {
                device.ssdpPath = valueString
            }
        } else if (part.startsWith('ssdpUSN:')) {
            part -= "ssdpUSN:"
            def valueString = part.trim()
            if (valueString) {
                device.ssdpUSN = valueString
            }
        } else if (part.startsWith('ssdpTerm:')) {
            part -= "ssdpTerm:"
            def valueString = part.trim()
            if (valueString) {
                device.ssdpTerm = valueString
            }
        } else if (part.startsWith('headers')) {
            part -= "headers:"
            def valueString = part.trim()
            if (valueString) {
                device.headers = valueString
                def heads = new String(device.headers.decodeBase64())
                TRACE("headers: \r\n${heads}")
            }
        } else if (part.startsWith('body')) {
            part -= "body:"
            def valueString = part.trim()
            if (valueString) {
                device.body = valueString
                def bods = new String(device.body.decodeBase64())
                TRACE("body: \r\n${bods}")
            }
        }
    }

    device
}

private subscribeAction(path, callbackPath="") {
    def address = device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
    def parts = device.deviceNetworkId.split(":")
    def ip = convertHexToIP(getDataValue("ip"))
    def port = convertHexToInt(getDataValue("port"))
    ip = ip + ":" + port
	TRACE( "<http://${address}/notify${callbackPath}>")
	TRACE( "SUBSCRIBE ${path} ${ip} NT: upnp:event Second-3600")
    def result = new physicalgraph.device.HubAction(
        method: "SUBSCRIBE",
        path: path,
        headers: [
            HOST: ip,
            CALLBACK: "<http://${address}/notify$callbackPath>",
            NT: "upnp:event",
            TIMEOUT: "Second-3600"])
    result
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

    return writeValue('db', bell)
}

private def TRACE(message) {
    log.trace message
}
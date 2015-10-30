/**
 *  Arduino LAN Device Type
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
    definition (name: "Arduino LAN", namespace: "kirkbrownOK", author: "Kirk Brown") {
        capability "Contact Sensor"
        capability "Polling"
        capability "Refresh"
        capability "Temperature Measurement"
        capability "Switch Level"
		capability "Actuator"
		capability "Indicator"
        capability "Temperature Measurement"
		capability "Switch"
        capability "Motion Sensor"
        capability "Button"
        
        attribute "switchC", "string"
        attribute "switchA", "string"
        attribute "switchFan", "string"
        attribute "kirksCar", "number"
        attribute "riatasCar", "number"
        attribute "walkBy", "number"
        
        
        command "onC"
        command "offC"
        command "onA"
        command "offA"
        command "onFan"
        command "offFan"
        command "subscribe"
    }

    simulator {
    }

    tiles {
        standardTile("contact", "device.contact", width: 1, height: 1, inactiveLabel: false) {
            state "open", label: '${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }
        valueTile("temperature", "device.temperature") {
            state "temperature", label:'${currentValue}°', unit:"F",
            backgroundColors:[
                    [value: 12, color: "#153591"],
                    [value: 25, color: "#1e9cbb"],
                    [value: 37, color: "#90d2a7"],
                    [value: 50, color: "#44b621"],
                    [value: 62, color: "#f1d801"],
                    [value: 75, color: "#d04e00"],
                    [value: 87, color: "#bc2323"]
                ]
        }
        standardTile("switch", "device.switch", width: 1, height: 1) {
			state("0", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#79b821", action: "refresh")
			state("1", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("2", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("3", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("4", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("5", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            
		}
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("switchFan", "device.switchFan", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "off", label: '${name}', action: "onFan", icon: "st.Lighting.light24", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "offFan", icon: "st.Lighting.light24", backgroundColor: "#79b821"
		}
        standardTile("switchC", "device.switchC", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "off", label: 'G: ${name}', action: "onC", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
			state "on", label: 'G: ${name}', action: "offC", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchCon", "device.switchC", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Garage ON', action: "onC", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchCoff", "device.switchC", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Garage OFF', action: "offC", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
		}
        standardTile("switchA", "device.switchA", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "off", label: 'Fan: ${name}', action: "onA", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
			state "on", label: 'Fan: ${name}', action: "offA", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchAon", "device.switchA", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Fan ON', action: "onA", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchAoff", "device.switchA", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Fan OFF', action: "offA", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
		}
		controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
			state "level", label: '${name}', action:"switch level.setLevel"
		}
        standardTile("motion", "device.motion", width: 1, height: 1) {
			state("active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
			state("inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
        }
        standardTile("contact", "device.contact", width: 1, height: 1) {
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#79b821", action: "refresh")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffa81e", action: "refresh")
		}
        standardTile("walkBy", "device.walkBy", width: 1, height: 1) {
			state("off", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#79b821", action: "refresh")
			state("on", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffa81e", action: "refresh")
		}
        valueTile("kirksCar", "device.kirksCar") {
			state "default", label:'${currentValue} in', action:"refresh",
            backgroundColors:[
                    [value: 5, color: "#ff0000"], //Used for tornado distance
                    [value: 36, color: "#3fff00"], //Green, good to go
                    [value: 72, color: "#ffff00"], // In garage, could be better
                    [value: 120, color: "#1900fc"], //10' from wall... do better
                    [value: 175, color: "#000000"]
                ]
		}       
        valueTile("riatasCar", "device.riatasCar") {
			state "default", label:'${currentValue} in', action:"refresh",
            backgroundColors:[
                    [value: 24, color: "#ff0000"], //TOO CLOSE!
                    [value: 45, color: "#ff02bf"], //Pink, good to go
                    [value: 72, color: "#ffff00"], // In garage, could be better
                    [value: 120, color: "#1900fc"], //10' from wall... do better
                    [value: 175, color: "#000000"]
                ]
		}
        main "switchFan"
        details (["switch", "temperature","switchFan","switchC","switchCon","switchCoff", 
        	"switchA","switchAon","switchAoff","kirksCar","riatasCar","walkBy", "levelSliderControl", 
            "motion","contact","refresh"])
    }
}

// parse events into attributes
def parse(String description) {
	
    def usn = getDataValue('ssdpUSN')
    TRACE( "Parsing Arduino DT ${device.deviceNetworkId} ${usn} '${description}'")

    def parsedEvent = parseDiscoveryMessage(description)

    if (parsedEvent['body'] != null) {
    	try{
            def xmlText = new String(parsedEvent.body.decodeBase64())
            def xmlTop = new XmlSlurper().parseText(xmlText)
            def cmd = xmlTop.cmd[0] //
            def val = xmlTop.values[0] //
            def act = xmlTop.actuator[0]
            def switchVal = xmlTop.switch[0]
            def livingRoomFan = xmlTop.lrf[0] //
            def chc = xmlTop.chc[0] //
            def cha = xmlTop.cha[0] //
            def motion1 = xmlTop.motion1[0] 
            def contact1 = xmlTop.contact1[0]
            def remoteCode = xmlTop.remoteCode[0]
            def livingRoomLight = xmlTop.lrl[0]

            def targetUsn = xmlTop.usn[0].toString()

            TRACE( "Processing command ${cmd} val: ${val} for ${targetUsn}")

            parent.getChildDevices().each { child ->
                def childUsn = child.getDataValue("ssdpUSN").toString()
                if (childUsn == targetUsn) {
                    TRACE( "childUSN ${childUsn} equal to Target USN ${targetUsn}")
                    try {

                        if (cmd == 'poll') {
                            //log.info "Instructing child ${child.device.label} to poll"
                            child.poll()
                        } else if (cmd == 'status-open') {
                            def value = 'open'
                            log.info "Updating ${child.device.label} to ${value}"
                            child.sendEvent(name: 'contact', value: value)
                        } else if (cmd == 'status-closed') {
                            def value = 'closed'
                            log.info "Updating ${child.device.label} to ${value}"
                            child.sendEvent(name: 'contact', value: value)
                        }
                    } catch (e) {
                        //log.info "No Cmd"
                    }
                    try{
                        if(val.toFloat() > 0 ) {
                            log.info "Updating ${child.device.label} to ${val}"
                            child.sendEvent(name: 'temperature', value: val)
                        }
                    } catch(e) {
                        log.info "No values msg"
                    }
                    try {
                        if (remoteCode.toFloat() >= 0) {
                            log.info "received Switch Signal: ${remoteCode.toFloat()}"
                            child.sendEvent(name: 'switch', value: remoteCode)
                        }
                    } catch (e) {
                        log.info "No Switch msg"
                    }
                    try {
                        if (motion.toFloat() == 0) {
                            log.info "received Motion Signal: ${motion.toFloat()}"
                            child.sendEvent(name: 'motion', value: 'inactive')
                        } else if (motion.toFloat() == 1) {
                            log.info "received Motion Signal: ${motion.toFloat()}"
                            child.sendEvent(name: 'motion', value: 'active')
                        }
                    } catch (e) {
                        log.info "No motion in msg"
                    }
                    try {
                        if (motion1.toFloat() == 0) {
                            log.debug "received motion1 Signal: ${motion1.toFloat()}"
                            child.sendEvent(name: 'motion', value: 'inactive')
                        } else if (motion1.toFloat() == 1) {
                            log.info "received motion1 Signal: ${motion1.toFloat()}"
                            child.sendEvent(name: 'motion', value: 'active')
                        }
                    } catch (e) {
                        log.info "No motion1 in msg"
                    }
                    try {
                        if (livingRoomLight.toFloat() == 0) {
                            log.info "received lrl Signal: ${livingRoomLight.toFloat()}"
                            child.sendEvent(name: 'switchFan', value: 'off')
                        } else if (livingRoomLight.toFloat() == 1) {
                            log.info "received lrl Signal: ${livingRoomLight.toFloat()}"
                            child.sendEvent(name: 'switchFan', value: 'on')
                        }
                    } catch (e) {
                        log.info "No lrl in msg"
                    }
                    try {
                        if (chc.toFloat() == 0) {
                            log.info "received chc Signal: ${chc.toFloat()}"
                            child.sendEvent(name: 'switchC', value: 'off')
                        } else if (chc.toFloat() == 1) {
                            log.info "received chc Signal: ${chc.toFloat()}"
                            child.sendEvent(name: 'switchC', value: 'on')
                        }
                    } catch (e) {
                        log.info "No chc in msg"
                    }
                    try {
                        if (cha.toFloat() == 0) {
                            log.info "received cha Signal: ${cha.toFloat()}"
                            child.sendEvent(name: 'switchA', value: 'off')
                        } else if (cha.toFloat() == 1) {
                            log.info "received cha Signal: ${cha.toFloat()}"
                            child.sendEvent(name: 'switchA', value: 'on')
                        }
                    } catch (e) {
                        log.info "No cha in msg"
                    }
                    try {
                        if (livingRoomFan.toFloat() >= 0) {
                            log.info "received lrf Signal: ${livingRoomFan.toFloat()}"
                            child.sendEvent(name: 'level', value: livingRoomFan)
                        } 
                    } catch (e) {
                        log.info "No lrf in msg"
                    }

                } else {
                    TRACE( "childUSN ${childUsn} not equal to Target USN ${targetUsn}")
                }

            }
        } catch (e) {
            TRACE("NO XML: Probably a GET response")
        }
    }
    null
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

    new physicalgraph.device.HubAction(
        'method': 'GET',
        'path': path,
        'headers': [
            'HOST': getHostAddress(),
        ], device.deviceNetworkId)
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

def subscribe() {
	state.ssdpPath = getDataValue("ssdpPath")
    log.debug "Subscribe requested\r\n${state.ssdpPath}"
    subscribeAction(getDataValue("ssdpPath"))
}

private def parseDiscoveryMessage(String description) {
	//TRACE("In PDM:${description}")
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
	TRACE("SWITCH ON")
	
}

def off() {
	TRACE("SWITCH OFF")

}
def onFan() {
    TRACE("onFan()")

    if (device.currentValue("switchFan") == "on") {
        return null
    }
	TRACE("sending on()")
    sendEvent([name:"switchFan", value:"on"])
    return writeValue('cmd', 221)
}

def offFan() {
    TRACE("offFan()")

    if (device.currentValue("switchFan") == "off") {
        return null
    }
	TRACE("sending off()")
    sendEvent([name:"switchFan", value:"off"])
    return writeValue('cmd', 221)
}
def onC() {
    TRACE("onC()")

	TRACE("sending onC()")
    sendEvent([name:"switchC", value:"on"])
    return writeValue('cmd', 601)
}

def offC() {
    TRACE("offC()")

	TRACE("sending offC()")
    sendEvent([name:"switchC", value:"off"])
    return writeValue('cmd', 600)
}
def onA() {
    TRACE("onA()")

	TRACE("sending onA()")
    sendEvent([name:"switchA", value:"on"])
    return writeValue('cmd', 603)
}

def offA() {
    TRACE("offA()")

	TRACE("sending offA()")
    sendEvent([name:"switchA", value:"off"])
    return writeValue('cmd', 602)
}
def setLevel(value) {
	TRACE("setLevel(${value})")
	def roundedValue = sendValue(value)
    TRACE("sending rounded Value ${roundedValue}")

    if (device.currentValue("level") == roundedValue) {
        return null
    }
	TRACE("sending setLevel()")
    sendEvent([name:"setLevel()", value:roundedValue])
    return writeValue('cmd', 500 + roundedValue)
    
}

private sendValue(level) {
	if (level < 16) return 0
	if (level < 49) return 33
	if (level < 85) return 66
    if (level < 100) return 99

}

private def TRACE(message) {
    log.trace message
}
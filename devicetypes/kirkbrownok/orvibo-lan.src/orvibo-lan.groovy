/**
 *  Orvibo Device Type
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
    definition (name: "Orvibo LAN", namespace: "kirkbrownOK", author: "Kirk Brown") {
        capability "Polling"
        capability "Refresh"
        capability "Actuator"
		capability "Switch"

    }

    simulator {
    }

    tiles {
    	
        standardTile("switch", "device.switch", width: 1, height: 1) {
			state("0", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#79b821", action: "refresh")
			state("1", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            
		}
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        
        main "switch"
        details (["switch","refresh"])
    }
}

// parse events into attributes
def parse(String description) {
	TRACE(${description})
    def usn = getDataValue('ssdpUSN')
    TRACE( "Parsing Arduino DT ${device.deviceNetworkId} ${usn} '${description}'")

    def parsedEvent = parseDiscoveryMessage(description)
	def events = []
    def ev = []
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
            def we1 = xmlTop.we1[0] //
            def motion1 = xmlTop.mn1[0] 
            def contact1 = xmlTop.ct1[0]
            def contact2 = xmlTop.ct2[0]
            def contact3 = xmlTop.ct3[0]
            def contact4 = xmlTop.ct4[0]
            def contact5 = xmlTop.ct5[0]
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
                            //log.info "Instructing 10:49 child ${child.device.label} to poll"
                            child.poll()
                        } else if (cmd == 'status-open') {
                            def value = 'open'
                            TRACE( "Updating ${child.device.label} to ${value}")
                            //child.sendEvent(name: 'contact', value: value)
                        } else if (cmd == 'status-closed') {
                            def value = 'closed'
                            TRACE( "Updating ${child.device.label} to ${value}")
                            //child.sendEvent(name: 'contact', value: value)
                        }
                    } catch (e) {
                        //TRACE( "No Cmd")
                    }
                    try{
                        if(val.toFloat() > 0 ) {
                            TRACE( "Updating ${child.device.label} to ${val}")
                            if (child.currentTemperature != val) {
                            	child.sendEvent(name: 'temperature', value: val)
                            }
                        }
                    } catch(e) {
                        TRACE( "No values msg")
                    }
                    try {
                        if (remoteCode.toFloat() >= 0) {
                            TRACE( "received Switch Signal: ${remoteCode.toFloat()}")
                            if( child.currentValue("Switch") != remoteCode) child.sendEvent(name: 'switch', value: remoteCode)
                        }
                    } catch (e) {
                        TRACE( "No Switch msg")
                    }
                    try {        
        
                        if (contact1.toFloat() == 0) {
                            TRACE( "received Contact1 Signal: ${contact1.toFloat()}")
                            if (child.currentValue("contact1") != 'closed' ) {
                            	ev = [name:   "contact1",value:  'closed' ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor1:close'] 
                            	events << createEvent(ev)
                            }
                        } else if (contact1.toFloat() ==1) {
                        	TRACE( "received Contact Signal: ${contact1.toFloat()}")
                            if(child.currentValue("contact1") != 'open') { 
                            	//child.sendEvent([name: 'contact1', value: 'open', name: 'contact', value: 'Sensor1:open'])
                        		ev = [name:   "contact1", value:  'open' ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor1:open'] 
                            	events << createEvent(ev)
                            
                            }
                        }
                    } catch (e) {
                        TRACE( "No contact1 msg")
                    }
                    try {
                        if (contact2.toFloat() == 0) {
                            TRACE( "received Contact2 Signal: ${contact2.toFloat()}")
                            if(child.currentValue("contact2") != 'closed') {
                            	ev = [
                                    name:   "contact2",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor2:close'] 
                            	events << createEvent(ev)        
                            }//child.sendEvent([name: 'contact2', value: 'closed', name:'contact',value:'Sensor2:close'])
                            
                        } else if (contact2.toFloat() ==1) {
                        	TRACE( "received Contact2 Signal: ${contact2.toFloat()}")
                            if( child.currentValue("contact2") != 'open') {
                            	ev = [
                                    name:   "contact2",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor2:open'] 
                            	events << createEvent(ev)        
                            } //child.sendEvent([name: 'contact2', value: 'open', name:'contact',value:'Sensor2:open'])
                        }
                    } catch (e) {
                        TRACE( "No contact2 msg")
                    }
                    try {
                        if (contact3.toFloat() == 0) {
                            TRACE( "received Contact3 Signal: ${contact3.toFloat()}")
                            if (child.currentValue("contact3") != 'closed') {
                            	ev = [
                                    name:   "contact3",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor3:close'] 
                            	events << createEvent(ev)        
                            }//child.sendEvent([name: 'contact3', value: 'closed', name:'contact',value:'Sensor3:close'])
                        } else if (contact3.toFloat() ==1) {
                        	TRACE( "received Contact3 Signal: ${contact3.toFloat()}")
                            if (child.currentValue("contact3") != 'open') {
                            	ev = [
                                    name:   "contact3",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor3:open'] 
                            	events << createEvent(ev)        
                            }//child.sendEvent([name: 'contact3', value: 'open', name:'contact',value:'Sensor3:open'])
                        }
                    } catch (e) {
                        TRACE( "No contact3 msg")
                    }
  /*                  try {
                        if (contact4.toFloat() == 0) {
                            TRACE( "received Contact4 Signal: ${contact4.toFloat()}")
                            TRACE("C4 is ${child.currentValue("contact4")} dev ${device.currentValue("contact4")}")
                            if(child.currentValue("contact4") != 'closed') {
                            	TRACE("Sending 4 closed")
                                TRACE("C4 is ${child.currentValue("contact4")} dev ${device.currentValue("contact4")}")
                            	child.sendEvent([name: 'contact4', value: 'closed', name:'contact',value:'Sensor4:close'])
                            }
                        } else if (contact4.toFloat() ==1) {
                        	TRACE( "received Contact4 Signal: ${contact4.toFloat()}")
                            if(child.currentValue("contact4") != 'open') {
                            	TRACE("Sending 4 open")
                                TRACE("C4 is ${child.currentValue("contact4")} dev ${device.currentValue("contact4")}")
                            	child.sendEvent([name: 'contact4', value: 'open', name:'contact',value:'Sensor4:open'])
                            }
                        }
                    } catch (e) {
                        TRACE( "No contact4 msg")
                    } */
                    
                    try {
                        if (contact4.toFloat() == 0) {
                            TRACE( "received Contact4 Signal: ${contact4.toFloat()}")
                            
                            if(child.currentValue("contact4") != 'closed') {
                            	ev = [
                                    name:   "contact4",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor4:close'] 
                            	events << createEvent(ev)                           }
                        } else if (contact4.toFloat() ==1) {
                        	//log.info "received Contact4 Signal: ${contact4.toFloat()}"
                            if(child.currentValue("contact4") != 'open') {
                            	ev = [
                                    name:   "contact4",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor4:open'] 
                            	events << createEvent(ev)
                            }
                        }
                    } catch (e) {
                        TRACE( "No contact4 msg")
                    }                    
                    try {
                        if (contact5.toFloat() == 0) {
                            TRACE( "received Contact5 Signal: ${contact5.toFloat()}")
                            if(child.currentValue("contact5") != 'closed'){
                            	ev = [
                                    name:   "contact5",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor5:close'] 
                            	events << createEvent(ev)                               	}
                        } else if (contact5.toFloat() ==1) {
                        	TRACE( "received Contact5 Signal: ${contact5.toFloat()}")
                            if(child.currentValue("contact5") != 'open'){
								ev = [
                                    name:   "contact5",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor5:open'] 
                            	events << createEvent(ev)        
							}
                        }
                    } catch (e) {
                        TRACE( "No contact5 msg")
                    }
                    try {
                        if (motion.toFloat() == 0) {
                            TRACE( "received Motion Signal: ${motion.toFloat()}")
                            if (child.currentMotion != 'inactive') child.sendEvent(name: 'motion', value: 'inactive')
                        } else if (motion.toFloat() == 1) {
                            TRACE( "received Motion Signal: ${motion.toFloat()}")
                            if (child.currentMotion != 'active') child.sendEvent(name: 'motion', value: 'active')
                        }
                    } catch (e) {
                        TRACE( "No motion in msg")
                    }
                    try {
                        if (motion1.toFloat() == 0) {
                            TRACE( "received motion1 Signal: ${motion1.toFloat()}")
                            if (child.currentMotion != 'inactive') child.sendEvent(name: 'motion', value: 'inactive')
                        } else if (motion1.toFloat() == 1) {
                            TRACE( "received motion1 Signal: ${motion1.toFloat()}")
                            if (child.currentMotion != 'active')child.sendEvent(name: 'motion', value: 'active')
                        }
                    } catch (e) {
                        TRACE( "No motion1 in msg")
                    }
                    try {
                        if (livingRoomLight.toFloat() == 0) {
                            TRACE( "received lrl Signal: ${livingRoomLight.toFloat()}")
                            if( child.currentSwitchFan != 'off') child.sendEvent(name: 'switchFan', value: 'off')
                        } else if (livingRoomLight.toFloat() == 1) {
                            TRACE( "received lrl Signal: ${livingRoomLight.toFloat()}")
                            if( child.currentSwitchFan != 'on') child.sendEvent(name: 'switchFan', value: 'on')
                        }
                    } catch (e) {
                        TRACE( "No lrl in msg")
                    }
                    try {
                        if (chc.toFloat() == 0) {
                            TRACE( "received chc Signal: ${chc.toFloat()}")
                            if (child.currentSwitchC != 'off' )child.sendEvent(name: 'switchC', value: 'off')
                        } else if (chc.toFloat() == 1) {
                            TRACE( "received chc Signal: ${chc.toFloat()}")
                            if (child.currentSwitchC != 'on') child.sendEvent(name: 'switchC', value: 'on')
                        }
                    } catch (e) {
                        TRACE( "No chc in msg")
                    }
                    try {
                        if (cha.toFloat() == 0) {
                            TRACE( "received cha Signal: ${cha.toFloat()}")
                            child.sendEvent(name: 'switchA', value: 'off')
                        } else if (cha.toFloat() == 1) {
                            TRACE( "received cha Signal: ${cha.toFloat()}")
                            child.sendEvent(name: 'switchA', value: 'on')
                        }
                    } catch (e) {
                        TRACE( "No cha in msg")
                    }
                    try {
                        if (we1.toFloat() == 0) {
                            TRACE( "received we1 Signal: ${we1.toFloat()}")
                            child.sendEvent(name: 'switchWemo1', value: 'off')
                        } else if (we1.toFloat() == 1) {
                            TRACE( "received we1 Signal: ${we1.toFloat()}")
                            child.sendEvent(name: 'switchWemo1', value: 'on')
                        } else  {
                        	TRACE( "received we1 error: ${we1.toFloat()}")
                            child.sendEvent(name: 'switchWemo1', value: 'error')
                        }
                    } catch (e) {
                        TRACE( "No we1 in msg")
                    }
                    try {
                        if (livingRoomFan.toFloat() >= 0) {
                            TRACE( "received lrf Signal: ${livingRoomFan.toFloat()}")
                            child.sendEvent(name: 'level', value: livingRoomFan)
                        } 
                    } catch (e) {
                        TRACE( "No lrf in msg")
                    }

                } else {
                    TRACE( "childUSN ${childUsn} not equal to Target USN ${targetUsn}")
                }

            }
        } catch (e) {
            TRACE("NO XML: Probably a GET response: $e")
        }
    }
    TRACE("EVENTS: ${events}")
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
    onFan()
	
}

def off() {
	TRACE("SWITCH OFF")
	offFan()
}
def onFan() {
    TRACE("onFan()")
	TRACE("sending on()")
    sendEvent([name:"switchFan", value:"on"])
    return writeValue('cmd', 221)
}

def offFan() {
    TRACE("offFan()")
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
def wemoOn1() {
    TRACE("wemoon1A()")

	TRACE("sending wemoon1()")
    sendEvent([name:"switchWemo1", value:"on"])
    return writeValue('cmd', 616)
}

def wemoOff1() {
    TRACE("wemooff1()")

	TRACE("sending wemooff1()")
    sendEvent([name:"switchWemo1", value:"off"])
    return writeValue('cmd', 617)
}
def refreshWemo1() {
    TRACE("refreshWemo1()")

	TRACE("sending refreshWemo1()")
    sendEvent([name:"switchWemo1", value:"refresh"])
    return writeValue('cmd', 618)
}
def toggleSwitch(switchNum) {
    TRACE("toggleSwitch() number ${switchNum}")

    return writeValue('cmd', "61${switchNum}")
}
def setLevel(value) {
	TRACE("setLevel(${value})")
	def roundedValue = sendValue(value)
    TRACE("sending rounded Value ${roundedValue}")

//    if (device.currentValue("level") == roundedValue) {
//        return null
//    }
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
/**
 *  Adapted Filtrete 3M-50 WiFi Thermostat to work with Arduino light controller
 *
 *  --------------------------------------------------------------------------
 *
 *  Major thanks to geko@statusbits.com for teaching how to use LAN connections
 *  and parsing JSON data. Arduino listens for GET/POST traffic and hands off 
 *  JSON data as a sensor
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
    input("confIpAddr", "string", title:"Arduino IP Address", defaultValue: "192.168.2.107",
        required:true, displayDuringSetup: true)
    input("confTcpPort", "number", title:"TCP Port",
        defaultValue:"80", required:true, displayDuringSetup:true)
}

metadata {
    definition (name:"ArduinoEthernetLightBridge", namespace:"kirkbrownOK", author:"Kirk Brown") {
    	//Primarily based on author:"geko@statusbits.com" radiothermostat
        capability "Switch Level"
		capability "Actuator"
		capability "Indicator"
        capability "Temperature Measurement"
		capability "Switch"
        capability "Refresh"
        capability "Polling"
        capability "Motion Sensor"
        capability "Contact Sensor"
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
        
    }

    tiles {
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
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
			state "level", label: '${name}', action:"switch level.setLevel"
		}
        standardTile("motion", "device.motion", width: 1, height: 1) {
			state("active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
			state("inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
        }
        standardTile("switch", "device.switch", width: 1, height: 1) {
			state("0", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#79b821", action: "refresh")
			state("1", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("2", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("3", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("4", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("5", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            
		}
        standardTile("contact", "device.contact", width: 1, height: 1) {
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#79b821", action: "refresh")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffa81e", action: "refresh")
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
        valueTile("temperature", "device.temperature", width: 1, height: 1) {
        	state "temperature", label:'${currentValue}°', action: "refresh",
            	inactiveLabel:false,
                backgroundColors:[
                    [value: 31, color: "#153591"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
                    [value: 74, color: "#44b621"],
                    [value: 84, color: "#f1d801"],
                    [value: 95, color: "#d04e00"],
                    [value: 96, color: "#bc2323"]
                ]
        }        
        standardTile("walkBy", "device.walkBy", width: 1, height: 1) {
			state("off", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#79b821", action: "refresh")
			state("on", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffa81e", action: "refresh")
		}        
        /*
		valueTile("level", "device.level", inactiveLabel: false, decoration: "flat") {
			state "level", label: 'Level ${currentValue}%'
		}
        */
		

		main(["switch", "switchFan"])
		//details(["switch", "level", "levelSliderControl", "refresh"])
        details(["switch", "temperature","switchFan","switchC","switchCon","switchCoff", "switchA","switchAon","switchAoff","kirksCar","riatasCar","walkBy", "levelSliderControl","contact", "motion","refresh"])
    }

    simulator {
    /*
        To DO
        */
    }
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
def parse(String message) {
    TRACE("parse(${message})")

    def msg = stringToMap(message)

    if (msg.headers) {
        // parse HTTP response headers
        def headers = new String(msg.headers.decodeBase64())
        def parsedHeaders = parseHttpHeaders(headers)
        TRACE("parsedHeaders: ${parsedHeaders}")
        if (parsedHeaders.status != 200) {
            log.error "Server error: ${parsedHeaders.reason}"
            return null
        }

        // parse HTTP response body
        if (!msg.body) {
            log.error "HTTP response has no body"
            return null
        }

        def body = new String(msg.body.decodeBase64())
        def slurper = new JsonSlurper()
        def arduino = slurper.parseText(body)

        return parseData(arduino)
    } else if (msg.containsKey("simulator")) {
        // simulator input
        return parseData(msg)
    }

    return null
}



// polling.poll 
def poll() {
    TRACE("poll()")
    return refresh()
}

// refresh.refresh
def refresh() {
    TRACE("refresh()")
    STATE()

    setNetworkId(confIpAddr, confTcpPort)
    return apiGet("/light")
}

// Sets device Network ID in 'AAAAAAAA:PPPP' format
private String setNetworkId(ipaddr, port) { 
    TRACE("setNetworkId(${ipaddr}, ${port})")

    def hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    def hexPort = String.format('%04X', port.toInteger())
    device.deviceNetworkId = "${hexIp}:${hexPort}"
    //log.debug "device.deviceNetworkId = ${device.deviceNetworkId}"
}

private apiGet(String path) {
    TRACE("apiGet(${path})")

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
    TRACE("apiPost(${path}, ${data})")

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

private def writeValue(name, value) {
    TRACE("writeValue(${name}, ${value})")

    setNetworkId(confIpAddr, confTcpPort)

    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPost("/light", json),
        delayHubAction(2000),        
        
        //apiGet("/light")
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

private def parseData(Map arduino) {
    //TRACE("parseData(${arduino})")

    def events = []
    if (arduino.containsKey("error_msg")) {
        log.error "error: ${arduino.error_msg}"
        return null
    }

    if (arduino.containsKey("success")) {
        // this is POST response - ignore
        return null
    }
    
    if (arduino.containsKey("livingRoomLight")) {
        //Float temp = arduino.livingRoomLight.toFloat()
         
         def value = parseLivingRoomLight(arduino.livingRoomLight)
        if (device.currentState("switch")?.value != value) {
            def ev = [
                name:   "switchFan",
                value:  value
            ]

            events << createEvent(ev)
        }
    }
    if (arduino.containsKey("motion1")) {
        //Float temp = arduino.motion1.toFloat()
         
         def value = parseMotion(arduino.motion1)
         //TRACE("motion1: ${value}")
        if (device.currentState("motion")?.value != value) {
            def ev = [
                name:   "motion",
                value:  value
            ]
			
            events << createEvent(ev)
        }
    }
    if (arduino.containsKey("contact1")) {
         
         def value = parseContact(arduino.contact1)
         //TRACE("contact1: ${value}")
        if (device.currentState("contact")?.value != value) {
            def ev = [
                name:   "contact",
                value:  value
            ]
			
            events << createEvent(ev)
        }
    }
    if (arduino.containsKey("remote")) {
         
         TRACE("remote: ${arduino.remote}")
        if (device.currentState("switch")?.value != value) {
            def ev = [
                name:   "switch",
                value:  arduino.remote
            ]
			TRACE(ev)
            events << createEvent(ev)
        } else {
        	TRACE("Not True")
        }
        
    }
    
    if (arduino.containsKey("channelC")) {
        //Float temp = arduino.channelC
         
         def value = parseChannelOutlet(arduino.channelC)
        if (device.currentState("switchC")?.value != value) {
            def ev = [
                name:   "switchC",
                value:  value
            ]

            events << createEvent(ev)
        }
    } 
    if (arduino.containsKey("ck")) {
         
         def value = arduino.ck
        if (device.currentState("kirksCar")?.value != value) {
            def ev = [
                name:   "kirksCar",
                value:  value
            ]

            events << createEvent(ev)
        }
    } 
    if (arduino.containsKey("ca")) {
         
         def value = arduino.ca
        if (device.currentState("riatasCar")?.value != value) {
            def ev = [
                name:   "riatasCar",
                value:  value
            ]

            events << createEvent(ev)
        }
    }
    if (arduino.containsKey("dalt")) {
         
         def value = arduino.dalt
         def ev = [
             name:   "temperature",
             value:  value
         ]

         events << createEvent(ev)
     
    }    
    if (arduino.containsKey("wB")) {
         
         def value = parseChannelOutlet(arduino.wB)
        if (device.currentState("walkBy")?.value != value) {
            def ev = [
                name:   "walkBy",
                value:  value
            ]

            events << createEvent(ev)
        }
    }    
    if (arduino.containsKey("channelA")) {
        //Float temp = arduino.channelC
         
         def value = parseChannelOutlet(arduino.channelA)
        if (device.currentState("switchA")?.value != value) {
            def ev = [
                name:   "switchA",
                value:  value
            ]

            events << createEvent(ev)
        }
    }    
    if (arduino.containsKey("livingRoomFan")) {
        //Float temp = arduino.livingRoomFan
         TRACE("Contains livingRoomFan $arduino.livingRoomFan")
        if (device.currentState("levelSliderControl")?.value != arduino.livingRoomFan) {
        	def ev = [
            	name:   "levelSliderControl",
            	value:  arduino.livingRoomFan
            
        	]

        	events << createEvent(ev)
        }
    }

    TRACE("events: ${events}")
    return events
}

private def parseLivingRoomLight(val) {
    def values = [
        "OFF",     // 0
        "ON"  // 1
    ]

    return values[val.toInteger()]
}

private def parseMotion(val) {
    def values = [
        "inactive",     // 0
        "active"  // 1
    ]

    return values[val.toInteger()]
}
private def parseContact(val) {
    def values = [
        "closed",     // 0
        "open"  // 1
    ]

    return values[val.toInteger()]
}

private def parseChannelOutlet(val) {
    def values = [
        "OFF",     // 0
        "ON"  // 1
    ]

    return values[val.toInteger()]
}

private def TRACE(message) {
    log.debug message
}

private def STATE() {
    TRACE( "deviceNetworkId : ${device.deviceNetworkId}")
    TRACE( "light : ${device.currentValue("switch")}")
    TRACE( "level : ${device.currentValue("levelSliderControl")}")
    TRACE("motion : ${device.currentValue("motion")}")
}
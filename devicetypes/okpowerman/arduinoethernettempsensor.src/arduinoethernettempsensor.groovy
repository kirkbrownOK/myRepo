/**
 *  Filtrete 3M-50 WiFi Thermostat
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
    input("confIpAddr", "string", title:"Arduino IP Address", defaultValue: "192.168.2.128",
        required:true, displayDuringSetup: true)
    input("confTcpPort", "number", title:"TCP Port",
        defaultValue:"80", required:true, displayDuringSetup:true)
    input("MinutesOfError","number",title: "Only send error event if in error condition for this many minutes: ", defaultValue:30, required: false)
    input("tempOffset","number",title: "Temperature offset", defaultValue:0, required: false)
}

metadata {
    definition (name:"ArduinoEthernetTempSensor", namespace:"okpowerman", author:"tinkererscave@gmail.com") {
    	//Primarily based on author:"geko@statusbits.com" radiothermostat
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"
        capability "Switch"
        
        command "refresh"
        attribute "malfunction","string"

        
    }

    tiles {
        valueTile("temperature", "device.temperature", width: 1, height: 1) {
        	state "temperature", label:'${currentValue}°', action: "refresh", canChangeIcon: true, icon: "st.Weather.weather2",
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
        valueTile("temperature1", "device.temperature1", width: 1, height: 1) {
        	state "temperature1", label:'${currentValue}°', action: "refresh",
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
        valueTile("humidity", "device.humidity", width: 1, height: 1) {
			state "humidity", label:'${currentValue}%\n Humidity', action: "refresh",
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
        standardTile("tempStatus", "device.switch", width: 1, height: 1, inactiveLabel:false) {
            state "on", label: 'Normal', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#79b821"
            state "off", label: 'No Data', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#FF0000"
            state "error", label: 'Error', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#000000"
        }
		standardTile("refresh", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("state", "device.malfunction", inactiveLabel: false, decoration: "flat") {
			state "normal", label:'normal'
            state "emergency", label:'broken'
		}

        main(["temperature"])

        details(["temperature", "temperature1", "humidity","tempStatus", "refresh", "state"])
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
        def tstat = slurper.parseText(body)

        return parseTstatData(tstat)
    } else if (msg.containsKey("simulator")) {
        // simulator input
        return parseTstatData(msg)
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
    state.minutesoferror = MinutesOfError
	state.lastRefreshRequest = now()
    setNetworkId(confIpAddr, confTcpPort)
    //For initializing this needs to be uncommented.
    //return apiGet("/bedroom")
    state.minutesSinceUpdate = (state.lastRefreshRequest - state.lastSuccessfulMessage ) / 60000
    if (state.minutesSinceUpdate > state.minutesoferror) {
		log.error("Arduino hasn't responded in ${state.minutesSinceUpdate} minutes")
        sendEvent([name: "switch", value: "error",desriptionText:"Arduino Not available"]) 
        sendEvent([name: "malfunction", value: "emergency", descriptionText: "Arduino in error for too long"])
        state.lastSuccessfulMessage = now()
    }
    return apiGet("/bedroom")
}

// Sets device Network ID in 'AAAAAAAA:PPPP' format
private String setNetworkId(ipaddr, port) { 
    TRACE("setNetworkId(${ipaddr}, ${port})")

    def hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    def hexPort = String.format('%04X', port.toInteger())
    device.deviceNetworkId = "${hexIp}:${hexPort}"
    TRACE("device.deviceNetworkId = ${device.deviceNetworkId}")
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
    TRACE("parseTstatData(${tstat})")

    def events = []
    if (tstat.containsKey("error_msg")) {
        log.error "error: ${tstat.error_msg}"
        return null
    }

    if (tstat.containsKey("success")) {
        // this is POST response - ignore
        return null
    }
    if (tstat.containsKey("atemp")) {
    	def myOffset = 0
    	try{
            if(tempOffset > 0 || tempOffset < 0) {
				myOffset = tempOffset	
            }
        } catch (e) {
        	TRACE("TEMPOFFSET NOT DEFINED")
        }
        def myTemp = tstat.atemp.toFloat() + myOffset

         TRACE("Contains atemp")
        def ev = [
            name:   "temperature",
            value:  tstat.atemp.toFloat() + myOffset,
            
        ]

        events << createEvent(ev)
        if(myTemp > 40) {
        	ev = [
            	name: "malfunction",
                value: "normal"
                ]
                
        } else {
        	ev = [
            	name: "malfunction",
                value: "emergency"
            ]
        }
        events << createEvent(ev)
        ev = [
        	name: "switch",
            value: "on",
        ]
        events << createEvent(ev)
        state.lastSuccessfulMessage = now()
    }
        if (tstat.containsKey("humidity")) {
        //Float temp = tstat.humidity.toFloat()
         TRACE("Contains humidity")
        def ev = [
            name:   "humidity",
            value:  tstat.humidity.toFloat(),
            
        ]

        events << createEvent(ev)
    }
    if (tstat.containsKey("dtemp")) {
        //Float temp = tstat.dtemp.toFloat()
         TRACE("Contains dtemp")
        def ev = [
            name:   "temperature1",
            value:  tstat.dtemp.toFloat(),
            
        ]

        events << createEvent(ev)
    }

    TRACE("events: ${events}")
    return events
}

def getTemperature(value) {
	return ${device.currentValue("temperature")}
}
def getTemperature() {
	return ${device.currentValue("temperature")}
}


private def TRACE(message) {
    log.debug message
}

private def STATE() {
    TRACE( "deviceNetworkId : ${device.deviceNetworkId}")
    TRACE( "temperature : ${device.currentValue("temperature")}")
}
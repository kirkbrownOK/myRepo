/**
 *  Filtrete 3M-50 WiFi Thermostat with Multi Attribute Tile
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright (c) 2014 geko@statusbits.com
 * //I just edited geko's code. 
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
 *  The latest version of this file can be found at:
 *  <https://github.com/statusbits/smartthings/tree/master/RadioThermostat/>
 *
 *  Revision History
 *  ----------------
 *  2014-09-13: Version: 1.0.1  Fixed fan control bug
 *  2014-09-11: Version: 1.0.0  Released Version 1.0.0
 *  2014-08-12: Version: 0.9.0
 */

import groovy.json.JsonSlurper

preferences {
    input("confIpAddr", "string", title:"Thermostat IP Address",
        required:true, displayDuringSetup: true)
    input("confTcpPort", "number", title:"Thermostat TCP Port",
        defaultValue:"80", required:true, displayDuringSetup:true)
}

metadata {
    definition (name:"Radio Thermostat Multi-Tile", namespace:"kirkbrownOK", author:"Kirk Brown") {
        capability "Thermostat"
        capability "Temperature Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"

        // Custom attributes
        attribute "fanState", "string"  // Fan operating state. Values: "on", "off"
        attribute "hold", "string"      // Target temperature Hold status. Values: "on", "off"
		attribute "heatingOverride", "number"
        attribute "coolingOverride", "number"
        attribute "rem_mode", "string"
        attribute "remote_temp", "number"
        
        
        // Custom commands
        command "heatUp"
        command "heatDown"
        command "coolUp"
        command "coolDown"
        command "holdOn"
        command "holdOff"
        command "fanOn"
        command "fanOff"
        command "fanAuto" 
        command "tempDown"
        command "tempUp"
        command "remoteTemp"
        command "clearRemote"
        command "refreshRemote"
    }

tiles(scale:2) {
		multiAttributeTile(name:"thermostatNoHumidity", type:"thermostat", width:6, height:4) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState("coolingSetpoint", label:'${currentValue}', unit:"dF", defaultState: true)
				attributeState("temp", label:'${currentValue}', unit:"dF")
			}
			tileAttribute("device.temperature", key: "VALUE_CONTROL") {
				attributeState("VALUE_UP", action: "tempUp")
				attributeState("VALUE_DOWN", action: "tempDown")
			}
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#44b621")
				attributeState("heating", backgroundColor:"#ffa81e")
				attributeState("cooling", backgroundColor:"#269bd2")
			}
			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("off", label:'${name}')
				attributeState("heat", label:'${name}')
				attributeState("cool", label:'${name}')
				attributeState("auto", label:'${name}')
			}
			tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
                attributeState("coolingSetpoint", label:'${currentValue}', unit:"dF", defaultState: true)
				attributeState("heatingSetpoint", label:'${currentValue}', unit:"dF")
			}
			tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
				attributeState("coolingSetpoint", label:'${currentValue}', unit:"dF", defaultState: true)
			}
		}
        valueTile("temperature", "device.temperature") {
            state "temperature", label:'${currentValue}°', unit:"F",icon:"st.Appliances.appliances11",
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
        valueTile("rem_temperature", "device.remote_temp") {
            state "remote_temp", label:'${currentValue}°', unit:"F",icon:"st.Appliances.appliances11",
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

        valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel:false) {
            state "default", label:'${currentValue}°', unit:"F",
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
        valueTile("heatingOverride", "device.heatingOverride", inactiveLabel:false) {
            state "default", label:'${currentValue}°', unit:"F",
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

        valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel:false) {
            state "default", label:'${currentValue}°', unit:"F",
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
        
        standardTile("heatUp", "device.heatingSetpoint", inactiveLabel:false, decoration:"flat") {
            state "default", label:'Heating', icon:"st.custom.buttons.add-icon", action:"heatUp"
        }

        standardTile("heatDown", "device.heatingSetpoint", inactiveLabel:false, decoration:"flat") {
            state "default", label:'Heating', icon:"st.custom.buttons.subtract-icon", action:"heatDown"
        }

        standardTile("coolUp", "device.coolingSetpoint", inactiveLabel:false, decoration:"flat") {
            state "default", label:'Cooling', icon:"st.custom.buttons.add-icon", action:"coolUp"
        }

        standardTile("coolDown", "device.coolingSetpoint", inactiveLabel:false, decoration:"flat") {
            state "default", label:'Cooling', icon:"st.custom.buttons.subtract-icon", action:"coolDown"
        }

        standardTile("operatingState", "device.thermostatOperatingState", inactiveLabel:false, decoration:"flat") {
            state "default", label:'[State]'
            state "idle", label:'', icon:"st.thermostat.heating-cooling-off"
            state "heating", label:'', icon:"st.thermostat.heating"
            state "cooling", label:'', icon:"st.thermostat.cooling"
        }

        standardTile("fanState", "device.fanState", inactiveLabel:false, decoration:"flat") {
            state "default", label:'[Fan State]'
            state "on", label:'', icon:"st.thermostat.fan-on"
            state "off", label:'', icon:"st.thermostat.fan-off"
        }

        standardTile("mode", "device.thermostatMode", inactiveLabel:false) {
            state "default", label:'[Mode]'
            state "off", label:'', icon:"st.thermostat.heating-cooling-off", backgroundColor:"#FFFFFF", action:"thermostat.heat"
            state "heat", label:'', icon:"st.thermostat.heat", backgroundColor:"#FFCC99", action:"thermostat.cool"
            state "cool", label:'', icon:"st.thermostat.cool", backgroundColor:"#99CCFF", action:"thermostat.auto"
            state "auto", label:'', icon:"st.thermostat.auto", backgroundColor:"#99FF99", action:"thermostat.off"
        }
        standardTile("rem_mode", "device.rem_mode", inactiveLabel:false) {
            //state "default", label:'[Mode]', action:"refreshRemote"
            state "off", label:'OFF', icon:"st.thermostat.heating-cooling-off", backgroundColor:"#FFFFFF", action:"refreshRemote"
            state "on", label:'ON', icon:"st.thermostat.auto", backgroundColor:"#FFFFFF", action:"thermostat.clearRemote"
        }

        standardTile("fanMode", "device.thermostatFanMode", inactiveLabel:false) {
            state "default", label:'[Fan Mode]'
            state "auto", label:'', icon:"st.thermostat.fan-auto", backgroundColor:"#A4FCA6", action:"thermostat.fanOn"
            state "on", label:'', icon:"st.thermostat.fan-on", backgroundColor:"#FAFCA4", action:"thermostat.fanAuto"
        }

        standardTile("hold", "device.hold", inactiveLabel:false) {
            state "default", label:'[Hold]'
            state "on", label:'Hold On', icon:"st.Weather.weather2", backgroundColor:"#FFDB94", action:"holdOff"
            state "off", label:'Hold Off', icon:"st.Weather.weather2", backgroundColor:"#FFFFFF", action:"holdOn"
        }

        standardTile("refresh", "device.thermostatMode", inactiveLabel:false, decoration:"flat",width:6, height:2) {
            state "default", icon:"st.secondary.refresh", action:"refresh.refresh"
        }

        main(["temperature","thermostatNoHumidity" ])

        details(["thermostatNoHumidity","temperature", "operatingState", "fanState",
        	"mode", "fanMode", "hold",
            "heatingSetpoint", "heatDown", "heatUp",
            "coolingSetpoint", "coolDown", "coolUp",
            "rem_mode","rem_temperature",
             
            "refresh"])
    }

    simulator {
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
def tempUp() {
	def mode = device.currentValue("thermostatMode")
    if (!mode) {
    	log.warn "Mode undefined"
    	return
    }
    if (mode == "cool") {
      	coolUp()
    } else if (mode == "heat") {
    	heatUp()
    } else {
    	log.warn "Mode is $mode and not heat/cool"
    }	
} 

def tempDown() {
	def mode = device.currentValue("thermostatMode")
    if (!mode) {
    	log.warn "Mode undefined"
    	return
    }
    if (mode == "cool") {
      	coolDown()
    } else if (mode == "heat") {
    	heatDown()
    } else {
    	log.warn "Mode is $mode and not heat/cool"
    }
}
// thermostat.setThermostatMode
def setThermostatMode(mode) {
    TRACE("setThermostatMode(${mode})")

    switch (mode) {
    case "off":             return off()
    case "heat":            return heat()
    case "cool":            return cool()
    case "auto":            return auto()
    case "emergency heat":  return emergencyHeat()
    }

    log.error "Invalid thermostat mode: \'${mode}\'"
}

// thermostat.off
def off() {
    TRACE("off()")

    if (device.currentValue("thermostatMode") == "off") {
        return null
    }

    sendEvent([name:"thermostatMode", value:"off"])
    return writeTstatValue('tmode', 0)
}

// thermostat.heat
def heat() {
    TRACE("heat()")

    if (device.currentValue("thermostatMode") == "heat") {
        return null
    }

    sendEvent([name:"thermostatMode", value:"heat"])
    return writeTstatValue('tmode', 1)
}

// thermostat.cool
def cool() {
    TRACE("cool()")

    if (device.currentValue("thermostatMode") == "cool") {
        return null
    }

    sendEvent([name:"thermostatMode", value:"cool"])
    return writeTstatValue('tmode', 2)
}

// thermostat.auto
def auto() {
    TRACE("auto()")

    if (device.currentValue("thermostatMode") == "auto") {
        return null
    }

    sendEvent([name:"thermostatMode", value:"auto"])
    return writeTstatValue('tmode', 3)
}

// thermostat.emergencyHeat
def emergencyHeat() {
    TRACE("emergencyHeat()")
    log.warn "'emergency heat' mode is not supported"
    return null
}

// thermostat.setThermostatFanMode
def setThermostatFanMode(fanMode) {
    TRACE("setThermostatFanMode(${fanMode})")

    switch (fanMode) {
    case "auto":        return fanOff()
    case "circulate":   return fanCirculate()
    case "on":          return fanOn()
    }

    log.error "Invalid fan mode: \'${fanMode}\'"
}

// thermostat.fanAuto
def fanAuto() {
    TRACE("fanAuto()")

    if (device.currentValue("thermostatFanMode") == "auto") {
        //return null
    }
    log.debug "Fan Auto"

    sendEvent([name:"thermostatFanMode", value:"auto"])
    return writeTstatValue('fmode', 0)
}

// thermostat.fanCirculate
def fanCirculate() {
    TRACE("fanCirculate()")
    log.warn "Fan 'Circulate' mode is not supported"
    return null
}

// thermostat.fanOn
def fanOn() {
    TRACE("fanOn()")
	log.debug "Fan ON"
    if (device.currentValue("thermostatFanMode") == "on") {
        //return null
    }

    sendEvent([name:"thermostatFanMode", value:"on"])
    return writeTstatValue('fmode', 2)
}
def fanOff() {
	fanAuto()
    //log.debug "Fan Off"
    //TRACE("Fan OFF")
}

// thermostat.setHeatingSetpoint
def setHeatingSetpoint(tempHeat) {
    TRACE("setHeatingSetpoint(${tempHeat})")

    def ev = [
        name:   "heatingSetpoint",
        value:  tempHeat,
        unit:   getTemperatureScale(),
    ]

    sendEvent(ev)
    ev = [ 
    	name:   "thermostatSetpoint",
        value:  tempHeat,
        unit:   getTemperatureScale(),
    ]
	sendEvent(ev)
    if (getTemperatureScale() == "C") {
        tempHeat = temperatureCtoF(tempHeat)
    }

    return writeTstatValue('it_heat', tempHeat)
}
// thermostat.setHeatingOverride
def setHeatingOverride(tempHeat) {
    TRACE("setHeatingOverrideSetpoint(${tempHeat})")
	sendEvent([name:"heatingOverride",value: tempHeat,unit: getTemperatureScale()])

    if (getTemperatureScale() == "C") {
        tempHeat = temperatureCtoF(tempHeat)
    }

}

// thermostat.setCoolingSetpoint
def setCoolingSetpoint(tempCool) {
    TRACE("setCoolingSetpoint(${tempCool})")

    def ev = [
        name:   "coolingSetpoint",
        value:  tempCool,
        unit:   getTemperatureScale(),
    ]
    sendEvent(ev)
    ev = [ 
    	name:   "thermostatSetpoint",
        value:  tempCool,
        unit:   getTemperatureScale(),
    ]
    sendEvent(ev)

    if (getTemperatureScale() == "C") {
        tempCool = temperatureCtoF(tempCool)
    }
	//state.setCoolingTemp = 1
    return writeTstatValue('it_cool', tempCool)
}
// thermostat.setCoolingSetpoint
def remoteTemp(rem_temp) {
    TRACE("remoteTemp(${rem_temp})")

    def ev = [
        name:   "remote_temp",
        value:  rem_temp,
        unit:   getTemperatureScale(),
    ]

    sendEvent(ev)
    def ev2 = [
    	name: "rem_mode",
        value: "On"
    ]
	sendEvent(ev2)
    if (getTemperatureScale() == "C") {
        tempCool = temperatureCtoF(tempCool)
    }
    return writeRemoteValue('rem_temp', rem_temp)
}
def clearRemote() {
	TRACE("Clear Remote Temp")
	def ev = [
        name:   "remote_temp",
        value:  rem_temp,
        unit:   getTemperatureScale(),
    ]

    sendEvent(ev)
    return writeRemoteValue('rem_mode', 0)
}

def heatDown() {
    TRACE("heatDown()")

    def currentT = device.currentValue("heatingSetpoint")?.toFloat()
    if (!currentT) {
        return
    }

    def limit = 50
    def step = 1
    if (getTemperatureScale() == "C") {
        limit = 10
        step = 0.5
    }

    if (currentT > limit) {
        setHeatingSetpoint(currentT - step)
    }
}

def heatUp() {
    TRACE("heatUp()")

    def currentT = device.currentValue("heatingSetpoint")?.toFloat()
    if (!currentT) {
        return
    }

    def limit = 95
    def step = 1
    if (getTemperatureScale() == "C") {
        limit = 35
        step = 0.5
    }

    if (currentT < limit) {
        setHeatingSetpoint(currentT + step)
    }
}

def coolDown() {
    TRACE("coolDown()")

    def currentT = device.currentValue("coolingSetpoint")?.toFloat()
    if (!currentT) {
        return
    }

    def limit = 50
    def step = 1
    if (getTemperatureScale() == "C") {
        limit = 10
        step = 0.5
    }
/*
    if (currentT > limit) {
    	if(state.setCoolingTemp > 10) {
    		state.setCoolingTemp = state.setCoolingTemp - step
        } else {
        	state.setCoolingTemp = currentT - step
        }
        //setCoolingSetpoint(currentT - step)
        runIn(3,setCoolingSetpoint)
    }*/
    setCoolingSetpoint(currentT - step)
     
}

def coolUp() {
    TRACE("coolUp()")

    def currentT = device.currentValue("coolingSetpoint")?.toFloat()
    if (!currentT) {
        return
    }

    def limit = 95
    def step = 1
    if (getTemperatureScale() == "C") {
        limit = 35
        step = 0.5
    }
	/*
    if (currentT < limit) {
        if(state.setCoolingTemp > 10) {
    		state.setCoolingTemp = state.setCoolingTemp + step
        } else {
        	state.setCoolingTemp = currentT + step
        }
        //setCoolingSetpoint(currentT - step)
        runIn(3,setCoolingSetpoint)
    }
    */
    setCoolingSetpoint(currentT + step)
}

def holdOn() {
    TRACE("holdOn()")

    if (device.currentValue("hold") == "on") {
        return null
    }

    sendEvent([name:"hold", value:"on"])
    writeTstatValue("hold", 1)
}

def holdOff() {
    TRACE("holdOff()")

    if (device.currentValue("hold") == "off") {
        return null
    }

    sendEvent([name:"hold", value:"off"])
    writeTstatValue("hold", 0)
}

// polling.poll 
def poll() {
   // TRACE("poll()")
    return refresh()
}

// refresh.refresh
def refresh() {
	//STATE()
    //TRACE("refresh()")
    

    setNetworkId(confIpAddr, confTcpPort)
    return apiGet("/tstat")
}
def refreshRemote() {
	setNetworkId(confIpAddr, confTcpPort)
    TRACE("remote_temp update")
    return apiGet("/tstat/remote_temp")
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
	//TRACE("${httpRequest}")
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
    TRACE("writeTstatValue(${name}, ${value})")

    setNetworkId(confIpAddr, confTcpPort)

    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPost("/tstat", json),
        delayHubAction(2000),
        apiGet("/tstat")
    ]

    return hubActions
}
private def writeRemoteValue(name, value) {
    TRACE("writeRemoteValue(${name}, ${value})")

    setNetworkId(confIpAddr, confTcpPort)

    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPost("/tstat/remote_temp", json),
        delayHubAction(2000),
        apiGet("/tstat/remote_temp")
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
    TRACE("parseTstatData(${tstat})")

    def events = []
    if (tstat.containsKey("error_msg")) {
        log.error "Thermostat error: ${tstat.error_msg}"
        return null
    }

    if (tstat.containsKey("success")) {
        // this is POST response - ignore
        return null
    }

    if (tstat.containsKey("temp")) {
        //Float temp = tstat.temp.toFloat()
        def ev = [
            name:   "temperature",
            value:  scaleTemperature(tstat.temp.toFloat()),
            unit:   getTemperatureScale(),
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("t_cool")) {
        def ev = [
            name:   "coolingSetpoint",
            value:  scaleTemperature(tstat.t_cool.toFloat()),
            unit:   getTemperatureScale(),
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("t_heat")) {
        def ev = [
            name:   "heatingSetpoint",
            value:  scaleTemperature(tstat.t_heat.toFloat()),
            unit:   getTemperatureScale(),
        ]

        events << createEvent(ev)
    }

    if (tstat.containsKey("tstate")) {
        def value = parseThermostatState(tstat.tstate)
        if (device.currentState("thermostatOperatingState")?.value != value) {
            def ev = [
                name:   "thermostatOperatingState",
                value:  value
            ]

            events << createEvent(ev)
        }
    }

    if (tstat.containsKey("fstate")) {
        def value = parseFanState(tstat.fstate)
        if (device.currentState("fanState")?.value != value) {
            def ev = [
                name:   "fanState",
                value:  value
            ]

            events << createEvent(ev)
        }
    }

    if (tstat.containsKey("tmode")) {
        def value = parseThermostatMode(tstat.tmode)
        if (device.currentState("thermostatMode")?.value != value) {
            def ev = [
                name:   "thermostatMode",
                value:  value
            ]

            events << createEvent(ev)
        }
    }

    if (tstat.containsKey("fmode")) {
        def value = parseFanMode(tstat.fmode)
        if (device.currentState("thermostatFanMode")?.value != value) {
            def ev = [
                name:   "thermostatFanMode",
                value:  value
            ]

            events << createEvent(ev)
        }
    }

    if (tstat.containsKey("hold")) {
        def value = parseThermostatHold(tstat.hold)
        if (device.currentState("hold")?.value != value) {
            def ev = [
                name:   "hold",
                value:  value
            ]

            events << createEvent(ev)
        }
    }
    if (tstat.containsKey("rem_mode")) {
    	TRACE("rem_mode Match")
        def value = parseThermostatHold(tstat.rem_mode)
        TRACE("rem_mode $value")
        if (device.currentState("rem_mode")?.value != value) {
        	TRACE("rem_mode true")
            def ev = [
                name:   "rem_mode",
                value:  value
            ]

            events << createEvent(ev)
        }
    }

    TRACE("events: ${events}")
    return events
}

private def parseThermostatState(val) {
    def values = [
        "idle",     // 0
        "heating",  // 1
        "cooling"   // 2
    ]

    return values[val.toInteger()]
}

private def parseFanState(val) {
    def values = [
        "off",      // 0
        "on"        // 1
    ]

    return values[val.toInteger()]
}

private def parseThermostatMode(val) {
    def values = [
        "off",      // 0
        "heat",     // 1
        "cool",     // 2
        "auto"      // 3
    ]

    return values[val.toInteger()]
}

def heatOverrideDown() {
    TRACE("heatOverrideDown()")

    def currentT = device.currentValue("heatingOverride")?.toFloat()
    if (!currentT) {
        return
    }

    def limit = 50
    def step = 1
    if (getTemperatureScale() == "C") {
        limit = 10
        step = 0.5
    }

    if (currentT > limit) {
        setHeatingOverride(currentT - step)
    }
}

def heatOverrideUp() {
    TRACE("heatOverrideUp()")

    def currentT = device.currentValue("heatingOverride")?.toFloat()
    if (!currentT) {
        return
    }

    def limit = 80
    def step = 1
    if (getTemperatureScale() == "C") {
        limit = 35
        step = 0.5
    }

    if (currentT < limit) {
        setHeatingOverride(currentT + step)
    }
}

def coolOverrideDown() {
    TRACE("coolOverrideDown()")

    def currentT = device.currentValue("coolingOverride")?.toFloat()
    if (!currentT) {
    	setCoolingOverride(80)
        return
    }

    def limit = 50
    def step = 1
    if (getTemperatureScale() == "C") {
        limit = 10
        step = 0.5
    }

    if (currentT > limit) {
        setCoolingOverride(currentT - step)
    }
}

def coolOverrideUp() {
    TRACE("coolOverrideUp()")

    def currentT = device.currentValue("coolingOverride")?.toFloat()
    if (!currentT) {
        return
    }

    def limit = 95
    def step = 1
    if (getTemperatureScale() == "C") {
        limit = 35
        step = 0.5
    }

    if (currentT < limit) {
        setCoolingOverride(currentT + step)
    }
}

private def parseFanMode(val) {
    def values = [
        "auto",     // 0
        "circulate",// 1 (not supported by CT30)
        "on"        // 2
    ]

    return values[val.toInteger()]
}

private def parseThermostatHold(val) {
    def values = [
        "off",      // 0
        "on"        // 1
    ]

    return values[val.toInteger()]
}

private def scaleTemperature(Float temp) {
    if (getTemperatureScale() == "C") {
        return temperatureFtoC(temp)
    }

    return temp.round(1)
}

private def temperatureCtoF(Float tempC) {
    Float t = (tempC * 1.8) + 32
    return t.round(1)
}

private def temperatureFtoC(Float tempF) {
    Float t = (tempF - 32) / 1.8
    return t.round(1)
}

private def TRACE(message) {
    log.debug message
}

private def STATE() {
    //log.debug "deviceNetworkId : ${device.deviceNetworkId}"
    //log.debug "temperature : ${device.currentValue("temperature")}"
    //log.debug "heatingSetpoint : ${device.currentValue("heatingSetpoint")}"
    //log.debug "coolingSetpoint : ${device.currentValue("coolingSetpoint")}"
    //log.debug "thermostatMode : ${device.currentValue("thermostatMode")}"
    //log.debug "thermostatFanMode : ${device.currentValue("thermostatFanMode")}"
    //log.debug "thermostatOperatingState : ${device.currentValue("thermostatOperatingState")}"
    //log.debug "fanState : ${device.currentValue("fanState")}"
    //log.debug "hold : ${device.currentValue("hold")}"
}
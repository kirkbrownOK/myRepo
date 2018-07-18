/**
 *  OG&E Price Signal
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
include 'asynchttp_v1'

preferences {
	section("What time does smarthours start?") {
		input name: "startTime", title: "Turn On Time?", type: "time"
	}
	section("What time does smarthours stop?") {
		input name: "stopTime", title: "Turn Off Time?", type: "time"
	}   
}

metadata {
    definition (name:"OG&E Price Device", namespace:"kirkbrownOK", author:"Kirk Brown") {
        capability "Thermostat"
        capability "Temperature Measurement"
        capability "Sensor"
        //capability "Refresh"
        //capability "Polling"

        // Custom attributes
        attribute "priceNow", "number"
        attribute "offPeakPrice", "number"  
        attribute "onPeakPrice", "number"      
		attribute "criticalPeakPrice", "number"
        attribute "tomorrowOffPeakPrice", "number"
        attribute "tomorrowOnPeakPrice", "number"      
		attribute "tomorrowCriticalPeakPrice", "number"     
        
        command "setOffPeakPrice", ["number"]
        command "setOnPeakPrice", ["number"]
        command "setCriticalPeakPrice", ["number"]
        command "setTomOffPeakPrice", ["number"]
        command "setTomOnPeakPrice", ["number"]
        command "setTomCriticalPeakPrice", ["number"]
    }

    tiles {
        valueTile("offPeakPrice", "device.offPeakPrice") {
            state "temperature", label:'${currentValue}°', unit:"F",icon:"st.Appliances.appliances11",
                backgroundColors:[
                    [value: 6, color: "#66ff66"],
                    [value: 12, color: "#ffff66"],
                    [value: 25, color: "#ff8533"],
                    [value: 40, color: "#ff0000"]
                ]
        }

        valueTile("onPeakPrice", "device.onPeakPrice", inactiveLabel:false) {
            state "default", label:'${currentValue}°', unit:"F",
                backgroundColors:[
                    [value: 6, color: "#66ff66"],
                    [value: 12, color: "#ffff66"],
                    [value: 25, color: "#ff8533"],
                    [value: 40, color: "#ff0000"]
                ]
        }
        valueTile("criticalPeakPrice", "device.criticalPeakPrice", inactiveLabel:false) {
            state "default", label:'${currentValue}°', unit:"F",
                backgroundColors:[
                    [value: 6, color: "#66ff66"],
                    [value: 12, color: "#ffff66"],
                    [value: 25, color: "#ff8533"],
                    [value: 40, color: "#ff0000"]
                ]
        }
		valueTile("priceNow", "device.priceNow", inactiveLabel:false) {
            state "default", label:'${currentValue}°', unit:"F",
                backgroundColors:[
                    [value: 6, color: "#66ff66"],
                    [value: 12, color: "#ffff66"],
                    [value: 25, color: "#ff8533"],
                    [value: 40, color: "#ff0000"]
                ]
        }
        standardTile("refresh", "device.thermostatMode", inactiveLabel:false, decoration:"flat") {
            state "default", icon:"st.secondary.refresh", action:"refresh.refresh"
        }

        main(["priceNow"])

        details(["offPeakPrice", "onPeakPrice", "criticalPeakPrice","priceNow","refresh"])
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


def installed() {
	log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated(settings) {
	unschedule()
	initialize()
}

def initialize() {
	log.debug "$startTime"
    if (state.configured) {
    	//do nothing
    } else {
    	setOffPeakPrice(5)
        setOnPeakPrice(5)
        setCriticalPeakPrice(0)
        setTomOffPeakPrice(5)
        setTomOnPeakPrice(5)
        setTomCriticalPeakPrice(0)
        state.configured = 1
    }
	schedule(startTime, "onPeakPriceStarts")
	schedule(stopTime, "offPeakPriceStarts")
    def pair = startTime.split("T")
    pair = pair[1].trim()
    pair = pair.split(":")
    def startHour = pair[0]
    log.debug "startTime: ${startTime} hour: ${startHour}"
    def myDate = new Date()
    def myHour =myDate.getHours()-5
    if(myHour > 13) {
    	onPeakPriceStarts()
    } else if (myHour > 18) {
    	offPeakPriceStarts()
    }
}
def parse(String message) {
    TRACE("parse(${message})")
    /*
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
	*/
    return null
}


// polling.poll 
def poll() {
    TRACE("poll()")
    return refresh()
}

// refresh.refresh
def refresh() {
	def params = [
        uri:  'https://api.github.com',
        contentType: 'application/json'
    ]
    def data = [key1: "hello world"]

    asynchttp_v1.get('responseHandlerMethod', params, data)
	
    TRACE("refresh() ")
    //STATE()
	
    //return apiGet("/OK_PriceSignal/")
}

def responseHandlerMethod(response, data) {
    log.debug "got response data: ${response.getData()}"
    log.debug "data map passed to handler method is: $data"
}
/*
private apiGet(String path) {
    TRACE("apiGet(${path})")

    def headers = [
        HOST:       "secure.oge.com",					*/
//        Accept:     "*/*",
/*        "Upgrade-Insecure-Requests": "1"

    ]

    def httpRequest = [
        method:     'GET',
        path:       path,
        headers:    headers
    ]
	TRACE("${httpRequest}")
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

    TRACE("events: ${events}")
    return events
    
    return
}
*/
def setOffPeakPrice(value) {
	sendEvent(name:"offPeakPrice", value: value)
}
def setOnPeakPrice(value) {
	sendEvent(name:"onPeakPrice", value: value)
}
def setCriticalPeakPrice(value) {
	sendEvent(name:"criticalPeakPrice", value: value)
}
def setTomOffPeakPrice(value) {
	sendEvent(name:"tomorrowOffPeakPrice", value: value)
}
def setTomOnPeakPrice(value) {
	sendEvent(name:"tomorrowOnPeakPrice", value: value)
}
def setTomCriticalPeakPrice(value) {
	sendEvent(name:"tomorrowCriticalPeakPrice", value: value)
}
def offPeakPriceStarts() {
	sendEvent(name:"priceNow", value: currentValue("offPeakPrice"))
}
def onPeakPriceStarts() {
	state.peakPrice = device.currentValue("onPeakPrice")
    if(device.currentValue("criticalPeakPrice") > 0) {
    	state.peakPrice = device.currentValue("criticalPeakPrice")
    }
    TRACE("Sending Peak price: ${state.peakPrice}")
	sendEvent(name:"priceNow",value: state.peakPrice)
}
private def TRACE(message) {
    log.debug message
}
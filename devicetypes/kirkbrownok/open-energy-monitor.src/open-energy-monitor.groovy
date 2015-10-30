/**
 *  Reads power measurements from arduino open energy monitor sensor.
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
    input "confIpAddr", "string", title:"Arduino IP Address", defaultValue: "192.168.0.121", required:true, displayDuringSetup: true
    input "confTcpPort", "number", title:"TCP Port", defaultValue:"80", required:true, displayDuringSetup:true
    input "energyControl", "number", title:"Manual Energy Set Value", defaultValue:"0.0", required:false, displayDuringSetup:true
    input "minutesBeforeTimout", "number", title: "How many minutes before timeout error?", defaultVlaue: "5", required: true, displayDuringSetup:true
    input "meterNumber" , "string", title: "What is your plotwatt meter number?", required: true, displayDuringSetup: true
    input "plotwattApiKey", "string", title: "What is your plotwatt api key?", required: true, displayDuringSetup:true
        
}
metadata {
	definition (name: "Open Energy Monitor", namespace:"kirkbrownOK", author:"Kirk Brown") {
	capability "Energy Meter"
	capability "Power Meter"        
    capability "Sensor"
    capability "Refresh"
    capability "Polling"
    capability "Switch"


    // Custom attributes
    attribute "ardPower", "number"
    attribute "lastResetTime", "number"
	attribute "failedMsg", "number"
	attribute "lastMess", "number"
        // Custom commands
	command "newDay"
	command "resetDevice"

	}

	simulator {
		// TODO: define status and reply messages here
	}

    tiles {
	standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
    	standardTile("resetDay", "device.power", inactiveLabel: false, decoration: "flat") {
			state "default", label:'reset Meter', action:"newDay", icon: "st.Outdoor.outdoor20"
		}
        valueTile("power", "device.power") {
			state "default", label:'${currentValue} W', action:"refresh",canChangeIcon: true, icon: "st.Electronics.electronics13"
		}
		valueTile("energy", "device.energy") {
			state "default", label:'${currentValue} kWh',action: "refresh"
		}
        	valueTile("ardPower", "device.ardPower") {
			state "default", label:'Ard: ${currentValue} kWh',action:"refresh"
		}
        valueTile("reset", "device.lastResetTime", inactiveLabel: false, decoration: "flat") {
			state "default", label:'RST: ${currentValue}', action:"resetDevice"
		}
        valueTile("failedMsg", "device.failedMsg", inactiveLabel: false, decoration: "flat") {
			state "default", label:'FMC: ${currentValue}', action:"refresh.refresh"
		}	
        valueTile("PWsuccess", "device.PWsuccess", inactiveLabel: false, decoration: "flat") {
			state "PWsuccess", label:'Successful ${currentValue}', action:"refresh.refresh", 
            backgroundColors:[
                    [value: 1, color: "#44b621"]
                ]
		}
        valueTile("PWfailures", "device.PWfailures", inactiveLabel: false, decoration: "flat") {
			state "PWfailures", label:'Failures ${currentValue}', action:"refresh.refresh",canChangeIcon: true, 
            backgroundColors:[
                    [value: 1, color: "#bc2323"]
                ] 
                   
		}

        main(["power"])

        details(["power", "energy","ardPower","refresh","resetDay", "reset","failedMsg","PWsuccess","PWfailures"])
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
            //log.error "HTTP response has no body"
            return null
        }

        def body = new String(msg.body.decodeBase64())
        //TRACE(body)
        def slurper = new JsonSlurper()
        def tstat = slurper.parseText(body)
        float EMonSum = tstat.EMpower[0].toFloat()
        int removeEvery = 0
        int numOfSamples = tstat.EMpower.size()
        int csvTime = (now()/1000).toFloat().round()
        TRACE("Number of samples: ${numOfSamples} at ${csvTime}")
        //def csv = "${meterNumber},${tstat.EMpower[0].toFloat()},${csvTime-numOfSamples}"
        def csv = "${meterNumber},${tstat.EMpower[0].toFloat()},${csvTime-numOfSamples}"
        state.removeCounter = 1
        if (numOfSamples > 100) {
        	removeEvery = 2
        } else if (numOfSamples > 63) {
        	removeEvery = 3
        } else { 
        	removeEvery = 0
        }
        TRACE("Remove Every: ${removeEvery}")
        for (i in 1..numOfSamples - 1) {          	
                EMonSum += tstat.EMpower[i].toFloat()
                if (state.removeCounter == removeEvery){
                
                    state.removeCounter = 1
                } else {
                //	csv = "${csv},${meterNumber},${tstat.EMpower[i].toFloat()},${csvTime-numOfSamples+i}"
                	csv = "${csv},${meterNumber},${tstat.EMpower[i].toFloat()},${csvTime-numOfSamples+i}"
                    state.removeCounter = state.removeCounter +1
                }
		}
                //"Host": "plotwatt.com",	
        def postParams = [
        uri: "https://www.plotwatt.com",
        path: "/api/v2/push_readings",
        headers: [ 
        	Host : "plotwatt.com",
        	Authorization : "Basic ${plotwattApiKey.encodeBase64()}",
            Connection: "close"
        ],
        body: csv
        ]
        log.debug postParams
        try {
    		httpPost(postParams) {   
            resp -> 
            resp.headers.each {
            	//log.debug "${it.name} : ${it.value}"
                if (it.name == "Content-Length") {
                	//log.debug "CL: ${it.value}"
                    if (it.value == "2") {
                    	log.info "OK Success"
                    	state.PWsuccess = state.PWsuccess + 1
                		sendEvent([name: "PWsuccess", value: state.PWsuccess])
                    } else {
                    	state.PWfailures = state.PWfailures + 1
                		sendEvent([name: "PWfailures", value: state.PWfailures])
                    }
                }
                
        	}
                
    		}
		} catch (e) {
        	if ( e == "groovy.lang.StringWriterIOException: java.io.IOException: Stream closed") {
            	log.info "stream closed as expected"
                state.PWsuccess = state.PWsuccess + 1
                sendEvent([name: "PWsuccess", value: state.PWsuccess])
            } else {
    			log.debug "something went wrong: $e"
                state.PWfailures = state.PWfailures + 1
                sendEvent([name: "PWfailures", value: state.PWfailures])
            }
	    }
        
        state.empower = (EMonSum/numOfSamples)  
        TRACE("EM SUM: ${EMonSum} Num of Samples: ${numOfSamples} Average: ${state.empower}")
        return parseTstatData(tstat[1])
    } else if (msg.containsKey("simulator")) {
        // simulator input
        return parseTstatData(msg)
    }

    return null
}
def updated() {
	if(energyControl > 0) {
    	state.engerySum = energyControl
        state.ardPower = energyControl
        //energyControl = 0.0
    }
    if (state.lastTimeReceived > 0) {
    	//TRACE("LTR established")
    } else {
		state.lastTimeReceived = now()
        TRACE("LTR Corrected")
    }
    if (state.failedMessageCount >= 0) {
    	//TRACE("FMC established")
    } else {
        state.failedMessageCount = 0
        TRACE("FMC Corrected")
    }
    if (state.PWsuccess >= 0) {
    
    } else {
    	state.PWsuccess = 0
        sendEvent([name: "PWsuccess", value: state.PWsuccess])
    }
    if (state.PWfailures >=0) {
    
    } else {
    	state.PWfailures = 0
        sendEvent([name: "PWfailures", value: state.PWfailures])
    }
    
    //TRACE("Updated->Reset Counters")
}




// polling.poll 
def poll() {
    TRACE("poll()")
    
    return refresh()
}

// refresh.refresh
def refresh() {
	
	if ((now() - state.lastTimeReceived)/1000 > (2 * 60 )) {
    	state.failedMessageCount = state.failedMessageCount + 1
    	TRACE("Refresh failed to work for ${(now() - state.lastTimeReceived)/1000}")
    	sendEvent([name:"failedMsg", value: state.failedMessageCount ])	
        state.lastTimeReceived = now() 
    }
    TRACE("refresh()")
    //STATE()

    setNetworkId(confIpAddr, confTcpPort)
    return apiGet("/meter")
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
        apiPost("/meter", json),
        delayHubAction(10000),        
        
        apiGet("/meter")
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
        log.error "error: ${tstat.error_msg}"
        
        return null
    }

    if (tstat.containsKey("success")) {
        // this is POST response - ignore
        return null
    }
 
	if (tstat.containsKey("usC")) {
        def ev = [
            name:   "power",
            //value:  ((tstat.usC.toFloat() + tstat.usD.toFloat())*1000.0).round(0)
            value: (state.empower*1000).round(0)
        ]
        events << createEvent(ev)
        if(device.currentState("energy").date.getHours() == 0) {
        	//TRACE("Correct Hour for new day")
            if(state.dayHasBeenReset) {
            	//TRACE("Day has been Reset")
            } else {
            	newDay()
            }
        } else {
        	state.dayHasBeenReset = false
        
        }
        state.energySum = ((tstat.usC.toFloat() + tstat.usD.toFloat())*tstat.lastRefresh.toFloat()/3600.0)+state.energySum
        ev = [
            name:   "energy",
            //value: 24
            //value:  device.currentState("energy")?.toFloat() + ((tstat.usC.toFloat() + tstat.usD.toFloat())*tstat.lastRefresh.toFloat()/3600.0).round(1)
        	value: state.energySum.round(1)
        ]
        //TRACE(ev)
        events << createEvent(ev)
        state.ardPower = tstat.STpower.toFloat() + state.ardPower
        ev = [
            name:   "ardPower",
            //value: 24
            //value:  device.currentState("energy")?.toFloat() + ((tstat.usC.toFloat() + tstat.usD.toFloat())*tstat.lastRefresh.toFloat()/3600.0).round(1)
        	value: state.ardPower.round(2)
        ]
        //TRACE(ev)
        events << createEvent(ev)
        if ( tstat.lastResetTime == device.currentValue("lastResetTime")) {
            TRACE("lastReset received but same as last time")
            state.failedMessageCount ++
        } /*else if(tstat.lastMess == 0){
        	//Last message to PlotWatt failed
            TRACE("Last Plotwatt message failed")
            //state.failedMessageCount ++
            sendEvent([name: "lastMess", value: 0])
        
        } */else {
            state.failedMessageCount = 0 //Valid message received
            state.lastTimeReceived = now()
            
            
            events << createEvent(ev)
        }
        ev = [ name: "failedMsg", value: state.failedMessageCount ]
        events << createEvent(ev)        
        ev = [
                name:   "lastResetTime",
                value:  tstat.lastResetTime
            ]
            events << createEvent(ev)
        
        
	}

    //TRACE("events: ${events}")
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

private def newDay() {
	state.energySum = 0.0
    state.ardPower = 0.0
    state.dayHasBeenReset = true
    state.PWfailures = 0
    state.PWsuccess = 0
	TRACE("newday")
	sendEvent([name: "energy", value: state.energySum, descriptionText:"New Day Triggered",linkText:"EnergyMon"])
    sendEvent([name: "ardPower", value: state.ardPower, descriptionText:"New Day Triggered",linkText:"EnergyMon"])
    sendEvent([name: "PWsuccess", value: state.PWsuccess, descriptionText: "Restart Counter"])
	sendEvent([name: "PWfailures", value: state.PWfailures, descriptionText: "Restart Counter"])
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

/*
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
*/
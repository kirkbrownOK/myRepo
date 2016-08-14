/**
 *  Wink Spotter using API 
 *
 *  --------------------------------------------------------------------------
 *
 *  Copyright (c) 2016 okpowerman
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
    input("wink_client_id", "text", title:"Wink API Client ID", required:true, displayDuringSetup: true)
    input("wink_client_secret", "string", title:"Wink Client Secret", required:true, displayDuringSetup:true)
    input("user_id", "string", title: "Wink User ID", required:true, displayDuringSetup:true)
    input("user_password","string", title: "Wink User password", required:true, displayDuringSetup:true)
}

metadata {
    definition (name:"Wink Spotter", namespace:"okpowerman", author:"okpowerman@gmail.com") {
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"
        
        attribute "vibration", "string"
        attribute "loudness", "string"
        attribute "brightness", "number"

        
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
        standardTile("brightness","device.brightness", width: 1, height: 1) {
        	state "default", label:'${currentValue}', action: "refresh",
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
        valueTile("vibration","device.vibration", width: 1, height: 1) {
        	state "default", label:'Default', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "true", label:'T: ${currentValue}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "false", label:'F: ${currentValue}', icon: "st.contact.contact.closed", backgroundColor: "##79b821"
        }
        standardTile("loudness","device.loudness", width: 1, height: 1) {
        	state "default", label:'Default', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "true", label:'T: ${currentValue}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "false", label:'F: ${currentValue}', icon: "st.contact.contact.closed", backgroundColor: "##79b821"
        }
		standardTile("refresh", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}


        main(["temperature"])

        details(["temperature", "humidity", "vibration", "loudness", "brightness", "refresh"])
    }

    simulator {
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
    TRACE("refresh()")
    STATE()
	
    //setNetworkId(confIpAddr, confTcpPort)
    //return apiGet("/bedroom")
    log.debug "Starting Oauth"
    httpOauth()
    log.debug "Finished Oauth"
    httpListDevices()    
    httpGetSensorPod()
    return
}


private httpOauth() {	
	log.debug "wci: ${wink_client_id} wci.value: ${wink_client_id.value}"
    state.wci = "${wink_client_id.value}"
    def params = [
    	//uri: "https://private-e5232-wink.apiary-mock.com",
        uri: "https://winkapi.quirky.com",
        path: "/oauth2/token",
        headers: ["content-type": "application/json"],
        body: [
        	client_id: "e9af631f3850d6a3b66751daa60a0707",
    		client_secret: "c9718935c0ec65bb5208371ad8f6f83b",
    		username: "tokirkbrown@gmail.com",
    		password: "Kimberly12",
            grant_type: "password" 
/*
        	client_id: "consumer_key_goes_here",
    		client_secret: "consumer_secret_goes_here",
    		username: "user@example.com",
    		password: "password_goes_here",
			grant_type: "password" */
        ]
    ]
    log.debug "httpOauth: ${params}"
	try {
    	httpPostJson(params) { resp ->
        	//log.debug "response data: ${resp.data}"
            log.debug "resp.data.access_token ${resp.data.access_token}"
            state.access_token = resp.data.access_token
            state.refresh_token = resp.data.refresh_token
        	
    	}
	} catch (e) {
    	log.debug "something went wrong: $e"
	}

}

private httpListDevices() {	
    def params = [
    	//uri: "https://private-e5232-wink.apiary-mock.com",
        uri: "https://winkapi.quirky.com",
        path: "/users/me/wink_devices",
        headers: ["Authorization":"Bearer ${state.access_token}"],
    ]
    log.debug "httpListDevices: ${params}"
	try {
    	httpGet(params) { resp ->
        	//log.debug "response data: ${resp.data}" 
            //log.debug "resp.data.data ${resp.data.data}"
            //log.debug "response data.sensor_pod_id ${resp.data.data.sensor_pod_id[0]}"
            
    	}
	} catch (e) {
    	log.debug "something went wrong: $e"
	}

}
private httpGetSensorPod() {
    def params = [
    	//uri: "https://private-e5232-wink.apiary-mock.com",
        uri: "https://winkapi.quirky.com",
        path: "/sensor_pods/116040",
        headers: ["Authorization":"Bearer ${state.access_token}"]
    ]
    log.debug "httpGetSensorPod 116040"
	try {
    	httpGet(params) { resp ->
        	//log.debug "Get data: ${resp.data}" 
            log.debug "Get Last Reading ${resp.data.data.last_reading}"
            state.lastTempC = resp.data.data.last_reading.temperature
            state.lastTempF = state.lastTempC*1.8 + 32.0
            state.lastHumidity = resp.data.data.last_reading.humidity
            state.vibration = resp.data.data.last_reading.vibration
            state.brightness = resp.data.data.last_reading.brightness
            state.loudness = resp.data.data.last_reading.loudness
            log.debug "TempC ${state.lastTempF} and Humidity ${state.lastHumidity} vibration ${state.vibration} brightness ${state.brightness} loudness ${state.loudness}" 
            def ev = [
            	name:   "temperature",
            	value:  state.lastTempF,
            
        	]
            sendEvent(ev)
            ev = [
            	name: "humidity",
                value: state.lastHumidity,
            
            ]
            sendEvent(ev)
            ev = [
            	name: "vibration",
                value: state.vibration,
            
            ]
            sendEvent(ev)
            ev = [
            	name: "brightness",
                value: state.brightness,
            
            ]
            sendEvent(ev)
            ev = [
            	name: "loudness",
                value: state.loudness,
            
            ]
            sendEvent(ev)          
    	}
	} catch (e) {
    	log.debug "something went wrong: $e"
	}	
}

private def delayHubAction(ms) {
    return new physicalgraph.device.HubAction("delay ${ms}")
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
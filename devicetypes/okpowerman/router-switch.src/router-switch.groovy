/**
 *  Copyright 2016 Kirk Brown
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
    input "confIpAddr", "string", title:"Router IP Address", defaultValue: "192.168.0.1", required:true, displayDuringSetup: true
    input "confTcpPort", "number", title:"TCP Port", defaultValue:"8081", required:true, displayDuringSetup:true       
}
metadata {
	definition (name: "Router Switch", namespace: "okpowerman", author: "Kirk Brown") {
		capability "Actuator"
		capability "Button"
		capability "Sensor"

        capability "Switch"
        command "push1"
        command "hold1"
	}

	simulator {

	}
	tiles {
		standardTile("button", "device.button", width: 1, height: 1) {
			state "default", label: "", icon: "st.unknown.zwave.remote-controller", backgroundColor: "#ffffff"
		}
 		standardTile("push1", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Push 1", backgroundColor: "#ffffff", action: "push1"
		} 
 		standardTile("hold1", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Hold 1", backgroundColor: "#ffffff", action: "hold1"
		} 
        standardTile("on1", "device.switch", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Reset", backgroundColor: "#ffffff", action: "on"
		} 
		main "button"
		details(["button","push1","hold1", "on1"])
	}
}

def parse(String message) {
    TRACE("parse(${message})")
	//log.debug "Basic ${plotwattApiKey.encodeAsBase64()}"
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
        TRACE(body)
	}
    
	return null
}

def hold1() {
	sendEvent(name: "button", value: "held", data: [buttonNumber: "1"], descriptionText: "$device.displayName button 1 was held", isStateChange: true)
} 

def push1() {
	sendEvent(name: "button", value: "pushed", data: [buttonNumber: "1"], descriptionText: "$device.displayName button 1 was pushed", isStateChange: true)
}

// refresh.refresh
def refresh() {
	
    TRACE("refresh()")
    //STATE()

    setNetworkId(confIpAddr, confTcpPort)
    return apiGet("/userRpm/LoginRpm.htm?Save=Save")
}

private on() {
	refresh()
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
        Accept:     "*/*",
        Cookie:		"Authorization=Basic YWRtaW46MmQyZjUzM2JlZWM1MGIyMzc5MzQxNzJmMTg5M2IwMjg="
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
private def TRACE(message) {
    log.debug message
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
/**
 *  Verify DVR is online
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
    input("MinutesOfError","number",title: "Only send error event if in error condition for this many minutes: ", defaultValue:10, required: false)
    input("myURL","string",title: "The URL of the camera", defaultValue: "http://q2217.xipcam.com", required: true)
}
metadata {
	definition (name: "DVR Inspector", namespace:"kirkbrownOK", author:"Kirk Brown") {
	capability "Contact Sensor"
        capability "Switch"
        capability "Refresh"
        capability "Polling" 
        
        command "beenReset"

	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles { 
        standardTile("refresh", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("dvrStatus", "device.switch", width: 1, height: 1, inactiveLabel:false) {
				state "on", label: 'Normal', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#79b821"
				state "off", label: 'No Data', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#FF0000"
                state "error", label: 'Error', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#000000"
			}
         standardTile("dvrStatusNow", "device.switch2", width: 1, height: 1, inactiveLabel:false) {
				state "on", label: 'Normal', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#79b821"
				state "off", label: 'No Data', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#FF0000"
                state "error", label: 'Error', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#000000"
			}   
		// TODO: define your main and details tiles here
        main(["dvrStatus"])

        details(["dvrStatus","dvrStatusNow","refresh"])
	}
}

// parse events into attributes
def parse(String description) {
    TRACE("parse(${description})")

}

// polling.poll 
def poll() {
    TRACE("poll()")
    return refresh()
}

// refresh.refresh
def refresh() {
    
	def params = [
 /*   	uri: "https://data.sparkfun.com",
    	path: "/output/${PUBLICKEY}.json",
        contentType: 'application/json',
        query: ['gt[timestamp]': "now-${MinutesToAverage}min"],*/
       // headers: [host: "data.sparkfun.com", accept: "*/*" ]
        uri: "${myURL}:88",
    	path: "/"//,
        //contentType: 'application/json',
        //query: ["gt[timestamp]":"now-${SAMPLESTOAVERAGE}min"]
		]
	
	try {
    	TRACE(params)
    	httpGet(params) { resp ->
        	//resp.headers.each {
        	//	log.debug "${it.name} : ${it.value}"
    		//}
            //log.debug "response server: ${resp.headers.server}"   
            if( resp.headers.server == "WCY_WEBServer/2.0") {
                log.debug "DVR Server Detected!!!"
                sendEvent([name: "switch", value: "on"])
                sendEvent([name: "switch2", value: "on"])
                state.successfulMessage = now()
            } else {
            	log.error "Something returned a message but it wasn't the DVR"
            }
            //log.debug "response contentType: ${resp.contentType}"
            //log.debug "response data: ${resp.data}"  
        }
                   
			      
    } catch (e) {
    		sendEvent([name:"switch2",value:"off"])
    		log.error "something went wrong: $e"
            state.delta=(now()-state.successfulMessage)/60000
            log.debug "Since successful MSG: ${state.delta}"
            if(now()-state.successfulMessage>MinutesOfError*60000) { 
            	sendEvent([name:"switch",value:"off"])
            }    
		}
    return    
}
def beenReset() {
	log.debug "Cameras been reset"
	state.successfulMessage = now()
    sendEvent([name: "switch", value: "on"])
}

private def TRACE(message) {
    log.debug message
}
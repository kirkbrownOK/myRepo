/**
 *  SparkFun Data (Phant) Using 1 Page of Data
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
    input("PUBLICKEY", "string", title:"Data Sparkfun PUBLIC KEY", defaultValue: "...",
        required:true, displayDuringSetup: true)
    input("PRIVATEKEY", "string", title:"Data Sparkfun PRIVATE KEY",
        defaultValue:"...", required:true, displayDuringSetup:true)
    input("SAMPLESTOAVERAGE", "number", title: "Minutes of Phant Data", defaultValue: 5, required: true, displayDuringSetup:true)
    input("ignoreZero","number", title: "Should I ignore 0? 1 is yes, 0 is no: ", defaultValue: 0)
    input("MinutesOfError","number",title: "Only send error event if in error condition for this many minutes: ", defaultValue:10, required: false)
    
}
metadata {
	definition (name: "SparkFun Data (Phant) 1 Page of Data", namespace:"kirkbrownOK", author:"Kirk Brown") {
	capability "Contact Sensor"
	capability "Temperature Measurement"
        capability "Switch"
        capability "Refresh"
        capability "Polling"
        
        //custom attribute 
        
        attribute "led", "number"
        

	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles { 
            valueTile("temperature", "device.temperature", width: 1, height: 1, canChangeIcon: true, icon: "st.Weather.weather2") {
        	state "temperature", label:'${currentValue}°', action: "refresh",
            	inactiveLabel:false,
                backgroundColors:[
                    [value: 31, color: "#153591"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
                    [value: 74, color: "#44b621"],
                    [value: 84, color: "#f1d801"],
                    [value: 95, color: "#d04e00"],
                    [value: 100, color: "#bc2323"]
                ]
        }
         valueTile("led", "device.led", width: 1, height: 1) {
        	state "led", label:'${currentValue}\nLED', action: "refresh",
            	inactiveLabel:false,
                backgroundColors:[
                    [value: 31, color: "#153591"],
                    [value: 100, color: "#1e9cbb"],
                    [value: 200, color: "#90d2a7"],
                    [value: 300, color: "#44b621"],
                    [value: 500, color: "#f1d801"],
                    [value: 750, color: "#d04e00"],
                    [value: 1000, color: "#bc2323"]
                ]
        }
        standardTile("refresh", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("tempStatus", "device.switch", width: 1, height: 1, inactiveLabel:false) {
				state "on", label: 'Normal', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#79b821"
				state "off", label: 'No Data', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#FF0000"
                state "error", label: 'Error', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#000000"
			}
		// TODO: define your main and details tiles here
        main(["temperature"])

        details(["temperature", "led", "refresh","tempStatus"])
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
        uri: "http://data.sparkfun.com",
    	path: "/output/${PUBLICKEY}.json",
        contentType: 'application/json',
        query: ["page":"1"]
		]
	try {
    	TRACE(params)
/*    	httpGet(params) { resp ->
        	resp.headers.each {
        	log.debug "${it.name} : ${it.value}"
    		}
    	log.debug "response contentType: ${resp.contentType}"
    	log.debug "response data: ${resp.data}"        
        }*/
   	httpGet(params) { resp ->
        	//resp.headers.each {
        	//	TRACE( "${it.name} : ${it.value}")
            //}
            
            TRACE("data: ${resp.data}")
            //TRACE("data size ${resp.data.size()}")
            state.minutesoferror = MinutesOfError
            if (resp.data.size <1) {
            	log.error("There is no data in the queried time frame")
                sendEvent([name: "switch", value: "off",descriptionText: "No response from Sparkfun"])
                return              
            } else if( state.timeStamp == resp.data.timestamp[0]) {
            	state.timeStampError = (now() - state.lastSuccessfulTimestamp)/60000
            	//This occurs when the timestamp is the same across multiple data retrievals
                if(state.timeStampError > state.minutesoferror){
            		
                    log.error("Timestamp isn't changing for ${state.timeStampError} minutes")
                	sendEvent([name: "switch", value: "error",desriptionText:"Temperature is not updating on Sparkfun"])
                    state.lastSuccessfulTimestamp = now()
                    return
                } else {
                	TRACE("Timestamp didn't change. BUT its only been ${state.timeStampError} minutes")
                }
            	
            } else {
                int ledSum = 0
                int temperatureSum = 0
                int numOfSamples = resp.data.light.size()
                state.timeStamp = resp.data.timestamp[0]
                state.lastSuccessfulTimestamp = now()
                if(numOfSamples > SAMPLESTOAVERAGE*2) {
                    numOfSamples = SAMPLESTOAVERAGE*2
                } 

                for (i in 0..numOfSamples-1) {

                    if((ignoreZero == 1) && (resp.data.dalt[i].toFloat() <= 1)) {
                        //log.debug "SKIPPING: ${i} led: ${resp.data.led[i].toFloat()} pulset: ${resp.data.pulset[i].toFloat()} temp: ${resp.data.dalt[i].toFloat()} flame: ${resp.data.flame[i].toFloat()}"
                        numOfSamples --

                    } else {
                        //log.debug "${i} led: ${resp.data.led[i].toFloat()} pulset: ${resp.data.pulset[i].toFloat()} temp: ${resp.data.dalt[i].toFloat()} flame: ${resp.data.flame[i].toFloat()}"
                        ledSum += resp.data.light[i].toFloat()
                        temperatureSum += resp.data.dalt[i].toFloat()
                    }    

                }
                TRACE( "led: ${ledSum} temp: ${temperatureSum} samples to Avg: ${SAMPLESTOAVERAGE} numOfSamples: ${numOfSamples}")
                /*
                if(numOfSamples > SAMPLESTOAVERAGE) {
                    numOfSamples = SAMPLESTOAVERAGE
                }
                */
                state.led = (ledSum/numOfSamples)
                state.temperature = (temperatureSum/numOfSamples)
                state.led = state.led.toFloat().round(0)
                state.temperature = state.temperature.toFloat().round(1)


                TRACE ("LED avg: ${state.led} temp: ${state.temperature}")
                state.successfulMessage = now()
                sendEvent([name: "temperature", value: state.temperature, unit: "F"])
                sendEvent([name: "led", value: state.led])
                sendEvent([name: "switch", value: "on"])           
            
			}
		}      
    } catch (e) {
    		
    		log.error "something went wrong: $e"
            state.delta=(now()-state.successfulMessage)/60000
            log.error "Minutes since successful MSG: ${state.delta}"
            if(now()-state.successfulMessage>MinutesOfError*60000) { 
            	sendEvent([name:"switch",value:"error",descriptionText: "In timeout error for ${state.delta} minutes"])
            }    
		}
    return    
}

private def TRACE(message) {
    log.debug message
}
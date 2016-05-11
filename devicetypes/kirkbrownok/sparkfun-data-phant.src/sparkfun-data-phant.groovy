/**
 *  SparkFun Data (Phant) Illuminence Sensor facing an information LED on my water Softener. Temp measurement for fun
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
    input("pilotOutThreshold", "number", title: "What value is reported when the pilot is out?", defaultValue: 80)
    input("pilotOnThreshold", "number", title: "What value is reported when the pilot is on?", defaultValue: 300)
    input("ignoreZero","number", title: "Should I ignore 0? 1 is yes, 0 is no: ", defaultValue: 0)
    input("minuteDelayHeating","number", title: "How many minutes to wait after Heating for sensor to normalize?", required: false)
    input("pilotOutThresholdDelay","number", title: "What value reported after heating to signal out to override heating delay", required: false)
	input("MinutesOfError","number",title: "Only send error event if in error condition for this many minutes: ", defaultValue:10, required: false)
    
}
metadata {
	definition (name: "SparkFun Data (Phant)", namespace:"kirkbrownOK", author:"Kirk Brown") {
		capability "Contact Sensor"
		capability "Temperature Measurement"
        capability "Switch"
        capability "Refresh"
        capability "Polling"
        
        //custom attribute 
        attribute "waterSoftener", "string"
        attribute "led", "number"
        attribute "pulseTime", "number"
        attribute "flameSensor", "number"
        attribute "flameSensorState", "string"
        
        command "refresh"

	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
            standardTile("waterSoftener", "device.switch", width: 1, height: 1, inactiveLabel:false) {
				state "off", label: 'Normal', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#79b821"
				state "on", label: 'Low Salt', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#FF0000"
                state "error", label: 'Error', action: "refresh", icon: "st.Bath.bath13", backgroundColor: "#000000"
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
                    [value: 100, color: "#bc2323"]
                ]
        }
        valueTile("pulseTime", "device.pulseTime", width: 1, height: 1) {
        	state "pulseTime", label:'${currentValue}', action: "refresh",
            	inactiveLabel:false,
                backgroundColors:[
                    [value: 0, color: "#79b821"],
                    [value: 400, color: "#FF0000"],
                    [value: 550, color: "#FF0000"],
                    [value: 600, color: "#FFFFFF"]
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
         valueTile("flameSensor", "device.flameSensor", width: 1, height: 1) {
        	state "default", label:'FLM:\n${currentValue}', action: "refresh",
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
        standardTile("flameSensorState", "device.flameSensorState", width: 1, height: 1) {
    		state "off", label: 'Out', action: "refresh", icon: "st.Weather.weather7", backgroundColor: "#0000FF"
            state "on", label: 'ON', action: "refresh", icon: "st.Seasonal Winter.seasonal-winter-009", backgroundColor: "#FFFF00"
            state "heating", label: 'FLAME', action: "refresh", icon: "st.Seasonal Winter.seasonal-winter-013", backgroundColor: "#FF0000"
        }
        standardTile("refresh", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		// TODO: define your main and details tiles here
        main(["waterSoftener"])

        details(["waterSoftener", "temperature", "led", "pulseTime","flameSensorState","flameSensor", "refresh"])
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
        uri: "http://data.sparkfun.com",
    	path: "/output/${PUBLICKEY}.json",
        contentType: 'application/json',
        query: ["page":"1"]
        //query: ["gt[timestamp]":"now-5min"]

		]
	//def paramString = "https://data.sparkfun.com/output/4JpQpaMq3ycvnNnKQZq9.json?"
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
            if (resp.data.size <1) {
            	log.error("There is no data in the queried time frame")
                sendEvent([name: "switch", value: "off"])
                return              
            } else if( state.timeStamp == resp.data.timestamp[0]) {
            	//This occurs when the timestamp is the same across multiple data retrievals
                if(now() - state.lastSuccessfulTimestamp > (SAMPLESTOAVERAGE*60)){
            		state.lastSuccessfulTimestamp = now()
                    log.error("Timestamp isn't changing")
                	sendEvent([name: "switch", value: "off"])
                    return
                }
            	
            }
            
            int ledSum = 0
            int pulseSum = 0
            int temperatureSum = 0
            int flameSum = 0
            int numOfSamples = resp.data.led.size()
            if(numOfSamples > SAMPLESTOAVERAGE*2) {
            	numOfSamples = SAMPLESTOAVERAGE*2
            }
			
            for (i in 0..numOfSamples-1) {
            	
                if((ignoreZero == 1) && (resp.data.flame[i].toFloat() == 0)) {
                	//log.debug "SKIPPING: ${i} led: ${resp.data.led[i].toFloat()} pulset: ${resp.data.pulset[i].toFloat()} temp: ${resp.data.dalt[i].toFloat()} flame: ${resp.data.flame[i].toFloat()}"
                    numOfSamples --
                    
                } else {
                	//log.debug "${i} led: ${resp.data.led[i].toFloat()} pulset: ${resp.data.pulset[i].toFloat()} temp: ${resp.data.dalt[i].toFloat()} flame: ${resp.data.flame[i].toFloat()}"
                	ledSum += resp.data.led[i].toFloat()
                	pulseSum += resp.data.pulset[i].toFloat()
                	temperatureSum += resp.data.dalt[i].toFloat()
                	flameSum += resp.data.flame[i].toFloat()
                }    
    			
			}
            //log.debug "led: ${ledSum} pulseTime: ${pulseSum} temp: ${temperatureSum} flameSum: ${flameSum} samples to Avg: ${SAMPLESTOAVERAGE} numOfSamples: ${numOfSamples}"
            /*if(numOfSamples > SAMPLESTOAVERAGE) {
            	numOfSamples = SAMPLESTOAVERAGE
            }*/
            state.led = (ledSum/numOfSamples)
            state.pulset = (pulseSum/numOfSamples)
            state.temperature = (temperatureSum/numOfSamples)
            state.flameSensor = (flameSum/numOfSamples)
            state.led = state.led.toFloat().round(0)
            state.pulset = state.pulset.toFloat().round(0)
            state.temperature = state.temperature.toFloat().round(0)
            state.flameSensor = state.flameSensor.toFloat().round(0)
            state.timeStamp = resp.data.timestamp[0]
            state.lastSuccessfulTimestamp = now()
            
            
            TRACE ("LED avg: ${state.led} pulset: ${state.pulset} temp: ${state.temperature} flame: ${state.flameSensor}")
            state.successfulMessage = now()
			sendEvent([name: "temperature", value: state.temperature, unit: "F"])
            sendEvent([name: "led", value: state.led])
            sendEvent([name: "pulseTime", value: state.pulset])
            sendEvent([name: "flameSensor", value: state.flameSensor])
            //TRACE("pulset: ${state.pulset.toFloat()} led: ${state.led.toFloat()}")
            if (( state.pulset.toFloat() > 400) && (state.pulset.toFloat() < 600))  {
            //This occurs when the LED is signalling LOW SALT
            	sendEvent([name: "switch", value: "on"])

            } else if (( state.pulset.toFloat() < 100) && (state.led.toFloat() < 200))  {
            	sendEvent([name: "switch", value: "error"])
                //This occurs when the LED is not blinking, but the sensor can't detect its on. Therfore in error
            	
            } else {
            	sendEvent([name: "switch", value: "off"])
                //This is the normal state, the LED is solid ON and the sensor can see it.
               
            }
            if ( device.currentValue("flameSensorState") == "heating") {
            	def pilotValue = pilotOutThresholdDelay ?: pilotOutThreshold
            	if (state.flameSensor.toFloat() < pilotValue ) {
                	//The state is heating, the sensor is reporting heating is over.
                	if( pilotOutThresholdDelay == null) {
                    	//The special pilot delay value is not defined
                    	if(now() - state.lastHeatingTime >= minuteDelayHeating*60000) {
                        	//The flame has been gone long enough to allow an OFF event
                            sendEvent([name: "flameSensorState", value: "off"])
                        }
                    
                    }
                    else {
                    	sendEvent([name: "flameSensorState", value: "off"])
                    }
                } else if ( state.flameSensor.toFloat() < pilotOnThreshold ) {
                	//The Water heater just quit heating and now is back to ON.
                    //The PHOTO sensors often read 5-10% lower after the HEATING cycle
                    //Tell the smartapp that heating just ended.
                    state.lastHeatingTime = now()
                    sendEvent([name: "flameSensorState", value: "on" ] )
                } else {
                    sendEvent([name: "flameSensorState", value: "heating"])
                }
            }    
            else {
                if (state.flameSensor.toFloat() < pilotOutThreshold ) {
                	if (now() - state.lastHeatingTime >= minuteDelayHeating*60000) {
                    	sendEvent([name: "flameSensorState", value: "off"])
                    }
                } else if ( state.flameSensor.toFloat() < pilotOnThreshold ) {
                    sendEvent([name: "flameSensorState", value: "on" ] )
                } else {
                    sendEvent([name: "flameSensorState", value: "heating"])
                    state.lastHeatingTime = now()
                }
             }
            
			
		}      
    } catch (e) {
    		log.error "something went wrong: $e"
            state.delta=(now()-state.successfulMessage)/60000
            
            if(now()-state.successfulMessage>MinutesOfError*60000) { 
            	sendEvent([name:"switch",value:"error"])
            }
		}
    return    
}

private def TRACE(message) {
    log.debug message
}
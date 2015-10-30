/**
 *  Copyright 2015 SmartThings
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
 *  It's Too Hot
 *
 *  Author: SmartThings
 */
definition(
    name: "Water Softener SmartApp",
    namespace: "okpowerman",
    author: "okpowerman",
    description: "Retrieve data from sparkfun and apply to water Softener",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/its-too-hot.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/its-too-hot@2x.png"
)

preferences {
	section("Monitor the temperature...") {
		input "temperatureSensor1", "capability.temperatureMeasurement"
	}
	section("Which switch should I update on?") {
		input "switch1", "capability.switch"
	}
    section("Sparkfun data details") {
    input "PUBLICKEY", "string", title:"Data Sparkfun PUBLIC KEY", defaultValue: "4JpQpaMq3ycvnNnKQZq9",required:true, displayDuringSetup: true 
    input "PRIVATEKEY", "string", title:"Data Sparkfun PRIVATE KEY", defaultValue:"...", required:true, displayDuringSetup:true 
    input "MinutesToAverage", "number", title: "Minutes of Phant Data", defaultValue: 5, required: true, displayDuringSetup:true 
    }
    
}

def installed() {
	subscribe(temperatureSensor1, "temperature", temperatureHandler)
    subscribe(switch1, "switch.on", refresh)
    log.trace "Installed"
}

def updated() {
	unsubscribe()
	subscribe(temperatureSensor1, "temperature", temperatureHandler)
    subscribe(switch1, "switch.on", refresh)
    log.trace "Updated"
}

def temperatureHandler(evt) {
	log.trace "temperature: $evt.value, $evt"


}

def refresh(evt) {
	log.trace "Update Switch Triggered: ${evt}"
		def params = [
    	uri: "https://data.sparkfun.com",
    	path: "/output/${PUBLICKEY}.json",
        contentType: 'application/json',
        query: ['gt[timestamp]': "now-${MinutesToAverage}min"],
        headers: [host: "data.sparkfun.com", accept: "*/*" ]
         
		]

	try {
    	TRACE(params)
    	httpGet(params) { resp ->
        	//resp.headers.each {
        		//log.debug "${it.name} : ${it.value}"
            //}
            
            TRACE("data: ${resp.data}")
            
            int ledSum = 0
            int pulseSum = 0
            int temperatureSum = 0
            int numOfSamples = resp.data.led.size()
			
            for (i in 0..numOfSamples-1) {
            	//log.debug "${i} led: ${resp.data.led[i].toFloat()} pulset: ${resp.data.pulset[i].toFloat()} temp: ${resp.data.dalt[i].toFloat()}"
                ledSum += resp.data.led[i].toFloat()
                pulseSum += resp.data.pulset[i].toFloat()
                temperatureSum += resp.data.dalt[i].toFloat()
    			
			}
            state.led = (ledSum/numOfSamples)
            state.pulset = (pulseSum/numOfSamples)
            state.temperature = (temperatureSum/numOfSamples)
            state.led = state.led.toFloat().round(0)
            state.pulset = state.pulset.toFloat().round(0)
            state.temperature = state.temperature.toFloat().round(0)
            
            //log.debug "led: ${ledSum} pulseTime: ${pulseSum} temp: ${temperatureSum}"
            TRACE ("LED avg: ${state.led} pulset: ${state.pulset} temp: ${state.temperature}")
            
			//sendEvent([name: "temperature", value: state.temperature, unit: "F"])
            //sendEvent([name: "led", value: state.led])
            //sendEvent([name: "pulseTime", value: state.pulset])
            TRACE("pulset: ${state.pulset.toFloat()} led: ${state.led.toFloat()}")
            if (( state.pulset.toFloat() > 400) && (state.pulset.toFloat() < 600))  {
            //This occurs when the LED is signalling LOW SALT
            	//sendEvent([name: "switch", value: "on"])

            } else if (( state.pulset.toFloat() < 100) && (state.led.toFloat() < 200))  {
            	//sendEvent([name: "switch", value: "error"])
                //This occurs when the LED is not blinking, but the sensor can't detect its on. Therfore in error
            	
            }else {
            	sendEvent([name: "switch", value: "off"])
                //This is the normal state, the LED is solid ON and the sensor can see it.
               
            }
            
			
		}       
    } catch (e) {
    		log.error "something went wrong: $e"
		}
        
}

private def TRACE(message) {
    log.debug message
}
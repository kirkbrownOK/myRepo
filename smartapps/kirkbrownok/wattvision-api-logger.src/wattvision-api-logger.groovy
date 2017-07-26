/**
 *  Wattvision API Logger
 *
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
definition(
    name: "Wattvision API Logger",
    namespace: "kirkbrownOK",
    author: "Kirk Brown",
    description: "Take a Zwave or other power meter and send the data to your Wattvision Account",
    category: "Green Living",
    iconUrl: "https://www.wattvision.com/zimg/wvlogo10-small.png",
    iconX2Url: "https://www.wattvision.com/zimg/wvlogo10-small.png",
    iconX3Url: "https://www.wattvision.com/zimg/wvlogo10-small.png")


preferences {
    section ("Wattvision API Settings") {
		input "api_id", "string", title: "Wattvision API ID", required:true
        input "api_key", "string", title: "Wattvision API Key", required:true    
        input "sensor_id", "string", title: "Sensor ID:", required:true
	}
    section("House Power Meter") {
		input "housePower", "capability.powerMeter", title: "House Power Meters", required:true
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(housePower,"power",powerEvent)
	// TODO: subscribe to attributes, devices, locations, etc.
    state.apiKey = "1ykdl5x9rbuzhspdz3r3zi8jonu3xbd9"
    state.apiId = "a0qaugm2iu333isjzsg2i2ghsjqupntn"
    state.sensorId = "59919802"
    
}
public wattvisionDateFormat() { "yyyy-MM-dd'T'HH:mm:ss" }

def powerEvent(evt) {
	def timeReceived = evt.date.format(wattvisionDateFormat())
    def watthours = 1000*housePower.currentEnergy
	TRACE("AT ${timeReceived} Recevied ${housePower.name} at ${evt.value} ${evt.unit} kWh ${housePower.currentEnergy} Wh ${watthours}")
    
    def postBody = "{\"sensor_id\":\"${state.sensorId}\",\"api_id\":\"${state.apiId}\",\"api_key\":\"${state.apiKey}\",\"time\":\"${timeReceived}\",\"watts\":${evt.value},\"watthours\":\"${watthours}\"}"
	//TRACE("Body: $postBody")
    	def params = [
        	uri: "https://www.wattvision.com/api/v0.2/elec",
            //uri: "http://requestb.in/14c6xrr1",
            //path: "/api/v0.2/elec",
            //contentType:"application/json",
            headers: ["Content-Type":"application/x-www-form-urlencoded"],
            //body: ["sensor_id":state.sensorId,"api_id":state.apiId,"api_key":state.apiKey,"time":timeReceived,"watts":evt.value]
            body: postBody
            ]
        //TRACE("Posting ${params}")   
        httpPost(params) { resp ->
        
//            resp.headers.each {
  //              TRACE("${it.name} : ${it.value}")
    //        }
            TRACE( "response data: ${resp.data}")
            
        }

}
def TRACE(msg) {
	log.debug msg
}
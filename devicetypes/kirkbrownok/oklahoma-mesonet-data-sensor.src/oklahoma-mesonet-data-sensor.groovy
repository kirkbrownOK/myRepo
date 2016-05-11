/**
 *  Oklahoma Mesonet Data Sensor
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
metadata {
	definition (name: "Oklahoma Mesonet Data Sensor", namespace: "kirkbrownOK", author: "Kirk Brown") {
		capability "Illuminance Measurement"
		capability "Polling"
		capability "Refresh"
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"

		attribute "Solar_Irradiance", "number"
        attribute "temperatureMax", "number"
        attribute "temperatureMin", "number"
        attribute "humidity", "number"
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
        valueTile("temperatureMax", "device.temperatureMax", width: 1, height: 1) {
        	state "temperatureMax", label:'Max:${currentValue}°', action: "refresh",
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
        valueTile("temperatureMin", "device.temperatureMin", width: 1, height: 1) {
        	state "temperatureMin", label:'Min:${currentValue}°', action: "refresh",
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
		standardTile("refresh", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}


        main(["temperature"])

        details(["temperature", "temperatureMax", "temperatureMin", "humidity", "refresh"])        
	}
	preferences {
		section("Where is the sensor station? ") {
			input "sensorLocation", "string", title: "Where is it?"
		}
	}

	simulator {
		// TODO: define status and reply messages here
	}

}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'illuminance' attribute
	// TODO: handle 'humidity' attribute
	// TODO: handle 'temperature' attribute
	// TODO: handle 'Solar_Irradiance' attribute

}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
	// TODO: poll command will make an HTTP:GET request to https://www.mesonet.org/data/public/mesonet/current/current
    // the response is a lot of CSV data.
    def pollParams = [
        uri: "http://www.mesonet.org",
        path: "/data/public/mesonet/current/current.csv.txt",
        headers: [ contentType: 'application/json']        
        ]
//    def events = []
//    def ev = []    
        
    try {
        httpGet(pollParams) {resp ->
            //resp.headers.each {
            //	log.debug "${it.name} : ${it.value}"
        	//}
//STID,NAME,ST,LAT,LON,YR,MO,DA,HR,MI,TAIR,TDEW,RELH,CHIL,HEAT,WDIR,WSPD,WMAX,PRES,TMAX,TMIN,RAIN
//0	  ,1   ,2 ,3  ,4  ,5 ,6 ,7 ,8 ,9 ,10  , 11 , 12 , 13 , 14 , 15 , 16 , 17 , 18 , 19 , 20 , 21  
           resp.data.each {
            	//TRACE("${it.value}")
                def tempString = new String(it.value)
                //TRACE("STR: ${tempString}")
                if(tempString.contains("${sensorLocation}")) {
                	
                	log.info "I FOUND ${sensorLocation} ${it.value}"
                    
                    def pair = tempString.split(",")
                    //log.debug pair[18].trim()
                    log.info "Temp: ${pair[10].trim()} Tmax: ${pair[19].trim()} Tmin: ${pair[20].trim()} Humidity: ${pair[12].trim()}"
                    sendEvent([name: "temperature", value: "${pair[10].trim()}"])
                    sendEvent([name: "humidity", value: "${pair[12].trim()}"])
                    sendEvent([name: "temperatureMax", value: "${pair[19].trim()}"])
                    sendEvent([name: "temperatureMin", value: "${pair[20].trim()}"])
                                                                   
                    /*
                    ev = [ name: "temperature", value: "${pair[10].trim()}", descriptionText: "${sensorLocation}" ]
                    events << createEvent(ev)
                    ev = [ name: "temperatureMax", value: "${pair[19].trim()}", descriptionText: "${sensorLocation}" ]
                    events << createEvent(ev)
                    [ name: "temperatureMin", value: "${pair[20].trim()}", descriptionText: "${sensorLocation}" ]
                    events << createEvent(ev)
                    */
                    
                }                
            }
       }
    } catch (e) {
        log.error "something went wrong: $e"
    }  
     return
}

def refresh() {
	log.debug "Executing 'refresh'"
    poll()
}

def TRACE(message) {
	log.trace message
}

/**
 *  Oklahoma Mesonet 5 Minute Data Sensor
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
	definition (name: "Oklahoma Mesonet 5 Minute Data Sensor", namespace: "kirkbrownOK", author: "Kirk Brown") {
		capability "Illuminance Measurement"
		capability "Polling"
		capability "Refresh"
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"

		attribute "illuminance", "number"
        attribute "windSpeed", "number"
        attribute "windMax", "number"
        attribute "rain", "number"
        attribute "temperature", "number"
        attribute "pressure", "number"
        attribute "humidity", "number"
        attribute "site", "string"
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
        valueTile("illuminance", "device.illuminance", width: 1, height: 1) {
        	state "default", label:'${currentValue} Watts/m^2', action: "refresh", canChangeIcon: true, icon: "st.Weather.weather2",
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
        valueTile("windSpeed", "device.windSpeed", width: 1, height: 1) {
        	state "default", label:'${currentValue} mph', action: "refresh",
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
        valueTile("rain", "device.rain", width: 1, height: 1) {
        	state "default", label:'${currentValue} in', action: "refresh",
            	inactiveLabel:false,
                backgroundColors:[
                    [value: 0, color: "#153591"],
                    [value: 0.1, color: "#1e9cbb"],
                    [value: 0.2, color: "#90d2a7"],
                    [value: 0.4, color: "#44b621"],
                    [value: 0.6, color: "#f1d801"],
                    [value: 0.8, color: "#d04e00"],
                    [value: 1.0, color: "#bc2323"]
                ]
        }
        valueTile("windMax", "device.windMax", width: 1, height: 1) {
        	state "default", label:'${currentValue} mph', action: "refresh",
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
        standardTile("site", "device.site", inactiveLabel: false, decoration: "flat") {
			state "default", label:'${currentValue}', action:"refresh.refresh"
		}


        main(["temperature"])

        details(["site","temperature", "windSpeed", "windMax", "rain", "humidity","illuminance", "refresh"])        
	}
	preferences {
		section("Where is the sensor station? ") {
			input ("sensorLocation", "enum", title: "Where is it?", options: ["ACME","ADAX","ALTU","ALV2","ANT2","APAC","ARD2","ARNE","BEAV","BESS","BIXB","BLAC","BOIS","BOWL","BREC","BRIS","BROK","BUFF","BURB","BURN","BUTL","BYAR","CAMA","CARL","CENT","CHAN","CHER","CHEY","CHIC","CLAY","CLOU","COOK","COPA","DURA","ELKC","ELRE","ERIC","EUFA","FAIR","FITT","FORA","FREE","FTCB","GOOD","GRA2","GUTH","HASK","HECT","HINT","HOBA","HOLD","HOLL","HOOK","HUGO","IDAB","INOL","JAYX","KENT","KETC","KIN2","LAHO","LANE","MADI","MANG","MARE","MAYR","MCAL","MEDF","MEDI","MIAM","MINC","MRSH","MTHE","NEWK","NEWP","NINN","NOWA","NRMN","OILT","OKCE","OKCN","OKEM","OKMU","PAUL","PAWN","PERK","PORT","PRYO","PUTN","REDR","RING","SALL","SEIL","SHAW","SKIA","SLAP","SPEN","STIG","STIL","STUA","SULP","TAHL","TALA","TALI","TIPT","TISH","TULN","VALL","VINI","WAL2","WASH","WATO","WAUR","WEAT","WEBR","WEST","WILB","WIST","WOOD","WYNO"], 
            	required: true, displayDuringSetup: true, defaultValue: "MINC" )
		}
	}

	simulator {
		// TODO: define status and reply messages here
	}

}

// parse events into attributes
def parse(String description) {
	//log.debug "Parsing '${description}'"
    def events = []
    def ev = []
    def pair = description.split("\n")
    pair.each {
    	def tempString = new String(it.value)
        //TRACE("STR: ${tempString}")
        if(tempString.contains("${sensorLocation}")) {

            log.info "I FOUND ${sensorLocation} ${it.value}"

            state.stringValue = ["MINC",66,1320,43,18.1,6.7,6.6,156,8.7,1.1,8.4,0.00,965.91,261,17.5,5.4]
            def subPair = tempString.split("   ")
            //log.debug subPair
            state.counter1 = 0
            state.timeToMove = false
            state.useStateValues = false
            subPair.each {
                if(state.timeToMove) {

                } else if(subPair[state.counter1].trim().contains(" ")) {
                	state.useStateValues = true
                    log.info "I found a bad space: ${state.counter1}"
                    TRACE( "for i = ${subPair.size()}; i > ${state.counter1}; i--")
                    def i = 0
                    for (i = subPair.size(); i > state.counter1; i--) {
                        TRACE( "state.stringValue[${i}] = subPair[${i-1}]")
                        state.stringValue[i] = subPair[i-1]
                    }
                    for (i = 0; i <= state.counter1; i ++) {
                    	TRACE( "state.stringValue[${i}] = subPair[${i}]")
                        state.stringValue[i] = subPair[i]
                    }
                    def tempString2 = subPair[state.counter1].split(" ")
                    TRACE( "TS ${tempString2}")
                    state.stringValue[state.counter1] = tempString2[1].trim()
                    state.stringValue[state.counter1+1] = tempString2[3].trim()
                    TRACE( "Sub : ${subPair}")
                    TRACE( "Stat: ${state.stringValue}")
                    subPair = state.stringValue
                    TRACE( "NSUB: ${subPair}")
                    
                } else {
                	
                    TRACE( "${state.counter1} ${subPair[state.counter1].trim()} all is well")
                }



                //log.debug "subPair[${state.counter1}] ${subPair[state.counter1]}"
                state.counter1 = state.counter1 + 1
            }
                
            
            log.info "${subPair}"
//Mesonet 5minute.obs.current.mdf
//STID STNM TIME RELH TAIR WSPD WVEC WDIR WDSD WSSD WMAX RAIN PRES SRAD TA9M WS2M
//0		,1	2		3	4	5	6		7	8	9	10		11	12	13	14	15
            //log.info "Temp: ${subPair[4].trim()} WSPD: ${subPair[5].trim()} rain: ${subPair[11].trim()} Humidity: ${subPair[3].trim()}"
            
            log.info "STID:${subPair[0].trim()} STNM:${subPair[1].trim()} TIME:${subPair[2].trim()} RELH:${subPair[3].trim()} TAIR:${subPair[4].trim()} WSPD:${subPair[5].trim()}"
            log.info "WVEC:${subPair[6].trim()} WDIR:${subPair[7].trim()} WDSD:${subPair[8].trim()} WSSD:${subPair[9].trim()} WMAX:${subPair[10].trim()} RAIN:${subPair[11].trim()}"
            log.info "PRES:${subPair[12].trim()} SRAD:${subPair[13].trim()} TA9M:${subPair[14].trim()} WS2M:${subPair[15].trim()}"
            def temp = (subPair[4].trim().toFloat()*9.0/5.0+32).round(1)
            def humi = subPair[3].trim()
            def windM= (subPair[10].trim().toFloat()*3.28084*60*60/5280.0).round(1)
            def windS= (subPair[5].trim().toFloat()*3.28084*60*60/5280.0).round(1)
            def rain = (subPair[11].trim().toFloat()*0.03937007874).round(2)
            def sRads= subPair[13].trim()
            sendEvent([ name: "temperature", value: "${temp}", descriptionText: "${sensorLocation} Temp: ${temp}°" ])
            sendEvent([ name: "humidity", value: "${humi}", descriptionText: "${sensorLocation} Humidity: ${humi}%" ])
            sendEvent([ name: "windMax", value: "${windM}", descriptionText: "${sensorLocation} Max Wind: ${windM} mph" ])
            sendEvent([ name: "windSpeed", value: "${windS}", descriptionText: "${sensorLocation} Wind: ${windS} mph" ])
			sendEvent([ name: "rain", value: "${rain}", descriptionText: "${sensorLocation} Rain Today: ${rain} inches" ])
            sendEvent([ name: "illuminance", value: "${sRads}", descriptionText: "${sensorLocation} Sun: ${sRads}Watts/m^2" ])
            /*
            ev = [ name: "temperature", value: "${pair[4].trim()}", descriptionText: "${sensorLocation}" ]
            events << createEvent(ev)
            ev = [ name: "humidity", value: "${pair[3].trim()}", descriptionText: "${sensorLocation}" ]
            events << createEvent(ev)
            ev = [ name: "windMax", value: "${pair[10].trim()}", descriptionText: "${sensorLocation}" ]
            events << createEvent(ev)
            ev = [ name: "windSpeed", value: "${pair[5].trim()}", descriptionText: "${sensorLocation}" ]
            events << createEvent(ev)
            ev = [ name: "rain", value: "${pair[11].trim()}", descriptionText: "${sensorLocation}" ]
            events << createEvent(ev)
			*/

        }     
    }

}

// handle commands
def poll() {
	log.debug "Executing poll"
	// TODO: poll command will make an HTTP:GET request to https://www.mesonet.org/data/public/mesonet/current/current
    // the response is a lot of CSV data.
    def pollParams = [
        uri: "http://www.mesonet.org",
        path: "/data/public/mesonet/current/5minute.obs.current.mdf",
        headers: [ contentType: 'application/json']        
        ]    
    TRACE("TRY GET")    
    try {
        httpGet(pollParams) {resp ->
            resp.headers.each {
            	TRACE( "${it.name} : ${it.value}")
        	}
            def str = ""
//Mesonet current.csv.txt format
//STID,NAME,ST,LAT,LON,YR,MO,DA,HR,MI,TAIR,TDEW,RELH,CHIL,HEAT,WDIR,WSPD,WMAX,PRES,TMAX,TMIN,RAIN
//0	  ,1   ,2 ,3  ,4  ,5 ,6 ,7 ,8 ,9 ,10  , 11 , 12 , 13 , 14 , 15 , 16 , 17 , 18 , 19 , 20 , 21 
//Mesonet 5minute.obs.current.mdf
//STID STNM TIME RELH TAIR WSPD WVEC WDIR WDSD WSSD WMAX RAIN PRES SRAD TA9M WS2M
//0		,1	2		3	4	5	6		7	8	9	10		11	12	13	14	15
           resp.data.each {
           		//if(state.counter < 100 ) {
            		//TRACE("${state.counter}: ${it.value}")
                    
                    
                
                    def tempString = new String(it.value)
                   
                    str=str+tempString
                }
                
                
            //}
            //TRACE("state str: ${state.str}")
           parse(str)
            
       }
    } catch (e) {
        log.error "something went wrong: $e"
    }  
     return
}
def updated() {
	log.info "Update site to ${sensorLocation}"
	sendEvent([name: "site", value: "${sensorLocation}", descriptionText: "${sensorLocation}"])
    poll()
}

def refresh() {
	log.debug "Executing refresh"
    poll()
	// TODO: handle 'refresh' command
}

def TRACE(message) {
	//log.trace message
}

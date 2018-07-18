/**
 *  Copyright 2017 Kirk Brown
 * 	This code was based on https://github.com/edent/BMW-i-Remote
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
 *	BMW Connected Drive
 *
 *	Author: Kirk Brown
 *	Date: 2017-9-1
 * There are a large number of debug statements that will turn on if you uncomment the statement inside the TRACE function at the bottom of the code
 *
 *
 */
metadata {
	definition (name: "BMW Connected Car", namespace: "kirkbrownOK/BMW_Connected", author: "Kirk Brown") {
		
		//capability "Thermostat"
		capability "Sensor"
		capability "Refresh"
		//capability "Relative Humidity Measurement"
		//capability "Health Check"
        capability "Battery"
        capability "Switch"
        //capability "Lock"

        command "refresh"
        command "poll"
        command "preConditionOn"
        command "lockDoors"
        command "unlockDoors"
        command "getCars"
        command "getCmd"

        attribute "mileage", "number"
        attribute "updateReason", "string"
        attribute "updateTime", "string"
        attribute "doors", "string"
        attribute "doorDriverFront", "string"
        attribute "doorDriverRear", "string"
        attribute "doorPassengerFront", "string"
        attribute "doorPassengerRear", "string"
        attribute "windowDriverFront", "string"
        attribute "windowDriverRear", "string"
        attribute "windowPassengerFront", "string"
        attribute "windowPassengerRear", "string"
        attribute "trunk", "string"
        attribute "rearWindow", "string"
        attribute "convertibleRoofState", "string"
        attribute "hood", "string"
        attribute "doorLockState", "string"
        attribute "parkingLight", "string"
        attribute "positionLight", "string"
        attribute "remainingFuel", "number"
        attribute "remainingRangeElectric", "number"
        attribute "remainingRangeElectricMls", "number"
        attribute "remainingRangeFuel", "number"
        attribute "remainingRangeFuelMls", "number"
        attribute "maxRangeElectric", "number"
        attribute "maxRangeElectricMls", "number"
        attribute "fuelPercent", "number"
        attribute "maxFuel", "number"
        attribute "connectionStatus", "string"
        attribute "chargingStatus", "string"
        attribute "chargingLevelHv", "number"
        attribute "lastChargingEndReason", "string"
        attribute "lastChargingEndResult", "string"
        attribute "lat", "number"
        attribute "lon", "number"
        attribute "heading", "number"
        attribute "status", "string"
        attribute "debug", "string"
        attribute "chargingTimeRemaining", "number"
        attribute "cmdStatus", "string"

	}

tiles(scale:2) {
        valueTile("mileage", "device.mileage", decoration: "flat") {
            state "mileage", label:'${currentValue}', unit:"km",
                backgroundColors:[

                // Fahrenheit
                [value: 4000, color: "#153591"],
                [value: 7500, color: "#1e9cbb"],
                [value: 15000, color: "#90d2a7"],
                [value: 25000, color: "#44b621"],
                [value: 35000, color: "#f1d801"],
                [value: 50000, color: "#d04e00"],
                [value: 75000, color: "#bc2323"]
            ]
        }

        
        
        standardTile("doorDriverFront", "device.doorDriverFront", inactiveLabel:false, decoration:"flat",width:3,height:2) {
            state "default", label:'Door Status', icon:"st.custom.buttons.add-icon", action:"refresh"
            state "CLOSED", label: 'Doors:\nDriver: ${currentValue}',  action: "refresh"
            state "OPEN", label: 'Doors:\nDriver: ${currentValue}', action: "refresh"
        }
        standardTile("doorPassengerFront", "device.doorPassengerFront", inactiveLabel:false, decoration:"flat",width:3,height:1) {
            state "default", label:'Door Status', icon:"st.custom.buttons.add-icon", action:"refresh"
            state "CLOSED", label: 'Passenger: ${currentValue}', action: "refresh"
            state "OPEN", label: 'Passenger: ${currentValue}',  action: "refresh"
        }
        standardTile("doorPassengerRear", "device.doorPassengerRear", inactiveLabel:false, decoration:"flat",width:3,height:1) {
            state "default", label:'Passenger Rear  ${currentValue', icon:"st.custom.buttons.add-icon", action:"refresh"
            state "CLOSED", label: 'Passenger Rear: ${currentValue}', action: "refresh"
            state "OPEN", label: 'Passenger Rear: ${currentValue}', action: "refresh"
        }
        standardTile("doorDriverRear", "device.doorDriverRear", inactiveLabel:false, decoration:"flat",width:3,height:1) {
            state "default", label:'Door Status', icon:"st.custom.buttons.add-icon", action:"refresh"
            state "CLOSED", label: 'Driver Rear: ${currentValue}', action: "refresh"
            state "OPEN", label: 'Driver Rear: ${currentValue}', action: "refresh"
        }
        standardTile("doorLockState", "device.doorLockState", inactiveLabel:false, decoration:"flat",width:3,height:1) {
            state "default", label:'Locks: ${currentValue}',  action:"refresh"
            state "SELECTIVE_LOCKED", label: 'Locks: Unlocked', action:"refresh"
            state "UNLOCKED", label: 'Locks: ${currentValue}', action: "refresh"
            state "LOCKED", label: 'Locks: ${currentValue}', action: "refresh"
        }
        standardTile("windowStatus", "device.doorDriverRear", inactiveLabel:false, decoration:"flat",width:3,height:1) {
            state "default", label:'Windows: ${currentValue}', action:"refresh"
            state "CLOSED", label: 'Windows: ${currentValue}', action: "refresh"
            state "OPEN", label: 'Windows: ${currentValue}', action: "refresh"
        }
        standardTile("lockMyDoors", "device.doors", inactiveLabel:false, decoration:"flat", width: 3) {
            state "default", label:'Lock Doors', action: "lockDoors"
        }
        standardTile("unlockMyDoors", "device.doors", inactiveLabel:false, decoration:"flat", width: 3) {
            state "default", label:'UnLock Doors', action: "unlockDoors"
        }
        standardTile("climateControl", "device.preconditionOn", inactiveLabel:false, decoration:"flat", width: 3) {
            state "default", label:'Climate Control', action: "preconditionOn"
        }
        standardTile("getCmd", "device.doors", inactiveLabel:false, decoration:"flat", width: 3) {
            state "default", label:'Get Cmd', action: "getCmd"
        }

        standardTile("cmdStatus", "device.cmdStatus", inactiveLabel:false,  decoration:"flat",width:3) {
            state "default", label:'CMD: ${currentValue}'
        }

        standardTile("refresh", "device.refresh", decoration:"flat",width:1, height:1) {
            state "default", icon:"st.secondary.refresh", action:"refresh.refresh"
            state "error", icon:"st.secondary.refresh", action:"refresh.refresh"
        }
        //No clue what a Fully Charged battery is vs a Dead Battery. Guessing from 2 AA nominal 1.5V per unit and 3.0V is adequate
		valueTile("batteryDisplay", "device.chargingLevelHv",  canChangeIcon: true,  width: 2, height: 1) {
            state "default", label:'${currentValue}', unit:"V", icon:"st.Transportation.transportation6"
                backgroundColors:[
                    [value: 25, color: "#ff3300"],
                    [value: 50, color: "#ffff00"],
                    [value: 75, color: "#33cc33"],
                    [value: 90, color: "#33cc33"]
                ]
        }
        main(["batteryDisplay" ])

        details(["doorDriverFront","batteryDisplay", "refresh",
        							"climateControl",
                "doorPassengerFront","lockMyDoors",
                "doorDriverRear","unlockMyDoors",
                "doorPassengerRear","getCmd",
                "doorLockState","windowStatus",
                "cmdStatus"  
           ])
    }
    

}

void installed() {
    // The device refreshes every 5 minutes by default so if we miss 2 refreshes we can consider it offline
    // Using 12 minutes because in testing, device health team found that there could be "jitter"
    //sendEvent(name: "checkInterval", value: 60 * 12, data: [protocol: "cloud"], displayed: false)
    
    //send initial default events to populate the tiles.
    updated()
    
}

void updated() {
	sendEvent(name:"doors",value:"allClosed")
}
def ping() {
    def isAlive = device.currentValue("deviceAlive") == "true" ? true : false
    if (isAlive) {
        refresh()
    }
}
def on() {
	TRACE("On turn on Conditioning")
    preConditionOn();
}
def off() {
	TRACE("Off send unlock")
    unlockDoors();
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def refresh() {

	poll()
	log.debug "refresh completed"
}

void poll() {	
	TRACE("Refresh $device.name: $device.deviceNetworkId")
	parent.pollChild(device.deviceNetworkId)
}

def generateEvent(results) {
	if(results) {
    	TRACE("results:\n$results")
        sendEvent([name: "degug", value: "${results}"])
        try{
            results.each { name, value ->
				checkSendEvent(name,value)
          } 
        }catch(e) {
          	log.info "No new data"
        }
    }
    return null
}


def checkSendEvent(evtName,evtValue,evtDescription=null,evtUnit=null,evtDisplayed=true) {
	//TRACE("Updating: name: ${evtName}, value: ${evtValue}, descriptionText: ${evtDescription}, unit: ${evtUnit}")
    try {
	def checkVal = device.currentValue(evtName) == null ? " " : device.currentValue(evtName)
    def myMap = []
    if (checkVal != evtValue) {
    	if(evtDisplayed == true) {
    		log.info "Updating: name: ${evtName}, value: ${evtValue}, descriptionText: ${evtDescription}, unit: ${evtUnit}"
        }
    	if((evtDescription == null) && (evtUnit == null)) {
        	myMap = [name: evtName, value: evtValue, displayed: evtDisplayed]
        } else if (evtUnit == null) {
        	myMap = [name: evtName, value: evtValue, descriptionText: evtDescription, displayed: evtDisplayed]
        } else if (evtDescription == null) {
        	myMap = [name: evtName, value: evtValue, unit: evtUnit, displayed: evtDisplayed]
        } else {
        	myMap = [name: evtName, value: evtValue, descriptionText: evtDescription, unit: evtUnit, displayed: evtDisplayed]
        }
        if(evtName != "refresh") { sendEvent(name:"refresh",value:"normal",displayed:false) }
        //log.debug "Sending Check Event: ${myMap}"
    	sendEvent(myMap)
    } else {
    	//log.debug "${evtName}:${evtValue} is the same"
    }
    } catch (e) {
    	log.debug "checkSendEvent $evtName $evtValue $e"
    }
}

void heatUp() {
	log.debug "Heat Up"
	def mode = device.currentValue("thermostatMode")
	if (mode == "off" ) {
		heat()
	}
	def heatingSetpoint = device.currentValue("heatingSetpoint")
	
    def targetvalue = heatingSetpoint + 1

    sendEvent(name:"heatingSetpoint", "value":targetvalue, "unit":location.temperatureScale, displayed: false)

    runIn(5, setDataHeatingSetpoint,[data: [value: targetvalue], overwrite: true]) //when user click button this runIn will be overwrite
}

void setHeatingSetpoint(setpoint) {
	TRACE( "***heating setpoint $setpoint")
    def cmdString = "set"

	def heatingSetpoint = setpoint.toInteger()
    
	def coolingSetpoint = device.currentValue("coolingSetpoint")
	def deviceId = device.deviceNetworkId
	def maxHeatingSetpoint = device.currentValue("maxHeatingSetpoint")
	def minHeatingSetpoint = device.currentValue("minHeatingSetpoint")
    def thermostatMode = device.currentValue("thermostatMode")

	//enforce limits of heatingSetpoint
	if (heatingSetpoint > maxHeatingSetpoint) {
		heatingSetpoint = maxHeatingSetpoint
	} else if (heatingSetpoint < minHeatingSetpoint) {
		heatingSetpoint = minHeatingSetpoint
	}

	//enforce limits of heatingSetpoint vs coolingSetpoint
	if (heatingSetpoint >= coolingSetpoint) {
		coolingSetpoint = heatingSetpoint
	}

    
    
    if ( thermostatMode == "auto") {
    	cmdString = "SetAutoHeat" 
        //log.debug "Is AUTHO heat ${cmdString}"
    } else if( (thermostatMode == "heat") || (thermostatMode == "aux") ) { 
    	cmdString = "SetHeat" 
        //log.debug "Is Reg Heat ${cmdString}"
    }
     log.debug "Sending heatingSetpoint: ${heatingSetpoint} mode: ${thermostatMode} string: ${cmdString}"  
     sendEvent("name":"heatingSetpoint", "value":heatingSetpoint, "unit":location.temperatureScale)
    if (parent.setTempCmd(deviceId, cmdString, heatingSetpoint)) {       
        
        //"on" means the schedule will not run
        //"temporary" means do nothing special"
        //"off" means do nothing special
        def currentHoldMode = getDataByName("thermostatHoldMode")
        def desiredHoldType = holdType == null ? "temporary" : holdType
        //log.debug "holdType is: ${holdType} des Hold type is: ${desiredHoldType}"
        if( (desiredHoldType == "Permanent") && (currentHoldMode != "on")) {
            parent.setStringCmd(deviceId, "SetScheduleMode", "Off")
            sendEvent(name:"thermostatHoldMode", value: "on")
        } else {
            sendEvent(name:"thermostatHoldMode", value: "temporary")
        }
        //log.debug "Done setHeatingSetpoint: ${heatingSetpoint}"

	} else {
		log.error "Error setHeatingSetpoint(setpoint)"
	}
    
}
void preConditionOn() {
	TRACE("condition ON")
	def deviceId = device.deviceNetworkId
	parent.sendExecuteService(deviceId, "CLIMATE_NOW")
   updateProcess()
}
void getCars() {
	TRACE("Get cars")
    def resp = parent.getBMWCars()
    TRACE("GC: $resp")
}
void getCmd() {
	TRACE("Get command Status")
    def resp = parent.checkCommand()
	try{
       /* resp.data.executionStatus.each { name, value ->
        	TRACE("GC: $name : $value")
            checkSendEvent(name,value)
        } */
       checkSendEvent("cmdStatus",resp.data.executionStatus.status) 
       runIn(30,refresh)
    }catch(e) {
        log.info "No new data"
    }
    TRACE("Cmd Status: $resp.data")
}
void lockDoors() {
	TRACE("Lock my doors")
	def deviceId = device.deviceNetworkId
	parent.sendExecuteService(deviceId, "DOOR_LOCK")
    updateProcess()
    
}
void updateProcess() {
	checkSendEvent("cmdStatus", "INITIATED")
    runIn(30,getCmd)
}
void lock() {
	lockDoors()
    updateProcess()
}
void unlock() {
	unlockDoors()
    updateProcess()
}
void unlockDoors() {
	TRACE("Unlock Doors")
	def deviceId = device.deviceNetworkId
	parent.sendExecuteService(deviceId, "DOOR_UNLOCK", 1)
    updateProcess()
}



def switchToMode(nextMode) {
	//log.debug "In switchToMode = ${nextMode}"
	if (nextMode in modes()) {
    	nextMode = nextMode.toLowerCase()
		state.lastTriedMode = nextMode
		"$nextMode"()
	} else {
		log.debug("no mode method '$nextMode'")
	}
}

def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

def generateActivityFeedsEvent(notificationMessage) {
	sendEvent(name: "notificationMessage", value: "$device.displayName $notificationMessage", descriptionText: "$device.displayName $notificationMessage", displayed: true)
}

private def TRACE(message) {
    log.debug message
}
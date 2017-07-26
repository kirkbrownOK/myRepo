/**
 *  Keep Me Cozy Custom
 *
 *  Author: SmartThings
 */

definition(
    name: "Keep Me Cozy Custom",
    namespace: "smartthings",
    author: "SmartThings",
    description: "Works the same as Keep Me Cozy, but enables you to pick an alternative temperature sensor in a separate space from the thermostat. Focuses on making you comfortable where you are spending your time rather than where the thermostat is located.",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo@2x.png"
)

preferences() {
	section("Choose thermostat... ") {
		input "thermostat", "capability.thermostat"
	}
	section("Heat setting..." ) {
		input "heatingSetpoint", "decimal", title: "Degrees"
	}
	section("Air conditioning setting...") {
		input "coolingSetpoint", "decimal", title: "Degrees"
	}
	section("Optionally choose temperature sensor to use instead of the thermostat's... ") {
		input "sensor", "capability.temperatureMeasurement", title: "Temp Sensors", required: false, multiple: true
        }
    section("Optionally choose Virtual Thermost to use with Amazon Echo for voice controls") {
    	input "virtualThermostat", "capability.thermostat", title: "Virtual Thermostat", required: false
        input "realThermostat", "capability.thermostat", title: "Real Thermostat", required: false 
    }
    section("How long to delay between turning HVAC on?") {
		input "sensorDelay", "number", title: "Sensor Delay", required: false
        }
   section("What do you want the threshold to be?") {
		input "sensorThreshold", "decimal", title: "Sensor Threshold", required: true, default: 1.0
        }     
}

def installed()
{
	log.debug "enter installed, state: $state"
    initialize()
	
}
def initialize() {
    if ( sensorDelay > 5) {
       log.debug "sensor delay acceptable ${sensorDelay}"
       state.sensorDelay = sensorDelay
    } else {
    	state.sensorDelay = 10
        log.debug "Sensor Delay fixed"
    }
	
    state.heatingSetpoint = state.heatingSetpoint ? state.heatingSetpoint: heatingSetpoint
    state.coolingSetpoint = state.coolingSetpoint ? state.coolingSetpoint: coolingSetpoint
    state.threshold = sensorThreshold
	subscribeToEvents()
}

def updated()
{
	log.debug "enter updated, state: $state"
	unsubscribe()
    initialize()
    evaluate()
}

def subscribeToEvents()
{
	subscribe(location, changedLocationMode)
    subscribe(thermostat, "temperature", temperatureHandlerThermostat)
	subscribe(thermostat, "thermostatMode", temperatureHandler)
    subscribe(thermostat, "thermostatOperatingstate", temperatureHandler)
	if (sensor) {
    	sensor.each{
        	log.trace("${it.name}")
			subscribe(it, "temperature", temperatureHandler)
        }    
        if(virtualThermostat) {
        	virtualThermostat.setHeatingSetpoint(state.heatingSetpoint)
            virtualThermostat.setCoolingSetpoint(state.coolingSetpoint)
        	log.debug "Virtual Thermostat for voice control and Temp setting storage"
            subscribe(virtualThermostat, "heatingSetpoint", virtualHeatingSetpoint)
            subscribe(virtualThermostat, "coolingSetpoint", virtualCoolingSetpoint)
        }
        
	} else if(realThermostat) {
        	subscribe(realThermostat, "heatingSetpoint", temperatureHandlerThermostat)
            subscribe(realThermostat, "coolingSetpoint", temperatureHandlerThermostat)
            subscribe(realThermostat, "temperature", temperatureHandlerThermostat)
            subscribe(realThermostat, "thermostatFanMode", temperatureHandlerThermostat)
            runEvery5Minutes(fiveMinuteUpdate)
        }
	//evaluate()
}

def changedLocationMode(evt)
{
	log.debug "changedLocationMode mode: $evt.value, heat: $heat, cool: $cool"
    
    state.heatingSetpoint = heatingSetpoint
    state.coolingSetpoint = coolingSetpoint    
	evaluate()
}
public myDateFormat() { "yyyy-MM-dd'T'HH:mm:ss" }
def temperatureHandler(evt)
{	
	//updated()
	def timeReceived = evt.date.format(myDateFormat())
    if(state.lastIdle > 0 ) {
    	//DO nothing
    } else {
        state.lastIdle = 1444016997130
    }
	log.debug "${evt.name} ${evt.value} ${evt.date}"
	if ((evt.name == "thermostatOperatingState") && (evt.value == "idle")) {
    	state.lastIdle = now() 
        
        timeReceived = evt.date.format(myDateFormat())
        log.debug "thermostatOperatingState changed to IDLE at ${timeReceived}"
    }
    log.debug "LastIdle: ${state.lastIdle}"
    if((now() - state.lastIdle)/1000 > (60*state.sensorDelay) ) {
    	log.debug "Its been ${state.sensorDelay} minutes"
        if(virtualThermostat) {
        	getSetpoints()
        }
        evaluate()
    } else { 
    	log.debug "Its only been ${(now() - state.lastIdle)/1000/60} minutes"
        //state.sensorDelay = 5
    }
	
}
def fiveMinuteUpdate() {
	log.debug "5 minute update"
    evaluate()
}
def temperatureHandlerThermostat(evt)
{	
	log.debug "Thermostat temp update: $evt.name $evt.value $evt.descriptionText"
	evaluate()
   	
}
def evaluate()
{
	if (sensor) {
		def threshold = state.threshold
        log.debug "Threshold is: ${threshold}"
		def tm = thermostat.currentThermostatMode
		def ct = thermostat.currentTemperature
        def currentTemp = 0
        def sensorCounter = 0
        sensorCounter = 0
        currentTemp = 0
        
        sensor.each{
        	log.trace("${it.name} : ${it.currentTemperature} sensor state: ${it.currentSwitch}")
            if (it.currentSwitch == "on") {
            	currentTemp = currentTemp + it.currentTemperature
                log.trace("${it.name} is ${it.currentSwitch} making currentTemp sum: ${currentTemp}")
                sensorCounter = sensorCounter + 1
            } else {
            	log.warn("${it.name} is ${it.currentSwitch} making currentTemp sum: ${currentTemp} Not INCLUDED")
            }
            
        }
        if (sensorCounter > 0) {
        	currentTemp = currentTemp / sensorCounter
        } else {
        	currentTemp = ct
        }    
		log.trace("evaluate:, mode: $tm -- temp: $ct, heat: $thermostat.currentHeatingSetpoint, cool: $thermostat.currentCoolingSetpoint -- "  +
			"sensor: $currentTemp, heat: $state.heatingSetpoint, cool: $state.coolingSetpoint")
		if (tm in ["cool","auto"]) {
			// air conditioner
            //TRUE -> 72    -  69  >= 1.0 Evaluates true -> Send 65 - 3 = 62 Deg
            //False -> 69 - 69 >= 1.0 Evaluates False
			if (currentTemp - state.coolingSetpoint >= threshold) {
				thermostat.setCoolingSetpoint(ct - 3)
				log.debug "thermostat.setCoolingSetpoint(${ct - 3}), ON"
			}
            //true -> 
			//else if (state.coolingSetpoint - currentTemp >= threshold && ct - thermostat.currentCoolingSetpoint >= threshold) {
			else if (state.coolingSetpoint - currentTemp >= threshold ) {
				thermostat.setCoolingSetpoint(ct + 3)
				log.debug "thermostat.setCoolingSetpoint(${ct + 3}), OFF"
			}
		}
		if (tm in ["heat","emergency heat","auto"]) {
			// heater
			if (state.heatingSetpoint - currentTemp >= threshold) {
            	    if((now() - state.lastIdle)/1000 > (60*state.sensorDelay) ) {
                        thermostat.setHeatingSetpoint(ct + 3)
                    }
				
				log.debug "thermostat.setHeatingSetpoint(${ct + 3}), ON"
			}
			//else if (currentTemp - state.heatingSetpoint >= threshold && thermostat.currentHeatingSetpoint - ct >= threshold) {
			else if (currentTemp - state.heatingSetpoint >= threshold) {
            	thermostat.setHeatingSetpoint(ct - 3)
				log.debug "thermostat.setHeatingSetpoint(${ct - 3}), OFF"
			}
		}
	} else if (realThermostat) {
    	def tm = thermostat.currentThermostatMode
    	def rtm = realThermostat.currentThermostatMode
		def ct = thermostat.currentTemperature
        def rct = realThermostat.currentTemperature
        if (rtm != tm) {
        	log.debug "rtm is $rtm and tm is $tm setting tm to $rtm"
            thermostat."$rtm"()
        }
        if(thermostat.currentHold != "on") {
        	log.debug "Hold got turned on"
        	thermostat.holdOn()
        }
        log.debug "rt fan mode is $realThermostat.currentThermostatFanMode and therm fan mode is $thermostat.currentThermostatFanMode"
        if (realThermostat.currentThermostatFanMode == "fanAuto") {
        	if (thermostat.currentThermostatFanMode != "auto") {
            	thermostat.fanAuto()
                log.debug "Setting the Fan to Auto"
            }
        } else if (realThermostat.currentThermostatFanMode == "fanOn") {
        	if( thermostat.currentThermostatFanMode != "on") {
            	thermostat.fanOn()
            }
        }
        def currentTemp = rct
        log.trace("evaluate:, mode: $tm/$rtm -- temp: $ct/$rct, heat: $thermostat.currentHeatingSetpoint/$realThermostat.currentHeatingSetpoint, cool: $thermostat.currentCoolingSetpoint/$realThermostat.currentCoolingSetpoint -- "  +
			"sensor: $currentTemp, heat: $realThermostat.currentHeatingSetpoint, cool: $realThermostat.currentCoolingSetpoint")
		if (rtm == "cool") {
			// air conditioner
            //TRUE -> 72    -  69  >= 1.0 Evaluates true -> Send 65 - 3 = 62 Deg
            //False -> 69 - 69 >= 1.0 Evaluates False
			if (currentTemp - realThermostat.currentCoolingSetpoint > 0) {
				thermostat.setCoolingSetpoint(ct - 3)
				log.debug "thermostat.setCoolingSetpoint(${ct - 3}), ON"
			}
            //true -> 
			//else if (state.coolingSetpoint - currentTemp >= threshold && ct - thermostat.currentCoolingSetpoint >= threshold) {
			else if (realThermostat.currentCoolingSetpoint - currentTemp >= 0 ) {
				thermostat.setCoolingSetpoint(ct + 3)
				log.debug "thermostat.setCoolingSetpoint(${ct + 3}), OFF"
			}
		}
		if (rtm in ["heat","emergency heat"]) {
			// heater : 80 - 75 > 0 -> 5 > 0 true turn heater ON bc it is 5 below setpoint
			if (realThermostat.currentHeatingSetpoint - currentTemp > 0) {
            	    if((now() - state.lastIdle)/1000 > (60*state.sensorDelay) ) {
                        thermostat.setHeatingSetpoint(ct + 3)
                        log.debug "thermostat.setHeatingSetpoint(${ct + 3}), Turn Heater on"
                    }				
			}
			//else if (currentTemp - state.heatingSetpoint >= threshold && thermostat.currentHeatingSetpoint - ct >= threshold) {
            // Temp is 70 setpoint is 68 -> 2 is not > 0 so turn off 
			else if (currentTemp - realThermostat.currentHeatingSetpoint >= 0) {
            	thermostat.setHeatingSetpoint(ct - 3)
				log.debug "thermostat.setHeatingSetpoint(${ct - 3}), OFF"
			}
		}
    }
	else {
		thermostat.setHeatingSetpoint(61)
		thermostat.setCoolingSetpoint(69)
		thermostat.poll()
	}
    if(virtualThermostat) {
    	updateVirtualThermostat()
    }
}

def virtualHeatingSetpoint(evt) {
	log.debug "${virtualThermostat.name} changed heating to: ${evt.value} from ${state.heatingSetpoint}"
    state.heatingSetpoint = evt.value.toFloat()
    runIn(10, evaluate)
    //evaluate()
}
def virtualCoolingSetpoint(evt) {
	log.debug "${virtualThermostat.name} changed cooling to: ${evt.value} from ${state.coolingSetpoint}"
    state.coolingSetpoint = evt.value.toFloat()
    runIn(10, evaluate)
    //evaluate()
}
def getSetpoints() {
	log.debug "Manual updates of setpoints"
    //log.debug "Heating from ${state.heatingSetpoint} to ${virtualThermostat.currentHeatingSetpoint.toFloat()}"
    state.heatingSetpoint = virtualThermostat.currentHeatingSetpoint.toFloat()
    //log.debug "Cooling from ${state.coolingSetpoint} to ${virtualThermostat.currentCoolingSetpoint.toFloat()}"
    state.coolingSetpoint = virtualThermostat.currentCoolingSetpoint.toFloat()
}
def updateVirtualThermostat() {
	log.debug "Updating Thermostat: FAN: $thermostat.currentThermostatFanMode TherMode: $thermostat.currentThermostatMode OpState: $thermostat.currentThermostatOperatingState H: ${sensor[0].currentHumidity}"
	virtualThermostat.setThermostatFanMode(thermostat.currentThermostatFanMode)
    virtualThermostat.setThermostatMode(thermostat.currentThermostatMode)
    try {
    	virtualThermostat.setThermostatOperatingState(thermostat.currentThermostatOperatingState)
    } catch (e) {
    	log.info "Thermostat doesn't include operating state"
    
    }
    try {
    	virtualThermostat.setHumidity(sensor[0].currentHumidity)
    } catch (e) {
    	log.info "Thermostat doesn't include humidity"
    
    }
    
}
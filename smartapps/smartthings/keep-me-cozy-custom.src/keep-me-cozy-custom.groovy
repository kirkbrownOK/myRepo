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
    section("Delay before allowing the AC to come back on") {    
        input "sensorDelay", "number", title: "Delay between idle to update", required: false
	}
}

def installed()
{
	log.debug "enter installed, state: $state"
	subscribeToEvents()
}

def updated()
{
	log.debug "enter updated, state: $state"
    if ( sensorDelay > 5) {
       log.debug "sensor delay acceptable ${sensorDelay}"
       state.sensorDelay = sensorDelay
    } else {
    	state.sensorDelay = 10
        log.debug "Sensor Delay fixed"
    }
	unsubscribe()
//    thermostat.setHeatingOverride(heatingSetpoint)
//    thermostat.setCoolingOverride(coolingSetpoint)
    state.heatingSetpoint = heatingSetpoint
    state.coolingSetpoint = coolingSetpoint
	subscribeToEvents()
}

def subscribeToEvents()
{
	subscribe(location, changedLocationMode)
	if (sensor) {
    	sensor.each{
        	log.trace("${it.name}")
			subscribe(it, "temperature", temperatureHandler)
        }    
		subscribe(thermostat, "temperature", temperatureHandler)
		subscribe(thermostat, "thermostatMode", temperatureHandler)
        subscribe(thermostat, "thermostatOperatingstate", temperatureHandler)
        //subscribe(thermostat, "heatingOverride", heatingOverrideHandler)
        //subscribe(thermostat, "coolingOverride", coolingOverrideHandler)
        
	}
	//evaluate()
}

def changedLocationMode(evt)
{
	log.debug "changedLocationMode mode: $evt.value, heat: $heat, cool: $cool"
//    thermostat.setCoolingOverride(coolingSetpoint)
//    thermostat.setHeatingOverride(heatingSetpoint)
    
    state.heatingSetpoint = heatingSetpoint
    state.coolingSetpoint = coolingSetpoint    
	evaluate()
}

def temperatureHandler(evt)
{
    if(state.lastIdle > 0 ) {
    	//DO nothing
    } else {
        state.lastIdle = 1444016997130
    }
	log.debug "${evt.name} ${evt.value} ${evt.date}"
	if ((evt.name == "thermostatOperatingState") && (evt.value == "idle")) {
    	state.lastIdle = now() 
        log.debug "thermostatOperatingState changed to IDLE"
    }
    log.debug "LastIdle: ${state.lastIdle}"
    if((now() - state.lastIdle)/1000 > (60*state.sensorDelay) ) {
    	log.debug "Its been ${state.sensorDelay} minutes"
        evaluate()
    } else { 
    	log.debug "Its only been ${(now() - state.lastIdle)/1000/60}"
    }
	
}

private evaluate()
{
	if (sensor) {
		def threshold = 1.0
		def tm = thermostat.currentThermostatMode
		def ct = thermostat.currentTemperature
        def currentTemp = 0
        def sensorCounter = 0
        sensorCounter = 0
        
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
			if (currentTemp - state.coolingSetpoint >= threshold) {
				thermostat.setCoolingSetpoint(ct - 3)
				log.debug "thermostat.setCoolingSetpoint(${ct - 3}), ON"
			}
			else if (state.coolingSetpoint - currentTemp >= threshold && ct - thermostat.currentCoolingSetpoint >= threshold) {
				thermostat.setCoolingSetpoint(ct + 3)
				log.debug "thermostat.setCoolingSetpoint(${ct + 3}), OFF"
			}
		}
		if (tm in ["heat","emergency heat","auto"]) {
			// heater
			if (state.heatingSetpoint - currentTemp >= threshold) {
				thermostat.setHeatingSetpoint(ct + 3)
				log.debug "thermostat.setHeatingSetpoint(${ct + 3}), ON"
			}
			else if (currentTemp - state.heatingSetpoint >= threshold && thermostat.currentHeatingSetpoint - ct >= threshold) {
				thermostat.setHeatingSetpoint(ct - 3)
				log.debug "thermostat.setHeatingSetpoint(${ct - 3}), OFF"
			}
		}
	}
	else {
		thermostat.setHeatingSetpoint(state.heatingSetpoint)
		thermostat.setCoolingSetpoint(state.coolingSetpoint)
		thermostat.poll()
	}
}

// for backward compatibility with existing subscriptions
def coolingSetpointHandler(evt) {
	log.debug "coolingSetpointHandler()"
}
def heatingSetpointHandler (evt) {
	log.debug "heatingSetpointHandler ()"
}
def coolingOverrideHandler(evt) {
	log.debug "Cooling override from: ${state.coolingSetpoint} to ${evt.value}"
    state.coolingSetpoint = evt.value
    evaluate()

}

def heatingOverrideHandler(evt) {
	log.debug "Heating override from: ${state.heatingSetpoint} to ${evt.value}"
    state.heatingSetpoint = evt.value.toFloat()
    evaluate()  
}
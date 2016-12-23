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
    section("How long to delay between turning HVAC on?") {
		input "sensorDelay", "number", title: "Sensor Delay", required: false
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
	
    state.heatingSetpoint = heatingSetpoint
    state.coolingSetpoint = coolingSetpoint
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
	if (sensor) {
    	sensor.each{
        	log.trace("${it.name}")
			subscribe(it, "temperature", temperatureHandler)
        }    
		subscribe(thermostat, "temperature", temperatureHandlerThermostat)
		subscribe(thermostat, "thermostatMode", temperatureHandler)
        subscribe(thermostat, "thermostatOperatingstate", temperatureHandler)
        
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
        evaluate()
    } else { 
    	log.debug "Its only been ${(now() - state.lastIdle)/1000/60} minutes"
        //state.sensorDelay = 5
    }
	
}

def temperatureHandlerThermostat(evt)
{	
	log.debug "Thermostat temp update"
	evaluate()
   	
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
	}
	else {
		thermostat.setHeatingSetpoint(61)
		thermostat.setCoolingSetpoint(69)
		thermostat.poll()
	}
}

/**
 *  Light Follows Me and restores state before motion
 *
 *  Author: SmartThings and OKpowerman
 */

definition(
    name: "Light Follows Me and Returns",
    namespace: "okpowerman",
    author: "OKpowerman",
    description: "Turn your lights on when motion is detected and then off again once the motion stops for a set period of time.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo-switch.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo-switch@2x.png"
)

preferences {
	section("Turn on when there's movement..."){
		input "motion1", "capability.motionSensor", title: "Where?"
	}
	section("And off when there's been no movement for..."){
		input "minutes1", "number", title: "Minutes?"
	}
	section("Turn on/off light(s)..."){
		input "switches", "capability.switch", multiple: true
	}
    section("Using either on this light sensor (optional) or the local sunrise and sunset"){
		input "lightSensor", "capability.illuminanceMeasurement", required: false
	}
	section ("Sunrise offset (optional)...") {
		input "sunriseOffsetValue", "text", title: "HH:MM", required: false
		input "sunriseOffsetDir", "enum", title: "Before or After", required: false, options: ["Before","After"]
	}
	section ("Sunset offset (optional)...") {
		input "sunsetOffsetValue", "text", title: "HH:MM", required: false
		input "sunsetOffsetDir", "enum", title: "Before or After", required: false, options: ["Before","After"]
	}
	section ("Zip code (optional, defaults to location coordinates when location services are enabled)...") {
		input "zipCode", "text", title: "Zip code", required: false
	}
    
}

def installed() {
	initialize()

    
}

def updated() {
	unsubscribe()
	unschedule()
    getSunriseOffset()
    getSunsetOffset()
	initialize()

    
}

def motionHandler(evt) {
    
	//TRACE( "$evt.name: $evt.value   state: $state.lastState")
    if(enabled()) {
			if (evt.value == "active") {
			TRACE( "Active")
        
        
        	def switchState = switches.currentState("switch")
        	if (state.motionControl) {
        		TRACE("This Motion event is controlling state of the lights. Original last state:$state.lastState")
        	} else {
        		for (it in (switches)) {
					if (it.currentSwitch == "on") {
            	    	state.lastState = "on"
                    	state.lastLevel = it.currentLevel
						break
					} else {
            	    	state.lastState = "off"
                    	state.lastLevel = 0
            		}
				}
            	state.motionControl = true
        	}
            
        
			switches.setLevel(99)   
        	TRACE("Original State: $state.lastState:$state.lastLevel")
        	switches.on()
        } else if (evt.value == "inactive") {
    		TRACE( "lastState is $state.lastState")
			runIn(minutes1 * 60, scheduleCheck, [overwrite: false])
		}
    } else {
        	TRACE("Not the time to turn on the lights")
        
        }
}
def scheduleCheck() {
    TRACE("RUNNING CHECK")
	def motionState = motion1.currentState("motion")
    if (motionState.value == "inactive") {
        def elapsed = now() - motionState.rawDateCreated.time
    	def threshold = 1000 * 60 * minutes1 - 1000
    	if (elapsed >= threshold) {
        	if(state.lastState=="on") {
            	TRACE( "Lights on BEFORE motion. Time since motion:($elapsed ms):  setting lights to DIM")
            	switches.setLevel(state.lastLevel)
                TRACE( "set level to: $state.lastLevel")
            } else {
             	switches.off()
                state.lastState = "off"
                TRACE( "Lights NOT on before motion. Time since motion:($elapsed ms): turning lights off")
                
            }    
            state.motionControl = false
            TRACE("Lights not controlled by motion any more")
            
    	} else {
        	TRACE( "Motion has not stayed inactive long enough since last check ($elapsed ms):  doing nothing")
        }
    } else {
    	TRACE( "Motion is active, do nothing and wait for inactive")
	}
}

def initialize() {
	subscribe(motion1, "motion", motionHandler)
    subscribe(switches, "switch", switchHandler)
    state.lastState = "off"
    
    state.motionControl = false
	if (lightSensor) {
		subscribe(lightSensor, "illuminance", illuminanceHandler, [filterEvents: false])
	}
	else {
		//subscribe(location, "position", locationPositionChange)
        //subscribe(switch, "on", sunriseSunsetTimeHandler)
		//subscribe(location, "sunriseTime", sunriseSunsetTimeHandler)
		//subscribe(location, "sunsetTime", sunriseSunsetTimeHandler)
		astroCheck()
        //state.lastAstroCheck = now() - 86400000
	}
}
def switchHandler(evt) {
	//Disable any motion control even because the switch was manually controlled
    updated()
    TRACE("Motion Control Restart")
    

}
/*
def sunriseSunsetTimeHandler(evt) {
	state.lastSunriseSunsetEvent = now()
	log.debug "SmartNightlight.sunriseSunsetTimeHandler($app.id)"
	astroCheck()
}
*/
def astroCheck() {
	def s = getSunriseAndSunset(zipCode: zipCode, sunriseOffset: sunriseOffset, sunsetOffset: sunsetOffset)
	state.riseTime = s.sunrise.time
	state.setTime = s.sunset.time
    state.lastAstroCheck = now()
	log.debug "rise: ${new Date(state.riseTime)}($state.riseTime), set: ${new Date(state.setTime)}($state.setTime) lastAstro ${state.lastAstroCheck}"

}

private enabled() {
	def result
    if (now() - state.lastAstroCheck > 3600000) {
    	//Its been 1 hours since last sunset/sunrise time
    	astroCheck()    
    } else {
    	log.debug "Astro not needed, perform in ${(86400000-(now() - state.lastAstroCheck))/(1000*60*60)} hours"
    }
	if (lightSensor) {
		result = lightSensor.currentIlluminance < 30
	}
	else {
		def t = now()
        TRACE("now is ${t} rising Time: ${state.riseTime} setTime: ${state.setTime}")
		result = t < state.riseTime || t > state.setTime
	}
	return result
}

private getSunriseOffset() {
	sunriseOffsetValue ? (sunriseOffsetDir == "Before" ? "-$sunriseOffsetValue" : sunriseOffsetValue) : null
}

private getSunsetOffset() {
	sunsetOffsetValue ? (sunsetOffsetDir == "Before" ? "-$sunsetOffsetValue" : sunsetOffsetValue) : null
}

private def TRACE(message) {
    log.debug message
}
/**
 *  Sometimes I interrupt the Gentle Wake up for the bedroom. As a result the wife HATES that the 
  light comes on the next time at say 10% brightness. SO this app will monitor the light switching on
  at less than a user defined brightness level and then set the brightness to full brightness. It 
  should not change the brightness to full, unless it was turned ON. in other words, it should let dimmed
  levels of light stay dimmed.
 *
 *  Author: Kirk Brown
 */
definition(
    name: "Monitor Light Dimness to Force Full Brightness",
    namespace: "okpowerman",
    author: "okpowerman",
    description: "When a light is turned on at a dimness lower than threshold, turn it on full brightness",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png"
)

preferences {
	section("Turn on a light...") {
		input "lights", "capability.switch"
	}
    section("Light threshold 1 to 100") {
		input "lightThreshold", "number"
	}
}

def installed() {
	initialize()
	TRACE("installed to ${lights}:${lightThreshold}")
}

def updated() {
	unsubscribe()
    initialize()

}
def initialize() {
    TRACE("initialized to ${lights}:${lightThreshold}")
	subscribe(lights, "switch.on", checkBrightness)
}

def checkBrightness(evt) {
	state.threshold = lightThreshold*1.0
    state.currentLevel = lights.currentValue("level")*1.0
    TRACE(" Light Event") 
	TRACE("Light ON level: ${state.currentLevel} and threshold is: ${state.threshold}")
	if (state.currentLevel < state.threshold) {
    
    log.trace("Less than threshold")
    }
	if (state.currentLevel < state.threshold) {
    	lights.setLevel(99)
        TRACE("Turning up the brightness")
    } else {
    	TRACE("No need to turn up the brightness")
    }   	
   
}

private def TRACE(message) {
    //log.debug message
}
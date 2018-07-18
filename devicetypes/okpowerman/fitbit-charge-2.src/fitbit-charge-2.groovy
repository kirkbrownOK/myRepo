/**
 *  Fitbit User
 *
    Based on Jawbone User
 *  Author: juano23@gmail.com
 *  Date: 2013-08-15
 */
 // for the UI

metadata {
	// Automatically generated. Make future change here.
	definition (name: "Fitbit Charge 2", namespace: "okpowerman", author: "Kirk Brown") {
		capability "Refresh"
		capability "Polling"
        capability "Switch"
        capability "Sleep Sensor"
        capability "Step Sensor"  
        
        attribute "sleepText", "string"
        attribute "sleepStart", "string"
        
        //command 
        
	}

    simulator {
        status "sleeping": "sleeping: 1"
        status "not sleeping": "sleeping: 0"
    }

    tiles {
        standardTile("sleeping", "device.sleeping", width: 1, height: 1, canChangeIcon: false, canChangeBackground: false) {
            state("sleeping", label: "Sleeping", icon:"st.Bedroom.bedroom12", backgroundColor:"#ffffff",action:"on")
            state("awake", label: "Awake", icon:"st.Health & Wellness.health12", backgroundColor:"#79b821", action:"off")
        }
        standardTile("steps", "device.steps", width: 2, height: 2, canChangeIcon: false, canChangeBackground: false) {
            state("steps", label: '${currentValue} Steps', icon:"st.Health & Wellness.health11", backgroundColor:"#ffffff")                     
        }
        standardTile("goal", "device.goal", width: 1, height: 1, canChangeIcon: false, canChangeBackground: false,  decoration: "flat") {
            state("goal", label: '${currentValue} Steps', icon:"st.Health & Wellness.health5", backgroundColor:"#ffffff")
        }                
        standardTile("refresh", "device.steps", inactiveLabel: false, decoration: "flat") {
            state "default", action:"polling.poll", icon:"st.secondary.refresh"
        }
        main "steps"
        details(["steps", "goal", "sleeping", "refresh"])
    }
}
//public wattvisionDateFormat() { "yyyy-MM-dd'T'HH:mm:ss" }
public getSleepTime(dateString,pattern = "yyyy-MM-dd'T'HH:mm:ss.zzz") {
	return parse(pattern,dateString)
}
public def off(){
    sendEvent(name:"switch",value:"off")
    sendEvent(name:"sleeping",value:"awake")
}
public def on(){
    sendEvent(name:"switch",value:"on")
    sendEvent(name:"sleeping",value:"sleeping")
}
def generateSleepingEvent(boolean sleeping) {
    log.debug "Here in generateSleepingEvent!"
    def value = formatValue(sleeping)
    def linkText = getLinkText(device)
    def descriptionText = formatDescriptionText(linkText, sleeping)
    def handlerName = getState(sleeping)

    def results = [
        name: "sleeping",
        value: value,
        unit: null,
        linkText: linkText,
        descriptionText: descriptionText,
        handlerName: handlerName
    ]

    sendEvent (results)

    log.debug "Generating Sleep Event: ${results}"        


    def results2 = [
        name: "button",
        value: "held",
        unit: null,
        linkText: linkText,
        descriptionText: "${linkText} button was pressed",
        handlerName: "buttonHandler",
        data: [buttonNumber: 1],
        isStateChange: true
    ] 
    log.debug "Generating Button Event: ${results2}"

    sendEvent (results2)
}


def poll() {
	log.debug "Executing 'poll'"
	try {
    	def results = parent.pollChild(this)
        } catch (e) {
        log.debug "Poll failed: $e."
    }    
	return null
}
def refresh() {
	poll()
}

def uninstalled() {
	log.debug "Uninstalling device, then app"
	parent.app.delete()
}

private String formatValue(boolean sleeping) {
    if (sleeping)
    	return "sleeping"
    else
        return "not sleeping"
}

private formatDescriptionText(String linkText, boolean sleeping) {
    if (sleeping)
    	return "$linkText is sleeping"
    else
        return "$linkText is not sleeping"
}

private getState(boolean sleeping) {
    if (sleeping)
    	return "sleeping"
    else
        return "not sleeping"
}
public myDateFormat() { "yyyy-MM-dd'T'HH:mm:ss" }
def saveSleepEvent(resp) {
	def currentSleep = device.currentValue("sleepingText")
    def startTime = ""
    //resp = [summary:[totalTimeInBed:331, totalMinutesAsleep:314, totalSleepRecords:1], sleep:[[isMainSleep:true, restlessCount:12, logId:13421801082, minutesAfterWakeup:0, dateOfSleep:"2017-01-08", minutesToFallAsleep:0, startTime:"2017-01-08T03:31:00.000", restlessDuration:12, minutesAwake:11, timeInBed:331, minuteData:[[dateTime:"03:31:00", value:3], [dateTime:"03:32:00", value:3]]]]]
    if(resp.containsKey("sleep")) {
    	//TRACE("cKey: sleep : $resp.sleep")
        def sleeps = resp.sleep
        //Handle multiple sleep events
        sleeps.each{ it ->
        	TRACE("sleep each")
            if( it.containsKey("isMainSleep") ) {
            	state.lastSleepId != it.logId
                state.lastSleepId = it.logId
                sendEvent(name:"sleeping",value:"sleeping")
                //This is the main sleep event
                if(it.containsKey("startTime"))  {
                	startTime = it.startTime
                    def now = new Date()
                    def end = Date.parse("yyyy-MM-dd'T'HH:mm:ss","${startTime}".replace("0.000","0"))
                    long unxNow = now.getTime()
                    long unxEnd = end.getTime()

                    unxNow = unxNow/1000
                    unxEnd = unxEnd/1000 - location.timeZone.rawOffset.toInteger()/1000
					TRACE("Now() $unxNow : SleepStartTime $unxEnd ")
                    
                    //timeDiff = Math.abs(unxNow-unxEnd)
                    //timeDiff = Math.round(timeDiff/60)                   
                }
                TRACE("Main sleep startTime is ${startTime}")
                
                //TRACE("gST ${startTimeDate}")
            }
        }
    } else if (resp.containsKey("summary")){
    	if (resp.summary.totalSleepRecords == 0) {
        	TRACE("No recorded sleep for today")
            sendEvent(name:"sleeping", value: "awake")
        }   
    }
    resp.each {name,value ->    	
        //if(name == "sleep") TRACE(".each $name : $value")
    }
    
    if(currentSleep != resp) {
		sendEvent(name: "sleepText", value: "${resp}",displayed: false)
    } else {
    	TRACE("No new sleep")
    }
    //def startTime = resp?.sleep?.startTime

}

private def TRACE(message) {
	log.debug "${message}"
}
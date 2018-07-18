/**
 *  Monoprice Z-Wave Plus Motion/Temperature Sensor
 *	(P/N 15271)
 *  Based on Monoprice z-wave pluse Door/Window Sensor 1.0 by Kevin LaFramboise
 *  (P/N 15270)
 *
 *  Author: 
 *    Kevin LaFramboise (krlaframboise)
 *
 *  URL to documentation:
 *    
 *
 *  Changelog:
 *
 *    1.0 (12/29/2016)
 *      - Initial Release
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
metadata {
	definition (
		name: "Monoprice Z-Wave Plus Motion Temp Sensor", 
		namespace: "kirkbrownOK", 
		author: "Kevin LaFramboise" //Modified by Kirk Brown
	) {
		capability "Sensor"
		//capability "Contact Sensor"
		capability "Configuration"
		capability "Battery"
		capability "Tamper Alert"
        capability "Temperature Measurement"
        capability "Motion Sensor"
		capability "Refresh"

		attribute "lastCheckin", "number"
			
		fingerprint deviceId: "0x0701", inClusters: "0x5E, 0x98, 0x86, 0x72, 0x5A, 0x85, 0x59, 0x73, 0x80, 0x71, 0x70, 0x84, 0x7A"
		//fingerprint type:"0701", cc: "5E,98,72,5A,80,73,86,84,85,59,71,70,7A"
        fingerprint type:"8C07", inClusters: "5E,98,86,72,5A,31,71"
		fingerprint mfr:"0109", prod:"2002", model:"0205" 
	}
	
	simulator { }
	
	preferences {
		input "checkinInterval", "number",
			title: "Minimum Check-in Interval (Hours)",
			defaultValue: 6,
			range: "1..167",
			displayDuringSetup: true, 
			required: false
		input "reportBatteryEvery", "number", 
			title: "Battery Reporting Interval (Hours)", 
			description: "This setting can't be less than the Minimum Check-in Interval.",
			defaultValue: 6,
			range: "1..167",
			displayDuringSetup: true, 
			required: false
		input "useF", "number", 
			title: "F or C", 
			description: "1 for F and 0 for C",
			defaultValue: 1,
			
			displayDuringSetup: true
			//required: true
		input "offsetTempVal", "number", 
			title: "Temp Offset", 
			description: "Choose a +/- temp offset",
			defaultValue: 0,
			displayDuringSetup: true
			//required: true            
//		input "enableExternalSensor", "bool", 
//			title: "Enable External Sensor?",
//			description: "The Monoprice Door/Window Sensor includes terminals that allow you to attach an external sensor.",
//			defaultValue: false,
//			displayDuringSetup: true, 
//			required: false
		input "autoClearTamper", "bool", 
			title: "Automatically Clear Tamper?",
			description: "The tamper detected event is raised when the device is opened.  This setting allows you to decide whether or not to have the clear event automatically raised when the device closes.",
			defaultValue: false,
			displayDuringSetup: true, 
			required: false
		input "debugOutput", "bool", 
			title: "Enable debug logging?", 
			defaultValue: true, 
			displayDuringSetup: true, 
			required: false
	}

	tiles(scale: 2) {
    /*
		multiAttributeTile(name:"contact", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.contact", key: "PRIMARY_CONTROL") {
				attributeState "closed", 
					label:'closed', 
					icon:"st.contact.contact.closed", 
					backgroundColor:"#79b821"
				attributeState "open", 
					label:'open', 
					icon:"st.contact.contact.open", 
					backgroundColor:"#ffa81e"
			}
		}
        */
		
		valueTile("battery", "device.battery", decoration: "flat", width: 2, height: 2){
			state "battery", label:'${currentValue}% battery', unit:""
		}		
		
		standardTile("tampering", "device.tamper", width: 2, height: 2) {
			state "detected", label:"Tamper", backgroundColor: "#ff0000"
			state "clear", label:"No Tamper", backgroundColor: "#cccccc"			
		}
		standardTile("motion", "device.motion", width: 3, height: 2) {
			state "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0"
			state "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff"
		}

		valueTile("temperature", "device.temperature", inactiveLabel: false) {
			state "temperature", label:'${currentValue}°',
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
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat") {
			state "battery", label:'${currentValue}% battery', unit:"%"
		}      
	
		standardTile("refresh", "device.refresh", width: 2, height: 2) {
			state "default", label: "Refresh", action: "refresh", icon:""
		}
		
		main("temperature")
		details(["motion", "temperature", "battery", "tampering", "refresh"])
	}
}

def updated() {	
	// This method always gets called twice when preferences are saved.
	if (!isDuplicateCommand(state.lastUpdated, 3000)) {
				
		state.lastUpdated = new Date().time
		logTrace "updated()"
		
//		if (state.checkinInterval != settings?.checkinInterval || state.enableExternalSensor != settings?.enableExternalSensor) {
		if (state.checkinInterval != settings?.checkinInterval ) {

			state.pendingChanges = true
		}
	}	
}


def configure() {	
	logTrace "configure()"
	def cmds = []
	
	if (!device.currentValue("motion")) {
		sendEvent(name: "motion", value: "inactive", isStateChange: true, displayed: false)
	}
    if (!device.currentValue("temperature")) {
		sendEvent(name: "temperature", value: 73, unit: "F", isStateChange: true, displayed: false)
	}
	
	if (!state.isConfigured) {
		logTrace "Waiting 1 second because this is the first time being configured"
		// Give inclusion time to finish.
		cmds << "delay 1000"			
	}
		
	cmds += delayBetween([
		wakeUpIntervalSetCmd(getCheckinIntervalSetting() * 60),
		//externalSensorConfigSetCmd(settings?.enableExternalSensor ?: false),
		//externalSensorConfigGetCmd(),
        //sensorMultilevelSetUnits(0x01),
        //configSetCmd(paramNumber, valSize, val),
        configGetCmd(1),
        configGetCmd(2),
        configGetCmd(3),
        configGetCmd(4),
        configGetCmd(5),
        
        
		batteryGetCmd()
	], 100)
		
	logDebug "Sending configuration to device."
	return cmds
}

private getCheckinIntervalSetting() {
	return (settings?.checkinInterval ?: 6)
}
				
def parse(String description) {
	def result = []
	
	if (description.startsWith("Err 106")) {
		state.useSecureCmds = false
		log.warn "Secure Inclusion Failed: ${description}"
		result << createEvent( name: "secureInclusion", value: "failed", eventType: "ALERT", descriptionText: "This sensor failed to complete the network security key exchange. If you are unable to control it via SmartThings, you must remove it from your network and add it again.")
	}
	else if (description.startsWith("Err")) {
		log.warn "Parse Error: $description"
		result << createEvent(descriptionText: "$device.displayName $description", isStateChange: true)
	}
	else {
		def cmd = zwave.parse(description, getCommandClassVersions())
		if (cmd) {
			result += zwaveEvent(cmd)
		}
		else {
			logDebug "Unable to parse description: $description"
		}
	}
	
	if (canCheckin()) {
		result << createEvent(name: "lastCheckin",value: new Date().time, isStateChange: true, displayed: false)
	}
	
	return result
}

private canCheckin() {
	// Only allow the event to be created once per minute.
	def lastCheckin = device.currentValue("lastCheckin")
	return (!lastCheckin || lastCheckin < (new Date().time - 60000))
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def encapCmd = cmd.encapsulatedCommand(getCommandClassVersions())
		
	def result = []
	if (encapCmd) {
		state.useSecureCmds = true
		result += zwaveEvent(encapCmd)
	}
	else if (cmd.commandClassIdentifier == 0x5E) {
		logTrace "Unable to parse ZwaveplusInfo cmd"
	}
	else {
		log.warn "Unable to extract encapsulated cmd from $cmd"
		result << createEvent(descriptionText: "$cmd")
	}
	return result
}

private getCommandClassVersions() {
	[
		0x20: 1,  // Basic
		0x59: 1,  // AssociationGrpInfo
		0x5A: 1,  // DeviceResetLocally
		0x5E: 2,  // ZwaveplusInfo
		0x70: 1,  // Configuration
		0x71: 3,  // Alarm v1 or Notification v4
		0x72: 2,  // ManufacturerSpecific*=
		0x73: 1,  // Powerlevel
		0x7A: 2,  // FirmwareUpdateMd
		0x80: 1,  // Battery
		0x84: 2,  // WakeUp
		0x85: 2,  // Association
		0x86: 1,  // Version (2)
		0x31: 5,  // SensorMultilevel
        0x9E: 1,  // SensorConfigLevel
		0x98: 1  // Security
	]
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv2.WakeUpNotification cmd)
{
	logTrace "WakeUpNotification: $cmd"
	def result = []
	
	if (canSendConfiguration()) {
		result += configure()
		result << "delay 500"
	}
	else if (canReportBattery()) {
		result << batteryGetCmd()
		result << "delay 500"
	}
	else {
		logTrace "Skipping battery check because it was already checked within the last $reportEveryHours hours."
	}
    /*
    result << configSetCmd(4,1,0x01)
    result << "delay 500"
    result << configGetCmd(1)
    result << "delay 500"
    result << configGetCmd(2)
    result << "delay 500"
    result << configGetCmd(3)
    result << "delay 500"
    result << configGetCmd(4)
    result << "delay 500"
    result << configGetCmd(5) 
	result << "delay 500"
    */
    result << temperatureGetCmd()
    result << "delay 500"
	if (result) {
		result << "delay 500"
	}
	
	result << wakeUpNoMoreInfoCmd()
	
	return response(result)
}

private canReportBattery() {
	def reportEveryHours = settings?.reportBatteryEvery ?: 6
	def reportEveryMS = (reportEveryHours * 60 * 60 * 1000)
		
	return (!state.lastBatteryReport || ((new Date().time) - state.lastBatteryReport > reportEveryMS)) 
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	logTrace "BatteryReport: $cmd"
	def map = [ 
		name: "battery", 		
		unit: "%"
	]
	
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "Battery is low"
		map.isStateChange = true
	}
	else {	
		def isNew = (device.currentValue("battery") != cmd.batteryLevel)
		map.value = cmd.batteryLevel
		map.displayed = isNew
		map.isStateChange = isNew
		logDebug "Battery is ${cmd.batteryLevel}%"
	}	
	
	state.lastBatteryReport = new Date().time	
	[
		createEvent(map)
	]
}	

def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
	logTrace "ConfigurationReport: $cmd"
	def parameterName
	switch (cmd.parameterNumber) {
		default:	
			parameterName = "Parameter #${cmd.parameterNumber}"
	}		
	if (parameterName) {
		logDebug "${parameterName}: ${cmd.configurationValue}"
	} 
	state.isConfigured = true
	state.pendingRefresh = false
	state.pendingChanges = false
	state.checkinInterval = getCheckinIntervalSetting()
	return []
}
//zwave.sensorConfigurationV1.sensorTriggerLevelGet().format(),
def zwaveEvent(physicalgraph.zwave.commands.sensorconfigurationv1.SensorTriggerLevelReport cmd) {
	def result = []	
	logTrace "Sensor Trigger LevelReport: $cmd"
}


def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	logTrace "BasicReport: $cmd"	
	return []
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	logTrace "Basic Set: $cmd"	
	return []
}


def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd) {
	def result = []	
	logTrace "NotificationReport: $cmd"
	if (cmd.notificationType == 0x06) {
		result += handleContactEvent(cmd.event)
	}
	else if (cmd.notificationType == 0x07) {		
		result += handleTamperEvent(cmd.event)
	}
	return result
}
def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd) {

	logTrace "SensorMultilevelReport: $cmd"
	def result = []
	def map = [:]
    def myVal
	switch (cmd.sensorType) {
		case 1:
			def cmdScale = cmd.scale == 1 ? "F" : "C"
			map.name = "temperature"
            myVal = cmd.scaledSensorValue + offsetTempVal
			map.value = myVal
			map.unit = getTemperatureScale()
			break;
		case 3:
			map.name = "illuminance"
			map.value = cmd.scaledSensorValue.toInteger().toString()
			map.unit = "lux"
			break;
		case 5:
			map.name = "humidity"
			map.value = cmd.scaledSensorValue.toInteger().toString()
			map.unit = cmd.scale == 0 ? "%" : ""
			break;
		case 0x1E:
			map.name = "loudness"
			map.unit = cmd.scale == 1 ? "dBA" : "dB"
			map.value = cmd.scaledSensorValue.toString()
			break;
		default:
			map.descriptionText = cmd.toString()
	}
	result << createEvent(map)
	return result
}
def zwaveEvent(physicalgraph.zwave.Command cmd) {
	logDebug "Unhandled Command: $cmd"
	return []
}

private handleTamperEvent(event) {
	def result = []
	def val
	if (event == 0x03) {
		val = "detected"
	}
	else if (event == 0) {
		if (settings?.autoClearTamper) {
			val = "clear"
		}
		else {
			logDebug "Tamper is Clear"
		}
	}
	if (val) {
		result << createEvent(getEventMap("tamper", val))
	}
	return result
}

// Resets the tamper attribute to clear and requests the device to be refreshed.
def refresh() {	
	if (device.currentValue("tamper") != "clear") {
		sendEvent(getEventMap("tamper", "clear"))		
	}
	else {
		logDebug "The configuration and attributes will be refresh the next time the device wakes up.  If you want this to happen immediately, open the back cover of the device, wait until the red light turns solid, and then put the cover back on."
		state.pendingRefresh = true
	}
    
    
}

def getEventMap(eventName, newVal) {	
	def isNew = device.currentValue(eventName) != newVal
	def desc = "${eventName.capitalize()} is ${newVal}"
	logDebug "${desc}"
	[
		name: eventName, 
		value: newVal, 
		displayed: isNew,
		descriptionText: desc
	]
}

private wakeUpIntervalSetCmd(val) {
	logTrace "wakeUpIntervalSetCmd(${val})"
	return secureCmd(zwave.wakeUpV2.wakeUpIntervalSet(seconds:val, nodeid:zwaveHubNodeId))
}

private wakeUpNoMoreInfoCmd() {
	return secureCmd(zwave.wakeUpV2.wakeUpNoMoreInformation())
}

private batteryGetCmd() {
	logTrace "Requesting battery report"
	return secureCmd(zwave.batteryV1.batteryGet())
}

private externalSensorConfigGetCmd() {
	return configGetCmd(1)
}
private retriggerTimeGetCmd() {
	return configGetCmd(1)
}
private temperatureScaleGetCmd() {
	return configGetCmd(2)
}
private pirSensitivityGetCmd() {
	return configGetCmd(3)
}
private temperatureGetCmd() {
	//return secureCmd(zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:0x01,scale:useF)) // get temp 
    return secureCmd(zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:0x01, scale:0x01)) // get temp 
}
private sensorTriggerLevel() {
	return secureCmd(zwave.sensorConfigurationV1.sensorTriggerLevelGet())
}
private temperatureOffsetGetCmd() {
	return configGetCmd(4)
}

private externalSensorConfigSetCmd(isEnabled) {
	//return configSetCmd(1, 1, (isEnabled ? 0xFF : 0x00))
}

private configSetCmd(paramNumber, valSize, val) {	
	logTrace "Setting configuration param #${paramNumber} to ${val}"
	return secureCmd(zwave.configurationV1.configurationSet(parameterNumber: paramNumber, size: valSize, configurationValue: [val]))
}

private configGetCmd(paramNumber) {
	logTrace "Requesting configuration report for param #${paramNumber}"
	return secureCmd(zwave.configurationV1.configurationGet(parameterNumber: paramNumber))
}
private configGetReport() {
	logTrace "Requesting configuration report "
	return secureCmd(zwave.configurationV1.configurationReport())
}

private secureCmd(cmd) {
	if (state.useSecureCmds == false) {
		return cmd.format()
	}
	else {
		return zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	}
}

private canSendConfiguration() {
	return (!state.isConfigured || state.pendingRefresh != false	|| state.pendingChanges != false)
    //return true
}

private isDuplicateCommand(lastExecuted, allowedMil) {
	!lastExecuted ? false : (lastExecuted + allowedMil > new Date().time) 
}

private logDebug(msg) {
	if (settings?.debugOutput || settings?.debugOutput == null) {
		log.debug "$msg"
	}
}

private logTrace(msg) {
	log.trace "$msg"
}
/**
 *  Copyright 2015 SmartThings
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
 *  Aeon Home Energy Meter
 *
 *  Author: SmartThings
 *
 *  Date: 2013-05-30
 */
metadata {
	definition (name: "AEON Meter As Switches", namespace: "kirkbrownOK", author: "Kirk Brown") {
		capability "Energy Meter"
		capability "Power Meter"
		capability "Configuration"
		capability "Sensor"
        capability "Refresh"
        capability "Polling"
        
        attribute "energyClamp1", "string"
        attribute "energyClamp2", "string"
        attribute "powerClamp1", "string"
        attribute "powerClamp2", "string"        
        attribute "ampsClamp1", "string"
        attribute "ampsClamp2", "string"
        
		command "reset"
        command "resetmyMeter"
        command "configure"
        command "refresh"
        command "poll"
        command "resetDisplay"
        
		fingerprint deviceId: "0x2101", inClusters: "0x70,0x31,0x72,0x86,0x32,0x80,0x85,0x60"

		//fingerprint deviceId: "0x3101", inClusters: "0x70,0x32,0x60,0x85,0x56,0x72,0x86"
	}

	// simulator metadata
	simulator {
		for (int i = 0; i <= 10000; i += 1000) {
			status "power  ${i} W": new physicalgraph.zwave.Zwave().meterV1.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 33, scale: 2, size: 4).incomingMessage()
		}
		for (int i = 0; i <= 100; i += 10) {
			status "energy  ${i} kWh": new physicalgraph.zwave.Zwave().meterV1.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 33, scale: 0, size: 4).incomingMessage()
		}
        // TODO: Add data feeds for Volts and Amps
	}

	// tile definitions
	tiles {
    
    // Watts row

        valueTile("powerClamp1", "device.powerClamp1", width:1, height: 1) {
        	state("default", label:'${currentValue} W',foregroundColor: "#000000",color: "#000000", 
            	backgroundColors:[
					[value: "0", 		color: "#153591"],
					[value: "500", 	color: "#1e9cbb"],
					[value: "900", 	color: "#90d2a7"],
					[value: "1500", 	color: "#44b621"],
					[value: "2000", 	color: "#f1d801"],
					[value: "3000", 	color: "#d04e00"], 
					[value: "4500", 	color: "#bc2323"]
				]
			)
        }
        valueTile("powerClamp2", "device.powerClamp2") {
        	state("default", 
        		label:'${currentValue} W', 
            	foregroundColors:[
            		[value: 1, color: "#000000"],
            		[value: 5000, color: "#ffffff"]
            	], 
            	foregroundColor: "#000000",
                backgroundColors:[
					[value: "0", 		color: "#153591"],
					[value: "3000", 	color: "#1e9cbb"],
					[value: "6000", 	color: "#90d2a7"],
					[value: "9000", 	color: "#44b621"],
					[value: "12000", 	color: "#f1d801"],
					[value: "15000", 	color: "#d04e00"], 
					[value: "18000", 	color: "#bc2323"]
					
				/* For low-wattage homes, use these values
					[value: "0", color: "#153591"],
					[value: "500", color: "#1e9cbb"],
					[value: "1000", color: "#90d2a7"],
					[value: "1500", color: "#44b621"],
					[value: "2000", color: "#f1d801"],
					[value: "2500", color: "#d04e00"],
					[value: "3000", color: "#bc2323"]
				*/
				]
			)
        }

        valueTile("energyClamp1", "device.energyClamp1") {
        	state(
        		"default", 
        		label: '${currentValue} kWh', 
        		foregroundColor: "#000000", 
        		backgroundColor: "#ffffff")
        }        
        valueTile("energyClamp2", "device.energyClamp2") {
        	state(
        		"default", 
        		label: '${currentValue} kWh', 
        		foregroundColor: "#000000", 
        		backgroundColor: "#ffffff")
        }
    
    // Amps row

        valueTile("ampsClamp1", "device.ampsClamp1") {
        	state(
        		"default",
        		label:'${currentValue} Amps',
        		foregroundColor: "#000000", 
    			color: "#000000", 
    			backgroundColors:[
					[value: "0", 	color: "#153591"],
					[value: "25", 	color: "#1e9cbb"],
					[value: "50", 	color: "#90d2a7"],
					[value: "75", 	color: "#44b621"],
					[value: "100", color: "#f1d801"],
					[value: "125", color: "#d04e00"], 
					[value: "150", color: "#bc2323"]
				]
			)
        }
        valueTile("ampsClamp2", "device.ampsClamp2") {
        	state(
        		"default", 
        		label:'${currentValue} Amps',
        		foregroundColor: "#000000", 
    			color: "#000000", 
    			backgroundColors:[
					[value: "0", 	color: "#153591"],
					[value: "25", 	color: "#1e9cbb"],
					[value: "50", 	color: "#90d2a7"],
					[value: "75", 	color: "#44b621"],
					[value: "100", color: "#f1d801"],
					[value: "125", color: "#d04e00"], 
					[value: "150", color: "#bc2323"]
				]
			)        		
        }
        
    // Controls row
		standardTile("reset", "command.reset", inactiveLabel: false) {
			state "default", label:'reset', action:"reset", icon: "st.Health & Wellness.health7"
		}
        standardTile("resetmyMeter", "command.resetmyMeter", inactiveLabel: false) {
			state "default", label:'resetAll', action:"resetmyMeter", icon: "st.Health & Wellness.health7"
		}
		standardTile("refresh", "command.refresh", inactiveLabel: false) {
			state "default", label:'refresh', action:"refresh.refresh", icon:"st.secondary.refresh-icon"
		}
		standardTile("configure", "command.configure", inactiveLabel: false) {
			state "configure", label:'', action: "configure", icon:"st.secondary.configure"
		}
		standardTile("toggle", "command.toggleDisplay", inactiveLabel: false) {
			state "default", label: "toggle", action: "toggleDisplay", icon: "st.motion.motion.inactive"
		}
		/* HEMv1 has a battery; v2 is line-powered */

// HEM Version Configuration only needs to be done here - comments to choose what gets displayed

		main (["powerClamp1","powerClamp2"])
		details([
			"powerClamp1","powerClamp2","refresh",
			"ampsClamp1","ampsClamp2","reset",
            "energyClamp1","energyClamp2","resetmyMeter",
            "configure", 
            //"battery",					// Include this for HEMv1
		])
	}
    preferences {
    	//input "kWhCost", "number", title: "\$/kWh (0.05)", description: "0.05", defaultValue: "0.05" 
    	input "kWhDelay", "number", title: "kWh report seconds (60)", /* description: "120", */ defaultValue: 120
    	input "detailDelay", "number", title: "Detail report seconds (30)", /* description: "30", */ defaultValue: 30
        
        input "param1", "number", title: "P1 Voltage", defaultValue: 120, range: "110..275"
        section{
        	input type: "paragraph", element: "paragraph", title: "Configure Selective Reporting", description: "Use these settings to configure the required change to trigger Selective Reporting."
          	input "param3", "enum", title: "P3 Selective Reporting: (configured in 4-11)", options:["1":"Enabled","0":"Disabled"], defaultValue: "Disabled"
			input "param4", "number", title: "P4 Minimum change in Watts Whole HEM", defaultValue: 50, range: "0..60000"
            input "param5", "number", title: "P5 Minimum change in Watts Clamp1", defaultValue: 50, range: "0..60000"
            input "param6", "number", title: "P6 Minimum change in Watts Clamp2", defaultValue: 50, range: "0..60000"
            input "param7", "number", title: "P7 Minimum change in Watts Clamp3", defaultValue: 50, range: "0..60000"
            input "param8", "number", title: "P8 Minimum % change in Wattage Whole HEM", defaultValue: 10, range: "0..100"
            input "param9", "number", title: "P9 Minimum % change in Wattage Clamp1", defaultValue: 10, range: "0..100"
            input "param10", "number", title: "P10 Minimum % change in Wattage Clamp2", defaultValue: 10, range: "0..100"
            input "param11", "number", title: "P11 Minimum % change in Wattage Clamp3", defaultValue: 10, range: "0..100"
            input "param12", "enum", title: "P12 Accumulate kWH when battery Powered", defaultValue: "No", options: ["1":"Yes", "0":"No"]
        }
        section{
        	input "param100", "enum", title: "P100 Reset P101-103", options:["1":"Reset Groups","0":"Dont Reset"], defaultValue: "Dont Reset"
        	input type: "paragraph", element: "paragraph", title: "Configure Group 1 Reporting", description: "Use these settings to configure group 1 reporting."        	
            input "param110", "enum", title: "P110 Reset P111-113", options:["1":"Reset Groups","0":"Dont Reset"], defaultValue: "Dont Reset"
            input "param111", "number", title: "P111 Interval for sending Group1 report", defaultValue: 720, range: "1..4294967295"
            input "g1_whole_kwh", "bool", title:"Whole Home KWH"
            input "g1_whole_w", "bool", title:"Whole Home W"
            input "g1_whole_v", "bool", title:"Whole Home V"
            input "g1_whole_a", "bool", title:"Whole Home A"
            input "g1_clamp1_kwh", "bool", title:"Clamp 1 KWH"
            input "g1_clamp1_w", "bool", title:"Clamp 1 W"
            input "g1_clamp1_v", "bool", title:"Clamp 1 V"
            input "g1_clamp1_a", "bool", title:"Clamp 1 A"
            input "g1_clamp2_kwh", "bool", title:"Clamp 2 KWH"
            input "g1_clamp2_w", "bool", title:"Clamp 2 W"
            input "g1_clamp2_v", "bool", title:"Clamp 2 V"
            input "g1_clamp2_a", "bool", title:"Clamp 2 A"
            
            input type: "paragraph", element: "paragraph", title: "Configure Group 2 Reporting", description: "Use these settings to configure group 2 reporting."        	
			input "param112", "number", title: "P112 Interval for sending Group2 report", defaultValue: 720, range: "1..4294967295"
            input "g2_whole_kwh", "bool", title:"Whole Home KWH"
            input "g2_whole_w", "bool", title:"Whole Home W"
            input "g2_whole_v", "bool", title:"Whole Home V"
            input "g2_whole_a", "bool", title:"Whole Home A"
            input "g2_clamp1_kwh", "bool", title:"Clamp 1 KWH"
            input "g2_clamp1_w", "bool", title:"Clamp 1 W"
            input "g2_clamp1_v", "bool", title:"Clamp 1 V"
            input "g2_clamp1_a", "bool", title:"Clamp 1 A"
            input "g2_clamp2_kwh", "bool", title:"Clamp 2 KWH"
            input "g2_clamp2_w", "bool", title:"Clamp 2 W"
            input "g2_clamp2_v", "bool", title:"Clamp 2 V"
            input "g2_clamp2_a", "bool", title:"Clamp 2 A"
            
            input type: "paragraph", element: "paragraph", title: "Configure Group 3 Reporting", description: "Use these settings to configure group 3 reporting."        	
            input "param113", "number", title: "P113 Interval for sending Group3 report", defaultValue: 720, range: "1..104294967295"
            input "g3_whole_kwh", "bool", title:"Whole Home KWH"
            input "g3_whole_w", "bool", title:"Whole Home W"
            input "g3_whole_v", "bool", title:"Whole Home V"
            input "g3_whole_a", "bool", title:"Whole Home A"
            input "g3_clamp1_kwh", "bool", title:"Clamp 1 KWH"
            input "g3_clamp1_w", "bool", title:"Clamp 1 W"
            input "g3_clamp1_v", "bool", title:"Clamp 1 V"
            input "g3_clamp1_a", "bool", title:"Clamp 1 A"
            input "g3_clamp2_kwh", "bool", title:"Clamp 2 KWH"
            input "g3_clamp2_w", "bool", title:"Clamp 2 W"
            input "g3_clamp2_v", "bool", title:"Clamp 2 V"
            input "g3_clamp2_a", "bool", title:"Clamp 2 A"
            //input "param255", "number", title: "P255 Reset device to factory settings", options:["1":"Reset Groups","0":"Dont Reset"], defaultValue: "Dont Reset"
            
        }
         
    }
}

def installed() {
	TRACE("Installed Settings:\n${settings}")
	reset()						// The order here is important
	configure()					// Since reports can start coming in even before we finish configure()
	refresh()
}

def updated() {
	TRACE("Updated Settings:\n${settings}")
	configure()
	refresh()
}

def parse(String description) {
	TRACE("Parse received ${description}")
	def result = null
    
	def cmd = zwave.parse(description, getCommandClassVersions())
	if (cmd) {    	
		result = createEvent(zwaveEvent(cmd))
	}
	if (result) { 
		TRACE("Parse returned ${result?.descriptionText}")
		return result
	} else {
    	TRACE("No result in parse")
	}
}

def zwaveEvent(physicalgraph.zwave.commands.meterv1.MeterReport cmd) {
    def newValue
    def MAX_AMPS = 150
    def MAX_WATTS = 20000
    TRACE("MeterV1Report: ${cmd}")
    if (cmd.meterType == 33) {
		if (cmd.scale == 0) {
        	//newValue = Math.round(cmd.scaledMeterValue * 100) / 100
            newValue = cmd.scaledMeterValue as int
        	if (newValue != state.energyValue) {
        		state.energyValue = newValue
				[name: "energy", value: state.energyValue, unit: "kWh", descriptionText: "Total Energy: ${state.enegryValue} kWh"]
            }
		} 
		else if (cmd.scale == 1) {
            //newValue = Math.round( cmd.scaledMeterValue * 100) / 100
            newValue = cmd.scaledMeterValue as int
            if (newValue != state.energyValue) {
            	
                state.energyValue = newValue
				[name: "energy", value: newValue, unit: "kVAh", descriptionText: "Total Energy: ${newValue} kVAh"]
            }
		}
		else if (cmd.scale==2) {				
        	//newValue = Math.round(cmd.scaledMeterValue)		// really not worth the hassle to show decimals for Watts
            newValue = cmd.scaledMeterValue as int		// really not worth the hassle to show decimals for Watts

            if (newValue > MAX_WATTS) { return }				// Ignore ridiculous values (a 200Amp supply @ 120volts is roughly 24000 watts)
        	if (newValue != state.powerValue) {
				state.powerValue = newValue              
                [name: "power", value: newValue, unit: "W", descriptionText: "Total Power: ${newValue} Watts"]
                
            }
		}
 	}
    else if (cmd.meterType == 161) {
    	if (cmd.scale==1) {
        	//newValue = Math.round( cmd.scaledMeterValue * 100) / 100
            newValue = cmd.scaledMeterValue as int
            if ( newValue > MAX_AMPS) { return }								// Ignore silly values for 200Amp service
        	if (newValue != state.ampsValue) {
        		               
                state.ampsValue = newValue
				[name: "amps", value: state.ampsValue, unit: "A", descriptionText: "Total Current: ${state.ampsValue} Amps"]
            }
        }
    }           
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
	def dispValue
	def newValue
	def formattedValue
    def MAX_AMPS = 100
    def MAX_WATTS = 12000
	TRACE("multiChannelV3 ${cmd}")
   	if (cmd.commandClass == 50) {  
    	TRACE(" L1 and L2 Report")
   		def encapsulatedCommand = cmd.encapsulatedCommand([0x30: 1, 0x31: 1]) // can specify command class versions here like in zwave.parse
		if (encapsulatedCommand) {
			if (cmd.sourceEndPoint == 1) {
				if (encapsulatedCommand.scale == 2 ) {
					newValue = Math.round(encapsulatedCommand.scaledMeterValue)
                    
                    newValue =2*newValue
                    if (newValue > MAX_WATTS) { return }
					if (newValue != state.powerL1) {
                    	
						state.powerL1 = newValue						
						[name: "powerClamp1", value: state.powerL1, unit: "W", descriptionText: "Clamp1 Power: ${state.powerL1} Watts"]                        
					}
				} 
				else if (encapsulatedCommand.scale == 0 ){
					newValue = Math.round(2*encapsulatedCommand.scaledMeterValue * 100) / 100	
					if (newValue != state.energyL1) {
						state.energyL1 = newValue
						[name: "energyClamp1", value: state.energyL1, unit: "kWh", descriptionText: "Clamp1 Energy: ${state.energyL1} kWh"]						
					}
				}
				else if (encapsulatedCommand.scale == 1 ){
					newValue = Math.round(2*encapsulatedCommand.scaledMeterValue * 100) / 100
					
					if (newValue != state.energyL1) {
						state.energyL1 = newValue
						[name: "energyClamp1", value: state.energyL1, unit: "kVAh", descriptionText: "Clamp1 Energy: ${state.energyL1} kVAh"]
					}
				}
				else if (encapsulatedCommand.scale == 5 ) {
					newValue = Math.round(encapsulatedCommand.scaledMeterValue * 100) / 100
                    if (newValue > MAX_AMPS) { return }
					if (newValue != state.ampsL1) {
						state.ampsL1 = newValue
						[name: "ampsClamp1", value: state.ampsL1, unit: "", descriptionText: "Clamp1 Current: ${state.ampsL1} Amps"]
					}
               	}               	
			} 
			else if (cmd.sourceEndPoint == 2) {
				if (encapsulatedCommand.scale == 2 ){
					newValue = Math.round(2*encapsulatedCommand.scaledMeterValue)
                    if (newValue > MAX_WATTS ) { return }
					if (newValue != state.powerL2) {
                    	TRACE("New PL2 : ${newValue}")
						state.powerL2 = newValue
                        
						[name: "powerClamp2", value: state.powerL2, unit: "W", descriptionText: "Clamp2 Power: ${state.powerL2} Watts"]
					} else { TRACE("SAME Powerl2") }
				} 
				else if (encapsulatedCommand.scale == 0 ){
					newValue = Math.round(2*encapsulatedCommand.scaledMeterValue * 100) / 100
					if (newValue != state.energyL2) {
						state.energyL2 = newValue
						[name: "energyClamp2", value: state.energyL2, unit: "kWh", descriptionText: "Clamp2 Energy: ${state.energyL2} kWh"]
					}
				} 
				else if (encapsulatedCommand.scale == 1 ){
					newValue = Math.round(2*encapsulatedCommand.scaledMeterValue * 100) / 100
					if (newValue != state.energyL2) {
						state.energyL2 = newValue
						[name: "energyClamp2", value: state.energyL2, unit: "kVAh", descriptionText: "Clamp2 Energy: ${state.energyL2} kVAh"]
						
					}
				}				
				else if (encapsulatedCommand.scale == 5 ){
               		newValue = Math.round(encapsulatedCommand.scaledMeterValue * 100) / 100
                    if (newValue > MAX_AMPS) { return } 
				
					if (newValue != state.ampsL2) {
						state.ampsL2 = newValue
							[name: "ampsClamp2", value: state.ampsL2, unit: "A", descriptionText: "Clamp2 Current: ${state.ampsL2} Amps"]
					}
				}              	
			}
		}
	}
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [:]
	map.name = "battery"
	map.unit = "%"
	
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} battery is low"
		map.isStateChange = true
	} 
	else {
		map.value = cmd.batteryLevel
	}
	TRACE( map)
	return map
}
def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
	TRACE("Meterv3Report ${cmd}")
}
def zwaveEvent(physicalgraph.zwave.commands.meterv2.MeterReport cmd) {
	TRACE("Meterv2Report ${cmd}")
}
//physicalgraph.zwave.commands.meterv2.MeterSupportedReport
def zwaveEvent(physicalgraph.zwave.commands.meterv2.MeterSupportedReport cmd) {
	TRACE("Meterv2SupportedReport ${cmd}")
}
def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd) {
	TRACE("SensorMultilevelv5Report ${cmd}")
}
def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in
    TRACE("Unhandled event ${cmd}")
	[:]
}

def refresh() {			// Request HEMv2 to send us the latest values for the 4 we are tracking
	TRACE("refresh()")
    
	delayBetween([
		zwave.meterV2.meterGet(scale: 0).format(),		// Change 0 to 1 if international version
		zwave.meterV2.meterGet(scale: 2).format(),
		zwave.meterV2.meterGet(scale: 4).format(),
		zwave.meterV2.meterGet(scale: 5).format()
	], 1000)
    resetDisplay()
}

def poll() {
	TRACE("poll()")
	refresh()
}

def resetDisplay() {
	TRACE("resetDisplay()")
	
    sendEvent(name: "ampsClamp1", value: state.ampsL1, unit: "A")    
    sendEvent(name: "powerClamp1", value: state.powerL1, unit: "W")     
    sendEvent(name: "energyClamp1", value: state.energyL1, unit: "kWh")

    sendEvent(name: "ampsClamp2", value: state.ampsL2, unit: "A")
    sendEvent(name: "powerClamp2", value: state.powerL2, unit: "W")
    sendEvent(name: "energyClamp2", value: state.energyL2, unit: "kWh")   


}

def reset() {
	TRACE("reset()")

    state.energyL1 = ""
    state.energyL2 = ""
    state.powerL1 = ""
    state.powerL2 = ""
    state.ampsL1 = ""
    state.ampsL2 = ""
	
    resetDisplay()  
    

// No V1 available
	def cmd = delayBetween( [
		zwave.meterV2.meterReset().format(),			// Reset all values
		zwave.meterV2.meterGet(scale: 0).format(),		// Request the values we are interested in (0-->1 for kVAh)
		zwave.meterV2.meterGet(scale: 2).format(),
		zwave.meterV2.meterGet(scale: 4).format(),
		zwave.meterV2.meterGet(scale: 5).format()
	], 1000)
    cmd
    
    configure()
}

def resetmyMeter() {
	// No V1 available
	return [
		zwave.meterV2.meterReset().format(),
		zwave.meterV2.meterGet(scale: 0).format()
	]
}

//0x70,0x31,0x72,0x86,0x32,0x80,0x85,0x60
private getCommandClassVersions() {
	[
		0x20: 1,  // Basic
        0x31: 5,  // SensorMultilevel
        0x32: 2,  // METER
//		0x59: 1,  // AssociationGrpInfo
//		0x5A: 1,  // DeviceResetLocally
//		0x5E: 2,  // ZwaveplusInfo
		0x60: 3 ,  // MULTI_CHANNEL
		0x70: 1,  // Configuration
//		0x71: 3,  // Alarm v1 or Notification v4
		0x72: 2,  // ManufacturerSpecific*=
		0x80: 1,  // Battery
		0x84: 2,  // WakeUp
		0x85: 2,  // Association
		0x86: 1,  // Version (2)
	]
}

def configure() {
	TRACE( "configure()")
    
	Long kDelay = settings.kWhDelay as Long
    Long dDelay = settings.detailDelay as Long
    def param1 = settings.param1 ? settings.param1 : 120
    def param3 = settings.param3 ? settings.param3 : 0
    def param4 = settings.param4 ? settings.param4 : 50
    def param5 = settings.param5 ? settings.param5 : 50
    def param6 = settings.param6 ? settings.param6 : 50
    def param7 = settings.param7 ? settings.param7 : 50
    def param8 = settings.param8 ? settings.param8 : 10
    def param9 = settings.param9 ? settings.param9 : 10
    def param10 = settings.param10 ? settings.param10 : 10
    def param11 = settings.param11 ? settings.param11 : 10
    def param12 = settings.param12 ? settings.param12 : 0
    def param100 = settings.param100 ? settings.param100 : 0
    def param101 = (settings.g1_whole_kwh ? 1 : 0 ) +	(settings.g1_whole_w ? 2 : 0 ) +	(settings.g1_whole_v ? 4 : 0 ) +	
    	(settings.g1_whole_a ? 8 : 0 ) +	(settings.g1_clamp1_kwh ? 2048 : 0 ) +	(settings.g1_clamp1_w ? 256 : 0 ) +	
        (settings.g1_clamp1_v ? 65536 : 0 ) +	(settings.g1_clamp1_a ? 524288 : 0 ) + (settings.g1_clamp2_kwh ? 4096 : 0 ) +
        (settings.g1_clamp2_w ? 512 : 0 ) +	(settings.g1_clamp2_v ? 131072 : 0 ) +	(settings.g1_clamp2_a ? 1048576 : 0 )
    
    def param102 = (settings.g2_whole_kwh ? 1 : 0 ) +	(settings.g2_whole_w ? 2 : 0 ) +	(settings.g2_whole_v ? 4 : 0 ) +
    	(settings.g2_whole_a ? 8 : 0 ) + (settings.g2_clamp1_kwh ? 2048 : 0 ) +	(settings.g2_clamp1_w ? 256 : 0 ) +
        (settings.g2_clamp1_v ? 65536 : 0 ) +	(settings.g2_clamp1_a ? 524288 : 0 ) +	(settings.g2_clamp2_kwh ? 4096 : 0 ) +	
        (settings.g2_clamp2_w ? 512 : 0 ) +	(settings.g2_clamp2_v ? 131072 : 0 ) +	(settings.g2_clamp2_a ? 1048576 : 0 ) 
    
    def param103 =(settings.g3_whole_kwh ? 1 : 0 ) +	(settings.g3_whole_w ? 2 : 0 ) +	(settings.g3_whole_v ? 4 : 0 ) + 
    	(settings.g3_whole_a ? 8 : 0 ) +	(settings.g3_clamp1_kwh ? 2048 : 0 ) +	(settings.g3_clamp1_w ? 256 : 0 ) +	
        (settings.g3_clamp1_v ? 65536 : 0 ) +	(settings.g3_clamp1_a ? 524288 : 0 ) +	(settings.g3_clamp2_kwh ? 4096 : 0 ) +	
        (settings.g3_clamp2_w ? 512 : 0 ) +	(settings.g3_clamp2_v ? 131072 : 0 ) +	(settings.g3_clamp2_a ? 1048576 : 0 ) 
    def param110 = settings.param110 ? settings.param110 : 0
    def param111 = settings.param111 ? settings.param111 : 720
    def param112 = settings.param112 ? settings.param112 : 720
    def param113 = settings.param113 ? settings.param113 : 720 

  /*  TRACE("param1 : ${param1}" +
        "\nparam3 : ${param3}"+
        "\nparam4 : ${param4}"+
        "\nparam5 : ${param5}"+
        "\nparam6 : ${param6}"+
        "\nparam7 : ${param7}"+
        "\nparam8 : ${param8}"+
        "\nparam9 : ${param9}"+
        "\nparam10 : ${param10}"+
        "\nparam11 : ${param11}"+
        "\nparam12 : ${param12}"+
        "\nparam100 : ${param100}"+
        "\nparam101 : ${param101}"+
        "\nparam102 : ${param102}"+
        "\nparam103 : ${param103}"+
        "\nparam110 : ${param110}"+
        "\nparam111 : ${param111}"+
        "\nparam112 : ${param112}"+
        "\nparam113 : ${param113}"
    )*/
    
    if (kDelay == null) {		// Shouldn't have to do this, but there seem to be initialization errors
		kDelay = 15
	}

	if (dDelay == null) {
		dDelay = 15
	}
    	def cmd = delayBetween([
    // https://s3.amazonaws.com/cdn.freshdesk.com/data/helpdesk/attachments/production/6009584509/
    // original/26%20Home%20Energy%20Meter%20G1.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=
    // AKIAJ2JSYZ7O3I4JO6DA%2F20170829%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20170829T024638Z&X-Amz-Expires=
    // 300&X-Amz-Signature=29557a82d9ffd903a2f69a33e4e29f4618c6915665348c5ca523f810f0b73e52&X-Amz-SignedHeaders=Host&response-content-type=application%2Fpdf
    	//P1: Voltage
        zwave.configurationV1.configurationSet(parameterNumber: 1, size: 1, scaledConfigurationValue: param1).format(),
    	//parameterNumber: 3 = Selective reporting -> 0 disable, report on schedule 1- report on change according to params 4 - 11
		zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: param3).format(),			// 1 Enabled/ 0 disable selective reporting
		//param 4 = Threshold change in Watts for WHOLE HEM: default 50, range is 0 - 60000
		zwave.configurationV1.configurationSet(parameterNumber: 4, size: 2, scaledConfigurationValue: param4).format(),			// Don't send whole HEM unless watts have changed by 30
		zwave.configurationV1.configurationSet(parameterNumber: 5, size: 2, scaledConfigurationValue: param5).format(),			// Don't send L1 Data unless watts have changed by 15
		zwave.configurationV1.configurationSet(parameterNumber: 6, size: 2, scaledConfigurationValue: param6).format(),			// Don't send L2 Data unless watts have changed by 15
        zwave.configurationV1.configurationSet(parameterNumber: 7, size: 2, scaledConfigurationValue: param7).format(),			// Don't send L3 Data unless watts have changed by 15
        zwave.configurationV1.configurationSet(parameterNumber: 8, size: 1, scaledConfigurationValue: param8).format(),			// Or by 5% (whole HEM)
		zwave.configurationV1.configurationSet(parameterNumber: 9, size: 1, scaledConfigurationValue: param9).format(),			// Or by 5% (L1)
        zwave.configurationV1.configurationSet(parameterNumber: 10, size: 1, scaledConfigurationValue: param10).format(),			// Or by 5% (L2)
        zwave.configurationV1.configurationSet(parameterNumber: 11, size: 1, scaledConfigurationValue: param11).format(),			// Or by 5% (L3)
        zwave.configurationV1.configurationSet(parameterNumber: 12, size: 1, scaledConfigurationValue: param12).format(),			// Accumulate kwh on battery
		zwave.configurationV1.configurationSet(parameterNumber: 100, size: 4, scaledConfigurationValue: param100).format(),		// 1 reset 101-103
		zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: param101).format(),   	// Whole HEM and L1/L2 power in kWh
		zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: param111).format(), 	// Default every 120 Seconds
		zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: param102).format(),  // L1/L2 for Amps & Watts, Whole HEM for Amps, Watts, & Volts
		zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: param112).format(), 	// Defaul every 30 seconds
		zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: param103).format(),		// Power (Watts) L1, L2, Total
		zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: param113).format() 		// every 6 seconds
	], 1000)
/*	def cmd = delayBetween([
    // https://s3.amazonaws.com/cdn.freshdesk.com/data/helpdesk/attachments/production/6009584509/
    // original/26%20Home%20Energy%20Meter%20G1.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=
    // AKIAJ2JSYZ7O3I4JO6DA%2F20170829%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20170829T024638Z&X-Amz-Expires=
    // 300&X-Amz-Signature=29557a82d9ffd903a2f69a33e4e29f4618c6915665348c5ca523f810f0b73e52&X-Amz-SignedHeaders=Host&response-content-type=application%2Fpdf
    	//parameterNumber: 3 = Selective reporting -> 0 disable, report on schedule 1- report on change according to params 4 - 11
		zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: 1).format(),			// 1 Enabled/ 0 disable selective reporting
		//param 4 = Threshold change in Watts for WHOLE HEM: default 50, range is 0 - 60000
		zwave.configurationV1.configurationSet(parameterNumber: 4, size: 2, scaledConfigurationValue: 10).format(),			// Don't send whole HEM unless watts have changed by 30
//		zwave.configurationV1.configurationSet(parameterNumber: 5, size: 2, scaledConfigurationValue: 5).format(),			// Don't send L1 Data unless watts have changed by 15
//		zwave.configurationV1.configurationSet(parameterNumber: 6, size: 2, scaledConfigurationValue: 5).format(),			// Don't send L2 Data unless watts have changed by 15
//      zwave.configurationV1.configurationSet(parameterNumber: 8, size: 1, scaledConfigurationValue: 1).format(),			// Or by 5% (whole HEM)
//		zwave.configurationV1.configurationSet(parameterNumber: 9, size: 1, scaledConfigurationValue: 1).format(),			// Or by 5% (L1)
//      zwave.configurationV1.configurationSet(parameterNumber: 10, size: 1, scaledConfigurationValue: 1).format(),			// Or by 5% (L2)
//		zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 6145).format(),   	// Whole HEM and L1/L2 power in kWh
//		zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: kDelay).format(), 	// Default every 120 Seconds
//		zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 1573646).format(),  // L1/L2 for Amps & Watts, Whole HEM for Amps, Watts, & Volts
//		zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: dDelay).format(), 	// Defaul every 30 seconds

//		zwave.configurationV1.configurationSet(parameterNumber: 100, size: 1, scaledConfigurationValue: 0).format(),		// reset to defaults
		zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 6149).format(),   	// All L1/L2 kWh, total Volts & kWh
		zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: kDelay).format(), 		// Every 6 seconds
		zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 1572872).format(),	// Amps L1, L2, Total
		zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: 60).format(), 		// every 30 seconds
		zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 770).format(),		// Power (Watts) L1, L2, Total
		zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: dDelay).format() 		// every 6 seconds
	], 2000)
*/
	//TRACE( cmd)

	cmd
}
def TRACE(msg) {
	log.debug msg
}
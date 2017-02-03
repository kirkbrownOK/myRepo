/**
 *  Arduino LAN Device Type
 *
 * 
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
    definition (name: "Arduino LAN", namespace: "kirkbrownOK", author: "Kirk Brown") {
        capability "Contact Sensor"
        capability "Polling"
        capability "Refresh"
        capability "Temperature Measurement"
        capability "Switch Level"
		capability "Actuator"
		capability "Indicator"
        capability "Temperature Measurement"
		capability "Switch"
        capability "Motion Sensor"
        capability "Button"
        
        attribute "switchC", "string"
        attribute "switchW", "string"
        attribute "switchA", "string"
        attribute "switchOrvibo1", "string"
        attribute "switchFan", "string"
        attribute "switchWemo1", "string"
        //attribute "level", "number"
        attribute "kirksCar", "number"
        attribute "riatasCar", "number"
        attribute "walkBy", "number"
        attribute "contact1", "string"
        attribute "contact2", "string"
        attribute "contact3", "string"
        attribute "contact4", "string"
        attribute "contact5", "string"
        attribute "contact6", "string"
        attribute "contact7", "string"
        attribute "contact8", "string"
        
        
        command "onC"
        command "offC"
        command "onA"
        command "offA"
        command "onW"
        command "offW"
        command "orviboOn1"
        command "orviboOff1"
        command "refreshOrvibo1"
        command "onFan"
        command "offFan"
        command "subscribe"
        command "toggleSwitch"
        command "wemoOn1"
        command "wemoOff1"
        command "refreshWemo1"
    }

    simulator {
    }

    tiles {
    	standardTile("contact", "device.contact", width: 1, height: 1) {
			state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#79b821", action: "refresh")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#ffa81e", action: "refresh")
		}
        standardTile("contact1", "device.contact1", width: 1, height: 1, inactiveLabel: false) {
            state "open", label: '1: ${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '1: ${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }
        standardTile("contact2", "device.contact2", width: 1, height: 1, inactiveLabel: false) {
            state "open", label: '2: ${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '2: ${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }
        standardTile("contact3", "device.contact3", width: 1, height: 1, inactiveLabel: false) {
            state "open", label: '3: ${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '3: ${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }
        standardTile("contact4", "device.contact4", width: 1, height: 1, inactiveLabel: false) {
            state "open", label: '4: ${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '4: ${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }
        standardTile("contact5", "device.contact5", width: 1, height: 1, inactiveLabel: false) {
            state "open", label: '5: ${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '5: ${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }
        standardTile("contact6", "device.contact6", width: 1, height: 1, inactiveLabel: false) {
            state "open", label: '6: ${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '6: ${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }
        standardTile("contact7", "device.contact7", width: 1, height: 1, inactiveLabel: false) {
            state "open", label: '7: ${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '7: ${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }
        standardTile("contact8", "device.contact8", width: 1, height: 1, inactiveLabel: false) {
            state "open", label: '8: ${name}', icon: "st.contact.contact.open", backgroundColor: "#ffa81e"
            state "closed", label: '8: ${name}', icon: "st.contact.contact.closed", backgroundColor: "#79b821"
        }
        valueTile("temperature", "device.temperature") {
            state "temperature", label:'${currentValue}°', unit:"F",
            backgroundColors:[
                    [value: 12, color: "#153591"],
                    [value: 25, color: "#1e9cbb"],
                    [value: 37, color: "#90d2a7"],
                    [value: 50, color: "#44b621"],
                    [value: 62, color: "#f1d801"],
                    [value: 75, color: "#d04e00"],
                    [value: 87, color: "#bc2323"]
                ]
        }
        standardTile("switch", "device.switch", width: 1, height: 1) {
			state("0", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#79b821", action: "refresh")
			state("1", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("2", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("3", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("4", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            state("5", label:'${currentValue}', icon:"st.Electronics.electronics5", backgroundColor:"#ffa81e", action: "refresh")
            
		}
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("switchFan", "device.switchFan", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "off", label: '${name}', action: "onFan", icon: "st.Lighting.light24", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "offFan", icon: "st.Lighting.light24", backgroundColor: "#79b821"
		}
        standardTile("switchC", "device.switchC", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "off", label: 'G: ${name}', action: "onC", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
			state "on", label: 'G: ${name}', action: "offC", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchCon", "device.switchC", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Garage ON', action: "onC", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchCoff", "device.switchC", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Garage OFF', action: "offC", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
		}
        standardTile("switchW", "device.switchW", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "off", label: 'W: ${name}', action: "onW", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
			state "on", label: 'W: ${name}', action: "offW", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchWon", "device.switchW", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'W ON', action: "onW", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchWoff", "device.switchW", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'W OFF', action: "offW", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
		}
        standardTile("switchA", "device.switchA", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "off", label: 'Fan: ${name}', action: "onA", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
			state "on", label: 'Fan: ${name}', action: "offA", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchAon", "device.switchA", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Fan ON', action: "onA", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchAoff", "device.switchA", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Fan OFF', action: "offA", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
		}
        standardTile("switchWemo1", "device.switchWemo1", width: 1, height: 1) {
        	//state "default", label: 'Wemo1: ${value}', action: "refreshWemo1", backgroundColor: "#79b821"
			state "off", label: 'Wemo1: ${name}', action: "refreshWemo1", icon:"st.motion.motion", backgroundColor: "#ffffff"
			state "on", label: 'Wemo1: ${name}', action: "refreshWemo1", icon:"st.motion.motion", backgroundColor: "#79b821"
            state "refresh", label: 'Wemo1: ${name}', action: "refreshWemo1", icon:"st.motion.motion", backgroundColor: "#ffff00"
            state "error", label: 'Wemo1: ${name}', action: "refreshWemo1", icon:"st.motion.motion", backgroundColor: "#79b821"
            
		}
        standardTile("switchWemoOn1", "device.switchWemo1", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Wemo1 ON', action: "wemoOn1", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchWemoOff1", "device.switchWemo1", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Wemo1 OFF', action: "wemoOff1", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
		}
        standardTile("switchOrvibo1", "device.switchOrvibo1", width: 1, height: 1) {
        	//state "default", label: 'Orvibo1: ${value}', action: "refreshOrvibo1", backgroundColor: "#79b821"
			state "off", label: 'Orvibo1: ${name}', action: "refreshOrvibo1", icon:"st.motion.motion", backgroundColor: "#ffffff"
			state "on", label: 'Orvibo1: ${name}', action: "refreshOrvibo1", icon:"st.motion.motion", backgroundColor: "#79b821"
            state "refresh", label: 'Orvibo1: ${name}', action: "refreshOrvibo1", icon:"st.motion.motion", backgroundColor: "#ffff00"
            state "error", label: 'Orvibo1: ${name}', action: "refreshOrvibo1", icon:"st.motion.motion", backgroundColor: "#79b821"
            
		}
        standardTile("switchOrviboOn1", "device.switchOrvibo1", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Orvibo1 ON', action: "orviboOn1", icon: "st.Appliances.appliances17", backgroundColor: "#79b821"
		}
        standardTile("switchOrviboOff1", "device.switchOrvibo1", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "default", label: 'Orvibo1 OFF', action: "orviboOff1", icon: "st.Appliances.appliances17", backgroundColor: "#ffffff"
		}        
		controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
			state "level", label: '${name}', action:"switch level.setLevel"
		}
        standardTile("motion", "device.motion", width: 1, height: 1) {
			state("active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0")
			state("inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff")
        }
        main "switchFan"
        details (["switch", "temperature","switchFan","switchC","switchCon","switchCoff", 
        	"switchA","switchAon","switchAoff","switchW","switchWon","switchWoff",
            "switchWemo1","switchWemoOn1","switchWemoOff1",
            "switchOrvibo1","switchOrviboOn1","switchOrviboOff1","motion","contact","contact1","contact2","contact3",
            "contact4","contact5","contact6","contact7","contact8","refresh"])
    }
}

// parse events into attributes
def parse(String description) {
	
    def usn = getDataValue('ssdpUSN')
    TRACE( "Parsing Arduino DT ${device.name} ${device.deviceNetworkId} ")

    def parsedEvent = parseDiscoveryMessage(description)
	def events = []
    def ev = []
    if (parsedEvent['body'] != null) {
    	try{
            def xmlText = new String(parsedEvent.body.decodeBase64())
            def xmlTop = new XmlSlurper().parseText(xmlText)
            def cmd = xmlTop.cmd[0] //
            def val = xmlTop.values[0] //
            def act = xmlTop.actuator[0]
            def switchVal = xmlTop.switch[0]
            def livingRoomFan = xmlTop.lrf[0] //
            def chc = xmlTop.chc[0] //
            def cha = xmlTop.cha[0] //
            def chw = xmlTop.chw[0]
            def we1 = xmlTop.we1[0] //
            def or1 = xmlTop.or1[0]
            def mn1 = xmlTop.mn1[0] 
            def ct1 = xmlTop.ct1[0]
            def ct2 = xmlTop.ct2[0]
            def ct3 = xmlTop.ct3[0]
            def ct4 = xmlTop.ct4[0]
            def ct5 = xmlTop.ct5[0]
            def ct6 = xmlTop.ct6[0]
            def ct7 = xmlTop.ct7[0]
            def ct8 = xmlTop.ct8[0]
            def rc = xmlTop.remoteCode[0]
            def lrl = xmlTop.lrl[0]

            def targetUsn = xmlTop.usn[0].toString()

            TRACE( "Processing xmlText ${xmlText} for ${targetUsn}")

            parent.getChildDevices().each { child ->
                def childUsn = getDataValue("ssdpUSN").toString()
                //TRACE("A:${childUsn}")
                //TRACE("B:${targetUsn}")
                if (childUsn == targetUsn) {
                    //TRACE( "childUSN ${childUsn} equal to Target USN ${targetUsn}")
                    TRACE( "Found child")
                    try {

                        if (cmd == 'poll') {
                            //log.info "Instructing 10:49 child ${child.device.label} to poll"
                            child.poll()
                        } else if (cmd == 'status-open') {
                            def value = 'open'
                            TRACE( "Updating ${child.device.label} to ${value}")
                            //child.sendEvent(name: 'contact', value: value)
                        } else if (cmd == 'status-closed') {
                            def value = 'closed'
                            TRACE( "Updating ${child.device.label} to ${value}")
                            //child.sendEvent(name: 'contact', value: value)
                        }
                    } catch (e) {
                        //TRACE( "No Cmd")
                    }
                    try{
                        if(val.toFloat() > 0 ) {
                            TRACE( "Updating ${child.device.label} to ${val}")
                            if (currentTemperature != val) {
                            	sendEvent(name: 'temperature', value: val)
                            }
                        }
                    } catch(e) {
                        TRACE( "No values msg")
                    }
                    try {
                        if (rc.toFloat() >= 0) {
                            TRACE( "received Switch Signal: ${rc.toFloat()}")
                            if( currentSwitch != rc) sendEvent(name: 'switch', value: rc)
                        }
                    } catch (e) {
                        TRACE( "No Switch msg")
                    }
                    try {        
        
                        if (ct1.toFloat() == 0) {
                            TRACE( "received Contact1 Signal: ${ct1.toFloat()}")
                            if (currentContact1 != 'closed' ) {
                            	ev = [name:   "contact1",value:  'closed' ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor1:close'] 
                            	events << createEvent(ev)
                            }
                        } else if (ct1.toFloat() ==1) {
                        	TRACE( "received Contact Signal: ${ct1.toFloat()}")
                            if(currentContact1 != 'open') { 
                            	//child.sendEvent([name: 'contact1', value: 'open', name: 'contact', value: 'Sensor1:open'])
                        		ev = [name:   "contact1", value:  'open' ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor1:open'] 
                            	events << createEvent(ev)
                            
                            }
                        }
                    } catch (e) {
                        TRACE( "No contact1 msg")
                    }
                    try {
                        if (ct2.toFloat() == 0) {
                            TRACE( "received Contact2 Signal: ${ct2.toFloat()}")
                            if(currenContact2 != 'closed') {
                            	ev = [
                                    name:   "contact2",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor2:close'] 
                            	events << createEvent(ev)        
                            }//child.sendEvent([name: 'contact2', value: 'closed', name:'contact',value:'Sensor2:close'])
                            
                        } else if (ct2.toFloat() ==1) {
                        	TRACE( "received Contact2 Signal: ${ct2.toFloat()}")
                            if( currentContact2 != 'open') {
                            	ev = [
                                    name:   "contact2",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor2:open'] 
                            	events << createEvent(ev)        
                            } //child.sendEvent([name: 'contact2', value: 'open', name:'contact',value:'Sensor2:open'])
                        }
                    } catch (e) {
                        TRACE( "No contact2 msg")
                    }
                    try {
                        if (ct3.toFloat() == 0) {
                            TRACE( "received Contact3 Signal: ${ct3.toFloat()}")
                            if (currentContact3 != 'closed') {
                            	ev = [
                                    name:   "contact3",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor3:close'] 
                            	events << createEvent(ev)        
                            }//child.sendEvent([name: 'contact3', value: 'closed', name:'contact',value:'Sensor3:close'])
                        } else if (ct3.toFloat() ==1) {
                        	TRACE( "received Contact3 Signal: ${ct3.toFloat()}")
                            if (currentContact3 != 'open') {
                            	ev = [
                                    name:   "contact3",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor3:open'] 
                            	events << createEvent(ev)        
                            }//child.sendEvent([name: 'contact3', value: 'open', name:'contact',value:'Sensor3:open'])
                        }
                    } catch (e) {
                        TRACE( "No contact3 msg")
                    }
                    
                    try {
                        if (ct4.toFloat() == 0) {
                            TRACE( "received Contact4 Signal: ${ct4.toFloat()}")
                            
                            if(currentContact4 != 'closed') {
                            	ev = [
                                    name:   "contact4",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor4:close'] 
                            	events << createEvent(ev)                           }
                        } else if (ct4.toFloat() ==1) {
                        	TRACE( "received Contact4 Signal: ${ct4.toFloat()}")
                            if(currentContact4 != 'open') {
                            	ev = [
                                    name:   "contact4",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor4:open'] 
                            	events << createEvent(ev)
                            }
                        }
                    } catch (e) {
                        TRACE( "No contact4 msg")
                    }                    
                    try {
                        if (ct5.toFloat() == 0) {
                            TRACE( "received Contact5 Signal: ${ct5.toFloat()}")
                            if(currentContact5 != 'closed'){
                            	ev = [
                                    name:   "contact5",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor5:close'] 
                            	events << createEvent(ev)                               	}
                        } else if (ct5.toFloat() ==1) {
                        	TRACE( "received Contact5 Signal: ${ct5.toFloat()}")
                            if(currentContact5 != 'open'){
								ev = [
                                    name:   "contact5",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor5:open'] 
                            	events << createEvent(ev)        
							}
                        }
                    } catch (e) {
                        TRACE( "No contact5 msg")
                    }
                    try {
                        if (ct6.toFloat() == 0) {
                            TRACE( "received Contact6 Signal: ${ct6.toFloat()}")
                            if(currentContact6 != 'closed'){
                            	ev = [
                                    name:   "contact6",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor6:close'] 
                            	events << createEvent(ev)                               	}
                        } else if (ct6.toFloat() ==1) {
                        	TRACE( "received Contact6 Signal: ${ct6.toFloat()}")
                            if(currentContact6 != 'open'){
								ev = [
                                    name:   "contact6",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor6:open'] 
                            	events << createEvent(ev)        
							}
                        }
                    } catch (e) {
                        TRACE( "No contact6 msg")
                    }
                    try {
                        if (ct7.toFloat() == 0) {
                            TRACE( "received Contact7 Signal: ${ct7.toFloat()}")
                            if(currentContact7 != 'closed'){
                            	ev = [
                                    name:   "contact7",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor7:close'] 
                            	events << createEvent(ev)                               	}
                        } else if (ct7.toFloat() ==1) {
                        	TRACE( "received Contact7 Signal: ${ct7.toFloat()}")
                            if(currentContact7 != 'open'){
								ev = [
                                    name:   "contact7",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor7:open'] 
                            	events << createEvent(ev)        
							}
                        }
                    } catch (e) {
                        TRACE( "No contact7 msg")
                    }
                    try {
                        if (ct8.toFloat() == 0) {
                            TRACE( "received Contact8 Signal: ${ct8.toFloat()}")
                            if(currentContact8 != 'closed'){
                            	ev = [
                                    name:   "contact8",
                                    value:  'closed',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor8:close'] 
                            	events << createEvent(ev)                               	}
                        } else if (ct8.toFloat() ==1) {
                        	TRACE( "received Contact8 Signal: ${ct8.toFloat()}")
                            if(currentContact8 != 'open'){
								ev = [
                                    name:   "contact8",
                                    value:  'open',                                    
                                ]
                                events << createEvent(ev)
                                ev = [name: 'contact', value: 'Sensor8:open'] 
                            	events << createEvent(ev)        
							}
                        }
                    } catch (e) {
                        TRACE( "No contact8 msg")
                    }
                    try {
                        if (mn.toFloat() == 0) {
                            TRACE( "received Motion Signal: ${mn.toFloat()}")
                            if (currentMotion != 'inactive') child.sendEvent(name: 'motion', value: 'inactive')
                        } else if (mn.toFloat() == 1) {
                            TRACE( "received Motion Signal: ${mn.toFloat()}")
                            if (currentMotion != 'active') child.sendEvent(name: 'motion', value: 'active')
                        }
                    } catch (e) {
                        TRACE( "No motion in msg")
                    }
                    try {
                        if (mn1.toFloat() == 0) {
                            TRACE( "received motion1 Signal: ${mn1.toFloat()}")
                            if (currentMotion != 'inactive') child.sendEvent(name: 'motion', value: 'inactive')
                        } else if (mn1.toFloat() == 1) {
                            TRACE( "received motion1 Signal: ${mn1.toFloat()}")
                            if (currentMotion != 'active')child.sendEvent(name: 'motion', value: 'active')
                        }
                    } catch (e) {
                        TRACE( "No motion1 in msg")
                    }
                    try {
                        if (lrl.toFloat() == 0) {
                            TRACE( "received lrl Signal: ${lrl.toFloat()}")
                            if( currentSwitchFan != 'off') child.sendEvent(name: 'switchFan', value: 'off')
                        } else if (lrl.toFloat() == 1) {
                            TRACE( "received lrl Signal: ${lrl.toFloat()}")
                            if( currentSwitchFan != 'on') child.sendEvent(name: 'switchFan', value: 'on')
                        }
                    } catch (e) {
                        TRACE( "No lrl in msg")
                    }
                    try {
                        if (chc.toFloat() == 0) {
                            TRACE( "received chc Signal: ${chc.toFloat()}")
                            if (currentSwitchC != 'off' )child.sendEvent(name: 'switchC', value: 'off')
                        } else if (chc.toFloat() == 1) {
                            TRACE( "received chc Signal: ${chc.toFloat()}")
                            if (currentSwitchC != 'on') child.sendEvent(name: 'switchC', value: 'on')
                        }
                    } catch (e) {
                        TRACE( "No chc in msg")
                    }
                    try {
                    	//log.trace "TRYING CHW" 
                        if (chw.toFloat() == 0) {
                            TRACE("received chw Signal: ${chw.toFloat()}")
                            sendEvent(name: 'switchW', value: 'off')
                        } else if (chw.toFloat() == 1) {
                            TRACE( "received chw Signal: ${chw.toFloat()}")
                            sendEvent(name: 'switchW', value: 'on')
                        }
                    } catch (e) {
                        log.trace "No chw in msg"
                    }
                    try {
                        if (cha.toFloat() == 0) {
                            TRACE( "received cha Signal: ${cha.toFloat()}")
                            sendEvent(name: 'switchA', value: 'off')
                        } else if (cha.toFloat() == 1) {
                            TRACE( "received cha Signal: ${cha.toFloat()}")
                            sendEvent(name: 'switchA', value: 'on')
                        }
                    } catch (e) {
                        TRACE( "No cha in msg")
                    }
                    try {
                        if (we1.toFloat() == 0) {
                            TRACE( "received we1 Signal: ${we1.toFloat()}")
                            sendEvent(name: 'switchWemo1', value: 'off')
                        } else if (we1.toFloat() == 1) {
                            TRACE( "received we1 Signal: ${we1.toFloat()}")
                            sendEvent(name: 'switchWemo1', value: 'on')
                        } else  {
                        	TRACE( "received we1 error: ${we1.toFloat()}")
                            sendEvent(name: 'switchWemo1', value: 'error')
                        }
                    } catch (e) {
                        TRACE( "No we1 in msg")
                    }
                    try {
                        if (or1.toFloat() == 0) {
                            TRACE( "received or1 Signal: ${or1.toFloat()}")
                            sendEvent(name: 'switchOrvibo1', value: 'off')
                        } else if (or1.toFloat() == 1) {
                            TRACE( "received or1 Signal: ${or1.toFloat()}")
                            sendEvent(name: 'switchOrvibo1', value: 'on')
                        } else  {
                        	TRACE( "received or1 error: ${orvibo1.toFloat()}")
                            sendEvent(name: 'switchOrvibo1', value: 'error')
                        }
                    } catch (e) {
                        TRACE( "No orvibo1 in msg")
                    }
                    try {
                        if (lrf.toFloat() >= 0) {
                            TRACE( "received lrf Signal: ${lrf.toFloat()}")
                            sendEvent(name: 'level', value: lrf)
                        } 
                    } catch (e) {
                        TRACE( "No lrf in msg")
                    }

                } else {
                    TRACE( "childUSN ${childUsn} not equal to Target USN ${targetUsn}")
                }

            }
        } catch (e) {
            TRACE("NO XML: Probably a GET response: $e")
        }
    } else {
    	TRACE("Missed first IF")
    }
    TRACE("EVENTS: ${events}")
    return events
}

private Integer convertHexToInt(hex) {
    Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
    [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
    def ip = getDataValue("ip")
    def port = getDataValue("port")

    if (!ip || !port) {
        def parts = device.deviceNetworkId.split(":")
        if (parts.length == 2) {
            ip = parts[0]
            port = parts[1]
        } else {
            //log.warn "Can't figure out ip and port for device: ${device.id}"
        }
    }

    //convert IP/port
    ip = convertHexToIP(ip)
    port = convertHexToInt(port)
    log.debug "Using ip: ${ip} and port: ${port} for device: ${device.id}"
    return ip + ":" + port
}

def getRequest(path) {
    //log.debug "Sending request for ${path} from ${device.deviceNetworkId}"

    new physicalgraph.device.HubAction(
        'method': 'GET',
        'path': path,
        'headers': [
            'HOST': getHostAddress(),
        ], device.deviceNetworkId)
}
def postRequest(path,json) {
   // log.debug "Sending request for ${path} message ${json} from ${device.deviceNetworkId}"

    new physicalgraph.device.HubAction(
        'method': 'POST',
        'path': path,
        'headers': [
            'HOST': getHostAddress(),
        ], device.deviceNetworkId)
}
private apiPost(String path, data) {
    log.debug "apiPost(${path}, ${data})"

    def headers = [
        HOST:       getHostAddress(),
        Accept:     "*/*"
    ]

    def httpRequest = [
        method:     'POST',
        path:       path,
        headers:    headers,
        body:       data
    ]

    return new physicalgraph.device.HubAction(httpRequest)
}
private def writeValue(name, value) {
   // TRACE("writeValue(${name}, ${value})")

    //setNetworkId(confIpAddr, confTcpPort)

    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPost(getDataValue("ssdpPath"), json)
    ]

    return hubActions
}
def poll() {
    //log.debug "Executing 'poll' from ${device.deviceNetworkId} "

    def path = getDataValue("ssdpPath")
    getRequest(path)
}

def refresh() {
    //log.debug "Executing 'refresh'"

    def path = getDataValue("ssdpPath")
    getRequest(path)
}

def subscribe() {
	state.ssdpPath = getDataValue("ssdpPath")
    //log.debug "Subscribe requested\r\n${state.ssdpPath}"
    subscribeAction(getDataValue("ssdpPath"))
}

private def parseDiscoveryMessage(String description) {
	//TRACE("In PDM:${description}")
    def device = [:]
    def parts = description.split(',')
    parts.each { part ->
        part = part.trim()
        if (part.startsWith('devicetype:')) {
            def valueString = part.split(":")[1].trim()
            device.devicetype = valueString
        } else if (part.startsWith('mac:')) {
            def valueString = part.split(":")[1].trim()
            if (valueString) {
                device.mac = valueString
            }
        } else if (part.startsWith('networkAddress:')) {
        	
            def valueString = part.split(":")[1].trim()
            TRACE("NET Address ${valueString}")
            if (valueString) {
                device.ip = valueString
            }
        } else if (part.startsWith('deviceAddress:')) {
        	
            def valueString = part.split(":")[1].trim()
            TRACE("Port: ${valueString}")
            if (valueString) {
                device.port = valueString
            }
        } else if (part.startsWith('ssdpPath:')) {
            def valueString = part.split(":")[1].trim()
            if (valueString) {
                device.ssdpPath = valueString
            }
        } else if (part.startsWith('ssdpUSN:')) {
            part -= "ssdpUSN:"
            def valueString = part.trim()
            if (valueString) {
                device.ssdpUSN = valueString
            }
        } else if (part.startsWith('ssdpTerm:')) {
            part -= "ssdpTerm:"
            def valueString = part.trim()
            if (valueString) {
                device.ssdpTerm = valueString
            }
        } else if (part.startsWith('headers')) {
            part -= "headers:"
            def valueString = part.trim()
            if (valueString) {
                device.headers = valueString
                def heads = new String(device.headers.decodeBase64())
                TRACE("headers: \r\n${heads}")
            }
        } else if (part.startsWith('body')) {
            part -= "body:"
            def valueString = part.trim()
            if (valueString) {
                device.body = valueString
                def bods = new String(device.body.decodeBase64())
                TRACE("body: \r\n${bods}")
            }
        }
    }

    device
}

private subscribeAction(path, callbackPath="") {
    def address = device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
    def parts = device.deviceNetworkId.split(":")
    def ip = convertHexToIP(getDataValue("ip"))
    def port = convertHexToInt(getDataValue("port"))
    ip = ip + ":" + port
	TRACE( "<http://${address}/notify${callbackPath}>")
	TRACE( "SUBSCRIBE ${path} ${ip} NT: upnp:event Second-3600")
    def result = new physicalgraph.device.HubAction(
        method: "SUBSCRIBE",
        path: path,
        headers: [
            HOST: ip,
            CALLBACK: "<http://${address}/notify$callbackPath>",
            NT: "upnp:event",
            TIMEOUT: "Second-3600"])
    result
}

def on() {
	//TRACE("SWITCH ON")
    onFan()
	
}

def off() {
	TRACE("SWITCH OFF")
	offFan()
}
def onFan() {
    TRACE("onFan()")
	TRACE("sending on()")
    sendEvent([name:"switchFan", value:"on"])
    return writeValue('cmd', 221)
}

def offFan() {
    TRACE("offFan()")
	TRACE("sending off()")
    sendEvent([name:"switchFan", value:"off"])
    return writeValue('cmd', 221)
}
def onC() {
    TRACE("onC()")

	TRACE("sending onC()")
    sendEvent([name:"switchC", value:"on"])
    return writeValue('cmd', 601)
}

def offC() {
    TRACE("offC()")

	TRACE("sending offC()")
    sendEvent([name:"switchC", value:"off"])
    return writeValue('cmd', 600)
}
def onW() {
    TRACE("onW()")

	TRACE("sending onW()")
    sendEvent([name:"switchW", value:"on"])
    return writeValue('cmd', 605)
}

def offW() {
    TRACE("offW()")

	TRACE("sending offW()")
    sendEvent([name:"switchW", value:"off"])
    return writeValue('cmd', 604)
}
def onA() {
    TRACE("onA()")

	TRACE("sending onA()")
    sendEvent([name:"switchA", value:"on"])
    return writeValue('cmd', 603)
}

def offA() {
    TRACE("offA()")

	TRACE("sending offA()")
    sendEvent([name:"switchA", value:"off"])
    return writeValue('cmd', 602)
}
def wemoOn1() {
    TRACE("wemoon1A()")

	TRACE("sending wemoon1()")
    sendEvent([name:"switchWemo1", value:"on"])
    return writeValue('cmd', 616)
}

def wemoOff1() {
    TRACE("wemooff1()")

	TRACE("sending wemooff1()")
    sendEvent([name:"switchWemo1", value:"off"])
    return writeValue('cmd', 617)
}
def refreshWemo1() {
    TRACE("refreshWemo1()")

	TRACE("sending refreshWemo1()")
    sendEvent([name:"switchWemo1", value:"refresh"])
    return writeValue('cmd', 618)
}
def orviboOn1() {
    TRACE("orviboon1A()")

	TRACE("sending orviboon1()")
    sendEvent([name:"switchOrvibo1", value:"on"])
    return writeValue('cmd', 619)
}

def orviboOff1() {
    TRACE("orvibooff1()")

	TRACE("sending orvibooff1()")
    sendEvent([name:"switchOrvibo1", value:"off"])
    return writeValue('cmd', 620)
}
def refreshOrvibo1() {
    TRACE("refreshOrvibo1()")

	TRACE("sending refreshOrvibo1()")
    sendEvent([name:"switchOrvibo1", value:"refresh"])
    return writeValue('cmd', 621)
}
def toggleSwitch(switchNum) {
    TRACE("toggleSwitch() number ${switchNum}")

    return writeValue('cmd', "61${switchNum}")
}
def setLevel(value) {
	TRACE("setLevel(${value})")
	def roundedValue = sendValue(value)
    TRACE("sending rounded Value ${roundedValue}")

//    if (device.currentValue("level") == roundedValue) {
//        return null
//    }
	TRACE("sending setLevel()")
    sendEvent([name:"setLevel()", value:roundedValue])
    return writeValue('cmd', 500 + roundedValue)
    
}

private sendValue(level) {
	if (level < 16) return 0
	if (level < 49) return 33
	if (level < 85) return 66
    if (level < 100) return 99

}

private def TRACE(message) {
    log.debug message
}
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
 *
 */
definition(
	name: "USE virtual Buttons for light setting",
	namespace: "okpowerman",
	author: "okpowerman",
	description: "When buttons are pressed (virtual or real) set light to that brightness.",
	category: "Convenience",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/HealthAndWellness/App-SleepyTime.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/HealthAndWellness/App-SleepyTime@2x.png"
)


preferences {
	section("Which light do you want to control?") {
		input "light1", "capability.switch", title: "Light to control:"
	}
	section("Choose the IFTTT buttons") {
    	input "button_custom","capability.switch", title: "Custom Value Button", required: false
        input "custom_value", "number", defaultValue: 18, required: false
		input "button_10","capability.switch", title: "10 percent"
        input "button_16","capability.switch", title: "16 percent"
        input "button_17","capability.switch", title: "17 percent"
        input "button_18","capability.switch", title: "18 percent"
        input "button_19","capability.switch", title: "19 percent"
        input "button_20","capability.switch", title: "20 percent"
        input "button_30","capability.switch", title: "30 percent"
        input "button_40","capability.switch", title: "40 percent"
        input "button_50","capability.switch", title: "50 percent"
        input "button_60","capability.switch", title: "60 percent"
        input "button_70","capability.switch", title: "70 percent"
        input "button_80","capability.switch", title: "80 percent"
        input "button_90","capability.switch", title: "90 percent"
        input "button_100","capability.switch", title: "100 percent"		
	}
}

def installed()
{
	initialize()
}

def updated()
{
	unsubscribe()
	initialize()
}
def initialize() 
{
	if(button_custom) subscribe(button_custom, "switch.on", sendCustom)
    subscribe(button_10, "switch.on", send10)
    subscribe(button_16, "switch.on", send16)
    subscribe(button_17, "switch.on", send17)
    subscribe(button_18, "switch.on", send18)
    subscribe(button_19, "switch.on", send19)
    subscribe(button_20, "switch.on", send20)
    subscribe(button_30, "switch.on", send30)
    subscribe(button_40, "switch.on", send40)
    subscribe(button_50, "switch.on", send50)
    subscribe(button_60, "switch.on", send60)
    subscribe(button_70, "switch.on", send70)
    subscribe(button_80, "switch.on", send80)
    subscribe(button_90, "switch.on", send90)
    subscribe(button_100, "switch.on", send100)
    
}
def sendCustom(evt)
{
    light1.setLevel(custom_value)
    TRACE("Setting $custom_value")
}
def send10(evt)
{
    light1.setLevel(10)
    TRACE("Setting 10")
}
def send16(evt)
{
    light1.setLevel(16)
    TRACE("Setting 16")
}
def send17(evt)
{
    light1.setLevel(17)
    TRACE("Setting 17")
}
def send18(evt)
{
    light1.setLevel(18)
    TRACE("Setting 18")
}
def send19(evt)
{
    light1.setLevel(19)
    TRACE("Setting 19")
}
def send20(evt)
{
    light1.setLevel(20)
    TRACE("Setting 20")
}
def send30(evt)
{
    light1.setLevel(30)
    TRACE("Setting 30")
}
def send40(evt)
{
    light1.setLevel(40)
    TRACE("Setting 40")
}
def send50(evt)
{
    light1.setLevel(50)
    TRACE("Setting 50")
}
def send60(evt)
{
    light1.setLevel(60)
    TRACE("Setting 60")
}
def send70(evt)
{
    light1.setLevel(70)
    TRACE("Setting 70")
}
def send80(evt)
{
    light1.setLevel(80)
    TRACE("Setting 80")
}
def send90(evt)
{
    light1.setLevel(90)
    TRACE("Setting 90")
}
def send100(evt)
{
    light1.setLevel(100)
    TRACE("Setting 100")
}

def TRACE (msg) {

	log.debug "$msg"
}
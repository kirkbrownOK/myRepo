/**
 *  Copyright 2016 Kirk Brown
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
 *  Send IFTTT Maker command when STEPS are updated
 *
 *  Author: Kirk Brown
 *  Date: 2016-05-31
 *
 */
definition(
    name: "Scrape OG&E Price Signal",
    namespace: "okpowerman",
    author: "okpowerman",
    description: "When a switch/event is triggered check the Price Signal from OG&E",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/window_contact@2x.png"
)

preferences {
	section("Choose one or more, when..."){
		input "triggerSwitch", "capability.switch", title: "Update when this switch triggers", required: true
        
	}
	section("Send the price points to this device"){
		input "priceHolder", "capability.switch", title: "Price Holder", required: true
        
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	subscribeToEvents()
}

def updated() {
	log.debug "SCRAPE OG&E Price Signal: ${settings}"
	unsubscribe()
	subscribeToEvents()
}

def subscribeToEvents() {
	subscribe(triggerSwitch, "switch.on", eventHandler) //tw
    
}

def eventHandler(evt) {
	log.debug "Scrape OG&E Price signal got evt ${evt}"
    scrapePage()
    
}

private scrapePage() {
	def params = [
    	uri: "http://webcache.googleusercontent.com",
        path: "/search",
        query: ["q": "cache:2-pPTu02rXsJ:https://oge.com/price&num=1&hl=en&gl=us&strip=0&vwsrc=0" ]
    	//uri: "https://secure.oge.com",
        //path: "/OK_PriceSignal"    	
	]
        log.debug "${params}"

	try {
   	 httpGet(params) { resp ->
        resp.headers.each {
        log.debug "${it.name} : ${it.value}"
    }
    //log.debug "response contentType: ${resp.contentType}"
    log.debug "response data: ${resp.data}"
    	/*state.dataTxt = "$resp.data"
    	def splitDataTxt = state.dataTxt.split("¢")
        	log.debug "state data text: $state.dataTxt"
            log.debug "splitDataTxt: $splitDataTxt[0].trim()"
			splitDataText.each {
            	log.debug ${it.value}
            }*/
        }
	} catch (e) {
    	log.error "something went wrong: $e"
	}
}
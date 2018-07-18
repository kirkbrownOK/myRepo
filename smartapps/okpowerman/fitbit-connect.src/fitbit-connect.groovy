/**
 *  Copyright 2016 OK Powerman based on Smartthings FITBIT
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
 *	Fitbit Service Manager
 *
 *	Author: OKpowerman
 *	Date: 2016-12-1
 *
 */
definition(
		name: "Fitbit (Connect)",
		namespace: "okpowerman",
		author: "okpowerman",
		description: "Connect your Fitbit to SmartThings.",
		category: "SmartThings Labs",
		iconUrl: "https://s2.q4cdn.com/857130097/files/images/Fitbit-logo-RGB.png",
		iconX2Url: "https://s2.q4cdn.com/857130097/files/images/Fitbit-logo-RGB.png",
		singleInstance: true
) {
	appSetting "clientId"
    appSetting "clientSecret"
}

preferences {
	page(name: "auth", title: "fitbit", nextPage:"", content:"authPage", uninstall: true, install:true)
    page(name: "listDevices", title: "Fitbit Devices", content: "listDevices", install: false)
}

mappings {
    path("/oauth/initialize") {action: [GET: "oauthInitUrl"]}
    path("/oauth/callback") {action: [GET: "callback"]}
}

def authPage() {
    // Check to see if our SmartApp has it's own access token and create one if not.
    if(!state.accessToken) {
        // the createAccessToken() method will store the access token in state.accessToken
        createAccessToken()
    }

    def redirectUrl = "https://graph.api.smartthings.com/oauth/initialize?appId=${app.id}&access_token=${state.accessToken}&apiServerUrl=${getApiServerUrl()}"
    // Check to see if we already have an access token from the 3rd party service.
    if(!state.authToken) {
        return dynamicPage(name: "auth", title: "Login", nextPage: "", uninstall: false) {
            section() {
                paragraph "tap below to log in to the 3rd party service and authorize SmartThings access"
                href url: redirectUrl, style: "embedded", required: true, title: "3rd Party product", description: "Click to enter credentials"
            }
        }
    } else {
    	log.debug "We are already authorized..."
        listDevices()
        // We have the token, so we can just call the 3rd party service to list our devices and select one to install.
    }
}


def oauthInitUrl() {

    // Generate a random ID to use as a our state value. This value will be used to verify the response we get back from the 3rd party service.
    state.oauthInitState = UUID.randomUUID().toString()

    def oauthParams = [
        response_type: "code",
        scope: "activity nutrition heartrate location profile settings sleep social weight",
        client_id: appSettings.clientId,
        client_secret: appSettings.clientSecret,
        state: state.oauthInitState,
        redirect_uri: "https://graph.api.smartthings.com/oauth/callback"
    ]

    redirect(location: "https://www.fitbit.com/oauth2/authorize?${toQueryString(oauthParams)}")
}

// The toQueryString implementation simply gathers everything in the passed in map and converts them to a string joined with the "&" character.
String toQueryString(Map m) {
        return m.collect { k, v -> "${k}=${URLEncoder.encode(v.toString())}" }.sort().join("&")
}
def callback() {
    log.debug "callback()>> params: $params, params.code ${params.code}"

    def code = params.code
    def oauthState = params.state

    // Validate the response from the 3rd party by making sure oauthState == state.oauthInitState as expected
    if (oauthState == state.oauthInitState){
    
    	//Fitbit requires the header Authorization: Basic [ClientID:ClientSecret].base64Encode
        def basicAuth = "${appSettings.clientId}:${appSettings.clientSecret}"
        def basicAuth64 = " Basic ${basicAuth.encodeAsBase64().toString()}"
        def myheaders = [:]
    	myheaders.put("Authorization", basicAuth64)
        def mybody = [:]
        mybody.put("client_id",appSettings.clientId)
        mybody.put("grant_type","authorization_code")
        mybody.put("redirect_uri","https://graph.api.smartthings.com/oauth/callback")
        mybody.put("code",code)
        mybody.put("state",state.oauthInitState)       
        def tokenParams = [
            grant_type: "authorization_code",
            code      : code,
            client_id : appSettings.clientId,
            //client_secret: appSettings.clientSecret,
            redirect_uri: "https://graph.api.smartthings.com/oauth/callback"
        ]
        
        def tokenUrl = "https://api.fitbit.com/oauth2/token"
        def postMap = [
        	headers: myheaders,
            uri: tokenUrl,
            body: mybody
            ]
		log.debug "PM: ${postMap}"
        httpPost(postMap) { resp ->
        	//log.debug "Token Resp: $resp.data"
            state.refreshToken = resp.data.refresh_token
            state.authToken = resp.data.access_token
        }

        if (state.authToken) {
            // call some method that will render the successfully connected message
            success()
        } else {
            // gracefully handle failures
            fail()
        }

    } else {
        log.error "callback() failed. Validation of state did not match. oauthState != state.oauthInitState"
    }
}


def success() {
	def message = """
        <p>Your ecobee Account is now connected to SmartThings!</p>
        <p>Click 'Done' to finish setup.</p>
    """
	connectionStatus(message)
}

def fail() {
	def message = """
        <p>The connection could not be established!</p>
        <p>Click 'Done' to return to the menu.</p>
    """
	connectionStatus(message)
}

def connectionStatus(message, redirectUrl = null) {
	def redirectHtml = ""
	if (redirectUrl) {
		redirectHtml = """
			<meta http-equiv="refresh" content="3; url=${redirectUrl}" />
		"""
	}

	def html = """
        <!DOCTYPE html>
        <html>
            <head>
                <meta name="viewport" content="width=640">
                <title>Fitbit & SmartThings connection</title>
                <style type="text/css">
                    @font-face {
                        font-family: 'Swiss 721 W01 Thin';
                        src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot');
                        src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.eot?#iefix') format('embedded-opentype'),
                        url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.woff') format('woff'),
                        url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.ttf') format('truetype'),
                        url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-thin-webfont.svg#swis721_th_btthin') format('svg');
                        font-weight: normal;
                        font-style: normal;
                    }
                    @font-face {
                        font-family: 'Swiss 721 W01 Light';
                        src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot');
                        src: url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.eot?#iefix') format('embedded-opentype'),
                        url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.woff') format('woff'),
                        url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.ttf') format('truetype'),
                        url('https://s3.amazonaws.com/smartapp-icons/Partner/fonts/swiss-721-light-webfont.svg#swis721_lt_btlight') format('svg');
                        font-weight: normal;
                        font-style: normal;
                    }
                    .container {
                        width: 90%;
                        padding: 4%;
                        text-align: center;
                    }
                    img {
                        vertical-align: middle;
                    }
                    p {
                        font-size: 2.2em;
                        font-family: 'Swiss 721 W01 Thin';
                        text-align: center;
                        color: #666666;
                        padding: 0 40px;
                        margin-bottom: 0;
                    }
                    span {
                        font-family: 'Swiss 721 W01 Light';
                    }
                </style>
            </head>
        <body>
            <div class="container">
                <img src="https://s2.q4cdn.com/857130097/files/images/Fitbit-logo-RGB.png" alt="connected device icon" />
                <img src="https://s3.amazonaws.com/smartapp-icons/Partner/support/st-logo%402x.png" alt="SmartThings logo" />
                ${message}
            </div>
        </body>
    </html>
    """

	render contentType: 'text/html', data: html
}

def getDeviceList() {
	log.debug "In getDeviceList"

	def deviceList = [:]
	state.deviceDataArr = []

	def deviceListParams = [
		uri: "https://api.fitbit.com/1/user/-/devices.json",
		//path: "/1/user/-/devices.json",
		headers: ["Authorization": "Bearer ${state.authToken}"]
        // TODO - the query string below is not consistent with the Ecobee docs:
        // https://www.ecobee.com/home/developer/api/documentation/v1/operations/get-thermostats.shtml
	]
	log.debug "${deviceListParams}"
	try {
		httpGet(deviceListParams) { resp ->
        	log.debug "resp = ${resp.data}"
            
			if (resp.status == 200) {
            	log.debug "200 received"
				resp.data.each { it ->
                	if (it.type == "TRACKER") {
                    	deviceList["${it.id}"] = it.deviceVersion
						state.deviceDataArr.push(['name'    : it.deviceVersion,
						'id'      : it.id,
						'type'    : "TRACER",
						
						'data'    : it
						
						])
                    }
                    
				}
			} else {
				log.debug "http status: ${resp.status}"
			}
		}
	} catch (groovyx.net.http.HttpResponseException e) {
        log.trace "Exception listing children: " + e.response.data
        if (e.response.data.status.code == 401) {
            log.debug "Refreshing your auth_token!"
            refreshAuthToken()
        }
    }
	
	return deviceList
}


def listDevices()
{
	log.debug "In listDevices"
	log.debug "validateToken"
    validateToken()
	//login()

	def devices = getDeviceList()
	log.debug "Device List = ${devices}"

	dynamicPage(name: "listDevices", title: "Choose devices", install: true) {
		section("Devices") {
			input "devices", "enum", title: "Select Device(s)", required: false, multiple: true, options: devices
		}
	}
}

def getThermostatDisplayName(stat) {
    if(stat?.name) {
        return stat.name.toString()
    }
    return (getThermostatTypeName(stat) + " (${stat.identifier})").toString()
}

def getThermostatTypeName(stat) {
	return stat.modelNumber == "siSmart" ? "Smart Si" : "Smart"
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
    unschedule()
	initialize()
}

def initialize() {
	log.debug "Initialize validate token"
	validateToken()
	settings.devices.each {
		def deviceId = it

		state.deviceDataArr.each {
			if ( it.id == deviceId ) {
				//addChildDevice("okpowerman", "Fitbit Charge 2", deviceId, null, [name: it.name, label: "My Fitbit", completedSetup: true])
                //log.debug "addChildDevice(\"okpowerman\", \"Fitbit Charge 2\", $deviceId, null, [name: $it.name, label: \"My Fitbit\", completedSetup: true])"
				log.trace "Device Already Added"	
			} else {
            	log.debug "${it.id} != ${deviceId}"
                addChildDevice("okpowerman", "Fitbit Charge 2", deviceId, null, [name: it.name, label: "My Fitbit", completedSetup: true])
                log.debug "addChildDevice(\"okpowerman\", \"Fitbit Charge 2\", $deviceId, null, [name: $it.name, label: \"My Fitbit\", completedSetup: true])"
            }	
		}
	}

	pollHandler() //first time polling data data from thermostat

	//automatically update devices status every 5 mins
	runEvery5Minutes("pollHandler")

}

def pollHandler() {
	log.debug "pollHandler()"
    settings.devices.each { 
    	log.debug ("it: ${it}")
        def childDevice = getChildDevice(it)
        pollChild(childDevice)
    }

}
def pollChild(childDevice) {
		validateToken()
		log.debug "Polling for Child"
        //log.debug "DNI: {$childDevice.device.deviceNetworkId}"
        pollSteps(childDevice)
        pollSleep(childDevice)
}
def pollSteps(childDevice) {
	def params = [
		uri: 'https://api.fitbit.com/1/user/-/activities/date/today.json',
		headers: ["Authorization": "Bearer ${state.authToken}" ],
		contentType: 'application/json'
	]

	httpGet(params) { response -> 
    	log.debug "In Response Goals"
        if (response.status !=200) {
        	log.error "response has error: $response.status $response.data"
            validateToken()
        } else {
        	def goals
            def summary
            try {
                // json response already parsed into JSONElement object
                goals = response.data.goals
                summary = response.data.summary
                log.debug "Summary: ${summary}"
                //state.sleepSummary = summary
                        
            } catch (e) {
                log.error "error parsing json from response: $e"
            }
            
            if (goals) {
                    
				log.debug "Goal = ${goals.steps} Steps"
				childDevice?.sendEvent(name:"goal", value: goals.steps)
            } else {
				log.debug "did not get json results from response body: $response.data"
            }
            if (summary) {    
				log.debug "Summary = ${summary.steps} Steps"
				childDevice?.sendEvent(name:"steps", value: summary.steps)
            } else {
				log.debug "did not get json results from response body: $response.data"
            }
        }
	}
}

def pollSleep(childDevice) {
	def params = [
		uri: 'https://api.fitbit.com/1/user/-/sleep/date/today.json',
		headers: ["Authorization": "Bearer ${state.authToken}" ],
		contentType: 'application/json'
	]
	try {
        httpGet(params) { response -> 
            log.debug "In Response Sleep"
            if (response.status != 200) {
                log.error "response has error: $response.status $response.data"
                validateToken()
            } else {
                def summary = false
                try {
                    // json response already parsed into JSONElement object
                    summary = response?.data == null ? "empty" : response?.data

                } catch (e) {
                    log.error "error parsing json from response: $e"
                }
                if (summary) {    
                    log.debug "Summary = ${summary} "
                    childDevice.saveSleepEvent(summary)
                } else {
                    log.debug "did not get json results from response body: $response.data"
                }
            }
        }
 	} catch (e) {
    	log.warn "response has error: $e"
   		validateToken()
    }
}

def getChildDeviceIdsString() {
	return thermostats.collect { it.split(/\./).last() }.join(',')
}

def toJson(Map m) {
    return groovy.json.JsonOutput.toJson(m)
}

def validateToken() {
	if(!state.expires_in) { state.expires_in = 0}
	log.debug "Now() ${now()} token expires in $state.expires_in"
    if (now() > state.expires_in) {
    	log.debug "Token is expired"
        refreshAuthToken()
    }

}
private refreshAuthToken() {
		log.debug "Attempting to refresh token"
		//Fitbit requires the header Authorization: Basic [ClientID:ClientSecret].base64Encode
        def basicAuth = "${appSettings.clientId}:${appSettings.clientSecret}"
        def basicAuth64 = " Basic ${basicAuth.encodeAsBase64().toString()}"
        def myheaders = [:]
    	myheaders.put("Authorization", basicAuth64)
        def mybody = [:]
        mybody.put("client_id",appSettings.clientId)
        mybody.put("grant_type","refresh_token")
        mybody.put("redirect_uri","https://graph.api.smartthings.com/oauth/callback")
        mybody.put("refresh_token",state.refreshToken)
        mybody.put("state",state.oauthInitState)       
        
        def tokenUrl = "https://api.fitbit.com/oauth2/token"
        def postMap = [
        	headers: myheaders,
            uri: tokenUrl,
            body: mybody
            ]
		log.debug "PM: ${postMap}"
        httpPost(postMap) { resp ->
        	//log.debug "Token Resp: $resp.data"
            log.debug "Old RT: $state.refreshToken"
            state.refreshToken = resp.data.refresh_token
            log.debug "New RT: $state.refreshToken"
            log.debug "Old SA: $state.authToken"
            state.authToken = resp.data.access_token
            log.debug "New SA: $state.authToken"
            log.debug "Expires at $state.expires_in now: $now"
            state.expires_in = resp.data.expires_in * 1000 + now()
        }  
}

def getServerUrl()           { return "https://graph.api.smartthings.com" }
def getShardUrl()            { return getApiServerUrl() }
def getCallbackUrl()         { return "https://graph.api.smartthings.com/oauth/callback" }
def getBuildRedirectUrl()    { return "${serverUrl}/oauth/initialize?appId=${app.id}&access_token=${state.accessToken}&apiServerUrl=${shardUrl}" }
//def getApiEndpoint()		 { return "https://tnrtkrucm4ig.runscope.net"}
def getApiEndpoint()         { return "https://www.fitbit.com" }
def getSmartThingsClientId() { return appSettings.clientId }

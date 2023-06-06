# RTB System
Simulates RTB system using Scala and AKKA freamwork.<br/>
 The application receives bid requests in JSON format based on the OpenRTB 2.5 protocol, then generates a response in JSON format based on the OpenRTB 2.5 protocol.<br/>
 The program can be run locally on a machine to simulate the functioning of an RTB system.<br/>
## What is OpenRTB?
OpenRTB (Open Real-Time Bidding) is a protocol that facilitates real-time bidding in the digital advertising ecosystem.<br/>
## Hot to run?
Before running, replace the content of the file found in src/bid_request.json with your Bid Request, or alternatively you can stay with the example in the file.<br/>
Then you can run Main.scala class.
## Program scenario
The Main class creates two Actors according to the AKKA toolkit:<br/>
One is a bidRequest and the other is a bidResponse.<br/>
The bidRequest creates a tender to publish an ad on the website/app according to the JSON RTB request that written in the file, and sends it to the bidResponse.<br/>
The bidRequest waits for a response according to the maximum time listed in the file.<br/>
The bidResponse builds a Response according to the open RTB standard and returns it to the bidRequest.<br/>
The bidRequest prints the received request and the program is over.<br/>
## The output
If you will not change the content in src/bid_request.json, you should get the following output:
````
Request ID: "test1"
User Agent: "Mozilla/5.0 (Linux; Android 4.4.2; DL1010Q Build/KOT49H) AppleWebKit/537.36 (KHTML,like Gecko) Version/4.0 Chrome/30.0.0.0 Safari/537.36"
Bid Response: {"id":"test1","seatbid":[{"bid":[{"id":"1","impid":"1","price":9.43}],"seat":"512"}]}
````

### Author: Maor Caspi
### Date: June 2023 

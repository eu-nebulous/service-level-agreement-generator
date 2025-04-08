# Service-Level Agreement Generator

The SLA generator is responsible for forming the SLAs (Service-Level Agreements) that are formed in Nebulous describing them ontologically, and posting them on an ActiveMQ topic.

# Building and Running the SLA Generator

Maven is required to build this project.
Java is required to run this project.

1. clone the repository;
2. you may specify the port the server will be running on by editing the value `src/main/resources/application.properties`. The default is `server.port=8000`;
3. run `mvn package -Dmaven.test.skip` on the root folder;
4. an executable `nebulous-sla-x.y.z.jar` file will be created in the `target` folder;
5. run the executable by using the `java -jar nebulous-ont-x.y.z.jar {{server IP}}` command.  You need to pass the IP address of an ontology server as a parameter

Terminating the program is done by aborting the task (CTRL + c in most command lines). 
Terminating the program has no bearing on the in-memory ontology maintained by the ontology-server.

# Using the SLA Generator

The SLA Generator has two endpoints: `get/sla` and `create/sla`: the former returning and the latter creating an SLA as well as posting it on an ActiveMQ topic.

In order to create an SLA a POST request must be sent that contains a JSON body with the SLA. That body is structured as follows:
 
"sls" defines all SLs (service levels) and SLOs (service-level objectives) of the SLA. Each SL, contains SLOs. Each SL has a *unique* name defined by an integer. Two SLs *cannot* be named the same. The "slos" list contains all of the SLOs for that particular SL. 

Each SLO within it has three fields: the "firstArgument" contains the metric of the SLO, the "operator" contains the operator that must be one of the following: "GREATER_EQUAL_THAN", "GREATER_THAN", "LESS_EQUAL_THAN", "LESS_THAN", "EQUALS", "NOT_EQUALS". Finally, "secondArgument" contains the value threshold value. There can be as many 
SLs as SLOs as desired, however, there must be at least one SL and each SL must have at least one SLO.

"metrics" contain all of the values used as a "firstArgument" in an SLO. Every "firstArgument" must match with a metric from the list. 
"transitions" specify the conditions to transition from one SL to another. The number of transitions in an SLA equals the number of SLs minus one. E.g., for three SLs, there are two SLs, one from SL 1 to SL 2, and one from SL 2 to SL 3. "firstSL" and "secondSL" define the 
define the unique names of the two SLs connected by the transition. "firstSL" defines the SL the SLA is currently at, whereas the "secondSL" is the SL the SLA will transition to. "violationThreshold" defines the value threshold for violations. "evaluationPeriod" defines the amount of time in which those violations must occur. E.g. `"violationThreshold": 4` and `"evaluationPeriod": "PT1H"` means that 4 violations must occur within 1 hour for a transition to take place.

Finally, settlements are 
```js
{
	"sls": [
		{
			"slName": Integer,
			"slos":[
				{
					"firstArgument": String,
					"operator": String,
					"secondArgument": Any
				},
				...
			]
		},
		...
	],
	"metrics":[
		{
			"name": String,
			"window":{
				"type":"sliding" || "batch",
				"unit": String,
				"value":Number
			},
			"output":{
				"type":"all",
				"unit": String,
				"value":Number
			}
		},
		...
	],
	"transitions":[
		{
			"firstSl": Integer,
			"secondSl": Integer,
			"evaluationPeriod": String (xsd:duration),
			"violationThreshold":Integer
		},
		...
	],
	"settlement":{
		"evaluationPeriod": String (xsd:duration),
		"settlementCount": Integer,
		"concernedSL": Integer
	}

}
```

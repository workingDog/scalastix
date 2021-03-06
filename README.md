
## STIX 2.0 protocol in Scala

[[1]](https://oasis-open.github.io/cti-documentation/) 
"Structured Threat Information Expression [STIX™] 
is a language and serialization format 
used to exchange [cyber threat intelligence (CTI)](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=cti). STIX enables organizations to share 
CTI with one another in a consistent and machine readable manner, allowing security 
communities to better understand what computer-based attacks they are most likely to 
see and to anticipate and/or respond to those attacks faster and more effectively. 
STIX is designed to improve many different capabilities, such as collaborative 
threat analysis, automated threat exchange, automated detection and response, and more."

This library **scalastix** is a [Scala](https://www.scala-lang.org/) library of classes and methods 
for STIX-2 Domain Objects (SDO) and associated data types. 
It is an API for serializing and de-serializing STIX-2 JSON content.
It includes all SDO, SRO, Observables, OpenVocab, Markings and supporting data types.

### References
 
1) [STIX 2.0 Specifications](https://oasis-open.github.io/cti-documentation/)
   
### Dependencies

1) [Play JSON](https://github.com/playframework/play-json) library providing the JSON serialization and de-serialization.

 
### Installation and packaging

This library attempts to follow the [OASIS published STIX-2.0 specifications](https://oasis-open.github.io/cti-documentation/).

To use the latest release (with scala 2.13.3) add the following dependency to your *build.sbt*:

    libraryDependencies += "com.github.workingDog" %% "scalastix" % "1.1"

The current source code version is **1.2-SNAPSHOT** using scala 2.13.3. 

To compile and package **scalastix** from source use [SBT](http://www.scala-sbt.org/).
To compile and generate a jar file from source:

    sbt package

This will produce a jar file "scalastix_2.13-1.2-SNAPSHOT.jar" in the "./target/scala-2.13" directory 
for use in Scala applications.


To publish the library to your local (Ivy) repository, simply type:

    sbt publishLocal

Then put this in your Scala app *build.sbt* file:

    libraryDependencies += "com.github.workingDog" %% "scalastix" % "1.2-SNAPSHOT" 
 
### Conventions

All Stix objects have their required parameters listed first, followed by the optionals. 

All SDO and SRO constructors start with the "type" parameter of the class, if omitted, the type is auto-generated. 
The "id" parameter is second, if omitted a random id is auto-generated.
 This is followed by the "created" and "modified" parameters, if omitted a current timestamp is auto-generated for those.
 
Similarly for bundle, "type" and "spec_version" are set automatically, "id" can also be auto-generated if desired.  
 
Custom properties can be added to any Scala SDO or Observable objects via the field name "custom".
                                    
### Usage
                         
In a Scala application the creation of a Stix object can be done as follows:

    import StixImplicits._
    
    // create an SDO, the type, id, created and modified are auto-generated
    val attack = new AttackPattern(name = "Spear Phishing",
                      kill_chain_phases = List(KillChainPhase("Kill", "Bill")),
                      external_references = List(ExternalReference("a-source-name")),
                      object_marking_refs = List(Identifier(Campaign.`type`)),
                      custom = CustomProps(Map("x_test" -> JsString("test1"))))
                      
    // convert to json
    val attackjson = Json.toJson(attack)
    // add to a bundle
    val bundle = Bundle(attack)
    // convert to json
    val bundlejson = Json.toJson(bundle)
                                                   
 Reading STIX-2 bundles:
 
     // read a STIX-2 bundle from a file
     val jsondoc = Source.fromFile("test1.json", "UTF-8").mkString
     // create a bundle object from it
     Json.fromJson[Bundle](Json.parse(jsondoc)).asOpt match {
       case Some(bundle) =>
         // print all individual sdo
         bundle.objects.foreach(sdo => println("sdo: " + sdo))
         // get all attack pattern
         val allAttacks = bundle.objects.filter(_.`type` == AttackPattern.`type`)
         allAttacks.foreach(x => println("attack: " + x))
         // convert them to json and print
         allAttacks.foreach(x => println(Json.prettyPrint(Json.toJson(x))))
 
       case None => println("invalid JSON")
     }
 
 
### Status

Work in progress


## STIX 2.1 protocol in Scala

[[1]](https://oasis-open.github.io/cti-documentation/) 
"Structured Threat Information Expression [STIX™] 
is a language and serialization format 
used to exchange [cyber threat intelligence (CTI)](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=cti). STIX enables organizations to share 
CTI with one another in a consistent and machine readable manner, allowing security 
communities to better understand what computer-based attacks they are most likely to 
see and to anticipate and/or respond to those attacks faster and more effectively. 
STIX is designed to improve many different capabilities, such as collaborative 
threat analysis, automated threat exchange, automated detection and response, and more."

This library **ScalaStix** is a [Scala](https://www.scala-lang.org/) and [Scala.js](https://www.scala-js.org/) library of classes and methods 
for STIX Domain Objects (SDO) and associated data types. 
It is an API for serializing and de-serializing STIX 2.1 JSON content.
It includes all SDO, SRO, Observables, OpenVocab, Markings and supporting data types.

#### Note
The older **ScalaStix** version 0.1 uses [circe](https://github.com/circe/circe) for JSON serialization and de-serialization. 

**ScalaStix** versions >= 0.2 use the [Play JSON](https://github.com/playframework/play-json) library to provide the JSON 
serialization and de-serialization. See also the [Play Framwork documentation](https://www.playframework.com/documentation/2.6.x/ScalaJson) 
for how to use Play JSON.  

### References
 
1) [STIX 2.1 Specification](https://oasis-open.github.io/cti-documentation/)
   
### Dependencies

1) [Play JSON](https://github.com/playframework/play-json) library providing the JSON serialization and de-serialization.
2) [Scala Java-Time](https://github.com/cquiroz/scala-java-time) provides an implementation of the java.time package.

 
### Installation and packaging

To use the latest release add the following dependency to your build.sbt:

    libraryDependencies += "com.github.workingDog" %% "scalastix" % "0.5"

The best way to compile and package **ScalaStix** from source is to use [SBT](http://www.scala-sbt.org/).
To compile and generate a jar file from source:

    sbt package

This will produce a jar file "scalastix_2.12-0.6-SNAPSHOT.jar" in the "./jvm/target/scala-2.12" directory 
for use in Scala applications, and 
"scalastix_sjs0.6_2.12-0.6-SNAPSHOT.jar" in the "./js/target/scala-2.12" directory 
for use in Scala.js applications.


To publish the libraries to your local (Ivy) repository, simply type:

    sbt publishLocal

Then put this in your Scala app build.sbt file

    libraryDependencies += "com.github.workingDog" %% "scalastix" % "0.6-SNAPSHOT" 
 
### Conventions

All Stix objects have their required parameters listed first, followed by the optionals. 

All SDO and SRO constructors start with the "type" parameter of the class, if omitted, the type is auto-generated. 
The "id" parameter is second, if omitted a random id is auto-generated.
 This is followed by the "created" and "modified" parameters, if omitted a current timestamp is auto-generated for those.
 
Similarly for bundle, "type" and "spec_version" are set automatically, "id" can also be auto-generated if desired.  
 
Custom properties can be added to any Stix objects such as SDO and Observables by adding 
 the custom properties as a JsonObject to the field name "custom".
                                    
### Usage
                         
In a Scala application the creation of a Stix object can be done as follows:

    import StixImplicits._
    
    // create an SDO, the type, id, created and modified are auto-generated
    val attack = new AttackPattern(name = "Spear Phishing",
                      kill_chain_phases = List(KillChainPhase("Kill", "Bill")),
                      external_references = List(ExternalReference("a-source-name")),
                      object_marking_refs = List(Identifier(Campaign.`type`)))
                      
    // convert to json
    val attackjson = Json.toJson(attack)
    // add to a bundle
    val bundle = Bundle(attack)
    // convert to json
    val bundlejson = Json.toJson(bundle)
                                                   
 Reading STIX-2 bundles:
 
     // read a STIX-2 bundle from a file
     val jsondoc = Source.fromFile("./stixfiles/test1.json").mkString
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

Only very basic testing done.

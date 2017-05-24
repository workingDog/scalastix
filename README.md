## STIX 2.1 protocol in Scala

[[1]](https://docs.google.com/document/d/1yvqWaPPnPW-2NiVCLqzRszcx91ffMowfT5MmE9Nsy_w/edit#) "Structured Threat Information Expression (STIX™) is a language and serialization format used to exchange cyber threat intelligence (CTI). STIX enables organizations to share CTI with one another in a consistent and machine readable manner, allowing security communities to better understand what computer-based attacks they are most likely to see and to anticipate and/or respond to those attacks faster and more effectively. STIX is designed to improve many different capabilities, such as collaborative threat analysis, automated threat exchange, automated detection and response, and more."

This library **ScalaStix** is a [Scala](https://www.scala-lang.org/) library of classes and methods 
for STIX Domain Objects (SDO) and associated data types. 
It is an API for serializing and de-serializing STIX 2.1 JSON content.
It includes all SDO, SRO, OpenVocab, Markings and supporting data types.

### References
 
1) [STIX 2.1 Specification](https://docs.google.com/document/d/1yvqWaPPnPW-2NiVCLqzRszcx91ffMowfT5MmE9Nsy_w/edit)
   

### Dependencies

1) [circe library](https://github.com/circe/circe) providing the JSON serialization and de-serialization for [Scala](https://www.scala-lang.org/) and [Scala.js](https://www.scala-js.org/) (i.e. --> Javascript)).
 
 
### Building

For Scala on the JVM, requires Java8.

The best way to compile and package **ScalaStix** from source is to use [SBT](http://www.scala-sbt.org/). 
To assemble the library and all its dependencies into a single jar file type:

    sbt assembly

This will produce a jar file "scalastix_2.12-1.0-SNAPSHOT.jar" in the "./target/scala-2.12" directory.

To publish the library to your local (Ivy) repository, simply type:

    sbt publishLocal

Then put this in your build.sbt file

    libraryDependencies += "com.github.workingDog" %% "scalastix" % "1.0-SNAPSHOT"
 
 
For Scala.js and thus compiling to JavaScript:

---> todo 
 
### Conventions

All SDO classes have their required parameters listed first, followed by the optionals. 

All SDO and SRO constructors start with the "type" parameter of the class, if omitted, the type is auto-generated. 
The "id" parameter is second, if omitted a random id is auto-generated.
 This is followed by the "created" and "modified" parameters, if omitted a current timestamp is auto-generated for those.
 
Similarly for bundle, "type" and "spec_version" are set automatically, "id" can also be auto-generated if desired.  
 
Use the "x_custom" field to add custom properties (as a JsonObject) to any Stix object such as SDO and Observables. 
 
 
### Usage
                         
In a Scala application the creation of a Stix object can be done as:

    import StixImplicits._
    
    // create an SDO, the type, id, created and modified are auto-generated
    val attack = new AttackPattern(name = "Spear Phishing",
                      kill_chain_phases = List(KillChainPhase("Kill", "Bill")),
                      external_references = List(new ExternalReference("a-source-name")),
                      object_marking_refs = List(Identifier("campaign")))
                      
    // convert to json
    val attackjs = attackPattern.asJson
    // add to a bundle
    val bundle = Bundle(attack)
    // convert to json
    bundle.asJson
            
Similarly when building a web browser App using Scala.js, that is compiling to javascript.            
                                         
See also the Examples.
 
### Status

Work in progress, not yet finished.

No testing done.

Change Log
==========

### changes in 1.0-SNAPSHOT

* changes to the Extensions json reads and writes, removed the type param
* changed X509V3ExtenstionsType inhibit_any_policy to Option[String]
* changed NetworkTraffic src_port and dst_port to Option[Int]

* added HttpRequestExt extention
* added ICMPExt extention
* added TCPExt extention
* added SocketExt extention 
* added WindowsProcessExt extention
* added WindowsServiceExt extention
* added UnixAccountExt extention
* added X509V3Ext extention


### changes in 0.9

* removed the 'type' parameter from WindowsRegistryValueType Observables
* removed the 'type' parameter from X509V3ExtenstionsType Observables
* removed the 'type' parameter from EmailMimeType Observables

* removed the 'type' parameter from AlternateDataStream Extensions
* removed the 'type' parameter from WindowPEOptionalHeaderType Extensions
* removed the 'type' parameter from WindowPESectionType Extensions

* change all Int (32 bit) to Long (64 bit), because the specs require 64 bit integers

### changes in 0.8

* updated scala, sbt and associated dependencies
* added def stringToIdentifierOption(s: String) to Identifier

### changes in 0.7
Major restructure to align with STIX-2.0 specifications.
 
* to align with STIX-2.0 specs; 
** removed all confidence and lang fields from all objects.
** default bundle spec_version="2.0"
** removed contest, organization from OpenVocab
** change object_refs to List[Identifier] in Report

* bug fix: change id default in CustomStix to --> id: Identifier = Identifier(CustomStix.`type`) 
* added the parameter, custom: Option[CustomProps] to CustomExtension Extension object
* removed dependence on https://github.com/cquiroz/scala-java-time, using java time now.

* NOTE: although not part of STIX-2.0 LanguageContent has been kept for compatibility.


### changes in 0.6

* restructuring of directories
* remove examples and stixfiles to another repo
* updated scala, sbt, plugins and dependencies 
* renamed assemply.sbt to assembly.sbt in project
* added CustomStix, CustomExtension, CustomObservable
* removed all scalajs dependencies


### changes in 0.5

* remove description from all Observables
* renamed x_custom to custom for all Observables
* added custom Format
* removed most theReads and theWrites and use Format instead

### changes in 0.4  

* added the missing hashes to ExternalReference
* removed toString from ExternalReference and GranularMarking

### changes in 0.3  

* bring back "org.threeten" % "threetenbp" instead of java.time, to cater for ZonedDateTime in scala and scala.js
* made cross compile for jvm and js
* add support for custom properties for SDO, SRO and StixObj, see also CustomProps

### changes in 0.2

* fixed a bug/typo in WindowPESectionType.`type`
* removed dependency on "org.threeten" % "threetenbp" % "1.3.4" (using java instead)
* remove all circe dependencies 
* add Play json dependency
* replace all circe json code with Play Json code

 

